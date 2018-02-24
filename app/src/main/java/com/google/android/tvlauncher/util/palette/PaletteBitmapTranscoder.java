package com.google.android.tvlauncher.util.palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

class PaletteBitmapTranscoder implements ResourceTranscoder<Bitmap, PaletteBitmapContainer> {
    private final BitmapPool mBitmapPool;

    PaletteBitmapTranscoder(Context context) {
        this.mBitmapPool = Glide.get(context).getBitmapPool();
    }

    public Resource<PaletteBitmapContainer> transcode(Resource<Bitmap> toTranscode) {
        Bitmap bitmap = (Bitmap) toTranscode.get();
        return new PaletteBitmapResource(new PaletteBitmapContainer(bitmap, Palette.from(bitmap).generate()), this.mBitmapPool);
    }

    @Nullable
    @Override
    public Resource<PaletteBitmapContainer> transcode(@NonNull Resource<Bitmap> toTranscode, @NonNull Options options) {
        return transcode(toTranscode);
    }
}
