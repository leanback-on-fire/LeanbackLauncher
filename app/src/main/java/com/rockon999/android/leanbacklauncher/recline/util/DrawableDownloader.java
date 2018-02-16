package com.rockon999.android.leanbacklauncher.recline.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.util.SparseArray;
import android.widget.ImageView;

import com.rockon999.android.leanbacklauncher.R;

import java.lang.ref.SoftReference;

public class DrawableDownloader {
    private static DrawableDownloader sInstance;
    private static final Object sInstanceLock;
    private SparseArray<PostProc<Bitmap>> mPostProcs;
    private RecycleBitmapPool mRecycledBitmaps;
    private CachedTaskPool mTaskPool;

    public static abstract class BitmapCallback extends CachedTaskPool.TaskCompleteCallback<Drawable> {
    }

    static class CallbackDrawableLoader extends DrawableLoader {
        BitmapCallback mBitmapCallback;

        CallbackDrawableLoader(BitmapCallback callback, RecycleBitmapPool recycledBitmapPool, PostProc<Bitmap> postProc) {
            super(null, recycledBitmapPool, postProc);
            this.mBitmapCallback = callback;
        }

        protected void onCancelled(Object result) {
            this.mBitmapCallback = null;
            super.onCancelled(result);
        }
    }

    /* renamed from: com.rockon999.android.recline.util.DrawableDownloader.3 */
    class C02213 implements CachedTaskPool.TaskBuilder {
        final /* synthetic */ BitmapWorkerOptions val$options;

        /* renamed from: com.rockon999.android.recline.util.DrawableDownloader.3.1 */
        class C02201 extends CallbackDrawableLoader {
            C02201(BitmapCallback $anonymous0, RecycleBitmapPool $anonymous1, PostProc $anonymous2) {
                super($anonymous0, $anonymous1, $anonymous2);
            }

            protected Drawable doInBackground(CachedTaskPool.TaskOption... params) {
                Drawable bitmap = super.doInBackground(params);
                BitmapWorkerOptions options = (BitmapWorkerOptions) params[0];
                if (bitmap != null) {
                    DrawableDownloader.this.addBitmapToMemoryCache(options, bitmap, this);
                }
                return bitmap;
            }

            protected void onPostExecute(Object bitmap) {
                this.mBitmapCallback.onCompleted((Drawable) bitmap);
                this.mBitmapCallback = null;
            }
        }

        C02213(BitmapWorkerOptions val$options) {
            this.val$options = val$options;
        }

        public AsyncTask<CachedTaskPool.TaskOption, Void, Object> buildTask(CachedTaskPool.TaskOption option, CachedTaskPool.TaskCompleteCallback callback) {
            return new C02201((BitmapCallback) callback, DrawableDownloader.this.mRecycledBitmaps, DrawableDownloader.this.getPostProc(this.val$options.getPostProcId()));
        }
    }

    private static class BitmapItem extends CachedTaskPool.CacheItem<BitmapDrawable> {
        int mOriginalHeight;
        int mOriginalWidth;

        public BitmapItem(int originalWidth, int originalHeight) {
            this.mOriginalWidth = originalWidth;
            this.mOriginalHeight = originalHeight;
        }

        BitmapDrawable findDrawable(BitmapWorkerOptions options) {
            int c = this.mObjects.size();
            for (int i = 0; i < c; i++) {
                BitmapDrawable d = this.mObjects.get(i);
                if (d.getIntrinsicWidth() == this.mOriginalWidth && d.getIntrinsicHeight() == this.mOriginalHeight) {
                    return d;
                }
                if (options.getHeight() != 2048) {
                    if (options.getHeight() <= d.getIntrinsicHeight()) {
                        return d;
                    }
                } else if (options.getWidth() != 2048 && options.getWidth() <= d.getIntrinsicWidth()) {
                    return d;
                }
            }
            return null;
        }

        public void addItem(BitmapDrawable d) {
            int i = 0;
            int c = this.mObjects.size();
            while (i < c && this.mObjects.get(i).getIntrinsicHeight() >= d.getIntrinsicHeight()) {
                i++;
            }
            this.mObjects.add(i, d);
            this.mByteCount += RecycleBitmapPool.getSize(d.getBitmap());
        }

        public void clear() {
            int c = this.mObjects.size();
            for (int i = 0; i < c; i++) {
                BitmapDrawable d = this.mObjects.get(i);
                if (d instanceof RefcountBitmapDrawable) {
                    ((RefcountBitmapDrawable) d).getRefcountObject().releaseRef();
                }
            }
            this.mObjects.clear();
            this.mByteCount = 0;
        }

        public BitmapDrawable getItem(CachedTaskPool downloader, CachedTaskPool.TaskOption option) {
            return (BitmapDrawable) DrawableDownloader.createRefCopy(downloader.getContext(), findDrawable((BitmapWorkerOptions) option));
        }
    }

    static {
        sInstanceLock = new Object();
    }

    public static final DrawableDownloader getInstance(Context context) {
        if (sInstance == null) {
            synchronized (sInstanceLock) {
                if (sInstance == null) {
                    sInstance = new DrawableDownloader(context);
                }
            }
        }
        return sInstance;
    }

    private DrawableDownloader(Context context) {
        this.mPostProcs = new SparseArray<>();
        this.mTaskPool = CachedTaskPool.getInstance(context);
        this.mRecycledBitmaps = new RecycleBitmapPool();
    }

    public void registerPostProc(int postProcId, PostProc<Bitmap> postProc) {
        this.mPostProcs.append(postProcId, postProc);
    }

    public PostProc<Bitmap> getPostProc(int postProcId) {
        return this.mPostProcs.get(postProcId);
    }

    public void getBitmap(BitmapWorkerOptions options, BitmapCallback callback) {
        this.mTaskPool.execute(options, callback, new C02213(options));
    }

    public boolean cancelDownload(Object key) {
        DrawableLoader task = null;
        if (key instanceof ImageView) {
            SoftReference<DrawableLoader> softReference = (SoftReference) ((ImageView) key).getTag(R.id.lb_image_download_task_tag);
            if (softReference != null) {
                task = softReference.get();
                softReference.clear();
            }
            if (task != null) {
                return task.cancel(true);
            }
        } else if (key instanceof BitmapCallback) {
            return this.mTaskPool.cancelTask((BitmapCallback) key);
        }
        return false;
    }

    private void addBitmapToMemoryCache(BitmapWorkerOptions key, Drawable bitmap, DrawableLoader loader) {
        if (key.isMemCacheEnabled() && (bitmap instanceof BitmapDrawable)) {
            String cacheKey = key.getCacheKey();
            LruCache<String, CachedTaskPool.CacheItem> memCache = this.mTaskPool.getMemCache();
            BitmapItem bitmapItem = (BitmapItem) memCache.get(cacheKey);
            if (bitmapItem != null) {
                memCache.remove(cacheKey);
            } else {
                bitmapItem = new BitmapItem(loader.getOriginalWidth(), loader.getOriginalHeight());
            }
            if (bitmap instanceof RefcountBitmapDrawable) {
                ((RefcountBitmapDrawable) bitmap).getRefcountObject().addRef();
            }
            bitmapItem.addItem((BitmapDrawable) bitmap);
            memCache.put(cacheKey, bitmapItem);
        }
    }

    private static Drawable createRefCopy(Context context, Drawable d) {
        if (d == null) {
            return null;
        }
        if (d instanceof RefcountBitmapDrawable) {
            RefcountBitmapDrawable refcountDrawable = (RefcountBitmapDrawable) d;
            refcountDrawable.getRefcountObject().addRef();
            d = new RefcountBitmapDrawable(context.getResources(), refcountDrawable);
        }
        return d;
    }
}
