package com.amazon.tv.leanbacklauncher.settings

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.areFavoritesEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.areInputsEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.areRecommendationsEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getAppsColumns
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getEnabledCategories
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getFavoriteRowMax
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getRowMax
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setAppsColumns
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setFavoriteRowMax
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setFavoritesEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setGamesEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setInputsEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setMusicEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setRecommendationsEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setRowMax
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setVideosEnabled
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils.isAmazonNotificationsEnabled
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.util.Util.refreshHome
import java.util.*

/**
 * Created by rockon999 on 3/25/18.
 */
class LegacyAppRowPreferenceFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(R.string.edit_row_title),  // title
            getString(R.string.select_app_customize_rows_title),  // description
            getString(R.string.settings_dialog_title),  // breadcrumb (home_screen_order_action_title)
            ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null)
        )
    }

    override fun onResume() {
        super.onResume()
        updateActions()
        startup++
    }

    override fun onPause() {
        super.onPause()
        startup = 0
    }

    private var musicIndex = 0
    private var gameIndex = 0
    private var videoIndex = 0
    private var favIndex = 0

    override fun onGuidedActionClicked(action: GuidedAction) {
        val id = action.id
        val catId = (id - 1) / 2
        val subId = ((id - 1) % 2).toInt()
        val enabled: Boolean
        val activity: Activity? = activity
        val categories = getEnabledCategories(activity!!)
        val value: Int

        if (catId == favIndex.toLong()) {
            when (subId) {
                0 -> {
                    enabled = areFavoritesEnabled(activity)
                    setFavoritesEnabled(activity, !enabled)
                }
                1 -> {
                    value = try {
                        action.description.toString().toInt()
                    } catch (nfe: NumberFormatException) {
                        1
                    }
                    setFavoriteRowMax(activity, value)
                }
            }
        } else if (catId == musicIndex.toLong()) {
            when (subId) {
                0 -> {
                    enabled = categories.contains(AppCategory.MUSIC)
                    setMusicEnabled(activity, !enabled)
                }
                1 -> {
                    value = try {
                        action.description.toString().toInt()
                    } catch (nfe: NumberFormatException) {
                        1
                    }
                    setRowMax(AppCategory.MUSIC, activity, value)
                }
            }
        } else if (catId == videoIndex.toLong()) {
            when (subId) {
                0 -> {
                    enabled = categories.contains(AppCategory.VIDEO)
                    setVideosEnabled(activity, !enabled)
                }
                1 -> {
                    value = try {
                        action.description.toString().toInt()
                    } catch (nfe: NumberFormatException) {
                        1
                    }
                    setRowMax(AppCategory.VIDEO, activity, value)
                }
            }
        } else if (catId == gameIndex.toLong()) {
            when (subId) {
                0 -> {
                    enabled = categories.contains(AppCategory.GAME)
                    setGamesEnabled(activity, !enabled)
                }
                1 -> {
                    value = try {
                        action.description.toString().toInt()
                    } catch (nfe: NumberFormatException) {
                        1
                    }
                    setRowMax(AppCategory.GAME, activity, value)
                }
            }
        } else if (id == ACTION_ID_APPS_MAX.toLong()) {
            value = try {
                action.description.toString().toInt()
            } catch (nfe: NumberFormatException) {
                1
            }
            setRowMax(AppCategory.OTHER, activity, value)
        } else if (id == ACTION_ID_APPS_COL.toLong()) {
            value = try {
                action.description.toString().toInt()
            } catch (nfe: NumberFormatException) {
                1
            }
            setAppsColumns(activity, value)
        } else if (id == ACTION_ID_RECOMMENDATIONS.toLong()) { // RECOMMENDATIONS
            enabled = areRecommendationsEnabled(activity)
            setRecommendationsEnabled(activity, !enabled)
            if (!enabled && isAmazonNotificationsEnabled(activity)) {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.recs_warning_sale),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (id == ACTION_ID_INPUTS.toLong()) { // INPUTS
            enabled = areInputsEnabled(activity)
            setInputsEnabled(activity, !enabled)
        }
        updateActions()
    }

    private fun updateActions() {

        val actions = ArrayList<GuidedAction>()
        val activity: Activity? = activity
        val categories = getEnabledCategories(activity!!)
        var desc: String
        var index = 0
        var state: Boolean

        // RECOMMENDATIONS
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            state = areRecommendationsEnabled(activity)
            desc =
                if (state) getString(R.string.v7_preference_on) else getString(R.string.v7_preference_off)
            actions.add(
                GuidedAction.Builder(activity)
                    .id(ACTION_ID_RECOMMENDATIONS.toLong())
                    .title(R.string.recs_row_title)
                    .description(desc)
                    .build()
            )
        }
        // INPUTS
        state = areInputsEnabled(activity)
        desc =
            if (state) getString(R.string.v7_preference_on) else getString(R.string.v7_preference_off)
        actions.add(
            GuidedAction.Builder(activity)
                .id(ACTION_ID_INPUTS.toLong())
                .title(R.string.inputs_row_title)
                .description(desc)
                .build()
        )
        // FAV
        state = areFavoritesEnabled(activity)
        desc =
            if (state) getString(R.string.v7_preference_on) else getString(R.string.v7_preference_off)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.favorites_row_title)
                .description(desc)
                .build()
        )
        var maxRows = getFavoriteRowMax(activity)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.max_favorites_rows_title)
                .description(maxRows.toString())
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        favIndex = (index - 1) / 2
        // VIDEO
        state = categories.contains(AppCategory.VIDEO)
        desc =
            if (state) getString(R.string.v7_preference_on) else getString(R.string.v7_preference_off)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.videos_row_title)
                .description(desc)
                .build()
        )
        maxRows = getRowMax(AppCategory.VIDEO, activity)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.max_videos_rows_title)
                .description(maxRows.toString())
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        videoIndex = (index - 1) / 2
        // MUSIC
        state = categories.contains(AppCategory.MUSIC)
        desc =
            if (state) getString(R.string.v7_preference_on) else getString(R.string.v7_preference_off)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.music_row_title)
                .description(desc)
                .build()
        )
        maxRows = getRowMax(AppCategory.MUSIC, activity)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.max_music_rows_title)
                .description(maxRows.toString())
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        musicIndex = (index - 1) / 2
        // GAME
        state = categories.contains(AppCategory.GAME)
        desc =
            if (state) getString(R.string.v7_preference_on) else getString(R.string.v7_preference_off)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.games_row_title)
                .description(desc)
                .build()
        )
        maxRows = getRowMax(AppCategory.GAME, activity)
        actions.add(
            GuidedAction.Builder(activity)
                .id((++index).toLong())
                .title(R.string.max_games_rows_title)
                .description(maxRows.toString())
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        gameIndex = (index - 1) / 2
        // ALL APPS
        maxRows = getRowMax(AppCategory.OTHER, activity)
        val maxApps = getAppsColumns(activity)
        actions.add(
            GuidedAction.Builder(activity)
                .id(ACTION_ID_APPS_MAX.toLong())
                .title(R.string.max_apps_rows_title)
                .description(maxRows.toString())
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        // Max Apps per row
        actions.add(
            GuidedAction.Builder(activity)
                .id(ACTION_ID_APPS_COL.toLong())
                .title(R.string.max_col_title)
                .description(maxApps.toString())
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        setActions(actions) // APPLY

        // REFRESH HOME BC
        if (startup > 0) {
            refreshHome(requireActivity())
        }
    }

    companion object {
        //        private const val ACTION_ID_APPS = 50
        private const val ACTION_ID_APPS_COL = 51
        private const val ACTION_ID_APPS_MAX = 52
        private const val ACTION_ID_RECOMMENDATIONS = 100
        private const val ACTION_ID_INPUTS = 200
        private var startup = 0
    }
}