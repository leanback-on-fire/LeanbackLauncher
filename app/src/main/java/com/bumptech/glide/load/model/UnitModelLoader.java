package com.bumptech.glide.load.model;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;

public class UnitModelLoader<Model>
  implements ModelLoader<Model, Model>
{
  public ModelLoader.LoadData<Model> buildLoadData(Model paramModel, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramModel), new UnitFetcher(paramModel));
  }
  
  public boolean handles(Model paramModel)
  {
    return true;
  }
  
  public static class Factory<Model>
    implements ModelLoaderFactory<Model, Model>
  {
    public ModelLoader<Model, Model> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UnitModelLoader();
    }
    
    public void teardown() {}
  }
  
  private static class UnitFetcher<Model>
    implements DataFetcher<Model>
  {
    private final Model resource;
    
    public UnitFetcher(Model paramModel)
    {
      this.resource = paramModel;
    }
    
    public void cancel() {}
    
    public void cleanup() {}
    
    public Class<Model> getDataClass()
    {
      return this.resource.getClass();
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super Model> paramDataCallback)
    {
      paramDataCallback.onDataReady(this.resource);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/UnitModelLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */