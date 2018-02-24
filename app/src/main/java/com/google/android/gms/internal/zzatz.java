package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;

public class zzatz
  extends zzf<zzatw>
{
  public zzatz(Context paramContext, Looper paramLooper, zzf.zzb paramzzb, zzf.zzc paramzzc)
  {
    super(paramContext, paramLooper, 93, paramzzb, paramzzc, null);
  }
  
  @NonNull
  protected String zzeJ()
  {
    return "com.google.android.gms.measurement.START";
  }
  
  @NonNull
  protected String zzeK()
  {
    return "com.google.android.gms.measurement.internal.IMeasurementService";
  }
  
  public zzatw zzgQ(IBinder paramIBinder)
  {
    return zzatw.zza.zzgP(paramIBinder);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */