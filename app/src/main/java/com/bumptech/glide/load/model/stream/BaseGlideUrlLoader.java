package com.bumptech.glide.load.model.stream;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class BaseGlideUrlLoader<Model>
  implements ModelLoader<Model, InputStream>
{
  private final ModelLoader<GlideUrl, InputStream> concreteLoader;
  @Nullable
  private final ModelCache<Model, GlideUrl> modelCache;
  
  protected BaseGlideUrlLoader(ModelLoader<GlideUrl, InputStream> paramModelLoader)
  {
    this(paramModelLoader, null);
  }
  
  protected BaseGlideUrlLoader(ModelLoader<GlideUrl, InputStream> paramModelLoader, @Nullable ModelCache<Model, GlideUrl> paramModelCache)
  {
    this.concreteLoader = paramModelLoader;
    this.modelCache = paramModelCache;
  }
  
  private static List<Key> getAlternateKeys(List<String> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localArrayList.add(new GlideUrl((String)paramList.next()));
    }
    return localArrayList;
  }
  
  @Nullable
  public ModelLoader.LoadData<InputStream> buildLoadData(Model paramModel, int paramInt1, int paramInt2, Options paramOptions)
  {
    Object localObject1 = null;
    if (this.modelCache != null) {
      localObject1 = (GlideUrl)this.modelCache.get(paramModel, paramInt1, paramInt2);
    }
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject1 = getUrl(paramModel, paramInt1, paramInt2, paramOptions);
      if (TextUtils.isEmpty((CharSequence)localObject1)) {
        paramModel = null;
      }
    }
    do
    {
      return paramModel;
      localObject1 = new GlideUrl((String)localObject1, getHeaders(paramModel, paramInt1, paramInt2, paramOptions));
      localObject2 = localObject1;
      if (this.modelCache != null)
      {
        this.modelCache.put(paramModel, paramInt1, paramInt2, localObject1);
        localObject2 = localObject1;
      }
      localObject1 = getAlternateUrls(paramModel, paramInt1, paramInt2, paramOptions);
      paramOptions = this.concreteLoader.buildLoadData(localObject2, paramInt1, paramInt2, paramOptions);
      paramModel = paramOptions;
    } while (((List)localObject1).isEmpty());
    return new ModelLoader.LoadData(paramOptions.sourceKey, getAlternateKeys((List)localObject1), paramOptions.fetcher);
  }
  
  protected List<String> getAlternateUrls(Model paramModel, int paramInt1, int paramInt2, Options paramOptions)
  {
    return Collections.emptyList();
  }
  
  @Nullable
  protected Headers getHeaders(Model paramModel, int paramInt1, int paramInt2, Options paramOptions)
  {
    return Headers.DEFAULT;
  }
  
  protected abstract String getUrl(Model paramModel, int paramInt1, int paramInt2, Options paramOptions);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/stream/BaseGlideUrlLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */