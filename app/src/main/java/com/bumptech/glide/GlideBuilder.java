package com.bumptech.glide;

import android.content.Context;
import android.os.Build.VERSION;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator.Builder;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.DefaultConnectivityMonitorFactory;
import com.bumptech.glide.request.RequestOptions;

public final class GlideBuilder
{
  private ArrayPool arrayPool;
  private BitmapPool bitmapPool;
  private ConnectivityMonitorFactory connectivityMonitorFactory;
  private final Context context;
  private RequestOptions defaultRequestOptions = new RequestOptions();
  private GlideExecutor diskCacheExecutor;
  private DiskCache.Factory diskCacheFactory;
  private Engine engine;
  private int logLevel = 4;
  private MemoryCache memoryCache;
  private MemorySizeCalculator memorySizeCalculator;
  private GlideExecutor sourceExecutor;
  
  GlideBuilder(Context paramContext)
  {
    this.context = paramContext.getApplicationContext();
  }
  
  Glide createGlide()
  {
    if (this.sourceExecutor == null) {
      this.sourceExecutor = GlideExecutor.newSourceExecutor();
    }
    if (this.diskCacheExecutor == null) {
      this.diskCacheExecutor = GlideExecutor.newDiskCacheExecutor();
    }
    if (this.memorySizeCalculator == null) {
      this.memorySizeCalculator = new MemorySizeCalculator.Builder(this.context).build();
    }
    if (this.connectivityMonitorFactory == null) {
      this.connectivityMonitorFactory = new DefaultConnectivityMonitorFactory();
    }
    if (this.bitmapPool == null) {
      if (Build.VERSION.SDK_INT < 11) {
        break label259;
      }
    }
    label259:
    for (this.bitmapPool = new LruBitmapPool(this.memorySizeCalculator.getBitmapPoolSize());; this.bitmapPool = new BitmapPoolAdapter())
    {
      if (this.arrayPool == null) {
        this.arrayPool = new LruArrayPool(this.memorySizeCalculator.getArrayPoolSizeInBytes());
      }
      if (this.memoryCache == null) {
        this.memoryCache = new LruResourceCache(this.memorySizeCalculator.getMemoryCacheSize());
      }
      if (this.diskCacheFactory == null) {
        this.diskCacheFactory = new InternalCacheDiskCacheFactory(this.context);
      }
      if (this.engine == null) {
        this.engine = new Engine(this.memoryCache, this.diskCacheFactory, this.diskCacheExecutor, this.sourceExecutor, GlideExecutor.newUnlimitedSourceExecutor());
      }
      return new Glide(this.context, this.engine, this.memoryCache, this.bitmapPool, this.arrayPool, this.connectivityMonitorFactory, this.logLevel, (RequestOptions)this.defaultRequestOptions.lock());
    }
  }
  
  public GlideBuilder setArrayPool(ArrayPool paramArrayPool)
  {
    this.arrayPool = paramArrayPool;
    return this;
  }
  
  public GlideBuilder setBitmapPool(BitmapPool paramBitmapPool)
  {
    this.bitmapPool = paramBitmapPool;
    return this;
  }
  
  public GlideBuilder setConnectivityMonitorFactory(ConnectivityMonitorFactory paramConnectivityMonitorFactory)
  {
    this.connectivityMonitorFactory = paramConnectivityMonitorFactory;
    return this;
  }
  
  @Deprecated
  public GlideBuilder setDecodeFormat(DecodeFormat paramDecodeFormat)
  {
    this.defaultRequestOptions.apply(new RequestOptions().format(paramDecodeFormat));
    return this;
  }
  
  public GlideBuilder setDefaultRequestOptions(RequestOptions paramRequestOptions)
  {
    this.defaultRequestOptions = paramRequestOptions;
    return this;
  }
  
  public GlideBuilder setDiskCache(DiskCache.Factory paramFactory)
  {
    this.diskCacheFactory = paramFactory;
    return this;
  }
  
  @Deprecated
  public GlideBuilder setDiskCache(final DiskCache paramDiskCache)
  {
    setDiskCache(new DiskCache.Factory()
    {
      public DiskCache build()
      {
        return paramDiskCache;
      }
    });
  }
  
  public GlideBuilder setDiskCacheExecutor(GlideExecutor paramGlideExecutor)
  {
    this.diskCacheExecutor = paramGlideExecutor;
    return this;
  }
  
  GlideBuilder setEngine(Engine paramEngine)
  {
    this.engine = paramEngine;
    return this;
  }
  
  public GlideBuilder setLogLevel(int paramInt)
  {
    if ((paramInt < 2) || (paramInt > 6)) {
      throw new IllegalArgumentException("Log level must be one of Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, or Log.ERROR");
    }
    this.logLevel = paramInt;
    return this;
  }
  
  public GlideBuilder setMemoryCache(MemoryCache paramMemoryCache)
  {
    this.memoryCache = paramMemoryCache;
    return this;
  }
  
  public GlideBuilder setMemorySizeCalculator(MemorySizeCalculator.Builder paramBuilder)
  {
    return setMemorySizeCalculator(paramBuilder.build());
  }
  
  public GlideBuilder setMemorySizeCalculator(MemorySizeCalculator paramMemorySizeCalculator)
  {
    this.memorySizeCalculator = paramMemorySizeCalculator;
    return this;
  }
  
  public GlideBuilder setResizeExecutor(GlideExecutor paramGlideExecutor)
  {
    this.sourceExecutor = paramGlideExecutor;
    return this;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/GlideBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */