package com.amazon.tv.leanbacklauncher.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.view.View

class FadeMaskView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private var mMask: Bitmap? = null
    private val mPaint: Paint = Paint()
    fun setBitmap(maskImage: Bitmap?) {
        mMask = maskImage
        if (mMask != null) {
            mPaint.shader = BitmapShader(mMask!!, TileMode.REPEAT, TileMode.CLAMP)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mMask != null) {
            canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), mPaint)
        }
    }

}