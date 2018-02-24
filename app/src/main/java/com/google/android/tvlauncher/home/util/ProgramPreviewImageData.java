package com.google.android.tvlauncher.home.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

public class ProgramPreviewImageData {
    private final Bitmap mBitmap;
    private final Bitmap mBlurredBitmap;
    private final Palette mPalette;

    ProgramPreviewImageData(@NonNull Bitmap bitmap, @NonNull Bitmap blurredBitmap, @NonNull Palette palette) {
        this.mBitmap = bitmap;
        this.mBlurredBitmap = blurredBitmap;
        this.mPalette = palette;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public Bitmap getBlurredBitmap() {
        return this.mBlurredBitmap;
    }

    public Palette getPalette() {
        return this.mPalette;
    }
}
