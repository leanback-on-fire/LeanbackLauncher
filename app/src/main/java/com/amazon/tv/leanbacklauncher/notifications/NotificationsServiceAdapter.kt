package com.amazon.tv.leanbacklauncher.notifications

import android.app.PendingIntent
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.RemoteException
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.leanbacklauncher.notifications.NotificationUtils.equals
import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient
import com.amazon.tv.leanbacklauncher.trace.AppTrace
import com.amazon.tv.leanbacklauncher.trace.AppTrace.TraceTag
import com.amazon.tv.tvrecommendations.IRecommendationsClient
import com.amazon.tv.tvrecommendations.IRecommendationsService
import com.amazon.tv.tvrecommendations.TvRecommendation
import java.lang.ref.WeakReference
import java.util.*

abstract class NotificationsServiceAdapter<VH : RecyclerView.ViewHolder?>(
    context: Context,
    minUpdateTime: Long,
    maxUpdateTime: Long
) : NotificationsViewAdapter<VH>(context, minUpdateTime, maxUpdateTime) {
    private val mBackgroundHandler: Handler

    @JvmField
    protected var mBoundService: IRecommendationsService? = null
    private val mComparator = NotifComparator()
    private var mNotificationListenerTraceToken: TraceTag? = null
    private val mNotificationsListener: NotificationsListener
    private val mRecommendationClient: SwitchingRecommendationsClient
    private val mRecommendationComparator: RecommendationComparator
    private var mServiceConnectTraceToken: TraceTag? = null

    init {
        synchronized(this) {
            if (sHandlerThread == null) {
                sHandlerThread = HandlerThread(javaClass.name)
                sHandlerThread?.start()
            }
        }
        mBackgroundHandler = Handler(sHandlerThread!!.looper)
        mRecommendationComparator = object : RecommendationComparator {
            override fun compare(o1: TvRecommendation?, o2: TvRecommendation?): Int {
                if (o1 == null) return 1
                if (o2 == null) return -1

                // todo
                if (o1.contentImage == null && o2.contentImage != null) {
                    return -1
                } else if (o1.contentImage != null && o2.contentImage == null) {
                    return 1
                }
                return o1.score.compareTo(o2.score)
            }
        }
        mRecommendationClient =
            object : SwitchingRecommendationsClient(context.applicationContext) {
                override fun onConnected(service: IRecommendationsService) {
                    onServiceConnected(service)
                }

                override fun onDisconnected() {
                    onServiceDisconnected()
                }
            }
        mNotificationsListener = NotificationsListener(RecommendationsHandler(this))
    }

    companion object {
        private var sHandlerThread: HandlerThread? = null
        private fun getSortKey(rec: TvRecommendation?): Double {
            val value = -1.0
            return try {
                java.lang.Double.valueOf(rec!!.sortKey).toDouble()
            } catch (e: NullPointerException) {
                value
            } catch (e2: NumberFormatException) {
                value
            }
        }
    }

    interface RecommendationComparator : Comparator<TvRecommendation?>
    private class NotifComparator : Comparator<TvRecommendation> {
        fun compare(lhsSortKey: Double, rhsSortKey: Double): Int {
            val sort = lhsSortKey - rhsSortKey
            if (sort > 0.0) {
                return 1
            }
            return if (sort < 0.0) -1 else 0
        }

        override fun compare(lhs: TvRecommendation, rhs: TvRecommendation): Int {
            return compare(getSortKey(lhs), getSortKey(rhs))
        }
    }

    private class NotificationsListener(private val mHandler: RecommendationsHandler) :
        IRecommendationsClient.Stub() {
        override fun onServiceStatusChanged(isReady: Boolean) {
            val recommendationsHandler = mHandler
            val recommendationsHandler2 = mHandler
            val i: Int = if (isReady) {
                1
            } else {
                0
            }
            recommendationsHandler.sendMessage(recommendationsHandler2.obtainMessage(4, i, 0))
        }

        override fun onClearRecommendations(reason: Int) {
            mHandler.sendMessage(mHandler.obtainMessage(2, reason, 0))
        }

        override fun onAddRecommendation(rec: TvRecommendation) {
            mHandler.sendMessage(mHandler.obtainMessage(0, rec))
        }

        override fun onUpdateRecommendation(rec: TvRecommendation) {
            mHandler.sendMessage(mHandler.obtainMessage(3, rec))
        }

        override fun onRemoveRecommendation(rec: TvRecommendation) {
            mHandler.sendMessage(mHandler.obtainMessage(1, rec))
        }

        override fun onRecommendationBatchStart() {}
        override fun onRecommendationBatchEnd() {}
    }

    private class RecommendationsHandler(adapter: NotificationsServiceAdapter<*>?) :
        Handler() {
        private val mNotificationsServiceAdapter =
            WeakReference<NotificationsServiceAdapter<*>?>(adapter)

        override fun handleMessage(msg: Message) {
            val adapter = mNotificationsServiceAdapter.get()
            if (adapter != null) {
                when (msg.what) {
                    0 -> {
                        adapter.addRecommendation(msg.obj as TvRecommendation)
                        return
                    }
                    1 -> {
                        adapter.removeRecommendation(msg.obj as TvRecommendation)
                        return
                    }
                    2 -> {
                        adapter.clearRecommendations(msg.arg1)
                        return
                    }
                    3 -> {
                        adapter.updateRecommendation((msg.obj as TvRecommendation))
                        return
                    }
                    4 -> {
                        adapter.serviceStatusChanged(msg.arg1 != 0)
                        return
                    }
                    else -> {
                    }
                }
            }
        }

    }

    abstract override val isPartnerClient: Boolean
    protected open fun onServiceConnected(service: IRecommendationsService?) {
        if (mServiceConnectTraceToken != null) {
            AppTrace.endAsyncSection(mServiceConnectTraceToken)
            mServiceConnectTraceToken = null
        }
        mBoundService = service
        mBackgroundHandler.post(Runnable {
            val boundService = mBoundService
            if (boundService != null) {
                try {
                    if (isPartnerClient) {
                        mNotificationListenerTraceToken =
                            AppTrace.beginAsyncSection("registerPartnerRowClient")
                        boundService.registerPartnerRowClient(mNotificationsListener, 1)
                        return@Runnable
                    }
                    mNotificationListenerTraceToken =
                        AppTrace.beginAsyncSection("registerRecommendationsClient")
                    boundService.registerRecommendationsClient(mNotificationsListener, 1)
                } catch (e: RemoteException) {
                    Log.e("NotifServiceAdapter", "Exception", e)
                }
            }
        })
    }

    protected open fun serviceStatusChanged(isReady: Boolean) {
        if (Log.isLoggable("NotifServiceAdapter", Log.DEBUG)) {
            Log.d("NotifServiceAdapter", "Notification Service Status changed. Ready = $isReady")
        }
    }

    protected open fun onServiceDisconnected() {
        mBoundService = null
    }

    open fun onInitUi() {
        if (Log.isLoggable("NotifServiceAdapter", Log.DEBUG)) {
            Log.d("NotifServiceAdapter", "onInitUi()")
        }
        mServiceConnectTraceToken = AppTrace.beginAsyncSection("connectToRecService")
        mRecommendationClient.connect()
    }

    private fun getPackageName(intent: PendingIntent?): String? {
        return intent?.creatorPackage
    }

    protected fun onActionRecommendationImpression(intent: PendingIntent?, group: String?) {
        mBackgroundHandler.post {
            val boundService = mBoundService
            if (boundService != null) {
                try {
                    boundService.onActionRecommendationImpression(getPackageName(intent), group)
                } catch (e: RemoteException) {
                    Log.e("NotifServiceAdapter", "RemoteException", e)
                }
            }
        }
    }

    protected fun onActionRecommendationClick(intent: PendingIntent?, group: String?) {
        mBackgroundHandler.post {
            val boundService = mBoundService
            if (boundService != null) {
                try {
                    boundService.onActionOpenRecommendation(getPackageName(intent), group)
                } catch (e: RemoteException) {
                    Log.e("NotifServiceAdapter", "RemoteException", e)
                }
            }
        }
    }

    fun reregisterListener() {
        onStopUi()
        onInitUi()
    }

    open fun onStopUi() {
        mBackgroundHandler.post {
            val boundService = mBoundService
            if (boundService != null) {
                try {
                    if (isPartnerClient) {
                        boundService.unregisterPartnerRowClient(mNotificationsListener)
                    } else {
                        boundService.unregisterRecommendationsClient(mNotificationsListener)
                    }
                } catch (e: RemoteException) {
                    Log.e("NotifServiceAdapter", "Error unregistering notifications client", e)
                }
            }
            mRecommendationClient.disconnect()
        }
    }

    override fun onRecommendationDismissed(tvRecommendation: TvRecommendation?) {
        mBackgroundHandler.post {
            val boundService = mBoundService
            if (boundService != null) {
                try {
                    boundService.dismissRecommendation(tvRecommendation!!.key)
                } catch (e: RemoteException) {
                    Log.e("NotifServiceAdapter", "Exception while cancelling notification", e)
                }
            }
        }
    }

    protected open fun onNotificationClick(intent: PendingIntent?, group: String?) {}
    protected fun serviceOnActionOpenLaunchPoint(component: String?, group: String?) {
        mBackgroundHandler.post {
            val boundService = mBoundService
            if (boundService != null) {
                try {
                    boundService.onActionOpenLaunchPoint(component, group)
                } catch (e: RemoteException) {
                    Log.e("NotifServiceAdapter", "Exception while cancelling notification", e)
                }
            }
        }
    }

    protected open fun onNewRecommendation(rec: TvRecommendation) {
        val map = rec.contentImage
        val pkg = rec.packageName
        // int bytes = map.getByteCount();
    }

    protected open fun onRecommendationRemoved(rec: TvRecommendation?) {}

    private fun insertPartnerRow(rec: TvRecommendation) {
        val masterList = masterList
        var position = masterList.size
        val sortKey = getSortKey(rec)
        for (i in masterList.indices) {
            if (mComparator.compare(
                    sortKey, getSortKey(
                        masterList[i]
                    )
                ) < 0
            ) {
                position = i
                break
            }
        }
        masterList.add(position, rec)
    }

    private fun insertRecommendationsRow(rec: TvRecommendation) {
        val masterList = masterList
        var position = masterList.size
        for (i in masterList.indices) {
            if (mRecommendationComparator.compare(masterList[i], rec) < 0) {
                position = i
                break
            }
        }
        masterList.add(position, rec)
    }

    private fun addRecommendation(rec: TvRecommendation?) {
        if (rec == null) return  // todo
        if (mNotificationListenerTraceToken != null) {
            AppTrace.endAsyncSection(mNotificationListenerTraceToken)
            mNotificationListenerTraceToken = null
        }
        AppTrace.beginSection("addRecommendation")
        try {
            if (isPartnerClient) {
                insertPartnerRow(rec)
            } else {
                insertRecommendationsRow(rec)
            }
            onRecommendationsUpdate()
            onNewRecommendation(rec)
        } finally {
            AppTrace.endSection()
        }
    }

    private fun removeRecommendation(rec: TvRecommendation?) {
        if (rec == null) return  // TODO
        val masterList = masterList
        var found = false
        val size = masterList.size
        var index = 0
        while (index < size) {
            if (equals(masterList[index], rec)) {
                found = true
                break
            }
            index++
        }
        if (found) {
            masterList.removeAt(index)
            onRecommendationsUpdate()
            onRecommendationRemoved(rec)
        }
    }

    private fun clearRecommendations(reason: Int) {
        masterList.clear()
        onRecommendationsUpdate()
        super.onClearRecommendations(reason)
    }

    private fun updateRecommendation(rec: TvRecommendation) {
        val masterList = masterList
        var found = false
        val size = masterList.size
        var index = 0
        while (index < size) {
            if (equals(masterList[index], rec)) {
                found = true
                break
            }
            index++
        }
        if (found) {
            masterList.removeAt(index)
            if (isPartnerClient) {
                insertPartnerRow(rec)
            } else {
                insertRecommendationsRow(rec)
            }
            onRecommendationsUpdate()
            return
        }
        addRecommendation(rec)
    }

}