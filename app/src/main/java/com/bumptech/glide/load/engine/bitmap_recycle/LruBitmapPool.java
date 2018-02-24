package com.bumptech.glide.load.engine.bitmap_recycle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LruBitmapPool
  implements BitmapPool
{
  private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;
  private static final String TAG = "LruBitmapPool";
  private final Set<Bitmap.Config> allowedConfigs;
  private int currentSize;
  private int evictions;
  private int hits;
  private final int initialMaxSize;
  private int maxSize;
  private int misses;
  private int puts;
  private final LruPoolStrategy strategy;
  private final BitmapTracker tracker;
  
  public LruBitmapPool(int paramInt)
  {
    this(paramInt, getDefaultStrategy(), getDefaultAllowedConfigs());
  }
  
  LruBitmapPool(int paramInt, LruPoolStrategy paramLruPoolStrategy, Set<Bitmap.Config> paramSet)
  {
    this.initialMaxSize = paramInt;
    this.maxSize = paramInt;
    this.strategy = paramLruPoolStrategy;
    this.allowedConfigs = paramSet;
    this.tracker = new NullBitmapTracker(null);
  }
  
  public LruBitmapPool(int paramInt, Set<Bitmap.Config> paramSet)
  {
    this(paramInt, getDefaultStrategy(), paramSet);
  }
  
  private void dump()
  {
    if (Log.isLoggable("LruBitmapPool", 2)) {
      dumpUnchecked();
    }
  }
  
  private void dumpUnchecked()
  {
    int i = this.hits;
    int j = this.misses;
    int k = this.puts;
    int m = this.evictions;
    int n = this.currentSize;
    int i1 = this.maxSize;
    String str = String.valueOf(this.strategy);
    Log.v("LruBitmapPool", String.valueOf(str).length() + 133 + "Hits=" + i + ", misses=" + j + ", puts=" + k + ", evictions=" + m + ", currentSize=" + n + ", maxSize=" + i1 + "\nStrategy=" + str);
  }
  
  private void evict()
  {
    trimToSize(this.maxSize);
  }
  
  private static Set<Bitmap.Config> getDefaultAllowedConfigs()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(Arrays.asList(Bitmap.Config.values()));
    if (Build.VERSION.SDK_INT >= 19) {
      localHashSet.add(null);
    }
    return Collections.unmodifiableSet(localHashSet);
  }
  
  private static LruPoolStrategy getDefaultStrategy()
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return new SizeConfigStrategy();
    }
    return new AttributeStrategy();
  }
  
  @Nullable
  private Bitmap getDirtyOrNull(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    for (;;)
    {
      Object localObject2;
      try
      {
        localObject2 = this.strategy;
        if (paramConfig != null)
        {
          localObject1 = paramConfig;
          localObject2 = ((LruPoolStrategy)localObject2).get(paramInt1, paramInt2, (Bitmap.Config)localObject1);
          if (localObject2 != null) {
            break label176;
          }
          if (Log.isLoggable("LruBitmapPool", 3))
          {
            localObject1 = String.valueOf(this.strategy.logBitmap(paramInt1, paramInt2, paramConfig));
            if (((String)localObject1).length() != 0)
            {
              localObject1 = "Missing bitmap=".concat((String)localObject1);
              Log.d("LruBitmapPool", (String)localObject1);
            }
          }
          else
          {
            this.misses += 1;
            if (Log.isLoggable("LruBitmapPool", 2))
            {
              paramConfig = String.valueOf(this.strategy.logBitmap(paramInt1, paramInt2, paramConfig));
              if (paramConfig.length() == 0) {
                break label225;
              }
              paramConfig = "Get bitmap=".concat(paramConfig);
              Log.v("LruBitmapPool", paramConfig);
            }
            dump();
            return (Bitmap)localObject2;
          }
        }
        else
        {
          localObject1 = DEFAULT_CONFIG;
          continue;
        }
        Object localObject1 = new String("Missing bitmap=");
        continue;
        this.hits += 1;
      }
      finally {}
      label176:
      this.currentSize -= this.strategy.getSize((Bitmap)localObject2);
      this.tracker.remove((Bitmap)localObject2);
      normalize((Bitmap)localObject2);
      continue;
      label225:
      paramConfig = new String("Get bitmap=");
    }
  }
  
  @TargetApi(12)
  private static void maybeSetAlpha(Bitmap paramBitmap)
  {
    if (Build.VERSION.SDK_INT >= 12) {
      paramBitmap.setHasAlpha(true);
    }
  }
  
  @TargetApi(19)
  private static void maybeSetPreMultiplied(Bitmap paramBitmap)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      paramBitmap.setPremultiplied(true);
    }
  }
  
  private static void normalize(Bitmap paramBitmap)
  {
    maybeSetAlpha(paramBitmap);
    maybeSetPreMultiplied(paramBitmap);
  }
  
  private void trimToSize(int paramInt)
  {
    for (;;)
    {
      try
      {
        Bitmap localBitmap;
        if (this.currentSize > paramInt)
        {
          localBitmap = this.strategy.removeLast();
          if (localBitmap == null)
          {
            if (Log.isLoggable("LruBitmapPool", 5))
            {
              Log.w("LruBitmapPool", "Size mismatch, resetting");
              dumpUnchecked();
            }
            this.currentSize = 0;
          }
        }
        else
        {
          return;
        }
        this.tracker.remove(localBitmap);
        this.currentSize -= this.strategy.getSize(localBitmap);
        this.evictions += 1;
        if (Log.isLoggable("LruBitmapPool", 3))
        {
          String str1 = String.valueOf(this.strategy.logBitmap(localBitmap));
          if (str1.length() != 0)
          {
            str1 = "Evicting bitmap=".concat(str1);
            Log.d("LruBitmapPool", str1);
          }
        }
        else
        {
          dump();
          localBitmap.recycle();
          continue;
        }
        String str2 = new String("Evicting bitmap=");
      }
      finally {}
    }
  }
  
  public void clearMemory()
  {
    if (Log.isLoggable("LruBitmapPool", 3)) {
      Log.d("LruBitmapPool", "clearMemory");
    }
    trimToSize(0);
  }
  
  @NonNull
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    Bitmap localBitmap = getDirtyOrNull(paramInt1, paramInt2, paramConfig);
    if (localBitmap != null)
    {
      localBitmap.eraseColor(0);
      return localBitmap;
    }
    return Bitmap.createBitmap(paramInt1, paramInt2, paramConfig);
  }
  
  @NonNull
  public Bitmap getDirty(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    Bitmap localBitmap2 = getDirtyOrNull(paramInt1, paramInt2, paramConfig);
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null) {
      localBitmap1 = Bitmap.createBitmap(paramInt1, paramInt2, paramConfig);
    }
    return localBitmap1;
  }
  
  public int getMaxSize()
  {
    return this.maxSize;
  }
  
  public void put(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      try
      {
        throw new NullPointerException("Bitmap must not be null");
      }
      finally {}
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalStateException("Cannot pool recycled bitmap");
    }
    if ((!paramBitmap.isMutable()) || (this.strategy.getSize(paramBitmap) > this.maxSize) || (!this.allowedConfigs.contains(paramBitmap.getConfig())))
    {
      if (Log.isLoggable("LruBitmapPool", 2))
      {
        String str = String.valueOf(this.strategy.logBitmap(paramBitmap));
        boolean bool1 = paramBitmap.isMutable();
        boolean bool2 = this.allowedConfigs.contains(paramBitmap.getConfig());
        Log.v("LruBitmapPool", String.valueOf(str).length() + 78 + "Reject bitmap from pool, bitmap: " + str + ", is mutable: " + bool1 + ", is allowed config: " + bool2);
      }
      paramBitmap.recycle();
      return;
    }
    int i = this.strategy.getSize(paramBitmap);
    this.strategy.put(paramBitmap);
    this.tracker.add(paramBitmap);
    this.puts += 1;
    this.currentSize += i;
    if (Log.isLoggable("LruBitmapPool", 2))
    {
      paramBitmap = String.valueOf(this.strategy.logBitmap(paramBitmap));
      if (paramBitmap.length() == 0) {
        break label297;
      }
    }
    label297:
    for (paramBitmap = "Put bitmap in pool=".concat(paramBitmap);; paramBitmap = new String("Put bitmap in pool="))
    {
      Log.v("LruBitmapPool", paramBitmap);
      dump();
      evict();
      break;
    }
  }
  
  public void setSizeMultiplier(float paramFloat)
  {
    try
    {
      this.maxSize = Math.round(this.initialMaxSize * paramFloat);
      evict();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  @SuppressLint({"InlinedApi"})
  public void trimMemory(int paramInt)
  {
    if (Log.isLoggable("LruBitmapPool", 3)) {
      Log.d("LruBitmapPool", 29 + "trimMemory, level=" + paramInt);
    }
    if (paramInt >= 40) {
      clearMemory();
    }
    while (paramInt < 20) {
      return;
    }
    trimToSize(this.maxSize / 2);
  }
  
  private static abstract interface BitmapTracker
  {
    public abstract void add(Bitmap paramBitmap);
    
    public abstract void remove(Bitmap paramBitmap);
  }
  
  private static class NullBitmapTracker
    implements LruBitmapPool.BitmapTracker
  {
    public void add(Bitmap paramBitmap) {}
    
    public void remove(Bitmap paramBitmap) {}
  }
  
  private static class ThrowingBitmapTracker
    implements LruBitmapPool.BitmapTracker
  {
    private final Set<Bitmap> bitmaps = Collections.synchronizedSet(new HashSet());
    
    public void add(Bitmap paramBitmap)
    {
      if (this.bitmaps.contains(paramBitmap))
      {
        String str = String.valueOf(paramBitmap);
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        throw new IllegalStateException(String.valueOf(str).length() + 58 + "Can't add already added bitmap: " + str + " [" + i + "x" + j + "]");
      }
      this.bitmaps.add(paramBitmap);
    }
    
    public void remove(Bitmap paramBitmap)
    {
      if (!this.bitmaps.contains(paramBitmap)) {
        throw new IllegalStateException("Cannot remove bitmap not in tracker");
      }
      this.bitmaps.remove(paramBitmap);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/LruBitmapPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */