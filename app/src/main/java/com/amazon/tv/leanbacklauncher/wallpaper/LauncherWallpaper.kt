package com.amazon.tv.leanbacklauncher.wallpaper

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.leanback.graphics.ColorFilterCache
import androidx.leanback.graphics.ColorFilterDimmer
import com.amazon.tv.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener
import com.amazon.tv.leanbacklauncher.MainActivity
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.animation.AnimatorLifecycle.OnAnimationFinishedListener
import com.amazon.tv.leanbacklauncher.trace.AppTrace
import com.amazon.tv.leanbacklauncher.util.Partner
import com.amazon.tv.leanbacklauncher.wallpaper.WallpaperDownloader.OnDownloadFinishedListener
import com.amazon.tv.leanbacklauncher.wallpaper.WallpaperInstaller.Companion.getInstance

class LauncherWallpaper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context, attrs, defStyle
), HomeScrollFractionListener, OnAnimationFinishedListener, AnimatedLayer.AnimationListener,
    OnDownloadFinishedListener {
    private lateinit var mBackground: WallpaperImage
    private val mDimmer: ColorFilterDimmer
    private val mDownloader: WallpaperDownloader
    private var mFadeMaskExt: AnimatedLayer? = null
    private var mHasBackgroundImage = false
    private var mInShyMode = true
    private lateinit var mOverlay: AnimatedLayer
    private val mScrollDarkeningAmount: Float
    private val mScrollDarkeningOffset: Float
    private var mScrollPosition = 0
    private var mVideoFadeMask: FadeMaskView? = null
    private var mVideoFadeMaskExt: ImageView? = null
    private var mWallpaper: WallpaperImage? = null
    private val mWallpaperInstaller: WallpaperInstaller? = getInstance(context)
    private var mWallpaperReady = false
    private val mWallpaperScrollScale: Float
    private val mZoom: Float
    private val mZoomThreshold: Float
    fun resetBackground() {
        mOverlay.cancelAnimation()
        mOverlay.visibility = GONE
        mFadeMaskExt!!.cancelAnimation()
        mOverlay.setImageDrawable(null)
        mBackground.setImageDrawable(null)
        mDownloader.reset()
        mHasBackgroundImage = false
        updateChildVisibility()
        updateScrollPosition()
    }

    var shynessMode: Boolean
        get() = mInShyMode
        set(shyMode) {
            mInShyMode = shyMode
            updateChildVisibility()
            updateDownloaderEnabled()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mWallpaper = findViewById(R.id.wallpaper)
        mBackground = findViewById(R.id.launcher_background)
        mOverlay = findViewById(R.id.animation_layer)
        mFadeMaskExt = findViewById(R.id.fade_mask_extension)
        mVideoFadeMask = findViewById(R.id.video_fade_mask)
        mVideoFadeMaskExt = findViewById(R.id.video_fade_mask_extension)
        if (mVideoFadeMask != null) {
            var videoMask = Partner.get(context).systemBackgroundVideoMask
            if (videoMask == null) {
                videoMask = BitmapFactory.decodeResource(resources, R.drawable.bg_protection_video)
            }
            mVideoFadeMask!!.setBitmap(videoMask)
        }
        mOverlay.setAnimationListener(this)
        (context as MainActivity).setOnLaunchAnimationFinishedListener(this)
    }

    override fun onAnimationFinished() {
        updateDownloaderEnabled()
    }

    fun onBackgroundImageChanged(imageUri: String?, signature: String?) {
        mDownloader.download(imageUri, signature)
    }

    override fun onDownloadComplete(drawable: Drawable?) {
        when {
            drawable != null -> {
                mHasBackgroundImage = true
                mOverlay.animateIn(drawable)
            }
            mHasBackgroundImage -> {
                mOverlay.animateOut(mBackground.drawable)
                mBackground.visibility = GONE
                mHasBackgroundImage = false
            }
            else -> {
                mDownloader.onImageDelivered()
            }
        }
    }

    override fun animationDone(visible: Boolean) {
        if (visible) {
            mBackground.setImageDrawable(mOverlay.drawable)
            mOverlay.visibility = GONE
        } else {
            mOverlay.setImageDrawable(null)
        }
        updateChildVisibility()
        mDownloader.onImageDelivered()
    }

    override fun onScrollPositionChanged(position: Int, fractionFromTop: Float) {
        mScrollPosition = position
        updateScrollPosition()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            updateScrollPosition()
        }
    }

    private fun updateScrollPosition() {
        loadWallpaperIfNeeded()
        val newPos = Math.round(mScrollPosition.toFloat() / mWallpaperScrollScale)
        mBackground.y = newPos.toFloat()
        mOverlay.y = newPos.toFloat()
        mVideoFadeMask!!.y = newPos.toFloat()
        if (mWallpaper != null) {
            mWallpaper!!.y = newPos.toFloat()
        }
        val maskExtY = measuredHeight + newPos
        mFadeMaskExt!!.y = maskExtY.toFloat()
        mVideoFadeMaskExt!!.y = maskExtY.toFloat()
        val dimLevel = (1.0f - Math.min(
            1.0f, Math.abs(mScrollPosition)
                .toFloat() / mScrollDarkeningOffset
        )) * mScrollDarkeningAmount
        val zoomLevel = mZoom * (1.0f - Math.min(
            1.0f, Math.abs(
                mScrollPosition
            )
                .toFloat() / mZoomThreshold
        ))
        mBackground.setZoomLevel(zoomLevel)
        mOverlay.setZoomLevel(zoomLevel)
        mDimmer.setActiveLevel(dimLevel)
        mBackground.colorFilter = mDimmer.colorFilter
        mOverlay.colorFilter = mDimmer.colorFilter
        invalidate()
    }

    private fun updateChildVisibility() {
        var i = 8
        if (mInShyMode) {
            mVideoFadeMask!!.visibility = GONE
            mVideoFadeMaskExt!!.visibility = GONE
            val wallpaperImage = mBackground
            if (mHasBackgroundImage) {
                i = 0
            }
            wallpaperImage.visibility = i
            if (mWallpaper != null) {
                mWallpaper!!.visibility = VISIBLE
                return
            }
            return
        }
        mVideoFadeMask!!.visibility = VISIBLE
        mVideoFadeMaskExt!!.visibility = VISIBLE
        mBackground.visibility = GONE
        if (mWallpaper != null) {
            mWallpaper!!.visibility = GONE
        }
    }

    private fun updateDownloaderEnabled() {
        val wallpaperDownloader = mDownloader
        val z = mInShyMode && !(context as MainActivity).isLaunchAnimationInProgress
        wallpaperDownloader.setEnabled(z)
    }

    private fun loadWallpaperIfNeeded() {
        if (!mWallpaperReady && mInShyMode) {
            AppTrace.beginSection("WallpaperLoad")
            try {
                val bitmap = mWallpaperInstaller!!.wallpaperBitmap
                mWallpaper!!.setImageBitmap(bitmap)
                mWallpaper!!.layoutParams = LayoutParams(-1, bitmap.height)
                mWallpaperReady = true
            } catch (e: OutOfMemoryError) {
                Log.e("LauncherWallpaper", "Cannot install wallpaper", e)
            } finally {
                AppTrace.endSection()
            }
        }
    }

    init {
        val resources = getContext().resources
        mDownloader = WallpaperDownloader(
            context,
            resources.getInteger(R.integer.wallpaper_update_delay),
            resources.getInteger(R.integer.wallpaper_fetch_timeout),
            this
        )
        mScrollDarkeningOffset = resources.getDimensionPixelOffset(R.dimen.home_scroll_size_search)
            .toFloat()
        mScrollDarkeningAmount =
            getResources().getFraction(R.fraction.wallpaper_scroll_darkening_amount, 1, 1)
        mWallpaperScrollScale =
            getResources().getFraction(R.fraction.wallpaper_to_launcher_scroll_scale, 1, 1)
        mZoom = getResources().getFraction(R.fraction.wallpaper_zoom_amount, 1, 1)
        mZoomThreshold = mScrollDarkeningOffset / getResources().getFraction(
            R.fraction.wallpaper_zoom_to_darkening_scale,
            1,
            1
        )
        mDimmer = ColorFilterDimmer.create(
            ColorFilterCache.getColorFilterCache(
                ContextCompat.getColor(
                    context, R.color.launcher_background_color
                )
            ), 0.0f, mScrollDarkeningAmount
        )
    }
}