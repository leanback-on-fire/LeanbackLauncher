package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.util.Util;

final class FixedSampleSizeRechunker
{
  private static final int MAX_SAMPLE_SIZE = 8192;
  
  public static Results rechunk(int paramInt, long[] paramArrayOfLong, int[] paramArrayOfInt, long paramLong)
  {
    int i1 = 8192 / paramInt;
    int i = 0;
    int k = paramArrayOfInt.length;
    int j = 0;
    while (j < k)
    {
      i += Util.ceilDivide(paramArrayOfInt[j], i1);
      j += 1;
    }
    long[] arrayOfLong1 = new long[i];
    int[] arrayOfInt1 = new int[i];
    int m = 0;
    long[] arrayOfLong2 = new long[i];
    int[] arrayOfInt2 = new int[i];
    k = 0;
    j = 0;
    i = 0;
    while (i < paramArrayOfInt.length)
    {
      int n = paramArrayOfInt[i];
      long l = paramArrayOfLong[i];
      while (n > 0)
      {
        int i2 = Math.min(i1, n);
        arrayOfLong1[j] = l;
        arrayOfInt1[j] = (paramInt * i2);
        m = Math.max(m, arrayOfInt1[j]);
        arrayOfLong2[j] = (k * paramLong);
        arrayOfInt2[j] = 1;
        l += arrayOfInt1[j];
        k += i2;
        n -= i2;
        j += 1;
      }
      i += 1;
    }
    return new Results(arrayOfLong1, arrayOfInt1, m, arrayOfLong2, arrayOfInt2, null);
  }
  
  public static final class Results
  {
    public final int[] flags;
    public final int maximumSize;
    public final long[] offsets;
    public final int[] sizes;
    public final long[] timestamps;
    
    private Results(long[] paramArrayOfLong1, int[] paramArrayOfInt1, int paramInt, long[] paramArrayOfLong2, int[] paramArrayOfInt2)
    {
      this.offsets = paramArrayOfLong1;
      this.sizes = paramArrayOfInt1;
      this.maximumSize = paramInt;
      this.timestamps = paramArrayOfLong2;
      this.flags = paramArrayOfInt2;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mp4/FixedSampleSizeRechunker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */