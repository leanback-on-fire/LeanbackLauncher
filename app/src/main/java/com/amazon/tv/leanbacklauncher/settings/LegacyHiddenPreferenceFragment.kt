package com.amazon.tv.leanbacklauncher.settings

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil.Companion.instance
import com.amazon.tv.leanbacklauncher.R
import java.util.*

class LegacyHiddenPreferenceFragment : GuidedStepSupportFragment() {
    private var mActionToPackageMap: HashMap<Long, String>? = null

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(getString(R.string.hidden_applications_title), getString(R.string.hidden_applications_desc), getString(R.string.settings_dialog_title), ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null))
    }

    override fun onResume() {
        super.onResume()
        loadHiddenApps()
    }

    private fun buildBannerFromIcon(icon: Drawable?): Drawable {
        val resources = resources
        val bannerWidth = resources.getDimensionPixelSize(R.dimen.preference_item_banner_width)
        val bannerHeight = resources.getDimensionPixelSize(R.dimen.preference_item_banner_height)
        val iconSize = resources.getDimensionPixelSize(R.dimen.preference_item_icon_size)
        val bitmap = Bitmap.createBitmap(bannerWidth, bannerHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.preference_item_banner_background, null))
        if (icon != null) {
            icon.setBounds((bannerWidth - iconSize) / 2, (bannerHeight - iconSize) / 2, (bannerWidth + iconSize) / 2, (bannerHeight + iconSize) / 2)
            icon.draw(canvas)
        }
        return BitmapDrawable(resources, bitmap)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.isChecked) {
            instance(activity!!)!!.hide(mActionToPackageMap!![action.id])
        } else {
            instance(activity!!)!!.unhide(mActionToPackageMap!![action.id])
        }
    }

    private fun loadHiddenApps() {
        val util = instance(activity!!)
        val packages: List<String> = ArrayList(util!!.hidden())
        if (isAdded) {
            mActionToPackageMap = HashMap()
            val actions = ArrayList<GuidedAction>()
            var actionId: Long = 0
            val pm = activity!!.packageManager
            for (pkg in packages) {
                var hidden: Boolean
                try {
                    val packageInfo = pm.getPackageInfo(pkg, 0)
                    var banner = pm.getApplicationBanner(packageInfo.applicationInfo)
                    banner = banner
                            ?: buildBannerFromIcon(pm.getApplicationIcon(packageInfo.applicationInfo))
                    hidden = util.isHidden(pkg)
                    if (hidden) // show only hidden apps
                        actions.add(GuidedAction.Builder(activity).id(actionId).title(pm.getApplicationLabel(packageInfo.applicationInfo)).icon(banner).checkSetId(-1).checked(hidden).build())
                    mActionToPackageMap!![actionId] = packageInfo.packageName
                    actionId++
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            setActions(actions)
        }
    }
}