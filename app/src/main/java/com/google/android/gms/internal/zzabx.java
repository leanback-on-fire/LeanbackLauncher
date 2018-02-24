package com.google.android.gms.internal;

import android.os.Process;

class zzabx
  implements Runnable
{
  private final int mPriority;
  private final Runnable zzw;
  
  public zzabx(Runnable paramRunnable, int paramInt)
  {
    this.zzw = paramRunnable;
    this.mPriority = paramInt;
  }
  
  public void run()
  {
    Process.setThreadPriority(this.mPriority);
    this.zzw.run();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzabx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */