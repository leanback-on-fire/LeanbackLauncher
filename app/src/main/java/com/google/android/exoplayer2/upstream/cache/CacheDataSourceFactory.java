package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink.Factory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;

public final class CacheDataSourceFactory
  implements DataSource.Factory
{
  private final Cache cache;
  private final DataSource.Factory cacheReadDataSourceFactory;
  private final DataSink.Factory cacheWriteDataSinkFactory;
  private final CacheDataSource.EventListener eventListener;
  private final int flags;
  private final DataSource.Factory upstreamFactory;
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt)
  {
    this(paramCache, paramFactory, paramInt, 2097152L);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt, long paramLong)
  {
    this(paramCache, paramFactory, new FileDataSourceFactory(), new CacheDataSinkFactory(paramCache, paramLong), paramInt, null);
  }
  
  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory1, DataSource.Factory paramFactory2, DataSink.Factory paramFactory, int paramInt, CacheDataSource.EventListener paramEventListener)
  {
    this.cache = paramCache;
    this.upstreamFactory = paramFactory1;
    this.cacheReadDataSourceFactory = paramFactory2;
    this.cacheWriteDataSinkFactory = paramFactory;
    this.flags = paramInt;
    this.eventListener = paramEventListener;
  }
  
  public DataSource createDataSource()
  {
    return new CacheDataSource(this.cache, this.upstreamFactory.createDataSource(), this.cacheReadDataSourceFactory.createDataSource(), this.cacheWriteDataSinkFactory.createDataSink(), this.flags, this.eventListener);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/CacheDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */