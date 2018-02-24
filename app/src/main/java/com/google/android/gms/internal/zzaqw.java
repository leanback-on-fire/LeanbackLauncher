package com.google.android.gms.internal;

public final class zzaqw
{
  private static zzaqw zzbnr;
  private final zzaqt zzbns = new zzaqt();
  private final zzaqu zzbnt = new zzaqu();
  
  static
  {
    zza(new zzaqw());
  }
  
  private static zzaqw zzGw()
  {
    try
    {
      zzaqw localzzaqw = zzbnr;
      return localzzaqw;
    }
    finally {}
  }
  
  public static zzaqt zzGx()
  {
    return zzGw().zzbns;
  }
  
  public static zzaqu zzGy()
  {
    return zzGw().zzbnt;
  }
  
  protected static void zza(zzaqw paramzzaqw)
  {
    try
    {
      zzbnr = paramzzaqw;
      return;
    }
    finally {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaqw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */