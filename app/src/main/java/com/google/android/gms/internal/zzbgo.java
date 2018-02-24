package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzad;

public class zzbgo
  extends zza
{
  public static final Parcelable.Creator<zzbgo> CREATOR = new zzbgp();
  final int mVersionCode;
  final zzad zzcqN;
  
  zzbgo(int paramInt, zzad paramzzad)
  {
    this.mVersionCode = paramInt;
    this.zzcqN = paramzzad;
  }
  
  public zzbgo(zzad paramzzad)
  {
    this(1, paramzzad);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbgp.zza(this, paramParcel, paramInt);
  }
  
  public zzad zzWh()
  {
    return this.zzcqN;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbgo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */