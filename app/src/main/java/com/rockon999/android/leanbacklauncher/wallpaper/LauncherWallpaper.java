package com.rockon999.android.leanbacklauncher.wallpaper;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.graphics.ColorFilterCache;
import android.support.v17.leanback.graphics.ColorFilterDimmer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rockon999.android.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener;
import com.rockon999.android.leanbacklauncher.MainActivity;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.util.Util;
import com.rockon999.android.leanbacklauncher.wallpaper.AnimatedLayer.AnimationListener;
import com.rockon999.android.leanbacklauncher.recline.util.BitmapWorkerOptions;
import com.rockon999.android.leanbacklauncher.recline.util.BitmapWorkerOptions.Builder;
import com.rockon999.android.leanbacklauncher.recline.util.DrawableDownloader;
import com.rockon999.android.leanbacklauncher.recline.util.RefcountBitmapDrawable;
import com.rockon999.android.leanbacklauncher.animation.AnimatorLifecycle;

public class LauncherWallpaper extends FrameLayout implements AnimationListener, AnimatorLifecycle.OnAnimationFinishedListener, HomeScrollFractionListener {
    private static final String TAG = "LauncherWallpaper";
    private WallpaperImage mBackground;
    private ColorDrawable mBackgroundColor;
    private int mBackgroundDim;
    private final DrawableDownloader.BitmapCallback mBitmapDownloadCallback;
    private final DrawableDownloader mBitmapDownloader;
    private String mCurrentBackgroundUri;
    private ColorFilterDimmer mDimmer;
    private String mDownloadingUri;
    private AnimatedLayer mFadeMaskExt;
    private Handler mHandler;
    private boolean mInShyMode;
    private AnimatedLayer mOverlay;
    private boolean mPendingChange;
    private Drawable mPendingImage;
    private String mPendingImgUri;
    private final float mScrollDarkeningAmount;
    private final float mScrollDarkeningOffset;
    private int mScrollPosition;
    private FadeMaskView mVideoFadeMask;
    private ImageView mVideoFadeMaskExt;
    private WallpaperImage mWallpaper;
    private final int mWallpaperDelay;
    private final int mWallpaperFetchTimeout;
    private final WallpaperInstaller mWallpaperInstaller;
    private final WallpaperManager mWallpaperManager;
    private boolean mWallpaperReady;
    private final float mWallpaperScrollScale;
    private final float mZoom;
    private final float mZoomThreshold;

    /* renamed from: LauncherWallpaper.1 */
    class C02051 extends Handler {
        C02051() {
        }

        @SuppressLint("PrivateResource")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    LauncherWallpaper.this.setBackgroundImage((String) msg.obj);
                    break;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    PendingUpdateData args = (PendingUpdateData) msg.obj;
                    if (args != null)
                        LauncherWallpaper.this.setOverlayBackground(args.image, args.uri);
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                    LauncherWallpaper.this.mBitmapDownloader.cancelDownload(LauncherWallpaper.this.mBitmapDownloadCallback);
                    Log.w("LauncherWallpaper", "TIMEOUT fetching wallpeper image: " + LauncherWallpaper.this.mDownloadingUri);
                    LauncherWallpaper.this.mDownloadingUri = null;
                    LauncherWallpaper.this.setOverlayBackground(null, null);
                    break;
                default:
            }
        }
    }

    /* renamed from: LauncherWallpaper.2 */
    class C02062 extends DrawableDownloader.BitmapCallback {
        C02062() {
        }

        public void onCompleted(Drawable bitmap) {
            LauncherWallpaper.this.mHandler.removeMessages(3);
            if (bitmap == null) {
                LauncherWallpaper.this.setOverlayBackground(null, null);
            } else {
                LauncherWallpaper.this.setOverlayBackground(bitmap, LauncherWallpaper.this.mDownloadingUri);
            }
            LauncherWallpaper.this.releaseDrawable(bitmap);
            LauncherWallpaper.this.mDownloadingUri = null;
        }
    }

    private static class PendingUpdateData {
        public Drawable image;
        public String uri;

        private PendingUpdateData() {
        }
    }

    public LauncherWallpaper(Context context) {
        this(context, null);
    }

    public LauncherWallpaper(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LauncherWallpaper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mInShyMode = true;
        this.mPendingChange = false;
        this.mHandler = new C02051();
        this.mBitmapDownloadCallback = new C02062();
        this.mBitmapDownloader = DrawableDownloader.getInstance(context);
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mWallpaperInstaller = WallpaperInstaller.getInstance(context);
        this.mScrollDarkeningOffset = (float) getContext().getResources().getDimensionPixelOffset(R.dimen.home_scroll_size_search);
        TypedValue out = new TypedValue();
        getResources().getValue(R.fraction.wallpaper_scroll_darkening_amount, out, true);
        this.mScrollDarkeningAmount = out.getFloat();
        getResources().getValue(R.fraction.wallpaper_to_launcher_scroll_scale, out, true);
        this.mWallpaperScrollScale = out.getFloat();
        getResources().getValue(R.fraction.wallpaper_zoom_amount, out, true);
        this.mZoom = out.getFloat();
        getResources().getValue(R.fraction.wallpaper_zoom_to_darkening_scale, out, true);
        this.mZoomThreshold = this.mScrollDarkeningOffset / out.getFloat();
        int backgroundColor = ContextCompat.getColor(context, R.color.launcher_background_color);
        this.mBackgroundColor = new ColorDrawable(backgroundColor);
        this.mDimmer = ColorFilterDimmer.create(ColorFilterCache.getColorFilterCache(backgroundColor), 0.0f, this.mScrollDarkeningAmount);
        this.mWallpaperDelay = getResources().getInteger(R.integer.wallpaper_update_delay);
        this.mWallpaperFetchTimeout = getResources().getInteger(R.integer.wallpaper_fetch_timeout);
        Bitmap maskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_protection);

        DisplayMetrics metrics = Util.getDisplayMetrics(context);
        this.mBitmapDownloader.registerPostProc(R.drawable.bg_protection, new BackgroundImagePostProcessor(context.getResources(), maskBitmap, metrics.widthPixels, metrics.heightPixels));
    }

    public void resetBackground() {
        Log.i(TAG, "resetBackground");
        this.mHandler.removeCallbacksAndMessages(null);
        this.mOverlay.cancelAnimation();
        this.mOverlay.setVisibility(8);
        this.mFadeMaskExt.cancelAnimation();
        this.mOverlay.setImageDrawable(null);
        this.mBackground.setImageDrawable(null);
        this.mCurrentBackgroundUri = null;
        updateChildVisibility();
        updateScrollPosition();
    }

    public boolean getShynessMode() {
        return this.mInShyMode;
    }

    public void setShynessMode(boolean shyMode) {
        this.mInShyMode = shyMode;
        Log.i(TAG, "setShynessMode->shyMode:" + shyMode);
        updateChildVisibility();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, null));
    }

    public void setOverlayBackground(Drawable drawable, String uri) {
        Log.i(TAG, "setOverlayBackground");
        MainActivity activity = (MainActivity) getContext();
        if (this.mOverlay.isAnimating() || activity.isLaunchAnimationInProgress()) {
            releaseDrawable(this.mPendingImage);
            this.mPendingChange = true;
            this.mPendingImage = addRef(drawable);
            this.mPendingImgUri = uri;
        } else if (drawable != null) {
            this.mCurrentBackgroundUri = uri;
            this.mOverlay.animateIn(drawable);
            this.mFadeMaskExt.animateIn(this.mBackgroundColor);
        } else {
            this.mCurrentBackgroundUri = null;
            this.mOverlay.animateOut(this.mBackground.getDrawable());
            this.mBackground.setVisibility(8);
            this.mFadeMaskExt.animateOut(this.mBackgroundColor);
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mWallpaper = (WallpaperImage) findViewById(R.id.wallpaper);
        this.mBackground = (WallpaperImage) findViewById(R.id.launcher_background);
        this.mOverlay = (AnimatedLayer) findViewById(R.id.animation_layer);
        this.mFadeMaskExt = (AnimatedLayer) findViewById(R.id.fade_mask_extension);
        this.mVideoFadeMask = (FadeMaskView) findViewById(R.id.video_fade_mask);
        this.mVideoFadeMaskExt = (ImageView) findViewById(R.id.video_fade_mask_extension);
        if (this.mVideoFadeMask != null) {
            Bitmap videoMask = BitmapFactory.decodeResource(getResources(), R.drawable.bg_protection_video);
            this.mVideoFadeMask.setBitmap(videoMask);
        }
        this.mOverlay.setAnimationListener(this);
        ((MainActivity) getContext()).setOnLaunchAnimationFinishedListener(this);
    }

    public void onBackgroundImageChanged(String imageUri, boolean notifActive) {
        if (!this.mInShyMode) {
            return;
        }
        if (imageUri == null || !(TextUtils.equals(imageUri, this.mPendingImgUri) || TextUtils.equals(imageUri, this.mDownloadingUri))) {
            this.mHandler.removeCallbacksAndMessages(null);
            cancelPendingUpdate();
            this.mBitmapDownloader.cancelDownload(this.mBitmapDownloadCallback);
            this.mDownloadingUri = null;
            if (!TextUtils.equals(imageUri, this.mCurrentBackgroundUri)) {
                if (notifActive || imageUri != null) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, imageUri), (long) this.mWallpaperDelay);
                }
            }
        }
    }

    private void cancelPendingUpdate() {
        if (this.mPendingChange) {
            this.mPendingChange = false;
            releaseDrawable(this.mPendingImage);
            this.mPendingImage = null;
        }
        this.mPendingImgUri = null;
    }

    private void setBackgroundImage(String imageUri) {
        Log.i(TAG, "setBackgroundImage:" + imageUri);
     /*   this.mBitmapDownloader.cancelDownload(this.mBitmapDownloadCallback);
        this.mDownloadingUri = null;
        if (imageUri != null) {
            fetchWallpaperImage(imageUri);
        } else {
            setOverlayBackground(null, null);
        }*/
    }

    private void fetchWallpaperImage(String uri) {
        this.mHandler.removeMessages(3);
        BitmapWorkerOptions options = new Builder(getContext().getApplicationContext()).resource(Uri.parse(uri)).cacheFlag(1).postProcId(R.drawable.bg_protection).width(1920).height(1080).build();
        this.mDownloadingUri = uri;
        this.mBitmapDownloader.getBitmap(options, this.mBitmapDownloadCallback);
        this.mHandler.sendEmptyMessageDelayed(3, (long) this.mWallpaperFetchTimeout);
    }

    private void releaseDrawable(Drawable drawable) {
        if (drawable instanceof RefcountBitmapDrawable) {
            int releaseRef = ((RefcountBitmapDrawable) drawable).getRefcountObject().releaseRef();
        }
    }

    private Drawable addRef(Drawable drawable) {
        if (drawable instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) drawable).getRefcountObject().addRef();
        }
        return drawable;
    }

    public void onAnimationFinished() {
        applyPendingUpdateIfNecessary();
    }

    public void animationDone(boolean visible) {
        if (visible) {
            this.mBackground.setImageDrawable(this.mOverlay.getDrawable());
            this.mOverlay.setVisibility(8);
        } else {
            this.mOverlay.setImageDrawable(null);
        }
        updateChildVisibility();
        applyPendingUpdateIfNecessary();
    }

    private void applyPendingUpdateIfNecessary() {
        if (this.mPendingChange) {
            PendingUpdateData args = new PendingUpdateData();
            args.image = this.mPendingImage;
            args.uri = this.mPendingImgUri;
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, args));
            this.mPendingImage = null;
            this.mPendingChange = false;
            this.mPendingImgUri = null;
        }
    }

    public void onScrollPositionChanged(int position, float fractionFromTop) {
        Log.i(TAG, "onScrollPositionChanged");
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
      /*  int newPos = Math.round(((float) this.mScrollPosition) / this.mWallpaperScrollScale);
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
        Log.i(TAG, "updateScrollPosition->dimLevel:" + dimLevel);
        Log.i(TAG, "updateScrollPosition->zoomLevel:" + zoomLevel);
        this.mBackground.setZoomLevel(zoomLevel);
        this.mOverlay.setZoomLevel(zoomLevel);
        this.mDimmer.setActiveLevel(dimLevel);
        this.mBackground.setColorFilter(this.mDimmer.getColorFilter());
        this.mBackgroundDim = Color.argb((int) ((this.mScrollDarkeningAmount * 255.0f) * (1.0f - dimLevel)), 0, 0, 0);
        this.mOverlay.setColorFilter(this.mDimmer.getColorFilter());
        invalidate();*/
    }

    private void updateChildVisibility() {
        Log.i(TAG, "updateChildVisibility");
        if (this.mInShyMode) {
            this.mVideoFadeMask.setVisibility(8);
            this.mVideoFadeMaskExt.setVisibility(8);
            if (this.mCurrentBackgroundUri != null) {
                this.mBackground.setVisibility(0);
                this.mFadeMaskExt.setVisibility(0);
            } else {
                this.mBackground.setVisibility(8);
                this.mFadeMaskExt.setVisibility(8);
            }
            if (this.mWallpaper != null) {
                this.mWallpaper.setVisibility(0);
                return;
            }
            return;
        }
        this.mVideoFadeMask.setVisibility(0);
        this.mVideoFadeMaskExt.setVisibility(0);
        this.mBackground.setVisibility(8);
        this.mFadeMaskExt.setVisibility(0);
        if (this.mWallpaper != null) {
            this.mWallpaper.setVisibility(8);
        }
    }

    private void loadWallpaperIfNeeded() {
        Log.i(TAG, "loadWallpaperIfNeeded");
        if (!this.mWallpaperReady && this.mInShyMode) {
            Bitmap bitmap = this.mWallpaperInstaller.getWallpaperBitmap();
            this.mWallpaper.setImageBitmap(bitmap);
            this.mWallpaper.setLayoutParams(new LayoutParams(-1, bitmap.getHeight()));
            this.mWallpaperReady = true;
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.mInShyMode) {
            canvas.drawColor(this.mBackgroundDim, Mode.SRC);
        }
        super.dispatchDraw(canvas);
    }
}
