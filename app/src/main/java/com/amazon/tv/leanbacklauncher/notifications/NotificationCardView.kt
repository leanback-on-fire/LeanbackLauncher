package com.amazon.tv.leanbacklauncher.notifications

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.BaseCardView
import com.amazon.tv.leanbacklauncher.DimmableItem
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator
import com.amazon.tv.leanbacklauncher.util.Util.isContentUri
import com.amazon.tv.tvrecommendations.TvRecommendation
import kotlin.math.max
import kotlin.math.roundToInt

class NotificationCardView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BaseCardView(context, attrs, defStyle), DimmableItem, ParticipatesInLaunchAnimation,
    ParticipatesInScrollAnimation {
    private var mAnimationsEnabled = true
    private var mAutoDismiss = false
    private var mBadgeImage: ImageView? = null
    private val mBadgeImageAlpha: Float
    private var mBadgeImageSelected: ImageView? = null
    var clickedIntent: PendingIntent? = null
    private var mClipBounds: Rect? = null
    private var launchAnimationColor = 0
    private var mContentView: TextView? = null
    private var mDimmer: ViewDimmer
    private val mFocusAnimDuration: Int
    private var mFocusAnimator: ViewFocusAnimator
    private val mImageHeight: Int
    private val mImageMaxWidth: Int
    private val mImageMinWidth: Int
    private var mImageView: ImageView? = null
    private var mInfoArea: View? = null
    private var mInfoBackground: ColorDrawable? = null
    private var mMetaAnim: ObjectAnimator? = null
    private val mMetaUnselectedHeight: Int
    private var mMetadataArea: View? = null
    private var mProgBar: ProgressBar? = null
    var recommendationGroup: String? = null
    private var mSelectedMetadataContainer: PrescaledLayout? = null
    var signature: String? = null
        private set
    private var mSourceNameView: TextView? = null
    private var mTitleView: TextView? = null
    private var mWallpaperUri: String? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        mImageView = findViewById(R.id.art_work)
        mMetadataArea = findViewById(R.id.metadata)
        mSelectedMetadataContainer = findViewById(R.id.selected_metadata_container)
        mInfoArea = findViewById(R.id.info_field)
        mTitleView = findViewById(R.id.title_text)
        mContentView = findViewById(R.id.content_text)
        mSourceNameView = findViewById(R.id.source_name)
        mBadgeImage = findViewById(R.id.badge)
        mBadgeImageSelected = findViewById(R.id.badge_selected)
        mProgBar = findViewById(R.id.progress_bar)
        val cardBkg = background
        launchAnimationColor =
            ResourcesCompat.getColor(resources, R.color.notif_background_color, null)
        mInfoBackground = ColorDrawable(launchAnimationColor)
        mInfoArea?.background = mInfoBackground
        mDimmer.addDimTarget(mImageView)
        mDimmer.addDimTarget(mTitleView)
        mDimmer.addDimTarget(mContentView)
        mDimmer.addDimTarget(mSourceNameView)
        mDimmer.addDimTarget(cardBkg)
        mDimmer.addDesatDimTarget(mBadgeImage)
        mDimmer.addDesatDimTarget(mBadgeImageSelected)
        mDimmer.addDimTarget(mInfoBackground)
        mDimmer.addDimTarget(mProgBar?.progressDrawable)
        mDimmer.setDimLevelImmediate()
        clipToOutline = true
    }

    fun setColor(color: Int) {
        launchAnimationColor = color
        if (launchAnimationColor != 0) {
            mInfoBackground?.color = launchAnimationColor
        } else {
            mInfoBackground?.color =
                ResourcesCompat.getColor(resources, R.color.notif_background_color, null)
        }
    }

    fun setMainImage(image: Drawable?) {
        mImageView?.setImageDrawable(image)
    }

    fun setTitleText(text: CharSequence?) {
        mTitleView?.text = text
    }

    fun setContentText(text: CharSequence?) {
        mContentView?.text = text
    }

    fun setSourceName(text: CharSequence?) {
        mSourceNameView?.text = text
    }

    fun setBadgeImage(image: Drawable?) {
        var img = image
        if (img != null) {
            img = img.mutate()
            mBadgeImage?.setImageDrawable(getDeselectedBadgeIcon(img))
            mBadgeImage?.visibility = View.VISIBLE
            mBadgeImageSelected?.setImageDrawable(img)
            mBadgeImageSelected?.visibility = View.VISIBLE
            return
        }
        mBadgeImageSelected?.visibility = View.GONE
        mBadgeImage?.visibility = View.GONE
    }

    private fun getDeselectedBadgeIcon(image: Drawable): Drawable {
        val iconWidth = image.intrinsicWidth
        val iconHeight = image.intrinsicHeight
        image.setBounds(0, 0, iconWidth, iconHeight)
        image.isFilterBitmap = true
        image.alpha = (255.0f * mBadgeImageAlpha).toInt()
        val bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888)
        image.draw(Canvas(bitmap))
        return BitmapDrawable(resources, bitmap)
    }

    private fun createNewFocusAnimator(): ViewFocusAnimator {
        return ViewFocusAnimator(this)
    }

    var wallpaperUri: String?
        get() = mWallpaperUri
        set(uri) {
            if (isContentUri(uri)) {
                mWallpaperUri = uri
                return
            }
            mWallpaperUri = null
            if (uri != null) {
                Log.w(
                    "NotificationCardView",
                    "Invalid Content URI provided for recommendation background: $uri"
                )
            }
        }

    fun setProgressShown(shown: Boolean) {
        if (mProgBar != null) {
            mProgBar?.visibility =
                if (shown) View.VISIBLE else View.INVISIBLE
        }
    }

    fun setAutoDismiss(auto: Boolean) {
        mAutoDismiss = auto
    }

    override fun setDimState(dimState: DimState, immediate: Boolean) {
        mDimmer.setDimState(dimState, immediate)
    }

    override fun setSelected(selected: Boolean) {
        if (selected != isSelected) {
            super.setSelected(selected)
            setMetaDataExpanded(selected)
        }
    }

    private fun setNotificationImage(rec: TvRecommendation) {
        try {
            val res = context.packageManager.getResourcesForApplication(rec.packageName)
            var image: Drawable? = null
            var width = -1
            var height = -1
            if (rec.contentImage != null) {
                image = BitmapDrawable(res, rec.contentImage)
                width = image.getIntrinsicWidth()
                height = image.getIntrinsicHeight()
            }
            if (rec.width > 0) {
                width = rec.width
            }
            if (rec.height > 0) {
                height = rec.height
            }
            mImageView?.scaleType = ImageView.ScaleType.CENTER_CROP
            setDimensions(width, height)
            setMainImage(image)
            setBadgeImage(ResourcesCompat.getDrawable(res, rec.badgeIcon, null))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun setDimensions(imgWidth: Int, imgHeight: Int) {
        var cardWidth: Int
        if (imgWidth <= 0 || imgHeight <= 0) {
            cardWidth = mImageMinWidth
        } else {
            cardWidth =
                (imgWidth.toFloat() / (imgHeight.toFloat() / mImageHeight.toFloat())).toInt()
            if (cardWidth > mImageMaxWidth) {
                cardWidth = mImageMaxWidth
            }
            if (cardWidth < mImageMinWidth) {
                cardWidth = mImageMinWidth
            }
        }
        setViewWidth(mImageView, cardWidth)
        setViewHeight(mImageView, mImageHeight)
        if (cardWidth.toFloat() / mImageHeight.toFloat() > 1.5f) {
            mTitleView?.maxLines = 1
            mContentView?.maxLines = 1
            return
        }
        mTitleView?.maxLines = 2
        mContentView?.maxLines = 2
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        resetCardState()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        resetCardState()
    }

    private fun resetCardState() {
        val focus = hasFocus()
        super.setSelected(focus)
        if (mMetaAnim != null) {
            mMetaAnim?.cancel()
            mMetaAnim = null
        }
        clearAnimation()
        mDimmer.setDimLevelImmediate()
        mFocusAnimator.setFocusImmediate(focus)
        setMetaDataExpandedImmediate(focus)
        setAnimationsEnabled(true)
    }

    private fun setMetaDataExpanded(expanded: Boolean) {
        var f = 1.0f
        if (mMetaAnim != null) {
            mMetaAnim?.cancel()
            mMetaAnim = null
        }
        if (mAnimationsEnabled && visibility == View.VISIBLE && hasWindowFocus() && isAttachedToWindow) {
            mSelectedMetadataContainer?.visibility = View.VISIBLE
            setMetadataOpenFraction(if (expanded) 0.0f else 1.0f)
            val str = "metadataOpenFraction"
            val fArr = FloatArray(2)
            val f2: Float = if (expanded) {
                0.0f
            } else {
                1.0f
            }
            fArr[0] = f2
            if (!expanded) {
                f = 0.0f
            }
            fArr[1] = f
            mMetaAnim = ObjectAnimator.ofFloat(this, str, *fArr)
            mMetaAnim?.duration = mFocusAnimDuration.toLong()
            mMetaAnim?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    setHasTransientState(true)
                }

                override fun onAnimationEnd(animation: Animator) {
                    setHasTransientState(false)
                    setMetaDataExpandedImmediate(expanded)
                }
            })
            mMetaAnim?.start()
            return
        }
        setMetaDataExpandedImmediate(expanded)
    }

    private fun setMetaDataExpandedImmediate(expanded: Boolean) {
        setMetadataAlphaAndPositionFraction(if (expanded) 1.0f else 0.0f)
        clipBounds = null
        mSelectedMetadataContainer?.visibility =
            if (expanded) View.VISIBLE else View.GONE
    }

    @Keep
    fun setMetadataOpenFraction(fract: Float) {
        setMetadataAlphaAndPositionFraction(fract)
        val height = mMetaUnselectedHeight + (max(
            0,
            mSelectedMetadataContainer!!.contentHeight - mMetaUnselectedHeight
        ).toFloat() * fract).roundToInt()
        if (mClipBounds == null) {
            mClipBounds = Rect()
        }
        mClipBounds!![0, 0, width] = getHeight() - mMetadataArea!!.height + height
        clipBounds = mClipBounds
    }

    private fun setMetadataAlphaAndPositionFraction(fract: Float) {
        val badgeTop = getRelativeTop(mBadgeImage, mMetadataArea)
        val badgeSelectedTop = getRelativeTop(mBadgeImageSelected, mMetadataArea)
        mSourceNameView!!.alpha = 1.0f - fract
        mBadgeImage!!.alpha = 1.0f - fract
        mBadgeImage!!.translationY = (badgeSelectedTop - badgeTop).toFloat() * fract
        mInfoArea!!.background.alpha = (255.0f * fract + 0.5f).toInt()
        mTitleView!!.alpha = fract
        mContentView!!.alpha = fract
        mBadgeImageSelected!!.alpha = fract
        mBadgeImageSelected!!.translationY =
            (-badgeSelectedTop + badgeTop).toFloat() * (1.0f - fract)
    }

    private fun getRelativeTop(view: View?, ancestor: View?): Int {
        return if (view === ancestor) {
            0
        } else getRelativeTop(
            view!!.parent as View,
            ancestor
        ) + view.top
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        isSelected = gainFocus
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        mAnimationsEnabled = enabled
        if (!(enabled || mMetaAnim == null || !mMetaAnim!!.isStarted)) {
            mMetaAnim?.end()
        }
        mFocusAnimator.setEnabled(enabled)
        mDimmer.setAnimationEnabled(enabled)
    }

    companion object {
        private fun setViewWidth(v: View?, width: Int) {
            val p = v!!.layoutParams
            if (p.width != width) {
                p.width = width
                v.layoutParams = p
            }
        }

        private fun setViewHeight(v: View?, height: Int) {
            val p = v!!.layoutParams
            if (p.height != height) {
                p.height = height
                v.layoutParams = p
            }
        }
    }

    init {
        val res = resources
        mImageMinWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_min_width)
        mImageMaxWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_max_width)
        mImageHeight = res.getDimensionPixelOffset(R.dimen.notif_card_img_height)
        mFocusAnimDuration = res.getInteger(R.integer.notif_card_metadata_animation_duration)
        mMetaUnselectedHeight = res.getDimensionPixelOffset(R.dimen.notif_card_info_height)
        mBadgeImageAlpha = res.getFraction(R.fraction.badge_icon_alpha, 1, 1)
        mDimmer = ViewDimmer(this)
        mFocusAnimator = createNewFocusAnimator()
    }
}