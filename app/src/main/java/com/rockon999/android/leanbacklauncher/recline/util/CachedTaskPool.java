package com.rockon999.android.leanbacklauncher.recline.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.LruCache;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CachedTaskPool {
    private static final Executor DOWNLOADER_THREAD_POOL_EXECUTOR;
    private static final Executor LOCAL_DOWNLOADER_THREAD_POOL_EXECUTOR;
    private static CachedTaskPool sInstance;
    private static final Object sInstanceLock;
    private Context mContext;
    private final LruCache<String, CacheItem> mMemoryCache;

    public static abstract class TaskCompleteCallback<T> {
        SoftReference<AsyncTask<TaskOption, Void, T>> mTask;

        public abstract void onCompleted(T t);
    }

    public interface TaskOption {
        String getCacheKey();

        boolean isLocal();
    }

    /* renamed from: com.rockon999.android.recline.util.CachedTaskPool.1 */
    class C02191 extends LruCache<String, CacheItem> {
        C02191(int $anonymous0) {
            super($anonymous0);
        }

        protected int sizeOf(String key, CacheItem item) {
            return item.mByteCount;
        }

        protected void entryRemoved(boolean evicted, String key, CacheItem oldValue, CacheItem newValue) {
            if (evicted) {
                oldValue.clear();
            }
        }
    }

    public static abstract class CacheItem<T> {
        protected int mByteCount;
        protected ArrayList<T> mObjects;

        public abstract void clear();

        public abstract T getItem(CachedTaskPool cachedTaskPool, TaskOption taskOption);

        public CacheItem() {
            this.mObjects = new ArrayList<>(3);
        }
    }

    public interface TaskBuilder<T> {
        AsyncTask<TaskOption, Void, T> buildTask(TaskOption taskOption, TaskCompleteCallback<T> taskCompleteCallback);
    }

    static {
        DOWNLOADER_THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(5);
        LOCAL_DOWNLOADER_THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(1);
        sInstanceLock = new Object();
    }

    public static final CachedTaskPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (sInstanceLock) {
                if (sInstance == null) {
                    sInstance = new CachedTaskPool(context);
                }
            }
        }
        return sInstance;
    }

    private CachedTaskPool(Context context) {
        this.mContext = context.getApplicationContext();
        int memClass = ((ActivityManager) context.getSystemService("activity")).getMemoryClass() / 4;
        if (memClass > 32) {
            memClass = 32;
        }
        this.mMemoryCache = new C02191(1048576 * memClass);
    }

    public Object getObjectFromMemCache(TaskOption options) {
        CacheItem item = this.mMemoryCache.get(options.getCacheKey());
        if (item != null) {
            return item.getItem(this, options);
        }
        return null;
    }

    public void execute(TaskOption options, TaskCompleteCallback callback, TaskBuilder taskBuilder) {
        cancelTask(callback);
        Object object = getObjectFromMemCache(options);
        if (object != null) {
            callback.onCompleted(object);
            return;
        }
        AsyncTask<TaskOption, Void, Object> task = taskBuilder.buildTask(options, callback);
        callback.mTask = new SoftReference(task);
        scheduleTask(task, options);
    }

    public static void scheduleTask(AsyncTask<TaskOption, Void, Object> task, TaskOption options) {
        if (options.isLocal()) {
            task.executeOnExecutor(LOCAL_DOWNLOADER_THREAD_POOL_EXECUTOR, options);
            return;
        }
        task.executeOnExecutor(DOWNLOADER_THREAD_POOL_EXECUTOR, options);
    }

    public boolean cancelTask(TaskCompleteCallback key) {
        if (key.mTask != null) {
            AsyncTask task = (AsyncTask) key.mTask.get();
            if (task != null) {
                return task.cancel(true);
            }
        }
        return false;
    }

    public Context getContext() {
        return this.mContext;
    }

    public LruCache<String, CacheItem> getMemCache() {
        return this.mMemoryCache;
    }
}
