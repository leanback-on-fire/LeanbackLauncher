package com.google.android.gms.common.util;

import android.os.SystemClock;

public class zzh
  implements Clock
{
  private static zzh zzaUK = new zzh();
  
  public static Clock zzAM()
  {
    return zzaUK;
  }
  
  public long currentTimeMillis()
  {
    return System.currentTimeMillis();
  }
  
  public long elapsedRealtime()
  {
    return SystemClock.elapsedRealtime();
  }
  
  public long nanoTime()
  {
    return System.nanoTime();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/common/util/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */