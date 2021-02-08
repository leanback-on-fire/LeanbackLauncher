package com.amazon.tv.firetv.leanbacklauncher.apps

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.recommendations.NotificationsServiceV4
import java.util.*

object RowPreferences {
    const val TAG = "AppPrefs"
    @JvmStatic
    fun getFavoriteRowConstraints(context: Context): IntArray {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        return intArrayOf(
                pref.getInt(context.getString(R.string.pref_min_favorites_rows), res.getInteger(R.integer.min_num_banner_rows)),
                pref.getInt(context.getString(R.string.pref_max_favorites_rows), res.getInteger(R.integer.max_num_banner_rows))
        )
    }

    @JvmStatic
    fun getAllAppsConstraints(context: Context): IntArray {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        return intArrayOf(
                pref.getInt(context.getString(R.string.pref_min_apps_rows), res.getInteger(R.integer.min_num_banner_rows)),
                pref.getInt(context.getString(R.string.pref_max_apps_rows), res.getInteger(R.integer.max_num_banner_rows))
        )
    }

    @JvmStatic
    fun getRowConstraints(cat: AppCategory?, context: Context): IntArray {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        return when (cat) {
            AppCategory.GAME -> intArrayOf(
                    pref.getInt(context.getString(R.string.pref_min_games_rows), res.getInteger(R.integer.min_num_banner_rows)),
                    pref.getInt(context.getString(R.string.pref_max_games_rows), res.getInteger(R.integer.max_num_banner_rows))
            )
            AppCategory.MUSIC -> intArrayOf(
                    pref.getInt(context.getString(R.string.pref_min_music_rows), res.getInteger(R.integer.min_num_banner_rows)),
                    pref.getInt(context.getString(R.string.pref_max_music_rows), res.getInteger(R.integer.max_num_banner_rows))
            )
            AppCategory.VIDEO -> intArrayOf(
                    pref.getInt(context.getString(R.string.pref_min_videos_rows), res.getInteger(R.integer.min_num_banner_rows)),
                    pref.getInt(context.getString(R.string.pref_max_videos_rows), res.getInteger(R.integer.max_num_banner_rows))
            )
            else -> intArrayOf(0, 0)
        }
    }

    @JvmStatic
    fun areFavoritesEnabled(context: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getBoolean(context.getString(R.string.pref_enable_favorites_row), true)
    }

    @JvmStatic
    fun setFavoritesEnabled(context: Context, value: Boolean): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().putBoolean(context.getString(R.string.pref_enable_favorites_row), value).apply()
        return true
    }

    @JvmStatic
    fun areInputsEnabled(context: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getBoolean(context.getString(R.string.pref_enable_inputs_row), false) // true
    }

    @JvmStatic
    fun setInputsEnabled(context: Context, value: Boolean): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().putBoolean(context.getString(R.string.pref_enable_inputs_row), value).apply()
        return true
    }

    @JvmStatic
    fun areRecommendationsEnabled(context: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getBoolean(context.getString(R.string.pref_enable_recommendations_row), false) // true
    }

    @JvmStatic
    fun setRecommendationsEnabled(context: Context, value: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 || context.packageManager.checkPermission("android.permission.WRITE_SECURE_SETTINGS", context.packageName) != PackageManager.PERMISSION_DENIED) {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            pref.edit().putBoolean(context.getString(R.string.pref_enable_recommendations_row), value).apply()
        } else {
            Toast.makeText(context, context.getString(R.string.recs_warning), Toast.LENGTH_LONG).show()
        }
        // request notifications access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!manager.isNotificationListenerAccessGranted(ComponentName(context, NotificationsServiceV4::class.java))) { // ComponentName
                // Open the permission page
                try {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // ignored
                }
            }
        }
        return true
    }

    @JvmStatic
    fun getEnabledCategories(context: Context): Set<AppCategory> {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val categories: MutableSet<AppCategory> = HashSet()
        if (pref.getBoolean(context.getString(R.string.pref_enable_games_row), true)) categories.add(AppCategory.GAME)
        if (pref.getBoolean(context.getString(R.string.pref_enable_music_row), true)) categories.add(AppCategory.MUSIC)
        if (pref.getBoolean(context.getString(R.string.pref_enable_videos_row), true)) categories.add(AppCategory.VIDEO)
        return categories
    }

    @JvmStatic
    fun setGamesEnabled(context: Context, value: Boolean): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().putBoolean(context.getString(R.string.pref_enable_games_row), value).apply()
        return true
    }

    @JvmStatic
    fun setMusicEnabled(context: Context, value: Boolean): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().putBoolean(context.getString(R.string.pref_enable_music_row), value).apply()
        return true
    }

    @JvmStatic
    fun setVideosEnabled(context: Context, value: Boolean): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().putBoolean(context.getString(R.string.pref_enable_videos_row), value).apply()
        return true
    }

    fun setFavoriteRowConstraints(context: Context, min: Int, max: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        pref.edit().putInt(context.getString(R.string.pref_min_favorites_rows), min).apply()
        pref.edit().putInt(context.getString(R.string.pref_max_favorites_rows), max).apply()
        return true
    }

    @JvmStatic
    fun setFavoriteRowMin(context: Context, min: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        pref.edit().putInt(context.getString(R.string.pref_min_favorites_rows), min).apply()
        return true
    }

    @JvmStatic
    fun setFavoriteRowMax(context: Context, max: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        pref.edit().putInt(context.getString(R.string.pref_max_favorites_rows), max).apply()
        return true
    }

    fun setAllAppsConstraints(context: Context, min: Int, max: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        pref.edit().putInt(context.getString(R.string.pref_min_apps_rows), min).apply()
        pref.edit().putInt(context.getString(R.string.pref_max_apps_rows), max).apply()
        return true
    }

    fun setAllAppsMin(context: Context, min: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        pref.edit().putInt(context.getString(R.string.pref_min_apps_rows), min).apply()
        return true
    }

    @JvmStatic
    fun setAllAppsMax(context: Context, max: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        pref.edit().putInt(context.getString(R.string.pref_max_apps_rows), max).apply()
        return true
    }

    fun setRowConstraints(cat: AppCategory?, context: Context, min: Int, max: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        when (cat) {
            AppCategory.GAME -> {
                pref.edit().putInt(context.getString(R.string.pref_min_games_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_games_rows), max).apply()
                pref.edit().putInt(context.getString(R.string.pref_min_music_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_music_rows), max).apply()
                pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply()
            }
            AppCategory.MUSIC -> {
                pref.edit().putInt(context.getString(R.string.pref_min_music_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_music_rows), max).apply()
                pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply()
            }
            AppCategory.VIDEO -> {
                pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply()
            }
        }
        return true
    }

    @JvmStatic
    fun setRowMin(cat: AppCategory?, context: Context, min: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        when (cat) {
            AppCategory.GAME -> {
                pref.edit().putInt(context.getString(R.string.pref_min_games_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_min_music_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply()
            }
            AppCategory.MUSIC -> {
                pref.edit().putInt(context.getString(R.string.pref_min_music_rows), min).apply()
                pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply()
            }
            AppCategory.VIDEO -> pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply()
        }
        return true
    }

    @JvmStatic
    fun setRowMax(cat: AppCategory?, context: Context, max: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        when (cat) {
            AppCategory.GAME -> {
                pref.edit().putInt(context.getString(R.string.pref_max_games_rows), max).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_music_rows), max).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply()
            }
            AppCategory.MUSIC -> {
                pref.edit().putInt(context.getString(R.string.pref_max_music_rows), max).apply()
                pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply()
            }
            AppCategory.VIDEO -> pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply()
        }
        return true
    }

    @JvmStatic
    fun getAppsMax(context: Context): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        return pref.getInt(context.getString(R.string.pref_max_apps), res.getInteger(R.integer.two_row_cut_off))
    }

    @JvmStatic
    fun setAppsMax(context: Context, max: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val res = context.resources
        pref.edit().putInt(context.getString(R.string.pref_max_apps), max).apply()
        return true
    }

    @JvmStatic
    fun getBannersSize(context: Context?): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getInt("banner_size", 100)
    }

    @JvmStatic
    fun setBannersSize(context: Context?, size: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (size > 49 && size < 201) pref.edit().putInt("banner_size", size).apply()
        return true
    }

    @JvmStatic
    fun getCorners(context: Context): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val targetCorners = context.resources.getDimensionPixelOffset(R.dimen.banner_corner_radius)
        return pref.getInt("banner_corner_radius", targetCorners)
    }

    @JvmStatic
    fun setCorners(context: Context?, radius: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        // if (radius > 0)
        pref.edit().putInt("banner_corner_radius", radius).apply()
        return true
    }

    @JvmStatic
    fun getFrameWidth(context: Context): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val targetStroke = context.resources.getDimensionPixelSize(R.dimen.banner_frame_stroke)
        return pref.getInt("banner_frame_stroke", targetStroke)
    }

    @JvmStatic
    fun setFrameWidth(context: Context?, width: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (width > 0) pref.edit().putInt("banner_frame_stroke", width).apply()
        return true
    }

    @JvmStatic
    fun getFrameColor(context: Context): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val targetColor = context.resources.getColor(R.color.banner_focus_frame_color)
        return pref.getInt("banner_focus_frame_color", targetColor)
    }

    @JvmStatic
    fun setFrameColor(context: Context?, color: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        // if (color > 0)
        pref.edit().putInt("banner_focus_frame_color", color).apply()
        return true
    }

    fun dimensionInDp(context: Context, dimensionInPixel: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel.toFloat(), context.resources.displayMetrics)
    }
}