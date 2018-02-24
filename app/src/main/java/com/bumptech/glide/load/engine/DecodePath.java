package com.bumptech.glide.load.engine;

import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DecodePath<DataType, ResourceType, Transcode>
{
  private static final String TAG = "DecodePath";
  private final Class<DataType> dataClass;
  private final List<? extends ResourceDecoder<DataType, ResourceType>> decoders;
  private final String failureMessage;
  private final Pools.Pool<List<Exception>> listPool;
  private final ResourceTranscoder<ResourceType, Transcode> transcoder;
  
  public DecodePath(Class<DataType> paramClass, Class<ResourceType> paramClass1, Class<Transcode> paramClass2, List<? extends ResourceDecoder<DataType, ResourceType>> paramList, ResourceTranscoder<ResourceType, Transcode> paramResourceTranscoder, Pools.Pool<List<Exception>> paramPool)
  {
    this.dataClass = paramClass;
    this.decoders = paramList;
    this.transcoder = paramResourceTranscoder;
    this.listPool = paramPool;
    paramClass = String.valueOf(paramClass.getSimpleName());
    paramClass1 = String.valueOf(paramClass1.getSimpleName());
    paramClass2 = String.valueOf(paramClass2.getSimpleName());
    this.failureMessage = (String.valueOf(paramClass).length() + 23 + String.valueOf(paramClass1).length() + String.valueOf(paramClass2).length() + "Failed DecodePath{" + paramClass + "->" + paramClass1 + "->" + paramClass2 + "}");
  }
  
  private Resource<ResourceType> decodeResource(DataRewinder<DataType> paramDataRewinder, int paramInt1, int paramInt2, Options paramOptions)
    throws GlideException
  {
    List localList = (List)this.listPool.acquire();
    try
    {
      paramDataRewinder = decodeResourceWithList(paramDataRewinder, paramInt1, paramInt2, paramOptions, localList);
      return paramDataRewinder;
    }
    finally
    {
      this.listPool.release(localList);
    }
  }
  
  private Resource<ResourceType> decodeResourceWithList(DataRewinder<DataType> paramDataRewinder, int paramInt1, int paramInt2, Options paramOptions, List<Exception> paramList)
    throws GlideException
  {
    Object localObject1 = null;
    int i = 0;
    int j = this.decoders.size();
    Object localObject3;
    for (;;)
    {
      Object localObject2 = localObject1;
      Object localObject4;
      if (i < j)
      {
        localObject4 = (ResourceDecoder)this.decoders.get(i);
        localObject2 = localObject1;
      }
      try
      {
        if (((ResourceDecoder)localObject4).handles(paramDataRewinder.rewindAndGet(), paramOptions)) {
          localObject2 = ((ResourceDecoder)localObject4).decode(paramDataRewinder.rewindAndGet(), paramInt1, paramInt2, paramOptions);
        }
        if (localObject2 != null)
        {
          if (localObject2 != null) {
            break;
          }
          throw new GlideException(this.failureMessage, new ArrayList(paramList));
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          if (Log.isLoggable("DecodePath", 2))
          {
            localObject4 = String.valueOf(localObject4);
            Log.v("DecodePath", String.valueOf(localObject4).length() + 26 + "Failed to decode data for " + (String)localObject4, localIOException);
          }
          paramList.add(localIOException);
          localObject3 = localObject1;
        }
        i += 1;
        localObject1 = localObject3;
      }
    }
    return (Resource<ResourceType>)localObject3;
  }
  
  public Resource<Transcode> decode(DataRewinder<DataType> paramDataRewinder, int paramInt1, int paramInt2, Options paramOptions, DecodeCallback<ResourceType> paramDecodeCallback)
    throws GlideException
  {
    paramDataRewinder = paramDecodeCallback.onResourceDecoded(decodeResource(paramDataRewinder, paramInt1, paramInt2, paramOptions));
    return this.transcoder.transcode(paramDataRewinder);
  }
  
  public String toString()
  {
    String str1 = String.valueOf(this.dataClass);
    String str2 = String.valueOf(this.decoders);
    String str3 = String.valueOf(this.transcoder);
    return String.valueOf(str1).length() + 47 + String.valueOf(str2).length() + String.valueOf(str3).length() + "DecodePath{ dataClass=" + str1 + ", decoders=" + str2 + ", transcoder=" + str3 + "}";
  }
  
  static abstract interface DecodeCallback<ResourceType>
  {
    public abstract Resource<ResourceType> onResourceDecoded(Resource<ResourceType> paramResource);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/DecodePath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */