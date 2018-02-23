package com.rockon999.android.leanbacklauncher.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.R;

import java.util.HashSet;
import java.util.Set;

public class AppsPreferences {

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
                return null;
        }
    }

    public enum SortingMode {
        FIXED,
        RECENCY;

        public static SortingMode fromString(String s) {
            if ("FIXED".equalsIgnoreCase(s)) {
                return FIXED;
            } else if ("RECENCY".equalsIgnoreCase(s)) {
                return RECENCY;
            }

            Log.d(AppsPreferences.TAG, "Warning, could not find: " + s + " as a sorting mode. Defaulting to fixed.");

            return FIXED;
        }
    }

    public static SortingMode getSavedSortingMode(Context context) {
        return SortingMode.fromString(PreferenceManager.getDefaultSharedPreferences(context).getString("apps_ranker_sorting_mode", AppsPreferences.SortingMode.FIXED.toString()));
    }

    public static void saveSortingMode(Context context, SortingMode mode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("apps_ranker_sorting_mode", mode.toString()).apply();
    }

    public static boolean areFavoritesEnabled(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_enable_favorites_row), true);
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

    public static final String TAG = "AppPrefs";
}
