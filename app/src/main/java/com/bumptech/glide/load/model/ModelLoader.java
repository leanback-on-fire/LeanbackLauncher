package com.bumptech.glide.load.model;

import android.support.annotation.Nullable;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.Preconditions;
import java.util.Collections;
import java.util.List;

public abstract interface ModelLoader<Model, Data>
{
  @Nullable
  public abstract LoadData<Data> buildLoadData(Model paramModel, int paramInt1, int paramInt2, Options paramOptions);
  
  public abstract boolean handles(Model paramModel);
  
  public static class LoadData<Data>
  {
    public final List<Key> alternateKeys;
    public final DataFetcher<Data> fetcher;
    public final Key sourceKey;
    
    public LoadData(Key paramKey, DataFetcher<Data> paramDataFetcher)
    {
      this(paramKey, Collections.emptyList(), paramDataFetcher);
    }
    
    public LoadData(Key paramKey, List<Key> paramList, DataFetcher<Data> paramDataFetcher)
    {
      this.sourceKey = ((Key)Preconditions.checkNotNull(paramKey));
      this.alternateKeys = ((List)Preconditions.checkNotNull(paramList));
      this.fetcher = ((DataFetcher)Preconditions.checkNotNull(paramDataFetcher));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/ModelLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */