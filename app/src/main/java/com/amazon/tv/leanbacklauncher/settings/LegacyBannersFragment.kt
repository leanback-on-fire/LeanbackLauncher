package com.amazon.tv.leanbacklauncher.settings

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getBannersSize
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getCorners
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getFrameColor
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getFrameWidth
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setBannersSize
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setCorners
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setFrameColor
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.setFrameWidth
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.util.Util.refreshHome
import java.util.*

class LegacyBannersFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
                getString(R.string.banners_prefs_title),  // title
                getString(R.string.banners_prefs_desc),  // description
                getString(R.string.settings_dialog_title),  // breadcrumb (parent)
                ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null) // icon
        )
    }

    override fun onResume() {
        super.onResume()
        updateActions()
    }

    private fun updateActions() {
        val actions = ArrayList<GuidedAction>()
        val activity = requireActivity()
        actions.add(GuidedAction.Builder(
                activity)
                .id(ACTION_SZ.toLong())
                .title(R.string.banners_size)
                .description(Integer.toString(getBannersSize(activity)))
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        actions.add(GuidedAction.Builder(
                activity)
                .id(ACTION_RAD.toLong())
                .title(R.string.banners_corners_radius)
                .description(Integer.toString(getCorners(activity)))
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        actions.add(GuidedAction.Builder(
                activity)
                .id(ACTION_FWD.toLong())
                .title(R.string.banners_frame_width)
                .description(Integer.toString(getFrameWidth(activity)))
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .build()
        )
        actions.add(GuidedAction.Builder(
                activity)
                .id(ACTION_FCL.toLong())
                .title(R.string.banners_frame_color)
                .description(hexStringColor(getFrameColor(activity)))
                .descriptionEditable(true)
                .descriptionEditInputType(InputType.TYPE_CLASS_TEXT)
                .build()
        )
        actions.add(GuidedAction.Builder(
                activity)
                .id(ACTION_BACK.toLong())
                .title(null)
                .description(R.string.banners_frame_color_desc)
                .multilineDescription(true)
                .focusable(false)
                .infoOnly(true)
                .build()
        )
        //actions.get(actions.size() - 1).setEnabled(false);
        //actions.get(actions.size() - 1).setFocusable(false);
        setActions(actions) // APPLY
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val activity = requireActivity()
        var value: Int
        when (action.id.toInt()) {
            ACTION_SZ -> {
                try {
                    value = action.description.toString().toInt()
                } catch (nfe: NumberFormatException) {
                    value = getBannersSize(activity)
                }
                setBannersSize(activity, value)
                refreshHome(activity)
            }
            ACTION_RAD -> {
                try {
                    value = action.description.toString().toInt()
                } catch (nfe: NumberFormatException) {
                    value = getCorners(activity!!)
                }
                setCorners(activity, value)
                refreshHome(activity)
            }
            ACTION_FWD -> {
                try {
                    value = action.description.toString().toInt()
                } catch (nfe: NumberFormatException) {
                    value = getFrameWidth(activity!!)
                }
                setFrameWidth(activity, value)
                refreshHome(activity)
            }
            ACTION_FCL -> {
                try {
                    value = Color.parseColor(action.description.toString())
                } catch (nfe: IllegalArgumentException) {
                    value = getFrameColor(activity!!)
                }
                setFrameColor(activity, value)
                refreshHome(activity)
            }
            ACTION_BACK -> fragmentManager!!.popBackStack()
            else -> {
            }
        }
        updateActions()
    }

    private fun hexStringColor(color: Int): String {
        return String.format("#%08X", -0x1 and color)
    }

    companion object {
        /* Action ID definition */
        private const val ACTION_SZ = 0
        private const val ACTION_RAD = 1
        private const val ACTION_FWD = 2
        private const val ACTION_FCL = 3
        private const val ACTION_BACK = 4
    }
}