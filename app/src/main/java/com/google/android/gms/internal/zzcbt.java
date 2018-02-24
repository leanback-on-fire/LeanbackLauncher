package com.google.android.gms.internal;

import android.content.Context;
import java.util.List;

public class zzcbt
  implements Runnable
{
  private final Context mContext;
  private final long wY;
  private final List<byte[]> zzaVI;
  
  public zzcbt(Context paramContext, List<byte[]> paramList, long paramLong)
  {
    this.mContext = paramContext;
    this.zzaVI = paramList;
    this.wY = paramLong;
  }
  
  public void run()
  {
    zzbtx.zza(this.mContext, "frc", this.zzaVI, 1, new zzbtw(), this.wY);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */