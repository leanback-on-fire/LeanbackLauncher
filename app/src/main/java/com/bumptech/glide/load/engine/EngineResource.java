package com.bumptech.glide.load.engine;

import android.os.Looper;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;

class EngineResource<Z>
  implements Resource<Z>
{
  private int acquired;
  private final boolean isCacheable;
  private boolean isRecycled;
  private Key key;
  private ResourceListener listener;
  private final Resource<Z> resource;
  
  EngineResource(Resource<Z> paramResource, boolean paramBoolean)
  {
    this.resource = ((Resource)Preconditions.checkNotNull(paramResource));
    this.isCacheable = paramBoolean;
  }
  
  void acquire()
  {
    if (this.isRecycled) {
      throw new IllegalStateException("Cannot acquire a recycled resource");
    }
    if (!Looper.getMainLooper().equals(Looper.myLooper())) {
      throw new IllegalThreadStateException("Must call acquire on the main thread");
    }
    this.acquired += 1;
  }
  
  public Z get()
  {
    return (Z)this.resource.get();
  }
  
  public Class<Z> getResourceClass()
  {
    return this.resource.getResourceClass();
  }
  
  public int getSize()
  {
    return this.resource.getSize();
  }
  
  boolean isCacheable()
  {
    return this.isCacheable;
  }
  
  public void recycle()
  {
    if (this.acquired > 0) {
      throw new IllegalStateException("Cannot recycle a resource while it is still acquired");
    }
    if (this.isRecycled) {
      throw new IllegalStateException("Cannot recycle a resource that has already been recycled");
    }
    this.isRecycled = true;
    this.resource.recycle();
  }
  
  void release()
  {
    if (this.acquired <= 0) {
      throw new IllegalStateException("Cannot release a recycled or not yet acquired resource");
    }
    if (!Looper.getMainLooper().equals(Looper.myLooper())) {
      throw new IllegalThreadStateException("Must call release on the main thread");
    }
    int i = this.acquired - 1;
    this.acquired = i;
    if (i == 0) {
      this.listener.onResourceReleased(this.key, this);
    }
  }
  
  void setResourceListener(Key paramKey, ResourceListener paramResourceListener)
  {
    this.key = paramKey;
    this.listener = paramResourceListener;
  }
  
  public String toString()
  {
    boolean bool1 = this.isCacheable;
    String str1 = String.valueOf(this.listener);
    String str2 = String.valueOf(this.key);
    int i = this.acquired;
    boolean bool2 = this.isRecycled;
    String str3 = String.valueOf(this.resource);
    return String.valueOf(str1).length() + 101 + String.valueOf(str2).length() + String.valueOf(str3).length() + "EngineResource{isCacheable=" + bool1 + ", listener=" + str1 + ", key=" + str2 + ", acquired=" + i + ", isRecycled=" + bool2 + ", resource=" + str3 + "}";
  }
  
  static abstract interface ResourceListener
  {
    public abstract void onResourceReleased(Key paramKey, EngineResource<?> paramEngineResource);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/EngineResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */