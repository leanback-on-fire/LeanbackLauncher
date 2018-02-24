package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzacj
  extends zza
{
  public static final Parcelable.Creator<zzacj> CREATOR = new zzack();
  private final String mName;
  private final String mValue;
  
  public zzacj(String paramString1, String paramString2)
  {
    this.mName = paramString1;
    this.mValue = paramString2;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public String getValue()
  {
    return this.mValue;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzack.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */