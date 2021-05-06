package com.amazon.tv.leanbacklauncher.apps

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.ImageView
import androidx.leanback.widget.BaseCardView
import com.amazon.tv.leanbacklauncher.DimmableItem
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator

class SettingsCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : BaseCardView(context, attrs, defStyle), DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation {
    private val mAnimDuration: Int = context.resources.getInteger(R.integer.item_scale_anim_duration)
    private var mCircle: ImageView? = null
    private var mDimmer: ViewDimmer? = null
    private var mFocusAnimator: ViewFocusAnimator? = null
    private var mIcon: ImageView? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        mCircle = findViewById(R.id.selection_circle)
        mIcon = findViewById(R.id.icon)
        mFocusAnimator = ViewFocusAnimator(this)
        mDimmer = ViewDimmer(this)
        mDimmer?.addDimTarget(mIcon)
        mDimmer?.addDimTarget(mCircle)
        mDimmer?.setDimLevelImmediate()
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        isSelected = gainFocus
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
            mCircle?.animate()?.alpha(if (selected) 1.0f else 0.0f)?.setDuration(mAnimDuration.toLong())?.start()
    }

    override fun setDimState(dimState: DimState, immediate: Boolean) {
        mDimmer?.setDimState(dimState, immediate)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimation()
        mDimmer?.setDimLevelImmediate()
        mFocusAnimator?.setFocusImmediate(hasFocus())
        setAnimationsEnabled(true)
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        mDimmer?.setAnimationEnabled(enabled)
        mFocusAnimator?.setEnabled(enabled)
    }

}