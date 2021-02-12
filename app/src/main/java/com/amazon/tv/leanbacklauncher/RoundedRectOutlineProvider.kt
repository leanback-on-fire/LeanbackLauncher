package com.amazon.tv.leanbacklauncher

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class RoundedRectOutlineProvider(private val mRadius: Float) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, view.width, view.height, mRadius)
    }

}