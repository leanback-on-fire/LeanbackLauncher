package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class DiskCacheWriteLocker
{
  private final Map<Key, WriteLock> locks = new HashMap();
  private final WriteLockPool writeLockPool = new WriteLockPool(null);
  
  void acquire(Key paramKey)
  {
    try
    {
      WriteLock localWriteLock2 = (WriteLock)this.locks.get(paramKey);
      WriteLock localWriteLock1 = localWriteLock2;
      if (localWriteLock2 == null)
      {
        localWriteLock1 = this.writeLockPool.obtain();
        this.locks.put(paramKey, localWriteLock1);
      }
      localWriteLock1.interestedThreads += 1;
      localWriteLock1.lock.lock();
      return;
    }
    finally {}
  }
  
  void release(Key paramKey)
  {
    Object localObject1;
    try
    {
      localObject1 = (WriteLock)Preconditions.checkNotNull((WriteLock)this.locks.get(paramKey));
      if (((WriteLock)localObject1).interestedThreads < 1)
      {
        paramKey = String.valueOf(paramKey);
        int i = ((WriteLock)localObject1).interestedThreads;
        throw new IllegalStateException(String.valueOf(paramKey).length() + 77 + "Cannot release a lock that is not held, key: " + paramKey + ", interestedThreads: " + i);
      }
    }
    finally {}
    ((WriteLock)localObject1).interestedThreads -= 1;
    if (((WriteLock)localObject1).interestedThreads == 0)
    {
      Object localObject2 = (WriteLock)this.locks.remove(paramKey);
      if (!localObject2.equals(localObject1))
      {
        localObject1 = String.valueOf(localObject1);
        localObject2 = String.valueOf(localObject2);
        paramKey = String.valueOf(paramKey);
        throw new IllegalStateException(String.valueOf(localObject1).length() + 75 + String.valueOf(localObject2).length() + String.valueOf(paramKey).length() + "Removed the wrong lock, expected to remove: " + (String)localObject1 + ", but actually removed: " + (String)localObject2 + ", key: " + paramKey);
      }
      this.writeLockPool.offer((WriteLock)localObject2);
    }
    ((WriteLock)localObject1).lock.unlock();
  }
  
  private static class WriteLock
  {
    int interestedThreads;
    final Lock lock = new ReentrantLock();
  }
  
  private static class WriteLockPool
  {
    private static final int MAX_POOL_SIZE = 10;
    private final Queue<DiskCacheWriteLocker.WriteLock> pool = new ArrayDeque();
    
    DiskCacheWriteLocker.WriteLock obtain()
    {
      synchronized (this.pool)
      {
        DiskCacheWriteLocker.WriteLock localWriteLock = (DiskCacheWriteLocker.WriteLock)this.pool.poll();
        ??? = localWriteLock;
        if (localWriteLock == null) {
          ??? = new DiskCacheWriteLocker.WriteLock(null);
        }
        return (DiskCacheWriteLocker.WriteLock)???;
      }
    }
    
    void offer(DiskCacheWriteLocker.WriteLock paramWriteLock)
    {
      synchronized (this.pool)
      {
        if (this.pool.size() < 10) {
          this.pool.offer(paramWriteLock);
        }
        return;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/cache/DiskCacheWriteLocker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */