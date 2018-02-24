package com.bumptech.glide.load.engine.cache;

import android.util.Log;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.disklrucache.DiskLruCache.Editor;
import com.bumptech.glide.disklrucache.DiskLruCache.Value;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskCache.Writer;
import java.io.File;
import java.io.IOException;

public class DiskLruCacheWrapper implements DiskCache {
    private static DiskLruCacheWrapper wrapper = null;
    private final File directory;
    private DiskLruCache diskLruCache;
    private final int maxSize;
    private final SafeKeyGenerator safeKeyGenerator;
    private final DiskCacheWriteLocker writeLocker = new DiskCacheWriteLocker();

    public static synchronized DiskCache get(File directory, int maxSize) {
        DiskCache diskCache;
        synchronized (DiskLruCacheWrapper.class) {
            if (wrapper == null) {
                wrapper = new DiskLruCacheWrapper(directory, maxSize);
            }
            diskCache = wrapper;
        }
        return diskCache;
    }

    protected DiskLruCacheWrapper(File directory, int maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
        this.safeKeyGenerator = new SafeKeyGenerator();
    }

    private synchronized DiskLruCache getDiskCache() throws IOException {
        if (this.diskLruCache == null) {
            this.diskLruCache = DiskLruCache.open(this.directory, 1, 1, (long) this.maxSize);
        }
        return this.diskLruCache;
    }

    public File get(Key key) {
        String safeKey = this.safeKeyGenerator.getSafeKey(key);
        if (Log.isLoggable("DiskLruCacheWrapper", 2)) {
            String valueOf = String.valueOf(key);
            Log.v("DiskLruCacheWrapper", new StringBuilder((String.valueOf(safeKey).length() + 29) + String.valueOf(valueOf).length()).append("Get: Obtained: ").append(safeKey).append(" for for Key: ").append(valueOf).toString());
        }
        File result = null;
        try {
            Value value = getDiskCache().get(safeKey);
            if (value != null) {
                result = value.getFile(0);
            }
        } catch (IOException e) {
            if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
                Log.w("DiskLruCacheWrapper", "Unable to get from disk cache", e);
            }
        }
        return result;
    }

    public void put(Key key, Writer writer) {
        this.writeLocker.acquire(key);
        Editor editor;
        try {
            String safeKey = this.safeKeyGenerator.getSafeKey(key);
            if (Log.isLoggable("DiskLruCacheWrapper", 2)) {
                String valueOf = String.valueOf(key);
                Log.v("DiskLruCacheWrapper", new StringBuilder((String.valueOf(safeKey).length() + 29) + String.valueOf(valueOf).length()).append("Put: Obtained: ").append(safeKey).append(" for for Key: ").append(valueOf).toString());
            }
            DiskLruCache diskCache = getDiskCache();
            if (diskCache.get(safeKey) != null) {
                this.writeLocker.release(key);
                return;
            }
            editor = diskCache.edit(safeKey);
            if (editor == null) {
                String str = "Had two simultaneous puts for: ";
                String valueOf2 = String.valueOf(safeKey);
                if (valueOf2.length() != 0) {
                    valueOf2 = str.concat(valueOf2);
                } else {
                    valueOf2 = new String(str);
                }
                throw new IllegalStateException(valueOf2);
            }
            if (writer.write(editor.getFile(0))) {
                editor.commit();
            }
            editor.abortUnlessCommitted();
            this.writeLocker.release(key);
        } catch (IOException e) {
            if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
                Log.w("DiskLruCacheWrapper", "Unable to put to disk cache", e);
            }
        } catch (Throwable th) {
            this.writeLocker.release(key);
        }
    }
}
