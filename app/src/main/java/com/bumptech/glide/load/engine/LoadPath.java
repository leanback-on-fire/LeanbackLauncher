package com.bumptech.glide.load.engine;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadPath<Data, ResourceType, Transcode>
{
  private final Class<Data> dataClass;
  private final List<? extends DecodePath<Data, ResourceType, Transcode>> decodePaths;
  private final String failureMessage;
  private final Pools.Pool<List<Exception>> listPool;
  
  public LoadPath(Class<Data> paramClass, Class<ResourceType> paramClass1, Class<Transcode> paramClass2, List<DecodePath<Data, ResourceType, Transcode>> paramList, Pools.Pool<List<Exception>> paramPool)
  {
    this.dataClass = paramClass;
    this.listPool = paramPool;
    this.decodePaths = ((List)Preconditions.checkNotEmpty(paramList));
    paramClass = String.valueOf(paramClass.getSimpleName());
    paramClass1 = String.valueOf(paramClass1.getSimpleName());
    paramClass2 = String.valueOf(paramClass2.getSimpleName());
    this.failureMessage = (String.valueOf(paramClass).length() + 21 + String.valueOf(paramClass1).length() + String.valueOf(paramClass2).length() + "Failed LoadPath{" + paramClass + "->" + paramClass1 + "->" + paramClass2 + "}");
  }
  
  private Resource<Transcode> loadWithExceptionList(DataRewinder<Data> paramDataRewinder, Options paramOptions, int paramInt1, int paramInt2, DecodePath.DecodeCallback<ResourceType> paramDecodeCallback, List<Exception> paramList)
    throws GlideException
  {
    int j = this.decodePaths.size();
    Object localObject1 = null;
    int i = 0;
    for (;;)
    {
      Object localObject2 = localObject1;
      if (i < j) {
        localObject2 = (DecodePath)this.decodePaths.get(i);
      }
      try
      {
        localObject2 = ((DecodePath)localObject2).decode(paramDataRewinder, paramInt1, paramInt2, paramOptions, paramDecodeCallback);
        localObject1 = localObject2;
      }
      catch (GlideException localGlideException)
      {
        for (;;)
        {
          paramList.add(localGlideException);
        }
        i += 1;
      }
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (localObject2 != null) {
          break;
        }
        throw new GlideException(this.failureMessage, new ArrayList(paramList));
      }
    }
    return localGlideException;
  }
  
  public Class<Data> getDataClass()
  {
    return this.dataClass;
  }
  
  public Resource<Transcode> load(DataRewinder<Data> paramDataRewinder, Options paramOptions, int paramInt1, int paramInt2, DecodePath.DecodeCallback<ResourceType> paramDecodeCallback)
    throws GlideException
  {
    List localList = (List)this.listPool.acquire();
    try
    {
      paramDataRewinder = loadWithExceptionList(paramDataRewinder, paramOptions, paramInt1, paramInt2, paramDecodeCallback, localList);
      return paramDataRewinder;
    }
    finally
    {
      this.listPool.release(localList);
    }
  }
  
  public String toString()
  {
    String str = String.valueOf(Arrays.toString(this.decodePaths.toArray(new DecodePath[this.decodePaths.size()])));
    return String.valueOf(str).length() + 22 + "LoadPath{decodePaths=" + str + "}";
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/LoadPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */