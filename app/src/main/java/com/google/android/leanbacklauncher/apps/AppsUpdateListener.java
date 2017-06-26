package com.google.android.leanbacklauncher.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView.Adapter;
import com.google.android.leanbacklauncher.HomeScreenRow;
import com.google.android.leanbacklauncher.apps.PackageChangedReceiver.Listener;
import com.google.android.leanbacklauncher.notifications.BlacklistListener;
import com.google.android.leanbacklauncher.util.Partner;
import com.google.android.leanbacklauncher.util.Util;
import java.util.ArrayList;

public class AppsUpdateListener implements Listener, InstallingLaunchPointListener, BlacklistListener {
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

    /* renamed from: com.google.android.leanbacklauncher.apps.AppsUpdateListener.1 */
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
        this.mAppsRanker.onAction(packageName, 0);
        this.mLaunchPointGen.addOrUpdatePackage(packageName);
        checkForSearchChanges(packageName);
    }

    public void onPackageChanged(String packageName) {
        Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.addOrUpdatePackage(packageName);
        checkForSearchChanges(packageName);
    }

    public void onPackageFullyRemoved(String packageName) {
        this.mAppsRanker.onAction(packageName, 3);
        Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.removePackage(packageName);
        checkForSearchChanges(packageName);
    }

    public void onPackageRemoved(String packageName) {
        Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.removePackage(packageName);
        if (!(this.mSearchChangeListener == null || packageName == null || !packageName.equalsIgnoreCase(this.mSearchPackageName))) {
            this.mSearchChangeListener.onSearchPackageChanged();
        }
        checkForSearchChanges(packageName);
    }

    public void onPackageReplaced(String packageName) {
        Partner.resetIfNecessary(this.mContext, packageName);
        this.mLaunchPointGen.addOrUpdatePackage(packageName);
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
        this.mAppsRanker.onAction(launchPoint.getPackageName(), 0);
        this.mLaunchPointGen.addOrUpdateInstallingLaunchPoint(launchPoint);
    }

    public void onInstallingLaunchPointChanged(LaunchPoint launchPoint) {
        this.mLaunchPointGen.addOrUpdateInstallingLaunchPoint(launchPoint);
    }

    public void onInstallingLaunchPointRemoved(LaunchPoint launchPoint, boolean success) {
        String pkgName = launchPoint.getPackageName();
        if (!(success || Util.isPackagePresent(this.mContext.getPackageManager(), pkgName))) {
            this.mAppsRanker.onAction(pkgName, 3);
        }
        this.mLaunchPointGen.removeInstallingLaunchPoint(launchPoint, success);
    }

    public void onPackageBlacklisted(String pkgName) {
        this.mLaunchPointGen.addToBlacklist(pkgName);
    }

    public void onPackageUnblacklisted(String pkgName) {
        this.mLaunchPointGen.removeFromBlacklist(pkgName);
    }

    private void registerExternalAppsReceiver() {
        this.mExternalAppsUpdateReceiver = new C01831();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiver(this.mExternalAppsUpdateReceiver, filter);
    }
}
