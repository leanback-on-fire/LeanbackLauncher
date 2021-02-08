package com.amazon.tv.firetv.tvrecommendations

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.amazon.tv.leanbacklauncher.MainActivity
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.recommendations.NotificationsServiceV4
import com.amazon.tv.leanbacklauncher.util.Util
import java.util.*

class NotificationListenerMonitor : Service() {
    private var mReconnectAttempts = 0
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Notification listener monitor created.")
        if (!listenerIsRunning()) {
            ensureNotificationPermissions(applicationContext)
            toggleNotificationListenerService()
            val timer = arrayOf(Timer())
            timer[0].schedule(object : TimerTask() {
                override fun run() {
                    if (!listenerIsRunning() && mReconnectAttempts < MAXIMUM_RECONNECT_ATTEMPTS) {
                        ensureNotificationPermissions(applicationContext)
                        toggleNotificationListenerService()
                        mReconnectAttempts++
                    } else {
                        Log.d(TAG, "Exit Notification listener monitor. Max 15 attempts reached.")
                        timer[0].cancel()
                    }
                }
            }, 5000, 5000)
        }
    }

    private fun listenerIsRunning(): Boolean {
        val notificationListenerComp = ComponentName(this, NotificationsServiceV4::class.java)
        Log.v(TAG, "Ensuring the notification listener is running: $notificationListenerComp")
        var running = false
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Int.MAX_VALUE)
        if (services == null) {
            Log.w(TAG, "No running services found. Aborting listener monitoring.")
            return false
        }
        for (service in services) {
            if (notificationListenerComp == service.service) {
                Log.w(TAG, "Ensuring Notification Listener { PID: " + service.pid + " | currentPID: " + Process.myPid() + " | clientPackage: " + service.clientPackage + " | clientCount: " + service.clientCount
                        + " | clientLabel: " + if (service.clientLabel == 0) "0" else "(" + resources.getString(service.clientLabel) + ")}")
                if (service.pid == Process.myPid()) {
                    running = true
                }
            }
        }
        if (running) {
            Log.d(TAG, "Listener is running!")
            return true
        }
        Log.d(TAG, "The listener has been killed... Attempting to start.")
        return false
    }

    private fun toggleNotificationListenerService() {
        Log.d(TAG, "Toggling notification listener...")
        val thisComponent = ComponentName(this, NotificationsServiceV4::class.java)
        val pm = packageManager
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
    }

    private fun ensureNotificationPermissions(context: Context) {
        Log.d(TAG, "Checking notify perms...")
        if (context.packageManager.checkPermission("android.permission.WRITE_SECURE_SETTINGS", context.packageName) != PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Perms: granted")
            var listeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            val component = ComponentName(context, NotificationsServiceV4::class.java).flattenToShortString()
            val list: Array<String?> = listeners?.split("\\s*:\\s*")?.toTypedArray()
                    ?: arrayOfNulls(0)
            var enabled = false
            for (equals in list) {
                if (TextUtils.equals(equals, component)) {
                    enabled = true
                    break
                }
            }
            if (!enabled) {
                listeners = if (listeners == null || listeners.length == 0) {
                    component
                } else {
                    "$listeners:$component"
                }
                Log.d(TAG, "Perms: enabled")
            } else {
                Log.d(TAG, "Perms: (already) enabled")
            }
            // register notification listener
            Settings.Secure.putString(context.contentResolver, "enabled_notification_listeners", listeners)
        } else {
            Log.d(TAG, "Perms: denied")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val CHANNEL_ID = "com.amazon.tv.leanbacklauncher.recommendations.NotificationsServiceV4"
        val CHANNEL_NAME = "LeanbackOnFire"
        val NOTIFICATION_ID = 1111
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel: NotificationChannel? = null
            notificationChannel = NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(CHANNEL_NAME)
                .setContentText(resources.getString(R.string.notification_text))
        if (Util.isAmazonDev(this)) builder.setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_notification))
        val notificationIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, builder.build())
        startForeground(NOTIFICATION_ID, builder.build())
        return START_STICKY
    }

    companion object {
        private const val TAG = "NotifyListenerMonitor"
        private const val MAXIMUM_RECONNECT_ATTEMPTS = 15
    }
}