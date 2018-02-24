package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;

public class zzacg
  extends zzl<zzacq>
{
  public zzacg(Context paramContext, Looper paramLooper, zzg paramzzg, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 64, paramzzg, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  protected zzacq zzdr(IBinder paramIBinder)
  {
    return zzacq.zza.zzdt(paramIBinder);
  }
  
  protected String zzeJ()
  {
    return "com.google.android.gms.config.START";
  }
  
  protected String zzeK()
  {
    return "com.google.android.gms.config.internal.IConfigService";
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */