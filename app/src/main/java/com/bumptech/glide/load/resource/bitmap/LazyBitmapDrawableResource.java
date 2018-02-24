package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

public class LazyBitmapDrawableResource implements Resource<BitmapDrawable> {
    private final Bitmap bitmap;
    private final BitmapPool bitmapPool;
    private final Resources resources;

    public static LazyBitmapDrawableResource obtain(Context context, Bitmap bitmap) {
        return obtain(context.getResources(), Glide.get(context).getBitmapPool(), bitmap);
    }

    public static LazyBitmapDrawableResource obtain(Resources resources, BitmapPool bitmapPool, Bitmap bitmap) {
        return new LazyBitmapDrawableResource(resources, bitmapPool, bitmap);
    }

    LazyBitmapDrawableResource(Resources resources, BitmapPool bitmapPool, Bitmap bitmap) {
        this.resources = (Resources) Preconditions.checkNotNull(resources);
        this.bitmapPool = (BitmapPool) Preconditions.checkNotNull(bitmapPool);
        this.bitmap = (Bitmap) Preconditions.checkNotNull(bitmap);
    }

    public Class<BitmapDrawable> getResourceClass() {
        return BitmapDrawable.class;
    }

    public BitmapDrawable get() {
        return new BitmapDrawable(this.resources, this.bitmap);
    }

    public int getSize() {
        return Util.getBitmapByteSize(this.bitmap);
    }

    public void recycle() {
        this.bitmapPool.put(this.bitmap);
    }
}
