package com.google.android.leanbacklauncher.apps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.leanbacklauncher.trace.AppTrace;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class LaunchPointListGenerator {
    private static final String[] sSpecialSettingsActions = new String[]{"android.settings.WIFI_SETTINGS"};
    private final List<LaunchPoint> mAllLaunchPoints = new LinkedList();
    private Map<String, Integer> mBlacklist = new ArrayMap();
    private final Queue<CachedAction> mCachedActions = new LinkedList();
    private final Context mContext;
    private boolean mExcludeChannelActivities;
    private final List<LaunchPoint> mInstallingLaunchPoints = new LinkedList();
    private boolean mIsReady = false;
    private final List<Listener> mListeners = new LinkedList();
    private final Object mLock = new Object();
    private ArrayList<LaunchPoint> mSettingsLaunchPoints = new ArrayList();
    private boolean mShouldNotify = false;

    public interface Listener {
        void onLaunchPointListGeneratorReady();

        void onLaunchPointsAddedOrUpdated(ArrayList<LaunchPoint> arrayList);

        void onLaunchPointsRemoved(ArrayList<LaunchPoint> arrayList);

        void onSettingsChanged();
    }

    private class CachedAction {
        private int mAction;
        private LaunchPoint mLaunchPoint;
        private String mPkgName;
        private boolean mSuccess;

        CachedAction(int action, String pkgName) {
            this.mSuccess = false;
            this.mAction = action;
            this.mPkgName = pkgName;
        }

        CachedAction(int action, LaunchPoint launchPoint) {
            this.mSuccess = false;
            this.mAction = action;
            this.mLaunchPoint = launchPoint;
        }

        CachedAction(LaunchPointListGenerator launchPointListGenerator, int action, LaunchPoint launchPoint, boolean success) {
            this(action, launchPoint);
            this.mSuccess = success;
        }

        public void apply() {
            switch (this.mAction) {
                case 0:
                    LaunchPointListGenerator.this.addOrUpdatePackage(this.mPkgName);
                    return;
                case 1:
                    LaunchPointListGenerator.this.removePackage(this.mPkgName);
                    return;
                case 2:
                    LaunchPointListGenerator.this.addToBlacklist(this.mPkgName);
                    return;
                case 3:
                    LaunchPointListGenerator.this.removeFromBlacklist(this.mPkgName);
                    return;
                case 4:
                    LaunchPointListGenerator.this.addOrUpdateInstallingLaunchPoint(this.mLaunchPoint);
                    return;
                case 5:
                    LaunchPointListGenerator.this.removeInstallingLaunchPoint(this.mLaunchPoint, this.mSuccess);
                    return;
                default:
                    return;
            }
        }

        public String toString() {
            String action = null;
            switch (this.mAction) {
                case 0:
                    action = "PKG_ADDED";
                    break;
                case 1:
                    action = "PKG_REMOVED";
                    break;
                case 2:
                    action = "BLACKLIST_ADDED";
                    break;
                case 3:
                    action = "BLACKLIST_REMOVED";
                    break;
                case 4:
                    action = "INSTALL_ADDED";
                    break;
                case 5:
                    action = "INSTALL_REMOVED";
                    break;
            }
            return "CachedAction(" + action + ", " + this.mPkgName + ", " + this.mLaunchPoint + ")";
        }
    }

    private class CreateLaunchPointListTask extends AsyncTask<Void, Void, List<LaunchPoint>> {
        private final boolean mFilterChannelsActivities;

        public CreateLaunchPointListTask(boolean excludeChannelActivities) {
            this.mFilterChannelsActivities = excludeChannelActivities;
        }

        protected List<LaunchPoint> doInBackground(Void... params) {
            AppTrace.beginSection("CreateLaunchPoints");
            try {
                Set<ComponentName> channelActivities = this.mFilterChannelsActivities ? LaunchPointListGenerator.this.getChannelActivities() : null;
                Intent mainIntent = new Intent("android.intent.action.MAIN");
                mainIntent.addCategory("android.intent.category.LEANBACK_LAUNCHER");
                List<LaunchPoint> launcherItems = new LinkedList();
                PackageManager pkgMan = LaunchPointListGenerator.this.mContext.getPackageManager();
                List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
                int size = rawLaunchPoints.size();
                for (int ptr = 0; ptr < size; ptr++) {
                    ResolveInfo info = (ResolveInfo) rawLaunchPoints.get(ptr);
                    ActivityInfo activityInfo = info.activityInfo;
                    if (activityInfo != null) {
                        if (!this.mFilterChannelsActivities) {
                            launcherItems.add(new LaunchPoint(LaunchPointListGenerator.this.mContext, pkgMan, info));
                        } else if (!channelActivities.contains(new ComponentName(activityInfo.packageName, activityInfo.name))) {
                            launcherItems.add(new LaunchPoint(LaunchPointListGenerator.this.mContext, pkgMan, info));
                        }
                    }
                }
                return launcherItems;
            } finally {
                AppTrace.endSection();
            }
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
                for (Listener mListener : LaunchPointListGenerator.this.mListeners) {
                    mListener.onLaunchPointListGeneratorReady();
                }
            }
        }
    }

    public LaunchPointListGenerator(Context ctx) {
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addOrUpdatePackage(java.lang.String r8) {
        /*
        r7 = this;
        r3 = android.text.TextUtils.isEmpty(r8);
        if (r3 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r4 = r7.mCachedActions;
        monitor-enter(r4);
        r3 = r7.mIsReady;	 Catch:{ all -> 0x001b }
        if (r3 != 0) goto L_0x001e;
    L_0x000e:
        r3 = r7.mCachedActions;	 Catch:{ all -> 0x001b }
        r5 = new com.google.android.leanbacklauncher.apps.LaunchPointListGenerator$CachedAction;	 Catch:{ all -> 0x001b }
        r6 = 0;
        r5.<init>(r6, r8);	 Catch:{ all -> 0x001b }
        r3.add(r5);	 Catch:{ all -> 0x001b }
        monitor-exit(r4);	 Catch:{ all -> 0x001b }
        goto L_0x0006;
    L_0x001b:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x001b }
        throw r3;
    L_0x001e:
        monitor-exit(r4);	 Catch:{ all -> 0x001b }
        r4 = r7.mLock;
        monitor-enter(r4);
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x0062 }
        r2.<init>();	 Catch:{ all -> 0x0062 }
        r3 = r7.mInstallingLaunchPoints;	 Catch:{ all -> 0x0062 }
        r5 = 1;
        r7.getLaunchPoints(r3, r2, r8, r5);	 Catch:{ all -> 0x0062 }
        r3 = r7.mAllLaunchPoints;	 Catch:{ all -> 0x0062 }
        r5 = 1;
        r7.getLaunchPoints(r3, r2, r8, r5);	 Catch:{ all -> 0x0062 }
        r1 = r7.createLaunchPoints(r8, r2);	 Catch:{ all -> 0x0062 }
        r3 = r1.isEmpty();	 Catch:{ all -> 0x0062 }
        if (r3 != 0) goto L_0x0065;
    L_0x003d:
        r3 = r7.mAllLaunchPoints;	 Catch:{ all -> 0x0062 }
        r3.addAll(r1);	 Catch:{ all -> 0x0062 }
        r3 = r7.isBlacklisted(r8);	 Catch:{ all -> 0x0062 }
        if (r3 != 0) goto L_0x0065;
    L_0x0048:
        r3 = r7.mShouldNotify;	 Catch:{ all -> 0x0062 }
        if (r3 == 0) goto L_0x0065;
    L_0x004c:
        r3 = r7.mListeners;	 Catch:{ all -> 0x0062 }
        r3 = r3.iterator();	 Catch:{ all -> 0x0062 }
    L_0x0052:
        r5 = r3.hasNext();	 Catch:{ all -> 0x0062 }
        if (r5 == 0) goto L_0x0065;
    L_0x0058:
        r0 = r3.next();	 Catch:{ all -> 0x0062 }
        r0 = (com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener) r0;	 Catch:{ all -> 0x0062 }
        r0.onLaunchPointsAddedOrUpdated(r1);	 Catch:{ all -> 0x0062 }
        goto L_0x0052;
    L_0x0062:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0062 }
        throw r3;
    L_0x0065:
        r3 = r2.isEmpty();	 Catch:{ all -> 0x0062 }
        if (r3 != 0) goto L_0x008b;
    L_0x006b:
        r3 = r7.isBlacklisted(r8);	 Catch:{ all -> 0x0062 }
        if (r3 != 0) goto L_0x008b;
    L_0x0071:
        r3 = r7.mShouldNotify;	 Catch:{ all -> 0x0062 }
        if (r3 == 0) goto L_0x008b;
    L_0x0075:
        r3 = r7.mListeners;	 Catch:{ all -> 0x0062 }
        r3 = r3.iterator();	 Catch:{ all -> 0x0062 }
    L_0x007b:
        r5 = r3.hasNext();	 Catch:{ all -> 0x0062 }
        if (r5 == 0) goto L_0x008b;
    L_0x0081:
        r0 = r3.next();	 Catch:{ all -> 0x0062 }
        r0 = (com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener) r0;	 Catch:{ all -> 0x0062 }
        r0.onLaunchPointsRemoved(r2);	 Catch:{ all -> 0x0062 }
        goto L_0x007b;
    L_0x008b:
        r3 = r7.packageHasSettingsEntry(r8);	 Catch:{ all -> 0x0062 }
        if (r3 == 0) goto L_0x00a7;
    L_0x0091:
        r3 = r7.mListeners;	 Catch:{ all -> 0x0062 }
        r3 = r3.iterator();	 Catch:{ all -> 0x0062 }
    L_0x0097:
        r5 = r3.hasNext();	 Catch:{ all -> 0x0062 }
        if (r5 == 0) goto L_0x00a7;
    L_0x009d:
        r0 = r3.next();	 Catch:{ all -> 0x0062 }
        r0 = (com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener) r0;	 Catch:{ all -> 0x0062 }
        r0.onSettingsChanged();	 Catch:{ all -> 0x0062 }
        goto L_0x0097;
    L_0x00a7:
        monitor-exit(r4);	 Catch:{ all -> 0x0062 }
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.addOrUpdatePackage(java.lang.String):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removePackage(java.lang.String r7) {
        /*
        r6 = this;
        r2 = android.text.TextUtils.isEmpty(r7);
        if (r2 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r3 = r6.mCachedActions;
        monitor-enter(r3);
        r2 = r6.mIsReady;	 Catch:{ all -> 0x001b }
        if (r2 != 0) goto L_0x001e;
    L_0x000e:
        r2 = r6.mCachedActions;	 Catch:{ all -> 0x001b }
        r4 = new com.google.android.leanbacklauncher.apps.LaunchPointListGenerator$CachedAction;	 Catch:{ all -> 0x001b }
        r5 = 1;
        r4.<init>(r5, r7);	 Catch:{ all -> 0x001b }
        r2.add(r4);	 Catch:{ all -> 0x001b }
        monitor-exit(r3);	 Catch:{ all -> 0x001b }
        goto L_0x0006;
    L_0x001b:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x001b }
        throw r2;
    L_0x001e:
        monitor-exit(r3);	 Catch:{ all -> 0x001b }
        r3 = r6.mLock;
        monitor-enter(r3);
        r1 = new java.util.ArrayList;	 Catch:{ all -> 0x0059 }
        r1.<init>();	 Catch:{ all -> 0x0059 }
        r2 = r6.mInstallingLaunchPoints;	 Catch:{ all -> 0x0059 }
        r4 = 1;
        r6.getLaunchPoints(r2, r1, r7, r4);	 Catch:{ all -> 0x0059 }
        r2 = r6.mAllLaunchPoints;	 Catch:{ all -> 0x0059 }
        r4 = 1;
        r6.getLaunchPoints(r2, r1, r7, r4);	 Catch:{ all -> 0x0059 }
        r2 = r1.isEmpty();	 Catch:{ all -> 0x0059 }
        if (r2 != 0) goto L_0x005c;
    L_0x0039:
        r2 = r6.isBlacklisted(r7);	 Catch:{ all -> 0x0059 }
        if (r2 != 0) goto L_0x005c;
    L_0x003f:
        r2 = r6.mShouldNotify;	 Catch:{ all -> 0x0059 }
        if (r2 == 0) goto L_0x005c;
    L_0x0043:
        r2 = r6.mListeners;	 Catch:{ all -> 0x0059 }
        r2 = r2.iterator();	 Catch:{ all -> 0x0059 }
    L_0x0049:
        r4 = r2.hasNext();	 Catch:{ all -> 0x0059 }
        if (r4 == 0) goto L_0x005c;
    L_0x004f:
        r0 = r2.next();	 Catch:{ all -> 0x0059 }
        r0 = (com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener) r0;	 Catch:{ all -> 0x0059 }
        r0.onLaunchPointsRemoved(r1);	 Catch:{ all -> 0x0059 }
        goto L_0x0049;
    L_0x0059:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0059 }
        throw r2;
    L_0x005c:
        r2 = r6.packageHasSettingsEntry(r7);	 Catch:{ all -> 0x0059 }
        if (r2 == 0) goto L_0x0078;
    L_0x0062:
        r2 = r6.mListeners;	 Catch:{ all -> 0x0059 }
        r2 = r2.iterator();	 Catch:{ all -> 0x0059 }
    L_0x0068:
        r4 = r2.hasNext();	 Catch:{ all -> 0x0059 }
        if (r4 == 0) goto L_0x0078;
    L_0x006e:
        r0 = r2.next();	 Catch:{ all -> 0x0059 }
        r0 = (com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener) r0;	 Catch:{ all -> 0x0059 }
        r0.onSettingsChanged();	 Catch:{ all -> 0x0059 }
        goto L_0x0068;
    L_0x0078:
        monitor-exit(r3);	 Catch:{ all -> 0x0059 }
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.removePackage(java.lang.String):void");
    }

    public boolean addToBlacklist(String pkgName) {
        boolean z = false;
        if (!TextUtils.isEmpty(pkgName)) {
            synchronized (this.mCachedActions) {
                if (this.mIsReady) {
                    z = false;
                    synchronized (this.mLock) {
                        Integer occurrences = (Integer) this.mBlacklist.get(pkgName);
                        if (occurrences == null || occurrences.intValue() <= 0) {
                            occurrences = Integer.valueOf(0);
                            z = true;
                            ArrayList<LaunchPoint> blacklistedLaunchPoints = new ArrayList();
                            getLaunchPoints(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                            getLaunchPoints(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                            if (!blacklistedLaunchPoints.isEmpty() && this.mShouldNotify) {
                                for (Listener cl : this.mListeners) {
                                    cl.onLaunchPointsRemoved(blacklistedLaunchPoints);
                                }
                            }
                        }
                        this.mBlacklist.put(pkgName, Integer.valueOf(occurrences.intValue() + 1));
                    }
                } else {
                    this.mCachedActions.add(new CachedAction(2, pkgName));
                }
            }
        }
        return z;
    }

    public boolean removeFromBlacklist(String pkgName) {
        return removeFromBlacklist(pkgName, false);
    }

    private boolean removeFromBlacklist(String pkgName, boolean force) {
        boolean z = false;
        if (!TextUtils.isEmpty(pkgName)) {
            synchronized (this.mCachedActions) {
                if (this.mIsReady) {
                    z = false;
                    synchronized (this.mLock) {
                        Integer occurrences = (Integer) this.mBlacklist.get(pkgName);
                        if (occurrences != null) {
                            occurrences = Integer.valueOf(occurrences.intValue() - 1);
                            if (occurrences.intValue() <= 0 || force) {
                                this.mBlacklist.remove(pkgName);
                                z = true;
                                ArrayList<LaunchPoint> blacklistedLaunchPoints = new ArrayList();
                                getLaunchPoints(this.mInstallingLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                                getLaunchPoints(this.mAllLaunchPoints, blacklistedLaunchPoints, pkgName, false);
                                if (!blacklistedLaunchPoints.isEmpty() && this.mShouldNotify) {
                                    for (Listener cl : this.mListeners) {
                                        cl.onLaunchPointsAddedOrUpdated(blacklistedLaunchPoints);
                                    }
                                }
                            } else {
                                this.mBlacklist.put(pkgName, occurrences);
                            }
                        }
                    }
                } else {
                    this.mCachedActions.add(new CachedAction(3, pkgName));
                }
            }
        }
        return z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addOrUpdateInstallingLaunchPoint(com.google.android.leanbacklauncher.apps.LaunchPoint r9) {
        /*
        r8 = this;
        if (r9 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r5 = r8.mCachedActions;
        monitor-enter(r5);
        r4 = r8.mIsReady;	 Catch:{ all -> 0x0017 }
        if (r4 != 0) goto L_0x001a;
    L_0x000a:
        r4 = r8.mCachedActions;	 Catch:{ all -> 0x0017 }
        r6 = new com.google.android.leanbacklauncher.apps.LaunchPointListGenerator$CachedAction;	 Catch:{ all -> 0x0017 }
        r7 = 4;
        r6.<init>(r7, r9);	 Catch:{ all -> 0x0017 }
        r4.add(r6);	 Catch:{ all -> 0x0017 }
        monitor-exit(r5);	 Catch:{ all -> 0x0017 }
        goto L_0x0002;
    L_0x0017:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0017 }
        throw r4;
    L_0x001a:
        monitor-exit(r5);	 Catch:{ all -> 0x0017 }
        r3 = r9.getPackageName();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r5 = r8.mLock;
        monitor-enter(r5);
        r4 = r8.mInstallingLaunchPoints;	 Catch:{ all -> 0x0074 }
        r6 = 1;
        r8.getLaunchPoints(r4, r2, r3, r6);	 Catch:{ all -> 0x0074 }
        r4 = r8.mAllLaunchPoints;	 Catch:{ all -> 0x0074 }
        r6 = 1;
        r8.getLaunchPoints(r4, r2, r3, r6);	 Catch:{ all -> 0x0074 }
        r1 = 0;
    L_0x0034:
        r4 = r2.size();	 Catch:{ all -> 0x0074 }
        if (r1 >= r4) goto L_0x0046;
    L_0x003a:
        r4 = r2.get(r1);	 Catch:{ all -> 0x0074 }
        r4 = (com.google.android.leanbacklauncher.apps.LaunchPoint) r4;	 Catch:{ all -> 0x0074 }
        r4.setInstallationState(r9);	 Catch:{ all -> 0x0074 }
        r1 = r1 + 1;
        goto L_0x0034;
    L_0x0046:
        r4 = r2.isEmpty();	 Catch:{ all -> 0x0074 }
        if (r4 == 0) goto L_0x004f;
    L_0x004c:
        r2.add(r9);	 Catch:{ all -> 0x0074 }
    L_0x004f:
        r4 = r8.mInstallingLaunchPoints;	 Catch:{ all -> 0x0074 }
        r4.addAll(r2);	 Catch:{ all -> 0x0074 }
        r4 = r8.isBlacklisted(r3);	 Catch:{ all -> 0x0074 }
        if (r4 != 0) goto L_0x0077;
    L_0x005a:
        r4 = r8.mShouldNotify;	 Catch:{ all -> 0x0074 }
        if (r4 == 0) goto L_0x0077;
    L_0x005e:
        r4 = r8.mListeners;	 Catch:{ all -> 0x0074 }
        r4 = r4.iterator();	 Catch:{ all -> 0x0074 }
    L_0x0064:
        r6 = r4.hasNext();	 Catch:{ all -> 0x0074 }
        if (r6 == 0) goto L_0x0077;
    L_0x006a:
        r0 = r4.next();	 Catch:{ all -> 0x0074 }
        r0 = (com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener) r0;	 Catch:{ all -> 0x0074 }
        r0.onLaunchPointsAddedOrUpdated(r2);	 Catch:{ all -> 0x0074 }
        goto L_0x0064;
    L_0x0074:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0074 }
        throw r4;
    L_0x0077:
        monitor-exit(r5);	 Catch:{ all -> 0x0074 }
        goto L_0x0002;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.addOrUpdateInstallingLaunchPoint(com.google.android.leanbacklauncher.apps.LaunchPoint):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeInstallingLaunchPoint(com.google.android.leanbacklauncher.apps.LaunchPoint r5, boolean r6) {
        /*
        r4 = this;
        if (r5 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r1 = r4.mCachedActions;
        monitor-enter(r1);
        r0 = r4.mIsReady;	 Catch:{ all -> 0x0017 }
        if (r0 != 0) goto L_0x001a;
    L_0x000a:
        r0 = r4.mCachedActions;	 Catch:{ all -> 0x0017 }
        r2 = new com.google.android.leanbacklauncher.apps.LaunchPointListGenerator$CachedAction;	 Catch:{ all -> 0x0017 }
        r3 = 5;
        r2.<init>(r4, r3, r5, r6);	 Catch:{ all -> 0x0017 }
        r0.add(r2);	 Catch:{ all -> 0x0017 }
        monitor-exit(r1);	 Catch:{ all -> 0x0017 }
        goto L_0x0002;
    L_0x0017:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0017 }
        throw r0;
    L_0x001a:
        monitor-exit(r1);	 Catch:{ all -> 0x0017 }
        if (r6 != 0) goto L_0x0002;
    L_0x001d:
        r0 = r5.getPackageName();
        r4.addOrUpdatePackage(r0);
        goto L_0x0002;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.removeInstallingLaunchPoint(com.google.android.leanbacklauncher.apps.LaunchPoint, boolean):void");
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
        boolean all = nonGames && games;
        for (LaunchPoint lp : parentList) {
            if (!isBlacklisted(lp.getPackageName()) && (games == lp.isGame() || all)) {
                childList.add(lp);
            }
        }
    }

    public ArrayList<LaunchPoint> getSettingsLaunchPoints(boolean force) {
        if (force || this.mSettingsLaunchPoints.isEmpty()) {
            createSettingsList();
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
        mainIntent.setPackage(pkgName).addCategory("android.intent.category.LEANBACK_LAUNCHER");
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
                        if (reusableLp.matches(info)) {
                        }
                    }
                    launchPoints.add(reusableLp.set(this.mContext, pkgMan, info));
                    reusableItt.remove();
                    rawItt.remove();
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

    private void createSettingsList() {
        AppTrace.beginSection("CreateSettingsList");
        try {
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
            this.mSettingsLaunchPoints.clear();
            this.mSettingsLaunchPoints.addAll(settingsItems);
        } finally {
            AppTrace.endSection();
        }
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
            boolean system;
            if (info.activityInfo == null || (info.activityInfo.applicationInfo.flags & 1) == 0) {
                system = false;
            } else {
                system = true;
            }
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
                boolean system = (info.activityInfo == null || (info.activityInfo.applicationInfo.flags & 1) == 0) ? false : true;
                if (info.activityInfo != null && system) {
                    return getComponentName(info);
                }
            }
        }
        return null;
    }

    private boolean isBlacklisted(String pkgName) {
        return this.mBlacklist.containsKey(pkgName);
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "LaunchPointListGenerator");
        prefix = prefix + "  ";
        writer.println(prefix + "mCachedActions: " + Arrays.toString(this.mCachedActions.toArray()));
        writer.println(prefix + "mAllLaunchPoints: " + Arrays.toString(this.mAllLaunchPoints.toArray()));
        writer.println(prefix + "mInstallingLaunchPoints: " + Arrays.toString(this.mInstallingLaunchPoints.toArray()));
        writer.println(prefix + "mBlacklist: " + this.mBlacklist);
        writer.println(prefix + "mSettingsLaunchPoints: " + Arrays.toString(this.mSettingsLaunchPoints.toArray()));
    }
}
