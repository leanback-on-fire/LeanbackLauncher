package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.bumptech.glide.util.Util;

class AttributeStrategy implements LruPoolStrategy {
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap();
    private final KeyPool keyPool = new KeyPool();

    static class Key implements Poolable {
        private Config config;
        private int height;
        private final KeyPool pool;
        private int width;

        public Key(KeyPool pool) {
            this.pool = pool;
        }

        public void init(int width, int height, Config config) {
            this.width = width;
            this.height = height;
            this.config = config;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key) o;
            if (this.width == other.width && this.height == other.height && this.config == other.config) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (((this.width * 31) + this.height) * 31) + (this.config != null ? this.config.hashCode() : 0);
        }

        public String toString() {
            return AttributeStrategy.getBitmapString(this.width, this.height, this.config);
        }

        public void offer() {
            this.pool.offer(this);
        }
    }

    static class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        public Key get(int width, int height, Config config) {
            Key result = (Key) get();
            result.init(width, height, config);
            return result;
        }

        protected Key create() {
            return new Key(this);
        }
    }

    AttributeStrategy() {
    }

    public void put(Bitmap bitmap) {
        this.groupedMap.put(this.keyPool.get(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig()), bitmap);
    }

    public Bitmap get(int width, int height, Config config) {
        return (Bitmap) this.groupedMap.get(this.keyPool.get(width, height, config));
    }

    public Bitmap removeLast() {
        return (Bitmap) this.groupedMap.removeLast();
    }

    public String logBitmap(Bitmap bitmap) {
        return getBitmapString(bitmap);
    }

    public String logBitmap(int width, int height, Config config) {
        return getBitmapString(width, height, config);
    }

    public int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }

    public String toString() {
        String valueOf = String.valueOf(this.groupedMap);
        return new StringBuilder(String.valueOf(valueOf).length() + 21).append("AttributeStrategy:\n  ").append(valueOf).toString();
    }

    private static String getBitmapString(Bitmap bitmap) {
        return getBitmapString(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    private static String getBitmapString(int width, int height, Config config) {
        String valueOf = String.valueOf(config);
        return new StringBuilder(String.valueOf(valueOf).length() + 27).append("[").append(width).append("x").append(height).append("], ").append(valueOf).toString();
    }
}
