package com.bumptech.glide.load.model;

public abstract interface ModelLoaderFactory<T, Y>
{
  public abstract ModelLoader<T, Y> build(MultiModelLoaderFactory paramMultiModelLoaderFactory);
  
  public abstract void teardown();
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/ModelLoaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */