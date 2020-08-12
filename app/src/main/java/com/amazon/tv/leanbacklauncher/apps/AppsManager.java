package com.amazon.tv.leanbacklauncher.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.preference.PreferenceManager;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory;
import com.amazon.tv.leanbacklauncher.HomeScreenRow;
import com.amazon.tv.leanbacklauncher.apps.PackageChangedReceiver.Listener;
import com.amazon.tv.leanbacklauncher.notifications.BlacklistListener;
import com.amazon.tv.leanbacklauncher.util.Partner;
import com.amazon.tv.leanbacklauncher.util.Util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppsManager implements InstallingLaunchPointListener, Listener, BlacklistListener {
    private static AppsManager sAppsManager;
    private AppsRanker mAppsRanker;
    private final Context mContext;
    private final BroadcastReceiver mExternalAppsUpdateReceiver;
    private LaunchPointListGenerator mLaunchPointListGenerator;
    private final MarketUpdateReceiver mMarketUpdateReceiver;
    private final PackageChangedReceiver mPackageChangedReceiver;
    private int mReceiversRegisteredRefCount;
    private ArrayList<HomeScreenRow> mRows = new ArrayList();
    private SearchPackageChangeListener mSearchChangeListener;
    private String mSearchPackageName;


    public interface SearchPackageChangeListener {
        void onSearchPackageChanged();
    }

    public enum SortingMode {
        FIXED,
        RECENCY
    }

    private AppsManager(Context context) {
        this.mContext = context;
        this.mLaunchPointListGenerator = new LaunchPointListGenerator(this.mContext);
        this.mAppsRanker = AppsRanker.getInstance(this.mContext);
        this.mMarketUpdateReceiver = new MarketUpdateReceiver(this);
        this.mPackageChangedReceiver = new PackageChangedReceiver(this);
        this.mExternalAppsUpdateReceiver = new ExternalAppsUpdateReceiver();
    }

    public static AppsManager getInstance(Context context) {
        if (sAppsManager == null) {
            sAppsManager = new AppsManager(context.getApplicationContext());
        }
        return sAppsManager;
    }

    public static SortingMode getSavedSortingMode(Context context) {
        return SortingMode.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("apps_ranker_sorting_mode", Partner.get(context).getAppSortingMode().toString()));
    }

    public static void saveSortingMode(Context context, SortingMode mode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("apps_ranker_sorting_mode", mode.toString()).apply();
    }

    public Comparator<LaunchPoint> getLaunchPointComparator() {
        return this.mAppsRanker.getLaunchPointComparator();
    }

    public SortingMode getSortingMode() {
        return this.mAppsRanker.getSortingMode();
    }

    public void saveOrderSnapshot(ArrayList<LaunchPoint> launchPoints) {
        this.mAppsRanker.saveOrderSnapshot(launchPoints);
    }

    public boolean rankLaunchPoints(ArrayList<LaunchPoint> launchPoints, AppsRanker.RankingListener listener) {
        return this.mAppsRanker.rankLaunchPoints(launchPoints, listener);
    }

    public void onAppsRankerAction(String packageName, String component, int actionType) {
        this.mAppsRanker.onAction(packageName, component, actionType);
    }

    public void onAppsRankerAction(String packageName, int actionType) {
        onAppsRankerAction(packageName, null, actionType);
    }

    public int insertLaunchPoint(ArrayList<LaunchPoint> launchPoints, LaunchPoint newLp) {
        return this.mAppsRanker.insertLaunchPoint(launchPoints, newLp);
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "AppsManager");
        prefix = prefix + "  ";
        this.mAppsRanker.dump(prefix, writer);
        // todo this.mLaunchPointListGenerator.dump(prefix, writer);
    }

    public void unregisterAppsRankerListeners() {
        this.mAppsRanker.unregisterListeners();
    }

    public boolean checkIfResortingIsNeeded() {
        return this.mAppsRanker.checkIfResortingIsNeeded();
    }

    public boolean isAppsRankerReady() {
        return this.mAppsRanker.isReady();
    }

    public List<LaunchPoint> getLaunchPointsByCategory(AppCategory other) {
        return this.mLaunchPointListGenerator.getLaunchPointsByCategory(other);
    }

    public ArrayList<LaunchPoint> getAllLaunchPoints() {
        return this.mLaunchPointListGenerator.getAllLaunchPoints();
    }

    public ArrayList<LaunchPoint> getSettingsLaunchPoints(boolean force) {
        return this.mLaunchPointListGenerator.getSettingsLaunchPoints(force);
    }

    public void addOrUpdatePackage(String pkgName) {
        this.mLaunchPointListGenerator.addOrUpdatePackage(pkgName);
    }

    public void removePackage(String pkgName) {
        this.mLaunchPointListGenerator.removePackage(pkgName);
    }

    public void addOrUpdateInstallingLaunchPoint(LaunchPoint lp) {
        this.mLaunchPointListGenerator.addOrUpdateInstallingLaunchPoint(lp);
    }

    public void removeInstallingLaunchPoint(LaunchPoint lp, boolean success) {
        this.mLaunchPointListGenerator.removeInstallingLaunchPoint(lp, success);
    }

    public boolean addToPartnerExclusionList(String pkgName) {
        return this.mLaunchPointListGenerator.addToBlacklist(pkgName);
    }

    public boolean removeFromPartnerExclusionList(String pkgName) {
        return this.mLaunchPointListGenerator.removeFromBlacklist(pkgName);
    }

    public void refreshLaunchPointList() {
        this.mLaunchPointListGenerator.refreshLaunchPointList();
    }

    public void setExcludeChannelActivities(boolean excludeChannelActivities) {
        this.mLaunchPointListGenerator.setExcludeChannelActivities(excludeChannelActivities);
    }

    public void registerLaunchPointListGeneratorListener(LaunchPointListGenerator.Listener listener) {
        this.mLaunchPointListGenerator.registerChangeListener(listener);
    }

    public boolean isLaunchPointListGeneratorReady() {
        return this.mLaunchPointListGenerator.isReady();
    }

    public void onInstallingLaunchPointAdded(LaunchPoint launchPoint) {
        onAppsRankerAction(launchPoint.getPackageName(), 0);
        addOrUpdateInstallingLaunchPoint(launchPoint);
    }

    public void onInstallingLaunchPointChanged(LaunchPoint launchPoint) {
        addOrUpdateInstallingLaunchPoint(launchPoint);
    }

    public void onInstallingLaunchPointRemoved(LaunchPoint launchPoint, boolean success) {
        String pkgName = launchPoint.getPackageName();
        if (!(success || Util.isPackagePresent(this.mContext.getPackageManager(), pkgName))) {
            onAppsRankerAction(pkgName, 3);
        }
        removeInstallingLaunchPoint(launchPoint, success);
    }

    public void onPackageAdded(String packageName) {
        onAppsRankerAction(packageName, 0);
        addOrUpdatePackage(packageName);
        checkForSearchChanges(packageName);
    }

    public void onPackageChanged(String packageName) {
        Partner.resetIfNecessary(this.mContext, packageName);
        addOrUpdatePackage(packageName);
        checkForSearchChanges(packageName);
    }

    public void onPackageFullyRemoved(String packageName) {
        onAppsRankerAction(packageName, 3);
        Partner.resetIfNecessary(this.mContext, packageName);
        removePackage(packageName);
        checkForSearchChanges(packageName);
    }

    public void onPackageRemoved(String packageName) {
        Partner.resetIfNecessary(this.mContext, packageName);
        removePackage(packageName);
        if (!(this.mSearchChangeListener == null || packageName == null || !packageName.equalsIgnoreCase(this.mSearchPackageName))) {
            this.mSearchChangeListener.onSearchPackageChanged();
        }
        checkForSearchChanges(packageName);
    }

    public void onPackageReplaced(String packageName) {
        Partner.resetIfNecessary(this.mContext, packageName);
        addOrUpdatePackage(packageName);
    }

    public void onPackageBlacklisted(String pkgName) {
        addToPartnerExclusionList(pkgName);
    }

    public void onPackageUnblacklisted(String pkgName) {
        removeFromPartnerExclusionList(pkgName);
    }

    private void checkForSearchChanges(String packageName) {
        if (this.mSearchChangeListener != null && packageName != null && packageName.equalsIgnoreCase(this.mSearchPackageName)) {
            this.mSearchChangeListener.onSearchPackageChanged();
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

    public void addAppRow(HomeScreenRow row) {
        this.mRows.add(row);
        refreshRow(row);
    }

    public void refreshRows() {
        for (int i = 0; i < this.mRows.size(); i++) {
            refreshRow(this.mRows.get(i));
        }
    }

    private void refreshRow(HomeScreenRow row) {
        Adapter<?> adapter = row.getAdapter();
        if (adapter instanceof AppsAdapter) {
            ((AppsAdapter) adapter).refreshDataSetAsync();
        }
    }

    public void registerUpdateReceivers() {
        int i = this.mReceiversRegisteredRefCount;
        this.mReceiversRegisteredRefCount = i + 1;
        if (i == 0) {
            this.mContext.registerReceiver(this.mPackageChangedReceiver, PackageChangedReceiver.getIntentFilter());
            this.mContext.registerReceiver(this.mMarketUpdateReceiver, MarketUpdateReceiver.getIntentFilter(), MarketUpdateReceiver.getBroadcastPermission(), null);
            this.mContext.registerReceiver(this.mExternalAppsUpdateReceiver, ExternalAppsUpdateReceiver.getIntentFilter());
        }
    }

    public void unregisterUpdateReceivers() {
        int i = this.mReceiversRegisteredRefCount - 1;
        this.mReceiversRegisteredRefCount = i;
        if (i == 0) {
            this.mContext.unregisterReceiver(this.mMarketUpdateReceiver);
            this.mContext.unregisterReceiver(this.mPackageChangedReceiver);
            this.mContext.unregisterReceiver(this.mExternalAppsUpdateReceiver);
        }
    }

    public void onDestroy() {
        unregisterAppsRankerListeners();
        this.mRows.clear();
        sAppsManager = null;
    }
}
