package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zza;

public class zzza
  extends zzys
{
  private zzzl zzaKC;
  private final zza<zzyn<?>> zzaMK = new zza();
  
  private zzza(zzzt paramzzzt)
  {
    super(paramzzzt);
    this.zzaOu.zza("ConnectionlessLifecycleHelper", this);
  }
  
  public static void zza(Activity paramActivity, zzzl paramzzzl, zzyn<?> paramzzyn)
  {
    zzzt localzzzt = zzt(paramActivity);
    zzza localzzza = (zzza)localzzzt.zza("ConnectionlessLifecycleHelper", zzza.class);
    paramActivity = localzzza;
    if (localzzza == null) {
      paramActivity = new zzza(localzzzt);
    }
    paramActivity.zzaKC = paramzzzl;
    paramActivity.zza(paramzzyn);
    paramzzzl.zza(paramActivity);
  }
  
  private void zza(zzyn<?> paramzzyn)
  {
    zzac.zzb(paramzzyn, "ApiKey cannot be null");
    this.zzaMK.add(paramzzyn);
  }
  
  public void onStart()
  {
    super.onStart();
    if (!this.zzaMK.isEmpty()) {
      this.zzaKC.zza(this);
    }
  }
  
  public void onStop()
  {
    super.onStop();
    this.zzaKC.zzb(this);
  }
  
  protected void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    this.zzaKC.zza(paramConnectionResult, paramInt);
  }
  
  zza<zzyn<?>> zzxN()
  {
    return this.zzaMK;
  }
  
  protected void zzxj()
  {
    this.zzaKC.zzxj();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */