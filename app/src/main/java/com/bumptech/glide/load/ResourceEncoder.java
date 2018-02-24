package com.bumptech.glide.load;

import com.bumptech.glide.load.engine.Resource;

public abstract interface ResourceEncoder<T>
  extends Encoder<Resource<T>>
{
  public abstract EncodeStrategy getEncodeStrategy(Options paramOptions);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/ResourceEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */