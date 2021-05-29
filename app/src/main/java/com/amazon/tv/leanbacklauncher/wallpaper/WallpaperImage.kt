package com.amazon.tv.leanbacklauncher.wallpaper

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

open class WallpaperImage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle) {
    private var mZoom = 0.0f
    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        setScaleMatrix(drawable)
        return changed
    }

    fun setZoomLevel(zoom: Float) {
        mZoom = zoom
        setScaleMatrix(drawable)
        invalidate()
    }

    private fun setScaleMatrix(drawable: Drawable?) {
        if (drawable != null) {
            val scale: Float
            val dx: Float
            val matrix = imageMatrix
            val vwidth = width - paddingLeft - paddingRight
            val vheight = height - paddingTop - paddingBottom
            val dwidth = drawable.intrinsicWidth
            val dheight = drawable.intrinsicHeight
            if (dwidth * vheight > vwidth * dheight) {
                scale = vheight.toFloat() / dheight.toFloat() * (mZoom + 1.0f)
                dx = (vwidth.toFloat() - dwidth.toFloat() * scale) * 0.5f
            } else {
                scale = vwidth.toFloat() / dwidth.toFloat() * (mZoom + 1.0f)
                dx = vwidth.toFloat() * mZoom * 0.5f * -1.0f
            }
            val dy = vheight.toFloat() * mZoom * 0.5f * -1.0f
            matrix.setScale(scale, scale)
            matrix.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
            imageMatrix = matrix
        }
    }
}