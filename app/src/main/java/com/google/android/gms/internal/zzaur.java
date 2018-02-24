package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;

class zzaur
{
  private long zzLe;
  private final Clock zzvi;
  
  public zzaur(Clock paramClock)
  {
    zzac.zzC(paramClock);
    this.zzvi = paramClock;
  }
  
  public void clear()
  {
    this.zzLe = 0L;
  }
  
  public void start()
  {
    this.zzLe = this.zzvi.elapsedRealtime();
  }
  
  public boolean zzE(long paramLong)
  {
    if (this.zzLe == 0L) {}
    while (this.zzvi.elapsedRealtime() - this.zzLe >= paramLong) {
      return true;
    }
    return false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaur.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */