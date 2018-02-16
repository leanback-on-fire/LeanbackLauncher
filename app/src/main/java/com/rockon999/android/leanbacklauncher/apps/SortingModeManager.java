package com.rockon999.android.leanbacklauncher.apps;

import android.content.Context;
import android.preference.PreferenceManager;

public class SortingModeManager {

    public enum SortingMode {
        FIXED,
        RECENCY
    }

    public static SortingMode getSavedSortingMode(Context context) {
        return SortingMode.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("apps_ranker_sorting_mode", SortingModeManager.SortingMode.FIXED.toString())); // todo check this
    }

    public static void saveSortingMode(Context context, SortingMode mode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("apps_ranker_sorting_mode", mode.toString()).apply();
    }
}
