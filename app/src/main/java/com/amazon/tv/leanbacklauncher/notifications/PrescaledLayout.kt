package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.amazon.tv.leanbacklauncher.R
import kotlin.math.ceil

class PrescaledLayout : ViewGroup {
    private var mContent: View? = null
    private var mScaleFactor = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        clipChildren = false
        mScaleFactor = resources.getFraction(R.fraction.lb_focus_zoom_factor_medium, 1, 1)
        mContent = getChildAt(0)
        mContent?.pivotX = 0.0f
        mContent?.pivotY = 0.0f
        mContent?.scaleX = 1.0f / mScaleFactor
        mContent?.scaleY = 1.0f / mScaleFactor
    }

    val contentHeight: Int
        get() = (mContent!!.measuredHeight.toFloat() / mScaleFactor).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        mContent!!.measure(
            MeasureSpec.makeMeasureSpec(
                ceil((width.toFloat() * mScaleFactor).toDouble()).toInt(), MeasureSpec.EXACTLY
            ), 0
        )
        setMeasuredDimension(
            width, (mContent!!.measuredHeight
                .toFloat() / mScaleFactor).toInt()
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mContent!!.layout(0, 0, mContent!!.measuredWidth, mContent!!.measuredHeight)
    }
}