package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;

abstract class zzatn
{
  private static volatile Handler zzaeM;
  private volatile long zzaeN;
  private final zzauh zzbLa;
  private boolean zzbMp;
  private final Runnable zzw;
  
  zzatn(zzauh paramzzauh)
  {
    zzac.zzC(paramzzauh);
    this.zzbLa = paramzzauh;
    this.zzbMp = true;
    this.zzw = new Runnable()
    {
      public void run()
      {
        if (Looper.myLooper() == Looper.getMainLooper()) {
          zzatn.zza(zzatn.this).zzMf().zzp(this);
        }
        boolean bool;
        do
        {
          return;
          bool = zzatn.this.zzcJ();
          zzatn.zza(zzatn.this, 0L);
        } while ((!bool) || (!zzatn.zzb(zzatn.this)));
        zzatn.this.run();
      }
    };
  }
  
  private Handler getHandler()
  {
    if (zzaeM != null) {
      return zzaeM;
    }
    try
    {
      if (zzaeM == null) {
        zzaeM = new Handler(this.zzbLa.getContext().getMainLooper());
      }
      Handler localHandler = zzaeM;
      return localHandler;
    }
    finally {}
  }
  
  public void cancel()
  {
    this.zzaeN = 0L;
    getHandler().removeCallbacks(this.zzw);
  }
  
  public abstract void run();
  
  public void zzC(long paramLong)
  {
    cancel();
    if (paramLong >= 0L)
    {
      this.zzaeN = this.zzbLa.zznq().currentTimeMillis();
      if (!getHandler().postDelayed(this.zzw, paramLong)) {
        this.zzbLa.zzMg().zzNT().zzm("Failed to schedule delayed post. time", Long.valueOf(paramLong));
      }
    }
  }
  
  public boolean zzcJ()
  {
    return this.zzaeN != 0L;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */