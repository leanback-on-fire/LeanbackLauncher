package com.amazon.tv.leanbacklauncher.settings

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.settings.RecommendationsPreferenceManager.LoadRecommendationPackagesCallback
import java.util.*

class LegacyRecommendationsPreferenceFragment : GuidedStepSupportFragment(), LoadRecommendationPackagesCallback {
    private var mActionToPackageMap: HashMap<Long, String> = hashMapOf()
    private var mPreferenceManager: RecommendationsPreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferenceManager = RecommendationsPreferenceManager(requireContext())
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(getString(R.string.recommendation_blacklist_content_title), getString(R.string.recommendation_blacklist_content_description), getString(R.string.settings_dialog_title), ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null))
    }

    override fun onResume() {
        super.onResume()
        mPreferenceManager?.loadRecommendationsPackages(this)
    }

    override fun onRecommendationPackagesLoaded(infos: List<RecommendationsPreferenceManager.PackageInfo>?) {
        if (isAdded) {
            mActionToPackageMap = HashMap()
            val actions = ArrayList<GuidedAction>()
            var actionId: Long = 0
            if (infos != null) {
                for (packageInfo in infos) {
                    var banner: Drawable?
                    banner = if (packageInfo.banner != null) {
                        packageInfo.banner
                    } else {
                        buildBannerFromIcon(packageInfo.icon)
                    }
                    actions.add(GuidedAction.Builder(activity)
                            .id(actionId)
                            .title(packageInfo.appTitle)
                            .icon(banner)
                            .checkSetId(-1)
                            .checked(!packageInfo.blacklisted)
                            .build()
                    )
                    mActionToPackageMap.put(actionId, packageInfo.packageName!!)
                    actionId++
                }
            }
            setActions(actions)
        }
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
        mPreferenceManager?.savePackageBlacklisted(mActionToPackageMap[java.lang.Long.valueOf(action.id)], !action.isChecked)
    }
}