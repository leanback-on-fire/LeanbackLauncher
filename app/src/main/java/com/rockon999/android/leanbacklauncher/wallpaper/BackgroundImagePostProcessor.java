package com.rockon999.android.leanbacklauncher.wallpaper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import com.rockon999.android.leanbacklauncher.recline.util.PostProc;

class BackgroundImagePostProcessor implements PostProc<Bitmap> {
    private int mHeight;
    private final Bitmap mMask;
    private final Rect mRect1;
    private final Rect mRect2;
    private Resources mResources;
    private int mWidth;

    public BackgroundImagePostProcessor(Resources res, Bitmap mask, int width, int height) {
        this.mRect1 = new Rect();
        this.mRect2 = new Rect();
        this.mResources = res;
        this.mMask = mask;
        this.mWidth = width;
        this.mHeight = height;
    }

    public Bitmap postProcess(Bitmap image) {
        if (image == null || this.mMask == null) {
            return null;
        }
        Bitmap bitmap;
        Canvas canvas;
        int width = image.getWidth();
        int height = image.getHeight();
        if (this.mHeight * width <= this.mWidth * height) {
            int newHeight = (this.mHeight * width) / this.mWidth;
            bitmap = Bitmap.createBitmap(width, newHeight, image.getConfig());
            canvas = new Canvas(bitmap);
            canvas.drawColor(-16777216);
            this.mRect1.set(0, 0, width, newHeight);
            canvas.drawBitmap(image, this.mRect1, this.mRect1, null);
        } else {
            int newWidth = (this.mWidth * height) / this.mHeight;
            bitmap = Bitmap.createBitmap(newWidth, height, image.getConfig());
            canvas = new Canvas(bitmap);
            canvas.drawColor(-16777216);
            int left = (width - newWidth) / 2;
            this.mRect1.set(left, 0, left + newWidth, height);
            this.mRect2.set(0, 0, newWidth, height);
            canvas.drawBitmap(image, this.mRect1, this.mRect2, null);
        }
        BitmapDrawable maskDrawable = new BitmapDrawable(this.mResources, this.mMask);
        maskDrawable.setTileModeX(TileMode.REPEAT);
        maskDrawable.setTileModeY(TileMode.CLAMP);
        float scale = ((float) canvas.getHeight()) / ((float) this.mMask.getHeight());
        this.mRect2.set(0, 0, (int) (((float) canvas.getWidth()) / scale), (int) (((float) canvas.getHeight()) / scale));
        maskDrawable.setBounds(this.mRect2);
        canvas.scale(scale, scale);
        maskDrawable.draw(canvas);
        return bitmap;
    }
}
