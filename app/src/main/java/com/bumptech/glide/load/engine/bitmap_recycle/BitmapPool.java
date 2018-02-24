package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public interface BitmapPool {
    void clearMemory();

    Bitmap get(int i, int i2, Config config);

    Bitmap getDirty(int i, int i2, Config config);

    void put(Bitmap bitmap);

    void trimMemory(int i);
}
