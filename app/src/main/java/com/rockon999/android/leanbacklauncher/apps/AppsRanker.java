package com.rockon999.android.leanbacklauncher.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.AppsDbHelper.Listener;
import com.rockon999.android.leanbacklauncher.logging.LoggingUtils;
import com.rockon999.android.leanbacklauncher.util.Util;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AppsRanker implements Listener {
    private static boolean DEBUG;
    private static String TAG;
    private static AppsRanker sAppsRanker; // todo remove memory leak
    private final Queue<CachedAction> mCachedActions;
    private Context mContext;
    private AppsDbHelper mDbHelper;
    private HashMap<String, AppsEntity> mEntities;
    private final Object mEntitiesLock;
    private ArrayList<String> mLastLaunchPointRankingLogDump;
    private Comparator<LaunchPoint> mLaunchPointComparator;
    private Queue<RankingListener> mListeners;
    private boolean mNeedsResorting;
    private SharedPreferencesChangeListener mPrefsListener;
    private boolean mQueryingScores;
    private AppsPreferences.SortingMode mSortingMode;

    public void saveFavoriteSnapshot(List<LaunchPoint> selectLaunchPoints) {
        for (LaunchPoint lp : selectLaunchPoints) {
            saveFavorite(lp);
        }
    }

    private void saveFavorite(LaunchPoint launchPoint) {
        AppsEntity e = this.mEntities.get(launchPoint.getPackageName());
        if (e == null) {
            // assume true for now todo hm that is a circle
            e = new AppsEntity(this.mContext, this.mDbHelper, launchPoint.getPackageName(), isFavorite(launchPoint));
        }
        this.mDbHelper.saveEntity(e, false);
    }

    public interface RankingListener {
        void onRankerReady();
    }

    private static class CachedAction {
        int action;
        String component;
        String group;
        String key;
        public AppCategory category;

        CachedAction(String k, String c, String g, int a, AppCategory category) {
            this.key = k;
            this.component = c;
            this.group = g;
            this.action = a;
            this.category = category;
        }
    }

    private class LaunchPointInstallComparator implements Comparator<LaunchPoint> {
        private LaunchPointInstallComparator() {
        }

        public int compare(LaunchPoint lhs, LaunchPoint rhs) {
            if (lhs == null || rhs == null) {
                return 0;
            }
            double lhsOrder = AppsRanker.this.getEntityOrder(lhs);
            double rhsOrder = AppsRanker.this.getEntityOrder(rhs);
            double lInstallTime = (double) lhs.getFirstInstallTime();
            double rInstallTime = (double) rhs.getFirstInstallTime();
            if (lhsOrder != rhsOrder) {
                if (lhsOrder == 0.0d) {
                    return 1;
                }
                if (rhsOrder == 0.0d) {
                    return -1;
                }
                return lhsOrder > rhsOrder ? 1 : -1;
            } else if (lInstallTime < 0.0d && rInstallTime >= 0.0d) {
                return 1;
            } else {
                if (rInstallTime < 0.0d && lInstallTime >= 0.0d) {
                    return -1;
                }
                if (lInstallTime == rInstallTime && lhs.getTitle() != null) {
                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                }
                return lInstallTime > rInstallTime ? 1 : -1;
            }
        }
    }

    private class LaunchPointRecencyComparator implements Comparator<LaunchPoint> {
        private LaunchPointRecencyComparator() {
        }

        public int compare(LaunchPoint lhs, LaunchPoint rhs) {
            if (lhs == null || rhs == null) {
                return 0;
            }
            double lhsScore = AppsRanker.this.getLastOpened(lhs);
            double rhsScore = AppsRanker.this.getLastOpened(rhs);
            if (rhsScore > lhsScore) {
                return 1;
            }
            if (rhsScore < lhsScore) {
                return -1;
            }
            return 0;
        }
    }

    private static class SharedPreferencesChangeListener implements OnSharedPreferenceChangeListener {
        private WeakReference<AppsRanker> mAppsRankerRef;

        public SharedPreferencesChangeListener(AppsRanker appsRanker) {
            this.mAppsRankerRef = new WeakReference<>(appsRanker);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            AppsRanker appsRanker = this.mAppsRankerRef.get();
            if (appsRanker != null) {
                appsRanker.checkForSortingModeChange();
            }
        }
    }

    static {
        TAG = "AppsRanker";
        DEBUG = true;
        sAppsRanker = null;
    }

    private AppsRanker(Context ctx, AppsDbHelper dbHelper) {
        this.mEntitiesLock = new Object();
        this.mListeners = new LinkedList<>();
        this.mCachedActions = new LinkedList<>();
        this.mEntities = new HashMap<>();
        this.mSortingMode = AppsPreferences.SortingMode.FIXED;
        this.mLastLaunchPointRankingLogDump = new ArrayList<>();
        this.mContext = ctx;
        this.mDbHelper = dbHelper;
        this.mSortingMode = AppsPreferences.getSavedSortingMode(this.mContext);
        registerPreferencesListeners();
        this.mQueryingScores = true;
        this.mDbHelper.loadEntities(this);
    }

    public static AppsRanker getInstance(Context context) {
        if (sAppsRanker == null) {
            sAppsRanker = new AppsRanker(context.getApplicationContext(), AppsDbHelper.getInstance(context));
        }
        return sAppsRanker;
    }

    public AppsPreferences.SortingMode getSortingMode() {
        AppsPreferences.SortingMode savedSortingMode = AppsPreferences.getSavedSortingMode(this.mContext);
        if (this.mSortingMode != savedSortingMode) {
            this.mSortingMode = savedSortingMode;
            this.mLaunchPointComparator = null;
        }
        return this.mSortingMode;
    }

    public boolean checkIfResortingIsNeeded() {
        boolean needed = this.mNeedsResorting;
        this.mNeedsResorting = false;
        return needed;
    }

    public void unregisterListeners() {
        unregisterPreferencesListeners();
    }

    public void onAction(String packageName, int actionType, AppCategory category) {
        onAction(packageName, null, null, actionType, category);
    }

    public void onAction(String packageName, String component, int actionType, AppCategory category) {
        onAction(packageName, component, null, actionType, category);
    }

    private void onAction(String key, String component, String group, int actionType, AppCategory category) {
        if (!TextUtils.isEmpty(key)) {
            if (actionType != 3) {
                this.mNeedsResorting = true;
            }
            synchronized (this.mCachedActions) {
                if (this.mQueryingScores) {
                    if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                        Log.d(TAG, "Scores not ready, caching this action");
                    }
                    this.mCachedActions.add(new CachedAction(key, component, group, actionType, category));
                    return;
                }
                if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "action: " + actionType + " for " + key + " - group = " + group);
                }
                synchronized (this.mEntitiesLock) {
                    AppsEntity entity = this.mEntities.get(key);
                    if (actionType != 3) {
                        if (entity == null) {
                            entity = new AppsEntity(this.mContext, this.mDbHelper, key, false);
                            this.mEntities.put(key, entity);
                        }
                        entity.onAction(actionType, component, group);
                        LoggingUtils.logRankerActionEvent(key, actionType, 0, TAG, this.mContext);
                        this.mDbHelper.saveEntity(entity, true);
                    } else if (entity != null) {
                        if (entity.getOrder(component) != 0) {
                            entity.onAction(actionType, component, null);
                            this.mDbHelper.removeEntity(key, false);
                        } else {
                            this.mEntities.remove(key);
                            this.mDbHelper.removeEntity(key, true);
                        }
                    }
                }
            }
        }
    }

    public boolean rankLaunchPoints(List<LaunchPoint> launchPoints, @NonNull RankingListener listener) {
        if (registerListenerIfNecessary(listener)) {
            return false;
        }
        if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "refreshing Launchpoint ranking");
        }
        synchronized (this.mEntitiesLock) {
            Collections.sort(launchPoints, getLaunchPointComparator());
            this.mLastLaunchPointRankingLogDump.clear();
            this.mLastLaunchPointRankingLogDump.add("Last Launchpoint Ranking Ordering: " + new Date().toString());
            for (LaunchPoint lp : launchPoints) {
                AppsEntity entity = this.mEntities.get(lp.getPackageName());
                if (entity != null) {
                    this.mLastLaunchPointRankingLogDump.add(lp.getTitle() + " | " + "Last Opened " + entity.getOrder(lp.getComponentName()));
                }
            }
            LoggingUtils.logAppRankActionEvent(launchPoints, this.mEntities, this.mContext);
        }
        return true;
    }

    public Comparator<LaunchPoint> getLaunchPointComparator() {
        if (this.mLaunchPointComparator == null) {
            this.mLaunchPointComparator = getSortingMode() == AppsPreferences.SortingMode.RECENCY ? new LaunchPointRecencyComparator() : new LaunchPointInstallComparator();
        }
        return this.mLaunchPointComparator;
    }

    public int insertLaunchPoint(List<LaunchPoint> launchPoints, LaunchPoint newLp) {
        if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Inserting new LaunchPoint");
        }
        if (registerListenerIfNecessary(null)) {
            int pos = launchPoints.size();
            launchPoints.add(newLp);
            return pos;
        }
        int pos = 0;
        Comparator<LaunchPoint> comp = getLaunchPointComparator();
        while (pos < launchPoints.size() && comp.compare(newLp, (LaunchPoint) launchPoints.get(pos)) >= 0) {
            pos++;
        }
        launchPoints.add(pos, newLp);
        return pos;
    }

    private double getLastOpened(LaunchPoint lp) {
        synchronized (this.mEntitiesLock) {
            AppsEntity entity = (AppsEntity) this.mEntities.get(lp.getPackageName());

            if (entity != null) {
                return (double) entity.getLastOpenedTimeStamp(lp.getComponentName());
            }
        }
        return -100.0d;
    }

    private double getEntityOrder(LaunchPoint lp) {
        synchronized (this.mEntitiesLock) {
            AppsEntity entity = this.mEntities.get(lp.getPackageName());
            if (entity != null) {
                return (double) entity.getOrder(lp.getComponentName());
            }
            return 0.0d;
        }

    }

    private boolean registerListenerIfNecessary(RankingListener listener) {
        boolean mustRegister;
        synchronized (this.mCachedActions) {
            mustRegister = this.mQueryingScores;
            if (mustRegister) {
                if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.d(TAG, "Entities not ready");
                }
                if (listener != null) {
                    this.mListeners.add(listener);
                }
            }
        }
        return mustRegister;
    }

    public void onEntitiesLoaded(HashMap<String, AppsEntity> entities) {
        int defaultLength = 0;
        synchronized (this.mEntitiesLock) {
            this.mEntities = entities;
        }
        synchronized (this.mCachedActions) {
            this.mQueryingScores = false;
            if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.d(TAG, "Scores retrieved, playing back " + this.mCachedActions.size() + " actions");
            }
            while (!this.mCachedActions.isEmpty()) {
                CachedAction action = this.mCachedActions.remove();
                onAction(action.key, action.component, action.group, action.action, action.category);
            }
            //if (!Util.initialRankingApplied(this.mContext)) {
            //    String[] outOfBoxOrder = this.mContext.getResources().getStringArray(R.array.out_of_box_order);

            // applyOutOfBoxOrdering(outOfBoxOrder, 0, defaultLength);

//                Util.setInitialRankingAppliedFlag(this.mContext, true);
            //          }
        }
        while (!this.mListeners.isEmpty()) {
            this.mListeners.remove().onRankerReady();
        }
    }

    public boolean isReady() {
        boolean z;
        synchronized (this.mCachedActions) {
            z = !this.mQueryingScores;
        }
        return z;
    }

    public boolean isFavorite(LaunchPoint launchPoint) {
        AppsEntity e = this.mEntities.get(launchPoint.getPackageName());
        return e != null && e.isFavorite();
    }

    public void setFavorited(LaunchPoint launchPoint, boolean isFavorited) {
        AppsEntity e = this.mEntities.get(launchPoint.getPackageName());

        if (e != null) {
            e.setFavorited(isFavorited);
        } else {
            e = new AppsEntity(this.mContext, this.mDbHelper, launchPoint.getPackageName(), isFavorited);
            this.mEntities.put(launchPoint.getPackageName(), e);
        }
    }

    public void saveOrderSnapshot(List<LaunchPoint> r5) {
        for (int i = 0; i < r5.size(); i++) {
            LaunchPoint launcher = r5.get(i);
            saveEntityOrder(launcher, i);
        }
    }

    private void saveEntityOrder(LaunchPoint launchPoint, int position) {
        AppsEntity e = this.mEntities.get(launchPoint.getPackageName());
        if (e != null) {
            e.setOrder(launchPoint.getComponentName(), ((long) position) + 1);
        } else {
            e = new AppsEntity(this.mContext, this.mDbHelper, launchPoint.getPackageName(), 0, (long) (position + 1), isFavorite(launchPoint));
        }
        this.mDbHelper.saveEntity(e, true);
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "==========================");
        for (String lpLine : this.mLastLaunchPointRankingLogDump) {
            writer.println(prefix + " " + lpLine);
        }
        writer.println(prefix + "==========================");
    }

    private void registerPreferencesListeners() {
        unregisterPreferencesListeners();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        if (prefs != null) {
            this.mPrefsListener = new SharedPreferencesChangeListener(this);
            prefs.registerOnSharedPreferenceChangeListener(this.mPrefsListener);
        }
    }

    private void unregisterPreferencesListeners() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        if (prefs != null && this.mPrefsListener != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(this.mPrefsListener);
            this.mPrefsListener = null;
        }
    }

    private void checkForSortingModeChange() {
        AppsPreferences.SortingMode savedSortingMode = AppsPreferences.getSavedSortingMode(this.mContext);
        if (this.mSortingMode != savedSortingMode) {
            this.mSortingMode = savedSortingMode;
            this.mLaunchPointComparator = null;
            this.mNeedsResorting = true;
        }
    }
}
