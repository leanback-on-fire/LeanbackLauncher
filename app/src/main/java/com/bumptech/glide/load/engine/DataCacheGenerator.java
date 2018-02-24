package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.File;
import java.util.List;

class DataCacheGenerator
  implements DataFetcherGenerator, DataFetcher.DataCallback<Object>
{
  private File cacheFile;
  private List<Key> cacheKeys;
  private final DataFetcherGenerator.FetcherReadyCallback cb;
  private final DecodeHelper<?> helper;
  private volatile ModelLoader.LoadData<?> loadData;
  private int modelLoaderIndex;
  private List<ModelLoader<File, ?>> modelLoaders;
  private int sourceIdIndex = -1;
  private Key sourceKey;
  
  DataCacheGenerator(DecodeHelper<?> paramDecodeHelper, DataFetcherGenerator.FetcherReadyCallback paramFetcherReadyCallback)
  {
    this(paramDecodeHelper.getCacheKeys(), paramDecodeHelper, paramFetcherReadyCallback);
  }
  
  DataCacheGenerator(List<Key> paramList, DecodeHelper<?> paramDecodeHelper, DataFetcherGenerator.FetcherReadyCallback paramFetcherReadyCallback)
  {
    this.cacheKeys = paramList;
    this.helper = paramDecodeHelper;
    this.cb = paramFetcherReadyCallback;
  }
  
  private boolean hasNextModelLoader()
  {
    return this.modelLoaderIndex < this.modelLoaders.size();
  }
  
  public void cancel()
  {
    ModelLoader.LoadData localLoadData = this.loadData;
    if (localLoadData != null) {
      localLoadData.fetcher.cancel();
    }
  }
  
  public void onDataReady(Object paramObject)
  {
    this.cb.onDataFetcherReady(this.sourceKey, paramObject, this.loadData.fetcher, DataSource.DATA_DISK_CACHE, this.sourceKey);
  }
  
  public void onLoadFailed(Exception paramException)
  {
    this.cb.onDataFetcherFailed(this.sourceKey, paramException, this.loadData.fetcher, DataSource.DATA_DISK_CACHE);
  }
  
  public boolean startNext()
  {
    boolean bool2 = false;
    Object localObject;
    while ((this.modelLoaders == null) || (!hasNextModelLoader()))
    {
      this.sourceIdIndex += 1;
      if (this.sourceIdIndex >= this.cacheKeys.size()) {
        return bool2;
      }
      localObject = (Key)this.cacheKeys.get(this.sourceIdIndex);
      DataCacheKey localDataCacheKey = new DataCacheKey((Key)localObject, this.helper.getSignature());
      this.cacheFile = this.helper.getDiskCache().get(localDataCacheKey);
      if (this.cacheFile != null)
      {
        this.sourceKey = ((Key)localObject);
        this.modelLoaders = this.helper.getModelLoaders(this.cacheFile);
        this.modelLoaderIndex = 0;
      }
    }
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
      localObject = this.modelLoaders;
      int i = this.modelLoaderIndex;
      this.modelLoaderIndex = (i + 1);
      this.loadData = ((ModelLoader)((List)localObject).get(i)).buildLoadData(this.cacheFile, this.helper.getWidth(), this.helper.getHeight(), this.helper.getOptions());
      if ((this.loadData != null) && (this.helper.hasLoadPath(this.loadData.fetcher.getDataClass())))
      {
        bool1 = true;
        this.loadData.fetcher.loadData(this.helper.getPriority(), this);
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/DataCacheGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */