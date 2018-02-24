package com.bumptech.glide.load.engine.bitmap_recycle;

public abstract interface ArrayAdapterInterface<T>
{
  public abstract int getArrayLength(T paramT);
  
  public abstract int getElementSizeInBytes();
  
  public abstract String getTag();
  
  public abstract T newArray(int paramInt);
  
  public abstract void resetArray(T paramT);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/ArrayAdapterInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */