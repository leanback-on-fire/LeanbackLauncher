package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;

public class zzbck
  extends zzl<zzbci>
{
  public zzbck(Context paramContext, Looper paramLooper, zzg paramzzg, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 51, paramzzg, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  protected String zzeJ()
  {
    return "com.google.android.gms.phenotype.service.START";
  }
  
  protected String zzeK()
  {
    return "com.google.android.gms.phenotype.internal.IPhenotypeService";
  }
  
  protected zzbci zzhv(IBinder paramIBinder)
  {
    return zzbci.zza.zzhu(paramIBinder);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */