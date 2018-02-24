package com.google.android.gms.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.Set;

public class zzatp
{
  final String mAppId;
  final String mName;
  final String mOrigin;
  final long zzaHE;
  final long zzbMu;
  final zzatr zzbMv;
  
  zzatp(zzauh paramzzauh, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    zzac.zzdc(paramString2);
    zzac.zzdc(paramString3);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzaHE = paramLong1;
    this.zzbMu = paramLong2;
    if ((this.zzbMu != 0L) && (this.zzbMu > this.zzaHE)) {
      paramzzauh.zzMg().zzNV().zzm("Event created with reverse previous/current timestamps. appId", zzaua.zzfH(paramString2));
    }
    this.zzbMv = zza(paramzzauh, paramBundle);
  }
  
  private zzatp(zzauh paramzzauh, String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, zzatr paramzzatr)
  {
    zzac.zzdc(paramString2);
    zzac.zzdc(paramString3);
    zzac.zzC(paramzzatr);
    this.mAppId = paramString2;
    this.mName = paramString3;
    paramString3 = paramString1;
    if (TextUtils.isEmpty(paramString1)) {
      paramString3 = null;
    }
    this.mOrigin = paramString3;
    this.zzaHE = paramLong1;
    this.zzbMu = paramLong2;
    if ((this.zzbMu != 0L) && (this.zzbMu > this.zzaHE)) {
      paramzzauh.zzMg().zzNV().zzm("Event created with reverse previous/current timestamps. appId", zzaua.zzfH(paramString2));
    }
    this.zzbMv = paramzzatr;
  }
  
  static zzatr zza(zzauh paramzzauh, Bundle paramBundle)
  {
    if ((paramBundle != null) && (!paramBundle.isEmpty()))
    {
      paramBundle = new Bundle(paramBundle);
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str == null)
        {
          paramzzauh.zzMg().zzNT().log("Param name can't be null");
          localIterator.remove();
        }
        else
        {
          Object localObject = paramzzauh.zzMc().zzo(str, paramBundle.get(str));
          if (localObject == null)
          {
            paramzzauh.zzMg().zzNV().zzm("Param value can't be null", str);
            localIterator.remove();
          }
          else
          {
            paramzzauh.zzMc().zza(paramBundle, str, localObject);
          }
        }
      }
      return new zzatr(paramBundle);
    }
    return new zzatr(new Bundle());
  }
  
  public String toString()
  {
    String str1 = this.mAppId;
    String str2 = this.mName;
    String str3 = String.valueOf(this.zzbMv);
    return String.valueOf(str1).length() + 33 + String.valueOf(str2).length() + String.valueOf(str3).length() + "Event{appId='" + str1 + "'" + ", name='" + str2 + "'" + ", params=" + str3 + "}";
  }
  
  zzatp zza(zzauh paramzzauh, long paramLong)
  {
    return new zzatp(paramzzauh, this.mOrigin, this.mAppId, this.mName, this.zzaHE, paramLong, this.zzbMv);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */