package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceEncoder;
import java.util.ArrayList;
import java.util.List;

public class ResourceEncoderRegistry
{
  final List<Entry<?>> encoders = new ArrayList();
  
  public <Z> void add(Class<Z> paramClass, ResourceEncoder<Z> paramResourceEncoder)
  {
    try
    {
      this.encoders.add(new Entry(paramClass, paramResourceEncoder));
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
  public <Z> ResourceEncoder<Z> get(Class<Z> paramClass)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 19	com/bumptech/glide/provider/ResourceEncoderRegistry:encoders	Ljava/util/List;
    //   6: invokeinterface 38 1 0
    //   11: istore_3
    //   12: iconst_0
    //   13: istore_2
    //   14: iload_2
    //   15: iload_3
    //   16: if_icmpge +44 -> 60
    //   19: aload_0
    //   20: getfield 19	com/bumptech/glide/provider/ResourceEncoderRegistry:encoders	Ljava/util/List;
    //   23: iload_2
    //   24: invokeinterface 41 2 0
    //   29: checkcast 6	com/bumptech/glide/provider/ResourceEncoderRegistry$Entry
    //   32: astore 4
    //   34: aload 4
    //   36: aload_1
    //   37: invokestatic 45	com/bumptech/glide/provider/ResourceEncoderRegistry$Entry:access$000	(Lcom/bumptech/glide/provider/ResourceEncoderRegistry$Entry;Ljava/lang/Class;)Z
    //   40: ifeq +13 -> 53
    //   43: aload 4
    //   45: invokestatic 49	com/bumptech/glide/provider/ResourceEncoderRegistry$Entry:access$100	(Lcom/bumptech/glide/provider/ResourceEncoderRegistry$Entry;)Lcom/bumptech/glide/load/ResourceEncoder;
    //   48: astore_1
    //   49: aload_0
    //   50: monitorexit
    //   51: aload_1
    //   52: areturn
    //   53: iload_2
    //   54: iconst_1
    //   55: iadd
    //   56: istore_2
    //   57: goto -43 -> 14
    //   60: aconst_null
    //   61: astore_1
    //   62: goto -13 -> 49
    //   65: astore_1
    //   66: aload_0
    //   67: monitorexit
    //   68: aload_1
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	this	ResourceEncoderRegistry
    //   0	70	1	paramClass	Class<Z>
    //   13	44	2	i	int
    //   11	6	3	j	int
    //   32	12	4	localEntry	Entry
    // Exception table:
    //   from	to	target	type
    //   2	12	65	finally
    //   19	49	65	finally
  }
  
  private static final class Entry<T>
  {
    private final ResourceEncoder<T> encoder;
    private final Class<T> resourceClass;
    
    Entry(Class<T> paramClass, ResourceEncoder<T> paramResourceEncoder)
    {
      this.resourceClass = paramClass;
      this.encoder = paramResourceEncoder;
    }
    
    private boolean handles(Class<?> paramClass)
    {
      return this.resourceClass.isAssignableFrom(paramClass);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/provider/ResourceEncoderRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */