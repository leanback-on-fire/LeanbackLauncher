package com.rockon999.android.firetv.leanbacklauncher.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.MainActivity;
import com.rockon999.android.leanbacklauncher.R;

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

    public static int[] getRowConstraints(AppCategory game, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        Resources res = context.getResources();

        switch (game) {
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

    public static boolean areInputsEnabled(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_enable_inputs_row), false);
    }

    public static Set<AppCategory> getEnabledCategories(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<AppCategory> categories = new HashSet<>();

        if (pref.getBoolean(context.getString(R.string.pref_enable_games_row), false)) {
            categories.add(AppCategory.GAME);
        } else if (pref.getBoolean(context.getString(R.string.pref_enable_music_row), false)) {
            categories.add(AppCategory.MUSIC);
        } else if (pref.getBoolean(context.getString(R.string.pref_enable_videos_row), false)) {
            categories.add(AppCategory.VIDEO);
        }

        return categories;
    }

    public static boolean areRecommendationsEnabled(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_enable_recommendations_row), true);

    }
}
