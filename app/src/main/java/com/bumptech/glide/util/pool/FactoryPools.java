package com.bumptech.glide.util.pool;

import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class FactoryPools {
    private static final Resetter<Object> EMPTY_RESETTER = new Resetter<Object>() {
        public void reset(Object object) {
        }
    };

    public interface Poolable {
        StateVerifier getVerifier();
    }

    public interface Factory<T> {
        T create();
    }

    public interface Resetter<T> {
        void reset(T t);
    }

    private static final class FactoryPool<T> implements Pool<T> {
        private final Factory<T> factory;
        private final Pool<T> pool;
        private final Resetter<T> resetter;

        FactoryPool(Pool<T> pool, Factory<T> factory, Resetter<T> resetter) {
            this.pool = pool;
            this.factory = factory;
            this.resetter = resetter;
        }

        public T acquire() {
            T result = this.pool.acquire();
            if (result == null) {
                result = this.factory.create();
                if (Log.isLoggable("FactoryPools", 2)) {
                    String valueOf = String.valueOf(result.getClass());
                    Log.v("FactoryPools", new StringBuilder(String.valueOf(valueOf).length() + 12).append("Created new ").append(valueOf).toString());
                }
            }
            if (result instanceof Poolable) {
                ((Poolable) result).getVerifier().setRecycled(false);
            }
            return result;
        }

        public boolean release(T instance) {
            if (instance instanceof Poolable) {
                ((Poolable) instance).getVerifier().setRecycled(true);
            }
            this.resetter.reset(instance);
            return this.pool.release(instance);
        }
    }

    public static <T extends Poolable> Pool<T> simple(int size, Factory<T> factory) {
        return build(new SimplePool(size), factory);
    }

    public static <T extends Poolable> Pool<T> threadSafe(int size, Factory<T> factory) {
        return build(new SynchronizedPool(size), factory);
    }

    public static <T> Pool<List<T>> threadSafeList() {
        return threadSafeList(20);
    }

    public static <T> Pool<List<T>> threadSafeList(int size) {
        return build(new SynchronizedPool(size), new Factory<List<T>>() {
            public List<T> create() {
                return new ArrayList();
            }
        }, new Resetter<List<T>>() {
            public void reset(List<T> object) {
                object.clear();
            }
        });
    }

    private static <T extends Poolable> Pool<T> build(Pool<T> pool, Factory<T> factory) {
        return build(pool, factory, emptyResetter());
    }

    private static <T> Pool<T> build(Pool<T> pool, Factory<T> factory, Resetter<T> resetter) {
        return new FactoryPool(pool, factory, resetter);
    }

    private static <T> Resetter<T> emptyResetter() {
        return EMPTY_RESETTER;
    }
}
