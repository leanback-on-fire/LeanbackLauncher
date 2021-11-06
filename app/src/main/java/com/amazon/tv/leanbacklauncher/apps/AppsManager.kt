package com.amazon.tv.leanbacklauncher.apps

import android.content.BroadcastReceiver
import android.content.Context
import androidx.preference.PreferenceManager
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory
import com.amazon.tv.leanbacklauncher.HomeScreenRow
import com.amazon.tv.leanbacklauncher.apps.MarketUpdateReceiver.Companion.broadcastPermission
import com.amazon.tv.leanbacklauncher.notifications.BlacklistListener
import com.amazon.tv.leanbacklauncher.util.Partner
import com.amazon.tv.leanbacklauncher.util.Util
import java.io.PrintWriter
import java.util.*

class AppsManager private constructor(private val mContext: Context) :
    InstallingLaunchPointListener, PackageChangedReceiver.Listener, BlacklistListener {
    private val mAppsRanker: AppsRanker = AppsRanker.getInstance(mContext)
    private val mExternalAppsUpdateReceiver: BroadcastReceiver
    private val mLaunchPointList: LaunchPointList = LaunchPointList(mContext)
    private val mMarketUpdateReceiver: MarketUpdateReceiver = MarketUpdateReceiver(this)
    private val mPackageChangedReceiver: PackageChangedReceiver = PackageChangedReceiver(this)
    private var mReceiversRegisteredRefCount = 0
    private val mRows: ArrayList<HomeScreenRow?> = ArrayList<HomeScreenRow?>()
    private var mSearchChangeListener: SearchPackageChangeListener? = null
    private var mSearchPackageName: String? = null

    init {
        mExternalAppsUpdateReceiver = ExternalAppsUpdateReceiver()
    }

    interface SearchPackageChangeListener {
        fun onSearchPackageChanged()
    }

    enum class SortingMode {
        FIXED, RECENCY
    }

    val launchPointComparator: Comparator<LaunchPoint>?
        get() = mAppsRanker.launchPointComparator
    val sortingMode: SortingMode
        get() = mAppsRanker.sortingMode

    fun saveOrderSnapshot(launchPoints: ArrayList<LaunchPoint>?) {
        mAppsRanker.saveOrderSnapshot(launchPoints)
    }

    fun rankLaunchPoints(
        launchPoints: ArrayList<LaunchPoint>?,
        listener: AppsRanker.RankingListener?
    ): Boolean {
        return launchPoints?.let { mAppsRanker.rankLaunchPoints(it, listener) } == true
    }

    fun onAppsRankerAction(packageName: String?, component: String?, actionType: Int) {
        mAppsRanker.onAction(packageName, component, actionType)
    }

    private fun onAppsRankerAction(packageName: String?, actionType: Int) {
        onAppsRankerAction(packageName, null, actionType)
    }

    fun insertLaunchPoint(launchPoints: ArrayList<LaunchPoint>, newLp: LaunchPoint): Int {
        return mAppsRanker.insertLaunchPoint(launchPoints, newLp)
    }

    fun dump(prefix: String, writer: PrintWriter) {
        var prefix = prefix
        writer.println(prefix + "AppsManager")
        prefix = "$prefix  "
        mAppsRanker.dump(prefix, writer)
        // todo this.mLaunchPointListGenerator.dump(prefix, writer);
    }

    private fun unregisterAppsRankerListeners() {
        mAppsRanker.unregisterListeners()
    }

    fun checkIfResortingIsNeeded(): Boolean {
        return mAppsRanker.checkIfResortingIsNeeded()
    }

    val isAppsRankerReady: Boolean
        get() = mAppsRanker.isReady

    fun getLaunchPointsByCategory(other: AppCategory?): List<LaunchPoint> {
        return mLaunchPointList.getLaunchPointsByCategory(other!!)
    }

    val allLaunchPoints: ArrayList<LaunchPoint>
        get() = mLaunchPointList.allLaunchPoints

    fun getSettingsLaunchPoints(force: Boolean): ArrayList<LaunchPoint> {
        return mLaunchPointList.getSettingsLaunchPoints(force)
    }

    fun addOrUpdatePackage(pkgName: String?) {
        mLaunchPointList.addOrUpdatePackage(pkgName)
    }

    private fun removePackage(pkgName: String?) {
        mLaunchPointList.removePackage(pkgName)
    }

    private fun addOrUpdateInstallingLaunchPoint(lp: LaunchPoint?) {
        mLaunchPointList.addOrUpdateInstallingLaunchPoint(lp)
    }

    fun removeInstallingLaunchPoint(lp: LaunchPoint?, success: Boolean) {
        mLaunchPointList.removeInstallingLaunchPoint(lp, success)
    }

    fun addToPartnerExclusionList(pkgName: String?): Boolean {
        return mLaunchPointList.addToBlacklist(pkgName)
    }

    fun removeFromPartnerExclusionList(pkgName: String?): Boolean {
        return mLaunchPointList.removeFromBlacklist(pkgName)
    }

    fun refreshLaunchPointList() {
        mLaunchPointList.refreshLaunchPointList()
    }

    fun setExcludeChannelActivities(excludeChannelActivities: Boolean) {
        mLaunchPointList.setExcludeChannelActivities(excludeChannelActivities)
    }

    fun registerLaunchPointListListener(listener: LaunchPointList.Listener?) {
        mLaunchPointList.registerChangeListener(listener!!)
    }

    override fun onInstallingLaunchPointAdded(launchPoint: LaunchPoint?) {
        onAppsRankerAction(launchPoint!!.packageName, 0)
        addOrUpdateInstallingLaunchPoint(launchPoint)
    }

    override fun onInstallingLaunchPointChanged(launchPoint: LaunchPoint?) {
        addOrUpdateInstallingLaunchPoint(launchPoint)
    }

    override fun onInstallingLaunchPointRemoved(launchPoint: LaunchPoint?, success: Boolean) {
        val pkgName = launchPoint!!.packageName
        if (!(success || Util.isPackagePresent(mContext.packageManager, pkgName))) {
            onAppsRankerAction(pkgName, 3)
        }
        removeInstallingLaunchPoint(launchPoint, success)
    }

    override fun onPackageAdded(packageName: String?) {
        onAppsRankerAction(packageName, 0)
        addOrUpdatePackage(packageName)
        checkForSearchChanges(packageName)
    }

    override fun onPackageChanged(packageName: String?) {
        Partner.resetIfNecessary(mContext, packageName)
        addOrUpdatePackage(packageName)
        checkForSearchChanges(packageName)
    }

    override fun onPackageFullyRemoved(packageName: String?) {
        onAppsRankerAction(packageName, 3)
        Partner.resetIfNecessary(mContext, packageName)
        removePackage(packageName)
        checkForSearchChanges(packageName)
    }

    override fun onPackageRemoved(packageName: String?) {
        Partner.resetIfNecessary(mContext, packageName)
        removePackage(packageName)
        if (!(mSearchChangeListener == null || packageName == null || !packageName.equals(
                mSearchPackageName,
                ignoreCase = true
            ))
        ) {
            mSearchChangeListener!!.onSearchPackageChanged()
        }
        checkForSearchChanges(packageName)
    }

    override fun onPackageReplaced(packageName: String?) {
        Partner.resetIfNecessary(mContext, packageName)
        addOrUpdatePackage(packageName)
    }

    override fun onPackageBlacklisted(pkgName: String?) {
        addToPartnerExclusionList(pkgName)
    }

    override fun onPackageUnblacklisted(pkgName: String?) {
        removeFromPartnerExclusionList(pkgName)
    }

    private fun checkForSearchChanges(packageName: String?) {
        if (mSearchChangeListener != null && packageName != null && packageName.equals(
                mSearchPackageName,
                ignoreCase = true
            )
        ) {
            mSearchChangeListener!!.onSearchPackageChanged()
        }
    }

    fun setSearchPackageChangeListener(
        listener: SearchPackageChangeListener?,
        searchPackageName: String?
    ) {
        mSearchChangeListener = listener
        mSearchPackageName = if (mSearchChangeListener != null) {
            searchPackageName
        } else {
            null
        }
    }

    fun addAppRow(row: HomeScreenRow?) {
        mRows.add(row)
        refreshRow(row)
    }

    fun refreshRows() {
        for (i in mRows.indices) {
            refreshRow(mRows[i])
        }
    }

    private fun refreshRow(row: HomeScreenRow?) {
        val adapter = row!!.adapter
        if (adapter is AppsAdapter) {
            adapter.refreshDataSetAsync()
        }
    }

    fun registerUpdateReceivers() {
        val i = mReceiversRegisteredRefCount
        mReceiversRegisteredRefCount = i + 1
        if (i == 0) {
            mContext.registerReceiver(mPackageChangedReceiver, PackageChangedReceiver.intentFilter)
            mContext.registerReceiver(
                mMarketUpdateReceiver,
                MarketUpdateReceiver.intentFilter,
                broadcastPermission,
                null
            )
            mContext.registerReceiver(
                mExternalAppsUpdateReceiver,
                ExternalAppsUpdateReceiver.intentFilter
            )
        }
    }

    fun unregisterUpdateReceivers() {
        val i = mReceiversRegisteredRefCount - 1
        mReceiversRegisteredRefCount = i
        if (i == 0) {
            mContext.unregisterReceiver(mMarketUpdateReceiver)
            mContext.unregisterReceiver(mPackageChangedReceiver)
            mContext.unregisterReceiver(mExternalAppsUpdateReceiver)
        }
    }

    fun onDestroy() {
        unregisterAppsRankerListeners()
        mRows.clear()
        sAppsManager = null
    }

    val isLaunchPointListGeneratorReady: Boolean
        get() {
            return this.mLaunchPointList.isReady
        }

    companion object {
        private var sAppsManager: AppsManager? = null

        @JvmStatic
        fun getInstance(context: Context): AppsManager? {
            if (sAppsManager == null) {
                sAppsManager = AppsManager(context.applicationContext)
            }
            return sAppsManager
        }

        @JvmStatic
        fun getSavedSortingMode(context: Context): SortingMode {
            return SortingMode.valueOf(
                PreferenceManager.getDefaultSharedPreferences(context).getString(
                    "apps_ranker_sorting_mode",
                    Partner.get(context).appSortingMode.toString(),
                ) ?: "FIXED"
            )
        }

        @JvmStatic
        fun saveSortingMode(context: Context, mode: SortingMode) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("apps_ranker_sorting_mode", mode.toString()).apply()
        }
    }
}