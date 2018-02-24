package com.bumptech.glide.provider;

import android.support.v4.util.ArrayMap;
import com.bumptech.glide.load.engine.LoadPath;
import com.bumptech.glide.util.MultiClassKey;
import java.util.concurrent.atomic.AtomicReference;

public class LoadPathCache {
    private final ArrayMap<MultiClassKey, LoadPath<?, ?, ?>> cache = new ArrayMap();
    private final AtomicReference<MultiClassKey> keyRef = new AtomicReference();

    public boolean contains(Class<?> dataClass, Class<?> resourceClass, Class<?> transcodeClass) {
        boolean result;
        MultiClassKey key = getKey(dataClass, resourceClass, transcodeClass);
        synchronized (this.cache) {
            result = this.cache.containsKey(key);
        }
        this.keyRef.set(key);
        return result;
    }

    public <Data, TResource, Transcode> LoadPath<Data, TResource, Transcode> get(Class<Data> dataClass, Class<TResource> resourceClass, Class<Transcode> transcodeClass) {
        LoadPath<?, ?, ?> result;
        MultiClassKey key = getKey(dataClass, resourceClass, transcodeClass);
        synchronized (this.cache) {
            result = (LoadPath) this.cache.get(key);
        }
        this.keyRef.set(key);
        return result;
    }

    public void put(Class<?> dataClass, Class<?> resourceClass, Class<?> transcodeClass, LoadPath<?, ?, ?> loadPath) {
        synchronized (this.cache) {
            this.cache.put(new MultiClassKey(dataClass, resourceClass, transcodeClass), loadPath);
        }
    }

    private MultiClassKey getKey(Class<?> dataClass, Class<?> resourceClass, Class<?> transcodeClass) {
        MultiClassKey key = (MultiClassKey) this.keyRef.getAndSet(null);
        if (key == null) {
            key = new MultiClassKey();
        }
        key.set(dataClass, resourceClass, transcodeClass);
        return key;
    }
}
