package com.google.android.exoplayer2.util;

import java.util.Arrays;

public final class LongArray
{
  private static final int DEFAULT_INITIAL_CAPACITY = 32;
  private int size;
  private long[] values;
  
  public LongArray()
  {
    this(32);
  }
  
  public LongArray(int paramInt)
  {
    this.values = new long[paramInt];
  }
  
  public void add(long paramLong)
  {
    if (this.size == this.values.length) {
      this.values = Arrays.copyOf(this.values, this.size * 2);
    }
    long[] arrayOfLong = this.values;
    int i = this.size;
    this.size = (i + 1);
    arrayOfLong[i] = paramLong;
  }
  
  public long get(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.size)) {
      throw new IndexOutOfBoundsException("Invalid index " + paramInt + ", size is " + this.size);
    }
    return this.values[paramInt];
  }
  
  public int size()
  {
    return this.size;
  }
  
  public long[] toArray()
  {
    return Arrays.copyOf(this.values, this.size);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/LongArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */