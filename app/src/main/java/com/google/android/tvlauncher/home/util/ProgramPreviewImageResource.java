package com.google.android.tvlauncher.home.util;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

class ProgramPreviewImageResource implements Resource<ProgramPreviewImageData> {
    private final BitmapPool mBitmapPool;
    private final ProgramPreviewImageData mProgramPreviewImageData;

    ProgramPreviewImageResource(@NonNull ProgramPreviewImageData programPreviewImageData, @NonNull BitmapPool bitmapPool) {
        this.mProgramPreviewImageData = programPreviewImageData;
        this.mBitmapPool = bitmapPool;
    }

    public Class<ProgramPreviewImageData> getResourceClass() {
        return ProgramPreviewImageData.class;
    }

    public ProgramPreviewImageData get() {
        return this.mProgramPreviewImageData;
    }

    public int getSize() {
        return Util.getBitmapByteSize(this.mProgramPreviewImageData.getBitmap());
    }

    public void recycle() {
        this.mBitmapPool.put(this.mProgramPreviewImageData.getBitmap());
    }
}
