package com.bumptech.glide.provider;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.bumptech.glide.load.engine.LoadPath;
import com.bumptech.glide.util.MultiClassKey;
import java.util.concurrent.atomic.AtomicReference;

public class LoadPathCache
{
  private final ArrayMap<MultiClassKey, LoadPath<?, ?, ?>> cache = new ArrayMap();
  private final AtomicReference<MultiClassKey> keyRef = new AtomicReference();
  
  private MultiClassKey getKey(Class<?> paramClass1, Class<?> paramClass2, Class<?> paramClass3)
  {
    MultiClassKey localMultiClassKey2 = (MultiClassKey)this.keyRef.getAndSet(null);
    MultiClassKey localMultiClassKey1 = localMultiClassKey2;
    if (localMultiClassKey2 == null) {
      localMultiClassKey1 = new MultiClassKey();
    }
    localMultiClassKey1.set(paramClass1, paramClass2, paramClass3);
    return localMultiClassKey1;
  }
  
  public boolean contains(Class<?> arg1, Class<?> paramClass2, Class<?> paramClass3)
  {
    paramClass2 = getKey(???, paramClass2, paramClass3);
    synchronized (this.cache)
    {
      boolean bool = this.cache.containsKey(paramClass2);
      this.keyRef.set(paramClass2);
      return bool;
    }
  }
  
  @Nullable
  public <Data, TResource, Transcode> LoadPath<Data, TResource, Transcode> get(Class<Data> arg1, Class<TResource> paramClass1, Class<Transcode> paramClass2)
  {
    paramClass1 = getKey(???, paramClass1, paramClass2);
    synchronized (this.cache)
    {
      paramClass2 = (LoadPath)this.cache.get(paramClass1);
      this.keyRef.set(paramClass1);
      return paramClass2;
    }
  }
  
  public void put(Class<?> paramClass1, Class<?> paramClass2, Class<?> paramClass3, LoadPath<?, ?, ?> paramLoadPath)
  {
    synchronized (this.cache)
    {
      this.cache.put(new MultiClassKey(paramClass1, paramClass2, paramClass3), paramLoadPath);
      return;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/provider/LoadPathCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */