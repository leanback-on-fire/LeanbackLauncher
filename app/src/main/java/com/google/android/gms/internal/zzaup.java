package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;

public final class zzaup
{
  private final Context mContext;
  private final Handler mHandler;
  private final zza zzbQN;
  
  public zzaup(zza paramzza)
  {
    this.mContext = paramzza.getContext();
    zzac.zzC(this.mContext);
    this.zzbQN = paramzza;
    this.mHandler = new Handler();
  }
  
  private zzaua zzMg()
  {
    return zzauh.zzbR(this.mContext).zzMg();
  }
  
  public static boolean zzj(Context paramContext, boolean paramBoolean)
  {
    zzac.zzC(paramContext);
    if (paramBoolean) {}
    for (String str = "com.google.android.gms.measurement.PackageMeasurementService";; str = "com.google.android.gms.measurement.AppMeasurementService") {
      return zzauw.zzy(paramContext, str);
    }
  }
  
  @MainThread
  public IBinder onBind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzMg().zzNT().log("onBind called with null intent");
      return null;
    }
    paramIntent = paramIntent.getAction();
    if ("com.google.android.gms.measurement.START".equals(paramIntent)) {
      return new zzaui(zzauh.zzbR(this.mContext));
    }
    zzMg().zzNV().zzm("onBind received unknown action", paramIntent);
    return null;
  }
  
  @MainThread
  public void onCreate()
  {
    zzauh localzzauh = zzauh.zzbR(this.mContext);
    zzaua localzzaua = localzzauh.zzMg();
    localzzauh.zzMi().zzNb();
    localzzaua.zzNZ().log("Local AppMeasurementService is starting up");
  }
  
  @MainThread
  public void onDestroy()
  {
    zzauh localzzauh = zzauh.zzbR(this.mContext);
    zzaua localzzaua = localzzauh.zzMg();
    localzzauh.zzMi().zzNb();
    localzzaua.zzNZ().log("Local AppMeasurementService is shutting down");
  }
  
  @MainThread
  public void onRebind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzMg().zzNT().log("onRebind called with null intent");
      return;
    }
    paramIntent = paramIntent.getAction();
    zzMg().zzNZ().zzm("onRebind called. action", paramIntent);
  }
  
  @MainThread
  public int onStartCommand(Intent paramIntent, int paramInt1, final int paramInt2)
  {
    final zzauh localzzauh = zzauh.zzbR(this.mContext);
    final zzaua localzzaua = localzzauh.zzMg();
    if (paramIntent == null) {
      localzzaua.zzNV().log("AppMeasurementService started with null intent");
    }
    do
    {
      return 2;
      paramIntent = paramIntent.getAction();
      localzzauh.zzMi().zzNb();
      localzzaua.zzNZ().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(paramInt2), paramIntent);
    } while (!"com.google.android.gms.measurement.UPLOAD".equals(paramIntent));
    localzzauh.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        localzzauh.zzOG();
        localzzauh.zzOB();
        zzaup.zzb(zzaup.this).post(new Runnable()
        {
          public void run()
          {
            if (zzaup.zza(zzaup.this).callServiceStopSelfResult(zzaup.1.this.zzabl))
            {
              zzaup.1.this.zzbOv.zzMi().zzNb();
              zzaup.1.this.zzbOy.zzNZ().log("Local AppMeasurementService processed last upload request");
            }
          }
        });
      }
    });
    return 2;
  }
  
  @MainThread
  public boolean onUnbind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzMg().zzNT().log("onUnbind called with null intent");
      return true;
    }
    paramIntent = paramIntent.getAction();
    zzMg().zzNZ().zzm("onUnbind called for intent. action", paramIntent);
    return true;
  }
  
  public static abstract interface zza
  {
    public abstract boolean callServiceStopSelfResult(int paramInt);
    
    public abstract Context getContext();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */