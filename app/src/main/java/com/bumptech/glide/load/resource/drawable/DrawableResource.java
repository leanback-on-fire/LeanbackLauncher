package com.bumptech.glide.load.resource.drawable;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;

public abstract class DrawableResource<T extends Drawable> implements Resource<T> {
    protected final T drawable;

    public DrawableResource(T drawable) {
        this.drawable = (Drawable) Preconditions.checkNotNull(drawable);
    }

    public final T get() {
        return this.drawable.getConstantState().newDrawable();
    }
}
