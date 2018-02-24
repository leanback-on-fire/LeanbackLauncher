package com.bumptech.glide.load.engine;

import android.util.Log;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCache.Writer;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.util.LogTime;
import java.util.Collections;
import java.util.List;

class SourceGenerator
  implements DataFetcherGenerator, DataFetcher.DataCallback<Object>, DataFetcherGenerator.FetcherReadyCallback
{
  private static final String TAG = "SourceGenerator";
  private final DataFetcherGenerator.FetcherReadyCallback cb;
  private Object dataToCache;
  private final DecodeHelper<?> helper;
  private volatile ModelLoader.LoadData<?> loadData;
  private int loadDataListIndex;
  private DataCacheKey originalKey;
  private DataCacheGenerator sourceCacheGenerator;
  
  public SourceGenerator(DecodeHelper<?> paramDecodeHelper, DataFetcherGenerator.FetcherReadyCallback paramFetcherReadyCallback)
  {
    this.helper = paramDecodeHelper;
    this.cb = paramFetcherReadyCallback;
  }
  
  private void cacheData(Object paramObject)
  {
    long l = LogTime.getLogTime();
    try
    {
      Object localObject1 = this.helper.getSourceEncoder(paramObject);
      Object localObject2 = new DataCacheWriter((Encoder)localObject1, paramObject, this.helper.getOptions());
      this.originalKey = new DataCacheKey(this.loadData.sourceKey, this.helper.getSignature());
      this.helper.getDiskCache().put(this.originalKey, (DiskCache.Writer)localObject2);
      if (Log.isLoggable("SourceGenerator", 2))
      {
        localObject2 = String.valueOf(this.originalKey);
        paramObject = String.valueOf(paramObject);
        localObject1 = String.valueOf(localObject1);
        double d = LogTime.getElapsedMillis(l);
        Log.v("SourceGenerator", String.valueOf(localObject2).length() + 95 + String.valueOf(paramObject).length() + String.valueOf(localObject1).length() + "Finished encoding source to cache, key: " + (String)localObject2 + ", data: " + (String)paramObject + ", encoder: " + (String)localObject1 + ", duration: " + d);
      }
      this.loadData.fetcher.cleanup();
      this.sourceCacheGenerator = new DataCacheGenerator(Collections.singletonList(this.loadData.sourceKey), this.helper, this);
      return;
    }
    finally
    {
      this.loadData.fetcher.cleanup();
    }
  }
  
  private boolean hasNextModelLoader()
  {
    return this.loadDataListIndex < this.helper.getLoadData().size();
  }
  
  public void cancel()
  {
    ModelLoader.LoadData localLoadData = this.loadData;
    if (localLoadData != null) {
      localLoadData.fetcher.cancel();
    }
  }
  
  public void onDataFetcherFailed(Key paramKey, Exception paramException, DataFetcher<?> paramDataFetcher, DataSource paramDataSource)
  {
    this.cb.onDataFetcherFailed(paramKey, paramException, paramDataFetcher, this.loadData.fetcher.getDataSource());
  }
  
  public void onDataFetcherReady(Key paramKey1, Object paramObject, DataFetcher<?> paramDataFetcher, DataSource paramDataSource, Key paramKey2)
  {
    this.cb.onDataFetcherReady(paramKey1, paramObject, paramDataFetcher, this.loadData.fetcher.getDataSource(), paramKey1);
  }
  
  public void onDataReady(Object paramObject)
  {
    DiskCacheStrategy localDiskCacheStrategy = this.helper.getDiskCacheStrategy();
    if ((paramObject != null) && (localDiskCacheStrategy.isDataCacheable(this.loadData.fetcher.getDataSource())))
    {
      this.dataToCache = paramObject;
      this.cb.reschedule();
      return;
    }
    this.cb.onDataFetcherReady(this.loadData.sourceKey, paramObject, this.loadData.fetcher, this.loadData.fetcher.getDataSource(), this.originalKey);
  }
  
  public void onLoadFailed(Exception paramException)
  {
    this.cb.onDataFetcherFailed(this.originalKey, paramException, this.loadData.fetcher, this.loadData.fetcher.getDataSource());
  }
  
  public void reschedule()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean startNext()
  {
    Object localObject;
    if (this.dataToCache != null)
    {
      localObject = this.dataToCache;
      this.dataToCache = null;
      cacheData(localObject);
    }
    boolean bool2;
    if ((this.sourceCacheGenerator != null) && (this.sourceCacheGenerator.startNext()))
    {
      bool2 = true;
      return bool2;
    }
    this.sourceCacheGenerator = null;
    this.loadData = null;
    boolean bool1 = false;
    for (;;)
    {
      bool2 = bool1;
      if (bool1) {
        break;
      }
      bool2 = bool1;
      if (!hasNextModelLoader()) {
        break;
      }
      localObject = this.helper.getLoadData();
      int i = this.loadDataListIndex;
      this.loadDataListIndex = (i + 1);
      this.loadData = ((ModelLoader.LoadData)((List)localObject).get(i));
      if ((this.loadData != null) && ((this.helper.getDiskCacheStrategy().isDataCacheable(this.loadData.fetcher.getDataSource())) || (this.helper.hasLoadPath(this.loadData.fetcher.getDataClass()))))
      {
        bool1 = true;
        this.loadData.fetcher.loadData(this.helper.getPriority(), this);
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/SourceGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */