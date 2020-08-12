package com.amazon.tv.leanbacklauncher.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class WallpaperBitmapTransform extends BitmapTransformation {
    private final BitmapDrawable mMaskDrawable;
    private final int mMaskHeight;
    private final Rect mRect1 = new Rect();
    private final Rect mRect2 = new Rect();

    public WallpaperBitmapTransform(Context context, Bitmap mask) {
        super();
        this.mMaskDrawable = new BitmapDrawable(context.getResources(), mask);
        this.mMaskDrawable.setTileModeX(TileMode.REPEAT);
        this.mMaskDrawable.setTileModeY(TileMode.CLAMP);
        this.mMaskHeight = mask.getHeight();
    }

    protected Bitmap transform(BitmapPool pool, Bitmap image, int outWidth, int outHeight) {
        if (image == null) {
            return null;
        }
        Bitmap bitmap;
        Canvas canvas;
        int width = image.getWidth();
        int height = image.getHeight();
        if (width * outHeight <= outWidth * height) {
            int newHeight = (width * outHeight) / outWidth;
            bitmap = obtainBitmap(pool, width, newHeight, image.getConfig());
            canvas = new Canvas(bitmap);
            canvas.drawColor(-16777216);
            this.mRect1.set(0, 0, width, newHeight);
            canvas.drawBitmap(image, this.mRect1, this.mRect1, null);
        } else {
            int newWidth = (outWidth * height) / outHeight;
            bitmap = obtainBitmap(pool, newWidth, height, image.getConfig());
            canvas = new Canvas(bitmap);
            canvas.drawColor(-16777216);
            int left = (width - newWidth) / 2;
            this.mRect1.set(left, 0, left + newWidth, height);
            this.mRect2.set(0, 0, newWidth, height);
            canvas.drawBitmap(image, this.mRect1, this.mRect2, null);
        }
        float scale = ((float) canvas.getHeight()) / ((float) this.mMaskHeight);
        this.mRect2.set(0, 0, (int) (((float) canvas.getWidth()) / scale), (int) (((float) canvas.getHeight()) / scale));
        this.mMaskDrawable.setBounds(this.mRect2);
        canvas.scale(scale, scale);
        this.mMaskDrawable.draw(canvas);
        return bitmap;
    }

    private Bitmap obtainBitmap(BitmapPool pool, int width, int height, Config config) {
        Bitmap recycled = pool.getDirty(width, height, config);
        return recycled != null ? recycled : Bitmap.createBitmap(width, height, config);
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }
}
