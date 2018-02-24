package com.google.android.tvlauncher.util.palette;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

class PaletteBitmapResource implements Resource<PaletteBitmapContainer> {
    private final BitmapPool mBitmapPool;
    private final PaletteBitmapContainer mPaletteBitmapContainer;

    PaletteBitmapResource(@NonNull PaletteBitmapContainer paletteBitmapContainer, @NonNull BitmapPool bitmapPool) {
        this.mPaletteBitmapContainer = paletteBitmapContainer;
        this.mBitmapPool = bitmapPool;
    }

    public Class<PaletteBitmapContainer> getResourceClass() {
        return PaletteBitmapContainer.class;
    }

    public PaletteBitmapContainer get() {
        return this.mPaletteBitmapContainer;
    }

    public int getSize() {
        return Util.getBitmapByteSize(this.mPaletteBitmapContainer.getBitmap());
    }

    public void recycle() {
        this.mBitmapPool.put(this.mPaletteBitmapContainer.getBitmap());
    }
}
