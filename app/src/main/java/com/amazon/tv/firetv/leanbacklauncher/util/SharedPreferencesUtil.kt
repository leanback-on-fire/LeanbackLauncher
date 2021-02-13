package com.amazon.tv.firetv.leanbacklauncher.util

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

/**
 * Created by rockon999 on 3/7/18.
 */
class SharedPreferencesUtil private constructor() {
    private var hiddenPref: SharedPreferences? = null
    private var favPref: SharedPreferences? = null
    private var genPref: SharedPreferences? = null

    fun isFavorite(component: String?): Boolean {
        return favPref!!.getBoolean(component, false)
    }

    fun favorite(component: String?) {
        favPref!!.edit().putBoolean(component, true).apply()
    }

    fun unfavorite(component: String?) {
        favPref!!.edit().putBoolean(component, false).apply()
    }

    fun hidden_apps(): Set<String> {
        return hiddenPref!!.all.keys
    }

    fun isHidden(component: String?): Boolean {
        return hiddenPref!!.getBoolean(component, false)
    }

    fun hide(component: String?) {
        hiddenPref!!.edit().putBoolean(component, true).apply()
    }

    fun unhide(component: String?) {
        hiddenPref!!.edit().putBoolean(component, false).apply()
    }

    fun isAllAppsShown(): Boolean {
        return genPref!!.getBoolean("show_all_apps", false)
    }

    fun showAllApps(mode: Boolean) {
        genPref!!.edit().putBoolean("show_all_apps", mode).apply()
    }

    // todo unregister too
    fun addFavoritesListener(listener: OnSharedPreferenceChangeListener?) {
        favPref!!.registerOnSharedPreferenceChangeListener(listener)
    }

    fun addHiddenListener(listener: OnSharedPreferenceChangeListener?) {
        hiddenPref!!.registerOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        private var instance: SharedPreferencesUtil? = null
        @JvmStatic
        fun instance(context: Context): SharedPreferencesUtil? {
            if (instance == null) instance = SharedPreferencesUtil()
            instance!!.hiddenPref = context.getSharedPreferences("hidden-apps", Context.MODE_PRIVATE)
            instance!!.favPref = context.getSharedPreferences("favorite-apps", Context.MODE_PRIVATE)
            instance!!.genPref = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
            return instance
        }
    }
}