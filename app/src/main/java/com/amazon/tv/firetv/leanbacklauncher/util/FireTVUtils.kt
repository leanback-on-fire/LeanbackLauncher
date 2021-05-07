package com.amazon.tv.firetv.leanbacklauncher.util

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.amazon.tv.leanbacklauncher.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by rockon999 on 2/24/18.
 */
object FireTVUtils {
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName

    @JvmStatic
    fun startAppSettings(context: Context, packageName: String?) {
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", packageName, null)
            context.startActivity(intent)
        } catch (e: Exception) {
            val errors = StringWriter()
            e.printStackTrace(PrintWriter(errors))
            val errorReason = errors.toString()
            Log.d(TAG, "Failed to launch settings-activity: \n$errorReason")
        }
    }

    @JvmStatic
    fun isAmazonStoreInstalled(context: Context): Boolean {
        val localIntent = Intent()
        localIntent.setPackage("com.amazon.venezia")
        val packageManager = context.packageManager
        val activities = packageManager.queryIntentActivities(localIntent, 0)
        return activities.size > 0
    }

    @JvmStatic
    fun isPlayStoreInstalled(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getApplicationInfo("com.android.vending", 0).enabled
        } catch (unused: PackageManager.NameNotFoundException) {
            false
        }
    }

    @JvmStatic
    fun isLocalNotificationsEnabled(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getApplicationInfo("com.amazon.device.sale.service", 0).enabled
        } catch (unused: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isLauncherSettingsEnabled(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getApplicationInfo("com.amazon.tv.launcher", 0).enabled
        } catch (unused: PackageManager.NameNotFoundException) {
            false
        }
    }

    @JvmStatic
    fun openAppInPlayStore(context: Context, packageName: String?) {
        try {
            startActivity(
                context,
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")),
                null
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                context,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                ),
                null
            )
        }
    }

    @JvmStatic
    fun openAppInAmazonStore(context: Context, packageName: String?) {
        try {
            val localIntent = Intent()
            localIntent.setPackage("com.amazon.venezia")
            localIntent.component =
                ComponentName.unflattenFromString("com.amazon.venezia/com.amazon.venezia.details.AppDetailsActivity")
            localIntent.data = Uri.fromParts("application", packageName, null)
            localIntent.putExtra("asin", "")
            localIntent.putExtra("packageName", packageName)
            localIntent.putExtra("clickStreamReftag", AmazonStoreSpoofer.buildRefTag())
            localIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK // 32768(0x8000)
            context.startActivity(localIntent)
        } catch (e: Exception) {
            val errors = StringWriter()
            e.printStackTrace(PrintWriter(errors))
            val errorReason = errors.toString()
            Log.d(TAG, "Failed to launch store-activity: \n$errorReason")
        }
    }

    // KNOWN KEY: com.amazon.device.settings..PACKAGE_NAME
    // intent.putExtra("com.amazon.device.settings..PACKAGE_NAME", packageName);
    val notificationPreferencesIntent: Intent
        get() {
            val settingsAct =
                "com.amazon.tv.notificationcenter/com.amazon.tv.notificationcenter.settings.NotificationSettingsActivity"
            val intent = Intent()
            intent.setPackage("com.amazon.tv.notificationcenter")
            intent.component = ComponentName.unflattenFromString(settingsAct)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            // KNOWN KEY: com.amazon.device.settings..PACKAGE_NAME
            // intent.putExtra("com.amazon.device.settings..PACKAGE_NAME", packageName);
            return intent
        }

    val notificationCenterIntent: Intent
        get() {
            val settingsAct =
                "com.amazon.tv.notificationcenter/com.amazon.tv.notificationcenter.NotificationCenterActivity"
            val intent = Intent()
            intent.setPackage("com.amazon.tv.notificationcenter")
            intent.component = ComponentName.unflattenFromString(settingsAct)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }

    val systemSettingsIntent: Intent
        get() {
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }

    private object AmazonStoreSpoofer {
        const val SPOOFED_WIDGET = "CONTEXT_MENU"
        const val SPOOFED_PAGE = "UNKNOWN"
        fun buildRefTag(): String {
            return referenceTagPrefix + buildSpoofedReferenceTag(SPOOFED_WIDGET, SPOOFED_PAGE)
        }

        // this is "bueller" (the default) // todo actually "match" device.
        val referenceTagPrefix: String
            get() = "apps_e1_" // this is "bueller" (the default) // todo actually "match" device.

        fun buildSpoofedReferenceTag(widget: String, page: String): String {
            return widget + '_' + page
        }
    }
}