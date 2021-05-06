package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.graphics.Bitmap
import com.amazon.tv.leanbacklauncher.PackageResourceCache
import com.amazon.tv.tvrecommendations.TvRecommendation
import com.bumptech.glide.request.target.SizeReadyCallback

class RecommendationCardView internal constructor(context: Context?, private val mResourceCache: PackageResourceCache?) : RecommendationView(context!!) {
    private var mRecommendation: TvRecommendation? = null

    constructor(context: Context?) : this(context, PackageResourceCache.getInstance(context))

    public override fun bindInfoAreaTitleAndContent() {
        mTitleView!!.text = mRecommendation!!.title
        mContentView!!.text = mRecommendation!!.text
    }

    override val contentImage: Bitmap?
        get() = if (mRecommendation != null) mRecommendation!!.contentImage else null
    override val dataWidth: Int
        get() = if (mRecommendation != null) mRecommendation!!.width else 0
    override val dataHeight: Int
        get() = if (mRecommendation != null) mRecommendation!!.height else 0

    public override fun hasProgress(): Boolean {
        return mRecommendation != null && mRecommendation!!.hasProgress()
    }

    val progress: Int
        get() = if (mRecommendation != null) mRecommendation!!.progress else 0

    fun setRecommendation(rec: TvRecommendation, updateImage: Boolean) {
        mRecommendation = rec
        var color = rec.color
        if (color == 0) {
            color = mInfoAreaDefaultColor
        }
        mInfoAreaColor = color
        bindProgressBar(mRecommendation!!.progressMax, mRecommendation!!.progress)
        bindSourceName(mRecommendation!!.sourceName, mRecommendation!!.packageName)
        bindBadge()
        bindContentDescription(mRecommendation!!.title, mRecommendation!!.sourceName, mRecommendation!!.text)
        onBind()
    }

    override val wallpaperUri: String?
        get() = mRecommendation!!.backgroundImageUri

    override val signature: String?
        get() {
            if (mSignature == null) {
                mSignature = "" + mRecommendation!!.title + mRecommendation!!.text + mRecommendation!!.sourceName
            }
            return mSignature
        }

    override fun bindBadge() {
        try {
            // TODO
            bindBadge(mResourceCache!!.getDrawable(mRecommendation!!.packageName, mRecommendation!!.badgeIcon))
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bindBadge(null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var isLayoutRtl = true
        super.onLayout(changed, left, top, right, bottom)
        val width = right - left
        val height = bottom - top
        if (layoutDirection != 1) {
            isLayoutRtl = false
        }
        layoutMainImage(width)
        layoutProgressBar(width)
        layoutSourceName(width, isLayoutRtl)
        layoutBadgeIcon(width, height, isLayoutRtl)
        layoutExpandedInfoArea(width, isLayoutRtl)
    }

    override fun removeCallback(cb: SizeReadyCallback) {}
}