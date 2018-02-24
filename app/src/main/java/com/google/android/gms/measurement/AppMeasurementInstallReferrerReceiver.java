package com.google.android.gms.measurement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import com.google.android.gms.internal.zzaue;
import com.google.android.gms.internal.zzaue.zza;

public final class AppMeasurementInstallReferrerReceiver
  extends BroadcastReceiver
  implements zzaue.zza
{
  private zzaue zzbLg;
  
  private zzaue zzLN()
  {
    if (this.zzbLg == null) {
      this.zzbLg = new zzaue(this);
    }
    return this.zzbLg;
  }
  
  public void doStartService(Context paramContext, Intent paramIntent) {}
  
  @MainThread
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    zzLN().onReceive(paramContext, paramIntent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/measurement/AppMeasurementInstallReferrerReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */