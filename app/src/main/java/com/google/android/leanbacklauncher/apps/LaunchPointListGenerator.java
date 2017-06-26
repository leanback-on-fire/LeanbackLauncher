package com.google.android.leanbacklauncher.apps;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class LaunchPointListGenerator {
    private static final String TAG = "LaunchPointList";
    private static final String[] sSpecialSettingsActions;
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
            Set wrap0 = this.mFilterChannelsActivities ? LaunchPointListGenerator.this.getChannelActivities() : null;
            Intent mainIntent = new Intent("android.intent.action.MAIN");
            mainIntent.addCategory("android.intent.category.LAUNCHER");
            List<LaunchPoint> launcherItems = new LinkedList();
            PackageManager pkgMan = LaunchPointListGenerator.this.mContext.getPackageManager();
            List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
            int size = rawLaunchPoints.size();
            for (int ptr = 0; ptr < size; ptr++) {
                ResolveInfo info = (ResolveInfo) rawLaunchPoints.get(ptr);
                ActivityInfo activityInfo = info.activityInfo;
                if (activityInfo != null) {
                    if(activityInfo.packageName.equals("com.android.tv.settings"))
                        continue;
                    if (!this.mFilterChannelsActivities) {
                        launcherItems.add(new LaunchPoint(LaunchPointListGenerator.this.mContext, pkgMan, info));
                    } else if (wrap0 != null && !wrap0.contains(new ComponentName(activityInfo.packageName, activityInfo.name))) {
                        launcherItems.add(new LaunchPoint(LaunchPointListGenerator.this.mContext, pkgMan, info));
                    }
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
                LaunchPointListGenerator.this.mIsReady = true;
                while (!LaunchPointListGenerator.this.mCachedActions.isEmpty()) {
                    ((CachedAction) LaunchPointListGenerator.this.mCachedActions.remove()).apply();
                }
                LaunchPointListGenerator.this.mShouldNotify = true;
                for (Listener onLaunchPointListGeneratorReady : LaunchPointListGenerator.this.mListeners) {
                    onLaunchPointListGeneratorReady.onLaunchPointListGeneratorReady();
                }
            }
        }
    }

    static {
        sSpecialSettingsActions = new String[]{"android.settings.WIFI_SETTINGS"};
    }

    public LaunchPointListGenerator(Context ctx) {
        this.mIsReady = false;
        this.mShouldNotify = false;
        this.mCachedActions = new LinkedList();
        this.mListeners = new LinkedList();
        this.mAllLaunchPoints = new LinkedList();
        this.mInstallingLaunchPoints = new LinkedList();
        this.mUpdatableBlacklist = new HashMap();
        this.mNonUpdatableBlacklist = new HashMap();
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
                        ArrayList<LaunchPoint> removedLaunchPoints = new ArrayList();
                        getLaunchPoints(this.mInstallingLaunchPoints, removedLaunchPoints, pkgName, true);
                        getLaunchPoints(this.mAllLaunchPoints, removedLaunchPoints, pkgName, true);
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
                        ArrayList<LaunchPoint> removedLaunchPoints = new ArrayList();
                        getLaunchPoints(this.mInstallingLaunchPoints, removedLaunchPoints, pkgName, true);
                        getLaunchPoints(this.mAllLaunchPoints, removedLaunchPoints, pkgName, true);
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
                    Integer occurrences = (Integer) blacklist.get(pkgName);
                    Integer otherOccurrences = (Integer) (updatable ? this.mNonUpdatableBlacklist : this.mUpdatableBlacklist).get(pkgName);
                    if (occurrences == null || occurrences.intValue() <= 0) {
                        occurrences = Integer.valueOf(0);
                        if (otherOccurrences == null || otherOccurrences.intValue() <= 0) {
                            added = true;
                            ArrayList<LaunchPoint> blacklistedLaunchPoints = new ArrayList();
                            getLaunchPoints(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                            getLaunchPoints(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                            if (!blacklistedLaunchPoints.isEmpty() && this.mShouldNotify) {
                                for (Listener cl : this.mListeners) {
                                    cl.onLaunchPointsRemoved(blacklistedLaunchPoints);
                                }
                            }
                        }
                    }
                    int intValue = occurrences.intValue() + 1;
                    occurrences = Integer.valueOf(intValue);
                    blacklist.put(pkgName, Integer.valueOf(intValue));
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
                    Integer occurrences = (Integer) blacklist.get(pkgName);
                    Integer otherOccurrences = (Integer) (updatable ? this.mNonUpdatableBlacklist : this.mUpdatableBlacklist).get(pkgName);
                    if (occurrences != null) {
                        occurrences = Integer.valueOf(occurrences.intValue() - 1);
                        if (occurrences.intValue() <= 0 || force) {
                            blacklist.remove(pkgName);
                            if (otherOccurrences == null) {
                                removed = true;
                                ArrayList<LaunchPoint> blacklistedLaunchPoints = new ArrayList();
                                getLaunchPoints(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                                getLaunchPoints(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
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
                    ArrayList<LaunchPoint> launchPoints = new ArrayList();
                    synchronized (this.mLock) {
                        getLaunchPoints(this.mInstallingLaunchPoints, launchPoints, pkgName, true);
                        getLaunchPoints(this.mAllLaunchPoints, launchPoints, pkgName, true);
                        for (int i = 0; i < launchPoints.size(); i++) {
                            ((LaunchPoint) launchPoints.get(i)).setInstallationState(launchPoint);
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

    private ArrayList<LaunchPoint> getLaunchPoints(List<LaunchPoint> parentList, ArrayList<LaunchPoint> found, String pkgName, boolean remove) {
        if (found == null) {
            found = new ArrayList();
        }
        Iterator<LaunchPoint> itt = parentList.iterator();
        while (itt.hasNext()) {
            LaunchPoint lp = (LaunchPoint) itt.next();
            if (TextUtils.equals(pkgName, lp.getPackageName())) {
                found.add(lp);
                if (remove) {
                    itt.remove();
                }
            }
        }
        return found;
    }

    public ArrayList<LaunchPoint> getGameLaunchPoints() {
        return getLaunchPoints(false, true);
    }

    public ArrayList<LaunchPoint> getNonGameLaunchPoints() {
        return getLaunchPoints(true, false);
    }

    public ArrayList<LaunchPoint> getAllLaunchPoints() {
        return getLaunchPoints(true, true);
    }

    private ArrayList<LaunchPoint> getLaunchPoints(boolean nonGames, boolean games) {
        ArrayList<LaunchPoint> launchPoints = new ArrayList();
        synchronized (this.mLock) {
            getLaunchPointsLocked(this.mInstallingLaunchPoints, launchPoints, nonGames, games);
            getLaunchPointsLocked(this.mAllLaunchPoints, launchPoints, nonGames, games);
        }
        return launchPoints;
    }

    private void getLaunchPointsLocked(List<LaunchPoint> parentList, List<LaunchPoint> childList, boolean nonGames, boolean games) {
        boolean z = nonGames ? games : false;
        for (LaunchPoint lp : parentList) {
            if (!isBlacklisted(lp.getPackageName()) && (games == lp.isGame() || z)) {
                childList.add(lp);
            }
        }
    }

    public ArrayList<LaunchPoint> getSettingsLaunchPoints(boolean force) {
        if (force || this.mSettingsLaunchPoints == null) {
            this.mSettingsLaunchPoints = createSettingsList();
        }
        return (ArrayList) this.mSettingsLaunchPoints.clone();
    }

    public void refreshLaunchPointList() {
        synchronized (this.mCachedActions) {
            this.mIsReady = false;
            this.mShouldNotify = false;
        }
        new CreateLaunchPointListTask(this.mExcludeChannelActivities).execute(new Void[0]);
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
        Iterator<LaunchPoint> reusableItt;
        if (reusable == null) {
            reusable = new ArrayList();
        }
        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.setPackage(pkgName).addCategory("android.intent.category.LAUNCHER");
        ArrayList<LaunchPoint> launchPoints = new ArrayList();
        PackageManager pkgMan = this.mContext.getPackageManager();
        List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
        if (this.mExcludeChannelActivities) {
            Set<ComponentName> channelActivities = getChannelActivities();
            rawItt = rawLaunchPoints.iterator();
            while (rawItt.hasNext()) {
                ActivityInfo activityInfo = ((ResolveInfo) rawItt.next()).activityInfo;
                if (channelActivities.contains(new ComponentName(activityInfo.packageName, activityInfo.name))) {
                    rawItt.remove();
                }
            }
        }
        rawItt = rawLaunchPoints.iterator();
        while (rawItt.hasNext()) {
            ResolveInfo info = (ResolveInfo) rawItt.next();
            if (info.activityInfo != null) {
                reusableItt = reusable.iterator();
                while (reusableItt.hasNext()) {
                    LaunchPoint reusableLp = (LaunchPoint) reusableItt.next();
                    if (!reusableLp.isInitialInstall()) {
                        if (reusableLp.equals(info)) {
                        }
                    }
                    launchPoints.add(reusableLp.set(this.mContext, pkgMan, info));
                    reusableItt.remove();
                    rawItt.remove();
                    break;
                }
            }
        }
        rawItt = rawLaunchPoints.iterator();
        reusableItt = reusable.iterator();
        while (rawItt.hasNext() && reusableItt.hasNext()) {
            launchPoints.add(((LaunchPoint) reusableItt.next()).set(this.mContext, pkgMan, (ResolveInfo) rawItt.next()));
            reusableItt.remove();
        }
        while (rawItt.hasNext()) {
            launchPoints.add(new LaunchPoint(this.mContext, pkgMan, (ResolveInfo) rawItt.next()));
        }
        return launchPoints;
    }

    private Set<ComponentName> getChannelActivities() {
        HashSet<ComponentName> channelActivities = new HashSet();
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
        ArrayList<LaunchPoint> settingsItems = new ArrayList();
        PackageManager pkgMan = this.mContext.getPackageManager();
        List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
        HashMap<ComponentName, Integer> specialEntries = new HashMap();
        for (int i = 0; i < sSpecialSettingsActions.length; i++) {
            specialEntries.put(getComponentNameForSettingsActivity(sSpecialSettingsActions[i]), Integer.valueOf(i));
        }
        int size = rawLaunchPoints.size();
        for (int ptr = 0; ptr < size; ptr++) {
            ResolveInfo info = (ResolveInfo) rawLaunchPoints.get(ptr);
            boolean system = (info.activityInfo.applicationInfo.flags & 1) != 0;
            ComponentName comp = getComponentName(info);
            int type = -1;
            if (specialEntries.containsKey(comp)) {
                type = ((Integer) specialEntries.get(comp)).intValue();
            }
            if (info.activityInfo != null && system) {
                LaunchPoint lp = new LaunchPoint(this.mContext, pkgMan, info, false, type);
                lp.addLaunchIntentFlags(32768);
                settingsItems.add(lp);
            }
        }
        return settingsItems;
    }

    public boolean packageHasSettingsEntry(String packageName) {
        if (this.mSettingsLaunchPoints != null) {
            for (int i = 0; i < this.mSettingsLaunchPoints.size(); i++) {
                if (TextUtils.equals(((LaunchPoint) this.mSettingsLaunchPoints.get(i)).getPackageName(), packageName)) {
                    return true;
                }
            }
        }
        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.addCategory("android.intent.category.LEANBACK_SETTINGS");
        List<ResolveInfo> rawLaunchPoints = this.mContext.getPackageManager().queryIntentActivities(mainIntent, 129);
        int size = rawLaunchPoints.size();
        for (int ptr = 0; ptr < size; ptr++) {
            ResolveInfo info = (ResolveInfo) rawLaunchPoints.get(ptr);
            boolean system = (info.activityInfo.applicationInfo.flags & 1) != 0;
            if (info.activityInfo != null && system && TextUtils.equals(info.activityInfo.applicationInfo.packageName, packageName)) {
                return true;
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
        mainIntent.addCategory("android.intent.category.LEANBACK_SETTINGS");
        List<ResolveInfo> launchPoints = this.mContext.getPackageManager().queryIntentActivities(mainIntent, 129);
        if (launchPoints.size() > 0) {
            int size = launchPoints.size();
            for (int ptr = 0; ptr < size; ptr++) {
                ResolveInfo info = (ResolveInfo) launchPoints.get(ptr);
                boolean system = (info.activityInfo.applicationInfo.flags & 1) != 0;
                if (info.activityInfo != null && system) {
                    return getComponentName(info);
                }
            }
        }
        return null;
    }

    private boolean isBlacklisted(String pkgName) {
        if (this.mUpdatableBlacklist.containsKey(pkgName)) {
            return true;
        }
        return this.mNonUpdatableBlacklist.containsKey(pkgName);
    }
}
