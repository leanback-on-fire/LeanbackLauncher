package com.bumptech.glide.load.model.stream;

import android.support.annotation.Nullable;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.HttpUrlFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;

public class HttpGlideUrlLoader
  implements ModelLoader<GlideUrl, InputStream>
{
  public static final Option<Integer> TIMEOUT = Option.memory("com.bumptech.glide.load.model.stream.HttpGlideUrlLoader.Timeout", Integer.valueOf(2500));
  @Nullable
  private final ModelCache<GlideUrl, GlideUrl> modelCache;
  
  public HttpGlideUrlLoader()
  {
    this(null);
  }
  
  public HttpGlideUrlLoader(ModelCache<GlideUrl, GlideUrl> paramModelCache)
  {
    this.modelCache = paramModelCache;
  }
  
  public ModelLoader.LoadData<InputStream> buildLoadData(GlideUrl paramGlideUrl, int paramInt1, int paramInt2, Options paramOptions)
  {
    Object localObject = paramGlideUrl;
    if (this.modelCache != null)
    {
      GlideUrl localGlideUrl = (GlideUrl)this.modelCache.get(paramGlideUrl, 0, 0);
      localObject = localGlideUrl;
      if (localGlideUrl == null)
      {
        this.modelCache.put(paramGlideUrl, 0, 0, paramGlideUrl);
        localObject = paramGlideUrl;
      }
    }
    return new ModelLoader.LoadData((Key)localObject, new HttpUrlFetcher((GlideUrl)localObject, ((Integer)paramOptions.get(TIMEOUT)).intValue()));
  }
  
  public boolean handles(GlideUrl paramGlideUrl)
  {
    return true;
  }
  
  public static class Factory
    implements ModelLoaderFactory<GlideUrl, InputStream>
  {
    private final ModelCache<GlideUrl, GlideUrl> modelCache = new ModelCache(500);
    
    public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new HttpGlideUrlLoader(this.modelCache);
    }
    
    public void teardown() {}
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/stream/HttpGlideUrlLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */