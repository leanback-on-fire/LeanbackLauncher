package com.amazon.tv.leanbacklauncher.wallpaper

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.util.Util.getDisplayMetrics
import java.io.File
import java.io.IOException

class WallpaperInstaller private constructor(context: Context) {
    private val mContext: Context = context
    private var mInstallationPending = false
    private var mInstallingWallpaper = false
    private val mWallpaperInstalled: Boolean
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName
//    private val bgScope = CoroutineScope(Dispatchers.IO)

    class WallpaperChangedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.intent.action.WALLPAPER_CHANGED"))
                getInstance(context)!!.onWallpaperChanged()
        }
    }

    fun installWallpaperIfNeeded() {
        if (!mWallpaperInstalled && !mInstallationPending) {
            mInstallationPending = true
// FIXME
//            bgScope.launch {
//                installWallpaper()
//            }
//            object : AsyncTask<Any?, Any?, Any?>() {
//                override fun doInBackground(vararg params: Any?): Any? {
//                    installWallpaper()
//                    return null
//                }
//            }.execute()
        }
    }

    // user background
    // custom background
    // default background
    val wallpaperBitmap: Bitmap
        get() {
            val resources = mContext.resources
            val prefs = PreferenceManager.getDefaultSharedPreferences(
                mContext
            )
            var systemBg: Drawable? = null
            // user background
            if (prefs.getString("wallpaper_image", "")!!.isNotEmpty()) {
                Log.d(TAG, "wallpaper image " + prefs.getString("wallpaper_image", ""))
                systemBg = Drawable.createFromPath(prefs.getString("wallpaper_image", ""))
            }
            // custom background
            if (systemBg == null) {
                val file = File(mContext.getExternalFilesDir(null), "background.jpg")
                if (file.canRead()) {
                    Log.d(TAG, "wallpaper path ${file.path}")
                    systemBg = Drawable.createFromPath(file.path)
                }
            }
            // default background
            if (systemBg == null) systemBg =
                ResourcesCompat.getDrawable(resources, R.drawable.bg_default, null)
            // ContextCompat.getDrawable(mContext, R.drawable.bg_default)
            val intrinsicWidth = systemBg?.intrinsicWidth ?: 1920
            val intrinsicHeight = systemBg?.intrinsicHeight ?: 1080
            var wallpaperWidth = getDisplayMetrics(mContext).widthPixels
            var wallpaperHeight = wallpaperWidth * intrinsicHeight / intrinsicWidth
            // createBitmap fix (width and height must be > 0)
            if (wallpaperWidth <= 0 || wallpaperHeight <= 0) {
                wallpaperWidth = 1
                wallpaperHeight = 1
            }
            val bitmap =
                Bitmap.createBitmap(wallpaperWidth, wallpaperHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.BLACK)
            systemBg?.setBounds(0, 0, wallpaperWidth, wallpaperHeight)
            systemBg?.draw(canvas)
            val maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_protection)
            val maskDrawable = BitmapDrawable(resources, maskBitmap)
            maskDrawable.tileModeX = TileMode.REPEAT
            maskDrawable.tileModeY = TileMode.CLAMP
            maskDrawable.setBounds(0, 0, wallpaperWidth, wallpaperHeight)
            maskDrawable.draw(canvas)
            return bitmap
        }

    private fun installWallpaper() {
        val e: Throwable
        try {
            Log.i(TAG, "Installing wallpaper")
            mInstallingWallpaper = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val wallpaperManager = WallpaperManager.getInstance(mContext)
                if (wallpaperManager.isWallpaperSupported && wallpaperManager.isSetWallpaperAllowed) {
                    val bitmap = wallpaperBitmap
                    wallpaperManager.suggestDesiredDimensions(bitmap.width, bitmap.height)
                    wallpaperManager.setBitmap(bitmap)
                }
            }
            return
        } catch (e2: IOException) {
            e = e2
        } catch (e3: OutOfMemoryError) {
            e = e3
        }
        Log.e(TAG, "Cannot install wallpaper", e)
        mInstallingWallpaper = false
    }

    private fun onWallpaperChanged() {
        val edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
        if (mInstallingWallpaper) {
            edit.putInt("wallpaper_version", 5)
            mInstallingWallpaper = false
        } else {
            edit.remove("wallpaper_version")
        }
        edit.apply()
    }

    companion object {
        private var instance: WallpaperInstaller? = null

        @JvmStatic
        fun getInstance(context: Context): WallpaperInstaller? {
            if (instance == null) {
                synchronized(WallpaperInstaller::class.java) {
                    if (instance == null) {
                        instance = WallpaperInstaller(context.applicationContext)
                    }
                }
            }
            return instance
        }
    }

    init {
        var z = false
        if (PreferenceManager.getDefaultSharedPreferences(mContext)
                .getInt("wallpaper_version", 0) == 5
        ) {
            z = true
        }
        mWallpaperInstalled = z
    }
}