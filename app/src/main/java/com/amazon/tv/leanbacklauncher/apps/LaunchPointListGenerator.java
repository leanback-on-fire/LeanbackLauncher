package com.amazon.tv.leanbacklauncher.apps;

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

import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory;
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils;
import com.amazon.tv.firetv.leanbacklauncher.util.SettingsUtil;
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil;
import com.amazon.tv.leanbacklauncher.BuildConfig;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.util.Util;

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
    private List<LaunchPoint> mAllLaunchPoints;
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
                case 0 /*0*/:
                    LaunchPointListGenerator.this.addOrUpdatePackage(this.mPkgName);
                case 1 /*1*/:
                    LaunchPointListGenerator.this.removePackage(this.mPkgName);
                case 2 /*2*/:
                    LaunchPointListGenerator.this.addToBlacklist(this.mPkgName, this.mUpdatable);
                case 3 /*3*/:
                    LaunchPointListGenerator.this.removeFromBlacklist(this.mPkgName, this.mUpdatable);
                case 4 /*4*/:
                    LaunchPointListGenerator.this.addOrUpdateInstallingLaunchPoint(this.mLaunchPoint);
                case 5 /*5*/:
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
                        if (!Util.isSystemApp(LaunchPointListGenerator.this.mContext, itemRawLaunchPoint.activityInfo.packageName) ||
                                itemRawLaunchPoint.activityInfo.packageName.startsWith("com.amazon.bueller") ||
                                itemRawLaunchPoint.activityInfo.packageName.startsWith("com.amazon.venezia") ||
                                itemRawLaunchPoint.activityInfo.packageName.startsWith("com.amazon.imdb.tv") &&
                                        !itemRawLaunchPoint.activityInfo.packageName.startsWith("com.amazon.hedwig") // broken launchpoint
                        ) {
                            if (!rawComponents.containsKey(itemRawLaunchPoint.activityInfo.packageName) &&
                                    !itemRawLaunchPoint.activityInfo.packageName.equals(LaunchPointListGenerator.this.mContext.getPackageName())
                            ) {
                                allLaunchPoints.add(itemRawLaunchPoint);
                            } // todo optimize & don't hardcode
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
                LaunchPointListGenerator.this.mAllLaunchPoints = new ArrayList<>();
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

                        // TODO: sync with CreateLaunchPointListTask
                        ArrayList<LaunchPoint> launchPoints = createLaunchPoints(pkgName);

                        if (!launchPoints.isEmpty()) {

                            // remove every launcher with this package
                            for (int x = 0; x < this.mAllLaunchPoints.size(); x++) {
                                LaunchPoint lp = this.mAllLaunchPoints.get(x);

                                if (lp != null && pkgName.equalsIgnoreCase(lp.getPackageName())) {
                                    this.mAllLaunchPoints.remove(x);
                                }
                            }
                            this.mAllLaunchPoints.addAll(launchPoints);

                            if (!isBlacklisted(pkgName) && this.mShouldNotify) {
                                for (Listener cl : this.mListeners) {
                                    cl.onLaunchPointsAddedOrUpdated(launchPoints);
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
                        getLaunchPointsByPackage(this.mInstallingLaunchPoints, removedLaunchPoints, pkgName, true);
                        getLaunchPointsByPackage(this.mAllLaunchPoints, removedLaunchPoints, pkgName, true);
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
                            getLaunchPointsByPackage(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                            getLaunchPointsByPackage(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
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
                                getLaunchPointsByPackage(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                                getLaunchPointsByPackage(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
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
                        getLaunchPointsByPackage(this.mInstallingLaunchPoints, launchPoints, pkgName, true);
                        getLaunchPointsByPackage(this.mAllLaunchPoints, launchPoints, pkgName, true);
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

    private List<LaunchPoint> getLaunchPointsByPackage(List<LaunchPoint> parentList, List<LaunchPoint> removeLaunchPoints, String pkgName, boolean remove) {
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

    public ArrayList<LaunchPoint> getAllLaunchPoints() {
        ArrayList<LaunchPoint> allLaunchPoints = new ArrayList<>();
        if (mAllLaunchPoints != null && mAllLaunchPoints.size() > 0) {
            for (LaunchPoint lp : mAllLaunchPoints) {
                if (!isBlacklisted(lp.getPackageName())) {
                    allLaunchPoints.add(lp);
                }
            }
        }

        return allLaunchPoints;
    }

    public ArrayList<LaunchPoint> getLaunchPointsByCategory(AppCategory... types) {
        ArrayList<LaunchPoint> launchPoints = new ArrayList<>();
        synchronized (this.mLock) {
            for (AppCategory category : types) {
                getLaunchPointsLocked(this.mInstallingLaunchPoints, launchPoints, category);
                getLaunchPointsLocked(this.mAllLaunchPoints, launchPoints, category);
            }
        }
        return launchPoints;
    }

    // todo clean up the AppCategory mess
    private void getLaunchPointsLocked(List<LaunchPoint> parentList, List<LaunchPoint> childList, AppCategory category) {
        switch (category) {
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
            case SETTINGS:
                childList.addAll(getSettingsLaunchPoints(false));
                break;
        }
    }

    public ArrayList<LaunchPoint> getSettingsLaunchPoints(boolean force) {
        if (force || this.mSettingsLaunchPoints == null) {
            this.mSettingsLaunchPoints = createSettingsList();
        }
        return new ArrayList<>(this.mSettingsLaunchPoints);
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

    private ArrayList<LaunchPoint> createLaunchPoints(String pkgName) {
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
        // LEANBACK_SETTINGS
        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.addCategory("android.intent.category.LEANBACK_SETTINGS");
        ArrayList<LaunchPoint> settingsItems = new ArrayList<>();
        PackageManager pkgMan = this.mContext.getPackageManager();
        List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
        HashMap<ComponentName, Integer> specialEntries = new HashMap<>();
        // WI-FI
        specialEntries.put(getComponentNameForSettingsActivity("android.settings.WIFI_SETTINGS"), SettingsUtil.SettingsType.WIFI.getCode());
        if (Util.isPackageEnabled(this.mContext, "com.android.tv.settings")) {
            specialEntries.put(ComponentName.unflattenFromString("com.android.tv.settings/.connectivity.NetworkActivity"), SettingsUtil.SettingsType.WIFI.getCode());
        }
        for (int ptr = 0, size = rawLaunchPoints.size(); ptr < size; ptr++) {
            ResolveInfo info = rawLaunchPoints.get(ptr);
            ComponentName comp = getComponentName(info);
            int type = -1;

            if (specialEntries.containsKey(comp)) {
                type = specialEntries.get(comp);
            }

            if (info.activityInfo != null) {
                LaunchPoint lp = new LaunchPoint(this.mContext, pkgMan, info, false, type);
                lp.addLaunchIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // 32768 (0x8000)
                // lp.setPriority(0);
                settingsItems.add(lp);
            }
        }
        // LAUNCHER SETTINGS // 4
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(this.mContext.getPackageName() + "/.settings.LegacyHomeScreenSettingsActivity"));
        LaunchPoint lp = new LaunchPoint(this.mContext, mContext.getString(R.string.launcher_settings), mContext.getDrawable(R.drawable.ic_settings_launcher), intent, 0);
        lp.addLaunchIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        lp.setSettingsType(SettingsUtil.SettingsType.APP_CONFIGURE.getCode());
        lp.setPriority(-4);
        settingsItems.add(lp);
        // NOTIFICATIONS // 3
        if (FireTVUtils.isLocalNotificationsEnabled(this.mContext)) {
            lp = new LaunchPoint(this.mContext, mContext.getString(R.string.notifications), mContext.getDrawable(R.drawable.ic_settings_notification), FireTVUtils.getNotificationCenterIntent(), 0);
            lp.addLaunchIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            lp.setSettingsType(SettingsUtil.SettingsType.NOTIFICATIONS.getCode());
            lp.setPriority(-3);
            settingsItems.add(lp);
        }
        // SYS SETTINGS // 1
        if (FireTVUtils.isLauncherSettingsEnabled(this.mContext) & !Util.isPackageEnabled(this.mContext, "com.android.tv.settings")) {
            lp = new LaunchPoint(this.mContext, mContext.getString(R.string.system_settings), mContext.getDrawable(R.drawable.ic_settings_settings), FireTVUtils.getSystemSettingsIntent(), 0);
            lp.addLaunchIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            lp.setSettingsType(SettingsUtil.SettingsType.UNKNOWN.getCode());
            lp.setPriority(-1);
            settingsItems.add(lp);
        }
        // NETWORK (AMAZON) // 2
        if (Util.isPackageEnabled(this.mContext, "com.amazon.tv.settings.v2") & !Util.isPackageEnabled(this.mContext, "com.android.tv.settings")) {
            Intent wintent = new Intent();
            wintent.setComponent(ComponentName.unflattenFromString("com.amazon.tv.settings.v2/.tv.network.NetworkActivity"));
            LaunchPoint wlp = new LaunchPoint(this.mContext, mContext.getString(R.string.settings_network), mContext.getDrawable(R.drawable.network_state_disconnected), wintent, 0);
            wlp.addLaunchIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            wlp.setSettingsType(SettingsUtil.SettingsType.WIFI.getCode());
            wlp.setPriority(-2);
            settingsItems.add(wlp);
        }
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
        // ComponentName.unflattenFromString("com.amazon.tv.settings.v2/.tv.network.NetworkActivity")
        Intent mainIntent = new Intent(action);
        // mainIntent.addCategory("android.intent.category.PREFERENCE");
        List<ResolveInfo> launchPoints = this.mContext.getPackageManager().queryIntentActivities(mainIntent, 129);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "getComponentNameForSettingsActivity(" + action + ") got " + launchPoints.toString());
        // com.amazon.tv.settings.v2/.tv.network.NetworkActivity
        if (launchPoints.size() > 0) {
            int size = launchPoints.size();
            for (int ptr = 0; ptr < size; ptr++) {
                ResolveInfo info = launchPoints.get(ptr);
                // todo fix this
                // if (info.activityInfo != null && info.activityInfo.packageName.contains("com.amazon.tv.leanbacklauncher")) {
                if (info.activityInfo != null) {
                    return getComponentName(info);
                }
            }
        }
//      if (launchPoints.size() > 0) {
//          int size = launchPoints.size();
//          for (int ptr = 0; ptr < size; ptr++) {
//              ResolveInfo info = launchPoints.get(ptr);
//
//              if (info.activityInfo != null) {
//                  return getComponentName(info);
//              }
//          }
//      }
        return null;
    }

    private SharedPreferencesUtil prefUtil;

    // TODO relocate this "hiding" code
    private boolean isBlacklisted(String pkgName) {
        if (this.prefUtil == null) {
            this.prefUtil = SharedPreferencesUtil.instance(this.mContext);
        }
        return this.prefUtil.isHidden(pkgName) || this.mUpdatableBlacklist.containsKey(pkgName) || this.mNonUpdatableBlacklist.containsKey(pkgName);
    }
}
