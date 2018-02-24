package com.bumptech.glide.provider;

import com.bumptech.glide.load.Encoder;
import java.util.ArrayList;
import java.util.List;

public class EncoderRegistry
{
  private final List<Entry<?>> encoders = new ArrayList();
  
  public <T> void add(Class<T> paramClass, Encoder<T> paramEncoder)
  {
    try
    {
      this.encoders.add(new Entry(paramClass, paramEncoder));
      return;
    }
    finally
    {
      paramClass = finally;
      throw paramClass;
    }
  }
  
  /* Error */
  @android.support.annotation.Nullable
  public <T> Encoder<T> getEncoder(Class<T> paramClass)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 19	com/bumptech/glide/provider/EncoderRegistry:encoders	Ljava/util/List;
    //   6: invokeinterface 38 1 0
    //   11: astore_2
    //   12: aload_2
    //   13: invokeinterface 44 1 0
    //   18: ifeq +30 -> 48
    //   21: aload_2
    //   22: invokeinterface 48 1 0
    //   27: checkcast 6	com/bumptech/glide/provider/EncoderRegistry$Entry
    //   30: astore_3
    //   31: aload_3
    //   32: aload_1
    //   33: invokevirtual 52	com/bumptech/glide/provider/EncoderRegistry$Entry:handles	(Ljava/lang/Class;)Z
    //   36: ifeq -24 -> 12
    //   39: aload_3
    //   40: invokestatic 56	com/bumptech/glide/provider/EncoderRegistry$Entry:access$000	(Lcom/bumptech/glide/provider/EncoderRegistry$Entry;)Lcom/bumptech/glide/load/Encoder;
    //   43: astore_1
    //   44: aload_0
    //   45: monitorexit
    //   46: aload_1
    //   47: areturn
    //   48: aconst_null
    //   49: astore_1
    //   50: goto -6 -> 44
    //   53: astore_1
    //   54: aload_0
    //   55: monitorexit
    //   56: aload_1
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	this	EncoderRegistry
    //   0	58	1	paramClass	Class<T>
    //   11	11	2	localIterator	java.util.Iterator
    //   30	10	3	localEntry	Entry
    // Exception table:
    //   from	to	target	type
    //   2	12	53	finally
    //   12	44	53	finally
  }
  
  private static final class Entry<T>
  {
    private final Class<T> dataClass;
    private final Encoder<T> encoder;
    
    public Entry(Class<T> paramClass, Encoder<T> paramEncoder)
    {
      this.dataClass = paramClass;
      this.encoder = paramEncoder;
    }
    
    public boolean handles(Class<?> paramClass)
    {
      return this.dataClass.isAssignableFrom(paramClass);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/provider/EncoderRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */