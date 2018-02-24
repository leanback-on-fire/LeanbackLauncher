package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class DiskCacheWriteLocker {
    private final Map<Key, WriteLock> locks = new HashMap();
    private final WriteLockPool writeLockPool = new WriteLockPool();

    private static class WriteLock {
        int interestedThreads;
        final Lock lock;

        private WriteLock() {
            this.lock = new ReentrantLock();
        }
    }

    private static class WriteLockPool {
        private final Queue<WriteLock> pool;

        private WriteLockPool() {
            this.pool = new ArrayDeque();
        }

        WriteLock obtain() {
            synchronized (this.pool) {
                WriteLock result = (WriteLock) this.pool.poll();
            }
            if (result == null) {
                return new WriteLock();
            }
            return result;
        }

        void offer(WriteLock writeLock) {
            synchronized (this.pool) {
                if (this.pool.size() < 10) {
                    this.pool.offer(writeLock);
                }
            }
        }
    }

    DiskCacheWriteLocker() {
    }

    void acquire(Key key) {
        WriteLock writeLock;
        synchronized (this) {
            writeLock = (WriteLock) this.locks.get(key);
            if (writeLock == null) {
                writeLock = this.writeLockPool.obtain();
                this.locks.put(key, writeLock);
            }
            writeLock.interestedThreads++;
        }
        writeLock.lock.lock();
    }

    void release(Key key) {
        WriteLock writeLock;
        synchronized (this) {
            writeLock = (WriteLock) Preconditions.checkNotNull((WriteLock) this.locks.get(key));
            if (writeLock.interestedThreads < 1) {
                String valueOf = String.valueOf(key);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 77).append("Cannot release a lock that is not held, key: ").append(valueOf).append(", interestedThreads: ").append(writeLock.interestedThreads).toString());
            }
            writeLock.interestedThreads--;
            if (writeLock.interestedThreads == 0) {
                WriteLock removed = (WriteLock) this.locks.remove(key);
                if (removed.equals(writeLock)) {
                    this.writeLockPool.offer(removed);
                } else {
                    valueOf = String.valueOf(writeLock);
                    String valueOf2 = String.valueOf(removed);
                    String valueOf3 = String.valueOf(key);
                    throw new IllegalStateException(new StringBuilder(((String.valueOf(valueOf).length() + 75) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Removed the wrong lock, expected to remove: ").append(valueOf).append(", but actually removed: ").append(valueOf2).append(", key: ").append(valueOf3).toString());
                }
            }
        }
        writeLock.lock.unlock();
    }
}
