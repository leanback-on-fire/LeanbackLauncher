package com.rockon999.android.leanbacklauncher.recline.util;

import android.content.Context;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.text.TextUtils;

public class BitmapWorkerOptions implements CachedTaskPool.TaskOption {
    private Config mBitmapConfig;
    private int mCacheFlag;
    private Context mContext;
    private int mHeight;
    private ShortcutIconResource mIconResource;
    private String mKey;
    private int mPostProcId;
    private Uri mResourceUri;
    private int mWidth;

    public static class Builder {
        private Config mBitmapConfig;
        private int mCacheFlag;
        private Context mContext;
        private int mHeight;
        private String mPackageName;
        private int mPostProcId;
        private String mResourceName;
        private Uri mResourceUri;
        private int mWidth;

        public Builder(Context context) {
            this.mWidth = 2048;
            this.mHeight = 2048;
            this.mContext = context.getApplicationContext();
            this.mCacheFlag = 0;
            this.mBitmapConfig = null;
            this.mPostProcId = -1;
        }

        public BitmapWorkerOptions build() {
            BitmapWorkerOptions options = new BitmapWorkerOptions();
            if (!TextUtils.isEmpty(this.mPackageName)) {
                options.mIconResource = new ShortcutIconResource();
                options.mIconResource.packageName = this.mPackageName;
                options.mIconResource.resourceName = this.mResourceName;
            }
            int largestDim = Math.max(this.mWidth, this.mHeight);
            if (largestDim > 2048) {
                double scale = 2048.0d / ((double) largestDim);
                this.mWidth = (int) (((double) this.mWidth) * scale);
                this.mHeight = (int) (((double) this.mHeight) * scale);
            }
            options.mResourceUri = this.mResourceUri;
            options.mWidth = this.mWidth;
            options.mHeight = this.mHeight;
            options.mContext = this.mContext;
            options.mCacheFlag = this.mCacheFlag;
            options.mBitmapConfig = this.mBitmapConfig;
            options.mPostProcId = this.mPostProcId;
            if (options.mIconResource != null || options.mResourceUri != null) {
                return options;
            }
            throw new RuntimeException("Both Icon and ResourceUri are null");
        }

        public Builder resource(Uri resourceUri) {
            this.mResourceUri = resourceUri;
            return this;
        }

        public Builder width(int width) {
            if (width > 0) {
                this.mWidth = width;
                return this;
            }
            throw new IllegalArgumentException("Can't set width to " + width);
        }

        public Builder height(int height) {
            if (height > 0) {
                this.mHeight = height;
                return this;
            }
            throw new IllegalArgumentException("Can't set height to " + height);
        }

        public Builder cacheFlag(int flag) {
            this.mCacheFlag = flag;
            return this;
        }

        public Builder postProcId(int postProcId) {
            this.mPostProcId = postProcId;
            return this;
        }
    }

    private BitmapWorkerOptions() {
    }

    public ShortcutIconResource getIconResource() {
        return this.mIconResource;
    }

    public Uri getResourceUri() {
        return this.mResourceUri;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public Context getContext() {
        return this.mContext;
    }

    public boolean isFromResource() {
        return getIconResource() != null || UriUtils.isAndroidResourceUri(getResourceUri()) || UriUtils.isShortcutIconResourceUri(getResourceUri());
    }

    public boolean isMemCacheEnabled() {
        return (this.mCacheFlag & 1) == 0;
    }

    public Config getBitmapConfig() {
        return this.mBitmapConfig;
    }

    public int getPostProcId() {
        return this.mPostProcId;
    }

    public String getCacheKey() {
        if (this.mKey == null) {
            String str;
            if (this.mIconResource != null) {
                str = this.mIconResource.packageName + "/" + this.mIconResource.resourceName;
            } else {
                str = this.mResourceUri.toString();
            }
            this.mKey = str;
            if (this.mPostProcId != 0) {
                this.mKey += " postproc:" + this.mPostProcId;
            }
        }
        return this.mKey;
    }

    public boolean isLocal() {
        return isFromResource();
    }

    public String toString() {
        if (this.mIconResource == null) {
            return "URI: " + this.mResourceUri;
        }
        return "PackageName: " + this.mIconResource.packageName + " Resource: " + this.mIconResource + " URI: " + this.mResourceUri;
    }
}
