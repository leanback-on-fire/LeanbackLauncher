package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzaac<A extends Api.zzb, L>
{
  private final zzzw<L> zzaOL;
  
  protected zzaac(zzzw<L> paramzzzw)
  {
    this.zzaOL = paramzzzw;
  }
  
  protected abstract void zzb(A paramA, TaskCompletionSource<Void> paramTaskCompletionSource)
    throws RemoteException;
  
  public zzzw.zzb<L> zzyK()
  {
    return this.zzaOL.zzyK();
  }
  
  public void zzyL()
  {
    this.zzaOL.clear();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */