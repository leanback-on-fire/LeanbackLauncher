package com.amazon.tv.leanbacklauncher.recommendations

import android.service.notification.StatusBarNotification
import android.util.Log
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.tvrecommendations.service.BaseNotificationsService

class NotificationsServiceV4 :
    BaseNotificationsService(false, GservicesRankerParameters.Factory()) {
    private val mDelegate: NotificationServiceDelegate? = null
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName

    interface NotificationServiceDelegate {
        fun onFetchExistingNotifications(statusBarNotificationArr: Array<StatusBarNotification>?)
        fun onNotificationPosted(statusBarNotification: StatusBarNotification?)
        fun onNotificationRemoved(statusBarNotification: StatusBarNotification?)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        if (BuildConfig.DEBUG) Log.d(TAG, "onListenerConnected()")
    }

    override fun onFetchExistingNotifications(notifications: Array<StatusBarNotification>) {
        if (isEnabled) {
            super.onFetchExistingNotifications(notifications)
            mDelegate?.onFetchExistingNotifications(notifications)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (isEnabled) {
            super.onNotificationPosted(sbn)
            mDelegate?.onNotificationPosted(sbn)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (isEnabled) {
            super.onNotificationRemoved(sbn)
            mDelegate?.onNotificationRemoved(sbn)
        }
    }

    private val isEnabled: Boolean
        get() = true
}