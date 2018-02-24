package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult>
{
  private final zzh<TResult> zzcEH = new zzh();
  
  @NonNull
  public Task<TResult> getTask()
  {
    return this.zzcEH;
  }
  
  public void setException(@NonNull Exception paramException)
  {
    this.zzcEH.setException(paramException);
  }
  
  public void setResult(TResult paramTResult)
  {
    this.zzcEH.setResult(paramTResult);
  }
  
  public boolean trySetException(@NonNull Exception paramException)
  {
    return this.zzcEH.trySetException(paramException);
  }
  
  public boolean trySetResult(TResult paramTResult)
  {
    return this.zzcEH.trySetResult(paramTResult);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/tasks/TaskCompletionSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */