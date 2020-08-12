package com.amazon.tv.firetv.leanbacklauncher.apps;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.recommendations.NotificationsServiceV4;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rockon999 on 3/25/18.
 */

public class RowPreferences {
    public static final String TAG = "AppPrefs";

    public static int[] getFavoriteRowConstraints(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        return new int[]{
                pref.getInt(context.getString(R.string.pref_min_favorites_rows), res.getInteger(R.integer.min_num_banner_rows)),
                pref.getInt(context.getString(R.string.pref_max_favorites_rows), res.getInteger(R.integer.max_num_banner_rows))
        };
    }

    public static int[] getAllAppsConstraints(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        return new int[]{
                pref.getInt(context.getString(R.string.pref_min_apps_rows), res.getInteger(R.integer.min_num_banner_rows)),
                pref.getInt(context.getString(R.string.pref_max_apps_rows), res.getInteger(R.integer.max_num_banner_rows))
        };
    }

    public static int[] getRowConstraints(AppCategory cat, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        switch (cat) {
            case GAME:
                return new int[]{
                        pref.getInt(context.getString(R.string.pref_min_games_rows), res.getInteger(R.integer.min_num_banner_rows)),
                        pref.getInt(context.getString(R.string.pref_max_games_rows), res.getInteger(R.integer.max_num_banner_rows))
                };
            case MUSIC:
                return new int[]{
                        pref.getInt(context.getString(R.string.pref_min_music_rows), res.getInteger(R.integer.min_num_banner_rows)),
                        pref.getInt(context.getString(R.string.pref_max_music_rows), res.getInteger(R.integer.max_num_banner_rows))
                };
            case VIDEO:
                return new int[]{
                        pref.getInt(context.getString(R.string.pref_min_videos_rows), res.getInteger(R.integer.min_num_banner_rows)),
                        pref.getInt(context.getString(R.string.pref_max_videos_rows), res.getInteger(R.integer.max_num_banner_rows))
                };
            default:
                return new int[]{0, 0};
        }
    }

    public static boolean areFavoritesEnabled(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_enable_favorites_row), true);
    }

    public static boolean setFavoritesEnabled(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(context.getString(R.string.pref_enable_favorites_row), value).apply();
        return true;
    }

    public static boolean areInputsEnabled(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_enable_inputs_row), false);  // true
    }

    public static boolean setInputsEnabled(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(context.getString(R.string.pref_enable_inputs_row), value).apply();
        return true;
    }

    public static boolean areRecommendationsEnabled(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_enable_recommendations_row), false); // true
    }

    public static boolean setRecommendationsEnabled(Context context, boolean value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 || context.getPackageManager().checkPermission("android.permission.WRITE_SECURE_SETTINGS", context.getPackageName()) != PackageManager.PERMISSION_DENIED) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            pref.edit().putBoolean(context.getString(R.string.pref_enable_recommendations_row), value).apply();
        } else {
            Toast.makeText(context, context.getString(R.string.recs_warning), Toast.LENGTH_LONG).show();
        }
        // request notifications access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (!manager.isNotificationListenerAccessGranted(new ComponentName(context, NotificationsServiceV4.class))) { // ComponentName
                // Open the permission page
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    // ignored
                }
            }
        }
        return true;
    }

    public static Set<AppCategory> getEnabledCategories(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<AppCategory> categories = new HashSet<>();

        if (pref.getBoolean(context.getString(R.string.pref_enable_games_row), true))
            categories.add(AppCategory.GAME);
        if (pref.getBoolean(context.getString(R.string.pref_enable_music_row), true))
            categories.add(AppCategory.MUSIC);
        if (pref.getBoolean(context.getString(R.string.pref_enable_videos_row), true))
            categories.add(AppCategory.VIDEO);

        return categories;
    }

    public static boolean setGamesEnabled(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(context.getString(R.string.pref_enable_games_row), value).apply();
        return true;
    }

    public static boolean setMusicEnabled(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(context.getString(R.string.pref_enable_music_row), value).apply();
        return true;
    }

    public static boolean setVideosEnabled(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(context.getString(R.string.pref_enable_videos_row), value).apply();
        return true;
    }

    public static boolean setFavoriteRowConstraints(Context context, int min, int max) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        pref.edit().putInt(context.getString(R.string.pref_min_favorites_rows), min).apply();
        pref.edit().putInt(context.getString(R.string.pref_max_favorites_rows), max).apply();
        return true;
    }

    public static boolean setFavoriteRowMin(Context context, int min) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        pref.edit().putInt(context.getString(R.string.pref_min_favorites_rows), min).apply();
        return true;
    }

    public static boolean setFavoriteRowMax(Context context, int max) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        pref.edit().putInt(context.getString(R.string.pref_max_favorites_rows), max).apply();
        return true;
    }

    public static boolean setAllAppsConstraints(Context context, int min, int max) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        pref.edit().putInt(context.getString(R.string.pref_min_apps_rows), min).apply();
        pref.edit().putInt(context.getString(R.string.pref_max_apps_rows), max).apply();
        return true;
    }

    public static boolean setAllAppsMin(Context context, int min) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        pref.edit().putInt(context.getString(R.string.pref_min_apps_rows), min).apply();
        return true;
    }

    public static boolean setAllAppsMax(Context context, int max) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        pref.edit().putInt(context.getString(R.string.pref_max_apps_rows), max).apply();
        return true;
    }

    public static boolean setRowConstraints(AppCategory cat, Context context, int min, int max) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        switch (cat) {
            case GAME:
                pref.edit().putInt(context.getString(R.string.pref_min_games_rows), min).apply();
                pref.edit().putInt(context.getString(R.string.pref_max_games_rows), max).apply();
            case MUSIC:
                pref.edit().putInt(context.getString(R.string.pref_min_music_rows), min).apply();
                pref.edit().putInt(context.getString(R.string.pref_max_music_rows), max).apply();
            case VIDEO:
                pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply();
                pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply();
        }
        return true;
    }

    public static boolean setRowMin(AppCategory cat, Context context, int min) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        switch (cat) {
            case GAME:
                pref.edit().putInt(context.getString(R.string.pref_min_games_rows), min).apply();
            case MUSIC:
                pref.edit().putInt(context.getString(R.string.pref_min_music_rows), min).apply();
            case VIDEO:
                pref.edit().putInt(context.getString(R.string.pref_min_videos_rows), min).apply();
        }
        return true;
    }

    public static boolean setRowMax(AppCategory cat, Context context, int max) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        switch (cat) {
            case GAME:
                pref.edit().putInt(context.getString(R.string.pref_max_games_rows), max).apply();
            case MUSIC:
                pref.edit().putInt(context.getString(R.string.pref_max_music_rows), max).apply();
            case VIDEO:
                pref.edit().putInt(context.getString(R.string.pref_max_videos_rows), max).apply();
        }
        return true;
    }

    public static int getAppsMax(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        return pref.getInt(context.getString(R.string.pref_max_apps), res.getInteger(R.integer.two_row_cut_off));
    }

    public static boolean setAppsMax(Context context, int max) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        pref.edit().putInt(context.getString(R.string.pref_max_apps), max).apply();
        return true;
    }

    public static int getBannersSize(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt("banner_size", 100);
    }

    public static boolean setBannersSize(Context context, int size) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (size > 49 && size < 201)
            pref.edit().putInt("banner_size", size).apply();
        return true;
    }

    public static int getCorners(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int targetCorners = context.getResources().getDimensionPixelOffset(R.dimen.banner_corner_radius);
        return pref.getInt("banner_corner_radius", targetCorners);
    }

    public static boolean setCorners(Context context, int radius) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        // if (radius > 0)
        pref.edit().putInt("banner_corner_radius", radius).apply();
        return true;
    }

    public static int getFrameWidth(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int targetStroke = context.getResources().getDimensionPixelSize(R.dimen.banner_frame_stroke);
        return pref.getInt("banner_frame_stroke", targetStroke);
    }

    public static boolean setFrameWidth(Context context, int width) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (width > 0)
            pref.edit().putInt("banner_frame_stroke", width).apply();
        return true;
    }

    public static int getFrameColor(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int targetColor = context.getResources().getColor(R.color.banner_focus_frame_color);
        return pref.getInt("banner_focus_frame_color", targetColor);
    }

    public static boolean setFrameColor(Context context, int color) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        // if (color > 0)
        pref.edit().putInt("banner_focus_frame_color", color).apply();
        return true;
    }

    public static float dimensionInDp(Context context, int dimensionInPixel) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel, context.getResources().getDisplayMetrics());
    }
}
