package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;

public abstract class zzzb<L>
  implements zzzw.zzc<L>
{
  private final DataHolder zzaML;
  
  protected zzzb(DataHolder paramDataHolder)
  {
    this.zzaML = paramDataHolder;
  }
  
  protected abstract void zza(L paramL, DataHolder paramDataHolder);
  
  public final void zzw(L paramL)
  {
    zza(paramL, this.zzaML);
  }
  
  public void zzxO()
  {
    if (this.zzaML != null) {
      this.zzaML.close();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */