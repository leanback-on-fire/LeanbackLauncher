package com.rockchips.android.leanbacklauncher.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;

import com.rockchips.android.leanbacklauncher.HomeScreenRow;
import com.rockchips.android.leanbacklauncher.notifications.BlacklistListener;

import java.util.ArrayList;

public class AppsUpdateListener implements PackageChangedReceiver.Listener, InstallingLaunchPointListener, BlacklistListener {
    private static final String TAG = "AppsUpdateListener";
    private final AppsRanker mAppsRanker;
    public Context mContext;
    private BroadcastReceiver mExternalAppsUpdateReceiver;
    private final LaunchPointListGenerator mLaunchPointGen;
    private final MarketUpdateReceiver mMarketUpdateReceiver;
    private final PackageChangedReceiver mPackageChangedReceiver;
    protected ArrayList<HomeScreenRow> mRows;
    private SearchPackageChangeListener mSearchChangeListener;
    private String mSearchPackageName;

    public interface SearchPackageChangeListener {
        void onSearchPackageChanged();
    }

    /* renamed from: AppsUpdateListener.1 */
    class C01831 extends BroadcastReceiver {
        C01831() {
        }

        public void onReceive(Context context, Intent intent) {
            AppsUpdateListener.this.onExternalPackagesStatusChanged();
        }
    }

    public AppsUpdateListener(Context context, LaunchPointListGenerator launchPointListGenerator, AppsRanker appsRanker) {
        this.mRows = new ArrayList();
        this.mContext = context;
        this.mAppsRanker = appsRanker;
        this.mLaunchPointGen = launchPointListGenerator;
        this.mMarketUpdateReceiver = new MarketUpdateReceiver(this);
        this.mPackageChangedReceiver = new PackageChangedReceiver(this);
        this.mContext.registerReceiver(this.mPackageChangedReceiver, PackageChangedReceiver.getIntentFilter());
        this.mContext.registerReceiver(this.mMarketUpdateReceiver, MarketUpdateReceiver.getIntentFilter(), MarketUpdateReceiver.getBroadcastPermission(), null);
        registerExternalAppsReceiver();
    }

    public void addAppRow(HomeScreenRow row) {
        this.mRows.add(row);
        refreshRow(row);
    }

    public void unregisterReceivers() {
        this.mContext.unregisterReceiver(this.mMarketUpdateReceiver);
        this.mContext.unregisterReceiver(this.mPackageChangedReceiver);
        if (this.mExternalAppsUpdateReceiver != null) {
            this.mContext.unregisterReceiver(this.mExternalAppsUpdateReceiver);
        }
    }

    private void refreshRow(HomeScreenRow row) {
        Adapter<?> adapter = row.getAdapter();
        if (adapter instanceof AppsAdapter) {
            ((AppsAdapter) adapter).refreshDataSetAsync();
        }
    }

    public void refreshRows() {
        for (int i = 0; i < this.mRows.size(); i++) {
            refreshRow((HomeScreenRow) this.mRows.get(i));
        }
    }

    public void setSearchPackageChangeListener(SearchPackageChangeListener listener, String searchPackageName) {
        this.mSearchChangeListener = listener;
        if (this.mSearchChangeListener != null) {
            this.mSearchPackageName = searchPackageName;
        } else {
            this.mSearchPackageName = null;
        }
    }

    public void onPackageAdded(String packageName) {
        Log.i(TAG, "onPackageAdded->packageName:" + packageName);
        /*this.mAppsRanker.onAction(packageName, 0);
        this.mLaunchPointGen.addOrUpdatePackage(packageName);
        checkForSearchChanges(packageName);*/
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onPackageChanged(String packageName) {
        Log.i(TAG, "onPackageChanged->packageName:" + packageName);
       /* Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.addOrUpdatePackage(packageName);
        checkForSearchChanges(packageName);*/
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onPackageFullyRemoved(String packageName) {
        Log.i(TAG, "onPackageFullyRemoved->packageName:" + packageName);
      /*  this.mAppsRanker.onAction(packageName, 3);
        Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.removePackage(packageName);*/
        //checkForSearchChanges(packageName);
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onPackageRemoved(String packageName) {
        Log.i(TAG, "onPackageRemoved->packageName:" + packageName);
      /*  Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.removePackage(packageName);
        if (!(this.mSearchChangeListener == null || packageName == null || !packageName.equalsIgnoreCase(this.mSearchPackageName))) {
            this.mSearchChangeListener.onSearchPackageChanged();
        }
        checkForSearchChanges(packageName);*/
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onPackageReplaced(String packageName) {
        Log.i(TAG, "onPackageReplaced->packageName:" + packageName);
        /*Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.addOrUpdatePackage(packageName);*/
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    private void checkForSearchChanges(String packageName) {
        if (this.mSearchChangeListener != null && packageName != null && packageName.equalsIgnoreCase(this.mSearchPackageName)) {
            this.mSearchChangeListener.onSearchPackageChanged();
        }
    }

    private void onExternalPackagesStatusChanged() {
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onInstallingLaunchPointAdded(LaunchPoint launchPoint) {
        this.mLaunchPointGen.refreshLaunchPointList();
      /*  this.mAppsRanker.onAction(launchPoint.getPackageName(), 0);
        this.mLaunchPointGen.addOrUpdateInstallingLaunchPoint(launchPoint);*/
    }

    public void onInstallingLaunchPointChanged(LaunchPoint launchPoint) {
        /*this.mLaunchPointGen.addOrUpdateInstallingLaunchPoint(launchPoint);*/
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onInstallingLaunchPointRemoved(LaunchPoint launchPoint, boolean success) {
      /*  String pkgName = launchPoint.getPackageName();
        if (!(success || Util.isPackagePresent(this.mContext.getPackageManager(), pkgName))) {
            this.mAppsRanker.onAction(pkgName, 3);
        }
        this.mLaunchPointGen.removeInstallingLaunchPoint(launchPoint, success);*/
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onPackageBlacklisted(String pkgName) {
        //this.mLaunchPointGen.addToBlacklist(pkgName);
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    public void onPackageUnblacklisted(String pkgName) {
        //this.mLaunchPointGen.removeFromBlacklist(pkgName);
        this.mLaunchPointGen.refreshLaunchPointList();
    }

    private void registerExternalAppsReceiver() {
        this.mExternalAppsUpdateReceiver = new C01831();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiver(this.mExternalAppsUpdateReceiver, filter);
    }
}
