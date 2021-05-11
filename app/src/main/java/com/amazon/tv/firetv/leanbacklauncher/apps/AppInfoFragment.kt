package com.amazon.tv.firetv.leanbacklauncher.apps

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils.isAmazonStoreInstalled
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils.isPlayStoreInstalled
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils.openAppInAmazonStore
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils.openAppInPlayStore
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils.startAppSettings
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil.Companion.instance
import com.amazon.tv.leanbacklauncher.R
import java.util.*

class AppInfoFragment : GuidedStepSupportFragment() {
    private var icon: Drawable? = null
    private var title: String? = null
    private var pkg: String? = null

    companion object {
        private const val ACTION_ID_IN_STORE = 1
        private const val ACTION_PLAY_STORE = ACTION_ID_IN_STORE + 1
        private const val ACTION_ID_SETTINGS = ACTION_PLAY_STORE + 1
        private const val ACTION_ID_FAVORITE = ACTION_ID_SETTINGS + 1
        private const val ACTION_ID_HIDE = ACTION_ID_FAVORITE + 1

        @JvmStatic
        fun newInstance(title: String?, pkg: String?, icon: Drawable?): AppInfoFragment {
            val args = Bundle()
            val fragment = AppInfoFragment()
            fragment.icon = icon
            fragment.title = title
            fragment.pkg = pkg
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            title,
            pkg,
            "",
            icon
        )
    }

    override fun onResume() {
        super.onResume()
        updateActions()
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val ctx = requireContext()
        val util = instance(ctx)
        when (action.id) {
            ACTION_ID_IN_STORE.toLong() -> {
                openAppInAmazonStore(ctx, pkg)
                //activity?.finishAffinity()
            }
            ACTION_PLAY_STORE.toLong() -> {
                openAppInPlayStore(ctx, pkg)
                //activity?.finishAffinity()
            }
            ACTION_ID_SETTINGS.toLong() -> {
                startAppSettings(ctx, pkg)
                //activity?.finishAffinity()
            }
            ACTION_ID_FAVORITE.toLong() -> {
                val favorited = !util!!.isFavorite(pkg)
                if (favorited) {
                    util.favorite(pkg)
                } else {
                    util.unfavorite(pkg)
                }
            }
            ACTION_ID_HIDE.toLong() -> {
                val hidden = !util!!.isHidden(pkg)
                if (hidden) {
                    util.hide(pkg)
                } else {
                    util.unhide(pkg)
                }
            }
        }

        updateActions()
    }

    private fun updateActions() {
        val ctx = requireContext()
        val util = instance(ctx)
        val actions = ArrayList<GuidedAction>()
        val favlabel =
            if (!util!!.isFavorite(pkg)) getString(R.string.app_info_add_favorites) else getString(
                R.string.app_info_rem_favorites
            )
        val hidelabel =
            if (!util.isHidden(pkg)) getString(R.string.app_info_hide_app) else getString(R.string.app_info_unhide_app)

        var action = GuidedAction.Builder(ctx)
            .id(ACTION_PLAY_STORE.toLong())
            .title(getString(R.string.app_info_in_playstore)).build()
        if (isPlayStoreInstalled(ctx)) actions.add(action)
        action = GuidedAction.Builder(ctx)
            .id(ACTION_ID_IN_STORE.toLong())
            .title(getString(R.string.app_info_in_store)).build()
        if (isAmazonStoreInstalled(ctx)) actions.add(action)
        action = GuidedAction.Builder(ctx)
            .id(ACTION_ID_SETTINGS.toLong())
            .title(getString(R.string.app_info_settings)).build()
        actions.add(action)
        action = GuidedAction.Builder(ctx)
            .id(ACTION_ID_FAVORITE.toLong())
            .title(favlabel).build()
        actions.add(action)
        action = GuidedAction.Builder(ctx)
            .id(ACTION_ID_HIDE.toLong())
            .title(hidelabel).build()
        actions.add(action)
        setActions(actions) // APPLY
    }
}