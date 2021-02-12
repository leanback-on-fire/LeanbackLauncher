package com.amazon.tv.leanbacklauncher

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import java.util.*

class HomeScrollManager(context: Context, private val mRecyclerView: RecyclerView) {
    private var mAnimationsEnabled = true
    private var mFractionFromTop = 0f
    private val mScrollAnimationThreshold: Int
    private val mScrollListeners: ArrayList<HomeScrollFractionListener?> = arrayListOf()
    private var mScrollPosition = 0
    private val mScrollThreshold: Int

    init {
        val resources = context.resources
        mScrollThreshold = resources.getDimensionPixelOffset(R.dimen.home_scroll_size_search)
        mScrollAnimationThreshold = resources.getDimensionPixelOffset(R.dimen.home_scroll_animation_threshold)
    }

    interface HomeScrollFractionListener {
        fun onScrollPositionChanged(i: Int, f: Float)
    }

    fun addHomeScrollListener(listener: HomeScrollFractionListener) {
        if (!mScrollListeners.contains(listener)) {
            mScrollListeners.add(listener)
            listener.onScrollPositionChanged(mScrollPosition, mFractionFromTop)
        }
    }

    fun removeHomeScrollListener(listener: HomeScrollFractionListener) {
        for (i in mScrollListeners.indices) {
            if (mScrollListeners[i] === listener) {
                mScrollListeners.removeAt(i)
                return
            }
        }
    }

    fun onScrolled(dy: Int, scrollPosition: Int) {
        if (mScrollPosition != scrollPosition) {
            mScrollPosition = scrollPosition
            if (mScrollThreshold > 0) {
                mFractionFromTop = Math.max(0.0f, Math.min(1.0f, Math.abs(mScrollPosition.toFloat() / mScrollThreshold.toFloat())))
            } else {
                mFractionFromTop = 0.0f
            }
            val shouldAnimate = Math.abs(dy) <= mScrollAnimationThreshold
            if (shouldAnimate != mAnimationsEnabled) {
                mAnimationsEnabled = shouldAnimate
                adjustAnimationsEnabledState(mRecyclerView)
            }
            updateListeners()
        }
    }

    private fun updateListeners() {
        for (i in mScrollListeners.indices) {
            mScrollListeners[i]!!.onScrollPositionChanged(mScrollPosition, mFractionFromTop)
        }
    }

    fun onScrollStateChanged(newState: Int) {
        mAnimationsEnabled = newState == 0
        adjustAnimationsEnabledState(mRecyclerView)
    }

    private fun adjustAnimationsEnabledState(view: View) {
        if (view is ParticipatesInScrollAnimation) {
            (view as ParticipatesInScrollAnimation).setAnimationsEnabled(mAnimationsEnabled)
        }
        if (view is ViewGroup) {
            val count = view.childCount
            for (i in 0 until count) {
                adjustAnimationsEnabledState(view.getChildAt(i))
            }
        }
    }
}