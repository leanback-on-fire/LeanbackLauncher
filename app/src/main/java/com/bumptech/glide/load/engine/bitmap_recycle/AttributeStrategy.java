package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.bumptech.glide.util.Util;

class AttributeStrategy
  implements LruPoolStrategy
{
  private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap();
  private final KeyPool keyPool = new KeyPool();
  
  private static String getBitmapString(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    paramConfig = String.valueOf(paramConfig);
    return String.valueOf(paramConfig).length() + 27 + "[" + paramInt1 + "x" + paramInt2 + "], " + paramConfig;
  }
  
  private static String getBitmapString(Bitmap paramBitmap)
  {
    return getBitmapString(paramBitmap.getWidth(), paramBitmap.getHeight(), paramBitmap.getConfig());
  }
  
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    paramConfig = this.keyPool.get(paramInt1, paramInt2, paramConfig);
    return (Bitmap)this.groupedMap.get(paramConfig);
  }
  
  public int getSize(Bitmap paramBitmap)
  {
    return Util.getBitmapByteSize(paramBitmap);
  }
  
  public String logBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return getBitmapString(paramInt1, paramInt2, paramConfig);
  }
  
  public String logBitmap(Bitmap paramBitmap)
  {
    return getBitmapString(paramBitmap);
  }
  
  public void put(Bitmap paramBitmap)
  {
    Key localKey = this.keyPool.get(paramBitmap.getWidth(), paramBitmap.getHeight(), paramBitmap.getConfig());
    this.groupedMap.put(localKey, paramBitmap);
  }
  
  public Bitmap removeLast()
  {
    return (Bitmap)this.groupedMap.removeLast();
  }
  
  public String toString()
  {
    String str = String.valueOf(this.groupedMap);
    return String.valueOf(str).length() + 21 + "AttributeStrategy:\n  " + str;
  }
  
  static class Key
    implements Poolable
  {
    private Bitmap.Config config;
    private int height;
    private final AttributeStrategy.KeyPool pool;
    private int width;
    
    public Key(AttributeStrategy.KeyPool paramKeyPool)
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
        if (this.width == ((Key)paramObject).width)
        {
          bool1 = bool2;
          if (this.height == ((Key)paramObject).height)
          {
            bool1 = bool2;
            if (this.config == ((Key)paramObject).config) {
              bool1 = true;
            }
          }
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      int j = this.width;
      int k = this.height;
      if (this.config != null) {}
      for (int i = this.config.hashCode();; i = 0) {
        return (j * 31 + k) * 31 + i;
      }
    }
    
    public void init(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.config = paramConfig;
    }
    
    public void offer()
    {
      this.pool.offer(this);
    }
    
    public String toString()
    {
      return AttributeStrategy.getBitmapString(this.width, this.height, this.config);
    }
  }
  
  static class KeyPool
    extends BaseKeyPool<AttributeStrategy.Key>
  {
    protected AttributeStrategy.Key create()
    {
      return new AttributeStrategy.Key(this);
    }
    
    public AttributeStrategy.Key get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
    {
      AttributeStrategy.Key localKey = (AttributeStrategy.Key)get();
      localKey.init(paramInt1, paramInt2, paramConfig);
      return localKey;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/AttributeStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */