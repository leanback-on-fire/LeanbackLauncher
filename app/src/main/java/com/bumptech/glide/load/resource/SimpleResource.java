package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;

public class SimpleResource<T> implements Resource<T> {
    protected final T data;

    public SimpleResource(T data) {
        this.data = Preconditions.checkNotNull(data);
    }

    public Class<T> getResourceClass() {
        return this.data.getClass();
    }

    public final T get() {
        return this.data;
    }

    public final int getSize() {
        return 1;
    }

    public void recycle() {
    }
}
