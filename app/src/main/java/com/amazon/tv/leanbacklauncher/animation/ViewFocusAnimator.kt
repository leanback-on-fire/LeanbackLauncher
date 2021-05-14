package com.amazon.tv.leanbacklauncher.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Keep
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration

open class ViewFocusAnimator(view: View) : OnFocusChangeListener {
    private val mCardElevationSupported: Boolean
    private var mEnabled = true
    private val mFocusAnimation: ObjectAnimator
    private var mFocusProgress = 0f
    private var mListener: OnFocusLevelChangeListener? = null
    private val mSelectedScaleDelta: Float
    private val mSelectedZDelta: Float

    @JvmField
    protected var mTargetView: View = view
    private val mUnselectedScale: Float
    private val mUnselectedZ: Float

    interface OnFocusLevelChangeListener {
        fun onFocusLevelChange(f: Float)
        fun onFocusLevelSettled(z: Boolean)
    }

    val focusedScaleFactor: Float
        get() = mTargetView.resources.getFraction(R.fraction.lb_focus_zoom_factor_medium, 1, 1)

    fun setOnFocusProgressListener(listener: OnFocusLevelChangeListener?) {
        mListener = listener
    }

    @set:Keep
    var focusProgress: Float
        get() = mFocusProgress
        set(level) {
            mFocusProgress = level
            val scale = mUnselectedScale + mSelectedScaleDelta * level
            val z = mUnselectedZ + mSelectedZDelta * level
            mTargetView.scaleX = scale
            mTargetView.scaleY = scale
            if (mCardElevationSupported) {
                mTargetView.z = z
            }
            mListener?.onFocusLevelChange(level)
        }

    fun animateFocus(focused: Boolean) {
        if (mEnabled) {
            if (mFocusAnimation.isStarted) {
                mFocusAnimation.cancel()
            }
            if (focusProgress != (if (focused) 1.0f else 0.0f)) {
                mFocusAnimation.setFloatValues(focusProgress, if (focused) 1.0f else 0.0f)
                mFocusAnimation.start()
                return
            }
            return
        }
        setFocusImmediate(focused)
    }

    fun setFocusImmediate(focused: Boolean) {
        if (mFocusAnimation.isStarted) {
            mFocusAnimation.cancel()
        }
        focusProgress = if (focused) 1.0f else 0.0f
        mListener?.onFocusLevelSettled(focused)
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (v === mTargetView) {
            setHasFocus(hasFocus)
        }
    }

    protected fun setHasFocus(hasFocus: Boolean) {
        if (mEnabled && mTargetView.visibility == View.VISIBLE && mTargetView.isAttachedToWindow && mTargetView.hasWindowFocus()) {
            animateFocus(hasFocus)
        } else {
            setFocusImmediate(hasFocus)
        }
    }

    fun setEnabled(enabled: Boolean) {
        mEnabled = enabled
        if (!mEnabled && mFocusAnimation.isStarted) {
            mFocusAnimation.end()
        }
    }

    init {
        val res = view.resources
        mTargetView.onFocusChangeListener = this
        mUnselectedScale = res.getFraction(R.fraction.unselected_scale, 1, 1)
        mSelectedScaleDelta = focusedScaleFactor - mUnselectedScale
        mUnselectedZ = res.getDimensionPixelOffset(R.dimen.unselected_item_z).toFloat()
        mSelectedZDelta = res.getDimensionPixelOffset(R.dimen.selected_item_z_delta).toFloat()
        mFocusAnimation = ObjectAnimator.ofFloat(this, "focusProgress", 0.0f)
        mFocusAnimation.duration = res.getInteger(R.integer.item_scale_anim_duration).toLong()
        mFocusAnimation.interpolator = AccelerateDecelerateInterpolator()
        var supported = true
        val launcherConfiguration = LauncherConfiguration.getInstance()
        if (launcherConfiguration == null || !launcherConfiguration.isCardElevationEnabled) {
            supported = false
        }
        mCardElevationSupported = supported
        focusProgress = 0.0f
        mFocusAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mTargetView.setHasTransientState(true)
            }

            override fun onAnimationEnd(animation: Animator) {
                mTargetView.setHasTransientState(false)
                mListener?.let {
                    val z = mFocusProgress > 0.5f
                    it.onFocusLevelSettled(z)
                }
            }
        })
    }
}