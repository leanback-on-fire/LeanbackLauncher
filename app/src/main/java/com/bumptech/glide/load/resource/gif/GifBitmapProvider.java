package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

public final class GifBitmapProvider implements BitmapProvider {
    private final ArrayPool arrayPool;
    private final BitmapPool bitmapPool;

    public GifBitmapProvider(BitmapPool bitmapPool, ArrayPool arrayPool) {
        this.bitmapPool = bitmapPool;
        this.arrayPool = arrayPool;
    }

    public Bitmap obtain(int width, int height, Config config) {
        return this.bitmapPool.getDirty(width, height, config);
    }

    public void release(Bitmap bitmap) {
        this.bitmapPool.put(bitmap);
    }

    public byte[] obtainByteArray(int size) {
        if (this.arrayPool == null) {
            return new byte[size];
        }
        return (byte[]) this.arrayPool.get(size, byte[].class);
    }

    public void release(byte[] bytes) {
        if (this.arrayPool != null) {
            this.arrayPool.put(bytes, byte[].class);
        }
    }

    public int[] obtainIntArray(int size) {
        if (this.arrayPool == null) {
            return new int[size];
        }
        return (int[]) this.arrayPool.get(size, int[].class);
    }

    public void release(int[] array) {
        if (this.arrayPool != null) {
            this.arrayPool.put(array, int[].class);
        }
    }
}
