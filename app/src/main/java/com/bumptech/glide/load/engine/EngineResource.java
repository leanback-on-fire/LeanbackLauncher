package com.bumptech.glide.load.engine;

import android.os.Looper;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;

class EngineResource<Z> implements Resource<Z> {
    private int acquired;
    private final boolean isCacheable;
    private boolean isRecycled;
    private Key key;
    private ResourceListener listener;
    private final Resource<Z> resource;

    interface ResourceListener {
        void onResourceReleased(Key key, EngineResource<?> engineResource);
    }

    EngineResource(Resource<Z> toWrap, boolean isCacheable) {
        this.resource = (Resource) Preconditions.checkNotNull(toWrap);
        this.isCacheable = isCacheable;
    }

    void setResourceListener(Key key, ResourceListener listener) {
        this.key = key;
        this.listener = listener;
    }

    boolean isCacheable() {
        return this.isCacheable;
    }

    public Class<Z> getResourceClass() {
        return this.resource.getResourceClass();
    }

    public Z get() {
        return this.resource.get();
    }

    public int getSize() {
        return this.resource.getSize();
    }

    public void recycle() {
        if (this.acquired > 0) {
            throw new IllegalStateException("Cannot recycle a resource while it is still acquired");
        } else if (this.isRecycled) {
            throw new IllegalStateException("Cannot recycle a resource that has already been recycled");
        } else {
            this.isRecycled = true;
            this.resource.recycle();
        }
    }

    void acquire() {
        if (this.isRecycled) {
            throw new IllegalStateException("Cannot acquire a recycled resource");
        } else if (Looper.getMainLooper().equals(Looper.myLooper())) {
            this.acquired++;
        } else {
            throw new IllegalThreadStateException("Must call acquire on the main thread");
        }
    }

    void release() {
        if (this.acquired <= 0) {
            throw new IllegalStateException("Cannot release a recycled or not yet acquired resource");
        } else if (Looper.getMainLooper().equals(Looper.myLooper())) {
            int i = this.acquired - 1;
            this.acquired = i;
            if (i == 0) {
                this.listener.onResourceReleased(this.key, this);
            }
        } else {
            throw new IllegalThreadStateException("Must call release on the main thread");
        }
    }

    public String toString() {
        boolean z = this.isCacheable;
        String valueOf = String.valueOf(this.listener);
        String valueOf2 = String.valueOf(this.key);
        int i = this.acquired;
        boolean z2 = this.isRecycled;
        String valueOf3 = String.valueOf(this.resource);
        return new StringBuilder(((String.valueOf(valueOf).length() + 101) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("EngineResource{isCacheable=").append(z).append(", listener=").append(valueOf).append(", key=").append(valueOf2).append(", acquired=").append(i).append(", isRecycled=").append(z2).append(", resource=").append(valueOf3).append("}").toString();
    }
}
