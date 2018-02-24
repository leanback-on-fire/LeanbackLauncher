package com.bumptech.glide.load.engine;

import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.Registry.NoModelLoaderAvailableException;
import com.bumptech.glide.Registry.NoSourceEncoderAvailableException;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.resource.UnitTransformation;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class DecodeHelper<Transcode>
{
  private final List<Key> cacheKeys = new ArrayList();
  private DecodeJob.DiskCacheProvider diskCacheProvider;
  private DiskCacheStrategy diskCacheStrategy;
  private GlideContext glideContext;
  private int height;
  private boolean isCacheKeysSet;
  private boolean isLoadDataSet;
  private boolean isTransformationRequired;
  private final List<ModelLoader.LoadData<?>> loadData = new ArrayList();
  private Object model;
  private Options options;
  private Priority priority;
  private Class<?> resourceClass;
  private Key signature;
  private Class<Transcode> transcodeClass;
  private Map<Class<?>, Transformation<?>> transformations;
  private int width;
  
  void clear()
  {
    this.glideContext = null;
    this.model = null;
    this.signature = null;
    this.resourceClass = null;
    this.transcodeClass = null;
    this.options = null;
    this.priority = null;
    this.transformations = null;
    this.diskCacheStrategy = null;
    this.loadData.clear();
    this.isLoadDataSet = false;
    this.cacheKeys.clear();
    this.isCacheKeysSet = false;
  }
  
  List<Key> getCacheKeys()
  {
    if (!this.isCacheKeysSet)
    {
      this.isCacheKeysSet = true;
      this.cacheKeys.clear();
      List localList = getLoadData();
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        ModelLoader.LoadData localLoadData = (ModelLoader.LoadData)localList.get(i);
        this.cacheKeys.add(localLoadData.sourceKey);
        this.cacheKeys.addAll(localLoadData.alternateKeys);
        i += 1;
      }
    }
    return this.cacheKeys;
  }
  
  DiskCache getDiskCache()
  {
    return this.diskCacheProvider.getDiskCache();
  }
  
  DiskCacheStrategy getDiskCacheStrategy()
  {
    return this.diskCacheStrategy;
  }
  
  int getHeight()
  {
    return this.height;
  }
  
  List<ModelLoader.LoadData<?>> getLoadData()
  {
    if (!this.isLoadDataSet)
    {
      this.isLoadDataSet = true;
      this.loadData.clear();
      List localList = this.glideContext.getRegistry().getModelLoaders(this.model);
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        ModelLoader.LoadData localLoadData = ((ModelLoader)localList.get(i)).buildLoadData(this.model, this.width, this.height, this.options);
        if (localLoadData != null) {
          this.loadData.add(localLoadData);
        }
        i += 1;
      }
    }
    return this.loadData;
  }
  
  <Data> LoadPath<Data, ?, Transcode> getLoadPath(Class<Data> paramClass)
  {
    return this.glideContext.getRegistry().getLoadPath(paramClass, this.resourceClass, this.transcodeClass);
  }
  
  Object getModel()
  {
    return this.model;
  }
  
  List<ModelLoader<File, ?>> getModelLoaders(File paramFile)
    throws Registry.NoModelLoaderAvailableException
  {
    return this.glideContext.getRegistry().getModelLoaders(paramFile);
  }
  
  Options getOptions()
  {
    return this.options;
  }
  
  Priority getPriority()
  {
    return this.priority;
  }
  
  List<Class<?>> getRegisteredResourceClasses()
  {
    return this.glideContext.getRegistry().getRegisteredResourceClasses(this.model.getClass(), this.resourceClass, this.transcodeClass);
  }
  
  <Z> ResourceEncoder<Z> getResultEncoder(Resource<Z> paramResource)
  {
    return this.glideContext.getRegistry().getResultEncoder(paramResource);
  }
  
  Key getSignature()
  {
    return this.signature;
  }
  
  <X> Encoder<X> getSourceEncoder(X paramX)
    throws Registry.NoSourceEncoderAvailableException
  {
    return this.glideContext.getRegistry().getSourceEncoder(paramX);
  }
  
  <Z> Transformation<Z> getTransformation(Class<Z> paramClass)
  {
    Transformation localTransformation = (Transformation)this.transformations.get(paramClass);
    Object localObject = localTransformation;
    if (localTransformation == null)
    {
      if ((this.transformations.isEmpty()) && (this.isTransformationRequired))
      {
        paramClass = String.valueOf(paramClass);
        throw new IllegalArgumentException(String.valueOf(paramClass).length() + 115 + "Missing transformation for " + paramClass + ". If you wish to ignore unknown resource types, use the optional transformation methods.");
      }
      localObject = UnitTransformation.get();
    }
    return (Transformation<Z>)localObject;
  }
  
  int getWidth()
  {
    return this.width;
  }
  
  boolean hasLoadPath(Class<?> paramClass)
  {
    return getLoadPath(paramClass) != null;
  }
  
  <R> DecodeHelper<R> init(GlideContext paramGlideContext, Object paramObject, Key paramKey, int paramInt1, int paramInt2, DiskCacheStrategy paramDiskCacheStrategy, Class<?> paramClass, Class<R> paramClass1, Priority paramPriority, Options paramOptions, Map<Class<?>, Transformation<?>> paramMap, boolean paramBoolean, DecodeJob.DiskCacheProvider paramDiskCacheProvider)
  {
    this.glideContext = paramGlideContext;
    this.model = paramObject;
    this.signature = paramKey;
    this.width = paramInt1;
    this.height = paramInt2;
    this.diskCacheStrategy = paramDiskCacheStrategy;
    this.resourceClass = paramClass;
    this.diskCacheProvider = paramDiskCacheProvider;
    this.transcodeClass = paramClass1;
    this.priority = paramPriority;
    this.options = paramOptions;
    this.transformations = paramMap;
    this.isTransformationRequired = paramBoolean;
    return this;
  }
  
  boolean isResourceEncoderAvailable(Resource<?> paramResource)
  {
    return this.glideContext.getRegistry().isResourceEncoderAvailable(paramResource);
  }
  
  boolean isSourceKey(Key paramKey)
  {
    List localList = getLoadData();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      if (((ModelLoader.LoadData)localList.get(i)).sourceKey.equals(paramKey)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/DecodeHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */