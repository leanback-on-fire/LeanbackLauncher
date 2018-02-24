package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;

class zzauc
  extends BroadcastReceiver
{
  static final String zzafo = zzauc.class.getName();
  private boolean zzafp;
  private boolean zzafq;
  private final zzauh zzbLa;
  
  zzauc(zzauh paramzzauh)
  {
    zzac.zzC(paramzzauh);
    this.zzbLa = paramzzauh;
  }
  
  private Context getContext()
  {
    return this.zzbLa.getContext();
  }
  
  private zzaua zzMg()
  {
    return this.zzbLa.zzMg();
  }
  
  @WorkerThread
  public boolean isRegistered()
  {
    this.zzbLa.zzmW();
    return this.zzafp;
  }
  
  @MainThread
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.zzbLa.zznA();
    paramContext = paramIntent.getAction();
    zzMg().zzNZ().zzm("NetworkBroadcastReceiver received action", paramContext);
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext))
    {
      final boolean bool = this.zzbLa.zzOt().isNetworkConnected();
      if (this.zzafq != bool)
      {
        this.zzafq = bool;
        this.zzbLa.zzMf().zzp(new Runnable()
        {
          public void run()
          {
            zzauc.zza(zzauc.this).zzR(bool);
          }
        });
      }
      return;
    }
    zzMg().zzNV().zzm("NetworkBroadcastReceiver received unknown action", paramContext);
  }
  
  @WorkerThread
  public void unregister()
  {
    this.zzbLa.zznA();
    this.zzbLa.zzmW();
    if (!isRegistered()) {
      return;
    }
    zzMg().zzNZ().log("Unregistering connectivity change receiver");
    this.zzafp = false;
    this.zzafq = false;
    Context localContext = getContext();
    try
    {
      localContext.unregisterReceiver(this);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      zzMg().zzNT().zzm("Failed to unregister the network broadcast receiver", localIllegalArgumentException);
    }
  }
  
  @WorkerThread
  public void zzpw()
  {
    this.zzbLa.zznA();
    this.zzbLa.zzmW();
    if (this.zzafp) {
      return;
    }
    getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    this.zzafq = this.zzbLa.zzOt().isNetworkConnected();
    zzMg().zzNZ().zzm("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzafq));
    this.zzafp = true;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */