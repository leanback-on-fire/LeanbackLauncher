package com.amazon.tv.leanbacklauncher.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import com.amazon.tv.leanbacklauncher.MainActivity
import com.amazon.tv.leanbacklauncher.R

object Util {
    @JvmStatic
    val searchIntent: Intent
        @SuppressLint("WrongConstant")
        get() = Intent("android.intent.action.ASSIST").addFlags(270532608)

    fun isAmazonDev(context: Context): Boolean {
        return context.packageManager.hasSystemFeature("amazon.hardware.fire_tv")
    }

    @JvmStatic
    fun isContentUri(uriString: String?): Boolean {
        return if (TextUtils.isEmpty(uriString)) {
            false
        } else isContentUri(Uri.parse(uriString))
    }

    @JvmStatic
    fun isConfirmKey(keyCode: Int): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_X, KeyEvent.KEYCODE_NUMPAD_ENTER -> true
            else -> false
        }
    }

    fun isContentUri(uri: Uri): Boolean {
        return "content" == uri.scheme || "file" == uri.scheme
    }

    fun isPackageEnabled(context: Context, packageName: String?): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName!!, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isPackagePresent(pkgMan: PackageManager, packageName: String?): Boolean {
        return try {
            pkgMan.getApplicationInfo(packageName!!, 0) != null
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isSystemApp(context: Context, packageName: String?): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName!!, 0).flags and 1 != 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isUninstallAllowed(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            context.packageManager.checkPermission("android.permission.REQUEST_DELETE_PACKAGES", context.packageName) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    @JvmStatic
    fun initialRankingApplied(ctx: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("launcher_oob_ranking_marker", false)
    }

    @JvmStatic
    fun setInitialRankingAppliedFlag(ctx: Context?, applied: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("launcher_oob_ranking_marker", applied).apply()
    }

    private fun startActivitySafely(context: Context, intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (t: Throwable) {
            Log.e("LeanbackLauncher", "Could not launch intent", t)
            Toast.makeText(context, R.string.failed_launch, Toast.LENGTH_SHORT).show()
            false
        }
    }

    @SuppressLint("WrongConstant")
    @JvmStatic
    @Throws(SendIntentException::class)
    fun startActivity(context: Context, intent: PendingIntent) {
        context.startIntentSender(intent.intentSender, null, 268435456, 268435456, 0)
    }

    // refresh home broadcast
    fun refreshHome(context: Context) {
        val Broadcast = Intent(MainActivity::class.java.name) // ACTION
        Broadcast.putExtra("RefreshHome", true)
        context.sendBroadcast(Broadcast)
    }

    fun getInstallTimeForPackage(context: Context, pkgName: String?): Long {
        pkgName?.let { name ->
            val pkgMan = context.packageManager
            pkgMan?.let {
                try {
                    val info = it.getPackageInfo(name, 0)
                    if (info != null) {
                        return info.firstInstallTime
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            Log.v("LeanbackLauncher", "Couldn't find install time for $name assuming it's right now")
        }
        return System.currentTimeMillis()
    }

    fun getWidgetId(ctx: Context?): Int {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt("widget_id", 0)
    }

    fun getWidgetComponentName(ctx: Context?): ComponentName? {
        val name = PreferenceManager.getDefaultSharedPreferences(ctx).getString("widget_component_name", null)
        return if (TextUtils.isEmpty(name)) {
            null
        } else ComponentName.unflattenFromString(name!!)
    }

    fun setWidget(ctx: Context?, id: Int, name: ComponentName?) {
        if (id == 0 || name == null) {
            clearWidget(ctx)
        } else {
            PreferenceManager.getDefaultSharedPreferences(ctx).edit().putInt("widget_id", id).putString("widget_component_name", name.flattenToString()).apply()
        }
    }

    fun clearWidget(ctx: Context?) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().remove("widget_id").remove("widget_component_name").apply()
    }

    @JvmStatic
    fun getSizeCappedBitmap(image: Bitmap?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (image == null) {
            return null
        }
        val imgWidth = image.width
        val imgHeight = image.height
        if (imgWidth <= maxWidth && imgHeight <= maxHeight || imgWidth <= 0 || imgHeight <= 0) {
            return image
        }
        val scale = Math.min(1.0f, maxHeight.toFloat() / imgHeight.toFloat())
        if (scale.toDouble() >= 1.0 && imgWidth <= maxWidth) {
            return image
        }
        val deltaW = Math.max((imgWidth.toFloat() * scale).toInt() - maxWidth, 0).toFloat() / scale
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        val newImage = Bitmap.createBitmap(image, (deltaW / 2.0f).toInt(), 0, (imgWidth.toFloat() - deltaW).toInt(), imgHeight, matrix, true)
        return newImage ?: image
    }

    @JvmStatic
    fun startSearchActivitySafely(context: Context, intent: Intent, deviceId: Int, isKeyboardSearch: Boolean): Boolean {
        intent.putExtra("android.intent.extra.ASSIST_INPUT_DEVICE_ID", deviceId)
        intent.putExtra("search_type", if (isKeyboardSearch) 2 else 1)
        return startActivitySafely(context, intent)
    }

    @JvmStatic
    fun startSearchActivitySafely(context: Context, intent: Intent, isKeyboardSearch: Boolean): Boolean {
        intent.putExtra("search_type", if (isKeyboardSearch) 2 else 1)
        return startActivitySafely(context, intent)
    }

    @JvmStatic
    fun playErrorSound(context: Context) {
        (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).playSoundEffect(9)
    }

    @JvmStatic
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val metrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(metrics)
        return metrics
    }

    fun isInTouchExploration(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return am.isEnabled && am.isTouchExplorationEnabled
    }
}