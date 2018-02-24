package com.bumptech.glide.load.engine;

import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemoryCache.ResourceRemovedListener;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class Engine
  implements EngineJobListener, MemoryCache.ResourceRemovedListener, EngineResource.ResourceListener
{
  private static final int JOB_POOL_SIZE = 150;
  private static final String TAG = "Engine";
  private final Map<Key, WeakReference<EngineResource<?>>> activeResources;
  private final MemoryCache cache;
  private final DecodeJobFactory decodeJobFactory;
  private final LazyDiskCacheProvider diskCacheProvider;
  private final EngineJobFactory engineJobFactory;
  private final Map<Key, EngineJob> jobs;
  private final EngineKeyFactory keyFactory;
  private final ResourceRecycler resourceRecycler;
  private ReferenceQueue<EngineResource<?>> resourceReferenceQueue;
  
  public Engine(MemoryCache paramMemoryCache, DiskCache.Factory paramFactory, GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3)
  {
    this(paramMemoryCache, paramFactory, paramGlideExecutor1, paramGlideExecutor2, paramGlideExecutor3, null, null, null, null, null, null);
  }
  
  Engine(MemoryCache paramMemoryCache, DiskCache.Factory paramFactory, GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, Map<Key, EngineJob> paramMap, EngineKeyFactory paramEngineKeyFactory, Map<Key, WeakReference<EngineResource<?>>> paramMap1, EngineJobFactory paramEngineJobFactory, DecodeJobFactory paramDecodeJobFactory, ResourceRecycler paramResourceRecycler)
  {
    this.cache = paramMemoryCache;
    this.diskCacheProvider = new LazyDiskCacheProvider(paramFactory);
    paramFactory = paramMap1;
    if (paramMap1 == null) {
      paramFactory = new HashMap();
    }
    this.activeResources = paramFactory;
    paramFactory = paramEngineKeyFactory;
    if (paramEngineKeyFactory == null) {
      paramFactory = new EngineKeyFactory();
    }
    this.keyFactory = paramFactory;
    paramFactory = paramMap;
    if (paramMap == null) {
      paramFactory = new HashMap();
    }
    this.jobs = paramFactory;
    paramFactory = paramEngineJobFactory;
    if (paramEngineJobFactory == null) {
      paramFactory = new EngineJobFactory(paramGlideExecutor1, paramGlideExecutor2, paramGlideExecutor3, this);
    }
    this.engineJobFactory = paramFactory;
    paramFactory = paramDecodeJobFactory;
    if (paramDecodeJobFactory == null) {
      paramFactory = new DecodeJobFactory(this.diskCacheProvider);
    }
    this.decodeJobFactory = paramFactory;
    paramFactory = paramResourceRecycler;
    if (paramResourceRecycler == null) {
      paramFactory = new ResourceRecycler();
    }
    this.resourceRecycler = paramFactory;
    paramMemoryCache.setResourceRemovedListener(this);
  }
  
  private EngineResource<?> getEngineResourceFromCache(Key paramKey)
  {
    paramKey = this.cache.remove(paramKey);
    if (paramKey == null) {
      return null;
    }
    if ((paramKey instanceof EngineResource)) {
      return (EngineResource)paramKey;
    }
    return new EngineResource(paramKey, true);
  }
  
  private ReferenceQueue<EngineResource<?>> getReferenceQueue()
  {
    if (this.resourceReferenceQueue == null)
    {
      this.resourceReferenceQueue = new ReferenceQueue();
      Looper.myQueue().addIdleHandler(new RefQueueIdleHandler(this.activeResources, this.resourceReferenceQueue));
    }
    return this.resourceReferenceQueue;
  }
  
  private EngineResource<?> loadFromActiveResources(Key paramKey, boolean paramBoolean)
  {
    if (!paramBoolean) {}
    do
    {
      return null;
      localObject = (WeakReference)this.activeResources.get(paramKey);
    } while (localObject == null);
    Object localObject = (EngineResource)((WeakReference)localObject).get();
    if (localObject != null)
    {
      ((EngineResource)localObject).acquire();
      return (EngineResource<?>)localObject;
    }
    this.activeResources.remove(paramKey);
    return (EngineResource<?>)localObject;
  }
  
  private EngineResource<?> loadFromCache(Key paramKey, boolean paramBoolean)
  {
    Object localObject;
    if (!paramBoolean) {
      localObject = null;
    }
    EngineResource localEngineResource;
    do
    {
      return (EngineResource<?>)localObject;
      localEngineResource = getEngineResourceFromCache(paramKey);
      localObject = localEngineResource;
    } while (localEngineResource == null);
    localEngineResource.acquire();
    this.activeResources.put(paramKey, new ResourceWeakReference(paramKey, localEngineResource, getReferenceQueue()));
    return localEngineResource;
  }
  
  private static void logWithTimeAndKey(String paramString, long paramLong, Key paramKey)
  {
    double d = LogTime.getElapsedMillis(paramLong);
    paramKey = String.valueOf(paramKey);
    Log.v("Engine", String.valueOf(paramString).length() + 37 + String.valueOf(paramKey).length() + paramString + " in " + d + "ms, key: " + paramKey);
  }
  
  public void clearDiskCache()
  {
    this.diskCacheProvider.getDiskCache().clear();
  }
  
  public <R> LoadStatus load(GlideContext paramGlideContext, Object paramObject, Key paramKey, int paramInt1, int paramInt2, Class<?> paramClass, Class<R> paramClass1, Priority paramPriority, DiskCacheStrategy paramDiskCacheStrategy, Map<Class<?>, Transformation<?>> paramMap, boolean paramBoolean1, Options paramOptions, boolean paramBoolean2, boolean paramBoolean3, ResourceCallback paramResourceCallback)
  {
    Util.assertMainThread();
    long l = LogTime.getLogTime();
    EngineKey localEngineKey = this.keyFactory.buildKey(paramObject, paramKey, paramInt1, paramInt2, paramMap, paramClass, paramClass1, paramOptions);
    Object localObject = loadFromCache(localEngineKey, paramBoolean2);
    if (localObject != null)
    {
      paramResourceCallback.onResourceReady((Resource)localObject, DataSource.MEMORY_CACHE);
      if (Log.isLoggable("Engine", 2)) {
        logWithTimeAndKey("Loaded resource from cache", l, localEngineKey);
      }
      return null;
    }
    localObject = loadFromActiveResources(localEngineKey, paramBoolean2);
    if (localObject != null)
    {
      paramResourceCallback.onResourceReady((Resource)localObject, DataSource.MEMORY_CACHE);
      if (Log.isLoggable("Engine", 2)) {
        logWithTimeAndKey("Loaded resource from active resources", l, localEngineKey);
      }
      return null;
    }
    localObject = (EngineJob)this.jobs.get(localEngineKey);
    if (localObject != null)
    {
      ((EngineJob)localObject).addCallback(paramResourceCallback);
      if (Log.isLoggable("Engine", 2)) {
        logWithTimeAndKey("Added to existing load", l, localEngineKey);
      }
      return new LoadStatus(paramResourceCallback, (EngineJob)localObject);
    }
    localObject = this.engineJobFactory.build(localEngineKey, paramBoolean2, paramBoolean3);
    paramGlideContext = this.decodeJobFactory.build(paramGlideContext, paramObject, localEngineKey, paramKey, paramInt1, paramInt2, paramClass, paramClass1, paramPriority, paramDiskCacheStrategy, paramMap, paramBoolean1, paramOptions, (DecodeJob.Callback)localObject);
    this.jobs.put(localEngineKey, localObject);
    ((EngineJob)localObject).addCallback(paramResourceCallback);
    ((EngineJob)localObject).start(paramGlideContext);
    if (Log.isLoggable("Engine", 2)) {
      logWithTimeAndKey("Started new load", l, localEngineKey);
    }
    return new LoadStatus(paramResourceCallback, (EngineJob)localObject);
  }
  
  public void onEngineJobCancelled(EngineJob paramEngineJob, Key paramKey)
  {
    
    if (paramEngineJob.equals((EngineJob)this.jobs.get(paramKey))) {
      this.jobs.remove(paramKey);
    }
  }
  
  public void onEngineJobComplete(Key paramKey, EngineResource<?> paramEngineResource)
  {
    
    if (paramEngineResource != null)
    {
      paramEngineResource.setResourceListener(paramKey, this);
      if (paramEngineResource.isCacheable()) {
        this.activeResources.put(paramKey, new ResourceWeakReference(paramKey, paramEngineResource, getReferenceQueue()));
      }
    }
    this.jobs.remove(paramKey);
  }
  
  public void onResourceReleased(Key paramKey, EngineResource paramEngineResource)
  {
    Util.assertMainThread();
    this.activeResources.remove(paramKey);
    if (paramEngineResource.isCacheable())
    {
      this.cache.put(paramKey, paramEngineResource);
      return;
    }
    this.resourceRecycler.recycle(paramEngineResource);
  }
  
  public void onResourceRemoved(Resource<?> paramResource)
  {
    Util.assertMainThread();
    this.resourceRecycler.recycle(paramResource);
  }
  
  public void release(Resource paramResource)
  {
    
    if ((paramResource instanceof EngineResource))
    {
      ((EngineResource)paramResource).release();
      return;
    }
    throw new IllegalArgumentException("Cannot release anything but an EngineResource");
  }
  
  static class DecodeJobFactory
  {
    private int creationOrder;
    private final DecodeJob.DiskCacheProvider diskCacheProvider;
    private final Pools.Pool<DecodeJob<?>> pool = FactoryPools.simple(150, new FactoryPools.Factory()
    {
      public DecodeJob<?> create()
      {
        return new DecodeJob(Engine.DecodeJobFactory.this.diskCacheProvider, Engine.DecodeJobFactory.this.pool);
      }
    });
    
    DecodeJobFactory(DecodeJob.DiskCacheProvider paramDiskCacheProvider)
    {
      this.diskCacheProvider = paramDiskCacheProvider;
    }
    
    <R> DecodeJob<R> build(GlideContext paramGlideContext, Object paramObject, EngineKey paramEngineKey, Key paramKey, int paramInt1, int paramInt2, Class<?> paramClass, Class<R> paramClass1, Priority paramPriority, DiskCacheStrategy paramDiskCacheStrategy, Map<Class<?>, Transformation<?>> paramMap, boolean paramBoolean, Options paramOptions, DecodeJob.Callback<R> paramCallback)
    {
      DecodeJob localDecodeJob = (DecodeJob)this.pool.acquire();
      int i = this.creationOrder;
      this.creationOrder = (i + 1);
      return localDecodeJob.init(paramGlideContext, paramObject, paramEngineKey, paramKey, paramInt1, paramInt2, paramClass, paramClass1, paramPriority, paramDiskCacheStrategy, paramMap, paramBoolean, paramOptions, paramCallback, i);
    }
  }
  
  static class EngineJobFactory
  {
    private final GlideExecutor diskCacheExecutor;
    private final EngineJobListener listener;
    private final Pools.Pool<EngineJob<?>> pool = FactoryPools.simple(150, new FactoryPools.Factory()
    {
      public EngineJob<?> create()
      {
        return new EngineJob(Engine.EngineJobFactory.this.diskCacheExecutor, Engine.EngineJobFactory.this.sourceExecutor, Engine.EngineJobFactory.this.sourceUnlimitedExecutor, Engine.EngineJobFactory.this.listener, Engine.EngineJobFactory.this.pool);
      }
    });
    private final GlideExecutor sourceExecutor;
    private final GlideExecutor sourceUnlimitedExecutor;
    
    EngineJobFactory(GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, EngineJobListener paramEngineJobListener)
    {
      this.diskCacheExecutor = paramGlideExecutor1;
      this.sourceExecutor = paramGlideExecutor2;
      this.sourceUnlimitedExecutor = paramGlideExecutor3;
      this.listener = paramEngineJobListener;
    }
    
    <R> EngineJob<R> build(Key paramKey, boolean paramBoolean1, boolean paramBoolean2)
    {
      return ((EngineJob)this.pool.acquire()).init(paramKey, paramBoolean1, paramBoolean2);
    }
  }
  
  private static class LazyDiskCacheProvider
    implements DecodeJob.DiskCacheProvider
  {
    private volatile DiskCache diskCache;
    private final DiskCache.Factory factory;
    
    public LazyDiskCacheProvider(DiskCache.Factory paramFactory)
    {
      this.factory = paramFactory;
    }
    
    public DiskCache getDiskCache()
    {
      if (this.diskCache == null) {}
      try
      {
        if (this.diskCache == null) {
          this.diskCache = this.factory.build();
        }
        if (this.diskCache == null) {
          this.diskCache = new DiskCacheAdapter();
        }
        return this.diskCache;
      }
      finally {}
    }
  }
  
  public static class LoadStatus
  {
    private final ResourceCallback cb;
    private final EngineJob engineJob;
    
    public LoadStatus(ResourceCallback paramResourceCallback, EngineJob paramEngineJob)
    {
      this.cb = paramResourceCallback;
      this.engineJob = paramEngineJob;
    }
    
    public void cancel()
    {
      this.engineJob.removeCallback(this.cb);
    }
  }
  
  private static class RefQueueIdleHandler
    implements MessageQueue.IdleHandler
  {
    private final Map<Key, WeakReference<EngineResource<?>>> activeResources;
    private final ReferenceQueue<EngineResource<?>> queue;
    
    public RefQueueIdleHandler(Map<Key, WeakReference<EngineResource<?>>> paramMap, ReferenceQueue<EngineResource<?>> paramReferenceQueue)
    {
      this.activeResources = paramMap;
      this.queue = paramReferenceQueue;
    }
    
    public boolean queueIdle()
    {
      Engine.ResourceWeakReference localResourceWeakReference = (Engine.ResourceWeakReference)this.queue.poll();
      if (localResourceWeakReference != null) {
        this.activeResources.remove(Engine.ResourceWeakReference.access$000(localResourceWeakReference));
      }
      return true;
    }
  }
  
  private static class ResourceWeakReference
    extends WeakReference<EngineResource<?>>
  {
    private final Key key;
    
    public ResourceWeakReference(Key paramKey, EngineResource<?> paramEngineResource, ReferenceQueue<? super EngineResource<?>> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      this.key = paramKey;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/Engine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */