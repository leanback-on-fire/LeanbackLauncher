package com.bumptech.glide.load.engine.bitmap_recycle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LruBitmapPool implements BitmapPool {
    private static final Config DEFAULT_CONFIG = Config.ARGB_8888;
    private final Set<Config> allowedConfigs;
    private int currentSize;
    private int evictions;
    private int hits;
    private final int initialMaxSize;
    private int maxSize;
    private int misses;
    private int puts;
    private final LruPoolStrategy strategy;
    private final BitmapTracker tracker;

    private interface BitmapTracker {
        void add(Bitmap bitmap);

        void remove(Bitmap bitmap);
    }

    private static class NullBitmapTracker implements BitmapTracker {
        private NullBitmapTracker() {
        }

        public void add(Bitmap bitmap) {
        }

        public void remove(Bitmap bitmap) {
        }
    }

    LruBitmapPool(int maxSize, LruPoolStrategy strategy, Set<Config> allowedConfigs) {
        this.initialMaxSize = maxSize;
        this.maxSize = maxSize;
        this.strategy = strategy;
        this.allowedConfigs = allowedConfigs;
        this.tracker = new NullBitmapTracker();
    }

    public LruBitmapPool(int maxSize) {
        this(maxSize, getDefaultStrategy(), getDefaultAllowedConfigs());
    }

    public synchronized void put(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        } else if (bitmap.isRecycled()) {
            throw new IllegalStateException("Cannot pool recycled bitmap");
        } else if (bitmap.isMutable() && this.strategy.getSize(bitmap) <= this.maxSize && this.allowedConfigs.contains(bitmap.getConfig())) {
            int size = this.strategy.getSize(bitmap);
            this.strategy.put(bitmap);
            this.tracker.add(bitmap);
            this.puts++;
            this.currentSize += size;
            if (Log.isLoggable("LruBitmapPool", 2)) {
                r2 = "LruBitmapPool";
                String str = "Put bitmap in pool=";
                String valueOf = String.valueOf(this.strategy.logBitmap(bitmap));
                Log.v(r2, valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            }
            dump();
            evict();
        } else {
            if (Log.isLoggable("LruBitmapPool", 2)) {
                r2 = String.valueOf(this.strategy.logBitmap(bitmap));
                Log.v("LruBitmapPool", new StringBuilder(String.valueOf(r2).length() + 78).append("Reject bitmap from pool, bitmap: ").append(r2).append(", is mutable: ").append(bitmap.isMutable()).append(", is allowed config: ").append(this.allowedConfigs.contains(bitmap.getConfig())).toString());
            }
            bitmap.recycle();
        }
    }

    private void evict() {
        trimToSize(this.maxSize);
    }

    public Bitmap get(int width, int height, Config config) {
        Bitmap result = getDirtyOrNull(width, height, config);
        if (result == null) {
            return Bitmap.createBitmap(width, height, config);
        }
        result.eraseColor(0);
        return result;
    }

    public Bitmap getDirty(int width, int height, Config config) {
        Bitmap result = getDirtyOrNull(width, height, config);
        if (result == null) {
            return Bitmap.createBitmap(width, height, config);
        }
        return result;
    }

    private synchronized Bitmap getDirtyOrNull(int width, int height, Config config) {
        Bitmap result;
        result = this.strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
        if (result == null) {
            if (Log.isLoggable("LruBitmapPool", 3)) {
                String str = "LruBitmapPool";
                String str2 = "Missing bitmap=";
                String valueOf = String.valueOf(this.strategy.logBitmap(width, height, config));
                Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            this.misses++;
        } else {
            this.hits++;
            this.currentSize -= this.strategy.getSize(result);
            this.tracker.remove(result);
            normalize(result);
        }
        if (Log.isLoggable("LruBitmapPool", 2)) {
            str = "LruBitmapPool";
            str2 = "Get bitmap=";
            valueOf = String.valueOf(this.strategy.logBitmap(width, height, config));
            Log.v(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        dump();
        return result;
    }

    private static void normalize(Bitmap bitmap) {
        maybeSetAlpha(bitmap);
        maybeSetPreMultiplied(bitmap);
    }

    @TargetApi(12)
    private static void maybeSetAlpha(Bitmap bitmap) {
        if (VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(true);
        }
    }

    @TargetApi(19)
    private static void maybeSetPreMultiplied(Bitmap bitmap) {
        if (VERSION.SDK_INT >= 19) {
            bitmap.setPremultiplied(true);
        }
    }

    public void clearMemory() {
        if (Log.isLoggable("LruBitmapPool", 3)) {
            Log.d("LruBitmapPool", "clearMemory");
        }
        trimToSize(0);
    }

    @SuppressLint({"InlinedApi"})
    public void trimMemory(int level) {
        if (Log.isLoggable("LruBitmapPool", 3)) {
            Log.d("LruBitmapPool", "trimMemory, level=" + level);
        }
        if (level >= 40) {
            clearMemory();
        } else if (level >= 20) {
            trimToSize(this.maxSize / 2);
        }
    }

    private synchronized void trimToSize(int size) {
        while (this.currentSize > size) {
            Bitmap removed = this.strategy.removeLast();
            if (removed == null) {
                if (Log.isLoggable("LruBitmapPool", 5)) {
                    Log.w("LruBitmapPool", "Size mismatch, resetting");
                    dumpUnchecked();
                }
                this.currentSize = 0;
            } else {
                this.tracker.remove(removed);
                this.currentSize -= this.strategy.getSize(removed);
                this.evictions++;
                if (Log.isLoggable("LruBitmapPool", 3)) {
                    String str = "LruBitmapPool";
                    String str2 = "Evicting bitmap=";
                    String valueOf = String.valueOf(this.strategy.logBitmap(removed));
                    Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                }
                dump();
                removed.recycle();
            }
        }
    }

    private void dump() {
        if (Log.isLoggable("LruBitmapPool", 2)) {
            dumpUnchecked();
        }
    }

    private void dumpUnchecked() {
        int i = this.hits;
        int i2 = this.misses;
        int i3 = this.puts;
        int i4 = this.evictions;
        int i5 = this.currentSize;
        int i6 = this.maxSize;
        String valueOf = String.valueOf(this.strategy);
        Log.v("LruBitmapPool", new StringBuilder(String.valueOf(valueOf).length() + 133).append("Hits=").append(i).append(", misses=").append(i2).append(", puts=").append(i3).append(", evictions=").append(i4).append(", currentSize=").append(i5).append(", maxSize=").append(i6).append("\nStrategy=").append(valueOf).toString());
    }

    private static LruPoolStrategy getDefaultStrategy() {
        if (VERSION.SDK_INT >= 19) {
            return new SizeConfigStrategy();
        }
        return new AttributeStrategy();
    }

    private static Set<Config> getDefaultAllowedConfigs() {
        Set<Config> configs = new HashSet();
        configs.addAll(Arrays.asList(Config.values()));
        if (VERSION.SDK_INT >= 19) {
            configs.add(null);
        }
        return Collections.unmodifiableSet(configs);
    }
}
