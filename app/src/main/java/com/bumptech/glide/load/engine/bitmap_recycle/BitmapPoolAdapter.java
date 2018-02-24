package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class BitmapPoolAdapter implements BitmapPool {
    public void put(Bitmap bitmap) {
        bitmap.recycle();
    }

    public Bitmap get(int width, int height, Config config) {
        return Bitmap.createBitmap(width, height, config);
    }

    public Bitmap getDirty(int width, int height, Config config) {
        return get(width, height, config);
    }

    public void clearMemory() {
    }

    public void trimMemory(int level) {
    }
}
