package com.rockchips.android.leanbacklauncher.apps;

import android.content.Context;
import android.preference.PreferenceManager;
import com.rockchips.android.leanbacklauncher.util.Partner;

public class SortingModeManager {

    public enum SortingMode {
        FIXED,
        RECENCY
    }

    public static SortingMode getSavedSortingMode(Context context) {
        return SortingMode.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("apps_ranker_sorting_mode", Partner.get(context).getAppSortingMode().toString()));
    }

    public static void saveSortingMode(Context context, SortingMode mode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("apps_ranker_sorting_mode", mode.toString()).apply();
    }
}
