package com.amazon.tv.leanbacklauncher.apps

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getAppsMax
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil.Companion.instance
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.EditableAppsRowView
import com.amazon.tv.leanbacklauncher.LauncherViewHolder
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter.AppViewHolder
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.getInstance
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.saveSortingMode
import com.amazon.tv.leanbacklauncher.util.Lists
import com.amazon.tv.leanbacklauncher.widget.RowViewAdapter
import java.util.*

open class AppsAdapter(context: Context, actionOpenLaunchPointListener: ActionOpenLaunchPointListener?, vararg appTypes: AppCategory?) : RowViewAdapter<AppViewHolder?>(context), AppsRanker.RankingListener, LaunchPointList.Listener, OnSharedPreferenceChangeListener {
    private val mActionOpenLaunchPointListener: ActionOpenLaunchPointListener?
    protected var mAppTypes: Set<AppCategory?>? = null
    protected var mFilter: AppFilter
    protected var mAppsManager: AppsManager?
    protected var mFlaggedForResort: Boolean
    protected val mInflater: LayoutInflater
    private var mItemsHaveBeenSorted = false
    protected var mLaunchPoints: ArrayList<LaunchPoint>
    private val mNotifyHandler = Handler()
    private val prefUtil: SharedPreferencesUtil?
    private val listener: OnSharedPreferenceChangeListener = this

    interface ActionOpenLaunchPointListener {
        fun onActionOpenLaunchPoint(str: String?, str2: String?)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        refreshDataSetAsync()
    }

    abstract class AppFilter {
        abstract fun include(point: LaunchPoint?): Boolean
    }

    open class AppViewHolder internal constructor(v: View?, adapter: AppsAdapter?) : LauncherViewHolder(v) {
        private val mAdapter: AppsAdapter?
        private var mBannerView: BannerView? = null
        var componentName: String? = null
            private set
        var packageName: String? = null
            private set

        open fun init(launchPoint: LaunchPoint?) {
            itemView.visibility = View.VISIBLE
            if (launchPoint != null) {
                packageName = launchPoint.packageName
                componentName = launchPoint.componentName
                setLaunchIntent(launchPoint.launchIntent)
                setLaunchTranslucent(launchPoint.isTranslucentTheme)
                setLaunchColor(launchPoint.launchColor)
            }
        }

        open fun init(packageName: String?, componentName: String?, launchIntent: Intent?, launchColor: Int) {
            this.packageName = packageName
            this.componentName = componentName
            setLaunchIntent(launchIntent)
            setLaunchColor(launchColor)
        }

        open fun clearBannerState() {
            mBannerView?.clearBannerForRecycle()
        }

        override fun onLaunchSucceeded() {
            if (mAdapter != null) {
                mAdapter.mAppsManager!!.onAppsRankerAction(packageName, componentName, 1)
                mAdapter.onActionOpenLaunchPoint(componentName, packageName)
                if (mAdapter.mAppsManager!!.sortingMode === AppsManager.SortingMode.RECENCY) {
                    mAdapter.mFlaggedForResort = true
                }
            }
        }

        fun checkEditModeDimLevel() {
            var curView: RecyclerView.ViewHolder? = null
            if (itemView is BannerView) {
                val parent = if (itemView.getParent() is EditableAppsRowView) itemView.getParent() as EditableAppsRowView else null
                if (parent != null) {
                    curView = parent.curViewHolder
                }
                if (itemView.isActivated() && parent != null && parent.editMode && curView != null && curView !== this && curView.itemView.isSelected) {
                    itemView.setDimState(ViewDimmer.DimState.EDIT_MODE, true)
                }
            }
        }

        fun setEditModeInBannerView() {
            if (itemView is BannerView && itemView.getParent() is EditableAppsRowView) {
                itemView.isEditMode = (itemView.getParent() as EditableAppsRowView).editMode
            }
        }

        override fun onClick(v: View) {
            if (v is BannerView && v.isEditMode) {
                v.onClickInEditMode()
            } else {
                super.onClick(v)
            }
        }

        init {
            if (v is BannerView) {
                mBannerView = v
                mBannerView!!.viewHolder = this
            }
            mAdapter = adapter
        }
    }

    open class AppBannerViewHolder(v: View?, adapter: AppsAdapter?) : AppViewHolder(v, adapter) {
        private val mBackground: Drawable
        private var mBannerView: ImageView? = null
        private val mOverlayHelper: InstallStateOverlayHelper
        /* bridge */ /* synthetic */  override fun init(str: String?, str2: String?, intent: Intent?, i: Int) {
            super.init(str, str2, intent, i)
        }

        /* bridge */ /* synthetic */  override fun onClick(view: View) {
            super.onClick(view)
        }

        override fun init(launchPoint: LaunchPoint?) {
            super.init(launchPoint)
            if (launchPoint != null && mBannerView != null) {
                val drawable = launchPoint.bannerDrawable
                if (drawable !is BitmapDrawable || drawable.bitmap.hasAlpha()) {
                    // background for Logo images
                    if (launchPoint.launchColor != 0)
                        mBannerView?.setBackgroundColor(launchPoint.launchColor)
                    else
                        mBannerView?.background = mBackground
                } else {
                    mBannerView?.background = null
                }
                mBannerView?.contentDescription = launchPoint.title
                mBannerView?.setImageDrawable(drawable)
                if (launchPoint.isInstalling) {
                    mOverlayHelper.initOverlay(launchPoint)
                } else {
                    mOverlayHelper.hideOverlay()
                }
            }
        }

        fun init(title: CharSequence?, banner: Drawable?, launchColor: Int) {
            super.init(null, null, null, launchColor)
            mBannerView?.let {
                it.setImageDrawable(banner)
                it.contentDescription = title
            }
        }

        override fun clearBannerState() {
            super.clearBannerState()
            mBannerView?.let {
                it.clipToOutline = true
            }
        }

        init {
            mOverlayHelper = InstallStateOverlayHelper(v)
            if (v != null) {
                mBannerView = v.findViewById(R.id.app_banner)
            } else {
                mBannerView = null
            }
            mBackground = v!!.resources.getDrawable(R.drawable.banner_background, null)
        }
    }

    private class AppFallbackViewHolder(v: View?, adapter: AppsAdapter?) : AppViewHolder(v, adapter) {
        private var mIconView: ImageView?
        private var mBannerView: LinearLayout?
        private var mLabelView: TextView?
        private val mOverlayHelper: InstallStateOverlayHelper

        override fun init(launchPoint: LaunchPoint?) {
            super.init(launchPoint)
            launchPoint?.let { lp ->
                val icon = lp.iconDrawable
                mIconView?.setImageDrawable(icon)
                mLabelView?.let { lv ->
                    lv.text = lp.title
                    // dynamic label text
                    if (isLight(lp.launchColor))
                        lv.setTextColor(Color.BLACK)
                    else
                        lv.setTextColor(Color.WHITE)
                }
                if (lp.isInstalling) {
                    mOverlayHelper.initOverlay(lp)
                } else {
                    mOverlayHelper.hideOverlay()
                }
                // background color
                mBannerView?.setBackgroundColor(lp.launchColor)
            }
        }

        init {
            mOverlayHelper = InstallStateOverlayHelper(v)
            mIconView = null
            mLabelView = null
            mBannerView = null
            v?.let {
                mIconView = it.findViewById(R.id.banner_icon)
                mLabelView = it.findViewById(R.id.banner_label)
                mBannerView = it.findViewById(R.id.app_banner)
            }
        }
    }

    private class InstallStateOverlayHelper(v: View?) {
        private var mOverlayView: View?
        private var mProgressBar: ProgressBar?
        private var mProgressView: TextView?
        private var mStateView: TextView?

        fun initOverlay(launchPoint: LaunchPoint) {
            mStateView?.text = launchPoint.getInstallStateString(mStateView!!.context)
            mProgressView?.text = launchPoint.getInstallProgressString(mProgressView!!.context)
            mProgressBar?.let {
                val progressPercent = launchPoint.installProgressPercent
                if (progressPercent == -1) {
                    it.isIndeterminate = true
                } else {
                    it.progress = progressPercent
                    it.isIndeterminate = false
                }
            }
            mOverlayView?.visibility = View.VISIBLE
        }

        fun hideOverlay() {
            mOverlayView?.visibility = View.GONE
        }

        init {
            mOverlayView = null
            mStateView = null
            mProgressView = null
            mProgressBar = null
            v?.let {
                mOverlayView = it.findViewById(R.id.install_state_overlay)
                mStateView = it.findViewById(R.id.banner_install_state)
                mProgressView = it.findViewById(R.id.banner_install_progress)
                mProgressBar = it.findViewById(R.id.progress_bar)
            }
        }
    }

    private inner class RefreshTask : AsyncTask<Void?, Void?, ArrayList<LaunchPoint>>() {
        override fun doInBackground(vararg params: Void?): ArrayList<LaunchPoint> {
            val lps: List<LaunchPoint> = refreshedLaunchPointList
            val filtered = ArrayList<LaunchPoint>()

            // TODO
            for (lp in lps) {
                if (mFilter.include(lp)) {
                    filtered.add(lp)
                }
            }
            return filtered
        }

        override fun onPostExecute(launchPoints: ArrayList<LaunchPoint>) {
            val changes = Lists.getChanges(mLaunchPoints, launchPoints, mAppsManager!!.launchPointComparator)
            mLaunchPoints = launchPoints
            onPostRefresh()
            for (change in changes) {
                when (change.type) {
                    Lists.Change.Type.INSERTION -> notifyItemRangeInserted(change.index, change.count)
                    Lists.Change.Type.REMOVAL -> notifyItemRangeRemoved(change.index, change.count)
                    else -> throw IllegalStateException("Unsupported change type: " + change.type)
                }
            }
        }
    }

    private class SettingViewHolder(v: View?, adapter: AppsAdapter?) : AppViewHolder(v, adapter) {
        private var mIconView: ImageView?
        private var mLabelView: TextView?
        private var mMainView: View?
        override fun init(launchPoint: LaunchPoint?) {
            super.init(launchPoint)
            launchPoint?.let { lp ->
                mIconView?.setImageDrawable(lp.iconDrawable)
                mLabelView?.text = lp.title
                mMainView?.contentDescription = lp.contentDescription
            }
        }

        init {
            mMainView = null
            mIconView = null
            mLabelView = null
            v?.let {
                mMainView = it.findViewById(R.id.main)
                mIconView = it.findViewById(R.id.icon)
                mLabelView = it.findViewById(R.id.label)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position >= mLaunchPoints.size) {
            Log.e("AppsAdapter", "getItemViewType with out of bounds index = $position")
            return if (!mAppTypes!!.contains(AppCategory.SETTINGS)) {
                0
            } else 2
        }
        val launchPoint = mLaunchPoints[position]
        if (mAppTypes!!.contains(AppCategory.SETTINGS)) {
            return 2
        }
        return if (launchPoint!!.hasBanner()) {
            0
        } else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val holder: AppViewHolder
        return when (viewType) {
            0 -> {
                holder = AppBannerViewHolder(mInflater.inflate(R.layout.app_banner, parent, false), this)
                holder
            }
            1 -> {
                holder = AppFallbackViewHolder(mInflater.inflate(R.layout.app_fallback_banner, parent, false), this)
                holder
            }
            else -> { // 2: setings
                holder = SettingViewHolder(mInflater.inflate(R.layout.setting, parent, false), this)
                holder
            }
        }
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        if (position < mLaunchPoints.size) {
            val launchPoint = mLaunchPoints[position]
            holder.clearBannerState()
            holder.init(launchPoint)
        }
    }

    override fun onViewAttachedToWindow(holder: AppViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.itemView is BannerView && holder.itemView.getParent() is EditableAppsRowView) {
            (holder.itemView.getParent() as EditableAppsRowView).refreshSelectedView()
            holder.checkEditModeDimLevel()
            holder.setEditModeInBannerView()
        }
    }

    override fun onViewDetachedFromWindow(holder: AppViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.itemView is BannerView) {
            holder.itemView.setSelected(false)
        }
    }

    override fun getItemCount(): Int {
        return mLaunchPoints.size
    }

    fun sortItemsIfNeeded(force: Boolean) {
        val sortNeeded: Boolean
        sortNeeded = mFlaggedForResort || force
        mFlaggedForResort = false
        if (force && mAppsManager!!.sortingMode === AppsManager.SortingMode.FIXED) {
            saveAppOrderSnapshot()
        }
        if (sortNeeded) {
            mItemsHaveBeenSorted = true
            sortLaunchPoints(mLaunchPoints)
            notifyDataSetChanged()
        }
    }

    fun takeItemsHaveBeenSorted(): Boolean {
        val sorted = mItemsHaveBeenSorted
        mItemsHaveBeenSorted = false
        return sorted
    }

    fun moveLaunchPoint(initPosition: Int, desiredPosition: Int, userAction: Boolean): Boolean {
        if (desiredPosition < 0 || desiredPosition > mLaunchPoints.size - 1 || initPosition < 0 || initPosition > mLaunchPoints.size - 1) {
            return false
        }
        val focused = mLaunchPoints[initPosition]
        mLaunchPoints[initPosition] = mLaunchPoints[desiredPosition]
        mLaunchPoints[desiredPosition] = focused
        notifyItemMoved(initPosition, desiredPosition)
        if (Math.abs(desiredPosition - initPosition) > 1) {
            notifyItemMoved(desiredPosition + if (desiredPosition - initPosition > 0) -1 else 1, initPosition)
        }
        if (!userAction) {
            return true
        }
        saveSortingMode(mContext, AppsManager.SortingMode.FIXED)
        return true
    }

    fun saveAppOrderSnapshot() {
        if (Log.isLoggable("LauncherEditMode", Log.DEBUG)) {
            Log.d("LauncherEditMode", "AppsAdapter saw EditMode change and initiated snapshot.")
        }
        mAppsManager!!.saveOrderSnapshot(mLaunchPoints)
    }

    fun getDrawableFromLaunchPoint(index: Int): Drawable? {
        return if (index < 0 || index >= mLaunchPoints.size) {
            null
        } else mLaunchPoints[index]!!.bannerDrawable
    }

    fun getLaunchPointForPosition(index: Int): LaunchPoint? {
        return if (index < 0 || index >= mLaunchPoints.size) {
            null
        } else mLaunchPoints[index]
    }

    private fun onActionOpenLaunchPoint(component: String?, group: String?) {
        if (mActionOpenLaunchPointListener != null) {
            mActionOpenLaunchPointListener.onActionOpenLaunchPoint(component, group)
        }
    }

    private val refreshedLaunchPointList: ArrayList<LaunchPoint>
        private get() {
            val launchPoints = ArrayList<LaunchPoint>()
            if (mAppTypes!!.isEmpty()) {
                return mAppsManager!!.allLaunchPoints
            }
            for (category in mAppTypes!!) {
                when (category) {
                    AppCategory.OTHER -> launchPoints.addAll(mAppsManager!!.getLaunchPointsByCategory(AppCategory.OTHER))
                    AppCategory.VIDEO -> launchPoints.addAll(mAppsManager!!.getLaunchPointsByCategory(AppCategory.VIDEO))
                    AppCategory.MUSIC -> launchPoints.addAll(mAppsManager!!.getLaunchPointsByCategory(AppCategory.MUSIC))
                    AppCategory.GAME -> launchPoints.addAll(mAppsManager!!.getLaunchPointsByCategory(AppCategory.GAME))
                    AppCategory.SETTINGS -> launchPoints.addAll(mAppsManager!!.getSettingsLaunchPoints(true))
                }
            }
            sortLaunchPoints(launchPoints)
            return launchPoints
        }

    protected open fun onPostRefresh() {}

    protected open fun sortLaunchPoints(launchPoints: ArrayList<LaunchPoint>) {
        if (!mAppTypes!!.contains(AppCategory.SETTINGS)) {
            if (launchPoints != null) {
                mAppsManager!!.rankLaunchPoints(launchPoints, this)
            }
        }
    }

    override fun onLaunchPointsAddedOrUpdated(launchPoints: ArrayList<LaunchPoint>?) {
        mNotifyHandler.post {
            if (BuildConfig.DEBUG) Log.d(TAG, "run: onLaunchPointsAddedOrUpdated")
            var saveAppOrderChanges = false
            // if (BuildConfig.DEBUG) Log.d(TAG, "size: " + AppsAdapter.this.mLaunchPoints.size());
            if (BuildConfig.DEBUG) Log.d(TAG, "Apps: " + mLaunchPoints)
            for (i in launchPoints!!.indices) {
                val lp = launchPoints[i]
                if (BuildConfig.DEBUG) Log.d(TAG, "Check: $lp")
                for (j in mLaunchPoints.indices) {
                    val alp = mLaunchPoints[j]
                    if (lp.packageName == alp!!.packageName) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "Found: $alp Position: $j")
                        mLaunchPoints.removeAt(j)
                        notifyItemRemoved(j)
                    }
                }
            }
            for (i in launchPoints.indices.reversed()) {
                if (!mFilter.include(launchPoints[i])) {
                    continue
                }
                val lp = launchPoints[i]
                if (lp != null && !mAppTypes!!.contains(lp.appCategory)) {
                    continue
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "notifyItemInserted")
                notifyItemInserted(mAppsManager!!.insertLaunchPoint(mLaunchPoints, lp))
                saveAppOrderChanges = true
            }
            if (saveAppOrderChanges && mAppsManager!!.sortingMode === AppsManager.SortingMode.FIXED) {
                saveAppOrderSnapshot()
            }
        }
    }

    override fun onLaunchPointsRemoved(launchPoints: ArrayList<LaunchPoint>?) {
        mNotifyHandler.post {
            var i: Int
            var saveAppOrderChanges = false
            var itemRemovedAt = -1
            for (j in mLaunchPoints.indices.reversed()) {
                i = launchPoints!!.size - 1
                while (i >= 0) {
                    if (mLaunchPoints[j]!!.equals(launchPoints[i]) && itemRemovedAt == -1) {
                        launchPoints.removeAt(i)
                        saveAppOrderChanges = true
                        itemRemovedAt = j
                        break
                    }
                    i--
                }
            }
            if (saveAppOrderChanges && mAppsManager!!.sortingMode === AppsManager.SortingMode.FIXED) {
                saveAppOrderSnapshot()
            }
            if (itemRemovedAt != -1) {
                val numRows: Int
                val maxApps = getAppsMax(mContext)
                val viewType = getItemViewType(itemRemovedAt)
                val res = mContext.resources
                numRows = if (this@AppsAdapter.itemCount > maxApps) {
                    res.getInteger(R.integer.max_num_banner_rows)
                } else {
                    res.getInteger(R.integer.min_num_banner_rows)
                }
                if ((viewType == 0 || viewType == 1) && numRows > 1) {
                    var lastPosition = itemRemovedAt
                    i = itemRemovedAt
                    while (i + numRows < mLaunchPoints.size) {
                        moveLaunchPoint(i, i + numRows, false)
                        lastPosition = i + numRows
                        i += numRows
                    }
                    mLaunchPoints.removeAt(lastPosition)
                    notifyItemRemoved(lastPosition)
                } else {
                    mLaunchPoints.removeAt(itemRemovedAt)
                    notifyItemRemoved(itemRemovedAt)
                }
            }
            saveAppOrderSnapshot()
        }
    }

    override fun onLaunchPointListGeneratorReady() {
        if (mAppsManager!!.isAppsRankerReady) {
            refreshDataSetAsync()
        }
    }

    override fun onSettingsChanged() {
        // TODO
    }

    override fun onRankerReady() {
        if (mAppsManager!!.isLaunchPointListGeneratorReady) {
            refreshDataSetAsync()
        }
    }

    override fun onViewRecycled(holder: AppViewHolder) {
        holder.itemView.visibility = View.VISIBLE
    }

    fun refreshDataSetAsync() {
        RefreshTask().execute()
    }

    companion object {
        private const val TAG = "AppsAdapter"
        fun isDark(color: Int): Boolean {
            return ColorUtils.calculateLuminance(color) < 0.25 // 0.5
        }

        fun isLight(color: Int): Boolean {
            return ColorUtils.calculateLuminance(color) > 0.5
        }
    }

    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mLaunchPoints = ArrayList()
        mAppsManager = getInstance(context)
        prefUtil = instance(context)
        prefUtil!!.addHiddenListener(listener)
        mFilter = object : AppFilter() {
            override fun include(point: LaunchPoint?): Boolean {
                return true
            }
        }
        if (appTypes != null) {
            mAppTypes = HashSet(Arrays.asList(*appTypes))
        } else {
            mAppTypes = emptySet()
        }
        mFlaggedForResort = false
        mActionOpenLaunchPointListener = actionOpenLaunchPointListener
        mAppsManager!!.registerLaunchPointListListener(this)
    }
}