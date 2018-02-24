package com.bumptech.glide.load.engine.bitmap_recycle;

import java.util.Arrays;

public final class IntegerArrayAdapter
  implements ArrayAdapterInterface<int[]>
{
  private static final String TAG = "IntegerArrayPool";
  
  public int getArrayLength(int[] paramArrayOfInt)
  {
    return paramArrayOfInt.length;
  }
  
  public int getElementSizeInBytes()
  {
    return 4;
  }
  
  public String getTag()
  {
    return "IntegerArrayPool";
  }
  
  public int[] newArray(int paramInt)
  {
    return new int[paramInt];
  }
  
  public void resetArray(int[] paramArrayOfInt)
  {
    Arrays.fill(paramArrayOfInt, 0);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/IntegerArrayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */