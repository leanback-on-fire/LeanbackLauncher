package com.amazon.tv.firetv.leanbacklauncher.apps

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.leanback.app.GuidedStepFragment
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

class AppInfoFragment : GuidedStepFragment() {
    private var icon: Drawable? = null
    private var title: String? = null
    private var pkg: String? = null

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(title,
                pkg,
                "",
                icon)
    }

    override fun onResume() {
        super.onResume()
        updateActions()
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == ACTION_ID_IN_STORE.toLong()) {
            openAppInAmazonStore(activity, pkg)
            activity.finish()
        } else if (action.id == ACTION_PLAY_STORE.toLong()) {
                openAppInPlayStore(activity, pkg)
                activity.finish()
            } else if (action.id == ACTION_ID_SETTINGS.toLong()) {
                startAppSettings(activity, pkg)
                activity.finish()
            } else {
                val context = activity.applicationContext
                val util = instance(context)
                if (action.id == ACTION_ID_FAVORITE.toLong()) {
                    val favorited = !util!!.isFavorite(pkg)
                    if (favorited) {
                        util.favorite(pkg)
                    } else {
                        util.unfavorite(pkg)
                    }
                } else if (action.id == ACTION_ID_HIDE.toLong()) {
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
        val context = activity.applicationContext
        val util = instance(context)
        val actions = ArrayList<GuidedAction>()
        val favlabel = if (!util!!.isFavorite(pkg)) getString(R.string.app_info_add_favorites) else getString(R.string.app_info_rem_favorites)
        val hidelabel = if (!util.isHidden(pkg)) getString(R.string.app_info_hide_app) else getString(R.string.app_info_unhide_app)
        var action = GuidedAction.Builder(context)
                .id(ACTION_PLAY_STORE.toLong())
                .title(getString(R.string.app_info_in_playstore)).build()
        if (isPlayStoreInstalled(context)) actions.add(action)
        action = GuidedAction.Builder(context)
                .id(ACTION_ID_IN_STORE.toLong())
                .title(getString(R.string.app_info_in_store)).build()
        if (isAmazonStoreInstalled(context)) actions.add(action)
        action = GuidedAction.Builder(context)
                .id(ACTION_ID_SETTINGS.toLong())
                .title(getString(R.string.app_info_settings)).build()
        actions.add(action)
        action = GuidedAction.Builder(context)
                .id(ACTION_ID_FAVORITE.toLong())
                .title(favlabel).build()
        actions.add(action)
        action = GuidedAction.Builder(context)
                .id(ACTION_ID_HIDE.toLong())
                .title(hidelabel).build()
        actions.add(action)
        setActions(actions) // APPLY
    }

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
}