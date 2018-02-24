package com.google.android.tvlauncher.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.IntentCompat;
import com.google.android.tvlauncher.data.PackagesWithChannelsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.model.ChannelPackage;
import java.util.ArrayList;
import java.util.List;

class AppModel {
    private Context mContext;
    private LoadAppsCallback mLoadAppsCallback;
    @VisibleForTesting(otherwise = 2)
    final PackagesWithChannelsObserver mPackagesObserver = new PackagesWithChannelsObserver() {
        public void onPackagesChange() {
            AppModel.this.onPackagesDataLoaded();
        }
    };
    private TvDataManager mTvDataManager;

    static class AppInfo implements Comparable<AppInfo> {
        Drawable mBanner;
        Drawable mIcon;
        int mNumberOfChannels;
        final String mPackageName;
        CharSequence mTitle;

        AppInfo(String packageName, int channels, ApplicationInfo applicationInfo, PackageManager packageManager) {
            this.mPackageName = packageName;
            this.mNumberOfChannels = channels;
            this.mTitle = packageManager.getApplicationLabel(applicationInfo);
            this.mBanner = AppModel.getApplicationBanner(packageName, packageManager);
            if (this.mBanner == null) {
                try {
                    this.mIcon = packageManager.getApplicationIcon(packageName);
                } catch (NameNotFoundException e) {
                }
            }
        }

        public int compareTo(@NonNull AppInfo o) {
            if (this.mTitle == null) {
                return o.mTitle != null ? 1 : 0;
            } else {
                return this.mTitle.toString().compareToIgnoreCase(o.mTitle.toString());
            }
        }
    }

    interface LoadAppsCallback {
        void onAppsChanged();

        void onAppsLoaded(List<AppInfo> list);
    }

    private static Drawable getApplicationBanner(String packageName, PackageManager pm) {
        Drawable banner = null;
        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.setPackage(packageName).addCategory(IntentCompat.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> infos = pm.queryIntentActivities(mainIntent, 1);
        if (infos != null && infos.size() > 0) {
            ActivityInfo info = ((ResolveInfo) infos.get(0)).activityInfo;
            if (info != null) {
                banner = info.loadBanner(pm);
            }
        }
        if (banner == null) {
            try {
                banner = pm.getApplicationBanner(packageName);
            } catch (NameNotFoundException e) {
            }
        }
        return banner;
    }

    AppModel(Context context) {
        this.mContext = context;
        this.mTvDataManager = TvDataManager.getInstance(context);
        this.mTvDataManager.registerPackagesWithChannelsObserver(this.mPackagesObserver);
    }

    @VisibleForTesting
    AppModel(Context context, TvDataManager tvDataManager) {
        this.mContext = context;
        this.mTvDataManager = tvDataManager;
    }

    void onPause() {
        this.mLoadAppsCallback = null;
        this.mTvDataManager.unregisterPackagesWithChannelsObserver(this.mPackagesObserver);
    }

    void loadApps(LoadAppsCallback callback) {
        this.mLoadAppsCallback = callback;
        this.mTvDataManager.registerPackagesWithChannelsObserver(this.mPackagesObserver);
        if (this.mTvDataManager.isPackagesWithChannelsDataLoaded()) {
            onPackagesDataLoaded();
        } else {
            this.mTvDataManager.loadPackagesWithChannelsData();
        }
    }

    private void onPackagesDataLoaded() {
        if (this.mLoadAppsCallback != null) {
            List<ChannelPackage> channelPackages = this.mTvDataManager.getPackagesWithChannels();
            List<AppInfo> applicationInfos = new ArrayList(channelPackages.size());
            PackageManager packageManager = this.mContext.getPackageManager();
            for (ChannelPackage channelPackage : channelPackages) {
                String packageName = channelPackage.getPackageName();
                ApplicationInfo applicationInfo = getApplicationInfo(packageName, packageManager);
                if (applicationInfo != null) {
                    applicationInfos.add(new AppInfo(packageName, channelPackage.getChannelCount(), applicationInfo, packageManager));
                }
            }
            this.mLoadAppsCallback.onAppsLoaded(applicationInfos);
        }
    }

    private ApplicationInfo getApplicationInfo(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return null;
        }
    }
}
