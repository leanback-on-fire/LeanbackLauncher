package com.bumptech.glide.load.engine;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;

final class LockedResource<Z>
  implements Resource<Z>, FactoryPools.Poolable
{
  private static final Pools.Pool<LockedResource<?>> POOL = FactoryPools.threadSafe(20, new FactoryPools.Factory()
  {
    public LockedResource<?> create()
    {
      return new LockedResource(null);
    }
  });
  private boolean isLocked;
  private boolean isRecycled;
  private final StateVerifier stateVerifier = StateVerifier.newInstance();
  private Resource<Z> toWrap;
  
  private void init(Resource<Z> paramResource)
  {
    this.isRecycled = false;
    this.isLocked = true;
    this.toWrap = paramResource;
  }
  
  static <Z> LockedResource<Z> obtain(Resource<Z> paramResource)
  {
    LockedResource localLockedResource = (LockedResource)POOL.acquire();
    localLockedResource.init(paramResource);
    return localLockedResource;
  }
  
  private void release()
  {
    this.toWrap = null;
    POOL.release(this);
  }
  
  public Z get()
  {
    return (Z)this.toWrap.get();
  }
  
  public Class<Z> getResourceClass()
  {
    return this.toWrap.getResourceClass();
  }
  
  public int getSize()
  {
    return this.toWrap.getSize();
  }
  
  public StateVerifier getVerifier()
  {
    return this.stateVerifier;
  }
  
  public void recycle()
  {
    try
    {
      this.stateVerifier.throwIfRecycled();
      this.isRecycled = true;
      if (!this.isLocked)
      {
        this.toWrap.recycle();
        release();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void unlock()
  {
    try
    {
      this.stateVerifier.throwIfRecycled();
      if (!this.isLocked) {
        throw new IllegalStateException("Already unlocked");
      }
    }
    finally {}
    this.isLocked = false;
    if (this.isRecycled) {
      recycle();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/LockedResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */