package com.bumptech.glide.load.resource.transcode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TranscoderRegistry
{
  private final List<Entry<?, ?>> transcoders = new ArrayList();
  
  public <Z, R> ResourceTranscoder<Z, R> get(Class<Z> paramClass, Class<R> paramClass1)
  {
    try
    {
      if (paramClass1.isAssignableFrom(paramClass)) {}
      Entry localEntry;
      for (paramClass = UnitTranscoder.get();; paramClass = localEntry.transcoder)
      {
        return paramClass;
        Iterator localIterator = this.transcoders.iterator();
        do
        {
          if (!localIterator.hasNext()) {
            break;
          }
          localEntry = (Entry)localIterator.next();
        } while (!localEntry.handles(paramClass, paramClass1));
      }
      paramClass = String.valueOf(paramClass);
      paramClass1 = String.valueOf(paramClass1);
      throw new IllegalArgumentException(String.valueOf(paramClass).length() + 47 + String.valueOf(paramClass1).length() + "No transcoder registered to transcode from " + paramClass + " to " + paramClass1);
    }
    finally {}
  }
  
  /* Error */
  public <Z, R> List<Class<R>> getTranscodeClasses(Class<Z> paramClass, Class<R> paramClass1)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 16	java/util/ArrayList
    //   5: dup
    //   6: invokespecial 17	java/util/ArrayList:<init>	()V
    //   9: astore_3
    //   10: aload_2
    //   11: aload_1
    //   12: invokevirtual 28	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   15: ifeq +15 -> 30
    //   18: aload_3
    //   19: aload_2
    //   20: invokeinterface 97 2 0
    //   25: pop
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_3
    //   29: areturn
    //   30: aload_0
    //   31: getfield 19	com/bumptech/glide/load/resource/transcode/TranscoderRegistry:transcoders	Ljava/util/List;
    //   34: invokeinterface 39 1 0
    //   39: astore 4
    //   41: aload 4
    //   43: invokeinterface 45 1 0
    //   48: ifeq -22 -> 26
    //   51: aload 4
    //   53: invokeinterface 49 1 0
    //   58: checkcast 6	com/bumptech/glide/load/resource/transcode/TranscoderRegistry$Entry
    //   61: aload_1
    //   62: aload_2
    //   63: invokevirtual 53	com/bumptech/glide/load/resource/transcode/TranscoderRegistry$Entry:handles	(Ljava/lang/Class;Ljava/lang/Class;)Z
    //   66: ifeq -25 -> 41
    //   69: aload_3
    //   70: aload_2
    //   71: invokeinterface 97 2 0
    //   76: pop
    //   77: goto -36 -> 41
    //   80: astore_1
    //   81: aload_0
    //   82: monitorexit
    //   83: aload_1
    //   84: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	85	0	this	TranscoderRegistry
    //   0	85	1	paramClass	Class<Z>
    //   0	85	2	paramClass1	Class<R>
    //   9	61	3	localArrayList	ArrayList
    //   39	13	4	localIterator	Iterator
    // Exception table:
    //   from	to	target	type
    //   2	26	80	finally
    //   30	41	80	finally
    //   41	77	80	finally
  }
  
  public <Z, R> void register(Class<Z> paramClass, Class<R> paramClass1, ResourceTranscoder<Z, R> paramResourceTranscoder)
  {
    try
    {
      this.transcoders.add(new Entry(paramClass, paramClass1, paramResourceTranscoder));
      return;
    }
    finally
    {
      paramClass = finally;
      throw paramClass;
    }
  }
  
  private static final class Entry<Z, R>
  {
    private final Class<Z> fromClass;
    private final Class<R> toClass;
    private final ResourceTranscoder<Z, R> transcoder;
    
    Entry(Class<Z> paramClass, Class<R> paramClass1, ResourceTranscoder<Z, R> paramResourceTranscoder)
    {
      this.fromClass = paramClass;
      this.toClass = paramClass1;
      this.transcoder = paramResourceTranscoder;
    }
    
    public boolean handles(Class<?> paramClass1, Class<?> paramClass2)
    {
      return (this.fromClass.isAssignableFrom(paramClass1)) && (paramClass2.isAssignableFrom(this.toClass));
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/transcode/TranscoderRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */