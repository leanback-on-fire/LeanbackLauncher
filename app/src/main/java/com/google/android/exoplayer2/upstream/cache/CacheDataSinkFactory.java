package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSink.Factory;

public final class CacheDataSinkFactory
  implements DataSink.Factory
{
  private final Cache cache;
  private final long maxCacheFileSize;
  
  public CacheDataSinkFactory(Cache paramCache, long paramLong)
  {
    this.cache = paramCache;
    this.maxCacheFileSize = paramLong;
  }
  
  public DataSink createDataSink()
  {
    return new CacheDataSink(this.cache, this.maxCacheFileSize);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/CacheDataSinkFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */