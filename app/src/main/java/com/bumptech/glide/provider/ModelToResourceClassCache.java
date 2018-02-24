package com.bumptech.glide.provider;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.bumptech.glide.util.MultiClassKey;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ModelToResourceClassCache
{
  private final ArrayMap<MultiClassKey, List<Class<?>>> registeredResourceClassCache = new ArrayMap();
  private final AtomicReference<MultiClassKey> resourceClassKeyRef = new AtomicReference();
  
  public void clear()
  {
    synchronized (this.registeredResourceClassCache)
    {
      this.registeredResourceClassCache.clear();
      return;
    }
  }
  
  @Nullable
  public List<Class<?>> get(Class<?> paramClass1, Class<?> arg2)
  {
    Object localObject = (MultiClassKey)this.resourceClassKeyRef.getAndSet(null);
    if (localObject == null) {
      paramClass1 = new MultiClassKey(paramClass1, ???);
    }
    synchronized (this.registeredResourceClassCache)
    {
      localObject = (List)this.registeredResourceClassCache.get(paramClass1);
      this.resourceClassKeyRef.set(paramClass1);
      return (List<Class<?>>)localObject;
      ((MultiClassKey)localObject).set(paramClass1, ???);
      paramClass1 = (Class<?>)localObject;
    }
  }
  
  public void put(Class<?> paramClass1, Class<?> paramClass2, List<Class<?>> paramList)
  {
    synchronized (this.registeredResourceClassCache)
    {
      this.registeredResourceClassCache.put(new MultiClassKey(paramClass1, paramClass2), paramList);
      return;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/provider/ModelToResourceClassCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */