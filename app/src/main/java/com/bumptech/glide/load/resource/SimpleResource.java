package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;

public class SimpleResource<T>
  implements Resource<T>
{
  protected final T data;
  
  public SimpleResource(T paramT)
  {
    this.data = Preconditions.checkNotNull(paramT);
  }
  
  public final T get()
  {
    return (T)this.data;
  }
  
  public Class<T> getResourceClass()
  {
    return this.data.getClass();
  }
  
  public final int getSize()
  {
    return 1;
  }
  
  public void recycle() {}
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/SimpleResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */