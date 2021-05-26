package com.amazon.tv.leanbacklauncher.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.getSavedSortingMode
import com.amazon.tv.leanbacklauncher.apps.AppsManager.SortingMode
import java.util.*

class LegacyAppsAndGamesPreferenceFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(R.string.home_screen_order_action_title),
            getString(R.string.home_screen_order_content_description),
            getString(R.string.settings_dialog_title),
            ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null)
        )
    }

    override fun onResume() {
        super.onResume()
        val actions = ArrayList<GuidedAction>()
        val sortingMode = getSavedSortingMode(activity)
        actions.add(
            GuidedAction.Builder(activity).id(1).title(R.string.edit_row_title)
                .description(R.string.select_app_customize_rows_title).build()
        )
        actions.add(
            GuidedAction.Builder(activity).id(2).title(R.string.home_screen_order_content_title)
                .description(if (sortingMode === SortingMode.FIXED) R.string.select_app_order_action_fixed else R.string.select_app_order_action_recency)
                .build()
        )
        // BROKEN
        //if (sortingMode == SortingMode.FIXED) {
        //    actions.add(new Builder(getActivity()).id(2).title((int) R.string.customize_app_order_action_title).build());
        //    actions.add(new Builder(getActivity()).id(3).title((int) R.string.customize_game_order_action_title).build());
        //}
        actions.add(
            GuidedAction.Builder(activity).id(3).title(R.string.banners_prefs_title)
                .description(R.string.banners_prefs_desc).build()
        )
        actions.add(
            GuidedAction.Builder(activity).id(4).title(R.string.wallpaper_title)
                .description(R.string.select_wallpaper_action_desc).build()
        )
        setActions(actions)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id.toInt()) {
            1 -> {
                add(fragmentManager, LegacyAppRowPreferenceFragment())
            }
            2 -> {
                add(fragmentManager, LegacyAppOrderPreferenceFragment())
            }
            3 -> {
                add(fragmentManager, LegacyBannersFragment())
            }
            4 -> {
                add(fragmentManager, LegacyWallpaperFragment())
            }
            else -> return
        }
    }
}