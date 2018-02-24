package com.google.android.gms.internal;

import java.io.UnsupportedEncodingException;

public class zzac
  extends zzl<String>
{
  private final zzn.zzb<String> zzaF;
  
  public zzac(int paramInt, String paramString, zzn.zzb<String> paramzzb, zzn.zza paramzza)
  {
    super(paramInt, paramString, paramzza);
    this.zzaF = paramzzb;
  }
  
  protected zzn<String> zza(zzj paramzzj)
  {
    try
    {
      String str1 = new String(paramzzj.data, zzy.zza(paramzzj.headers));
      return zzn.zza(str1, zzy.zzb(paramzzj));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        String str2 = new String(paramzzj.data);
      }
    }
  }
  
  protected void zzi(String paramString)
  {
    this.zzaF.zzb(paramString);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */