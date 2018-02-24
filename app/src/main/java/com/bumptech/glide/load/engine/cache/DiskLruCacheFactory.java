package com.bumptech.glide.load.engine.cache;

import java.io.File;

public class DiskLruCacheFactory
  implements DiskCache.Factory
{
  private final CacheDirectoryGetter cacheDirectoryGetter;
  private final int diskCacheSize;
  
  public DiskLruCacheFactory(CacheDirectoryGetter paramCacheDirectoryGetter, int paramInt)
  {
    this.diskCacheSize = paramInt;
    this.cacheDirectoryGetter = paramCacheDirectoryGetter;
  }
  
  public DiskLruCacheFactory(String paramString, int paramInt)
  {
    this(new CacheDirectoryGetter()
    {
      public File getCacheDirectory()
      {
        return new File(DiskLruCacheFactory.this);
      }
    }, paramInt);
  }
  
  public DiskLruCacheFactory(String paramString1, final String paramString2, int paramInt)
  {
    this(new CacheDirectoryGetter()
    {
      public File getCacheDirectory()
      {
        return new File(DiskLruCacheFactory.this, paramString2);
      }
    }, paramInt);
  }
  
  public DiskCache build()
  {
    File localFile = this.cacheDirectoryGetter.getCacheDirectory();
    if (localFile == null) {}
    while ((!localFile.mkdirs()) && ((!localFile.exists()) || (!localFile.isDirectory()))) {
      return null;
    }
    return DiskLruCacheWrapper.get(localFile, this.diskCacheSize);
  }
  
  public static abstract interface CacheDirectoryGetter
  {
    public abstract File getCacheDirectory();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/cache/DiskLruCacheFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */