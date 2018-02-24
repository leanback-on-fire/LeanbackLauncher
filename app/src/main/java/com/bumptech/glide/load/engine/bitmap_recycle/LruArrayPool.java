package com.bumptech.glide.load.engine.bitmap_recycle;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.util.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public final class LruArrayPool
  implements ArrayPool
{
  static final int DEFAULT_SIZE = 4194304;
  private static final int MAX_OVER_SIZE_MULTIPLE = 8;
  private static final int SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2;
  private final Map<Class, ArrayAdapterInterface> adapters = new HashMap();
  private int currentSize;
  private final GroupedLinkedMap<Key, Object> groupedMap = new GroupedLinkedMap();
  private final KeyPool keyPool = new KeyPool(null);
  private final int maxSize;
  private final Map<Class, NavigableMap<Integer, Integer>> sortedSizes = new HashMap();
  
  @VisibleForTesting
  public LruArrayPool()
  {
    this.maxSize = 4194304;
  }
  
  public LruArrayPool(int paramInt)
  {
    this.maxSize = paramInt;
  }
  
  private void decrementArrayOfSize(int paramInt, Class<?> paramClass)
  {
    paramClass = getSizesForAdapter(paramClass);
    Integer localInteger = (Integer)paramClass.get(Integer.valueOf(paramInt));
    if (localInteger == null)
    {
      paramClass = String.valueOf(this);
      throw new NullPointerException(String.valueOf(paramClass).length() + 56 + "Tried to decrement empty size, size: " + paramInt + ", this: " + paramClass);
    }
    if (localInteger.intValue() == 1)
    {
      paramClass.remove(Integer.valueOf(paramInt));
      return;
    }
    paramClass.put(Integer.valueOf(paramInt), Integer.valueOf(localInteger.intValue() - 1));
  }
  
  private void evict()
  {
    evictToSize(this.maxSize);
  }
  
  private void evictToSize(int paramInt)
  {
    while (this.currentSize > paramInt)
    {
      Object localObject = this.groupedMap.removeLast();
      Preconditions.checkNotNull(localObject);
      ArrayAdapterInterface localArrayAdapterInterface = getAdapterFromObject(localObject);
      this.currentSize -= localArrayAdapterInterface.getArrayLength(localObject) * localArrayAdapterInterface.getElementSizeInBytes();
      decrementArrayOfSize(localArrayAdapterInterface.getArrayLength(localObject), localObject.getClass());
      if (Log.isLoggable(localArrayAdapterInterface.getTag(), 2))
      {
        String str = localArrayAdapterInterface.getTag();
        int i = localArrayAdapterInterface.getArrayLength(localObject);
        Log.v(str, 20 + "evicted: " + i);
      }
    }
  }
  
  private <T> ArrayAdapterInterface<T> getAdapterFromObject(T paramT)
  {
    return getAdapterFromType(paramT.getClass());
  }
  
  private <T> ArrayAdapterInterface<T> getAdapterFromType(Class<T> paramClass)
  {
    ArrayAdapterInterface localArrayAdapterInterface = (ArrayAdapterInterface)this.adapters.get(paramClass);
    Object localObject = localArrayAdapterInterface;
    if (localArrayAdapterInterface == null) {
      if (!paramClass.equals(int[].class)) {
        break label51;
      }
    }
    for (localObject = new IntegerArrayAdapter();; localObject = new ByteArrayAdapter())
    {
      this.adapters.put(paramClass, localObject);
      return (ArrayAdapterInterface<T>)localObject;
      label51:
      if (!paramClass.equals(byte[].class)) {
        break;
      }
    }
    paramClass = String.valueOf(paramClass.getSimpleName());
    if (paramClass.length() != 0) {}
    for (paramClass = "No array pool found for: ".concat(paramClass);; paramClass = new String("No array pool found for: ")) {
      throw new IllegalArgumentException(paramClass);
    }
  }
  
  @Nullable
  private <T> T getArrayForKey(Key paramKey)
  {
    return (T)this.groupedMap.get(paramKey);
  }
  
  private NavigableMap<Integer, Integer> getSizesForAdapter(Class<?> paramClass)
  {
    NavigableMap localNavigableMap = (NavigableMap)this.sortedSizes.get(paramClass);
    Object localObject = localNavigableMap;
    if (localNavigableMap == null)
    {
      localObject = new TreeMap();
      this.sortedSizes.put(paramClass, localObject);
    }
    return (NavigableMap<Integer, Integer>)localObject;
  }
  
  private boolean isNoMoreThanHalfFull()
  {
    return (this.currentSize == 0) || (this.maxSize / this.currentSize >= 2);
  }
  
  private boolean isSmallEnoughForReuse(int paramInt)
  {
    return paramInt <= this.maxSize / 2;
  }
  
  private boolean mayFillRequest(int paramInt, Integer paramInteger)
  {
    return (paramInteger != null) && ((isNoMoreThanHalfFull()) || (paramInteger.intValue() <= paramInt * 8));
  }
  
  public void clearMemory()
  {
    try
    {
      evictToSize(0);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public <T> T get(int paramInt, Class<T> paramClass)
  {
    ArrayAdapterInterface localArrayAdapterInterface = getAdapterFromType(paramClass);
    try
    {
      Object localObject = (Integer)getSizesForAdapter(paramClass).ceilingKey(Integer.valueOf(paramInt));
      if (mayFillRequest(paramInt, (Integer)localObject)) {}
      for (localObject = this.keyPool.get(((Integer)localObject).intValue(), paramClass);; localObject = this.keyPool.get(paramInt, paramClass))
      {
        localObject = getArrayForKey((Key)localObject);
        if (localObject != null)
        {
          this.currentSize -= localArrayAdapterInterface.getArrayLength(localObject) * localArrayAdapterInterface.getElementSizeInBytes();
          decrementArrayOfSize(localArrayAdapterInterface.getArrayLength(localObject), paramClass);
        }
        if (localObject == null) {
          break;
        }
        localArrayAdapterInterface.resetArray(localObject);
        return (T)localObject;
      }
      if (!Log.isLoggable(localArrayAdapterInterface.getTag(), 2)) {
        break label182;
      }
    }
    finally {}
    Log.v(localArrayAdapterInterface.getTag(), 27 + "Allocated " + paramInt + " bytes");
    label182:
    return (T)localArrayAdapterInterface.newArray(paramInt);
  }
  
  int getCurrentSize()
  {
    int j = 0;
    Iterator localIterator1 = this.sortedSizes.keySet().iterator();
    if (localIterator1.hasNext())
    {
      Class localClass = (Class)localIterator1.next();
      Iterator localIterator2 = ((NavigableMap)this.sortedSizes.get(localClass)).keySet().iterator();
      int i = j;
      for (;;)
      {
        j = i;
        if (!localIterator2.hasNext()) {
          break;
        }
        Integer localInteger = (Integer)localIterator2.next();
        ArrayAdapterInterface localArrayAdapterInterface = getAdapterFromType(localClass);
        j = localInteger.intValue();
        i += ((Integer)((NavigableMap)this.sortedSizes.get(localClass)).get(localInteger)).intValue() * j * localArrayAdapterInterface.getElementSizeInBytes();
      }
    }
    return j;
  }
  
  public <T> void put(T paramT, Class<T> paramClass)
  {
    for (;;)
    {
      int i;
      try
      {
        Object localObject = getAdapterFromType(paramClass);
        i = ((ArrayAdapterInterface)localObject).getArrayLength(paramT);
        int j = i * ((ArrayAdapterInterface)localObject).getElementSizeInBytes();
        boolean bool = isSmallEnoughForReuse(j);
        if (!bool) {
          return;
        }
        localObject = this.keyPool.get(i, paramClass);
        this.groupedMap.put((Poolable)localObject, paramT);
        paramT = getSizesForAdapter(paramClass);
        paramClass = (Integer)paramT.get(Integer.valueOf(((Key)localObject).size));
        int k = ((Key)localObject).size;
        if (paramClass == null)
        {
          i = 1;
          paramT.put(Integer.valueOf(k), Integer.valueOf(i));
          this.currentSize += j;
          evict();
          continue;
        }
        i = paramClass.intValue();
      }
      finally {}
      i += 1;
    }
  }
  
  public void trimMemory(int paramInt)
  {
    if (paramInt >= 40) {}
    for (;;)
    {
      try
      {
        clearMemory();
        return;
      }
      finally {}
      if (paramInt >= 20) {
        evictToSize(this.maxSize / 2);
      }
    }
  }
  
  private static final class Key
    implements Poolable
  {
    private Class arrayClass;
    private final LruArrayPool.KeyPool pool;
    private int size;
    
    Key(LruArrayPool.KeyPool paramKeyPool)
    {
      this.pool = paramKeyPool;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if ((paramObject instanceof Key))
      {
        paramObject = (Key)paramObject;
        bool1 = bool2;
        if (this.size == ((Key)paramObject).size)
        {
          bool1 = bool2;
          if (this.arrayClass == ((Key)paramObject).arrayClass) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      int j = this.size;
      if (this.arrayClass != null) {}
      for (int i = this.arrayClass.hashCode();; i = 0) {
        return j * 31 + i;
      }
    }
    
    void init(int paramInt, Class paramClass)
    {
      this.size = paramInt;
      this.arrayClass = paramClass;
    }
    
    public void offer()
    {
      this.pool.offer(this);
    }
    
    public String toString()
    {
      int i = this.size;
      String str = String.valueOf(this.arrayClass);
      return String.valueOf(str).length() + 27 + "Key{size=" + i + "array=" + str + "}";
    }
  }
  
  private static final class KeyPool
    extends BaseKeyPool<LruArrayPool.Key>
  {
    protected LruArrayPool.Key create()
    {
      return new LruArrayPool.Key(this);
    }
    
    LruArrayPool.Key get(int paramInt, Class paramClass)
    {
      LruArrayPool.Key localKey = (LruArrayPool.Key)get();
      localKey.init(paramInt, paramClass);
      return localKey;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/LruArrayPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */