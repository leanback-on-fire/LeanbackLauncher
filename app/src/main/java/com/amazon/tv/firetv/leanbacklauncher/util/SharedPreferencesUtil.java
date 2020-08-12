package com.amazon.tv.firetv.leanbacklauncher.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by rockon999 on 3/7/18.
 */

public class SharedPreferencesUtil {

    private SharedPreferences hiddenPref, favPref, genPref;

    private static SharedPreferencesUtil instance;

    public static SharedPreferencesUtil instance(Context context) {
        if (instance == null) instance = new SharedPreferencesUtil();
        instance.hiddenPref = context.getSharedPreferences("hidden-apps", Context.MODE_PRIVATE);
        instance.favPref = context.getSharedPreferences("favorite-apps", Context.MODE_PRIVATE);

        return instance;
    }

    private SharedPreferencesUtil() {
    }


    public boolean isFavorite(String component) {
        return favPref.getBoolean(component, false);
    }

    public void favorite(String component) {
        favPref.edit().putBoolean(component, true).apply();
    }

    public void unfavorite(String component) {
        favPref.edit().putBoolean(component, false).apply();
    }

    public Set<String> hidden() {
        return hiddenPref.getAll().keySet();
    }

    public boolean isHidden(String component) {
        return hiddenPref.getBoolean(component, false);
    }

    public void hide(String component) {
        hiddenPref.edit().putBoolean(component, true).apply();
    }

    public void unhide(String component) {
        hiddenPref.edit().putBoolean(component, false).apply();
    }

    // todo unregister too
    public void addFavoritesListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        favPref.registerOnSharedPreferenceChangeListener(listener);
    }

    public void addHiddenListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        hiddenPref.registerOnSharedPreferenceChangeListener(listener);
    }
}
