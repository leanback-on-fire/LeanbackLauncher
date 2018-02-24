package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaf;

public class zzbgq
  extends zza
{
  public static final Parcelable.Creator<zzbgq> CREATOR = new zzbgr();
  final int mVersionCode;
  private final ConnectionResult zzaSG;
  private final zzaf zzcqO;
  
  public zzbgq(int paramInt)
  {
    this(new ConnectionResult(paramInt, null), null);
  }
  
  zzbgq(int paramInt, ConnectionResult paramConnectionResult, zzaf paramzzaf)
  {
    this.mVersionCode = paramInt;
    this.zzaSG = paramConnectionResult;
    this.zzcqO = paramzzaf;
  }
  
  public zzbgq(ConnectionResult paramConnectionResult, zzaf paramzzaf)
  {
    this(1, paramConnectionResult, paramzzaf);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbgr.zza(this, paramParcel, paramInt);
  }
  
  public ConnectionResult zzAb()
  {
    return this.zzaSG;
  }
  
  public zzaf zzWi()
  {
    return this.zzcqO;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbgq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */