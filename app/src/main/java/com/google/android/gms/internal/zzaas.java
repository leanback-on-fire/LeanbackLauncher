package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzaas<A extends Api.zzb, L>
{
  private final zzzw.zzb<L> zzaOB;
  
  protected zzaas(zzzw.zzb<L> paramzzb)
  {
    this.zzaOB = paramzzb;
  }
  
  protected abstract void zzc(A paramA, TaskCompletionSource<Void> paramTaskCompletionSource)
    throws RemoteException;
  
  public zzzw.zzb<L> zzyK()
  {
    return this.zzaOB;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaas.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */