package com.bumptech.glide.load.engine.bitmap_recycle;

public interface ArrayPool {
    void clearMemory();

    <T> T get(int i, Class<T> cls);

    <T> void put(T t, Class<T> cls);

    void trimMemory(int i);
}
