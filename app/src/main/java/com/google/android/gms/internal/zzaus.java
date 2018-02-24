package com.google.android.gms.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.Clock;

public class zzaus
  extends zzauk
{
  private boolean zzaeQ;
  private final AlarmManager zzaeR = (AlarmManager)getContext().getSystemService("alarm");
  private final zzatn zzbQU;
  
  protected zzaus(zzauh paramzzauh)
  {
    super(paramzzauh);
    this.zzbQU = new zzatn(paramzzauh)
    {
      public void run()
      {
        zzaus.zza(zzaus.this);
      }
    };
  }
  
  private void zzPc()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    zzMi().zzNb();
    localIntent = localIntent.setClassName(localContext, "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    getContext().sendBroadcast(localIntent);
  }
  
  private PendingIntent zzpd()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    zzMi().zzNb();
    localIntent = localIntent.setClassName(localContext, "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    return PendingIntent.getBroadcast(getContext(), 0, localIntent, 0);
  }
  
  public void cancel()
  {
    zznA();
    this.zzaeQ = false;
    this.zzaeR.cancel(zzpd());
    this.zzbQU.cancel();
  }
  
  protected void onInitialize()
  {
    this.zzaeR.cancel(zzpd());
  }
  
  public void zzC(long paramLong)
  {
    zznA();
    zzMi().zzNb();
    if (!zzaue.zzi(getContext(), false)) {
      zzMg().zzNY().log("Receiver not registered/enabled");
    }
    zzMi().zzNb();
    if (!zzaup.zzj(getContext(), false)) {
      zzMg().zzNY().log("Service not registered/enabled");
    }
    cancel();
    long l = zznq().elapsedRealtime();
    this.zzaeQ = true;
    if ((paramLong < zzMi().zzNs()) && (!this.zzbQU.zzcJ())) {
      this.zzbQU.zzC(paramLong);
    }
    this.zzaeR.setInexactRepeating(2, l + paramLong, Math.max(zzMi().zzNt(), paramLong), zzpd());
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */