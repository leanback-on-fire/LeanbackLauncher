package com.bumptech.glide.load.model;

import java.util.Collections;
import java.util.Map;

public abstract interface Headers
{
  public static final Headers DEFAULT = new LazyHeaders.Builder().build();
  @Deprecated
  public static final Headers NONE = new Headers()
  {
    public Map<String, String> getHeaders()
    {
      return Collections.emptyMap();
    }
  };
  
  public abstract Map<String, String> getHeaders();
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/Headers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */