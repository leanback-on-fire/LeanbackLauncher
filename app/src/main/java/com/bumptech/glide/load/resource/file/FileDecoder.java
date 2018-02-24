package com.bumptech.glide.load.resource.file;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import java.io.File;

public class FileDecoder
  implements ResourceDecoder<File, File>
{
  public Resource<File> decode(File paramFile, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new FileResource(paramFile);
  }
  
  public boolean handles(File paramFile, Options paramOptions)
  {
    return true;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/file/FileDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */