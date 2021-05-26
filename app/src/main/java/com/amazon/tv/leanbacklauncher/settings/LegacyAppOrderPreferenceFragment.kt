package com.amazon.tv.leanbacklauncher.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.getSavedSortingMode
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.saveSortingMode
import com.amazon.tv.leanbacklauncher.apps.AppsManager.SortingMode

class LegacyAppOrderPreferenceFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(R.string.select_app_order_action_title),
            null,
            getString(R.string.home_screen_order_content_title),
            ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null)
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        var z = true
        val sortingMode = getSavedSortingMode(activity)
        actions.add(
            GuidedAction.Builder(activity)
                .id(1)
                .checkSetId(1)
                .checked(sortingMode === SortingMode.RECENCY)
                .title(R.string.select_app_order_action_recency)
                .description(R.string.recency_order_action_description)
                .build()
        )
        val builder = GuidedAction.Builder(activity)
            .id(2)
            .checkSetId(1)
        if (sortingMode !== SortingMode.FIXED) {
            z = false
        }
        actions.add(
            builder
                .checked(z)
                .title(R.string.select_app_order_action_fixed)
                .description(R.string.fixed_order_action_description)
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id.toInt()) {
            1 -> {
                saveSortingMode(activity, SortingMode.RECENCY)
                return
            }
            2 -> {
                saveSortingMode(activity, SortingMode.FIXED)
                return
            }
            else -> return
        }
    }
}