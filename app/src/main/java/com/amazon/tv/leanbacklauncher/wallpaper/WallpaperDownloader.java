package com.amazon.tv.leanbacklauncher.wallpaper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.trace.AppTrace;
import com.amazon.tv.leanbacklauncher.trace.AppTrace.TraceTag;
import com.amazon.tv.leanbacklauncher.util.Partner;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;

public class WallpaperDownloader {
    private final WallpaperBitmapTransform mBitmapTransform;
    private final Context mContext;
    private boolean mDelivering;
    private final int mDownloadDelay;
    private final int mDownloadTimeout;
    private WallpaperHolder mDownloadedImage;
    private boolean mEnabled;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WallpaperDownloader.this.download();
                    return;
                case 2:
                    Log.w("WallpaperDownloader", "Timeout fetching wallpaper image: " + WallpaperDownloader.this.mTarget.getRequest());
                    WallpaperDownloader.this.mRequestManager.clear(WallpaperDownloader.this.mTarget);
                    WallpaperDownloader.this.onDownloadFinished();
                    return;
                default:
                    return;
            }
        }
    };
    private final OnDownloadFinishedListener mListener;
    private WallpaperHolder mNextImage;
    private WallpaperHolder mPreviousImage;
    private final RequestManager mRequestManager;
    private WallpaperHolder mRequestedImage;
    private final SimpleTarget<Bitmap> mTarget = new SimpleTarget<Bitmap>(1920, 1080) {
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            if (WallpaperDownloader.this.mRequestedImage != null) {
                WallpaperDownloader.this.mHandler.removeMessages(2);
                WallpaperDownloader.this.mRequestedImage.drawable = new BitmapDrawable(WallpaperDownloader.this.mContext.getResources(), bitmap);
                setRequest(null);
                AppTrace.endAsyncSection(WallpaperDownloader.this.mRequestedImage.traceTag);
                WallpaperDownloader.this.onDownloadFinished();
            }
        }

        public void onLoadFailed(Drawable errorDrawable) {
            WallpaperDownloader.this.mHandler.removeMessages(2);
            WallpaperDownloader.this.onDownloadFinished();
        }
    };

    public interface OnDownloadFinishedListener {
        void onDownloadComplete(Drawable drawable);
    }

    private static class WallpaperHolder {
        Drawable drawable;
        String imageUri;
        String signature;
        TraceTag traceTag;

        private WallpaperHolder() {
        }

        public boolean equals(WallpaperHolder other) {
            return other != null && TextUtils.equals(this.imageUri, other.imageUri) && TextUtils.equals(this.signature, other.signature);
        }
    }

    public WallpaperDownloader(Context context, int downloadDelayMillis, int downloadTimeoutMillis, OnDownloadFinishedListener listener) {
        this.mContext = context;
        this.mListener = listener;
        Resources resources = context.getResources();
        this.mDownloadDelay = downloadDelayMillis;
        this.mDownloadTimeout = downloadTimeoutMillis;
        Bitmap maskBitmap = Partner.get(context).getSystemBackgroundMask();
        if (maskBitmap == null) {
            maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_protection);
        }
        this.mRequestManager = Glide.with(context);
        this.mBitmapTransform = new WallpaperBitmapTransform(context, maskBitmap);
    }

    public void reset() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mRequestManager.clear(this.mTarget);
        this.mRequestedImage = null;
        this.mDownloadedImage = null;
        this.mNextImage = null;
        this.mPreviousImage = null;
    }

    public void download(String imageUri, String signature) {
        if (this.mRequestedImage != null) {
            this.mRequestManager.clear(this.mTarget);
        }
        this.mRequestedImage = new WallpaperHolder();
        this.mRequestedImage.imageUri = imageUri;
        this.mRequestedImage.signature = signature;
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), this.mDownloadDelay);
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        if (enabled && this.mDownloadedImage != null) {
            deliver();
        }
    }

    private void download() {
        if (this.mRequestedImage != null) {
            if (this.mRequestedImage.imageUri != null) {
                this.mHandler.sendEmptyMessageDelayed(2, this.mDownloadTimeout);
                this.mRequestedImage.traceTag = AppTrace.beginAsyncSection("Background image load");
                this.mRequestManager.asBitmap()
                        .load(this.mRequestedImage.imageUri)
                        .apply(new RequestOptions()
                                .signature(new ObjectKey(this.mRequestedImage.signature))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transform(this.mBitmapTransform)) // removed context,
                        .into(this.mTarget);
                return;
            }
            this.mRequestManager.clear(this.mTarget);
            onDownloadFinished();
        }
    }

    private void onDownloadFinished() {
        if (this.mRequestedImage != null) {
            this.mDownloadedImage = this.mRequestedImage;
            this.mRequestedImage = null;
            if (this.mEnabled && !this.mDelivering) {
                deliver();
            }
        }
    }

    private void deliver() {
        if (this.mDownloadedImage.equals(this.mPreviousImage) || this.mDownloadedImage.equals(this.mNextImage)) {
            this.mDownloadedImage = null;
            return;
        }
        this.mDelivering = true;
        this.mNextImage = this.mDownloadedImage;
        this.mDownloadedImage = null;
        this.mListener.onDownloadComplete(this.mNextImage.drawable);
    }

    public void onImageDelivered() {
        this.mDelivering = false;
        this.mPreviousImage = this.mNextImage;
        this.mNextImage = null;
        if (this.mDownloadedImage != null) {
            deliver();
        }
    }

    Request getPendingRequest() {
        return this.mTarget.getRequest();
    }
}
