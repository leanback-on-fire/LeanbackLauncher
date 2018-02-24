package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;

public abstract class DiskCacheStrategy
{
  public static final DiskCacheStrategy ALL = new DiskCacheStrategy()
  {
    public boolean decodeCachedData()
    {
      return true;
    }
    
    public boolean decodeCachedResource()
    {
      return true;
    }
    
    public boolean isDataCacheable(DataSource paramAnonymousDataSource)
    {
      return paramAnonymousDataSource == DataSource.REMOTE;
    }
    
    public boolean isResourceCacheable(boolean paramAnonymousBoolean, DataSource paramAnonymousDataSource, EncodeStrategy paramAnonymousEncodeStrategy)
    {
      return (paramAnonymousDataSource != DataSource.RESOURCE_DISK_CACHE) && (paramAnonymousDataSource != DataSource.MEMORY_CACHE);
    }
  };
  public static final DiskCacheStrategy AUTOMATIC = new DiskCacheStrategy()
  {
    public boolean decodeCachedData()
    {
      return true;
    }
    
    public boolean decodeCachedResource()
    {
      return true;
    }
    
    public boolean isDataCacheable(DataSource paramAnonymousDataSource)
    {
      return paramAnonymousDataSource == DataSource.REMOTE;
    }
    
    public boolean isResourceCacheable(boolean paramAnonymousBoolean, DataSource paramAnonymousDataSource, EncodeStrategy paramAnonymousEncodeStrategy)
    {
      return ((paramAnonymousBoolean) && (paramAnonymousDataSource == DataSource.DATA_DISK_CACHE)) || ((paramAnonymousDataSource == DataSource.LOCAL) && (paramAnonymousEncodeStrategy == EncodeStrategy.TRANSFORMED));
    }
  };
  public static final DiskCacheStrategy DATA;
  public static final DiskCacheStrategy NONE = new DiskCacheStrategy()
  {
    public boolean decodeCachedData()
    {
      return false;
    }
    
    public boolean decodeCachedResource()
    {
      return false;
    }
    
    public boolean isDataCacheable(DataSource paramAnonymousDataSource)
    {
      return false;
    }
    
    public boolean isResourceCacheable(boolean paramAnonymousBoolean, DataSource paramAnonymousDataSource, EncodeStrategy paramAnonymousEncodeStrategy)
    {
      return false;
    }
  };
  public static final DiskCacheStrategy RESOURCE;
  
  static
  {
    DATA = new DiskCacheStrategy()
    {
      public boolean decodeCachedData()
      {
        return true;
      }
      
      public boolean decodeCachedResource()
      {
        return false;
      }
      
      public boolean isDataCacheable(DataSource paramAnonymousDataSource)
      {
        return (paramAnonymousDataSource != DataSource.DATA_DISK_CACHE) && (paramAnonymousDataSource != DataSource.MEMORY_CACHE);
      }
      
      public boolean isResourceCacheable(boolean paramAnonymousBoolean, DataSource paramAnonymousDataSource, EncodeStrategy paramAnonymousEncodeStrategy)
      {
        return false;
      }
    };
    RESOURCE = new DiskCacheStrategy()
    {
      public boolean decodeCachedData()
      {
        return false;
      }
      
      public boolean decodeCachedResource()
      {
        return true;
      }
      
      public boolean isDataCacheable(DataSource paramAnonymousDataSource)
      {
        return false;
      }
      
      public boolean isResourceCacheable(boolean paramAnonymousBoolean, DataSource paramAnonymousDataSource, EncodeStrategy paramAnonymousEncodeStrategy)
      {
        return (paramAnonymousDataSource != DataSource.RESOURCE_DISK_CACHE) && (paramAnonymousDataSource != DataSource.MEMORY_CACHE);
      }
    };
  }
  
  public abstract boolean decodeCachedData();
  
  public abstract boolean decodeCachedResource();
  
  public abstract boolean isDataCacheable(DataSource paramDataSource);
  
  public abstract boolean isResourceCacheable(boolean paramBoolean, DataSource paramDataSource, EncodeStrategy paramEncodeStrategy);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/DiskCacheStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */