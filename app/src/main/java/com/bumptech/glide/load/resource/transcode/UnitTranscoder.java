package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.load.engine.Resource;

public class UnitTranscoder<Z>
  implements ResourceTranscoder<Z, Z>
{
  private static final UnitTranscoder<?> UNIT_TRANSCODER = new UnitTranscoder();
  
  public static <Z> ResourceTranscoder<Z, Z> get()
  {
    return UNIT_TRANSCODER;
  }
  
  public Resource<Z> transcode(Resource<Z> paramResource)
  {
    return paramResource;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/transcode/UnitTranscoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */