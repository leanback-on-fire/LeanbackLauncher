package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzaue
{
  private final zza zzbOu;
  
  public zzaue(zza paramzza)
  {
    zzac.zzC(paramzza);
    this.zzbOu = paramzza;
  }
  
  public static boolean zzi(Context paramContext, boolean paramBoolean)
  {
    zzac.zzC(paramContext);
    if (paramBoolean) {}
    for (String str = "com.google.android.gms.measurement.PackageMeasurementReceiver";; str = "com.google.android.gms.measurement.AppMeasurementReceiver") {
      return zzauw.zza(paramContext, str, false);
    }
  }
  
  @MainThread
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    final zzauh localzzauh = zzauh.zzbR(paramContext);
    final zzaua localzzaua = localzzauh.zzMg();
    if (paramIntent == null) {
      localzzaua.zzNV().log("Receiver called with null intent");
    }
    do
    {
      return;
      localzzauh.zzMi().zzNb();
      localObject = paramIntent.getAction();
      localzzaua.zzNZ().zzm("Local receiver got", localObject);
      if ("com.google.android.gms.measurement.UPLOAD".equals(localObject))
      {
        zzaup.zzj(paramContext, false);
        paramIntent = new Intent().setClassName(paramContext, "com.google.android.gms.measurement.AppMeasurementService");
        paramIntent.setAction("com.google.android.gms.measurement.UPLOAD");
        this.zzbOu.doStartService(paramContext, paramIntent);
        return;
      }
    } while (!"com.android.vending.INSTALL_REFERRER".equals(localObject));
    Object localObject = paramIntent.getStringExtra("referrer");
    if (localObject == null)
    {
      localzzaua.zzNZ().log("Install referrer extras are null");
      return;
    }
    localObject = Uri.parse((String)localObject);
    localObject = localzzauh.zzMc().zzx((Uri)localObject);
    if (localObject == null)
    {
      localzzaua.zzNZ().log("No campaign defined in install referrer broadcast");
      return;
    }
    final long l = 1000L * paramIntent.getLongExtra("referrer_timestamp_seconds", 0L);
    if (l == 0L) {
      localzzaua.zzNV().log("Install referrer is missing timestamp");
    }
    localzzauh.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzauv localzzauv = localzzauh.zzMb().zzac(localzzauh.zzLW().getAppId(), "_fot");
        if ((localzzauv != null) && ((localzzauv.mValue instanceof Long))) {}
        for (long l1 = ((Long)localzzauv.mValue).longValue();; l1 = 0L)
        {
          long l2 = l;
          if ((l1 > 0L) && ((l2 >= l1) || (l2 <= 0L))) {}
          for (l1 -= 1L;; l1 = l2)
          {
            if (l1 > 0L) {
              paramContext.putLong("click_timestamp", l1);
            }
            AppMeasurement.getInstance(localzzaua).logEventInternal("auto", "_cmp", paramContext);
            this.zzbOy.zzNZ().log("Install campaign recorded");
            return;
          }
        }
      }
    });
  }
  
  public static abstract interface zza
  {
    public abstract void doStartService(Context paramContext, Intent paramIntent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */