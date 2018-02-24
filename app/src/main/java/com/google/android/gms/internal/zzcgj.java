package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcgj
{
  static final int Gj = zzaf(1, 3);
  static final int Gk = zzaf(1, 4);
  static final int Gl = zzaf(2, 0);
  static final int Gm = zzaf(3, 2);
  public static final int[] Gn = new int[0];
  public static final long[] Go = new long[0];
  public static final float[] Gp = new float[0];
  public static final double[] Gq = new double[0];
  public static final boolean[] Gr = new boolean[0];
  public static final String[] Gs = new String[0];
  public static final byte[][] Gt = new byte[0][];
  public static final byte[] Gu = new byte[0];
  
  static int zzBN(int paramInt)
  {
    return paramInt & 0x7;
  }
  
  public static int zzBO(int paramInt)
  {
    return paramInt >>> 3;
  }
  
  public static int zzaf(int paramInt1, int paramInt2)
  {
    return paramInt1 << 3 | paramInt2;
  }
  
  public static final int zzb(zzcfx paramzzcfx, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramzzcfx.getPosition();
    paramzzcfx.zzBt(paramInt);
    while (paramzzcfx.zzamI() == paramInt)
    {
      paramzzcfx.zzBt(paramInt);
      i += 1;
    }
    paramzzcfx.zzBx(j);
    return i;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcgj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */