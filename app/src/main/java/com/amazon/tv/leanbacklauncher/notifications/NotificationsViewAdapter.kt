package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.collection.ArraySet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.leanbacklauncher.MainActivity.IdleListener
import com.amazon.tv.leanbacklauncher.notifications.NotificationUtils.equals
import com.amazon.tv.leanbacklauncher.util.Preconditions
import com.amazon.tv.leanbacklauncher.widget.RowViewAdapter
import com.amazon.tv.tvrecommendations.TvRecommendation
import java.io.PrintWriter
import java.util.*

abstract class NotificationsViewAdapter<VH : RecyclerView.ViewHolder?> internal constructor(
    context: Context?,
    minUpdateTime: Long,
    maxUpdateTime: Long
) : RowViewAdapter<VH>(context), IdleListener {
    private var mHomeScreenMessaging: HomeScreenMessaging? = null
    private val mMasterList = ArrayList<TvRecommendation?>()
    private var mPrioritizeRowUpdateState: PrioritizeRowUpdateState<VH>? = null
    private val mRecommendationToBeRemoved: MutableSet<TvRecommendation?> = ArraySet<TvRecommendation?>()
    private val mSyncedList = ArrayList<TvRecommendation?>()
    var uiState = 0
        private set

    init {
        if (!isPartnerClient) {
            mPrioritizeRowUpdateState =
                PrioritizeRowUpdateState(this, minUpdateTime, maxUpdateTime)
        }
    }

    companion object {
        const val UI_STATE_VISIBLE = 1
    }

    internal class PrioritizeRowUpdateState<VH : RecyclerView.ViewHolder?>(
        private val mAdapter: NotificationsViewAdapter<VH>,
        private val mMinUpdateTime: Long,
        private val mMaxUpdateTime: Long
    ) {
        private val mHandler = Handler()
        private var mIsIdle = false
        private val mStateTickRunnable = Runnable { updateTick() }
        private fun scheduleUpdateTickIfNeeded() {
            val masterList = mAdapter.masterList
            val syncedList: List<TvRecommendation?> = mAdapter.syncedList
            mHandler.removeCallbacks(mStateTickRunnable)
            if (masterList.size - syncedList.size > 0) {
                mHandler.postDelayed(
                    mStateTickRunnable,
                    (mMaxUpdateTime - mMinUpdateTime) / (masterList.size - syncedList.size).toLong() + mMinUpdateTime
                )
            }
        }

        private fun unScheduleUpdateTick() {
            mHandler.removeCallbacks(mStateTickRunnable)
        }

        private fun postDeletesAndSubstitutes() {
            val masterList = mAdapter.masterList
            val syncedList = mAdapter.syncedList
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return syncedList.size
                }

                override fun getNewListSize(): Int {
                    return masterList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return equals(syncedList[oldItemPosition], masterList[newItemPosition])
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return syncedList[oldItemPosition] == masterList[newItemPosition]
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                    return masterList[newItemPosition]
                }
            }, false)
            val snapshot: List<TvRecommendation?> = ArrayList<TvRecommendation?>(syncedList)
            result.dispatchUpdatesTo(object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {
                    for (i in 0 until count) {
                        syncedList.add(position + i, null)
                    }
                }

                override fun onRemoved(position: Int, count: Int) {
                    for (i in 0 until count) {
                        syncedList.removeAt(position)
                    }
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    throw IllegalStateException("Should not receive move events")
                }

                override fun onChanged(position: Int, count: Int, payload: Any?) {
                    for (i in 0 until count) {
                        syncedList[position + i] = payload as TvRecommendation?
                    }
                }
            })
            val iterator = syncedList.iterator()
            while (iterator.hasNext()) {
                if (iterator.next() == null) {
                    iterator.remove()
                }
            }
            mAdapter.notifyChangesSinceSnapshot(snapshot)
            mAdapter.updateRowVisibility()
        }

        private fun postOneInsert() {
            val masterList = mAdapter.masterList
            val syncedList: MutableList<TvRecommendation?> = mAdapter.syncedList
            var i = 0
            while (i < masterList.size && i <= syncedList.size) {
                if (i == syncedList.size || masterList[i] != syncedList[i]) {
                    syncedList.add(i, masterList[i])
                    mAdapter.notifyItemInserted(mAdapter.nonNotifItemCount + i)
                    break
                }
                i++
            }
            mAdapter.updateRowVisibility()
        }

        private fun updateTick() {
            postOneInsert()
            scheduleUpdateTickIfNeeded()
        }

        fun onUiVisible() {
            mAdapter.postAllRowUpdates()
        }

        fun onUiInvisible() {
            unScheduleUpdateTick()
        }

        fun onNewRowChange() {
            if (mAdapter.uiState != 1) {
                return
            }
            if (mIsIdle) {
                mAdapter.postAllRowUpdates()
                return
            }
            postDeletesAndSubstitutes()
            scheduleUpdateTickIfNeeded()
        }

        fun onIdleStateChange(isIdle: Boolean) {
            mIsIdle = isIdle
            if (isIdle) {
                mAdapter.postAllRowUpdates()
                unScheduleUpdateTick()
            }
        }
    }

    protected abstract val isPartnerClient: Boolean
    protected abstract fun onRecommendationDismissed(tvRecommendation: TvRecommendation?)
    protected fun onClearRecommendations(reason: Int) {
        if (mHomeScreenMessaging != null) {
            mHomeScreenMessaging!!.onClearRecommendations(reason)
        }
    }

    override fun getItemCount(): Int {
        return mSyncedList.size + nonNotifItemCount
    }

    override fun onIdleStateChange(isIdle: Boolean) {
        if (!isPartnerClient) {
            mPrioritizeRowUpdateState?.onIdleStateChange(isIdle)
        }
    }

    override fun onVisibilityChange(isVisible: Boolean) {}
    protected fun getRecommendation(position: Int): TvRecommendation? {
        return mSyncedList[position - nonNotifItemCount]
    }

    private fun purgeDismissedNotifications() {
        for (rec in mRecommendationToBeRemoved) {
            removeRecommendation(rec)
            onRecommendationDismissed(rec)
        }
        mRecommendationToBeRemoved.clear()
    }

    protected fun dismissNotification(rec: TvRecommendation?) {
        mRecommendationToBeRemoved.add(rec)
    }

    fun setNotificationRowViewFlipper(homeScreenMessaging: HomeScreenMessaging) {
        mHomeScreenMessaging = Preconditions.checkNotNull(homeScreenMessaging)
    }

    private fun updateRowVisibility() {
        if (mHomeScreenMessaging != null) {
            mHomeScreenMessaging!!.recommendationsUpdated(mSyncedList.size > 0)
        }
    }

    fun onUiVisible() {
        uiState = 1
        if (isPartnerClient) {
            postAllRowUpdates()
        } else {
            mPrioritizeRowUpdateState?.onUiVisible()
        }
    }

    fun onUiInvisible() {
        uiState = 2
        purgeDismissedNotifications()
        if (!isPartnerClient) {
            mPrioritizeRowUpdateState?.onUiInvisible()
        }
    }

    val isUiVisible: Boolean
        get() = uiState == 1

    fun notifyChangesSinceSnapshot(snapshot: List<TvRecommendation?>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return snapshot.size
            }

            override fun getNewListSize(): Int {
                return mSyncedList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return equals(snapshot[oldItemPosition], mSyncedList[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return snapshot[oldItemPosition] == mSyncedList[newItemPosition]
            }
        })
        val offset = nonNotifItemCount
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(offset + position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(offset + position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(offset + fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                this@NotificationsViewAdapter.notifyItemRangeChanged(
                    offset + position,
                    count,
                    payload
                )
            }
        })
    }

    fun postAllRowUpdates() {
        val snapshot: List<TvRecommendation?> = ArrayList(
            mSyncedList
        )
        mSyncedList.clear()
        mSyncedList.addAll(mMasterList)
        notifyChangesSinceSnapshot(snapshot)
        updateRowVisibility()
    }

    private fun masterListHasChanged() {
        if (isPartnerClient) {
            when (uiState) {
                0 -> {
                    postAllRowUpdates()
                    return
                }
                1 -> {
                    postAllRowUpdates()
                    return
                }
                else -> return
            }
        }
        when (uiState) {
            0 -> {
                postAllRowUpdates()
                return
            }
            1, 2 -> {
                mPrioritizeRowUpdateState?.onNewRowChange()
                return
            }
            else -> return
        }
    }

    val syncedList: MutableList<TvRecommendation?>
        get() = mSyncedList
    val masterList: MutableList<TvRecommendation?>
        get() = mMasterList

    protected fun onRecommendationsUpdate() {
        masterListHasChanged()
    }

    private fun indexOfMasterRecommendation(rec: TvRecommendation?): Int {
        for (i in mMasterList.indices) {
            if (equals(rec, mMasterList[i])) {
                return i
            }
        }
        return -1
    }

    private fun removeRecommendation(rec: TvRecommendation?) {
        val index = indexOfMasterRecommendation(rec)
        if (index != -1) {
            mMasterList.removeAt(index)
            masterListHasChanged()
            if (Log.isLoggable("NotifViewAdapter", Log.DEBUG)) {
                Log.d(
                    "NotifViewAdapter",
                    "Recommendation Removed from position" + (index + nonNotifItemCount) + ": " + rec
                )
            }
        }
    }

    protected open val nonNotifItemCount: Int
        get() = 0

    protected fun notifyNonNotifItemChanged(position: Int) {
        super.notifyItemChanged(position)
    }

    protected fun notifyNonNotifItemRemoved(position: Int) {
        super.notifyItemRemoved(position)
    }

    protected fun notifyNonNotifItemInserted(position: Int) {
        super.notifyItemInserted(position)
    }

    fun dump(prefix: String, writer: PrintWriter) {
        var prefix = prefix
        writer.println(prefix + javaClass.name)
        prefix = "$prefix  "
        writer.println(prefix + "mMasterList: " + mMasterList.toTypedArray().contentToString())
        writer.println(prefix + "mSyncedList: " + mSyncedList.toTypedArray().contentToString())
    }

}