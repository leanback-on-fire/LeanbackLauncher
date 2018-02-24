package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;

class zzatq
{
  final String mAppId;
  final String mName;
  final long zzbMw;
  final long zzbMx;
  final long zzbMy;
  
  zzatq(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3)
  {
    zzac.zzdc(paramString1);
    zzac.zzdc(paramString2);
    if (paramLong1 >= 0L)
    {
      bool1 = true;
      zzac.zzaw(bool1);
      if (paramLong2 < 0L) {
        break label81;
      }
    }
    label81:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzaw(bool1);
      this.mAppId = paramString1;
      this.mName = paramString2;
      this.zzbMw = paramLong1;
      this.zzbMx = paramLong2;
      this.zzbMy = paramLong3;
      return;
      bool1 = false;
      break;
    }
  }
  
  zzatq zzNQ()
  {
    return new zzatq(this.mAppId, this.mName, this.zzbMw + 1L, this.zzbMx + 1L, this.zzbMy);
  }
  
  zzatq zzaA(long paramLong)
  {
    return new zzatq(this.mAppId, this.mName, this.zzbMw, this.zzbMx, paramLong);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */