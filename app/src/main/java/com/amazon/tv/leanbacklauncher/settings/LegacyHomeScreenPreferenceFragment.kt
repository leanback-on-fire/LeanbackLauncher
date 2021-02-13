package com.amazon.tv.leanbacklauncher.settings

import android.os.Build
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.settings.RecommendationsPreferenceManager.LoadBlacklistCountCallback
import java.util.*

class LegacyHomeScreenPreferenceFragment : GuidedStepSupportFragment(), LoadBlacklistCountCallback {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(getString(R.string.settings_dialog_title), null, "", ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null))
    }

    override fun onResume() {
        super.onResume()
        RecommendationsPreferenceManager(requireContext()).loadBlacklistCount(this)
    }

    override fun onBlacklistCountLoaded(blacklistCount: Int) {
        if (isAdded) {
            val actions = ArrayList<GuidedAction>()
            var description: String? = null
            if (blacklistCount != -1) {
                description = resources.getQuantityString(R.plurals.recommendation_blacklist_action_description, blacklistCount, Integer.valueOf(blacklistCount))
            }
            val version = String.format("%s v%s", getString(R.string.app_name), BuildConfig.VERSION_NAME)
            actions.add(GuidedAction.Builder(activity).id(1).title(R.string.home_screen_order_action_title).description(R.string.home_screen_order_desc).build())
            actions.add(GuidedAction.Builder(activity).id(2).title(R.string.hidden_applications_title).description(R.string.hidden_applications_desc).build())
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                actions.add(GuidedAction.Builder(activity).id(3).title(R.string.recommendation_blacklist_action_title).description(description).build())
            }
            actions.add(GuidedAction.Builder(activity).id(4).title(R.string.update).description(version).build())
            setActions(actions)
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id.toInt()) {
            1 -> {
                add(fragmentManager, LegacyAppsAndGamesPreferenceFragment())
                return
            }
            2 -> {
                add(fragmentManager, LegacyHiddenPreferenceFragment())
                return
            }
            3 -> {
                add(fragmentManager, LegacyRecommendationsPreferenceFragment())
                return
            }
            4 -> {
                add(fragmentManager, LegacyUpdatePreferenceFragment())
                return
            }
            else -> return
        }
    }
}