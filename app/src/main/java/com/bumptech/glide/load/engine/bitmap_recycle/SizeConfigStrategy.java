package com.bumptech.glide.load.engine.bitmap_recycle;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.Nullable;
import com.bumptech.glide.util.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

@TargetApi(19)
public class SizeConfigStrategy
  implements LruPoolStrategy
{
  private static final Bitmap.Config[] ALPHA_8_IN_CONFIGS = { Bitmap.Config.ALPHA_8 };
  private static final Bitmap.Config[] ARGB_4444_IN_CONFIGS;
  private static final Bitmap.Config[] ARGB_8888_IN_CONFIGS = { Bitmap.Config.ARGB_8888, null };
  private static final int MAX_SIZE_MULTIPLE = 8;
  private static final Bitmap.Config[] RGB_565_IN_CONFIGS = { Bitmap.Config.RGB_565 };
  private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap();
  private final KeyPool keyPool = new KeyPool();
  private final Map<Bitmap.Config, NavigableMap<Integer, Integer>> sortedSizes = new HashMap();
  
  static
  {
    ARGB_4444_IN_CONFIGS = new Bitmap.Config[] { Bitmap.Config.ARGB_4444 };
  }
  
  private void decrementBitmapOfSize(Integer paramInteger, Bitmap paramBitmap)
  {
    Object localObject = getSizesForConfig(paramBitmap.getConfig());
    Integer localInteger = (Integer)((NavigableMap)localObject).get(paramInteger);
    if (localInteger == null)
    {
      paramInteger = String.valueOf(paramInteger);
      paramBitmap = String.valueOf(logBitmap(paramBitmap));
      localObject = String.valueOf(this);
      throw new NullPointerException(String.valueOf(paramInteger).length() + 56 + String.valueOf(paramBitmap).length() + String.valueOf(localObject).length() + "Tried to decrement empty size, size: " + paramInteger + ", removed: " + paramBitmap + ", this: " + (String)localObject);
    }
    if (localInteger.intValue() == 1)
    {
      ((NavigableMap)localObject).remove(paramInteger);
      return;
    }
    ((NavigableMap)localObject).put(paramInteger, Integer.valueOf(localInteger.intValue() - 1));
  }
  
  private Key findBestKey(int paramInt, Bitmap.Config paramConfig)
  {
    Key localKey2 = this.keyPool.get(paramInt, paramConfig);
    Bitmap.Config[] arrayOfConfig = getInConfigs(paramConfig);
    int j = arrayOfConfig.length;
    int i = 0;
    for (;;)
    {
      Key localKey1 = localKey2;
      Bitmap.Config localConfig;
      Integer localInteger;
      if (i < j)
      {
        localConfig = arrayOfConfig[i];
        localInteger = (Integer)getSizesForConfig(localConfig).ceilingKey(Integer.valueOf(paramInt));
        if ((localInteger == null) || (localInteger.intValue() > paramInt * 8)) {
          break label143;
        }
        if (localInteger.intValue() == paramInt)
        {
          if (localConfig != null) {
            break label127;
          }
          localKey1 = localKey2;
          if (paramConfig == null) {
            break label124;
          }
        }
      }
      for (;;)
      {
        this.keyPool.offer(localKey2);
        localKey1 = this.keyPool.get(localInteger.intValue(), localConfig);
        label124:
        label127:
        do
        {
          return localKey1;
          localKey1 = localKey2;
        } while (localConfig.equals(paramConfig));
      }
      label143:
      i += 1;
    }
  }
  
  private static String getBitmapString(int paramInt, Bitmap.Config paramConfig)
  {
    paramConfig = String.valueOf(paramConfig);
    return String.valueOf(paramConfig).length() + 15 + "[" + paramInt + "](" + paramConfig + ")";
  }
  
  private static Bitmap.Config[] getInConfigs(Bitmap.Config paramConfig)
  {
    switch (paramConfig)
    {
    default: 
      return new Bitmap.Config[] { paramConfig };
    case ???: 
      return ARGB_8888_IN_CONFIGS;
    case ???: 
      return RGB_565_IN_CONFIGS;
    case ???: 
      return ARGB_4444_IN_CONFIGS;
    }
    return ALPHA_8_IN_CONFIGS;
  }
  
  private NavigableMap<Integer, Integer> getSizesForConfig(Bitmap.Config paramConfig)
  {
    NavigableMap localNavigableMap = (NavigableMap)this.sortedSizes.get(paramConfig);
    Object localObject = localNavigableMap;
    if (localNavigableMap == null)
    {
      localObject = new TreeMap();
      this.sortedSizes.put(paramConfig, localObject);
    }
    return (NavigableMap<Integer, Integer>)localObject;
  }
  
  @Nullable
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    paramConfig = findBestKey(Util.getBitmapByteSize(paramInt1, paramInt2, paramConfig), paramConfig);
    Bitmap localBitmap = (Bitmap)this.groupedMap.get(paramConfig);
    if (localBitmap != null)
    {
      decrementBitmapOfSize(Integer.valueOf(paramConfig.size), localBitmap);
      if (localBitmap.getConfig() == null) {
        break label68;
      }
    }
    label68:
    for (paramConfig = localBitmap.getConfig();; paramConfig = Bitmap.Config.ARGB_8888)
    {
      localBitmap.reconfigure(paramInt1, paramInt2, paramConfig);
      return localBitmap;
    }
  }
  
  public int getSize(Bitmap paramBitmap)
  {
    return Util.getBitmapByteSize(paramBitmap);
  }
  
  public String logBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return getBitmapString(Util.getBitmapByteSize(paramInt1, paramInt2, paramConfig), paramConfig);
  }
  
  public String logBitmap(Bitmap paramBitmap)
  {
    return getBitmapString(Util.getBitmapByteSize(paramBitmap), paramBitmap.getConfig());
  }
  
  public void put(Bitmap paramBitmap)
  {
    int i = Util.getBitmapByteSize(paramBitmap);
    Key localKey = this.keyPool.get(i, paramBitmap.getConfig());
    this.groupedMap.put(localKey, paramBitmap);
    paramBitmap = getSizesForConfig(paramBitmap.getConfig());
    Integer localInteger = (Integer)paramBitmap.get(Integer.valueOf(localKey.size));
    int j = localKey.size;
    if (localInteger == null) {}
    for (i = 1;; i = localInteger.intValue() + 1)
    {
      paramBitmap.put(Integer.valueOf(j), Integer.valueOf(i));
      return;
    }
  }
  
  @Nullable
  public Bitmap removeLast()
  {
    Bitmap localBitmap = (Bitmap)this.groupedMap.removeLast();
    if (localBitmap != null) {
      decrementBitmapOfSize(Integer.valueOf(Util.getBitmapByteSize(localBitmap)), localBitmap);
    }
    return localBitmap;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("SizeConfigStrategy{groupedMap=").append(this.groupedMap).append(", sortedSizes=(");
    Iterator localIterator = this.sortedSizes.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localStringBuilder.append(localEntry.getKey()).append('[').append(localEntry.getValue()).append("], ");
    }
    if (!this.sortedSizes.isEmpty()) {
      localStringBuilder.replace(localStringBuilder.length() - 2, localStringBuilder.length(), "");
    }
    return ")}";
  }
  
  static final class Key
    implements Poolable
  {
    private Bitmap.Config config;
    private final SizeConfigStrategy.KeyPool pool;
    private int size;
    
    public Key(SizeConfigStrategy.KeyPool paramKeyPool)
    {
      this.pool = paramKeyPool;
    }
    
    Key(SizeConfigStrategy.KeyPool paramKeyPool, int paramInt, Bitmap.Config paramConfig)
    {
      this(paramKeyPool);
      init(paramInt, paramConfig);
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
          if (Util.bothNullOrEqual(this.config, ((Key)paramObject).config)) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      int j = this.size;
      if (this.config != null) {}
      for (int i = this.config.hashCode();; i = 0) {
        return j * 31 + i;
      }
    }
    
    public void init(int paramInt, Bitmap.Config paramConfig)
    {
      this.size = paramInt;
      this.config = paramConfig;
    }
    
    public void offer()
    {
      this.pool.offer(this);
    }
    
    public String toString()
    {
      return SizeConfigStrategy.getBitmapString(this.size, this.config);
    }
  }
  
  static class KeyPool
    extends BaseKeyPool<SizeConfigStrategy.Key>
  {
    protected SizeConfigStrategy.Key create()
    {
      return new SizeConfigStrategy.Key(this);
    }
    
    public SizeConfigStrategy.Key get(int paramInt, Bitmap.Config paramConfig)
    {
      SizeConfigStrategy.Key localKey = (SizeConfigStrategy.Key)get();
      localKey.init(paramInt, paramConfig);
      return localKey;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/SizeConfigStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */