package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public abstract class zzzc
  implements Releasable, Result
{
  protected final DataHolder zzaML;
  protected final Status zzaiT;
  
  protected zzzc(DataHolder paramDataHolder, Status paramStatus)
  {
    this.zzaiT = paramStatus;
    this.zzaML = paramDataHolder;
  }
  
  public Status getStatus()
  {
    return this.zzaiT;
  }
  
  public void release()
  {
    if (this.zzaML != null) {
      this.zzaML.close();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */