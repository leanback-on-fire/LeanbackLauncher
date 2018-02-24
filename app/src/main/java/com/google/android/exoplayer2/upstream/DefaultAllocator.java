package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class DefaultAllocator
  implements Allocator
{
  private static final int AVAILABLE_EXTRA_CAPACITY = 100;
  private int allocatedCount;
  private Allocation[] availableAllocations;
  private int availableCount;
  private final int individualAllocationSize;
  private final byte[] initialAllocationBlock;
  private final Allocation[] singleAllocationReleaseHolder;
  private int targetBufferSize;
  private final boolean trimOnReset;
  
  public DefaultAllocator(boolean paramBoolean, int paramInt)
  {
    this(paramBoolean, paramInt, 0);
  }
  
  public DefaultAllocator(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (paramInt1 > 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      Assertions.checkArgument(bool1);
      bool1 = bool2;
      if (paramInt2 >= 0) {
        bool1 = true;
      }
      Assertions.checkArgument(bool1);
      this.trimOnReset = paramBoolean;
      this.individualAllocationSize = paramInt1;
      this.availableCount = paramInt2;
      this.availableAllocations = new Allocation[paramInt2 + 100];
      if (paramInt2 <= 0) {
        break;
      }
      this.initialAllocationBlock = new byte[paramInt2 * paramInt1];
      int i = 0;
      while (i < paramInt2)
      {
        this.availableAllocations[i] = new Allocation(this.initialAllocationBlock, i * paramInt1);
        i += 1;
      }
    }
    this.initialAllocationBlock = null;
    this.singleAllocationReleaseHolder = new Allocation[1];
  }
  
  /* Error */
  public Allocation allocate()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_0
    //   4: getfield 56	com/google/android/exoplayer2/upstream/DefaultAllocator:allocatedCount	I
    //   7: iconst_1
    //   8: iadd
    //   9: putfield 56	com/google/android/exoplayer2/upstream/DefaultAllocator:allocatedCount	I
    //   12: aload_0
    //   13: getfield 41	com/google/android/exoplayer2/upstream/DefaultAllocator:availableCount	I
    //   16: ifle +38 -> 54
    //   19: aload_0
    //   20: getfield 45	com/google/android/exoplayer2/upstream/DefaultAllocator:availableAllocations	[Lcom/google/android/exoplayer2/upstream/Allocation;
    //   23: astore_2
    //   24: aload_0
    //   25: getfield 41	com/google/android/exoplayer2/upstream/DefaultAllocator:availableCount	I
    //   28: iconst_1
    //   29: isub
    //   30: istore_1
    //   31: aload_0
    //   32: iload_1
    //   33: putfield 41	com/google/android/exoplayer2/upstream/DefaultAllocator:availableCount	I
    //   36: aload_2
    //   37: iload_1
    //   38: aaload
    //   39: astore_2
    //   40: aload_0
    //   41: getfield 45	com/google/android/exoplayer2/upstream/DefaultAllocator:availableAllocations	[Lcom/google/android/exoplayer2/upstream/Allocation;
    //   44: aload_0
    //   45: getfield 41	com/google/android/exoplayer2/upstream/DefaultAllocator:availableCount	I
    //   48: aconst_null
    //   49: aastore
    //   50: aload_0
    //   51: monitorexit
    //   52: aload_2
    //   53: areturn
    //   54: new 43	com/google/android/exoplayer2/upstream/Allocation
    //   57: dup
    //   58: aload_0
    //   59: getfield 39	com/google/android/exoplayer2/upstream/DefaultAllocator:individualAllocationSize	I
    //   62: newarray <illegal type>
    //   64: iconst_0
    //   65: invokespecial 50	com/google/android/exoplayer2/upstream/Allocation:<init>	([BI)V
    //   68: astore_2
    //   69: goto -19 -> 50
    //   72: astore_2
    //   73: aload_0
    //   74: monitorexit
    //   75: aload_2
    //   76: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	77	0	this	DefaultAllocator
    //   30	8	1	i	int
    //   23	46	2	localObject1	Object
    //   72	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	36	72	finally
    //   40	50	72	finally
    //   54	69	72	finally
  }
  
  public int getIndividualAllocationLength()
  {
    return this.individualAllocationSize;
  }
  
  public int getTotalBytesAllocated()
  {
    try
    {
      int i = this.allocatedCount;
      int j = this.individualAllocationSize;
      return i * j;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void release(Allocation paramAllocation)
  {
    try
    {
      this.singleAllocationReleaseHolder[0] = paramAllocation;
      release(this.singleAllocationReleaseHolder);
      return;
    }
    finally
    {
      paramAllocation = finally;
      throw paramAllocation;
    }
  }
  
  public void release(Allocation[] paramArrayOfAllocation)
  {
    for (;;)
    {
      try
      {
        if (this.availableCount + paramArrayOfAllocation.length >= this.availableAllocations.length) {
          this.availableAllocations = ((Allocation[])Arrays.copyOf(this.availableAllocations, Math.max(this.availableAllocations.length * 2, this.availableCount + paramArrayOfAllocation.length)));
        }
        int j = paramArrayOfAllocation.length;
        int i = 0;
        if (i < j)
        {
          Allocation localAllocation = paramArrayOfAllocation[i];
          if (localAllocation.data != this.initialAllocationBlock)
          {
            if (localAllocation.data.length != this.individualAllocationSize) {
              break label159;
            }
            break label153;
            Assertions.checkArgument(bool);
            Allocation[] arrayOfAllocation = this.availableAllocations;
            int k = this.availableCount;
            this.availableCount = (k + 1);
            arrayOfAllocation[k] = localAllocation;
            i += 1;
          }
        }
        else
        {
          this.allocatedCount -= paramArrayOfAllocation.length;
          notifyAll();
          return;
        }
      }
      finally {}
      label153:
      boolean bool = true;
      continue;
      label159:
      bool = false;
    }
  }
  
  public void reset()
  {
    try
    {
      if (this.trimOnReset) {
        setTargetBufferSize(0);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void setTargetBufferSize(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: iload_1
    //   3: aload_0
    //   4: getfield 90	com/google/android/exoplayer2/upstream/DefaultAllocator:targetBufferSize	I
    //   7: if_icmpge +21 -> 28
    //   10: iconst_1
    //   11: istore_2
    //   12: aload_0
    //   13: iload_1
    //   14: putfield 90	com/google/android/exoplayer2/upstream/DefaultAllocator:targetBufferSize	I
    //   17: iload_2
    //   18: ifeq +7 -> 25
    //   21: aload_0
    //   22: invokevirtual 93	com/google/android/exoplayer2/upstream/DefaultAllocator:trim	()V
    //   25: aload_0
    //   26: monitorexit
    //   27: return
    //   28: iconst_0
    //   29: istore_2
    //   30: goto -18 -> 12
    //   33: astore_3
    //   34: aload_0
    //   35: monitorexit
    //   36: aload_3
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	this	DefaultAllocator
    //   0	38	1	paramInt	int
    //   11	19	2	i	int
    //   33	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	10	33	finally
    //   12	17	33	finally
    //   21	25	33	finally
  }
  
  public void trim()
  {
    for (;;)
    {
      int k;
      int j;
      try
      {
        k = Math.max(0, Util.ceilDivide(this.targetBufferSize, this.individualAllocationSize) - this.allocatedCount);
        i = this.availableCount;
        if (k >= i) {
          return;
        }
        i = k;
        if (this.initialAllocationBlock == null) {
          break label175;
        }
        i = this.availableCount - 1;
        j = 0;
        if (j <= i)
        {
          Allocation localAllocation = this.availableAllocations[j];
          if (localAllocation.data == this.initialAllocationBlock)
          {
            j += 1;
          }
          else
          {
            Object localObject2 = this.availableAllocations[i];
            if (((Allocation)localObject2).data != this.initialAllocationBlock)
            {
              i -= 1;
            }
            else
            {
              Allocation[] arrayOfAllocation = this.availableAllocations;
              int m = j + 1;
              arrayOfAllocation[j] = localObject2;
              localObject2 = this.availableAllocations;
              j = i - 1;
              localObject2[i] = localAllocation;
              i = j;
              j = m;
            }
          }
        }
      }
      finally {}
      int i = Math.max(k, j);
      if (i < this.availableCount)
      {
        label175:
        Arrays.fill(this.availableAllocations, i, this.availableCount, null);
        this.availableCount = i;
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/DefaultAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */