package com.bumptech.glide.load.model;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class MultiModelLoader<Model, Data>
  implements ModelLoader<Model, Data>
{
  private final Pools.Pool<List<Exception>> exceptionListPool;
  private final List<ModelLoader<Model, Data>> modelLoaders;
  
  MultiModelLoader(List<ModelLoader<Model, Data>> paramList, Pools.Pool<List<Exception>> paramPool)
  {
    this.modelLoaders = paramList;
    this.exceptionListPool = paramPool;
  }
  
  public ModelLoader.LoadData<Data> buildLoadData(Model paramModel, int paramInt1, int paramInt2, Options paramOptions)
  {
    Object localObject1 = null;
    int j = this.modelLoaders.size();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      Object localObject3 = (ModelLoader)this.modelLoaders.get(i);
      Object localObject2 = localObject1;
      if (((ModelLoader)localObject3).handles(paramModel))
      {
        localObject3 = ((ModelLoader)localObject3).buildLoadData(paramModel, paramInt1, paramInt2, paramOptions);
        localObject2 = localObject1;
        if (localObject3 != null)
        {
          localObject2 = ((ModelLoader.LoadData)localObject3).sourceKey;
          localArrayList.add(((ModelLoader.LoadData)localObject3).fetcher);
        }
      }
      i += 1;
      localObject1 = localObject2;
    }
    if (!localArrayList.isEmpty()) {
      return new ModelLoader.LoadData((Key)localObject1, new MultiFetcher(localArrayList, this.exceptionListPool));
    }
    return null;
  }
  
  public boolean handles(Model paramModel)
  {
    Iterator localIterator = this.modelLoaders.iterator();
    while (localIterator.hasNext()) {
      if (((ModelLoader)localIterator.next()).handles(paramModel)) {
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    String str = String.valueOf(Arrays.toString(this.modelLoaders.toArray(new ModelLoader[this.modelLoaders.size()])));
    return String.valueOf(str).length() + 31 + "MultiModelLoader{modelLoaders=" + str + "}";
  }
  
  static class MultiFetcher<Data>
    implements DataFetcher<Data>, DataFetcher.DataCallback<Data>
  {
    private DataFetcher.DataCallback<? super Data> callback;
    private int currentIndex;
    private final Pools.Pool<List<Exception>> exceptionListPool;
    private List<Exception> exceptions;
    private final List<DataFetcher<Data>> fetchers;
    private Priority priority;
    
    MultiFetcher(List<DataFetcher<Data>> paramList, Pools.Pool<List<Exception>> paramPool)
    {
      this.exceptionListPool = paramPool;
      Preconditions.checkNotEmpty(paramList);
      this.fetchers = paramList;
      this.currentIndex = 0;
    }
    
    private void startNextOrFail()
    {
      if (this.currentIndex < this.fetchers.size() - 1)
      {
        this.currentIndex += 1;
        loadData(this.priority, this.callback);
        return;
      }
      this.callback.onLoadFailed(new GlideException("Fetch failed", new ArrayList(this.exceptions)));
    }
    
    public void cancel()
    {
      Iterator localIterator = this.fetchers.iterator();
      while (localIterator.hasNext()) {
        ((DataFetcher)localIterator.next()).cancel();
      }
    }
    
    public void cleanup()
    {
      this.exceptionListPool.release(this.exceptions);
      this.exceptions = null;
      Iterator localIterator = this.fetchers.iterator();
      while (localIterator.hasNext()) {
        ((DataFetcher)localIterator.next()).cleanup();
      }
    }
    
    public Class<Data> getDataClass()
    {
      return ((DataFetcher)this.fetchers.get(0)).getDataClass();
    }
    
    public DataSource getDataSource()
    {
      return ((DataFetcher)this.fetchers.get(0)).getDataSource();
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super Data> paramDataCallback)
    {
      this.priority = paramPriority;
      this.callback = paramDataCallback;
      this.exceptions = ((List)this.exceptionListPool.acquire());
      ((DataFetcher)this.fetchers.get(this.currentIndex)).loadData(paramPriority, this);
    }
    
    public void onDataReady(Data paramData)
    {
      if (paramData != null)
      {
        this.callback.onDataReady(paramData);
        return;
      }
      startNextOrFail();
    }
    
    public void onLoadFailed(Exception paramException)
    {
      this.exceptions.add(paramException);
      startNextOrFail();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/MultiModelLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */