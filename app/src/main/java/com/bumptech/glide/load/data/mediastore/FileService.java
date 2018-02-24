package com.bumptech.glide.load.data.mediastore;

import java.io.File;

class FileService
{
  public boolean exists(File paramFile)
  {
    return paramFile.exists();
  }
  
  public File get(String paramString)
  {
    return new File(paramString);
  }
  
  public long length(File paramFile)
  {
    return paramFile.length();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/mediastore/FileService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */