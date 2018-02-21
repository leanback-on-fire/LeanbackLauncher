package com.rockon999.android.leanbacklauncher.apps;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.util.FireTVUtils;
import com.rockon999.android.leanbacklauncher.util.SettingsUtil;
import com.rockon999.android.leanbacklauncher.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class LaunchPointListGenerator {
    private static final String TAG = "LaunchPointList";
    private final List<LaunchPoint> mAllLaunchPoints;
    private final Queue<CachedAction> mCachedActions;
    private final Context mContext;
    private boolean mExcludeChannelActivities;
    private final List<LaunchPoint> mInstallingLaunchPoints;
    private boolean mIsReady;
    private final List<Listener> mListeners;
    private final Object mLock;
    private HashMap<String, Integer> mNonUpdatableBlacklist;
    private ArrayList<LaunchPoint> mSettingsLaunchPoints;
    private boolean mShouldNotify;
    private HashMap<String, Integer> mUpdatableBlacklist;

    public interface Listener {
        void onLaunchPointListGeneratorReady();

        void onLaunchPointsAddedOrUpdated(ArrayList<LaunchPoint> arrayList);

        void onLaunchPointsRemoved(ArrayList<LaunchPoint> arrayList);

        void onSettingsChanged();
    }

    private class CachedAction {
        int mAction;
        LaunchPoint mLaunchPoint;
        String mPkgName;
        boolean mSuccess;
        boolean mUpdatable;

        CachedAction(int action, String pkgName) {
            this.mSuccess = false;
            this.mUpdatable = true;
            this.mAction = action;
            this.mPkgName = pkgName;
        }

        CachedAction(LaunchPointListGenerator this$0, int action, String pkgName, boolean updatable) {
            this(action, pkgName);
            this.mUpdatable = updatable;
        }

        CachedAction(int action, LaunchPoint launchPoint) {
            this.mSuccess = false;
            this.mUpdatable = true;
            this.mAction = action;
            this.mLaunchPoint = launchPoint;
        }

        CachedAction(LaunchPointListGenerator this$0, int action, LaunchPoint launchPoint, boolean success) {
            this(action, launchPoint);
            this.mSuccess = success;
        }

        @SuppressLint("PrivateResource")
        public void apply() {
            switch (this.mAction) {
                case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                    LaunchPointListGenerator.this.addOrUpdatePackage(this.mPkgName);
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    LaunchPointListGenerator.this.removePackage(this.mPkgName);
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    LaunchPointListGenerator.this.addToBlacklist(this.mPkgName, this.mUpdatable);
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                    LaunchPointListGenerator.this.removeFromBlacklist(this.mPkgName, this.mUpdatable);
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                    LaunchPointListGenerator.this.addOrUpdateInstallingLaunchPoint(this.mLaunchPoint);
                case android.support.v7.preference.R.styleable.Preference_android_selectable /*5*/:
                    LaunchPointListGenerator.this.removeInstallingLaunchPoint(this.mLaunchPoint, this.mSuccess);
                default:
            }
        }
    }

    private class CreateLaunchPointListTask extends AsyncTask<Void, Void, List<LaunchPoint>> {
        private final boolean mFilterChannelsActivities;

        public CreateLaunchPointListTask(boolean excludeChannelActivities) {
            this.mFilterChannelsActivities = excludeChannelActivities;
        }

        protected List<LaunchPoint> doInBackground(Void... params) {
            Intent mainIntent = new Intent("android.intent.action.MAIN");
            mainIntent.addCategory("android.intent.category.LAUNCHER");

            Intent tvIntent = new Intent("android.intent.action.MAIN");
            tvIntent.addCategory("android.intent.category.LEANBACK_LAUNCHER");

            List<LaunchPoint> launcherItems = new LinkedList<>();

            PackageManager pkgMan = LaunchPointListGenerator.this.mContext.getPackageManager();
            List<ResolveInfo> normLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
            List<ResolveInfo> tvLaunchPoints = pkgMan.queryIntentActivities(tvIntent, 129);

            Map<String, String> rawComponents = new HashMap<>();
            List<ResolveInfo> allLaunchPoints = new ArrayList<>();

            if (tvLaunchPoints != null && tvLaunchPoints.size() > 0) {
                for (ResolveInfo itemTvLaunchPoint : tvLaunchPoints) {
                    if (itemTvLaunchPoint.activityInfo != null && itemTvLaunchPoint.activityInfo.packageName != null && itemTvLaunchPoint.activityInfo.name != null) {
                        rawComponents.put(itemTvLaunchPoint.activityInfo.packageName, itemTvLaunchPoint.activityInfo.name);
                        allLaunchPoints.add(itemTvLaunchPoint);
                    }
                }
            }

            if (normLaunchPoints != null && normLaunchPoints.size() > 0) {
                for (ResolveInfo itemRawLaunchPoint : normLaunchPoints) {
                    if (itemRawLaunchPoint.activityInfo != null && itemRawLaunchPoint.activityInfo.packageName != null && itemRawLaunchPoint.activityInfo.name != null) {
                        // any system app that isn't TV-optimized likely isn't something the user needs or wants [except for Amazon Music & Photos (which apparently don't get leanback launchers :\)]
                        if (!Util.isSystemApp(LaunchPointListGenerator.this.mContext, itemRawLaunchPoint.activityInfo.packageName) || itemRawLaunchPoint.activityInfo.packageName.startsWith("com.amazon.bueller")) { // todo optimize & don't hardcode
                            if (!rawComponents.containsKey(itemRawLaunchPoint.activityInfo.packageName)) {
                                allLaunchPoints.add(itemRawLaunchPoint);
                            }
                        }
                    }
                }
            }

            for (int x = 0, size = allLaunchPoints.size(); x < size; x++) {
                ResolveInfo info = allLaunchPoints.get(x);

                ActivityInfo activityInfo = info.activityInfo;

                if (activityInfo != null) {
                    launcherItems.add(new LaunchPoint(LaunchPointListGenerator.this.mContext, pkgMan, info));
                }
            }

            return launcherItems;
        }

        public void onPostExecute(List<LaunchPoint> launcherItems) {
            synchronized (LaunchPointListGenerator.this.mLock) {
                LaunchPointListGenerator.this.mAllLaunchPoints.clear();
                LaunchPointListGenerator.this.mAllLaunchPoints.addAll(launcherItems);
            }
            synchronized (LaunchPointListGenerator.this.mCachedActions) {
                Log.i(TAG, "mCachedActions is empty:" + mCachedActions.isEmpty());
                LaunchPointListGenerator.this.mIsReady = true;
                LaunchPointListGenerator.this.mShouldNotify = true;
                for (Listener onLaunchPointListGeneratorReady : LaunchPointListGenerator.this.mListeners) {
                    Log.i(TAG, "onLaunchPointListGeneratorReady->className:" + onLaunchPointListGeneratorReady.getClass().getName());
                    onLaunchPointListGeneratorReady.onLaunchPointListGeneratorReady();
                }
            }
        }
    }


    public LaunchPointListGenerator(Context ctx) {
        this.mIsReady = false;
        this.mShouldNotify = false;
        this.mCachedActions = new LinkedList<>();
        this.mListeners = new LinkedList<>();
        this.mAllLaunchPoints = new LinkedList<>();
        this.mInstallingLaunchPoints = new LinkedList<>();
        this.mUpdatableBlacklist = new HashMap<>();
        this.mNonUpdatableBlacklist = new HashMap<>();
        this.mLock = new Object();
        this.mContext = ctx;
    }

    public void setExcludeChannelActivities(boolean excludeChannelActivities) {
        if (this.mExcludeChannelActivities != excludeChannelActivities) {
            this.mExcludeChannelActivities = excludeChannelActivities;
            refreshLaunchPointList();
        }
    }

    public void registerChangeListener(Listener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void addOrUpdatePackage(String pkgName) {
        if (!TextUtils.isEmpty(pkgName)) {
            synchronized (this.mCachedActions) {
                if (this.mIsReady) {
                    synchronized (this.mLock) {
                        ArrayList<LaunchPoint> removedLaunchPoints = new ArrayList<>();
                        ArrayList<LaunchPoint> launchPoints = createLaunchPoints(pkgName, removedLaunchPoints);

                        if (!launchPoints.isEmpty()) {

                            this.mAllLaunchPoints.addAll(launchPoints);
                            if (!isBlacklisted(pkgName) && this.mShouldNotify) {
                                for (Listener cl : this.mListeners) {
                                    cl.onLaunchPointsAddedOrUpdated(launchPoints);
                                }
                            }
                        }
                        if (!(removedLaunchPoints.isEmpty() || isBlacklisted(pkgName) || !this.mShouldNotify)) {
                            for (Listener cl2 : this.mListeners) {
                                cl2.onLaunchPointsRemoved(removedLaunchPoints);
                            }
                        }
                        if (packageHasSettingsEntry(pkgName)) {
                            for (Listener cl22 : this.mListeners) {
                                cl22.onSettingsChanged();
                            }
                        }
                    }
                    return;
                }
                this.mCachedActions.add(new CachedAction(0, pkgName));
            }
        }
    }

    public void removePackage(String pkgName) {
        if (!TextUtils.isEmpty(pkgName)) {
            synchronized (this.mCachedActions) {
                if (this.mIsReady) {
                    synchronized (this.mLock) {
                        ArrayList<LaunchPoint> removedLaunchPoints = new ArrayList<>();
                        getLaunchPointsByCategory(this.mInstallingLaunchPoints, removedLaunchPoints, pkgName, true);
                        getLaunchPointsByCategory(this.mAllLaunchPoints, removedLaunchPoints, pkgName, true);
                        if (!(removedLaunchPoints.isEmpty() || isBlacklisted(pkgName))) {
                            if (this.mShouldNotify) {
                                for (Listener cl : this.mListeners) {
                                    cl.onLaunchPointsRemoved(removedLaunchPoints);
                                }
                            }
                        }
                        if (packageHasSettingsEntry(pkgName)) {
                            for (Listener cl2 : this.mListeners) {
                                cl2.onSettingsChanged();
                            }
                        }
                    }
                    return;
                }
                this.mCachedActions.add(new CachedAction(1, pkgName));
            }
        }
    }

    public boolean addToBlacklist(String pkgName) {
        return addToBlacklist(pkgName, true);
    }

    public boolean addToBlacklist(String pkgName, boolean updatable) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        synchronized (this.mCachedActions) {
            if (this.mIsReady) {
                boolean added = false;
                synchronized (this.mLock) {
                    HashMap<String, Integer> blacklist = updatable ? this.mUpdatableBlacklist : this.mNonUpdatableBlacklist;
                    Integer occurrences = blacklist.get(pkgName);
                    Integer otherOccurrences = (updatable ? this.mNonUpdatableBlacklist : this.mUpdatableBlacklist).get(pkgName);
                    if (occurrences == null || occurrences <= 0) {
                        occurrences = 0;
                        if (otherOccurrences == null || otherOccurrences <= 0) {
                            added = true;
                            ArrayList<LaunchPoint> blacklistedLaunchPoints = new ArrayList<>();
                            getLaunchPointsByCategory(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                            getLaunchPointsByCategory(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                            if (!blacklistedLaunchPoints.isEmpty() && this.mShouldNotify) {
                                for (Listener cl : this.mListeners) {
                                    cl.onLaunchPointsRemoved(blacklistedLaunchPoints);
                                }
                            }
                        }
                    }
                    int intValue = occurrences + 1;
                    occurrences = intValue;
                    blacklist.put(pkgName, intValue);
                }
                return added;
            }
            this.mCachedActions.add(new CachedAction(this, 2, pkgName, updatable));
            return false;
        }
    }

    public boolean removeFromBlacklist(String pkgName) {
        return removeFromBlacklist(pkgName, false, true);
    }

    public boolean removeFromBlacklist(String pkgName, boolean updatable) {
        return removeFromBlacklist(pkgName, false, updatable);
    }

    private boolean removeFromBlacklist(String pkgName, boolean force, boolean updatable) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        synchronized (this.mCachedActions) {
            if (this.mIsReady) {
                boolean removed = false;
                synchronized (this.mLock) {
                    HashMap<String, Integer> blacklist = updatable ? this.mUpdatableBlacklist : this.mNonUpdatableBlacklist;
                    Integer occurrences = blacklist.get(pkgName);
                    Integer otherOccurrences = (updatable ? this.mNonUpdatableBlacklist : this.mUpdatableBlacklist).get(pkgName);
                    if (occurrences != null) {
                        occurrences = occurrences - 1;
                        if (occurrences <= 0 || force) {
                            blacklist.remove(pkgName);
                            if (otherOccurrences == null) {
                                removed = true;
                                ArrayList<LaunchPoint> blacklistedLaunchPoints = new ArrayList<>();
                                getLaunchPointsByCategory(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                                getLaunchPointsByCategory(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                                if (!blacklistedLaunchPoints.isEmpty() && this.mShouldNotify) {
                                    for (Listener cl : this.mListeners) {
                                        cl.onLaunchPointsAddedOrUpdated(blacklistedLaunchPoints);
                                    }
                                }
                            }
                        } else {
                            blacklist.put(pkgName, occurrences);
                        }
                    }
                }
                return removed;
            }
            this.mCachedActions.add(new CachedAction(this, 3, pkgName, updatable));
            return false;
        }
    }

    public void addOrUpdateInstallingLaunchPoint(LaunchPoint launchPoint) {
        if (launchPoint != null) {
            synchronized (this.mCachedActions) {
                if (this.mIsReady) {
                    String pkgName = launchPoint.getPackageName();
                    ArrayList<LaunchPoint> launchPoints = new ArrayList<>();
                    synchronized (this.mLock) {
                        getLaunchPointsByCategory(this.mInstallingLaunchPoints, launchPoints, pkgName, true);
                        getLaunchPointsByCategory(this.mAllLaunchPoints, launchPoints, pkgName, true);
                        for (int i = 0; i < launchPoints.size(); i++) {
                            launchPoints.get(i).setInstallationState(launchPoint);
                        }
                        if (launchPoints.isEmpty()) {
                            launchPoints.add(launchPoint);
                        }
                        this.mInstallingLaunchPoints.addAll(launchPoints);
                        if (!isBlacklisted(pkgName) && this.mShouldNotify) {
                            for (Listener cl : this.mListeners) {
                                cl.onLaunchPointsAddedOrUpdated(launchPoints);
                            }
                        }
                    }
                    return;
                }
                this.mCachedActions.add(new CachedAction(4, launchPoint));
            }
        }
    }

    public void removeInstallingLaunchPoint(LaunchPoint launchPoint, boolean success) {
        if (launchPoint != null) {
            synchronized (this.mCachedActions) {
                if (this.mIsReady) {
                    if (!success) {
                        addOrUpdatePackage(launchPoint.getPackageName());
                    }
                    return;
                }
                this.mCachedActions.add(new CachedAction(this, 5, launchPoint, success));
            }
        }
    }

    private ArrayList<LaunchPoint> getLaunchPointsByCategory(List<LaunchPoint> parentList, ArrayList<LaunchPoint> removeLaunchPoints, String pkgName, boolean remove) {
        if (removeLaunchPoints == null) {
            removeLaunchPoints = new ArrayList<>();
        }
        Iterator<LaunchPoint> itt = parentList.iterator();
        while (itt.hasNext()) {
            LaunchPoint lp = itt.next();
            if (TextUtils.equals(pkgName, lp.getPackageName())) {
                removeLaunchPoints.add(lp);
                if (remove) {
                    itt.remove();
                }
            }
        }
        return removeLaunchPoints;
    }

    public ArrayList<LaunchPoint> getLaunchPointsByType(AppCategory type) {
        return getLaunchPointsByCategory(type);
    }

    public ArrayList<LaunchPoint> getAllLaunchPoints() {
        ArrayList<LaunchPoint> allLaunchPoints = new ArrayList<>();
        if (mAllLaunchPoints != null && mAllLaunchPoints.size() > 0) {
            allLaunchPoints.addAll(mAllLaunchPoints);
        }
        return allLaunchPoints;
    }

    private ArrayList<LaunchPoint> getLaunchPointsByCategory(AppCategory type) {
        ArrayList<LaunchPoint> launchPoints = new ArrayList<>();
        synchronized (this.mLock) {
            getLaunchPointsLocked(this.mInstallingLaunchPoints, launchPoints, type);
            getLaunchPointsLocked(this.mAllLaunchPoints, launchPoints, type);
        }
        return launchPoints;
    }

    // todo clean up the AppCategory mess
    private void getLaunchPointsLocked(List<LaunchPoint> parentList, List<LaunchPoint> childList, AppCategory type) {
        switch (type) {
            case GAME:
                for (LaunchPoint lp : parentList) {
                    if (!isBlacklisted(lp.getPackageName()) && lp.isGame()) {
                        childList.add(lp);
                    }
                }
                break;
            case MUSIC:
                for (LaunchPoint lp : parentList) {
                    if (!isBlacklisted(lp.getPackageName()) && lp.getAppCategory() == AppCategory.MUSIC) {
                        childList.add(lp);
                    }
                }
                break;
            case SETTINGS:
                childList.addAll(getSettingsLaunchPoints(false));
                break;
            case VIDEO:
                for (LaunchPoint lp : parentList) {
                    if (!isBlacklisted(lp.getPackageName()) && lp.getAppCategory() == AppCategory.VIDEO) {
                        childList.add(lp);
                    }
                }
                break;
            case OTHER:
                for (LaunchPoint lp : parentList) {
                    if (!isBlacklisted(lp.getPackageName()) && lp.getAppCategory() == AppCategory.OTHER) {
                        childList.add(lp);
                    }
                }
                break;
        }

    }

    public ArrayList<LaunchPoint> getSettingsLaunchPoints(boolean force) {
        if (force || this.mSettingsLaunchPoints == null) {
            this.mSettingsLaunchPoints = createSettingsList();
        }
        ArrayList<LaunchPoint> copied = new ArrayList<>();
        copied.addAll(this.mSettingsLaunchPoints);
        return copied;
    }

    public void refreshLaunchPointList() {
        Log.i(TAG, "refreshLaunchPointList");
        synchronized (this.mCachedActions) {
            this.mIsReady = false;
            this.mShouldNotify = false;
        }
        new CreateLaunchPointListTask(this.mExcludeChannelActivities).execute();
    }

    public boolean isReady() {
        boolean z;
        synchronized (this.mCachedActions) {
            z = this.mIsReady;
        }
        return z;
    }

    private ArrayList<LaunchPoint> createLaunchPoints(String pkgName, ArrayList<LaunchPoint> reusable) {
        Iterator<ResolveInfo> rawItt;

        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.setPackage(pkgName).addCategory("android.intent.category.LAUNCHER");
        ArrayList<LaunchPoint> launchPoints = new ArrayList<>();
        PackageManager pkgMan = this.mContext.getPackageManager();
        List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);

        rawItt = rawLaunchPoints.iterator();

        while (rawItt.hasNext()) {
            launchPoints.add(new LaunchPoint(this.mContext, pkgMan, rawItt.next()));
        }
        return launchPoints;
    }

    private Set<ComponentName> getChannelActivities() {
        HashSet<ComponentName> channelActivities = new HashSet<>();
        for (ResolveInfo info : this.mContext.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", TvContract.buildChannelUri(0)), 513)) {
            if (info.activityInfo != null) {
                channelActivities.add(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
            }
        }
        return channelActivities;
    }

    private ArrayList<LaunchPoint> createSettingsList() {
        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.addCategory("android.intent.category.LEANBACK_SETTINGS");

        ArrayList<LaunchPoint> settingsItems = new ArrayList<>();
        PackageManager pkgMan = this.mContext.getPackageManager();
        List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
        HashMap<ComponentName, Integer> specialEntries = new HashMap<>();
        specialEntries.put(getComponentNameForSettingsActivity("android.settings.WIFI_SETTINGS"), SettingsUtil.Type.WIFI.getCode());

        for (int ptr = 0, size = rawLaunchPoints.size(); ptr < size; ptr++) {
            ResolveInfo info = rawLaunchPoints.get(ptr);
            ComponentName comp = getComponentName(info);
            int type = -1;

            if (specialEntries.containsKey(comp)) {
                type = specialEntries.get(comp);
            }

            if (info.activityInfo != null) {
                LaunchPoint lp = new LaunchPoint(this.mContext, pkgMan, info, false, type);
                lp.addLaunchIntentFlags(32768);
                settingsItems.add(lp);
            }
        }

        LaunchPoint lp = new LaunchPoint();
        lp.setTitle(mContext.getString(R.string.launcher_settings));
        lp.setIconDrawable(mContext.getDrawable(R.drawable.ic_settings_home));
        lp.setSettingsType(SettingsUtil.Type.APP_CONFIGURE.getCode());
        settingsItems.add(lp);

        lp = new LaunchPoint(this.mContext, mContext.getString(R.string.notifications), mContext.getDrawable(R.drawable.ic_settings_notification), FireTVUtils.getNotificationCenterIntent(), 0);
        lp.addLaunchIntentFlags(32768);
        lp.setSettingsType(SettingsUtil.Type.NOTIFICATIONS.getCode());
        settingsItems.add(lp);

        lp = new LaunchPoint();
        lp.setTitle(mContext.getString(R.string.edit_favorites));
        lp.setIconDrawable(mContext.getDrawable(R.drawable.ic_star_white));
        lp.setSettingsType(SettingsUtil.Type.EDIT_FAVORITES.getCode());
        settingsItems.add(lp);

        return settingsItems;
    }

    public boolean packageHasSettingsEntry(String packageName) {
        if (this.mSettingsLaunchPoints != null) {
            for (int i = 0; i < this.mSettingsLaunchPoints.size(); i++) {
                if (TextUtils.equals(this.mSettingsLaunchPoints.get(i).getPackageName(), packageName)) {
                    return true;
                }
            }
        }
        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.addCategory("android.intent.category.PREFERENCE");
        List<ResolveInfo> rawLaunchPoints = this.mContext.getPackageManager().queryIntentActivities(mainIntent, 129);
        int size = rawLaunchPoints.size();
        for (int ptr = 0; ptr < size; ptr++) {
            ResolveInfo info = rawLaunchPoints.get(ptr);
            if (info.activityInfo != null) {
                //boolean system = (info.activityInfo.applicationInfo.flags & 1) != 0;
                // Why discriminate against user-space settings app?
                if (/*system &&*/ TextUtils.equals(info.activityInfo.applicationInfo.packageName, packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ComponentName getComponentName(ResolveInfo info) {
        if (info == null) {
            return null;
        }
        return new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
    }

    private ComponentName getComponentNameForSettingsActivity(String action) {
        Intent mainIntent = new Intent(action);
        // mainIntent.addCategory("android.intent.category.PREFERENCE");
        List<ResolveInfo> launchPoints = this.mContext.getPackageManager().queryIntentActivities(mainIntent, 129);
        if (launchPoints.size() > 0) {
            int size = launchPoints.size();
            for (int ptr = 0; ptr < size; ptr++) {
                ResolveInfo info = launchPoints.get(ptr);

                // todo fix this
                if (info.activityInfo != null && info.activityInfo.packageName.contains("rockon999")) {
                    return getComponentName(info);
                }
            }
        }

        if (launchPoints.size() > 0) {
            int size = launchPoints.size();
            for (int ptr = 0; ptr < size; ptr++) {
                ResolveInfo info = launchPoints.get(ptr);

                if (info.activityInfo != null) {
                    return getComponentName(info);
                }
            }
        }
        return null;
    }

    private boolean isBlacklisted(String pkgName) {
        return this.mUpdatableBlacklist.containsKey(pkgName) || this.mNonUpdatableBlacklist.containsKey(pkgName);
    }
}
