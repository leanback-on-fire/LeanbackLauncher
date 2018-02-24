package com.bumptech.glide.load;

import android.support.annotation.Nullable;
import com.bumptech.glide.load.engine.Resource;
import java.io.IOException;

public abstract interface ResourceDecoder<T, Z>
{
  @Nullable
  public abstract Resource<Z> decode(T paramT, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException;
  
  public abstract boolean handles(T paramT, Options paramOptions)
    throws IOException;
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/ResourceDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */