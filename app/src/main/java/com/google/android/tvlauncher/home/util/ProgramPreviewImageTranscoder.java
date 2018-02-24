package com.google.android.tvlauncher.home.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

public class ProgramPreviewImageTranscoder implements ResourceTranscoder<Bitmap, ProgramPreviewImageData> {
    private static final float BLURRED_BITMAP_SCALE = 0.5f;
    private static final float BLUR_RADIUS = 8.0f;
    private final BitmapPool mBitmapPool;
    private ScriptIntrinsicBlur mBlur;
    private final Context mContext;
    private RenderScript mRenderScript;

    public ProgramPreviewImageTranscoder(Context context) {
        this.mContext = context.getApplicationContext();
        this.mBitmapPool = Glide.get(context).getBitmapPool();
    }

    public Resource<ProgramPreviewImageData> transcode(Resource<Bitmap> toTranscode) {
        Bitmap bitmap = (Bitmap) toTranscode.get();
        return new ProgramPreviewImageResource(new ProgramPreviewImageData(bitmap, generateBlurredBitmap(bitmap), Palette.from(bitmap).generate()), this.mBitmapPool);
    }

    private Bitmap generateBlurredBitmap(Bitmap originalBitmap) {
        if (this.mRenderScript == null) {
            this.mRenderScript = RenderScript.create(this.mContext);
            this.mBlur = ScriptIntrinsicBlur.create(this.mRenderScript, Element.U8_4(this.mRenderScript));
            this.mBlur.setRadius(BLUR_RADIUS);
        }
        Matrix m = new Matrix();
        m.setScale(BLURRED_BITMAP_SCALE, BLURRED_BITMAP_SCALE);
        Bitmap inputBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), m, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        Allocation inputAllocation = Allocation.createFromBitmap(this.mRenderScript, inputBitmap);
        Allocation outputAllocation = Allocation.createFromBitmap(this.mRenderScript, outputBitmap);
        this.mBlur.setInput(inputAllocation);
        this.mBlur.forEach(outputAllocation);
        outputAllocation.copyTo(outputBitmap);
        return outputBitmap;
    }

    @Nullable
    @Override
    public Resource<ProgramPreviewImageData> transcode(@NonNull Resource<Bitmap> toTranscode, @NonNull Options options) {
        return transcode(toTranscode);
    }
}
