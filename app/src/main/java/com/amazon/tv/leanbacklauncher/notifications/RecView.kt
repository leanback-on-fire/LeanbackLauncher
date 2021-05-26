package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils.TruncateAt
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences
import com.amazon.tv.leanbacklauncher.DimmableItem
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.RoundedRectOutlineProvider
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator.OnFocusLevelChangeListener
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration
import com.amazon.tv.leanbacklauncher.trace.AppTrace
import com.amazon.tv.leanbacklauncher.trace.AppTrace.TraceTag
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import kotlin.math.ceil
import kotlin.math.roundToInt

abstract class RecView(context: Context) : ViewGroup(context), Target<Bitmap?>,
    DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation,
    OnFocusLevelChangeListener {
    private val mBackground: Drawable?
    private val mBackgroundColor: Int
    private var mBadgeIcon: Drawable? = null
    private val mBadgeIconCollapsedBounds = RectF()
    private val mBadgeIconExpandedBounds = RectF()
    private val mBadgeIconIntrinsicBounds = RectF()
    private val mBadgeIconMatrix = Matrix()
    private val mBadgeMarginBottom: Int
    private val mBadgeSize: Int
    private var mClipBounds: Rect? = null

    @JvmField
    protected val mContentView: TextView?

    @JvmField
    protected val mDimmer: ViewDimmer

    @JvmField
    protected var mExpandedInfoAreaBound = false
    private val mFocusAnimator: ViewFocusAnimator?
    private var mFocusLevel = 0f
    private var mFocusLevelAnimating = false
    private val mGapBetweenSourceNameAndBadge: Int
    private var mGlideRequest: Request? = null

    @JvmField
    protected var mImage: Drawable? = null

    @JvmField
    protected val mImageHeight: Int
    private val mImageMaxWidth: Int
    private val mImageMinWidth: Int
    private val mInfoAreaCollapsedHeight: Int

    @JvmField
    var mInfoAreaColor = 0

    @JvmField
    protected val mInfoAreaDefaultColor: Int
    private var mInfoAreaExpandedHeight = 0
    private val mInfoAreaPaddingBottom: Int
    private val mInfoAreaPaddingEnd: Int
    private val mInfoAreaPaddingStart: Int
    private val mInfoAreaPaddingTop: Int
    private val mInfoAreaTop: Int
    private val mInfoBackground: ColorDrawable

    @JvmField
    protected var mPackageManager: PackageManager
    private var mProgressBar: ProgressBar? = null
    private val mProgressBarHeight: Int
    private var mProgressDrawable: Drawable? = null
    private val mScaleFactor: Float

    @JvmField
    protected var mSignature: String? = null
    private val mSourceNameView: TextView?

    @JvmField
    protected val mTitleView: TextView?
    private var mTraceTag: TraceTag? = null
    private val mTypeface: Typeface
    protected abstract fun bindBadge()
    protected abstract fun bindInfoAreaTitleAndContent()
    protected abstract val contentImage: Bitmap?
    protected abstract val dataHeight: Int
    protected abstract val dataWidth: Int
    abstract val signature: String?
    abstract val wallpaperUri: String?
    protected abstract fun hasProgress(): Boolean
    protected fun onBind() {
        mSignature = null
        mExpandedInfoAreaBound = false
        mFocusAnimator?.setFocusImmediate(hasFocus())
        mDimmer.setDimLevelImmediate()
        requestLayout()
    }

    open fun getLaunchAnimationColor(): Int {
        return this.mInfoAreaColor
    }

    protected fun bindProgressBar(progressMax: Int, progress: Int) {
        if (hasProgress()) {
            if (mProgressBar == null) {
                val context = context
                mProgressDrawable =
                    ContextCompat.getDrawable(context, R.drawable.card_progress_drawable)
                mProgressBar = ProgressBar(context, null, 0, 16973855)
                mProgressBar?.let { bar ->
                    bar.progressDrawable = mProgressDrawable
                    bar.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    addView(mProgressBar)
                    bar.visibility = VISIBLE
                    bar.max = progressMax
                    bar.progress = progress
                }
                mDimmer.addDimTarget(mProgressDrawable)
            }
        } else if (mProgressBar != null) {
            mProgressBar?.visibility = GONE
            mDimmer.removeDimTarget(mProgressDrawable)
        }
    }

    protected fun bindContentDescription(
        title: CharSequence?,
        label: CharSequence?,
        contentText: CharSequence?
    ) {
        val description: String = if (title != null) {
            if (label == null || title.toString().equals(label.toString(), ignoreCase = true)) {
                if (contentText == null || contentText.toString()
                        .equals(title.toString(), ignoreCase = true)
                ) {
                    title.toString()
                } else {
                    String.format(
                        resources.getString(R.string.notification_card_view_description_title_content),
                        title,
                        contentText
                    )
                }
            } else if (contentText == null || contentText.toString()
                    .equals(label.toString(), ignoreCase = true)
            ) {
                String.format(
                    resources.getString(R.string.notification_card_view_description_title_label),
                    title,
                    label
                )
            } else {
                String.format(
                    resources.getString(R.string.notification_card_view_description_title_label_content),
                    title,
                    label,
                    contentText
                )
            }
        } else if (label != null) {
            if (contentText == null || contentText.toString()
                    .equals(label.toString(), ignoreCase = true)
            ) {
                label.toString()
            } else {
                String.format(
                    resources.getString(R.string.notification_card_view_description_label_content),
                    label,
                    contentText
                )
            }
        } else contentText?.toString()
            ?: resources.getString(R.string.notification_card_view_description_default)
        contentDescription = description
    }

    protected fun bindSourceName(sourceName: CharSequence?, packageName: String?) {
        var name = sourceName
        if (name.isNullOrEmpty()) {
            try {
                name = mPackageManager.getApplicationLabel(
                    mPackageManager.getApplicationInfo(
                        packageName!!,
                        0
                    )
                )
            } catch (e: PackageManager.NameNotFoundException) {
                // unused
            }
        }
        mSourceNameView?.text = name
    }

    protected fun bindExpandedInfoArea() {
        if (!mExpandedInfoAreaBound) {
            if (width.toFloat() / mImageHeight.toFloat() > 1.5f) {
                mTitleView?.maxLines = 1
                mContentView?.maxLines = 1
            } else {
                mTitleView?.maxLines = 2
                mContentView?.maxLines = 2
            }
            bindInfoAreaTitleAndContent()
            mExpandedInfoAreaBound = true
            mInfoAreaExpandedHeight = 0
        }
    }

    protected fun bindBadge(badgeIcon: Drawable?) {
        if (mBadgeIcon != null) {
            mDimmer.removeDimTarget(mBadgeIcon)
            mDimmer.removeDesatDimTarget(mBadgeIcon)
        }
        mBadgeIcon = badgeIcon
        mBadgeIcon?.let { badge ->
            mBadgeIcon = badge.mutate()
            badge.setBounds(0, 0, badge.intrinsicWidth, badge.intrinsicHeight)
            mBadgeIconIntrinsicBounds[0.0f, 0.0f, badge.intrinsicWidth.toFloat()] =
                badge.intrinsicHeight.toFloat()
            badge.callback = this
            mDimmer.addDimTarget(mBadgeIcon)
            mDimmer.addDesatDimTarget(mBadgeIcon)
        }
    }

    private fun createTextView(textSize: Float, textColor: Int): TextView {
        val textView = TextView(context)
        textView.typeface = mTypeface
        textView.setTextSize(0, textSize)
        textView.setTextColor(textColor)
        textView.ellipsize = TruncateAt.END
        textView.textDirection = 5
        return textView
    }

    override fun onFocusLevelSettled(focused: Boolean) {
        if (focused) {
            bindExpandedInfoArea()
            mSourceNameView!!.visibility = View.GONE
            mTitleView!!.visibility = View.VISIBLE
            mTitleView.alpha = 1.0f
            mContentView!!.visibility = View.VISIBLE
            mContentView.alpha = 1.0f
        } else {
            mSourceNameView!!.visibility = View.VISIBLE
            mSourceNameView.alpha = 1.0f
            mTitleView!!.visibility = View.GONE
            mContentView!!.visibility = View.GONE
        }
        clipBounds = null
        requestLayout()
        mFocusLevelAnimating = false
    }

    override fun onFocusLevelChange(level: Float) {
        if (mFocusLevel != level) {
            if (mFocusLevel == 0.0f) {
                bindExpandedInfoArea()
                requestLayout()
            }
            mSourceNameView!!.visibility = VISIBLE
            mTitleView!!.visibility = VISIBLE
            mContentView!!.visibility = VISIBLE
            if (mInfoAreaExpandedHeight == 0) {
                measureExpandedInfoArea(calculateCardWidth())
            }
            mSourceNameView.alpha = 1.0f - level
            mTitleView.alpha = level
            mContentView.alpha = level
            if (mClipBounds == null) {
                mClipBounds = Rect()
            }
            mClipBounds!![0, 0, width] =
                mImageHeight + mInfoAreaCollapsedHeight + ((mInfoAreaExpandedHeight - mInfoAreaCollapsedHeight).toFloat() * level).roundToInt()
            clipBounds = mClipBounds
            mFocusLevelAnimating = true
            mFocusLevel = level
            interpolateBadgeIconLayout()
            invalidate()
        }
    }

    fun setMainImage(drawable: Drawable?) {
        if (mImage != null) {
            mDimmer.removeDimTarget(mImage)
        }
        if (drawable != null) {
            mImage = drawable.mutate()
            layoutMainImage(width)
            mImage!!.callback = this
            mDimmer.addDimTarget(mImage)
        } else {
            mImage = null
        }
        invalidate()
    }

    fun setUseBackground(useBackground: Boolean) {
        if (useBackground) {
            background = mBackground
            mDimmer.addDimTarget(mBackground)
            return
        }
        mDimmer.removeDimTarget(mBackground)
        background = null
    }

    override fun setDimState(dimState: DimState, z: Boolean) {
        mDimmer.setDimState(dimState, z)
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        mFocusAnimator!!.setEnabled(enabled)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        isSelected = gainFocus
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cardWidth = calculateCardWidth()
        if (mProgressBar != null && mProgressBar!!.visibility == 0) {
            mProgressBar!!.measure(
                MeasureSpec.makeMeasureSpec(cardWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mProgressBarHeight, MeasureSpec.EXACTLY)
            )
        }
        val startBound = mInfoAreaPaddingStart
        var endBound = cardWidth - mInfoAreaPaddingEnd
        if (mBadgeIcon != null) {
            endBound -= mBadgeSize + mGapBetweenSourceNameAndBadge
        }
        mSourceNameView!!.measure(
            MeasureSpec.makeMeasureSpec(endBound - startBound, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mInfoAreaCollapsedHeight, MeasureSpec.EXACTLY)
        )
        if (mExpandedInfoAreaBound && mInfoAreaExpandedHeight == 0) {
            measureExpandedInfoArea(cardWidth)
        }
        setMeasuredDimension(
            cardWidth,
            if (mFocusLevel == 0.0f) mImageHeight + mInfoAreaCollapsedHeight else mImageHeight + mInfoAreaExpandedHeight
        )
    }

    private fun calculateCardWidth(): Int {
        var cardWidth = dataWidth
        var imageHeight = dataHeight
        if (mImage != null) {
            if (cardWidth <= 0) {
                cardWidth = mImage!!.intrinsicWidth
            }
            if (imageHeight <= 0) {
                imageHeight = mImage!!.intrinsicHeight
            }
        }
        cardWidth = if (imageHeight > 0) {
            mImageHeight * cardWidth / imageHeight
        } else {
            mImageMinWidth
        }
        if (cardWidth > mImageMaxWidth) {
            return mImageMaxWidth
        }
        return if (cardWidth < mImageMinWidth) {
            mImageMinWidth
        } else cardWidth
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mInfoBackground.setBounds(0, mInfoAreaTop, right - left, bottom - top)
    }

    private fun measureExpandedInfoArea(width: Int) {
        mTitleView!!.measure(
            MeasureSpec.makeMeasureSpec(
                ((width - mInfoAreaPaddingEnd - mInfoAreaPaddingStart).toFloat() * mScaleFactor).toInt(),
                MeasureSpec.EXACTLY
            ), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        mContentView!!.measure(
            MeasureSpec.makeMeasureSpec(
                ((width - mInfoAreaPaddingEnd - mInfoAreaPaddingStart - mBadgeSize - mGapBetweenSourceNameAndBadge).toFloat() * mScaleFactor).toInt(),
                MeasureSpec.EXACTLY
            ), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        mInfoAreaExpandedHeight =
            (mInfoAreaPaddingTop.toFloat() + mTitleView.measuredHeight.toFloat() / mScaleFactor + mContentView.measuredHeight.toFloat() / mScaleFactor + mInfoAreaPaddingBottom.toFloat()).toInt()
    }

    protected fun layoutMainImage(width: Int) {
        mImage?.let { image ->
            val scaledImageWidth =
                image.intrinsicWidth.toFloat() * mImageHeight.toFloat() / image.intrinsicHeight.toFloat()
            image.setBounds(
                ((width.toFloat() - scaledImageWidth) * 0.5f).toInt(),
                0,
                ceil(((width.toFloat() + scaledImageWidth) * 0.5f).toDouble()).toInt(),
                mImageHeight
            )
        }
    }

    protected fun layoutProgressBar(width: Int) {
        mProgressBar?.layout(0, mImageHeight - mProgressBarHeight, width, mImageHeight)
    }

    protected fun layoutSourceName(width: Int, isLayoutRtl: Boolean) {
        if (mSourceNameView!!.visibility == 0) {
            layoutChild(mSourceNameView, mInfoAreaPaddingStart, mInfoAreaTop, width, isLayoutRtl)
        }
    }

    protected fun layoutBadgeIcon(width: Int, height: Int, isLayoutRtl: Boolean) {
        if (mBadgeIcon != null) {
            val scaledBadgeSize = mBadgeSize.toFloat() / mScaleFactor
            val badgeBottom = height.toFloat() - mBadgeMarginBottom.toFloat() / mScaleFactor
            if (isLayoutRtl) {
                mBadgeIconCollapsedBounds[mInfoAreaPaddingEnd.toFloat(), mInfoAreaTop.toFloat(), (mInfoAreaPaddingEnd + mBadgeSize).toFloat()] =
                    (mInfoAreaTop + mInfoAreaCollapsedHeight).toFloat()
                val badgeLeft = mInfoAreaPaddingEnd.toFloat() / mScaleFactor
                mBadgeIconExpandedBounds[badgeLeft, badgeBottom - scaledBadgeSize, badgeLeft + scaledBadgeSize] =
                    badgeBottom
            } else {
                mBadgeIconCollapsedBounds[(width - mInfoAreaPaddingEnd - mBadgeSize).toFloat(), mInfoAreaTop.toFloat(), (width - mInfoAreaPaddingEnd).toFloat()] =
                    (mInfoAreaTop + mInfoAreaCollapsedHeight).toFloat()
                val badgeRight = width.toFloat() - mInfoAreaPaddingEnd.toFloat() / mScaleFactor
                mBadgeIconExpandedBounds[badgeRight - scaledBadgeSize, badgeBottom - scaledBadgeSize, badgeRight] =
                    badgeBottom
            }
            interpolateBadgeIconLayout()
        }
    }

    protected fun layoutExpandedInfoArea(width: Int, isLayoutRtl: Boolean) {
        if (mTitleView != null && mTitleView.visibility == 0) {
            val start = (mInfoAreaPaddingStart.toFloat() * mScaleFactor).toInt()
            val titleTop =
                (mInfoAreaTop.toFloat() + mInfoAreaPaddingTop.toFloat() * mScaleFactor).toInt()
            val scaledWidth = (width.toFloat() * mScaleFactor).toInt()
            layoutChild(mTitleView, start, titleTop, scaledWidth, isLayoutRtl)
            scaleExpandedInfoAreaView(mTitleView)
            layoutChild(
                mContentView,
                start,
                titleTop + mTitleView.measuredHeight,
                scaledWidth,
                isLayoutRtl
            )
            scaleExpandedInfoAreaView(mContentView)
        }
    }

    private fun layoutChild(
        view: TextView?,
        start: Int,
        top: Int,
        parentWidth: Int,
        isLayoutRtl: Boolean
    ) {
        if (isLayoutRtl) {
            view!!.layout(
                parentWidth - start - view.measuredWidth,
                top,
                parentWidth - start,
                view.measuredHeight + top
            )
        } else {
            view!!.layout(start, top, view.measuredWidth + start, view.measuredHeight + top)
        }
    }

    private fun scaleExpandedInfoAreaView(view: TextView?) {
        view!!.pivotX = (-view.left).toFloat()
        view.pivotY = (-(view.top - mInfoAreaTop)).toFloat()
        view.scaleX = 1.0f / mScaleFactor
        view.scaleY = 1.0f / mScaleFactor
    }

    private fun interpolateBadgeIconLayout() {
        mBadgeIcon?.let {
            val rectC = mBadgeIconCollapsedBounds
            val rectE = mBadgeIconExpandedBounds
            sRect[rectC.left + (rectE.left - rectC.left) * mFocusLevel, rectC.top + (rectE.top - rectC.top) * mFocusLevel, rectC.right + (rectE.right - rectC.right) * mFocusLevel] =
                rectC.bottom + (rectE.bottom - rectC.bottom) * mFocusLevel
            mBadgeIconMatrix.setRectToRect(mBadgeIconIntrinsicBounds, sRect, ScaleToFit.CENTER)
        }
    }

    public override fun onDraw(canvas: Canvas) {
        if (mImage != null) {
            mImage!!.draw(canvas)
        } else {
            sPaint.color = mInfoAreaColor
            canvas.drawRect(0.0f, 0.0f, width.toFloat(), mImageHeight.toFloat(), sPaint)
        }
        mInfoBackground.color = ColorUtils.blendARGB(mBackgroundColor, mInfoAreaColor, mFocusLevel)
        mInfoBackground.draw(canvas)
        mBadgeIcon?.let { badge ->
            canvas.save()
            canvas.concat(mBadgeIconMatrix)
            badge.draw(canvas)
            canvas.restore()
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mImage || who === mBadgeIcon || who === mInfoBackground
    }

    fun onStartImageFetch() {
        mTraceTag = AppTrace.beginAsyncSection("RecImageFetch")
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        val contentImage = contentImage
        if (contentImage != null) {
            setMainImage(BitmapDrawable(resources, contentImage))
        } else {
            setMainImage(null)
        }
    }

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
        setMainImage(BitmapDrawable(resources, resource))
        onLoadComplete()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        onLoadComplete()
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        setMainImage(null)
        onLoadComplete()
    }

    protected fun onLoadComplete() {
        if (mTraceTag != null) {
            AppTrace.endAsyncSection(mTraceTag)
            mTraceTag = null
        }
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(Int.MIN_VALUE, Int.MIN_VALUE)
    }

    override fun setRequest(request: Request?) {
        mGlideRequest = request
    }

    override fun getRequest(): Request? {
        return mGlideRequest
    }

    override fun onStart() {}
    override fun onStop() {}
    override fun onDestroy() {}
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mGlideRequest != null && mGlideRequest!!.isRunning) {
            mGlideRequest!!.pause()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mGlideRequest != null && mGlideRequest!!.isCleared) {
            mGlideRequest!!.begin()
        }
    }

    override fun toString(): String {
        var obj: Any?
        var append = StringBuilder().append("S: ")
            .append(if (mSourceNameView == null) "No View" else mSourceNameView.text).append(" T: ")
        obj = if (mTitleView == null) {
            "No View"
        } else {
            mTitleView.text
        }
        append = append.append(obj).append(" C: ")
        obj = if (mContentView == null) {
            "No View"
        } else {
            mContentView.text
        }
        val titles = append.append(obj).toString()
        append = StringBuilder().append("FL: ").append(mFocusLevel).append(" FP: ")
        obj = if (mFocusAnimator == null) {
            "No Animator"
        } else {
            java.lang.Float.valueOf(mFocusAnimator.focusProgress)
        }
        return super.toString() + " -- " + titles + " " + append.append(obj).append(" IA: ")
            .append(mInfoAreaExpandedHeight).append(" FLA: ").append(mFocusLevelAnimating)
            .toString()
    }

    companion object {
        private var sOutline: RoundedRectOutlineProvider? = null
        private val sPaint = Paint()
        private val sRect = RectF()
    }

    init {
        setWillNotDraw(false)
        id = R.id.card
        isFocusable = true
        val launcherConfiguration = LauncherConfiguration.getInstance()
        if (launcherConfiguration != null && launcherConfiguration.isRoundCornersEnabled) {
            if (sOutline == null) { // sOutline = RoundedRectOutlineProvider(resources.getDimensionPixelOffset(R.dimen.notif_card_corner_radius).toFloat())
                sOutline = RoundedRectOutlineProvider(RowPreferences.getCorners(context).toFloat())
            }
            outlineProvider = sOutline
            clipToOutline = true
        }
        mDimmer = ViewDimmer(this)
        mInfoBackground = ColorDrawable()
        mDimmer.addDimTarget(mInfoBackground)
        mPackageManager = getContext().packageManager

        val res = context.resources
        val font = context.getString(R.string.font)
        mBackground = ContextCompat.getDrawable(context, R.drawable.rec_card_background)
        mImageMinWidth = res.getDimensionPixelSize(R.dimen.notif_card_img_min_width)
        mImageMaxWidth = res.getDimensionPixelSize(R.dimen.notif_card_img_max_width)
        mImageHeight = res.getDimensionPixelSize(R.dimen.notif_card_img_height)
        mProgressBarHeight = res.getDimensionPixelSize(R.dimen.progress_bar_height)
        mInfoAreaPaddingStart = res.getDimensionPixelOffset(R.dimen.notif_card_info_start_margin)
        mInfoAreaPaddingTop = res.getDimensionPixelOffset(R.dimen.notif_card_info_margin_top)
        mInfoAreaPaddingEnd = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_end_margin)
        mInfoAreaPaddingBottom = res.getDimensionPixelOffset(R.dimen.notif_card_info_margin_bottom)
        mInfoAreaCollapsedHeight = res.getDimensionPixelSize(R.dimen.notif_card_info_height)
        mInfoAreaDefaultColor = ContextCompat.getColor(context, R.color.notif_background_color)
        mGapBetweenSourceNameAndBadge = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_start_margin)
        mBadgeSize = res.getDimensionPixelSize(R.dimen.notif_card_extra_badge_size)
        mBadgeMarginBottom = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_bottom_margin)
        mTypeface = Typeface.create(font, Typeface.NORMAL)
        mBackgroundColor = ContextCompat.getColor(context, R.color.notif_background_color)
        val sourceNameTextSize = res.getDimension(R.dimen.notif_card_source_text_size)
        val sourceNameTextColor = ContextCompat.getColor(context, R.color.notif_source_text_color)
        mSourceNameView = createTextView(sourceNameTextSize, sourceNameTextColor)
        mSourceNameView.gravity = Gravity.CENTER_VERTICAL
        mSourceNameView.setLines(1)
        addView(mSourceNameView)
        mDimmer.addDimTarget(mSourceNameView)
        val titleTextSize = res.getDimension(R.dimen.notif_card_title_text_size)
        val titleTextColor = ContextCompat.getColor(context, R.color.notif_title_text_color)
        mTitleView = createTextView(titleTextSize, titleTextColor)
        addView(mTitleView)
        mDimmer.addDimTarget(mTitleView)
        val contentTextSize = res.getDimension(R.dimen.notif_card_content_text_size)
        val contentTextColor = ContextCompat.getColor(context, R.color.notif_content_text_color)
        mContentView = createTextView(contentTextSize, contentTextColor)
        addView(mContentView)
        mDimmer.addDimTarget(mContentView)
        mFocusAnimator = ViewFocusAnimator(this)
        mFocusAnimator.setOnFocusProgressListener(this)
        mScaleFactor = mFocusAnimator.focusedScaleFactor
        mInfoAreaTop = mImageHeight
    }
}