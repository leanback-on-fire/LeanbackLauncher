package com.rockon999.android.leanbacklauncher.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class AppsPreferences {

    public enum SortingMode {
        FIXED,
        RECENCY
    }

    public static SortingMode getSavedSortingMode(Context context) {
        return SortingMode.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("apps_ranker_sorting_mode", AppsPreferences.SortingMode.FIXED.toString())); // todo check this
    }

    public static void saveSortingMode(Context context, SortingMode mode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("apps_ranker_sorting_mode", mode.toString()).apply();
    }

    public static Set<String> getEnabledCategories(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = new HashSet<String>();
        set.add("FAVORITE");
        return pref.getStringSet("apps_rows_enabled_categories", set);
    }

    public static void addEnabledCategory(Context context, AppCategory category) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> str = pref.getStringSet("apps_rows_enabled_categories", new HashSet<String>());
        Set<String> editable = new HashSet<>(str);
        editable.add(category.name());
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet("apps_rows_enabled_categories", editable).apply();
    }
}
