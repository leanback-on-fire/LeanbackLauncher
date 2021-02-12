package com.amazon.tv.leanbacklauncher.apps

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.apps.AppsDbHelper
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.getSavedSortingMode
import com.amazon.tv.leanbacklauncher.apps.AppsManager.SortingMode
import com.amazon.tv.leanbacklauncher.util.Partner
import com.amazon.tv.leanbacklauncher.util.Util.initialRankingApplied
import com.amazon.tv.leanbacklauncher.util.Util.setInitialRankingAppliedFlag
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executor

class AppsRanker internal constructor(ctx: Context, dbHelper: AppsDbHelper, executor: Executor?) : AppsDbHelper.Listener {
    private val mCachedActions: Queue<CachedAction?>
    private val mContext: Context
    private val mDbHelper: AppsDbHelper
    private var mEntities: HashMap<String, AppsEntity>
    private val mEntitiesLock: Any
    private val mLastLaunchPointRankingLogDump: ArrayList<String?>
    private var mLaunchPointComparator: Comparator<LaunchPoint>? = null
    private val mListeners: Queue<RankingListener?>
    private var mNeedsResorting = false
    private var mPrefsListener: SharedPreferencesChangeListener? = null
    private var mQueryingScores: Boolean
    private var mSortingMode: SortingMode

    init {
        mEntitiesLock = Any()
        mListeners = LinkedList<RankingListener?>()
        mCachedActions = LinkedList<CachedAction?>()
        mEntities = HashMap<String, AppsEntity>()
        mSortingMode = SortingMode.FIXED
        mLastLaunchPointRankingLogDump = ArrayList<String?>()
        mContext = ctx
        mDbHelper = dbHelper
        mSortingMode = getSavedSortingMode(mContext)
        registerPreferencesListeners()
        mQueryingScores = true
        mDbHelper.loadEntities(this, executor)
    }

    interface RankingListener {
        fun onRankerReady()
    }

    private class CachedAction(var key: String, var component: String, var group: String?, var action: Int)

    private inner class LaunchPointInstallComparator() : Comparator<LaunchPoint?> {
        override fun compare(lhs: LaunchPoint?, rhs: LaunchPoint?): Int {
            if (lhs == null || rhs == null) {
                return 0
            }
            val lhsOrder = getEntityOrder(lhs)
            val rhsOrder = getEntityOrder(rhs)
            return if (lhsOrder == rhsOrder) {
                val lInstallTime = lhs.firstInstallTime
                val rInstallTime = rhs.firstInstallTime
                if (lInstallTime < 0 && rInstallTime >= 0) {
                    return 1
                }
                if (rInstallTime < 0 && lInstallTime >= 0) {
                    return -1
                }
                if (lInstallTime != rInstallTime) {
                    if (lInstallTime > rInstallTime) 1 else -1
                } else {
                    lhs.title!!.compareTo(rhs.title!!, ignoreCase = true)
                }
            } else if (lhsOrder == 0L) {
                1
            } else {
                if (rhsOrder == 0L) {
                    return -1
                }
                if (lhsOrder > rhsOrder) 1 else -1
            }
        }
    }

    private inner class LaunchPointRecencyComparator() : Comparator<LaunchPoint?> {
        override fun compare(lhs: LaunchPoint?, rhs: LaunchPoint?): Int {
            return if (lhs == null || rhs == null) {
                0
            } else java.lang.Long.compare(Math.max(getLastOpened(rhs), rhs.firstInstallTime), Math.max(getLastOpened(lhs), lhs.firstInstallTime))
        }
    }

    private class SharedPreferencesChangeListener(appsRanker: AppsRanker?) : OnSharedPreferenceChangeListener {
        private val mAppsRankerRef: WeakReference<AppsRanker?>
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            val appsRanker = mAppsRankerRef.get()
            appsRanker?.checkForSortingModeChange()
        }

        init {
            mAppsRankerRef = WeakReference<AppsRanker?>(appsRanker)
        }
    }

    private constructor(ctx: Context, dbHelper: AppsDbHelper) : this(ctx, dbHelper, AsyncTask.SERIAL_EXECUTOR) {}

    val sortingMode: SortingMode
        get() {
            val savedSortingMode = getSavedSortingMode(mContext)
            if (mSortingMode !== savedSortingMode) {
                mSortingMode = savedSortingMode
                mLaunchPointComparator = null
            }
            return mSortingMode
        }

    fun checkIfResortingIsNeeded(): Boolean {
        val needed = mNeedsResorting
        mNeedsResorting = false
        return needed
    }

    fun unregisterListeners() {
        unregisterPreferencesListeners()
    }

    fun onAction(packageName: String?, component: String?, actionType: Int) {
        onAction(packageName, component, null, actionType)
    }

    private fun onAction(key: String?, component: String?, group: String?, actionType: Int) { // , AppCategory category
        if (key?.isNotEmpty() == true) {
            if (actionType != 3) {
                mNeedsResorting = true
            }
            synchronized(mCachedActions) {
                if (mQueryingScores) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Scores not ready, caching this action")
                    }
                    mCachedActions.add(component?.let { CachedAction(key, it, group, actionType) }) // , category
                    return
                }
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "action: $actionType for $key - group = $group")
                }
                synchronized(mEntitiesLock) {
                    var entity = mEntities[key]
                    if (actionType != 3) {
                        if (entity == null) {
                            entity = AppsEntity(mContext, mDbHelper, key) //, false
                            mEntities[key] = entity
                        }
                        entity.onAction(actionType, component, group)
                        //  LoggingUtils.logRankerActionEvent(key, actionType, 0, TAG, this.mContext);
                        mDbHelper.saveEntity(entity) // , true
                    } else if (entity != null) {
                        if (entity.getOrder(component) != 0L) {
                            entity.onAction(actionType, component, null)
                            mDbHelper.removeEntity(key, false)
                        } else {
                            mEntities.remove(key)
                            mDbHelper.removeEntity(key, true)
                        }
                    }
                }
            }
        }
    }

    fun rankLaunchPoints(launchPoints: ArrayList<LaunchPoint>, listener: RankingListener?): Boolean {
        if (registerListenerIfNecessary(listener)) {
            return false
        }
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "refreshing Launchpoint ranking")
        }
        synchronized(mEntitiesLock) {
            Collections.sort(launchPoints, launchPointComparator)
            mLastLaunchPointRankingLogDump.clear()
            mLastLaunchPointRankingLogDump.add("Last Launchpoint Ranking Ordering: " + Date().toString())
            for (lp in launchPoints) {
                val entity = mEntities[lp.packageName]
                if (entity != null) {
                    mLastLaunchPointRankingLogDump.add(lp.title + " | R " + entity.getOrder(lp.componentName) + " | LO " + getLastOpened(lp) + " | INST " + lp.firstInstallTime)
                }
            }
        }
        return true
    }

    val launchPointComparator: Comparator<LaunchPoint>?
        get() {
            if (mLaunchPointComparator == null) {
                mLaunchPointComparator = if (sortingMode === SortingMode.RECENCY)
                    LaunchPointRecencyComparator() as Comparator<LaunchPoint>
                else
                    LaunchPointInstallComparator() as Comparator<LaunchPoint>
            }
            return mLaunchPointComparator
        }

    fun insertLaunchPoint(launchPoints: ArrayList<LaunchPoint>, newLp: LaunchPoint): Int {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Inserting new LaunchPoint")
        }
        if (registerListenerIfNecessary(null)) {
            val pos = launchPoints.size
            launchPoints.add(newLp)
            return pos
        }
        var pos = 0
        val comp = launchPointComparator
        while (pos < launchPoints.size && comp!!.compare(newLp, launchPoints[pos]) >= 0) {
            pos++
        }
        launchPoints.add(pos, newLp)
        return pos
    }

    private fun getLastOpened(lp: LaunchPoint): Long {
        var entity: AppsEntity?
        synchronized(mEntitiesLock) { entity = mEntities[lp.packageName] }
        return if (entity != null) entity!!.getLastOpenedTimeStamp(lp.componentName) else -100
    }

    private fun getEntityOrder(lp: LaunchPoint): Long {
        var entity: AppsEntity?
        synchronized(mEntitiesLock) { entity = mEntities[lp.packageName] }
        return if (entity != null) entity!!.getOrder(lp.componentName) else 0
    }

    private fun registerListenerIfNecessary(listener: RankingListener?): Boolean {
        var mustRegister: Boolean
        synchronized(mCachedActions) {
            mustRegister = mQueryingScores
            if (mustRegister) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Entities not ready")
                }
                if (listener != null) {
                    mListeners.add(listener)
                }
            }
        }
        return mustRegister
    }

    override fun onEntitiesLoaded(entities: HashMap<String, AppsEntity>) {
        synchronized(mEntitiesLock) { mEntities = entities }
        synchronized(mCachedActions) {
            mQueryingScores = false
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Scores retrieved, playing back " + mCachedActions.size + " actions")
            }
            while (!mCachedActions.isEmpty()) {
                val action = mCachedActions.remove()
                onAction(action!!.key, action.component, action.group, action.action)
            }
            if (!initialRankingApplied(mContext)) {
                val outOfBoxOrder = outOfBoxOrder
                val partnerOutOfBoxOrder = Partner.get(mContext).outOfBoxOrder
                val partnerLength = partnerOutOfBoxOrder?.size ?: 0
                val totalOrderings = outOfBoxOrder.size + partnerLength
                val baseTime = System.currentTimeMillis()
                if (partnerOutOfBoxOrder != null) {
                    applyOutOfBoxOrdering(partnerOutOfBoxOrder, 0, totalOrderings, baseTime)
                }
                applyOutOfBoxOrdering(outOfBoxOrder, partnerLength, totalOrderings, baseTime)
                setInitialRankingAppliedFlag(mContext, true)
            }
        }
        while (!mListeners.isEmpty()) {
            mListeners.remove()!!.onRankerReady()
        }
    }

    val outOfBoxOrder: Array<String>
        get() = mContext.resources.getStringArray(R.array.out_of_box_order)
    val isReady: Boolean
        get() {
            var z: Boolean
            synchronized(mCachedActions) { z = !mQueryingScores }
            return z
        }

    private fun applyOutOfBoxOrdering(order: Array<String>?, offsetEntities: Int, totalEntities: Int, baseTime: Long) {
        if (order != null && order.size != 0 && offsetEntities >= 0 && totalEntities >= order.size + offsetEntities) {
            val entitiesBelow = totalEntities - offsetEntities - order.size
            val size = order.size
            for (i in 0 until size) {
                val key = order[size - i - 1]
                if (!mEntities.containsKey(key)) {
                    val score = (entitiesBelow + i + 1).toLong() + baseTime
                    val e = AppsEntity(mContext, mDbHelper, key, score, (entitiesBelow + size - i).toLong())
                    mEntities[key] = e
                    mDbHelper.saveEntity(e)
                }
            }
        }
    }

    fun saveOrderSnapshot(launchPoints: ArrayList<LaunchPoint>) {
        synchronized(mEntitiesLock) {
            for (i in launchPoints.indices) {
                saveEntityOrder(launchPoints[i], i)
            }
        }
    }

    private fun saveEntityOrder(launchPoint: LaunchPoint, position: Int) {
        var e = mEntities[launchPoint.packageName]
        if (e != null) {
            e.setOrder(launchPoint.componentName, position.toLong() + 1)
        } else {
            e = AppsEntity(mContext, mDbHelper, launchPoint.packageName, 0, (position + 1).toLong())
            mEntities[launchPoint.packageName!!] = e
        }
        mDbHelper.saveEntity(e)
    }

    fun dump(prefix: String, writer: PrintWriter) {
        writer.println("$prefix==========================")
        val it: Iterator<*> = mLastLaunchPointRankingLogDump.iterator()
        while (it.hasNext()) {
            writer.println(prefix + " " + it.next())
        }
        writer.println("$prefix==========================")
    }

    private fun registerPreferencesListeners() {
        unregisterPreferencesListeners()
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        if (prefs != null) {
            mPrefsListener = SharedPreferencesChangeListener(this)
            prefs.registerOnSharedPreferenceChangeListener(mPrefsListener)
        }
    }

    private fun unregisterPreferencesListeners() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        if (prefs != null && mPrefsListener != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(mPrefsListener)
            mPrefsListener = null
        }
    }

    private fun checkForSortingModeChange() {
        val savedSortingMode = getSavedSortingMode(mContext)
        if (mSortingMode !== savedSortingMode) {
            mSortingMode = savedSortingMode
            mLaunchPointComparator = null
            mNeedsResorting = true
        }
    }

    companion object {
        private const val TAG = "AppsRanker"

        @SuppressLint("StaticFieldLeak")
        private var sAppsRanker: AppsRanker? = null
        fun getInstance(context: Context): AppsRanker {
            if (sAppsRanker == null) {
                sAppsRanker = AppsRanker(context.applicationContext, AppsDbHelper.getInstance(context))
            }
            return sAppsRanker as AppsRanker
        }
    }
}