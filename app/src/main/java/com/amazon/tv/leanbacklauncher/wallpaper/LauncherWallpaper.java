package com.amazon.tv.leanbacklauncher.wallpaper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.leanback.graphics.ColorFilterCache;
import androidx.leanback.graphics.ColorFilterDimmer;

import com.amazon.tv.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener;
import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.animation.AnimatorLifecycle.OnAnimationFinishedListener;
import com.amazon.tv.leanbacklauncher.trace.AppTrace;
import com.amazon.tv.leanbacklauncher.util.Partner;
import com.amazon.tv.leanbacklauncher.wallpaper.AnimatedLayer.AnimationListener;

public class LauncherWallpaper extends FrameLayout implements HomeScrollFractionListener, OnAnimationFinishedListener, AnimationListener, WallpaperDownloader.OnDownloadFinishedListener {
    private WallpaperImage mBackground;
    private ColorFilterDimmer mDimmer;
    private final WallpaperDownloader mDownloader;
    private AnimatedLayer mFadeMaskExt;
    private boolean mHasBackgroundImage;
    private boolean mInShyMode;
    private AnimatedLayer mOverlay;
    private final float mScrollDarkeningAmount;
    private final float mScrollDarkeningOffset;
    private int mScrollPosition;
    private FadeMaskView mVideoFadeMask;
    private ImageView mVideoFadeMaskExt;
    private WallpaperImage mWallpaper;
    private final WallpaperInstaller mWallpaperInstaller;
    private boolean mWallpaperReady;
    private final float mWallpaperScrollScale;
    private final float mZoom;
    private final float mZoomThreshold;

    public LauncherWallpaper(Context context) {
        this(context, null);
    }

    public LauncherWallpaper(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LauncherWallpaper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mInShyMode = true;
        this.mWallpaperInstaller = WallpaperInstaller.getInstance(context);
        Resources resources = getContext().getResources();
        this.mDownloader = new WallpaperDownloader(context, resources.getInteger(R.integer.wallpaper_update_delay), resources.getInteger(R.integer.wallpaper_fetch_timeout), this);
        this.mScrollDarkeningOffset = (float) resources.getDimensionPixelOffset(R.dimen.home_scroll_size_search);
        this.mScrollDarkeningAmount = getResources().getFraction(R.fraction.wallpaper_scroll_darkening_amount, 1, 1);
        this.mWallpaperScrollScale = getResources().getFraction(R.fraction.wallpaper_to_launcher_scroll_scale, 1, 1);
        this.mZoom = getResources().getFraction(R.fraction.wallpaper_zoom_amount, 1, 1);
        this.mZoomThreshold = this.mScrollDarkeningOffset / getResources().getFraction(R.fraction.wallpaper_zoom_to_darkening_scale, 1, 1);
        this.mDimmer = ColorFilterDimmer.create(ColorFilterCache.getColorFilterCache(ContextCompat.getColor(context, R.color.launcher_background_color)), 0.0f, this.mScrollDarkeningAmount);
    }

    public void resetBackground() {
        this.mOverlay.cancelAnimation();
        this.mOverlay.setVisibility(View.GONE);
        this.mFadeMaskExt.cancelAnimation();
        this.mOverlay.setImageDrawable(null);
        this.mBackground.setImageDrawable(null);
        this.mDownloader.reset();
        this.mHasBackgroundImage = false;
        updateChildVisibility();
        updateScrollPosition();
    }

    public boolean getShynessMode() {
        return this.mInShyMode;
    }

    public void setShynessMode(boolean shyMode) {
        this.mInShyMode = shyMode;
        updateChildVisibility();
        updateDownloaderEnabled();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mWallpaper = findViewById(R.id.wallpaper);
        this.mBackground = findViewById(R.id.launcher_background);
        this.mOverlay = findViewById(R.id.animation_layer);
        this.mFadeMaskExt = findViewById(R.id.fade_mask_extension);
        this.mVideoFadeMask = findViewById(R.id.video_fade_mask);
        this.mVideoFadeMaskExt = findViewById(R.id.video_fade_mask_extension);
        if (this.mVideoFadeMask != null) {
            Bitmap videoMask = Partner.get(getContext()).getSystemBackgroundVideoMask();
            if (videoMask == null) {
                videoMask = BitmapFactory.decodeResource(getResources(), R.drawable.bg_protection_video);
            }
            this.mVideoFadeMask.setBitmap(videoMask);
        }
        this.mOverlay.setAnimationListener(this);
        ((MainActivity) getContext()).setOnLaunchAnimationFinishedListener(this);
    }

    public void onAnimationFinished() {
        updateDownloaderEnabled();
    }

    public void onBackgroundImageChanged(String imageUri, String signature) {
        this.mDownloader.download(imageUri, signature);
    }

    public void onDownloadComplete(Drawable drawable) {
        if (drawable != null) {
            this.mHasBackgroundImage = true;
            this.mOverlay.animateIn(drawable);
        } else if (this.mHasBackgroundImage) {
            this.mOverlay.animateOut(this.mBackground.getDrawable());
            this.mBackground.setVisibility(View.GONE);
            this.mHasBackgroundImage = false;
        } else {
            this.mDownloader.onImageDelivered();
        }
    }

    public void animationDone(boolean visible) {
        if (visible) {
            this.mBackground.setImageDrawable(this.mOverlay.getDrawable());
            this.mOverlay.setVisibility(View.GONE);
        } else {
            this.mOverlay.setImageDrawable(null);
        }
        updateChildVisibility();
        this.mDownloader.onImageDelivered();
    }

    public void onScrollPositionChanged(int position, float fractionFromTop) {
        this.mScrollPosition = position;
        updateScrollPosition();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            updateScrollPosition();
        }
    }

    private void updateScrollPosition() {
        loadWallpaperIfNeeded();
        int newPos = Math.round(((float) this.mScrollPosition) / this.mWallpaperScrollScale);
        this.mBackground.setY((float) newPos);
        this.mOverlay.setY((float) newPos);
        this.mVideoFadeMask.setY((float) newPos);
        if (this.mWallpaper != null) {
            this.mWallpaper.setY((float) newPos);
        }
        int maskExtY = getMeasuredHeight() + newPos;
        this.mFadeMaskExt.setY((float) maskExtY);
        this.mVideoFadeMaskExt.setY((float) maskExtY);
        float dimLevel = (1.0f - Math.min(1.0f, ((float) Math.abs(this.mScrollPosition)) / this.mScrollDarkeningOffset)) * this.mScrollDarkeningAmount;
        float zoomLevel = this.mZoom * (1.0f - Math.min(1.0f, ((float) Math.abs(this.mScrollPosition)) / this.mZoomThreshold));
        this.mBackground.setZoomLevel(zoomLevel);
        this.mOverlay.setZoomLevel(zoomLevel);
        this.mDimmer.setActiveLevel(dimLevel);
        this.mBackground.setColorFilter(this.mDimmer.getColorFilter());
        this.mOverlay.setColorFilter(this.mDimmer.getColorFilter());
        invalidate();
    }

    private void updateChildVisibility() {
        int i = 8;
        if (this.mInShyMode) {
            this.mVideoFadeMask.setVisibility(View.GONE);
            this.mVideoFadeMaskExt.setVisibility(View.GONE);
            WallpaperImage wallpaperImage = this.mBackground;
            if (this.mHasBackgroundImage) {
                i = 0;
            }
            wallpaperImage.setVisibility(i);
            if (this.mWallpaper != null) {
                this.mWallpaper.setVisibility(View.VISIBLE);
                return;
            }
            return;
        }
        this.mVideoFadeMask.setVisibility(View.VISIBLE);
        this.mVideoFadeMaskExt.setVisibility(View.VISIBLE);
        this.mBackground.setVisibility(View.GONE);
        if (this.mWallpaper != null) {
            this.mWallpaper.setVisibility(View.GONE);
        }
    }

    private void updateDownloaderEnabled() {
        WallpaperDownloader wallpaperDownloader = this.mDownloader;
        boolean z = this.mInShyMode && !((MainActivity) getContext()).isLaunchAnimationInProgress();
        wallpaperDownloader.setEnabled(z);
    }

    private void loadWallpaperIfNeeded() {
        if (!this.mWallpaperReady && this.mInShyMode) {
            AppTrace.beginSection("WallpaperLoad");
            try {
                Bitmap bitmap = this.mWallpaperInstaller.getWallpaperBitmap();
                this.mWallpaper.setImageBitmap(bitmap);
                this.mWallpaper.setLayoutParams(new LayoutParams(-1, bitmap.getHeight()));
                this.mWallpaperReady = true;
            } catch (OutOfMemoryError e) {
                Log.e("LauncherWallpaper", "Cannot install wallpaper", e);
            } finally {
                AppTrace.endSection();
            }
        }
    }
}
