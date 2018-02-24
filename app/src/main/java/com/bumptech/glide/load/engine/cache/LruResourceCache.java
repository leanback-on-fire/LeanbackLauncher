package com.bumptech.glide.load.engine.cache;

import android.annotation.SuppressLint;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.cache.MemoryCache.ResourceRemovedListener;
import com.bumptech.glide.util.LruCache;

public class LruResourceCache extends LruCache<Key, Resource<?>> implements MemoryCache {
    private ResourceRemovedListener listener;

    public /* bridge */ /* synthetic */ Resource put(Key key, Resource resource) {
        return (Resource) super.put(key, resource);
    }

    public /* bridge */ /* synthetic */ Resource remove(Key key) {
        return (Resource) super.remove(key);
    }

    public LruResourceCache(int size) {
        super(size);
    }

    public void setResourceRemovedListener(ResourceRemovedListener listener) {
        this.listener = listener;
    }

    protected void onItemEvicted(Key key, Resource<?> item) {
        if (this.listener != null) {
            this.listener.onResourceRemoved(item);
        }
    }

    protected int getSize(Resource<?> item) {
        return item.getSize();
    }

    @SuppressLint({"InlinedApi"})
    public void trimMemory(int level) {
        if (level >= 40) {
            clearMemory();
        } else if (level >= 20) {
            trimToSize(getCurrentSize() / 2);
        }
    }
}
