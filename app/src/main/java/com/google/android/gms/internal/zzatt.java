package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatt
  extends zza
{
  public static final Parcelable.Creator<zzatt> CREATOR = new zzatu();
  public final String name;
  public final String zzaIu;
  public final zzatr zzbMC;
  public final long zzbMD;
  
  zzatt(zzatt paramzzatt, long paramLong)
  {
    zzac.zzC(paramzzatt);
    this.name = paramzzatt.name;
    this.zzbMC = paramzzatt.zzbMC;
    this.zzaIu = paramzzatt.zzaIu;
    this.zzbMD = paramLong;
  }
  
  public zzatt(String paramString1, zzatr paramzzatr, String paramString2, long paramLong)
  {
    this.name = paramString1;
    this.zzbMC = paramzzatr;
    this.zzaIu = paramString2;
    this.zzbMD = paramLong;
  }
  
  public String toString()
  {
    String str1 = this.zzaIu;
    String str2 = this.name;
    String str3 = String.valueOf(this.zzbMC);
    return String.valueOf(str1).length() + 21 + String.valueOf(str2).length() + String.valueOf(str3).length() + "origin=" + str1 + ",name=" + str2 + ",params=" + str3;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzatu.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */