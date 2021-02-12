package com.amazon.tv.leanbacklauncher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class OpaqueBitmapTransformation(context: Context?, color: Int) : BitmapTransformation() {
    private val mBackgroundColor: Int
    private val mHashCode: Int
    private val mKeyBytes: ByteArray

    init {
        val idBytes: ByteArray
        mBackgroundColor = color
        idBytes = "OpaqueBitmapTransformation".toByteArray(StandardCharsets.UTF_8)
        mKeyBytes = ByteArray(idBytes.size + 4)
        System.arraycopy(idBytes, 0, mKeyBytes, 0, idBytes.size)
        mKeyBytes[idBytes.size] = (color shr 24 and 15).toByte()
        mKeyBytes[idBytes.size + 1] = (color shr 16 and 15).toByte()
        mKeyBytes[idBytes.size + 2] = (color shr 8 and 15).toByte()
        mKeyBytes[idBytes.size + 3] = (color and 15).toByte()
        mHashCode = Arrays.hashCode(mKeyBytes)
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        if (!toTransform.hasAlpha()) {
            return toTransform
        }
        var result = pool[outWidth, outHeight, Bitmap.Config.ARGB_8888]
        if (result == null) {
            result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result)
        canvas.drawColor(mBackgroundColor)
        canvas.drawBitmap(toTransform, 0.0f, 0.0f, null)
        return result
    }

    override fun equals(o: Any?): Boolean {
        return o is OpaqueBitmapTransformation && o.mBackgroundColor == mBackgroundColor
    }

    override fun hashCode(): Int {
        return mHashCode
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(mKeyBytes)
    }

}