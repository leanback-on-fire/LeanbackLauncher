package com.rockon999.android.leanbacklauncher.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.AppsDbHelper.Listener;
import com.rockon999.android.leanbacklauncher.apps.AppsManager.SortingMode;
import com.rockon999.android.leanbacklauncher.util.Partner;
import com.rockon999.android.leanbacklauncher.util.Util;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class AppsRanker implements Listener {
    private static final String TAG = "appsranker";
    @SuppressLint({"StaticFieldLeak"})
    private static AppsRanker sAppsRanker = null;
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
    private SortingMode mSortingMode;

    public interface RankingListener {
        void onRankerReady();
    }

    private static class CachedAction {
        int action;
        String component;
        String group;
        String key;

        CachedAction(String k, String c, String g, int a) {
            this.key = k;
            this.component = c;
            this.group = g;
            this.action = a;
        }
    }

    private class LaunchPointInstallComparator implements Comparator<LaunchPoint> {
        private LaunchPointInstallComparator() {
        }

        public int compare(LaunchPoint lhs, LaunchPoint rhs) {
            if (lhs == null || rhs == null) {
                return 0;
            }
            long lhsOrder = AppsRanker.this.getEntityOrder(lhs);
            long rhsOrder = AppsRanker.this.getEntityOrder(rhs);
            if (lhsOrder == rhsOrder) {
                long lInstallTime = lhs.getFirstInstallTime();
                long rInstallTime = rhs.getFirstInstallTime();
                if (lInstallTime < 0 && rInstallTime >= 0) {
                    return 1;
                }
                if (rInstallTime < 0 && lInstallTime >= 0) {
                    return -1;
                }
                if (lInstallTime != rInstallTime) {
                    return lInstallTime > rInstallTime ? 1 : -1;
                } else {
                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                }
            } else if (lhsOrder == 0) {
                return 1;
            } else {
                if (rhsOrder == 0) {
                    return -1;
                }
                return lhsOrder > rhsOrder ? 1 : -1;
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
            return Long.compare(Math.max(AppsRanker.this.getLastOpened(rhs), rhs.getFirstInstallTime()), Math.max(AppsRanker.this.getLastOpened(lhs), lhs.getFirstInstallTime()));
        }
    }

    private static class SharedPreferencesChangeListener implements OnSharedPreferenceChangeListener {
        private WeakReference<AppsRanker> mAppsRankerRef;

        public SharedPreferencesChangeListener(AppsRanker appsRanker) {
            this.mAppsRankerRef = new WeakReference(appsRanker);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            AppsRanker appsRanker = (AppsRanker) this.mAppsRankerRef.get();
            if (appsRanker != null) {
                appsRanker.checkForSortingModeChange();
            }
        }
    }

    private AppsRanker(Context ctx, AppsDbHelper dbHelper) {
        this(ctx, dbHelper, AsyncTask.SERIAL_EXECUTOR);
    }

    AppsRanker(Context ctx, AppsDbHelper dbHelper, Executor executor) {
        this.mEntitiesLock = new Object();
        this.mListeners = new LinkedList();
        this.mCachedActions = new LinkedList();
        this.mEntities = new HashMap();
        this.mSortingMode = SortingMode.FIXED;
        this.mLastLaunchPointRankingLogDump = new ArrayList();
        this.mContext = ctx;
        this.mDbHelper = dbHelper;
        this.mSortingMode = AppsManager.getSavedSortingMode(this.mContext);
        registerPreferencesListeners();
        this.mQueryingScores = true;
        this.mDbHelper.loadEntities(this, executor);
    }

    public static AppsRanker getInstance(Context context) {
        if (sAppsRanker == null) {
            sAppsRanker = new AppsRanker(context.getApplicationContext(), AppsDbHelper.getInstance(context));
        }
        return sAppsRanker;
    }

    public SortingMode getSortingMode() {
        SortingMode savedSortingMode = AppsManager.getSavedSortingMode(this.mContext);
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

    public void onAction(String packageName, String component, int actionType) {
        onAction(packageName, component, null, actionType);
    }

    private void onAction(String key, String component, String group, int actionType) { // , AppCategory category
        if (!TextUtils.isEmpty(key)) {
            if (actionType != 3) {
                this.mNeedsResorting = true;
            }
            synchronized (this.mCachedActions) {
                if (this.mQueryingScores) {
                    // if (DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.d(TAG, "Scores not ready, caching this action");
                    // }
                    this.mCachedActions.add(new CachedAction(key, component, group, actionType)); // , category
                    return;
                }
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "action: " + actionType + " for " + key + " - group = " + group);
                }
                synchronized (this.mEntitiesLock) {
                    AppsEntity entity = this.mEntities.get(key);
                    if (actionType != 3) {
                        if (entity == null) {
                            entity = new AppsEntity(this.mContext, this.mDbHelper, key); //, false
                            this.mEntities.put(key, entity);
                        }
                        entity.onAction(actionType, component, group);
                        //  LoggingUtils.logRankerActionEvent(key, actionType, 0, TAG, this.mContext);
                        this.mDbHelper.saveEntity(entity); // , true
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

    public boolean rankLaunchPoints(ArrayList<LaunchPoint> launchPoints, RankingListener listener) {
        if (registerListenerIfNecessary(listener)) {
            return false;
        }
        if (Log.isLoggable("AppsRanker", 2)) {
            Log.v("AppsRanker", "refreshing Launchpoint ranking");
        }
        synchronized (this.mEntitiesLock) {
            Collections.sort(launchPoints, getLaunchPointComparator());
            this.mLastLaunchPointRankingLogDump.clear();
            this.mLastLaunchPointRankingLogDump.add("Last Launchpoint Ranking Ordering: " + new Date().toString());
            Iterator it = launchPoints.iterator();
            while (it.hasNext()) {
                LaunchPoint lp = (LaunchPoint) it.next();
                AppsEntity entity = (AppsEntity) this.mEntities.get(lp.getPackageName());
                if (entity != null) {
                    this.mLastLaunchPointRankingLogDump.add(lp.getTitle() + " | R " + entity.getOrder(lp.getComponentName()) + " | LO " + getLastOpened(lp) + " | INST " + lp.getFirstInstallTime());
                }
            }
            // LoggingUtils.logAppRankActionEvent(launchPoints, this.mEntities, this.mContext);
        }
        return true;
    }

    public Comparator<LaunchPoint> getLaunchPointComparator() {
        if (this.mLaunchPointComparator == null) {
            this.mLaunchPointComparator = getSortingMode() == SortingMode.RECENCY ? new LaunchPointRecencyComparator() : new LaunchPointInstallComparator();
        }
        return this.mLaunchPointComparator;
    }

    public int insertLaunchPoint(ArrayList<LaunchPoint> launchPoints, LaunchPoint newLp) {
        if (Log.isLoggable("AppsRanker", 2)) {
            Log.v("AppsRanker", "Inserting new LaunchPoint");
        }
        if (registerListenerIfNecessary(null)) {
            int pos = launchPoints.size();
            launchPoints.add(newLp);
            return pos;
        }
        int pos = 0;
        Comparator<LaunchPoint> comp = getLaunchPointComparator();
        while (pos < launchPoints.size() && comp.compare(newLp, launchPoints.get(pos)) >= 0) {
            pos++;
        }
        launchPoints.add(pos, newLp);
        return pos;
    }

    private long getLastOpened(LaunchPoint lp) {
        AppsEntity entity;
        synchronized (this.mEntitiesLock) {
            entity = (AppsEntity) this.mEntities.get(lp.getPackageName());
        }
        return entity != null ? entity.getLastOpenedTimeStamp(lp.getComponentName()) : -100;
    }

    private long getEntityOrder(LaunchPoint lp) {
        AppsEntity entity;
        synchronized (this.mEntitiesLock) {
            entity = (AppsEntity) this.mEntities.get(lp.getPackageName());
        }
        return entity != null ? entity.getOrder(lp.getComponentName()) : 0;
    }

    private boolean registerListenerIfNecessary(RankingListener listener) {
        boolean mustRegister;
        synchronized (this.mCachedActions) {
            mustRegister = this.mQueryingScores;
            if (mustRegister) {
                if (Log.isLoggable("AppsRanker", 2)) {
                    Log.d("AppsRanker", "Entities not ready");
                }
                if (listener != null) {
                    this.mListeners.add(listener);
                }
            }
        }
        return mustRegister;
    }

    public final void onEntitiesLoaded(HashMap<String, AppsEntity> entities) {
        synchronized (this.mEntitiesLock) {
            this.mEntities = entities;
        }
        synchronized (this.mCachedActions) {
            this.mQueryingScores = false;
            if (Log.isLoggable("AppsRanker", 2)) {
                Log.d("AppsRanker", "Scores retrieved, playing back " + this.mCachedActions.size() + " actions");
            }
            while (!this.mCachedActions.isEmpty()) {
                CachedAction action = (CachedAction) this.mCachedActions.remove();
                onAction(action.key, action.component, action.group, action.action);
            }
            if (!Util.initialRankingApplied(this.mContext)) {
                String[] outOfBoxOrder = getOutOfBoxOrder();
                String[] partnerOutOfBoxOrder = Partner.get(this.mContext).getOutOfBoxOrder();
                int partnerLength = partnerOutOfBoxOrder != null ? partnerOutOfBoxOrder.length : 0;
                int totalOrderings = outOfBoxOrder.length + partnerLength;
                long baseTime = System.currentTimeMillis();
                if (partnerOutOfBoxOrder != null) {
                    applyOutOfBoxOrdering(partnerOutOfBoxOrder, 0, totalOrderings, baseTime);
                }
                applyOutOfBoxOrdering(outOfBoxOrder, partnerLength, totalOrderings, baseTime);
                Util.setInitialRankingAppliedFlag(this.mContext, true);
            }
        }
        while (!this.mListeners.isEmpty()) {
            ((RankingListener) this.mListeners.remove()).onRankerReady();
        }
    }

    String[] getOutOfBoxOrder() {
        return this.mContext.getResources().getStringArray(R.array.out_of_box_order);
    }

    public boolean isReady() {
        boolean z;
        synchronized (this.mCachedActions) {
            z = !this.mQueryingScores;
        }
        return z;
    }

    private void applyOutOfBoxOrdering(String[] order, int offsetEntities, int totalEntities, long baseTime) {
        if (order != null && order.length != 0 && offsetEntities >= 0 && totalEntities >= order.length + offsetEntities) {
            int entitiesBelow = (totalEntities - offsetEntities) - order.length;
            int size = order.length;
            for (int i = 0; i < size; i++) {
                String key = order[(size - i) - 1];
                if (!this.mEntities.containsKey(key)) {
                    long score = ((long) ((entitiesBelow + i) + 1)) + baseTime;
                    AppsEntity e = new AppsEntity(this.mContext, this.mDbHelper, key, score, (long) ((entitiesBelow + size) - i));
                    this.mEntities.put(key, e);
                    this.mDbHelper.saveEntity(e);
                }
            }
        }
    }

    public void saveOrderSnapshot(ArrayList<LaunchPoint> launchPoints) {
        synchronized (this.mEntitiesLock) {
            for (int i = 0; i < launchPoints.size(); i++) {
                saveEntityOrder((LaunchPoint) launchPoints.get(i), i);
            }
        }
    }

    private void saveEntityOrder(LaunchPoint launchPoint, int position) {
        AppsEntity e = (AppsEntity) this.mEntities.get(launchPoint.getPackageName());
        if (e != null) {
            e.setOrder(launchPoint.getComponentName(), ((long) position) + 1);
        } else {
            e = new AppsEntity(this.mContext, this.mDbHelper, launchPoint.getPackageName(), 0, (long) (position + 1));
            this.mEntities.put(launchPoint.getPackageName(), e);
        }
        this.mDbHelper.saveEntity(e);
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "==========================");
        Iterator it = this.mLastLaunchPointRankingLogDump.iterator();
        while (it.hasNext()) {
            writer.println(prefix + " " + ((String) it.next()));
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
        SortingMode savedSortingMode = AppsManager.getSavedSortingMode(this.mContext);
        if (this.mSortingMode != savedSortingMode) {
            this.mSortingMode = savedSortingMode;
            this.mLaunchPointComparator = null;
            this.mNeedsResorting = true;
        }
    }
}
