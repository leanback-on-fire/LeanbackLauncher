package com.amazon.tv.firetv.leanbacklauncher.apps

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil.Companion.instance
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter
import com.amazon.tv.leanbacklauncher.apps.LaunchPoint

/**
 * Created by rockon999 on 3/7/18.
 */
class FavoritesAdapter(
    context: Context,
    actionOpenLaunchPointListener: ActionOpenLaunchPointListener?,
    vararg appTypes: AppCategory?
) : AppsAdapter(context, actionOpenLaunchPointListener, *appTypes),
    OnSharedPreferenceChangeListener {
    private val prefUtil: SharedPreferencesUtil? = instance(context)
    private val listener: OnSharedPreferenceChangeListener = this
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        refreshDataSetAsync()
    }

    internal inner class FavoritesAppFilter : AppFilter() {
        override fun include(point: LaunchPoint?): Boolean {
            return prefUtil != null && prefUtil.isFavorite(point?.packageName)
        }
    }

    init {
        mFilter = FavoritesAppFilter()
        this.prefUtil?.addFavoritesListener(listener)
    }
}