package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzaa.zza;

public class zzcbq
{
  private String zzakW;
  
  public zzcbq(@Nullable String paramString)
  {
    this.zzakW = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzcbq)) {
      return false;
    }
    paramObject = (zzcbq)paramObject;
    return zzaa.equal(this.zzakW, ((zzcbq)paramObject).zzakW);
  }
  
  @Nullable
  public String getToken()
  {
    return this.zzakW;
  }
  
  public int hashCode()
  {
    return zzaa.hashCode(new Object[] { this.zzakW });
  }
  
  public String toString()
  {
    return zzaa.zzB(this).zzh("token", this.zzakW).toString();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */