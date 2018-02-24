package com.bumptech.glide;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.data.DataRewinder.Factory;
import com.bumptech.glide.load.data.DataRewinderRegistry;
import com.bumptech.glide.load.engine.DecodePath;
import com.bumptech.glide.load.engine.LoadPath;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.ModelLoaderRegistry;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry;
import com.bumptech.glide.provider.EncoderRegistry;
import com.bumptech.glide.provider.LoadPathCache;
import com.bumptech.glide.provider.ModelToResourceClassCache;
import com.bumptech.glide.provider.ResourceDecoderRegistry;
import com.bumptech.glide.provider.ResourceEncoderRegistry;
import com.bumptech.glide.util.pool.FactoryPools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Registry
{
  private final DataRewinderRegistry dataRewinderRegistry = new DataRewinderRegistry();
  private final ResourceDecoderRegistry decoderRegistry = new ResourceDecoderRegistry();
  private final EncoderRegistry encoderRegistry = new EncoderRegistry();
  private final Pools.Pool<List<Exception>> exceptionListPool = FactoryPools.threadSafeList();
  private final LoadPathCache loadPathCache = new LoadPathCache();
  private final ModelLoaderRegistry modelLoaderRegistry = new ModelLoaderRegistry(this.exceptionListPool);
  private final ModelToResourceClassCache modelToResourceClassCache = new ModelToResourceClassCache();
  private final ResourceEncoderRegistry resourceEncoderRegistry = new ResourceEncoderRegistry();
  private final TranscoderRegistry transcoderRegistry = new TranscoderRegistry();
  
  private <Data, TResource, Transcode> List<DecodePath<Data, TResource, Transcode>> getDecodePaths(Class<Data> paramClass, Class<TResource> paramClass1, Class<Transcode> paramClass2)
  {
    ArrayList localArrayList = new ArrayList();
    paramClass1 = this.decoderRegistry.getResourceClasses(paramClass, paramClass1).iterator();
    while (paramClass1.hasNext())
    {
      Class localClass1 = (Class)paramClass1.next();
      Iterator localIterator = this.transcoderRegistry.getTranscodeClasses(localClass1, paramClass2).iterator();
      while (localIterator.hasNext())
      {
        Class localClass2 = (Class)localIterator.next();
        localArrayList.add(new DecodePath(paramClass, localClass1, localClass2, this.decoderRegistry.getDecoders(paramClass, localClass1), this.transcoderRegistry.get(localClass1, localClass2), this.exceptionListPool));
      }
    }
    return localArrayList;
  }
  
  public <Data, TResource> Registry append(Class<Data> paramClass, Class<TResource> paramClass1, ResourceDecoder<Data, TResource> paramResourceDecoder)
  {
    this.decoderRegistry.append(paramResourceDecoder, paramClass, paramClass1);
    return this;
  }
  
  public <Model, Data> Registry append(Class<Model> paramClass, Class<Data> paramClass1, ModelLoaderFactory<Model, Data> paramModelLoaderFactory)
  {
    this.modelLoaderRegistry.append(paramClass, paramClass1, paramModelLoaderFactory);
    return this;
  }
  
  public <Data, TResource, Transcode> LoadPath<Data, TResource, Transcode> getLoadPath(Class<Data> paramClass, Class<TResource> paramClass1, Class<Transcode> paramClass2)
  {
    LoadPath localLoadPath = this.loadPathCache.get(paramClass, paramClass1, paramClass2);
    Object localObject = localLoadPath;
    if (localLoadPath == null)
    {
      localObject = localLoadPath;
      if (!this.loadPathCache.contains(paramClass, paramClass1, paramClass2))
      {
        localObject = getDecodePaths(paramClass, paramClass1, paramClass2);
        if (!((List)localObject).isEmpty()) {
          break label75;
        }
      }
    }
    label75:
    for (localObject = null;; localObject = new LoadPath(paramClass, paramClass1, paramClass2, (List)localObject, this.exceptionListPool))
    {
      this.loadPathCache.put(paramClass, paramClass1, paramClass2, (LoadPath)localObject);
      return (LoadPath<Data, TResource, Transcode>)localObject;
    }
  }
  
  public <Model> List<ModelLoader<Model, ?>> getModelLoaders(Model paramModel)
  {
    List localList = this.modelLoaderRegistry.getModelLoaders(paramModel);
    if (localList.isEmpty()) {
      throw new NoModelLoaderAvailableException(paramModel);
    }
    return localList;
  }
  
  public <Model, TResource, Transcode> List<Class<?>> getRegisteredResourceClasses(Class<Model> paramClass, Class<TResource> paramClass1, Class<Transcode> paramClass2)
  {
    Object localObject2 = this.modelToResourceClassCache.get(paramClass, paramClass1);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new ArrayList();
      localObject2 = this.modelLoaderRegistry.getDataClasses(paramClass).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = (Class)((Iterator)localObject2).next();
        localObject3 = this.decoderRegistry.getResourceClasses((Class)localObject3, paramClass1).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          Class localClass = (Class)((Iterator)localObject3).next();
          if ((!this.transcoderRegistry.getTranscodeClasses(localClass, paramClass2).isEmpty()) && (!((List)localObject1).contains(localClass))) {
            ((List)localObject1).add(localClass);
          }
        }
      }
      this.modelToResourceClassCache.put(paramClass, paramClass1, Collections.unmodifiableList((List)localObject1));
    }
    return (List<Class<?>>)localObject1;
  }
  
  public <X> ResourceEncoder<X> getResultEncoder(Resource<X> paramResource)
    throws Registry.NoResultEncoderAvailableException
  {
    ResourceEncoder localResourceEncoder = this.resourceEncoderRegistry.get(paramResource.getResourceClass());
    if (localResourceEncoder != null) {
      return localResourceEncoder;
    }
    throw new NoResultEncoderAvailableException(paramResource.getResourceClass());
  }
  
  public <X> DataRewinder<X> getRewinder(X paramX)
  {
    return this.dataRewinderRegistry.build(paramX);
  }
  
  public <X> Encoder<X> getSourceEncoder(X paramX)
    throws Registry.NoSourceEncoderAvailableException
  {
    Encoder localEncoder = this.encoderRegistry.getEncoder(paramX.getClass());
    if (localEncoder != null) {
      return localEncoder;
    }
    throw new NoSourceEncoderAvailableException(paramX.getClass());
  }
  
  public boolean isResourceEncoderAvailable(Resource<?> paramResource)
  {
    return this.resourceEncoderRegistry.get(paramResource.getResourceClass()) != null;
  }
  
  public <Data, TResource> Registry prepend(Class<Data> paramClass, Class<TResource> paramClass1, ResourceDecoder<Data, TResource> paramResourceDecoder)
  {
    this.decoderRegistry.prepend(paramResourceDecoder, paramClass, paramClass1);
    return this;
  }
  
  public <Model, Data> Registry prepend(Class<Model> paramClass, Class<Data> paramClass1, ModelLoaderFactory<Model, Data> paramModelLoaderFactory)
  {
    this.modelLoaderRegistry.prepend(paramClass, paramClass1, paramModelLoaderFactory);
    return this;
  }
  
  public Registry register(DataRewinder.Factory paramFactory)
  {
    this.dataRewinderRegistry.register(paramFactory);
    return this;
  }
  
  public <Data> Registry register(Class<Data> paramClass, Encoder<Data> paramEncoder)
  {
    this.encoderRegistry.add(paramClass, paramEncoder);
    return this;
  }
  
  public <TResource> Registry register(Class<TResource> paramClass, ResourceEncoder<TResource> paramResourceEncoder)
  {
    this.resourceEncoderRegistry.add(paramClass, paramResourceEncoder);
    return this;
  }
  
  public <TResource, Transcode> Registry register(Class<TResource> paramClass, Class<Transcode> paramClass1, ResourceTranscoder<TResource, Transcode> paramResourceTranscoder)
  {
    this.transcoderRegistry.register(paramClass, paramClass1, paramResourceTranscoder);
    return this;
  }
  
  public <Model, Data> Registry replace(Class<Model> paramClass, Class<Data> paramClass1, ModelLoaderFactory<Model, Data> paramModelLoaderFactory)
  {
    this.modelLoaderRegistry.replace(paramClass, paramClass1, paramModelLoaderFactory);
    return this;
  }
  
  public static class MissingComponentException
    extends RuntimeException
  {
    public MissingComponentException(String paramString)
    {
      super();
    }
  }
  
  public static class NoModelLoaderAvailableException
    extends Registry.MissingComponentException
  {
    public NoModelLoaderAvailableException(Class paramClass1, Class paramClass2)
    {
      super();
    }
    
    public NoModelLoaderAvailableException(Object paramObject)
    {
      super();
    }
  }
  
  public static class NoResultEncoderAvailableException
    extends Registry.MissingComponentException
  {
    public NoResultEncoderAvailableException(Class<?> paramClass)
    {
      super();
    }
  }
  
  public static class NoSourceEncoderAvailableException
    extends Registry.MissingComponentException
  {
    public NoSourceEncoderAvailableException(Class<?> paramClass)
    {
      super();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/Registry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */