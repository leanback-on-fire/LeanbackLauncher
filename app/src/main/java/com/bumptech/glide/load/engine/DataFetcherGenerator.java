package com.bumptech.glide.load.engine;

import android.support.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;

abstract interface DataFetcherGenerator
{
  public abstract void cancel();
  
  public abstract boolean startNext();
  
  public static abstract interface FetcherReadyCallback
  {
    public abstract void onDataFetcherFailed(Key paramKey, Exception paramException, DataFetcher<?> paramDataFetcher, DataSource paramDataSource);
    
    public abstract void onDataFetcherReady(Key paramKey1, @Nullable Object paramObject, DataFetcher<?> paramDataFetcher, DataSource paramDataSource, Key paramKey2);
    
    public abstract void reschedule();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/DataFetcherGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */