package com.bumptech.glide.load;

import com.bumptech.glide.load.engine.Resource;

public abstract interface Transformation<T>
  extends Key
{
  public abstract Resource<T> transform(Resource<T> paramResource, int paramInt1, int paramInt2);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/Transformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */