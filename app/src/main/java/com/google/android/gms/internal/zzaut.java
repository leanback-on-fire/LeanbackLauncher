package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzaut
  extends zza
{
  public static final Parcelable.Creator<zzaut> CREATOR = new zzauu();
  public final String name;
  public final String stringValue;
  public final int versionCode;
  public final String zzaIu;
  public final long zzbQW;
  public final Long zzbQX;
  public final Float zzbQY;
  public final Double zzbQZ;
  
  zzaut(int paramInt, String paramString1, long paramLong, Long paramLong1, Float paramFloat, String paramString2, String paramString3, Double paramDouble)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.zzbQW = paramLong;
    this.zzbQX = paramLong1;
    this.zzbQY = null;
    if (paramInt == 1)
    {
      paramString1 = (String)localObject;
      if (paramFloat != null) {
        paramString1 = Double.valueOf(paramFloat.doubleValue());
      }
    }
    for (this.zzbQZ = paramString1;; this.zzbQZ = paramDouble)
    {
      this.stringValue = paramString2;
      this.zzaIu = paramString3;
      return;
    }
  }
  
  zzaut(zzauv paramzzauv)
  {
    this(paramzzauv.mName, paramzzauv.zzbRa, paramzzauv.mValue, paramzzauv.mOrigin);
  }
  
  zzaut(String paramString1, long paramLong, Object paramObject, String paramString2)
  {
    zzac.zzdc(paramString1);
    this.versionCode = 2;
    this.name = paramString1;
    this.zzbQW = paramLong;
    this.zzaIu = paramString2;
    if (paramObject == null)
    {
      this.zzbQX = null;
      this.zzbQY = null;
      this.zzbQZ = null;
      this.stringValue = null;
      return;
    }
    if ((paramObject instanceof Long))
    {
      this.zzbQX = ((Long)paramObject);
      this.zzbQY = null;
      this.zzbQZ = null;
      this.stringValue = null;
      return;
    }
    if ((paramObject instanceof String))
    {
      this.zzbQX = null;
      this.zzbQY = null;
      this.zzbQZ = null;
      this.stringValue = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      this.zzbQX = null;
      this.zzbQY = null;
      this.zzbQZ = ((Double)paramObject);
      this.stringValue = null;
      return;
    }
    throw new IllegalArgumentException("User attribute given of un-supported type");
  }
  
  public Object getValue()
  {
    if (this.zzbQX != null) {
      return this.zzbQX;
    }
    if (this.zzbQZ != null) {
      return this.zzbQZ;
    }
    if (this.stringValue != null) {
      return this.stringValue;
    }
    return null;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzauu.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */