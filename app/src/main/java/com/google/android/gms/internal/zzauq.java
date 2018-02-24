package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.Clock;

public class zzauq
  extends zzauk
{
  private Handler mHandler;
  protected long zzbQQ;
  private final zzatn zzbQR = new zzatn(this.zzbLa)
  {
    @WorkerThread
    public void run()
    {
      zzauq.this.zzPa();
    }
  };
  private final zzatn zzbQS = new zzatn(this.zzbLa)
  {
    @WorkerThread
    public void run()
    {
      zzauq.zza(zzauq.this);
    }
  };
  
  zzauq(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  private void zzOY()
  {
    try
    {
      if (this.mHandler == null) {
        this.mHandler = new Handler(Looper.getMainLooper());
      }
      return;
    }
    finally {}
  }
  
  @WorkerThread
  private void zzPb()
  {
    zzmW();
    zzaX(false);
    zzLT().zzah(zznq().elapsedRealtime());
  }
  
  @WorkerThread
  private void zzaD(long paramLong)
  {
    zzmW();
    zzOY();
    this.zzbQR.cancel();
    this.zzbQS.cancel();
    zzMg().zzNZ().zzm("Activity resumed, time", Long.valueOf(paramLong));
    this.zzbQQ = paramLong;
    if (zznq().currentTimeMillis() - zzMh().zzbOi.get() > zzMh().zzbOk.get())
    {
      zzMh().zzbOj.set(true);
      zzMh().zzbOl.set(0L);
    }
    if (zzMh().zzbOj.get())
    {
      this.zzbQR.zzC(Math.max(0L, zzMh().zzbOh.get() - zzMh().zzbOl.get()));
      return;
    }
    this.zzbQS.zzC(Math.max(0L, 3600000L - zzMh().zzbOl.get()));
  }
  
  @WorkerThread
  private void zzaE(long paramLong)
  {
    zzmW();
    zzOY();
    this.zzbQR.cancel();
    this.zzbQS.cancel();
    zzMg().zzNZ().zzm("Activity paused, time", Long.valueOf(paramLong));
    if (this.zzbQQ != 0L) {
      zzMh().zzbOl.set(zzMh().zzbOl.get() + (paramLong - this.zzbQQ));
    }
    zzMh().zzbOk.set(zznq().currentTimeMillis());
  }
  
  protected void onInitialize() {}
  
  @MainThread
  protected void zzOX()
  {
    final long l = zznq().elapsedRealtime();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzauq.zza(zzauq.this, l);
      }
    });
  }
  
  @MainThread
  protected void zzOZ()
  {
    final long l = zznq().elapsedRealtime();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzauq.zzb(zzauq.this, l);
      }
    });
  }
  
  @WorkerThread
  protected void zzPa()
  {
    zzmW();
    long l = zznq().elapsedRealtime();
    zzMg().zzNZ().zzm("Session started, time", Long.valueOf(l));
    zzMh().zzbOj.set(false);
    zzLV().zzd("auto", "_s", new Bundle());
  }
  
  @WorkerThread
  public boolean zzaX(boolean paramBoolean)
  {
    zzmW();
    zznA();
    long l1 = zznq().elapsedRealtime();
    if (this.zzbQQ == 0L) {
      this.zzbQQ = (l1 - 3600000L);
    }
    long l2 = l1 - this.zzbQQ;
    if ((!paramBoolean) && (l2 < 1000L))
    {
      zzMg().zzNZ().zzm("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(l2));
      return false;
    }
    zzMh().zzbOl.set(l2);
    zzMg().zzNZ().zzm("Recording user engagement, ms", Long.valueOf(l2));
    Bundle localBundle = new Bundle();
    localBundle.putLong("_et", l2);
    zzaun.zza(zzLZ().zzOP(), localBundle);
    zzLV().zzd("auto", "_e", localBundle);
    this.zzbQQ = l1;
    this.zzbQS.cancel();
    this.zzbQS.zzC(Math.max(0L, 3600000L - zzMh().zzbOl.get()));
    return true;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */