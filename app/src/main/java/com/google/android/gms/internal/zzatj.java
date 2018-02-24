package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatj
  extends zza
{
  public static final Parcelable.Creator<zzatj> CREATOR = new zzatk();
  public String packageName;
  public final int versionCode;
  public String zzaIu;
  public zzaut zzbLS;
  public long zzbLT;
  public boolean zzbLU;
  public String zzbLV;
  public zzatt zzbLW;
  public long zzbLX;
  public zzatt zzbLY;
  public long zzbLZ;
  public zzatt zzbMa;
  
  zzatj(int paramInt, String paramString1, String paramString2, zzaut paramzzaut, long paramLong1, boolean paramBoolean, String paramString3, zzatt paramzzatt1, long paramLong2, zzatt paramzzatt2, long paramLong3, zzatt paramzzatt3)
  {
    this.versionCode = paramInt;
    this.packageName = paramString1;
    this.zzaIu = paramString2;
    this.zzbLS = paramzzaut;
    this.zzbLT = paramLong1;
    this.zzbLU = paramBoolean;
    this.zzbLV = paramString3;
    this.zzbLW = paramzzatt1;
    this.zzbLX = paramLong2;
    this.zzbLY = paramzzatt2;
    this.zzbLZ = paramLong3;
    this.zzbMa = paramzzatt3;
  }
  
  zzatj(zzatj paramzzatj)
  {
    this.versionCode = 1;
    zzac.zzC(paramzzatj);
    this.packageName = paramzzatj.packageName;
    this.zzaIu = paramzzatj.zzaIu;
    this.zzbLS = paramzzatj.zzbLS;
    this.zzbLT = paramzzatj.zzbLT;
    this.zzbLU = paramzzatj.zzbLU;
    this.zzbLV = paramzzatj.zzbLV;
    this.zzbLW = paramzzatj.zzbLW;
    this.zzbLX = paramzzatj.zzbLX;
    this.zzbLY = paramzzatj.zzbLY;
    this.zzbLZ = paramzzatj.zzbLZ;
    this.zzbMa = paramzzatj.zzbMa;
  }
  
  zzatj(String paramString1, String paramString2, zzaut paramzzaut, long paramLong1, boolean paramBoolean, String paramString3, zzatt paramzzatt1, long paramLong2, zzatt paramzzatt2, long paramLong3, zzatt paramzzatt3)
  {
    this.versionCode = 1;
    this.packageName = paramString1;
    this.zzaIu = paramString2;
    this.zzbLS = paramzzaut;
    this.zzbLT = paramLong1;
    this.zzbLU = paramBoolean;
    this.zzbLV = paramString3;
    this.zzbLW = paramzzatt1;
    this.zzbLX = paramLong2;
    this.zzbLY = paramzzatt2;
    this.zzbLZ = paramLong3;
    this.zzbMa = paramzzatt3;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzatk.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */