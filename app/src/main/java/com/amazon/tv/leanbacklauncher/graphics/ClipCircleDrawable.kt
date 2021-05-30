package com.amazon.tv.leanbacklauncher.graphics

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import kotlin.math.max

class ClipCircleDrawable(private val mColor: Int) : Drawable() {
    override fun draw(canvas: Canvas) {
        val diameter = max(bounds.width(), bounds.height())
            .toFloat()
        val paint = Paint()
        paint.color = mColor
        paint.isAntiAlias = true
        canvas.drawCircle(diameter / 2.0f, diameter / 2.0f, diameter / 2.0f, paint)
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}