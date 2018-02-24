package com.bumptech.glide.load.engine.cache;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import com.bumptech.glide.util.Preconditions;

public final class MemorySizeCalculator
{
  static final int BYTES_PER_ARGB_8888_PIXEL = 4;
  static final int LOW_MEMORY_BYTE_ARRAY_POOL_DIVISOR = 2;
  private static final String TAG = "MemorySizeCalculator";
  private final int arrayPoolSize;
  private final int bitmapPoolSize;
  private final Context context;
  private final int memoryCacheSize;
  
  MemorySizeCalculator(Context paramContext, ActivityManager paramActivityManager, ScreenDimensions paramScreenDimensions, float paramFloat1, float paramFloat2, int paramInt, float paramFloat3, float paramFloat4)
  {
    this.context = paramContext;
    int i = paramInt;
    if (isLowMemoryDevice(paramActivityManager)) {
      i = paramInt / 2;
    }
    this.arrayPoolSize = i;
    paramInt = getMaxSize(paramActivityManager, paramFloat3, paramFloat4);
    int j = paramScreenDimensions.getWidthPixels() * paramScreenDimensions.getHeightPixels() * 4;
    i = Math.round(j * paramFloat2);
    j = Math.round(j * paramFloat1);
    int k = paramInt - this.arrayPoolSize;
    String str1;
    if (j + i <= k)
    {
      this.memoryCacheSize = j;
      this.bitmapPoolSize = i;
      if (Log.isLoggable("MemorySizeCalculator", 3))
      {
        paramContext = String.valueOf(toMb(this.memoryCacheSize));
        paramScreenDimensions = String.valueOf(toMb(this.bitmapPoolSize));
        str1 = String.valueOf(toMb(this.arrayPoolSize));
        if (j + i <= paramInt) {
          break label354;
        }
      }
    }
    label354:
    for (boolean bool1 = true;; bool1 = false)
    {
      String str2 = String.valueOf(toMb(paramInt));
      paramInt = paramActivityManager.getMemoryClass();
      boolean bool2 = isLowMemoryDevice(paramActivityManager);
      Log.d("MemorySizeCalculator", String.valueOf(paramContext).length() + 177 + String.valueOf(paramScreenDimensions).length() + String.valueOf(str1).length() + String.valueOf(str2).length() + "Calculation complete, Calculated memory cache size: " + paramContext + ", pool size: " + paramScreenDimensions + ", byte array size: " + str1 + ", memory class limited? " + bool1 + ", max size: " + str2 + ", memoryClass: " + paramInt + ", isLowMemoryDevice: " + bool2);
      return;
      paramFloat3 = k / (paramFloat2 + paramFloat1);
      this.memoryCacheSize = Math.round(paramFloat3 * paramFloat1);
      this.bitmapPoolSize = Math.round(paramFloat3 * paramFloat2);
      break;
    }
  }
  
  private static int getMaxSize(ActivityManager paramActivityManager, float paramFloat1, float paramFloat2)
  {
    int i = paramActivityManager.getMemoryClass();
    boolean bool = isLowMemoryDevice(paramActivityManager);
    float f = i * 1024 * 1024;
    if (bool) {}
    for (;;)
    {
      return Math.round(f * paramFloat2);
      paramFloat2 = paramFloat1;
    }
  }
  
  @TargetApi(19)
  private static boolean isLowMemoryDevice(ActivityManager paramActivityManager)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return paramActivityManager.isLowRamDevice();
    }
    return Build.VERSION.SDK_INT < 11;
  }
  
  private String toMb(int paramInt)
  {
    return Formatter.formatFileSize(this.context, paramInt);
  }
  
  public int getArrayPoolSizeInBytes()
  {
    return this.arrayPoolSize;
  }
  
  public int getBitmapPoolSize()
  {
    return this.bitmapPoolSize;
  }
  
  public int getMemoryCacheSize()
  {
    return this.memoryCacheSize;
  }
  
  public static final class Builder
  {
    static final int ARRAY_POOL_SIZE_BYTES = 4194304;
    static final int BITMAP_POOL_TARGET_SCREENS = 4;
    static final float LOW_MEMORY_MAX_SIZE_MULTIPLIER = 0.33F;
    static final float MAX_SIZE_MULTIPLIER = 0.4F;
    static final int MEMORY_CACHE_TARGET_SCREENS = 2;
    private ActivityManager activityManager;
    private int arrayPoolSizeBytes = 4194304;
    private float bitmapPoolScreens = 4.0F;
    private final Context context;
    private float lowMemoryMaxSizeMultiplier = 0.33F;
    private float maxSizeMultiplier = 0.4F;
    private float memoryCacheScreens = 2.0F;
    private MemorySizeCalculator.ScreenDimensions screenDimensions;
    
    public Builder(Context paramContext)
    {
      this.context = paramContext;
      this.activityManager = ((ActivityManager)paramContext.getSystemService("activity"));
      this.screenDimensions = new MemorySizeCalculator.DisplayMetricsScreenDimensions(paramContext.getResources().getDisplayMetrics());
    }
    
    public MemorySizeCalculator build()
    {
      return new MemorySizeCalculator(this.context, this.activityManager, this.screenDimensions, this.memoryCacheScreens, this.bitmapPoolScreens, this.arrayPoolSizeBytes, this.maxSizeMultiplier, this.lowMemoryMaxSizeMultiplier);
    }
    
    Builder setActivityManager(ActivityManager paramActivityManager)
    {
      this.activityManager = paramActivityManager;
      return this;
    }
    
    public Builder setArrayPoolSize(int paramInt)
    {
      this.arrayPoolSizeBytes = paramInt;
      return this;
    }
    
    public Builder setBitmapPoolScreens(float paramFloat)
    {
      if (paramFloat >= 0.0F) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkArgument(bool, "Bitmap pool screens must be greater than or equal to 0");
        this.bitmapPoolScreens = paramFloat;
        return this;
      }
    }
    
    public Builder setLowMemoryMaxSizeMultiplier(float paramFloat)
    {
      if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkArgument(bool, "Low memory max size multiplier must be between 0 and 1");
        this.lowMemoryMaxSizeMultiplier = paramFloat;
        return this;
      }
    }
    
    public Builder setMaxSizeMultiplier(float paramFloat)
    {
      if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkArgument(bool, "Size multiplier must be between 0 and 1");
        this.maxSizeMultiplier = paramFloat;
        return this;
      }
    }
    
    public Builder setMemoryCacheScreens(float paramFloat)
    {
      if (this.bitmapPoolScreens >= 0.0F) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkArgument(bool, "Memory cache screens must be greater than or equal to 0");
        this.memoryCacheScreens = paramFloat;
        return this;
      }
    }
    
    Builder setScreenDimensions(MemorySizeCalculator.ScreenDimensions paramScreenDimensions)
    {
      this.screenDimensions = paramScreenDimensions;
      return this;
    }
  }
  
  private static final class DisplayMetricsScreenDimensions
    implements MemorySizeCalculator.ScreenDimensions
  {
    private final DisplayMetrics displayMetrics;
    
    public DisplayMetricsScreenDimensions(DisplayMetrics paramDisplayMetrics)
    {
      this.displayMetrics = paramDisplayMetrics;
    }
    
    public int getHeightPixels()
    {
      return this.displayMetrics.heightPixels;
    }
    
    public int getWidthPixels()
    {
      return this.displayMetrics.widthPixels;
    }
  }
  
  static abstract interface ScreenDimensions
  {
    public abstract int getHeightPixels();
    
    public abstract int getWidthPixels();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/cache/MemorySizeCalculator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */