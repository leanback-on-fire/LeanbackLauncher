package com.amazon.tv.leanbacklauncher.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class WallpaperBitmapTransform(context: Context, mask: Bitmap) : BitmapTransformation() {
    private val mMaskDrawable: BitmapDrawable = BitmapDrawable(context.resources, mask)
    private val mMaskHeight: Int
    private val mRect1 = Rect()
    private val mRect2 = Rect()
    override fun transform(pool: BitmapPool, image: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
//        if (image == null) {
//            return null
//        }
        val bitmap: Bitmap
        val canvas: Canvas
        val width = image.width
        val height = image.height
        if (width * outHeight <= outWidth * height) {
            val newHeight = width * outHeight / outWidth
            bitmap = obtainBitmap(pool, width, newHeight, image.config)
            canvas = Canvas(bitmap)
            canvas.drawColor(-16777216)
            mRect1[0, 0, width] = newHeight
            canvas.drawBitmap(image, mRect1, mRect1, null)
        } else {
            val newWidth = outWidth * height / outHeight
            bitmap = obtainBitmap(pool, newWidth, height, image.config)
            canvas = Canvas(bitmap)
            canvas.drawColor(-16777216)
            val left = (width - newWidth) / 2
            mRect1[left, 0, left + newWidth] = height
            mRect2[0, 0, newWidth] = height
            canvas.drawBitmap(image, mRect1, mRect2, null)
        }
        val scale = canvas.height.toFloat() / mMaskHeight.toFloat()
        mRect2[0, 0, (canvas.width.toFloat() / scale).toInt()] = (canvas.height
            .toFloat() / scale).toInt()
        mMaskDrawable.bounds = mRect2
        canvas.scale(scale, scale)
        mMaskDrawable.draw(canvas)
        return bitmap
    }

    private fun obtainBitmap(
        pool: BitmapPool,
        width: Int,
        height: Int,
        config: Bitmap.Config
    ): Bitmap {
        val recycled = pool.getDirty(width, height, config)
        return recycled ?: Bitmap.createBitmap(width, height, config)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}

    init {
        mMaskDrawable.tileModeX = TileMode.REPEAT
        mMaskDrawable.tileModeY = TileMode.CLAMP
        mMaskHeight = mask.height
    }
}