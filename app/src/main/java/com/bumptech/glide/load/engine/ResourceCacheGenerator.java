package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.File;
import java.util.List;

class ResourceCacheGenerator
  implements DataFetcherGenerator, DataFetcher.DataCallback<Object>
{
  private File cacheFile;
  private final DataFetcherGenerator.FetcherReadyCallback cb;
  private ResourceCacheKey currentKey;
  private final DecodeHelper<?> helper;
  private volatile ModelLoader.LoadData<?> loadData;
  private int modelLoaderIndex;
  private List<ModelLoader<File, ?>> modelLoaders;
  private int resourceClassIndex = -1;
  private int sourceIdIndex = 0;
  private Key sourceKey;
  
  public ResourceCacheGenerator(DecodeHelper<?> paramDecodeHelper, DataFetcherGenerator.FetcherReadyCallback paramFetcherReadyCallback)
  {
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
    this.cb.onDataFetcherReady(this.sourceKey, paramObject, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE, this.currentKey);
  }
  
  public void onLoadFailed(Exception paramException)
  {
    this.cb.onDataFetcherFailed(this.currentKey, paramException, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE);
  }
  
  public boolean startNext()
  {
    boolean bool1 = false;
    List localList1 = this.helper.getCacheKeys();
    boolean bool2;
    if (localList1.isEmpty())
    {
      bool2 = bool1;
      return bool2;
    }
    List localList2 = this.helper.getRegisteredResourceClasses();
    for (;;)
    {
      if ((this.modelLoaders != null) && (hasNextModelLoader())) {
        break label247;
      }
      this.resourceClassIndex += 1;
      if (this.resourceClassIndex >= localList2.size())
      {
        this.sourceIdIndex += 1;
        bool2 = bool1;
        if (this.sourceIdIndex >= localList1.size()) {
          break;
        }
        this.resourceClassIndex = 0;
      }
      Key localKey = (Key)localList1.get(this.sourceIdIndex);
      Class localClass = (Class)localList2.get(this.resourceClassIndex);
      Transformation localTransformation = this.helper.getTransformation(localClass);
      this.currentKey = new ResourceCacheKey(localKey, this.helper.getSignature(), this.helper.getWidth(), this.helper.getHeight(), localTransformation, localClass, this.helper.getOptions());
      this.cacheFile = this.helper.getDiskCache().get(this.currentKey);
      if (this.cacheFile != null)
      {
        this.sourceKey = localKey;
        this.modelLoaders = this.helper.getModelLoaders(this.cacheFile);
        this.modelLoaderIndex = 0;
      }
    }
    label247:
    this.loadData = null;
    bool1 = false;
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
      localList1 = this.modelLoaders;
      int i = this.modelLoaderIndex;
      this.modelLoaderIndex = (i + 1);
      this.loadData = ((ModelLoader)localList1.get(i)).buildLoadData(this.cacheFile, this.helper.getWidth(), this.helper.getHeight(), this.helper.getOptions());
      if ((this.loadData != null) && (this.helper.hasLoadPath(this.loadData.fetcher.getDataClass())))
      {
        bool1 = true;
        this.loadData.fetcher.loadData(this.helper.getPriority(), this);
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/ResourceCacheGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */