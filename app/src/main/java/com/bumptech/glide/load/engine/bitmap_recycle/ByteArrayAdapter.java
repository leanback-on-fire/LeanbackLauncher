package com.bumptech.glide.load.engine.bitmap_recycle;

import java.util.Arrays;

public final class ByteArrayAdapter
  implements ArrayAdapterInterface<byte[]>
{
  private static final String TAG = "ByteArrayPool";
  
  public int getArrayLength(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte.length;
  }
  
  public int getElementSizeInBytes()
  {
    return 1;
  }
  
  public String getTag()
  {
    return "ByteArrayPool";
  }
  
  public byte[] newArray(int paramInt)
  {
    return new byte[paramInt];
  }
  
  public void resetArray(byte[] paramArrayOfByte)
  {
    Arrays.fill(paramArrayOfByte, (byte)0);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/ByteArrayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */