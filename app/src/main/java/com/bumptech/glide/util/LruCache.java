package com.bumptech.glide.util;

import android.support.annotation.Nullable;
import java.util.LinkedHashMap;

public class LruCache<T, Y>
{
  private final LinkedHashMap<T, Y> cache = new LinkedHashMap(100, 0.75F, true);
  private int currentSize = 0;
  private final int initialMaxSize;
  private int maxSize;
  
  public LruCache(int paramInt)
  {
    this.initialMaxSize = paramInt;
    this.maxSize = paramInt;
  }
  
  private void evict()
  {
    trimToSize(this.maxSize);
  }
  
  public void clearMemory()
  {
    trimToSize(0);
  }
  
  public boolean contains(T paramT)
  {
    try
    {
      boolean bool = this.cache.containsKey(paramT);
      return bool;
    }
    finally
    {
      paramT = finally;
      throw paramT;
    }
  }
  
  @Nullable
  public Y get(T paramT)
  {
    try
    {
      paramT = this.cache.get(paramT);
      return paramT;
    }
    finally
    {
      paramT = finally;
      throw paramT;
    }
  }
  
  public int getCurrentSize()
  {
    try
    {
      int i = this.currentSize;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getMaxSize()
  {
    try
    {
      int i = this.maxSize;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected int getSize(Y paramY)
  {
    return 1;
  }
  
  protected void onItemEvicted(T paramT, Y paramY) {}
  
  /* Error */
  public Y put(T paramT, Y paramY)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_2
    //   4: invokevirtual 64	com/bumptech/glide/util/LruCache:getSize	(Ljava/lang/Object;)I
    //   7: aload_0
    //   8: getfield 31	com/bumptech/glide/util/LruCache:maxSize	I
    //   11: if_icmplt +15 -> 26
    //   14: aload_0
    //   15: aload_1
    //   16: aload_2
    //   17: invokevirtual 66	com/bumptech/glide/util/LruCache:onItemEvicted	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   20: aconst_null
    //   21: astore_1
    //   22: aload_0
    //   23: monitorexit
    //   24: aload_1
    //   25: areturn
    //   26: aload_0
    //   27: getfield 25	com/bumptech/glide/util/LruCache:cache	Ljava/util/LinkedHashMap;
    //   30: aload_1
    //   31: aload_2
    //   32: invokevirtual 68	java/util/LinkedHashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   35: astore_1
    //   36: aload_2
    //   37: ifnull +17 -> 54
    //   40: aload_0
    //   41: aload_0
    //   42: getfield 27	com/bumptech/glide/util/LruCache:currentSize	I
    //   45: aload_0
    //   46: aload_2
    //   47: invokevirtual 64	com/bumptech/glide/util/LruCache:getSize	(Ljava/lang/Object;)I
    //   50: iadd
    //   51: putfield 27	com/bumptech/glide/util/LruCache:currentSize	I
    //   54: aload_1
    //   55: ifnull +17 -> 72
    //   58: aload_0
    //   59: aload_0
    //   60: getfield 27	com/bumptech/glide/util/LruCache:currentSize	I
    //   63: aload_0
    //   64: aload_1
    //   65: invokevirtual 64	com/bumptech/glide/util/LruCache:getSize	(Ljava/lang/Object;)I
    //   68: isub
    //   69: putfield 27	com/bumptech/glide/util/LruCache:currentSize	I
    //   72: aload_0
    //   73: invokespecial 70	com/bumptech/glide/util/LruCache:evict	()V
    //   76: goto -54 -> 22
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	LruCache
    //   0	84	1	paramT	T
    //   0	84	2	paramY	Y
    // Exception table:
    //   from	to	target	type
    //   2	20	79	finally
    //   26	36	79	finally
    //   40	54	79	finally
    //   58	72	79	finally
    //   72	76	79	finally
  }
  
  @Nullable
  public Y remove(T paramT)
  {
    try
    {
      paramT = this.cache.remove(paramT);
      if (paramT != null) {
        this.currentSize -= getSize(paramT);
      }
      return paramT;
    }
    finally {}
  }
  
  public void setSizeMultiplier(float paramFloat)
  {
    if (paramFloat < 0.0F) {
      try
      {
        throw new IllegalArgumentException("Multiplier must be >= 0");
      }
      finally {}
    }
    this.maxSize = Math.round(this.initialMaxSize * paramFloat);
    evict();
  }
  
  /* Error */
  protected void trimToSize(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 27	com/bumptech/glide/util/LruCache:currentSize	I
    //   6: iload_1
    //   7: if_icmple +75 -> 82
    //   10: aload_0
    //   11: getfield 25	com/bumptech/glide/util/LruCache:cache	Ljava/util/LinkedHashMap;
    //   14: invokevirtual 93	java/util/LinkedHashMap:entrySet	()Ljava/util/Set;
    //   17: invokeinterface 99 1 0
    //   22: invokeinterface 105 1 0
    //   27: checkcast 107	java/util/Map$Entry
    //   30: astore_3
    //   31: aload_3
    //   32: invokeinterface 110 1 0
    //   37: astore_2
    //   38: aload_0
    //   39: aload_0
    //   40: getfield 27	com/bumptech/glide/util/LruCache:currentSize	I
    //   43: aload_0
    //   44: aload_2
    //   45: invokevirtual 64	com/bumptech/glide/util/LruCache:getSize	(Ljava/lang/Object;)I
    //   48: isub
    //   49: putfield 27	com/bumptech/glide/util/LruCache:currentSize	I
    //   52: aload_3
    //   53: invokeinterface 113 1 0
    //   58: astore_3
    //   59: aload_0
    //   60: getfield 25	com/bumptech/glide/util/LruCache:cache	Ljava/util/LinkedHashMap;
    //   63: aload_3
    //   64: invokevirtual 74	java/util/LinkedHashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   67: pop
    //   68: aload_0
    //   69: aload_3
    //   70: aload_2
    //   71: invokevirtual 66	com/bumptech/glide/util/LruCache:onItemEvicted	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   74: goto -72 -> 2
    //   77: astore_2
    //   78: aload_0
    //   79: monitorexit
    //   80: aload_2
    //   81: athrow
    //   82: aload_0
    //   83: monitorexit
    //   84: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	85	0	this	LruCache
    //   0	85	1	paramInt	int
    //   37	34	2	localObject1	Object
    //   77	4	2	localObject2	Object
    //   30	40	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   2	74	77	finally
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/util/LruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */