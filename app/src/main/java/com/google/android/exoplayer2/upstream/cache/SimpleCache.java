package com.google.android.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import com.google.android.exoplayer2.util.Assertions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public final class SimpleCache
  implements Cache
{
  private final File cacheDir;
  private final CacheEvictor evictor;
  private final CachedContentIndex index;
  private Cache.CacheException initializationException;
  private final HashMap<String, ArrayList<Cache.Listener>> listeners;
  private final HashMap<String, CacheSpan> lockedSpans;
  private long totalSpace = 0L;
  
  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor)
  {
    this(paramFile, paramCacheEvictor, null);
  }
  
  public SimpleCache(final File paramFile, CacheEvictor paramCacheEvictor, byte[] paramArrayOfByte)
  {
    this.cacheDir = paramFile;
    this.evictor = paramCacheEvictor;
    this.lockedSpans = new HashMap();
    this.index = new CachedContentIndex(paramFile, paramArrayOfByte);
    this.listeners = new HashMap();
    paramFile = new ConditionVariable();
    new Thread("SimpleCache.initialize()")
    {
      public void run()
      {
        synchronized (SimpleCache.this)
        {
          paramFile.open();
          try
          {
            SimpleCache.this.initialize();
            SimpleCache.this.evictor.onCacheInitialized();
            return;
          }
          catch (Cache.CacheException localCacheException)
          {
            for (;;)
            {
              SimpleCache.access$102(SimpleCache.this, localCacheException);
            }
          }
        }
      }
    }.start();
    paramFile.block();
  }
  
  private void addSpan(SimpleCacheSpan paramSimpleCacheSpan)
  {
    this.index.add(paramSimpleCacheSpan.key).addSpan(paramSimpleCacheSpan);
    this.totalSpace += paramSimpleCacheSpan.length;
    notifySpanAdded(paramSimpleCacheSpan);
  }
  
  private SimpleCacheSpan getSpan(String paramString, long paramLong)
    throws Cache.CacheException
  {
    CachedContent localCachedContent = this.index.get(paramString);
    if (localCachedContent == null)
    {
      paramString = SimpleCacheSpan.createOpenHole(paramString, paramLong);
      return paramString;
    }
    for (;;)
    {
      SimpleCacheSpan localSimpleCacheSpan = localCachedContent.getSpan(paramLong);
      paramString = localSimpleCacheSpan;
      if (!localSimpleCacheSpan.isCached) {
        break;
      }
      paramString = localSimpleCacheSpan;
      if (localSimpleCacheSpan.file.exists()) {
        break;
      }
      removeStaleSpansAndCachedContents();
    }
  }
  
  private void initialize()
    throws Cache.CacheException
  {
    if (!this.cacheDir.exists()) {
      this.cacheDir.mkdirs();
    }
    File[] arrayOfFile;
    do
    {
      return;
      this.index.load();
      arrayOfFile = this.cacheDir.listFiles();
    } while (arrayOfFile == null);
    int j = arrayOfFile.length;
    int i = 0;
    if (i < j)
    {
      File localFile = arrayOfFile[i];
      if (localFile.getName().equals("cached_content_index.exi")) {}
      for (;;)
      {
        i += 1;
        break;
        if (localFile.length() > 0L) {}
        for (SimpleCacheSpan localSimpleCacheSpan = SimpleCacheSpan.createCacheEntry(localFile, this.index);; localSimpleCacheSpan = null)
        {
          if (localSimpleCacheSpan == null) {
            break label114;
          }
          addSpan(localSimpleCacheSpan);
          break;
        }
        label114:
        localFile.delete();
      }
    }
    this.index.removeEmpty();
    this.index.store();
  }
  
  private void notifySpanAdded(SimpleCacheSpan paramSimpleCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramSimpleCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanAdded(this, paramSimpleCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanAdded(this, paramSimpleCacheSpan);
  }
  
  private void notifySpanRemoved(CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanRemoved(this, paramCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanRemoved(this, paramCacheSpan);
  }
  
  private void notifySpanTouched(SimpleCacheSpan paramSimpleCacheSpan, CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramSimpleCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
  }
  
  private void removeSpan(CacheSpan paramCacheSpan, boolean paramBoolean)
    throws Cache.CacheException
  {
    CachedContent localCachedContent = this.index.get(paramCacheSpan.key);
    Assertions.checkState(localCachedContent.removeSpan(paramCacheSpan));
    this.totalSpace -= paramCacheSpan.length;
    if ((paramBoolean) && (localCachedContent.isEmpty()))
    {
      this.index.removeEmpty(localCachedContent.key);
      this.index.store();
    }
    notifySpanRemoved(paramCacheSpan);
  }
  
  private void removeStaleSpansAndCachedContents()
    throws Cache.CacheException
  {
    Object localObject = new LinkedList();
    Iterator localIterator1 = this.index.getAll().iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((CachedContent)localIterator1.next()).getSpans().iterator();
      while (localIterator2.hasNext())
      {
        CacheSpan localCacheSpan = (CacheSpan)localIterator2.next();
        if (!localCacheSpan.file.exists()) {
          ((LinkedList)localObject).add(localCacheSpan);
        }
      }
    }
    localObject = ((LinkedList)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      removeSpan((CacheSpan)((Iterator)localObject).next(), false);
    }
    this.index.removeEmpty();
    this.index.store();
  }
  
  public NavigableSet<CacheSpan> addListener(String paramString, Cache.Listener paramListener)
  {
    try
    {
      ArrayList localArrayList2 = (ArrayList)this.listeners.get(paramString);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.listeners.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramListener);
      paramString = getCachedSpans(paramString);
      return paramString;
    }
    finally {}
  }
  
  public void commitFile(File paramFile)
    throws Cache.CacheException
  {
    boolean bool2 = true;
    SimpleCacheSpan localSimpleCacheSpan;
    try
    {
      localSimpleCacheSpan = SimpleCacheSpan.createCacheEntry(paramFile, this.index);
      if (localSimpleCacheSpan != null)
      {
        bool1 = true;
        Assertions.checkState(bool1);
        Assertions.checkState(this.lockedSpans.containsKey(localSimpleCacheSpan.key));
        bool1 = paramFile.exists();
        if (bool1) {
          break label57;
        }
      }
      for (;;)
      {
        return;
        bool1 = false;
        break;
        label57:
        if (paramFile.length() != 0L) {
          break label79;
        }
        paramFile.delete();
      }
      paramFile = Long.valueOf(getContentLength(localSimpleCacheSpan.key));
    }
    finally {}
    label79:
    if (paramFile.longValue() != -1L) {
      if (localSimpleCacheSpan.position + localSimpleCacheSpan.length > paramFile.longValue()) {
        break label148;
      }
    }
    label148:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Assertions.checkState(bool1);
      addSpan(localSimpleCacheSpan);
      this.index.store();
      notifyAll();
      break;
    }
  }
  
  public long getCacheSpace()
  {
    try
    {
      long l = this.totalSpace;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public NavigableSet<CacheSpan> getCachedSpans(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 50	com/google/android/exoplayer2/upstream/cache/SimpleCache:index	Lcom/google/android/exoplayer2/upstream/cache/CachedContentIndex;
    //   6: aload_1
    //   7: invokevirtual 107	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:get	(Ljava/lang/String;)Lcom/google/android/exoplayer2/upstream/cache/CachedContent;
    //   10: astore_1
    //   11: aload_1
    //   12: ifnonnull +9 -> 21
    //   15: aconst_null
    //   16: astore_1
    //   17: aload_0
    //   18: monitorexit
    //   19: aload_1
    //   20: areturn
    //   21: new 254	java/util/TreeSet
    //   24: dup
    //   25: aload_1
    //   26: invokevirtual 252	com/google/android/exoplayer2/upstream/cache/CachedContent:getSpans	()Ljava/util/TreeSet;
    //   29: invokespecial 305	java/util/TreeSet:<init>	(Ljava/util/Collection;)V
    //   32: astore_1
    //   33: goto -16 -> 17
    //   36: astore_1
    //   37: aload_0
    //   38: monitorexit
    //   39: aload_1
    //   40: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	SimpleCache
    //   0	41	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   2	11	36	finally
    //   21	33	36	finally
  }
  
  public long getContentLength(String paramString)
  {
    try
    {
      long l = this.index.getContentLength(paramString);
      return l;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public Set<String> getKeys()
  {
    try
    {
      HashSet localHashSet = new HashSet(this.index.getKeys());
      return localHashSet;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean isCached(String paramString, long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 50	com/google/android/exoplayer2/upstream/cache/SimpleCache:index	Lcom/google/android/exoplayer2/upstream/cache/CachedContentIndex;
    //   6: aload_1
    //   7: invokevirtual 107	com/google/android/exoplayer2/upstream/cache/CachedContentIndex:get	(Ljava/lang/String;)Lcom/google/android/exoplayer2/upstream/cache/CachedContent;
    //   10: astore_1
    //   11: aload_1
    //   12: ifnull +25 -> 37
    //   15: aload_1
    //   16: lload_2
    //   17: lload 4
    //   19: invokevirtual 319	com/google/android/exoplayer2/upstream/cache/CachedContent:isCached	(JJ)Z
    //   22: istore 6
    //   24: iload 6
    //   26: ifeq +11 -> 37
    //   29: iconst_1
    //   30: istore 6
    //   32: aload_0
    //   33: monitorexit
    //   34: iload 6
    //   36: ireturn
    //   37: iconst_0
    //   38: istore 6
    //   40: goto -8 -> 32
    //   43: astore_1
    //   44: aload_0
    //   45: monitorexit
    //   46: aload_1
    //   47: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	SimpleCache
    //   0	48	1	paramString	String
    //   0	48	2	paramLong1	long
    //   0	48	4	paramLong2	long
    //   22	17	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	11	43	finally
    //   15	24	43	finally
  }
  
  /* Error */
  public void releaseHoleSpan(CacheSpan paramCacheSpan)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: aload_0
    //   4: getfield 43	com/google/android/exoplayer2/upstream/cache/SimpleCache:lockedSpans	Ljava/util/HashMap;
    //   7: aload_1
    //   8: getfield 194	com/google/android/exoplayer2/upstream/cache/CacheSpan:key	Ljava/lang/String;
    //   11: invokevirtual 323	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   14: if_acmpne +16 -> 30
    //   17: iconst_1
    //   18: istore_2
    //   19: iload_2
    //   20: invokestatic 216	com/google/android/exoplayer2/util/Assertions:checkState	(Z)V
    //   23: aload_0
    //   24: invokevirtual 301	java/lang/Object:notifyAll	()V
    //   27: aload_0
    //   28: monitorexit
    //   29: return
    //   30: iconst_0
    //   31: istore_2
    //   32: goto -13 -> 19
    //   35: astore_1
    //   36: aload_0
    //   37: monitorexit
    //   38: aload_1
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	SimpleCache
    //   0	40	1	paramCacheSpan	CacheSpan
    //   18	14	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	17	35	finally
    //   19	27	35	finally
  }
  
  public void removeListener(String paramString, Cache.Listener paramListener)
  {
    try
    {
      ArrayList localArrayList = (ArrayList)this.listeners.get(paramString);
      if (localArrayList != null)
      {
        localArrayList.remove(paramListener);
        if (localArrayList.isEmpty()) {
          this.listeners.remove(paramString);
        }
      }
      return;
    }
    finally {}
  }
  
  public void removeSpan(CacheSpan paramCacheSpan)
    throws Cache.CacheException
  {
    try
    {
      removeSpan(paramCacheSpan, true);
      return;
    }
    finally
    {
      paramCacheSpan = finally;
      throw paramCacheSpan;
    }
  }
  
  public void setContentLength(String paramString, long paramLong)
    throws Cache.CacheException
  {
    try
    {
      this.index.setContentLength(paramString, paramLong);
      this.index.store();
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public File startFile(String paramString, long paramLong1, long paramLong2)
    throws Cache.CacheException
  {
    try
    {
      Assertions.checkState(this.lockedSpans.containsKey(paramString));
      if (!this.cacheDir.exists())
      {
        removeStaleSpansAndCachedContents();
        this.cacheDir.mkdirs();
      }
      this.evictor.onStartFile(this, paramString, paramLong1, paramLong2);
      paramString = SimpleCacheSpan.getCacheFile(this.cacheDir, this.index.assignIdForKey(paramString), paramLong1, System.currentTimeMillis());
      return paramString;
    }
    finally {}
  }
  
  /* Error */
  public SimpleCacheSpan startReadWrite(String paramString, long paramLong)
    throws java.lang.InterruptedException, Cache.CacheException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: lload_2
    //   5: invokevirtual 360	com/google/android/exoplayer2/upstream/cache/SimpleCache:startReadWriteNonBlocking	(Ljava/lang/String;J)Lcom/google/android/exoplayer2/upstream/cache/SimpleCacheSpan;
    //   8: astore 4
    //   10: aload 4
    //   12: ifnull +8 -> 20
    //   15: aload_0
    //   16: monitorexit
    //   17: aload 4
    //   19: areturn
    //   20: aload_0
    //   21: invokevirtual 363	java/lang/Object:wait	()V
    //   24: goto -22 -> 2
    //   27: astore_1
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_1
    //   31: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	SimpleCache
    //   0	32	1	paramString	String
    //   0	32	2	paramLong	long
    //   8	10	4	localSimpleCacheSpan	SimpleCacheSpan
    // Exception table:
    //   from	to	target	type
    //   2	10	27	finally
    //   20	24	27	finally
  }
  
  public SimpleCacheSpan startReadWriteNonBlocking(String paramString, long paramLong)
    throws Cache.CacheException
  {
    try
    {
      if (this.initializationException != null) {
        throw this.initializationException;
      }
    }
    finally {}
    SimpleCacheSpan localSimpleCacheSpan = getSpan(paramString, paramLong);
    if (localSimpleCacheSpan.isCached)
    {
      paramString = this.index.get(paramString).touch(localSimpleCacheSpan);
      notifySpanTouched(localSimpleCacheSpan, paramString);
    }
    for (;;)
    {
      return paramString;
      if (!this.lockedSpans.containsKey(paramString))
      {
        this.lockedSpans.put(paramString, localSimpleCacheSpan);
        paramString = localSimpleCacheSpan;
      }
      else
      {
        paramString = null;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/SimpleCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */