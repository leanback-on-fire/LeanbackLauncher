package com.amazon.tv.leanbacklauncher.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.trace.AppTrace
import com.amazon.tv.leanbacklauncher.trace.AppTrace.TraceTag
import com.amazon.tv.leanbacklauncher.util.Partner
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey

class WallpaperDownloader(
    private val mContext: Context,
    downloadDelayMillis: Int,
    downloadTimeoutMillis: Int,
    private val mListener: OnDownloadFinishedListener
) {
    private val mBitmapTransform: WallpaperBitmapTransform
    private var mDelivering = false
    private val mDownloadDelay: Int
    private val mDownloadTimeout: Int
    private var mDownloadedImage: WallpaperHolder? = null
    private var mEnabled = false
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    this@WallpaperDownloader.download()
                    return
                }
                2 -> {
                    Log.w(
                        "WallpaperDownloader",
                        "Timeout fetching wallpaper image: " + mTarget.request
                    )
                    mRequestManager.clear(mTarget)
                    onDownloadFinished()
                    return
                }
                else -> return
            }
        }
    }
    private var mNextImage: WallpaperHolder? = null
    private var mPreviousImage: WallpaperHolder? = null
    private val mRequestManager: RequestManager
    private var mRequestedImage: WallpaperHolder? = null
    private val mTarget: CustomTarget<Bitmap> = object : CustomTarget<Bitmap>(1920, 1080) {
        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
            if (mRequestedImage != null) {
                mHandler.removeMessages(2)
                mRequestedImage!!.drawable = BitmapDrawable(mContext.resources, bitmap)
                request = null
                AppTrace.endAsyncSection(mRequestedImage!!.traceTag)
                onDownloadFinished()
            }
        }
        override fun onLoadCleared(placeholder: Drawable?) {}
        override fun onLoadFailed(errorDrawable: Drawable?) {
            mHandler.removeMessages(2)
            onDownloadFinished()
        }
    }

    interface OnDownloadFinishedListener {
        fun onDownloadComplete(drawable: Drawable?)
    }

    private class WallpaperHolder {
        var drawable: Drawable? = null
        var imageUri: String? = null
        var signature: String? = null
        var traceTag: TraceTag? = null
        fun equals(other: WallpaperHolder?): Boolean {
            return other != null && TextUtils.equals(imageUri, other.imageUri) && TextUtils.equals(
                signature, other.signature
            )
        }
    }

    fun reset() {
        mHandler.removeCallbacksAndMessages(null)
        mRequestManager.clear(mTarget)
        mRequestedImage = null
        mDownloadedImage = null
        mNextImage = null
        mPreviousImage = null
    }

    fun download(imageUri: String?, signature: String?) {
        if (mRequestedImage != null) {
            mRequestManager.clear(mTarget)
        }
        mRequestedImage = WallpaperHolder()
        mRequestedImage!!.imageUri = imageUri
        mRequestedImage!!.signature = signature
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendMessageDelayed(mHandler.obtainMessage(1), mDownloadDelay.toLong())
    }

    fun setEnabled(enabled: Boolean) {
        mEnabled = enabled
        if (enabled && mDownloadedImage != null) {
            deliver()
        }
    }

    private fun download() {
        if (mRequestedImage != null) {
            if (mRequestedImage!!.imageUri != null) {
                mHandler.sendEmptyMessageDelayed(2, mDownloadTimeout.toLong())
                mRequestedImage!!.traceTag = AppTrace.beginAsyncSection("Background image load")
                mRequestManager.asBitmap()
                    .load(mRequestedImage!!.imageUri)
                    .apply(
                        RequestOptions()
                            .signature(ObjectKey(mRequestedImage!!.signature!!))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(mBitmapTransform)
                    ) // removed context,
                    .into(mTarget)
                return
            }
            mRequestManager.clear(mTarget)
            onDownloadFinished()
        }
    }

    private fun onDownloadFinished() {
        if (mRequestedImage != null) {
            mDownloadedImage = mRequestedImage
            mRequestedImage = null
            if (mEnabled && !mDelivering) {
                deliver()
            }
        }
    }

    private fun deliver() {
        if (mDownloadedImage!!.equals(mPreviousImage) || mDownloadedImage!!.equals(mNextImage)) {
            mDownloadedImage = null
            return
        }
        mDelivering = true
        mNextImage = mDownloadedImage
        mDownloadedImage = null
        mListener.onDownloadComplete(mNextImage!!.drawable)
    }

    fun onImageDelivered() {
        mDelivering = false
        mPreviousImage = mNextImage
        mNextImage = null
        if (mDownloadedImage != null) {
            deliver()
        }
    }

    val pendingRequest: Request?
        get() = mTarget.request

    init {
        val resources = mContext.resources
        mDownloadDelay = downloadDelayMillis
        mDownloadTimeout = downloadTimeoutMillis
        var maskBitmap = Partner.get(mContext).systemBackgroundMask
        if (maskBitmap == null) {
            maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_protection)
        }
        mRequestManager = Glide.with(mContext)
        mBitmapTransform = WallpaperBitmapTransform(mContext, maskBitmap)
    }
}