package com.bumptech.glide.load.engine.bitmap_recycle;

import android.util.Log;
import com.bumptech.glide.util.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class LruArrayPool implements ArrayPool {
    private final Map<Class, ArrayAdapterInterface> adapters;
    private int currentSize;
    private final GroupedLinkedMap<Key, Object> groupedMap;
    private final KeyPool keyPool;
    private final int maxSize;
    private final Map<Class, NavigableMap<Integer, Integer>> sortedSizes;

    private static final class Key implements Poolable {
        private Class arrayClass;
        private final KeyPool pool;
        private int size;

        Key(KeyPool pool) {
            this.pool = pool;
        }

        void init(int length, Class arrayClass) {
            this.size = length;
            this.arrayClass = arrayClass;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key) o;
            if (this.size == other.size && this.arrayClass == other.arrayClass) {
                return true;
            }
            return false;
        }

        public String toString() {
            int i = this.size;
            String valueOf = String.valueOf(this.arrayClass);
            return new StringBuilder(String.valueOf(valueOf).length() + 27).append("Key{size=").append(i).append("array=").append(valueOf).append("}").toString();
        }

        public void offer() {
            this.pool.offer(this);
        }

        public int hashCode() {
            return (this.size * 31) + (this.arrayClass != null ? this.arrayClass.hashCode() : 0);
        }
    }

    private static final class KeyPool extends BaseKeyPool<Key> {
        private KeyPool() {
        }

        Key get(int size, Class arrayClass) {
            Key result = (Key) get();
            result.init(size, arrayClass);
            return result;
        }

        protected Key create() {
            return new Key(this);
        }
    }

    public LruArrayPool() {
        this.groupedMap = new GroupedLinkedMap();
        this.keyPool = new KeyPool();
        this.sortedSizes = new HashMap();
        this.adapters = new HashMap();
        this.maxSize = 4194304;
    }

    public LruArrayPool(int maxSize) {
        this.groupedMap = new GroupedLinkedMap();
        this.keyPool = new KeyPool();
        this.sortedSizes = new HashMap();
        this.adapters = new HashMap();
        this.maxSize = maxSize;
    }

    public synchronized <T> void put(T array, Class<T> arrayClass) {
        ArrayAdapterInterface<T> arrayAdapter = getAdapterFromType(arrayClass);
        int size = arrayAdapter.getArrayLength(array);
        int arrayBytes = size * arrayAdapter.getElementSizeInBytes();
        if (isSmallEnoughForReuse(arrayBytes)) {
            Key key = this.keyPool.get(size, arrayClass);
            this.groupedMap.put(key, array);
            NavigableMap<Integer, Integer> sizes = getSizesForAdapter(arrayClass);
            Integer current = (Integer) sizes.get(Integer.valueOf(key.size));
            sizes.put(Integer.valueOf(key.size), Integer.valueOf(current == null ? 1 : current.intValue() + 1));
            this.currentSize += arrayBytes;
            evict();
        }
    }

    public <T> T get(int size, Class<T> arrayClass) {
        ArrayAdapterInterface<T> arrayAdapter = getAdapterFromType(arrayClass);
        synchronized (this) {
            Key key;
            Integer possibleSize = (Integer) getSizesForAdapter(arrayClass).ceilingKey(Integer.valueOf(size));
            if (mayFillRequest(size, possibleSize)) {
                key = this.keyPool.get(possibleSize.intValue(), arrayClass);
            } else {
                key = this.keyPool.get(size, arrayClass);
            }
            T result = getArrayForKey(key);
            if (result != null) {
                this.currentSize -= arrayAdapter.getArrayLength(result) * arrayAdapter.getElementSizeInBytes();
                decrementArrayOfSize(arrayAdapter.getArrayLength(result), arrayClass);
            }
        }
        if (result != null) {
            arrayAdapter.resetArray(result);
            return result;
        }
        if (Log.isLoggable(arrayAdapter.getTag(), 2)) {
            Log.v(arrayAdapter.getTag(), "Allocated " + size + " bytes");
        }
        return arrayAdapter.newArray(size);
    }

    private <T> T getArrayForKey(Key key) {
        return this.groupedMap.get(key);
    }

    private boolean isSmallEnoughForReuse(int byteSize) {
        return byteSize <= this.maxSize / 2;
    }

    private boolean mayFillRequest(int requestedSize, Integer actualSize) {
        return actualSize != null && (isNoMoreThanHalfFull() || actualSize.intValue() <= requestedSize * 8);
    }

    private boolean isNoMoreThanHalfFull() {
        return this.currentSize == 0 || this.maxSize / this.currentSize >= 2;
    }

    public synchronized void clearMemory() {
        evictToSize(0);
    }

    public synchronized void trimMemory(int level) {
        if (level >= 40) {
            clearMemory();
        } else if (level >= 20) {
            evictToSize(this.maxSize / 2);
        }
    }

    private void evict() {
        evictToSize(this.maxSize);
    }

    private void evictToSize(int size) {
        while (this.currentSize > size) {
            Object evicted = this.groupedMap.removeLast();
            Preconditions.checkNotNull(evicted);
            ArrayAdapterInterface<Object> arrayAdapter = getAdapterFromObject(evicted);
            this.currentSize -= arrayAdapter.getArrayLength(evicted) * arrayAdapter.getElementSizeInBytes();
            decrementArrayOfSize(arrayAdapter.getArrayLength(evicted), evicted.getClass());
            if (Log.isLoggable(arrayAdapter.getTag(), 2)) {
                Log.v(arrayAdapter.getTag(), "evicted: " + arrayAdapter.getArrayLength(evicted));
            }
        }
    }

    private void decrementArrayOfSize(int size, Class<?> arrayClass) {
        NavigableMap<Integer, Integer> sizes = getSizesForAdapter(arrayClass);
        Integer current = (Integer) sizes.get(Integer.valueOf(size));
        if (current == null) {
            String valueOf = String.valueOf(this);
            throw new NullPointerException(new StringBuilder(String.valueOf(valueOf).length() + 56).append("Tried to decrement empty size, size: ").append(size).append(", this: ").append(valueOf).toString());
        } else if (current.intValue() == 1) {
            sizes.remove(Integer.valueOf(size));
        } else {
            sizes.put(Integer.valueOf(size), Integer.valueOf(current.intValue() - 1));
        }
    }

    private NavigableMap<Integer, Integer> getSizesForAdapter(Class<?> arrayClass) {
        NavigableMap<Integer, Integer> sizes = (NavigableMap) this.sortedSizes.get(arrayClass);
        if (sizes != null) {
            return sizes;
        }
        sizes = new TreeMap();
        this.sortedSizes.put(arrayClass, sizes);
        return sizes;
    }

    private <T> ArrayAdapterInterface<T> getAdapterFromObject(T object) {
        return getAdapterFromType(object.getClass());
    }

    private <T> ArrayAdapterInterface<T> getAdapterFromType(Class<T> arrayPoolClass) {
        ArrayAdapterInterface adapter = (ArrayAdapterInterface) this.adapters.get(arrayPoolClass);
        if (adapter == null) {
            if (arrayPoolClass.equals(int[].class)) {
                adapter = new IntegerArrayAdapter();
            } else if (arrayPoolClass.equals(byte[].class)) {
                adapter = new ByteArrayAdapter();
            } else {
                String str = "No array pool found for: ";
                String valueOf = String.valueOf(arrayPoolClass.getSimpleName());
                throw new IllegalArgumentException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            }
            this.adapters.put(arrayPoolClass, adapter);
        }
        return adapter;
    }
}
