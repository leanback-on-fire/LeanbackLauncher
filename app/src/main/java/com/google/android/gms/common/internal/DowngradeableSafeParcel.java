package com.google.android.gms.common.internal;

import com.google.android.gms.common.internal.safeparcel.zza;

public abstract class DowngradeableSafeParcel
  extends zza
  implements ReflectedParcelable
{
  private static final Object zzaRJ = new Object();
  private static ClassLoader zzaRK = null;
  private static Integer zzaRL = null;
  private boolean zzaRM = false;
  
  protected static boolean zzcW(String paramString)
  {
    zzzO();
    return true;
  }
  
  protected static ClassLoader zzzO()
  {
    synchronized (zzaRJ)
    {
      return null;
    }
  }
  
  protected static Integer zzzP()
  {
    synchronized (zzaRJ)
    {
      return null;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/common/internal/DowngradeableSafeParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */