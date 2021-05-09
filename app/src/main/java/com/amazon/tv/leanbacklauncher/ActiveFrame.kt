package com.amazon.tv.leanbacklauncher

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.amazon.tv.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener
import com.amazon.tv.leanbacklauncher.animation.FadeAnimator
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenView
import kotlin.math.ceil
import kotlin.math.floor

class ActiveFrame @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle), HomeScrollFractionListener, ParticipatesInScrollAnimation {
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName
    private val mAccessibilityManager: AccessibilityManager
    private var mActiveTextMargin: Float
    private val mAnimDuration: Int
    private var mBottomPadding: Int
    private var mCardSpacing: Float
    private var mDimState: DimState
    private var mDimmer: ViewDimmer? = null
    private var mDownscaleFactor: Float
    private var mExpandAnim: RowExpandAnimation? = null
    private var mExpanded: Float
    private var mHeader: View? = null
    private var mHeaderFadeInAnimation: Animator? = null
    private var mHeaderFadeOutAnimation: Animator? = null
    private var mHeaderHeight: Int
    private val mHeaderVisible: Boolean
    var mRow: ActiveItemsRowView? = null
    private var mRowMinSpacing: Int
    private var mRowPadding: Float
    private var mScalesWhenUnfocused: Boolean

    init {
        mDimState = DimState.INACTIVE
        mScalesWhenUnfocused = false
        mExpanded = 1.0f
        mHeaderVisible = true
        descendantFocusability = 262144
        mHeaderHeight = resources.getDimensionPixelSize(R.dimen.header_height)
        mActiveTextMargin = resources.getDimension(R.dimen.header_text_active_margin_extra)
        mAnimDuration = resources.getInteger(R.integer.item_scale_anim_duration)
        mBottomPadding = resources.getDimensionPixelSize(R.dimen.group_vertical_spacing)
        mRowPadding = resources.getDimension(R.dimen.row_padding)
        mCardSpacing = resources.getDimension(R.dimen.card_spacing)
        mDownscaleFactor = resources.getFraction(R.fraction.inactive_banner_scale_down_amount, 1, 1)
        if (mDownscaleFactor < 0.0f || mDownscaleFactor >= 1.0f) {
            mDownscaleFactor = 0.0f
        }
        mRowMinSpacing = ((1.0f - mDownscaleFactor) * resources.getDimension(R.dimen.inter_card_spacing)).toInt()
        mAccessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }

    private inner class RowExpandAnimation @SuppressLint("ResourceType") constructor(private val mStartValue: Float, end: Float) : Animation() {
        private val mDelta: Float = end - mStartValue
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            setExpandedFraction(mStartValue + mDelta * interpolatedTime)
        }

        init {
            duration = mAnimDuration.toLong()
            interpolator = AnimationUtils.loadInterpolator(this@ActiveFrame.context, 17563661)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val count = childCount
        var i = 0
        while (i < count) {
            val view = getChildAt(i)
            if (view is ActiveItemsRowView) {
                mRow = view
                break
            } else if (view is HomeScreenView) {
                mRow = view.notificationRow
                break
            } else {
                i++
            }
        }

        mRow?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom -> updateRow(left, right) }
        mHeader = findViewById(R.id.header)
        mHeader?.let {
            mDimmer = ViewDimmer(this)
            val title = it.findViewById<TextView>(R.id.title)
            title?.let {
                mDimmer?.addDimTarget(title)
            }
            val icon = it.findViewById<ImageView>(R.id.icon)
            icon?.let {
                mDimmer?.addDimTarget(icon)
            }
            mDimmer?.setDimState(mDimState, true)
            mHeaderFadeInAnimation = FadeAnimator(mHeader, FadeAnimator.Direction.FADE_IN)
            mHeaderFadeOutAnimation = FadeAnimator(mHeader, FadeAnimator.Direction.FADE_OUT)
        }
    }

    override fun setActivated(activated: Boolean) {
        var animateStateChange = false
        super.setActivated(activated)
        mDimState = ViewDimmer.activatedToDimState(activated)
        mDimmer?.setDimState(mDimState, false)
        mRow?.let {
            it.isActivated = activated
            if (mScalesWhenUnfocused) {
                if (hasWindowFocus() && !mAccessibilityManager.isEnabled) {
                    animateStateChange = true
                }
                setRowState(ViewDimmer.dimStateToActivated(mDimState), animateStateChange)
            }
        }
    }

    fun setScaledWhenUnfocused(scalingEnabled: Boolean) {
        mScalesWhenUnfocused = scalingEnabled
        if (mScalesWhenUnfocused) {
            setRowState(ViewDimmer.dimStateToActivated(mDimState), false)
        } else {
            setRowState(expanded = true, animate = false)
        }
    }

    override fun onScrollPositionChanged(position: Int, fractionFromTop: Float) {
        // TODO: Removed header fading...
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mRow?.let { row ->
            if (row.scaleY < 1.0f) {
                setMeasuredDimension(
                    measuredWidth,
                    measuredHeight - (row.measuredHeight.toFloat() * (1.0f - row.scaleY)).toInt()
                )
            }
        }
    }

    fun resetScrollPosition(smooth: Boolean) {
        postDelayed({
            mRow?.let { row ->
                if (row.selectedPosition != 0) {
                    if (smooth) {
                        row.setSelectedPositionSmooth(0)
                    } else {
                        row.selectedPosition = 0
                    }
                }
            }
        }, 20)
    }

    private fun setRowState(expanded: Boolean, animate: Boolean) {
        var f = 1.0f
        mExpandAnim?.let {
            it.cancel()
            mExpandAnim = null
        }
        if (animate && isAttachedToWindow && visibility == VISIBLE) {
            val f2 = mExpanded
            if (!expanded) {
                f = 0.0f
            }
            mExpandAnim = RowExpandAnimation(f2, f)
            startAnimation(mExpandAnim)
        } else if (expanded) {
            setExpandedFraction(1.0f)
        } else {
            setExpandedFraction(0.0f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        adjustRowDimensions(w)
    }

    private fun setExpandedFraction(fraction: Float) {
        mExpanded = fraction
        adjustRowDimensions(measuredWidth)
        if (mHeader != null && mHeader!!.layoutParams is MarginLayoutParams) {
            val lp = mHeader!!.layoutParams as MarginLayoutParams
            val margin = (mActiveTextMargin * fraction).toInt()
            if (lp.bottomMargin != margin) {
                lp.bottomMargin = margin
                mHeader!!.layoutParams = lp
            }
        }
    }

    private fun adjustRowDimensions(frameWidth: Int) {
        var f = 1.0f
        mRow?.let { row ->
            val f2 = frameWidth.toFloat()
            if (mExpanded < 1.0f) {
                f = 1.0f - mDownscaleFactor
            }
            val rowLength = (f2 / f).toInt()
            val p = row.layoutParams
            if (rowLength <= 0 || p.width == rowLength) {
                updateRow(row.left, row.right)
                return
            }
            p.width = rowLength
            row.layoutParams = p
        }
    }

    // TODO: check floats
    private fun updateRow(left: Int, right: Int) {
        val isScaled = mExpanded < 1.0f
        val scale = 1.0f - (1.0f - mExpanded) * mDownscaleFactor
        val unfocusedScale = 1.0f - mDownscaleFactor
        val useRtl = layoutDirection == LAYOUT_DIRECTION_RTL
        mRow?.let { row ->
            val adapter = row.adapter
            if (adapter != null) {
                val rowLength = right - left
                val deltaW = rowLength - measuredWidth
                val usableSpace = rowLength.toFloat() - 2.0f * mRowPadding
                val itemCount = adapter.itemCount
                val selected = row.selectedPosition
                var numRows = row.aNumRows
                if (numRows <= 0) {
                    numRows = 1
                }
                // TODO: check numCol math
                val numCol = ceil((itemCount.toFloat() / numRows.toFloat()).toDouble()).toInt()
                val selectedCol = floor((row.selectedPosition.toFloat() / numRows.toFloat()).toDouble()).toInt()
                var selectedView: View? = null
                if (itemCount > 0 && selected >= 0) {
                    val holder = row.findViewHolderForAdapterPosition(selected)
                    if (holder != null) {
                        selectedView = holder.itemView
                    }
                }
                if (selectedView != null) {
                    val rowAlign: Int
                    val viewLength = selectedView.measuredWidth.toFloat()
                    val totalLength = itemCount.toFloat() * viewLength + mCardSpacing * (itemCount - 1)
                    val distFromStart = (mCardSpacing + viewLength) * selectedCol.toFloat() + 0.5f * viewLength
                    val distFromEnd = (mCardSpacing + viewLength) * (numCol - selectedCol - 1) + 0.5f * viewLength
                    rowAlign = when {
                        totalLength < measuredWidth.toFloat() - 2.0f * mRowPadding -> {
                            0
                        }
                        distFromStart <= (measuredWidth / 2).toFloat() -> {
                            0
                        }
                        distFromEnd < (measuredWidth / 2).toFloat() -> {
                            2
                        }
                        else -> {
                            1
                        }
                    }
                    val selectCtr = selectedView.left.toFloat() + viewLength / 2.0f
                    if (!isScaled) {
                        row.pivotX = if (useRtl) rowLength.toFloat() - mRowPadding else mRowPadding
                        row.translationX = 0.0f
                    } else if (rowAlign == 0) {
                        row.pivotX = if (useRtl) rowLength.toFloat() - mRowPadding else mRowPadding
                        row.translationX = 0.0f
                    } else if (rowAlign == 1) {
                        val activeItemsRowView = mRow
                        val f: Float = if (useRtl) {
                            rowLength.toFloat() - selectCtr
                        } else {
                            selectCtr
                        }
                        activeItemsRowView!!.pivotX = f
                        var deltaStart = distFromStart * unfocusedScale - (measuredWidth.toFloat() / 2.0f - mRowPadding)
                        if (deltaStart > 0.0f) {
                            deltaStart = 0.0f
                        } else {
                            deltaStart *= 1.0f - mExpanded
                        }
                        var deltaEnd = distFromEnd * unfocusedScale - (measuredWidth.toFloat() / 2.0f - mRowPadding)
                        if (deltaEnd > 0.0f) {
                            deltaEnd = 0.0f
                        } else {
                            deltaEnd *= 1.0f - mExpanded
                        }
                        var centerOffset = 0.0f
                        if (deltaStart < 0.0f) {
                            centerOffset = -deltaStart
                        } else if (deltaEnd < 0.0f) {
                            centerOffset = deltaEnd
                        }
                        row.translationX = (if (useRtl) -1 else 1).toFloat() * (measuredWidth.toFloat() / 2.0f - selectCtr - centerOffset)
                    } else if (totalLength <= usableSpace) {
                        val deltaX = (measuredWidth.toFloat() - 2.0f * mRowPadding - totalLength) * (if (useRtl) -1 else 1).toFloat()
                        row.pivotX = if (useRtl) rowLength.toFloat() - mRowPadding else mRowPadding
                        row.translationX = mExpanded * deltaX
                    } else {
                        row.pivotX = if (useRtl) mRowPadding else rowLength.toFloat() - mRowPadding
                        row.translationX = ((if (useRtl) 1 else -1) * deltaW).toFloat()
                    }
                } else {
                    row.pivotX = if (useRtl) rowLength.toFloat() - mRowPadding else mRowPadding
                    row.translationX = 0.0f
                }
                row.pivotY = 0.0f
                row.scaleX = scale
                row.scaleY = scale
            }
        }
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        mDimmer?.setAnimationEnabled(enabled)
    }
}