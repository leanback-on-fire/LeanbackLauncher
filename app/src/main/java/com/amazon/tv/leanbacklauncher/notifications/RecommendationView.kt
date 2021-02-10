package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.TextUtils.TruncateAt
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

abstract class RecommendationView(context: Context) : ViewGroup(context), Target<Bitmap?>, DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation, OnFocusLevelChangeListener {
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
        mFocusAnimator!!.setFocusImmediate(hasFocus())
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
                mProgressDrawable = ContextCompat.getDrawable(context, R.drawable.card_progress_drawable)
                mProgressBar = ProgressBar(context, null, 0, 16973855)
                mProgressBar?.let {
                    it.progressDrawable = mProgressDrawable
                    it.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    addView(mProgressBar)
                    it.visibility = VISIBLE
                    it.max = progressMax
                    it.progress = progress
                }
                mDimmer.addDimTarget(mProgressDrawable)
            }
        } else if (mProgressBar != null) {
            mProgressBar!!.visibility = GONE
            mDimmer.removeDimTarget(mProgressDrawable)
        }
    }

    protected fun bindContentDescription(title: CharSequence?, label: CharSequence?, contentText: CharSequence?) {
        val description: String
        description = if (title != null) {
            if (label == null || title.toString().equals(label.toString(), ignoreCase = true)) {
                if (contentText == null || contentText.toString().equals(title.toString(), ignoreCase = true)) {
                    title.toString()
                } else {
                    String.format(resources.getString(R.string.notification_card_view_description_title_content), title, contentText)
                }
            } else if (contentText == null || contentText.toString().equals(label.toString(), ignoreCase = true)) {
                String.format(resources.getString(R.string.notification_card_view_description_title_label), title, label)
            } else {
                String.format(resources.getString(R.string.notification_card_view_description_title_label_content), title, label, contentText)
            }
        } else if (label != null) {
            if (contentText == null || contentText.toString().equals(label.toString(), ignoreCase = true)) {
                label.toString()
            } else {
                String.format(resources.getString(R.string.notification_card_view_description_label_content), label, contentText)
            }
        } else contentText?.toString()
                ?: resources.getString(R.string.notification_card_view_description_default)
        contentDescription = description
    }

    protected fun bindSourceName(sourceName: CharSequence?, packageName: String?) {
        var sourceName = sourceName
        if (TextUtils.isEmpty(sourceName)) {
            sourceName = try {
                mPackageManager.getApplicationLabel(mPackageManager.getApplicationInfo(packageName!!, 0))
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
        mSourceNameView!!.text = sourceName
    }

    protected fun bindExpandedInfoArea() {
        if (!mExpandedInfoAreaBound) {
            if (width.toFloat() / mImageHeight.toFloat() > 1.5f) {
                mTitleView!!.maxLines = 1
                mContentView!!.maxLines = 1
            } else {
                mTitleView!!.maxLines = 2
                mContentView!!.maxLines = 2
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
        if (mBadgeIcon != null) {
            mBadgeIcon = mBadgeIcon!!.mutate()
            mBadgeIcon!!.setBounds(0, 0, mBadgeIcon!!.intrinsicWidth, mBadgeIcon!!.intrinsicHeight)
            mBadgeIconIntrinsicBounds[0.0f, 0.0f, mBadgeIcon!!.intrinsicWidth.toFloat()] = mBadgeIcon!!.intrinsicHeight.toFloat()
            mBadgeIcon!!.callback = this
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
            mSourceNameView!!.visibility = GONE
            mTitleView!!.visibility = VISIBLE
            mTitleView.alpha = 1.0f
            mContentView!!.visibility = VISIBLE
            mContentView.alpha = 1.0f
        } else {
            mSourceNameView!!.visibility = VISIBLE
            mSourceNameView.alpha = 1.0f
            mTitleView!!.visibility = GONE
            mContentView!!.visibility = GONE
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
            mClipBounds!![0, 0, width] = mImageHeight + mInfoAreaCollapsedHeight + Math.round((mInfoAreaExpandedHeight - mInfoAreaCollapsedHeight).toFloat() * level)
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

    override fun setDimState(dimState: DimState, immediate: Boolean) {
        mDimmer.setDimState(dimState, immediate)
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
            mProgressBar!!.measure(MeasureSpec.makeMeasureSpec(cardWidth, 1073741824), MeasureSpec.makeMeasureSpec(mProgressBarHeight, 1073741824))
        }
        val startBound = mInfoAreaPaddingStart
        var endBound = cardWidth - mInfoAreaPaddingEnd
        if (mBadgeIcon != null) {
            endBound -= mBadgeSize + mGapBetweenSourceNameAndBadge
        }
        mSourceNameView!!.measure(MeasureSpec.makeMeasureSpec(endBound - startBound, 1073741824), MeasureSpec.makeMeasureSpec(mInfoAreaCollapsedHeight, 1073741824))
        if (mExpandedInfoAreaBound && mInfoAreaExpandedHeight == 0) {
            measureExpandedInfoArea(cardWidth)
        }
        setMeasuredDimension(cardWidth, if (mFocusLevel == 0.0f) mImageHeight + mInfoAreaCollapsedHeight else mImageHeight + mInfoAreaExpandedHeight)
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

    protected fun measureExpandedInfoArea(width: Int) {
        mTitleView!!.measure(MeasureSpec.makeMeasureSpec(((width - mInfoAreaPaddingEnd - mInfoAreaPaddingStart).toFloat() * mScaleFactor).toInt(), 1073741824), MeasureSpec.makeMeasureSpec(0, 0))
        mContentView!!.measure(MeasureSpec.makeMeasureSpec(((width - mInfoAreaPaddingEnd - mInfoAreaPaddingStart - mBadgeSize - mGapBetweenSourceNameAndBadge).toFloat() * mScaleFactor).toInt(), 1073741824), MeasureSpec.makeMeasureSpec(0, 0))
        mInfoAreaExpandedHeight = (mInfoAreaPaddingTop.toFloat() + mTitleView.measuredHeight.toFloat() / mScaleFactor + mContentView.measuredHeight.toFloat() / mScaleFactor + mInfoAreaPaddingBottom.toFloat()).toInt()
    }

    protected fun layoutMainImage(width: Int) {
        if (mImage != null) {
            val scaledImageWidth = mImage!!.intrinsicWidth.toFloat() * mImageHeight.toFloat() / mImage!!.intrinsicHeight.toFloat()
            mImage!!.setBounds(((width.toFloat() - scaledImageWidth) * 0.5f).toInt(), 0, Math.ceil(((width.toFloat() + scaledImageWidth) * 0.5f).toDouble()).toInt(), mImageHeight)
        }
    }

    protected fun layoutProgressBar(width: Int) {
        if (mProgressBar != null) {
            mProgressBar!!.layout(0, mImageHeight - mProgressBarHeight, width, mImageHeight)
        }
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
                mBadgeIconCollapsedBounds[mInfoAreaPaddingEnd.toFloat(), mInfoAreaTop.toFloat(), (mInfoAreaPaddingEnd + mBadgeSize).toFloat()] = (mInfoAreaTop + mInfoAreaCollapsedHeight).toFloat()
                val badgeLeft = mInfoAreaPaddingEnd.toFloat() / mScaleFactor
                mBadgeIconExpandedBounds[badgeLeft, badgeBottom - scaledBadgeSize, badgeLeft + scaledBadgeSize] = badgeBottom
            } else {
                mBadgeIconCollapsedBounds[(width - mInfoAreaPaddingEnd - mBadgeSize).toFloat(), mInfoAreaTop.toFloat(), (width - mInfoAreaPaddingEnd).toFloat()] = (mInfoAreaTop + mInfoAreaCollapsedHeight).toFloat()
                val badgeRight = width.toFloat() - mInfoAreaPaddingEnd.toFloat() / mScaleFactor
                mBadgeIconExpandedBounds[badgeRight - scaledBadgeSize, badgeBottom - scaledBadgeSize, badgeRight] = badgeBottom
            }
            interpolateBadgeIconLayout()
        }
    }

    protected fun layoutExpandedInfoArea(width: Int, isLayoutRtl: Boolean) {
        if (mTitleView != null && mTitleView.visibility == 0) {
            val start = (mInfoAreaPaddingStart.toFloat() * mScaleFactor).toInt()
            val titleTop = (mInfoAreaTop.toFloat() + mInfoAreaPaddingTop.toFloat() * mScaleFactor).toInt()
            val scaledWidth = (width.toFloat() * mScaleFactor).toInt()
            layoutChild(mTitleView, start, titleTop, scaledWidth, isLayoutRtl)
            scaleExpandedInfoAreaView(mTitleView)
            layoutChild(mContentView, start, titleTop + mTitleView.measuredHeight, scaledWidth, isLayoutRtl)
            scaleExpandedInfoAreaView(mContentView)
        }
    }

    protected fun layoutChild(view: TextView?, start: Int, top: Int, parentWidth: Int, isLayoutRtl: Boolean) {
        if (isLayoutRtl) {
            view!!.layout(parentWidth - start - view.measuredWidth, top, parentWidth - start, view.measuredHeight + top)
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

    protected fun interpolateBadgeIconLayout() {
        if (mBadgeIcon != null) {
            val rectC = mBadgeIconCollapsedBounds
            val rectE = mBadgeIconExpandedBounds
            sRect[rectC.left + (rectE.left - rectC.left) * mFocusLevel, rectC.top + (rectE.top - rectC.top) * mFocusLevel, rectC.right + (rectE.right - rectC.right) * mFocusLevel] = rectC.bottom + (rectE.bottom - rectC.bottom) * mFocusLevel
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
        if (mBadgeIcon != null) {
            canvas.save()
            canvas.concat(mBadgeIconMatrix)
            mBadgeIcon!!.draw(canvas)
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
        var append = StringBuilder().append("S: ").append(if (mSourceNameView == null) "No View" else mSourceNameView.text).append(" T: ")
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
        return super.toString() + " -- " + titles + " " + append.append(obj).append(" IA: ").append(mInfoAreaExpandedHeight).append(" FLA: ").append(mFocusLevelAnimating).toString()
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
            if (sOutline == null) {
                // sOutline = RoundedRectOutlineProvider(resources.getDimensionPixelOffset(R.dimen.notif_card_corner_radius).toFloat())
                sOutline = RoundedRectOutlineProvider(RowPreferences.getCorners(context).toFloat())
            }
            outlineProvider = sOutline
            clipToOutline = true
        }
        mDimmer = ViewDimmer(this)
        mInfoBackground = ColorDrawable()
        mDimmer.addDimTarget(mInfoBackground)
        mPackageManager = getContext().packageManager
        /*int[] attr = new int[]{
                R.attr.background,
                R.attr.maxWidth,
                R.attr.cardFont,
                R.attr.minWidth,
                R.attr.imageHeight,
                R.attr.progressBarHeight,
                R.attr.infoAreaPaddingStart, //
                R.attr.infoAreaPaddingTop,
                R.attr.infoAreaPaddingEnd,
                R.attr.infoAreaPaddingBottom,
                R.attr.infoAreaCollapsedHeight,
                R.attr.infoAreaDefaultColor,
                R.attr.sourceNameTextSize,
                R.attr.sourceNameTextColor,
                R.attr.gapBetweenSourceNameAndBadge,
                R.attr.badgeSize,
                R.attr.badgeMarginBottom,
                R.attr.titleTextSize,
                R.attr.titleTextColor,
                R.attr.contentTextSize,
                R.attr.contentTextColor,
        };
        Arrays.sort(attr);*/
        //  TypedArray a = context.obtainStyledAttributes(R.style.LeanbackRecommendationCard, R.styleable.LeanbackRecommendationCard);

/*

   <item name="background">@drawable/rec_card_background</item>
        <item name="maxWidth">@dimen/notif_card_img_max_width</item>
        <item name="cardFont">@string/font</item>
        <item name="minWidth">@dimen/notif_card_img_min_width</item>
        <item name="imageHeight">@dimen/notif_card_img_height</item>
        <item name="progressBarHeight">@dimen/progress_bar_height</item>
        <item name="infoAreaPaddingStart">@dimen/notif_card_info_start_margin</item>
        <item name="infoAreaPaddingTop">@dimen/notif_card_info_margin_top</item>
        <item name="infoAreaPaddingEnd">@dimen/notif_card_info_badge_end_margin</item>
        <item name="infoAreaPaddingBottom">@dimen/notif_card_info_margin_bottom</item>
        <item name="infoAreaCollapsedHeight">@dimen/notif_card_info_height</item>
        <item name="infoAreaDefaultColor">@color/notif_background_color</item>
        <item name="sourceNameTextSize">@dimen/notif_card_source_text_size</item>
        <item name="sourceNameTextColor">@color/notif_source_text_color</item>
        <item name="gapBetweenSourceNameAndBadge">@dimen/notif_card_info_badge_start_margin</item>
        <item name="badgeSize">@dimen/notif_card_extra_badge_size</item>
        <item name="badgeMarginBottom">@dimen/notif_card_info_badge_bottom_margin</item>
        <item name="titleTextSize">@dimen/notif_card_title_text_size</item>
        <item name="titleTextColor">@color/notif_title_text_color</item>
        <item name="contentTextSize">@dimen/</item>
        <item name="contentTextColor">@color/notif_content_text_color</item>

 */
        val res = context.resources
        val font = context.getString(R.string.font) //= a.getString(2);
        mBackground = context.getDrawable(R.drawable.rec_card_background) //a.getDrawable(0);
        mImageMinWidth = res.getDimensionPixelSize(R.dimen.notif_card_img_min_width) //a.getDimensionPixelSize(3, 0);
        mImageMaxWidth = res.getDimensionPixelSize(R.dimen.notif_card_img_max_width) //a.getDimensionPixelSize(1, 0);
        mImageHeight = res.getDimensionPixelSize(R.dimen.notif_card_img_height) //a.getDimensionPixelSize(4, 0);
        mProgressBarHeight = res.getDimensionPixelSize(R.dimen.progress_bar_height) //a.getDimensionPixelSize(5, 0);
        mInfoAreaPaddingStart = res.getDimensionPixelOffset(R.dimen.notif_card_info_start_margin) //a.getDimensionPixelOffset(6, 0);
        mInfoAreaPaddingTop = res.getDimensionPixelOffset(R.dimen.notif_card_info_margin_top) // a.getDimensionPixelOffset(7, 0);
        mInfoAreaPaddingEnd = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_end_margin) ////a.getDimensionPixelOffset(8, 0);
        mInfoAreaPaddingBottom = res.getDimensionPixelOffset(R.dimen.notif_card_info_margin_bottom) //a.getDimensionPixelOffset(9, 0);
        mInfoAreaCollapsedHeight = res.getDimensionPixelSize(R.dimen.notif_card_info_height) //a.getDimensionPixelSize(10, 0);
        mInfoAreaDefaultColor = ContextCompat.getColor(context, R.color.notif_background_color) //a.getColor(11, 0);
        val sourceNameTextSize = res.getDimension(R.dimen.notif_card_source_text_size) //a.getDimension(12, 0.0f);
        val sourceNameTextColor = ContextCompat.getColor(context, R.color.notif_source_text_color) //a.getColor(13, 0);
        mGapBetweenSourceNameAndBadge = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_start_margin) //a.getDimensionPixelOffset(14, 0);
        mBadgeSize = res.getDimensionPixelSize(R.dimen.notif_card_extra_badge_size) //a.getDimensionPixelSize(15, 0);
        mBadgeMarginBottom = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_bottom_margin) //a.getDimensionPixelOffset(16, 0);
        val titleTextSize = res.getDimension(R.dimen.notif_card_title_text_size) //a.getDimension(17, 0.0f);
        val titleTextColor = ContextCompat.getColor(context, R.color.notif_title_text_color) //a.getColor(18, 0);
        val contentTextSize = res.getDimension(R.dimen.notif_card_content_text_size) //a.getDimension(19, 0.0f);
        val contentTextColor = ContextCompat.getColor(context, R.color.notif_content_text_color) //a.getColor(20, 0);
        //a.recycle();
        mTypeface = Typeface.create(font, 0)
        mBackgroundColor = ContextCompat.getColor(context, R.color.notif_background_color)
        mSourceNameView = createTextView(sourceNameTextSize, sourceNameTextColor)
        mSourceNameView.gravity = 16
        mSourceNameView.setLines(1)
        addView(mSourceNameView)
        mDimmer.addDimTarget(mSourceNameView)
        mTitleView = createTextView(titleTextSize, titleTextColor)
        addView(mTitleView)
        mDimmer.addDimTarget(mTitleView)
        mContentView = createTextView(contentTextSize, contentTextColor)
        addView(mContentView)
        mDimmer.addDimTarget(mContentView)
        mFocusAnimator = ViewFocusAnimator(this)
        mFocusAnimator.setOnFocusProgressListener(this)
        mScaleFactor = mFocusAnimator.focusedScaleFactor
        mInfoAreaTop = mImageHeight
    }
}