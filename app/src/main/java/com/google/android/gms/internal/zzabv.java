package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

public class zzabv
  implements Executor
{
  private final Handler mHandler;
  
  public zzabv(Looper paramLooper)
  {
    this.mHandler = new Handler(paramLooper);
  }
  
  public void execute(@NonNull Runnable paramRunnable)
  {
    this.mHandler.post(paramRunnable);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzabv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */