package com.bumptech.glide.load.engine;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class EngineJob<R>
  implements DecodeJob.Callback<R>, FactoryPools.Poolable
{
  private static final EngineResourceFactory DEFAULT_FACTORY = new EngineResourceFactory();
  private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper(), new MainThreadCallback(null));
  private static final int MSG_CANCELLED = 3;
  private static final int MSG_COMPLETE = 1;
  private static final int MSG_EXCEPTION = 2;
  private final List<ResourceCallback> cbs = new ArrayList(2);
  private DataSource dataSource;
  private DecodeJob<R> decodeJob;
  private final GlideExecutor diskCacheExecutor;
  private EngineResource<?> engineResource;
  private final EngineResourceFactory engineResourceFactory;
  private GlideException exception;
  private boolean hasLoadFailed;
  private boolean hasResource;
  private List<ResourceCallback> ignoredCallbacks;
  private boolean isCacheable;
  private volatile boolean isCancelled;
  private Key key;
  private final EngineJobListener listener;
  private final Pools.Pool<EngineJob<?>> pool;
  private Resource<?> resource;
  private final GlideExecutor sourceExecutor;
  private final GlideExecutor sourceUnlimitedExecutor;
  private final StateVerifier stateVerifier = StateVerifier.newInstance();
  private boolean useUnlimitedSourceGeneratorPool;
  
  EngineJob(GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, EngineJobListener paramEngineJobListener, Pools.Pool<EngineJob<?>> paramPool)
  {
    this(paramGlideExecutor1, paramGlideExecutor2, paramGlideExecutor3, paramEngineJobListener, paramPool, DEFAULT_FACTORY);
  }
  
  EngineJob(GlideExecutor paramGlideExecutor1, GlideExecutor paramGlideExecutor2, GlideExecutor paramGlideExecutor3, EngineJobListener paramEngineJobListener, Pools.Pool<EngineJob<?>> paramPool, EngineResourceFactory paramEngineResourceFactory)
  {
    this.diskCacheExecutor = paramGlideExecutor1;
    this.sourceExecutor = paramGlideExecutor2;
    this.sourceUnlimitedExecutor = paramGlideExecutor3;
    this.listener = paramEngineJobListener;
    this.pool = paramPool;
    this.engineResourceFactory = paramEngineResourceFactory;
  }
  
  private void addIgnoredCallback(ResourceCallback paramResourceCallback)
  {
    if (this.ignoredCallbacks == null) {
      this.ignoredCallbacks = new ArrayList(2);
    }
    if (!this.ignoredCallbacks.contains(paramResourceCallback)) {
      this.ignoredCallbacks.add(paramResourceCallback);
    }
  }
  
  private GlideExecutor getActiveSourceExecutor()
  {
    if (this.useUnlimitedSourceGeneratorPool) {
      return this.sourceUnlimitedExecutor;
    }
    return this.sourceExecutor;
  }
  
  private void handleCancelledOnMainThread()
  {
    this.stateVerifier.throwIfRecycled();
    if (!this.isCancelled) {
      throw new IllegalStateException("Not cancelled");
    }
    this.listener.onEngineJobCancelled(this, this.key);
    release(false);
  }
  
  private void handleExceptionOnMainThread()
  {
    this.stateVerifier.throwIfRecycled();
    if (this.isCancelled)
    {
      release(false);
      return;
    }
    if (this.cbs.isEmpty()) {
      throw new IllegalStateException("Received an exception without any callbacks to notify");
    }
    if (this.hasLoadFailed) {
      throw new IllegalStateException("Already failed once");
    }
    this.hasLoadFailed = true;
    this.listener.onEngineJobComplete(this.key, null);
    Iterator localIterator = this.cbs.iterator();
    while (localIterator.hasNext())
    {
      ResourceCallback localResourceCallback = (ResourceCallback)localIterator.next();
      if (!isInIgnoredCallbacks(localResourceCallback)) {
        localResourceCallback.onLoadFailed(this.exception);
      }
    }
    release(false);
  }
  
  private void handleResultOnMainThread()
  {
    this.stateVerifier.throwIfRecycled();
    if (this.isCancelled)
    {
      this.resource.recycle();
      release(false);
      return;
    }
    if (this.cbs.isEmpty()) {
      throw new IllegalStateException("Received a resource without any callbacks to notify");
    }
    if (this.hasResource) {
      throw new IllegalStateException("Already have resource");
    }
    this.engineResource = this.engineResourceFactory.build(this.resource, this.isCacheable);
    this.hasResource = true;
    this.engineResource.acquire();
    this.listener.onEngineJobComplete(this.key, this.engineResource);
    Iterator localIterator = this.cbs.iterator();
    while (localIterator.hasNext())
    {
      ResourceCallback localResourceCallback = (ResourceCallback)localIterator.next();
      if (!isInIgnoredCallbacks(localResourceCallback))
      {
        this.engineResource.acquire();
        localResourceCallback.onResourceReady(this.engineResource, this.dataSource);
      }
    }
    this.engineResource.release();
    release(false);
  }
  
  private boolean isInIgnoredCallbacks(ResourceCallback paramResourceCallback)
  {
    return (this.ignoredCallbacks != null) && (this.ignoredCallbacks.contains(paramResourceCallback));
  }
  
  private void release(boolean paramBoolean)
  {
    Util.assertMainThread();
    this.cbs.clear();
    this.key = null;
    this.engineResource = null;
    this.resource = null;
    if (this.ignoredCallbacks != null) {
      this.ignoredCallbacks.clear();
    }
    this.hasLoadFailed = false;
    this.isCancelled = false;
    this.hasResource = false;
    this.decodeJob.release(paramBoolean);
    this.decodeJob = null;
    this.exception = null;
    this.dataSource = null;
    this.pool.release(this);
  }
  
  public void addCallback(ResourceCallback paramResourceCallback)
  {
    Util.assertMainThread();
    this.stateVerifier.throwIfRecycled();
    if (this.hasResource)
    {
      paramResourceCallback.onResourceReady(this.engineResource, this.dataSource);
      return;
    }
    if (this.hasLoadFailed)
    {
      paramResourceCallback.onLoadFailed(this.exception);
      return;
    }
    this.cbs.add(paramResourceCallback);
  }
  
  void cancel()
  {
    if ((this.hasLoadFailed) || (this.hasResource) || (this.isCancelled)) {
      return;
    }
    this.isCancelled = true;
    this.decodeJob.cancel();
    if ((this.diskCacheExecutor.remove(this.decodeJob)) || (this.sourceExecutor.remove(this.decodeJob)) || (this.sourceUnlimitedExecutor.remove(this.decodeJob))) {}
    for (int i = 1;; i = 0)
    {
      this.listener.onEngineJobCancelled(this, this.key);
      if (i == 0) {
        break;
      }
      release(true);
      return;
    }
  }
  
  public StateVerifier getVerifier()
  {
    return this.stateVerifier;
  }
  
  EngineJob<R> init(Key paramKey, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.key = paramKey;
    this.isCacheable = paramBoolean1;
    this.useUnlimitedSourceGeneratorPool = paramBoolean2;
    return this;
  }
  
  boolean isCancelled()
  {
    return this.isCancelled;
  }
  
  public void onLoadFailed(GlideException paramGlideException)
  {
    this.exception = paramGlideException;
    MAIN_THREAD_HANDLER.obtainMessage(2, this).sendToTarget();
  }
  
  public void onResourceReady(Resource<R> paramResource, DataSource paramDataSource)
  {
    this.resource = paramResource;
    this.dataSource = paramDataSource;
    MAIN_THREAD_HANDLER.obtainMessage(1, this).sendToTarget();
  }
  
  public void removeCallback(ResourceCallback paramResourceCallback)
  {
    Util.assertMainThread();
    this.stateVerifier.throwIfRecycled();
    if ((this.hasResource) || (this.hasLoadFailed)) {
      addIgnoredCallback(paramResourceCallback);
    }
    do
    {
      return;
      this.cbs.remove(paramResourceCallback);
    } while (!this.cbs.isEmpty());
    cancel();
  }
  
  public void reschedule(DecodeJob<?> paramDecodeJob)
  {
    if (this.isCancelled)
    {
      MAIN_THREAD_HANDLER.obtainMessage(3, this).sendToTarget();
      return;
    }
    getActiveSourceExecutor().execute(paramDecodeJob);
  }
  
  public void start(DecodeJob<R> paramDecodeJob)
  {
    this.decodeJob = paramDecodeJob;
    if (paramDecodeJob.willDecodeFromCache()) {}
    for (GlideExecutor localGlideExecutor = this.diskCacheExecutor;; localGlideExecutor = getActiveSourceExecutor())
    {
      localGlideExecutor.execute(paramDecodeJob);
      return;
    }
  }
  
  static class EngineResourceFactory
  {
    public <R> EngineResource<R> build(Resource<R> paramResource, boolean paramBoolean)
    {
      return new EngineResource(paramResource, paramBoolean);
    }
  }
  
  private static class MainThreadCallback
    implements Handler.Callback
  {
    public boolean handleMessage(Message paramMessage)
    {
      EngineJob localEngineJob = (EngineJob)paramMessage.obj;
      switch (paramMessage.what)
      {
      default: 
        int i = paramMessage.what;
        throw new IllegalStateException(33 + "Unrecognized message: " + i);
      case 1: 
        localEngineJob.handleResultOnMainThread();
      }
      for (;;)
      {
        return true;
        localEngineJob.handleExceptionOnMainThread();
        continue;
        localEngineJob.handleCancelledOnMainThread();
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/EngineJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */