package com.amazon.tv.leanbacklauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class OpaqueBitmapTransformation extends BitmapTransformation {
    private final int mBackgroundColor;
    private final int mHashCode;
    private final byte[] mKeyBytes;

    public OpaqueBitmapTransformation(Context context, int color) {
        super();

        byte[] idBytes;
        this.mBackgroundColor = color;
        idBytes = "OpaqueBitmapTransformation".getBytes(StandardCharsets.UTF_8);
        this.mKeyBytes = new byte[(idBytes.length + 4)];
        System.arraycopy(idBytes, 0, this.mKeyBytes, 0, idBytes.length);
        this.mKeyBytes[idBytes.length] = (byte) ((color >> 24) & 15);
        this.mKeyBytes[idBytes.length + 1] = (byte) ((color >> 16) & 15);
        this.mKeyBytes[idBytes.length + 2] = (byte) ((color >> 8) & 15);
        this.mKeyBytes[idBytes.length + 3] = (byte) (color & 15);
        this.mHashCode = Arrays.hashCode(this.mKeyBytes);
    }

    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if (!toTransform.hasAlpha()) {
            return toTransform;
        }
        Bitmap result = pool.get(outWidth, outHeight, Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(outWidth, outHeight, Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        canvas.drawColor(this.mBackgroundColor);
        canvas.drawBitmap(toTransform, 0.0f, 0.0f, null);
        return result;
    }

    public boolean equals(Object o) {
        return (o instanceof OpaqueBitmapTransformation) && ((OpaqueBitmapTransformation) o).mBackgroundColor == this.mBackgroundColor;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(this.mKeyBytes);
    }
}
