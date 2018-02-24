package com.google.android.tvlauncher.util.palette;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

public class PaletteBitmapContainer {
    private final Bitmap mBitmap;
    private final Palette mPalette;

    PaletteBitmapContainer(@NonNull Bitmap bitmap, @NonNull Palette palette) {
        this.mBitmap = bitmap;
        this.mPalette = palette;
    }

    public Palette getPalette() {
        return this.mPalette;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }
}
