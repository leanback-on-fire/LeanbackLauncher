package com.bumptech.glide.util.pool;

import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class FactoryPools
{
  private static final int DEFAULT_POOL_SIZE = 20;
  private static final Resetter<Object> EMPTY_RESETTER = new Resetter()
  {
    public void reset(Object paramAnonymousObject) {}
  };
  private static final String TAG = "FactoryPools";
  
  private static <T extends Poolable> Pools.Pool<T> build(Pools.Pool<T> paramPool, Factory<T> paramFactory)
  {
    return build(paramPool, paramFactory, emptyResetter());
  }
  
  private static <T> Pools.Pool<T> build(Pools.Pool<T> paramPool, Factory<T> paramFactory, Resetter<T> paramResetter)
  {
    return new FactoryPool(paramPool, paramFactory, paramResetter);
  }
  
  private static <T> Resetter<T> emptyResetter()
  {
    return EMPTY_RESETTER;
  }
  
  public static <T extends Poolable> Pools.Pool<T> simple(int paramInt, Factory<T> paramFactory)
  {
    return build(new Pools.SimplePool(paramInt), paramFactory);
  }
  
  public static <T extends Poolable> Pools.Pool<T> threadSafe(int paramInt, Factory<T> paramFactory)
  {
    return build(new Pools.SynchronizedPool(paramInt), paramFactory);
  }
  
  public static <T> Pools.Pool<List<T>> threadSafeList()
  {
    return threadSafeList(20);
  }
  
  public static <T> Pools.Pool<List<T>> threadSafeList(int paramInt)
  {
    build(new Pools.SynchronizedPool(paramInt), new Factory()new Resetter
    {
      public List<T> create()
      {
        return new ArrayList();
      }
    }, new Resetter()
    {
      public void reset(List<T> paramAnonymousList)
      {
        paramAnonymousList.clear();
      }
    });
  }
  
  public static abstract interface Factory<T>
  {
    public abstract T create();
  }
  
  private static final class FactoryPool<T>
    implements Pools.Pool<T>
  {
    private final FactoryPools.Factory<T> factory;
    private final Pools.Pool<T> pool;
    private final FactoryPools.Resetter<T> resetter;
    
    FactoryPool(Pools.Pool<T> paramPool, FactoryPools.Factory<T> paramFactory, FactoryPools.Resetter<T> paramResetter)
    {
      this.pool = paramPool;
      this.factory = paramFactory;
      this.resetter = paramResetter;
    }
    
    public T acquire()
    {
      Object localObject2 = this.pool.acquire();
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = this.factory.create();
        localObject1 = localObject2;
        if (Log.isLoggable("FactoryPools", 2))
        {
          localObject1 = String.valueOf(localObject2.getClass());
          Log.v("FactoryPools", String.valueOf(localObject1).length() + 12 + "Created new " + (String)localObject1);
          localObject1 = localObject2;
        }
      }
      if ((localObject1 instanceof FactoryPools.Poolable)) {
        ((FactoryPools.Poolable)localObject1).getVerifier().setRecycled(false);
      }
      return (T)localObject1;
    }
    
    public boolean release(T paramT)
    {
      if ((paramT instanceof FactoryPools.Poolable)) {
        ((FactoryPools.Poolable)paramT).getVerifier().setRecycled(true);
      }
      this.resetter.reset(paramT);
      return this.pool.release(paramT);
    }
  }
  
  public static abstract interface Poolable
  {
    public abstract StateVerifier getVerifier();
  }
  
  public static abstract interface Resetter<T>
  {
    public abstract void reset(T paramT);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/util/pool/FactoryPools.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */