package com.amazon.tv.leanbacklauncher.wallpaper

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.amazon.tv.leanbacklauncher.R

class AnimatedLayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : WallpaperImage(
    context, attrs, defStyle
) {
    private val mFadeInAnim: Animator = AnimatorInflater.loadAnimator(context, R.animator.wallpaper_fade_in)
    private val mFadeOutAnim: Animator = AnimatorInflater.loadAnimator(context, R.animator.wallpaper_fade_out)
    private var mListener: AnimationListener? = null
    private var mRunningAnimation: Animator? = null

    interface AnimationListener {
        fun animationDone(z: Boolean)
    }

    fun setAnimationListener(listener: AnimationListener?) {
        mListener = listener
    }

    private val isAnimating: Boolean
        get() = if (mRunningAnimation != null) {
            mRunningAnimation!!.isRunning
        } else false

    fun cancelAnimation() {
        if (isAnimating) {
            mRunningAnimation!!.cancel()
        }
    }

    fun animateIn(image: Drawable?) {
        cancelAnimation()
        visibility = VISIBLE
        setImageDrawable(image)
        mRunningAnimation = mFadeInAnim
        mFadeInAnim.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        pivotX = 0.0f
        pivotY = (measuredHeight / 2).toFloat()
    }

    fun animateOut(image: Drawable?) {
        cancelAnimation()
        visibility = VISIBLE
        setImageDrawable(image)
        mRunningAnimation = mFadeOutAnim
        mFadeOutAnim.start()
    }

    init {
        mFadeInAnim.setTarget(this)
        mFadeOutAnim.setTarget(this)
        mFadeInAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (mRunningAnimation === mFadeInAnim) {
                    mRunningAnimation = null
                }
                mListener?.animationDone(true)
            }
        })
        mFadeOutAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (mRunningAnimation === mFadeOutAnim) {
                    mRunningAnimation = null
                }
                this@AnimatedLayer.visibility = GONE
                mListener?.animationDone(false)
            }
        })
    }
}