package com.bumptech.glide.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LruCache<T, Y> {
    private final LinkedHashMap<T, Y> cache = new LinkedHashMap(100, 0.75f, true);
    private int currentSize = 0;
    private final int initialMaxSize;
    private int maxSize;

    public LruCache(int size) {
        this.initialMaxSize = size;
        this.maxSize = size;
    }

    protected int getSize(Y y) {
        return 1;
    }

    protected void onItemEvicted(T t, Y y) {
    }

    public synchronized int getCurrentSize() {
        return this.currentSize;
    }

    public synchronized Y get(T key) {
        return this.cache.get(key);
    }

    public synchronized Y put(T key, Y item) {
        Y y;
        if (getSize(item) >= this.maxSize) {
            onItemEvicted(key, item);
            y = null;
        } else {
            y = this.cache.put(key, item);
            if (item != null) {
                this.currentSize += getSize(item);
            }
            if (y != null) {
                this.currentSize -= getSize(y);
            }
            evict();
        }
        return y;
    }

    public synchronized Y remove(T key) {
        Y value;
        value = this.cache.remove(key);
        if (value != null) {
            this.currentSize -= getSize(value);
        }
        return value;
    }

    public void clearMemory() {
        trimToSize(0);
    }

    protected synchronized void trimToSize(int size) {
        while (this.currentSize > size) {
            Entry<T, Y> last = (Entry) this.cache.entrySet().iterator().next();
            Y toRemove = last.getValue();
            this.currentSize -= getSize(toRemove);
            T key = last.getKey();
            this.cache.remove(key);
            onItemEvicted(key, toRemove);
        }
    }

    private void evict() {
        trimToSize(this.maxSize);
    }
}
