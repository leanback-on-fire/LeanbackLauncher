package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResourceDecoderRegistry
{
  private final List<Entry<?, ?>> decoders = new ArrayList();
  
  public <T, R> void append(ResourceDecoder<T, R> paramResourceDecoder, Class<T> paramClass, Class<R> paramClass1)
  {
    try
    {
      this.decoders.add(new Entry(paramClass, paramClass1, paramResourceDecoder));
      return;
    }
    finally
    {
      paramResourceDecoder = finally;
      throw paramResourceDecoder;
    }
  }
  
  public <T, R> List<ResourceDecoder<T, R>> getDecoders(Class<T> paramClass, Class<R> paramClass1)
  {
    ArrayList localArrayList;
    try
    {
      localArrayList = new ArrayList();
      Iterator localIterator = this.decoders.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        if (localEntry.handles(paramClass, paramClass1)) {
          localArrayList.add(localEntry.decoder);
        }
      }
    }
    finally {}
    return localArrayList;
  }
  
  public <T, R> List<Class<R>> getResourceClasses(Class<T> paramClass, Class<R> paramClass1)
  {
    ArrayList localArrayList;
    try
    {
      localArrayList = new ArrayList();
      Iterator localIterator = this.decoders.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        if (localEntry.handles(paramClass, paramClass1)) {
          localArrayList.add(localEntry.resourceClass);
        }
      }
    }
    finally {}
    return localArrayList;
  }
  
  public <T, R> void prepend(ResourceDecoder<T, R> paramResourceDecoder, Class<T> paramClass, Class<R> paramClass1)
  {
    try
    {
      this.decoders.add(0, new Entry(paramClass, paramClass1, paramResourceDecoder));
      return;
    }
    finally
    {
      paramResourceDecoder = finally;
      throw paramResourceDecoder;
    }
  }
  
  private static class Entry<T, R>
  {
    private final Class<T> dataClass;
    private final ResourceDecoder<T, R> decoder;
    private final Class<R> resourceClass;
    
    public Entry(Class<T> paramClass, Class<R> paramClass1, ResourceDecoder<T, R> paramResourceDecoder)
    {
      this.dataClass = paramClass;
      this.resourceClass = paramClass1;
      this.decoder = paramResourceDecoder;
    }
    
    public boolean handles(Class<?> paramClass1, Class<?> paramClass2)
    {
      return (this.dataClass.isAssignableFrom(paramClass1)) && (paramClass2.isAssignableFrom(this.resourceClass));
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/provider/ResourceDecoderRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */