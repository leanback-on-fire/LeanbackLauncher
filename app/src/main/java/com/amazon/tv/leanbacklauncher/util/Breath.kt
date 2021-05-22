package com.amazon.tv.leanbacklauncher.util

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

fun View.breath(
    times: Int = Animation.INFINITE,
    duration: Long = 3000,
    offset: Long = 100,
    minAlpha: Float = 0.5f, // relative alpha 50%
    maxAlpha: Float = 1.0f, // relative alpha 100%
    factor: Float = 3.0f, // velocity
    repeatMode: Int = Animation.REVERSE
) {
    startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
        it.duration = duration
        it.startOffset = offset
        it.repeatMode = repeatMode
        it.repeatCount = times
        it.interpolator = AccelerateInterpolator(factor)
    })
}