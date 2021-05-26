package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.res.ResourcesCompat
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.tvrecommendations.TvRecommendation
import com.bumptech.glide.request.target.SizeReadyCallback

class CaptivePortalNotificationCardView(context: Context?) : RecView(
    context!!
) {
    private var mNetworkIcon: Bitmap? = null
    private var mRecommendation: TvRecommendation? = null
    public override fun bindInfoAreaTitleAndContent() {
        mTitleView?.text = mRecommendation!!.title
        mContentView?.text = mRecommendation!!.text
    }

    override val contentImage: Bitmap?
        get() {
            if (mNetworkIcon == null) {
                mNetworkIcon = generateArtwork()
            }
            return mNetworkIcon
        }
    override val dataWidth: Int
        get() = if (mRecommendation != null) mRecommendation!!.width else 0
    override val dataHeight: Int
        get() = if (mRecommendation != null) mRecommendation!!.height else 0

    public override fun hasProgress(): Boolean {
        return false
    }

    fun setRecommendation(rec: TvRecommendation?, updateImage: Boolean) {
        mRecommendation = rec
        mInfoAreaColor = ResourcesCompat.getColor(resources, R.color.notif_background_color, null)
        mRecommendation?.let {
            bindSourceName(
                resources.getString(R.string.settings_network),
                it.packageName
            )
            bindContentDescription(
                it.title,
                it.sourceName,
                it.text
            )
        }
        setMainImage(BitmapDrawable(resources, contentImage))
        onBind()
    }

    override val wallpaperUri: String?
        get() = null

    override val signature: String?
        get() {
            if (mSignature == null) {
                mSignature =
                    mRecommendation?.title.toString() + mRecommendation?.text.toString() + mRecommendation?.sourceName.toString()
            }
            return mSignature
        }

    override fun bindBadge() {}
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var isLayoutRtl = true
        super.onLayout(changed, left, top, right, bottom)
        val width = right - left
        if (layoutDirection != LAYOUT_DIRECTION_RTL) {
            isLayoutRtl = false
        }
        layoutMainImage(width)
        layoutSourceName(width, isLayoutRtl)
        layoutExpandedInfoArea(width, isLayoutRtl)
    }

    private fun generateArtwork(): Bitmap {
        val networkIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_wifi_active_3, null)
        networkIcon!!.setTint(-1)
        val height = networkIcon.intrinsicHeight
        val width = networkIcon.intrinsicWidth
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(mRecommendation!!.color)
        val canvas = Canvas(bmp)
        networkIcon.bounds = Rect(0, 0, width, height)
        networkIcon.draw(canvas)
        return bmp
    }

    override fun removeCallback(cb: SizeReadyCallback) {}
}