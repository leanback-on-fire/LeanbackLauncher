package com.bumptech.glide.load.engine.bitmap_recycle;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.Nullable;
import com.bumptech.glide.util.Util;
import java.util.TreeMap;

@TargetApi(19)
class SizeStrategy
  implements LruPoolStrategy
{
  private static final int MAX_SIZE_MULTIPLE = 8;
  private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap();
  private final KeyPool keyPool = new KeyPool();
  private final TreeMap<Integer, Integer> sortedSizes = new PrettyPrintTreeMap();
  
  private void decrementBitmapOfSize(Integer paramInteger)
  {
    Integer localInteger = (Integer)this.sortedSizes.get(paramInteger);
    if (localInteger.intValue() == 1)
    {
      this.sortedSizes.remove(paramInteger);
      return;
    }
    this.sortedSizes.put(paramInteger, Integer.valueOf(localInteger.intValue() - 1));
  }
  
  private static String getBitmapString(int paramInt)
  {
    return 13 + "[" + paramInt + "]";
  }
  
  private static String getBitmapString(Bitmap paramBitmap)
  {
    return getBitmapString(Util.getBitmapByteSize(paramBitmap));
  }
  
  @Nullable
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    int i = Util.getBitmapByteSize(paramInt1, paramInt2, paramConfig);
    Key localKey = this.keyPool.get(i);
    Integer localInteger = (Integer)this.sortedSizes.ceilingKey(Integer.valueOf(i));
    Object localObject = localKey;
    if (localInteger != null)
    {
      localObject = localKey;
      if (localInteger.intValue() != i)
      {
        localObject = localKey;
        if (localInteger.intValue() <= i * 8)
        {
          this.keyPool.offer(localKey);
          localObject = this.keyPool.get(localInteger.intValue());
        }
      }
    }
    localObject = (Bitmap)this.groupedMap.get((Poolable)localObject);
    if (localObject != null)
    {
      ((Bitmap)localObject).reconfigure(paramInt1, paramInt2, paramConfig);
      decrementBitmapOfSize(localInteger);
    }
    return (Bitmap)localObject;
  }
  
  public int getSize(Bitmap paramBitmap)
  {
    return Util.getBitmapByteSize(paramBitmap);
  }
  
  public String logBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return getBitmapString(Util.getBitmapByteSize(paramInt1, paramInt2, paramConfig));
  }
  
  public String logBitmap(Bitmap paramBitmap)
  {
    return getBitmapString(paramBitmap);
  }
  
  public void put(Bitmap paramBitmap)
  {
    int i = Util.getBitmapByteSize(paramBitmap);
    Key localKey = this.keyPool.get(i);
    this.groupedMap.put(localKey, paramBitmap);
    paramBitmap = (Integer)this.sortedSizes.get(Integer.valueOf(localKey.size));
    TreeMap localTreeMap = this.sortedSizes;
    int j = localKey.size;
    if (paramBitmap == null) {}
    for (i = 1;; i = paramBitmap.intValue() + 1)
    {
      localTreeMap.put(Integer.valueOf(j), Integer.valueOf(i));
      return;
    }
  }
  
  @Nullable
  public Bitmap removeLast()
  {
    Bitmap localBitmap = (Bitmap)this.groupedMap.removeLast();
    if (localBitmap != null) {
      decrementBitmapOfSize(Integer.valueOf(Util.getBitmapByteSize(localBitmap)));
    }
    return localBitmap;
  }
  
  public String toString()
  {
    String str1 = String.valueOf(this.groupedMap);
    String str2 = String.valueOf(this.sortedSizes);
    return String.valueOf(str1).length() + 30 + String.valueOf(str2).length() + "SizeStrategy:\n  " + str1 + "\n  SortedSizes" + str2;
  }
  
  static final class Key
    implements Poolable
  {
    private final SizeStrategy.KeyPool pool;
    private int size;
    
    Key(SizeStrategy.KeyPool paramKeyPool)
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
        if (this.size == ((Key)paramObject).size) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return this.size;
    }
    
    public void init(int paramInt)
    {
      this.size = paramInt;
    }
    
    public void offer()
    {
      this.pool.offer(this);
    }
    
    public String toString()
    {
      return SizeStrategy.getBitmapString(this.size);
    }
  }
  
  static class KeyPool
    extends BaseKeyPool<SizeStrategy.Key>
  {
    protected SizeStrategy.Key create()
    {
      return new SizeStrategy.Key(this);
    }
    
    public SizeStrategy.Key get(int paramInt)
    {
      SizeStrategy.Key localKey = (SizeStrategy.Key)get();
      localKey.init(paramInt);
      return localKey;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/SizeStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */