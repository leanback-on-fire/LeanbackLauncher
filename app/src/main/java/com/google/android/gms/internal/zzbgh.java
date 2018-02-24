package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.List;

public class zzbgh
  extends zza
{
  public static final Parcelable.Creator<zzbgh> CREATOR = new zzbgi();
  final int mVersionCode;
  final boolean zzcqJ;
  final List<Scope> zzcqK;
  
  zzbgh(int paramInt, boolean paramBoolean, List<Scope> paramList)
  {
    this.mVersionCode = paramInt;
    this.zzcqJ = paramBoolean;
    this.zzcqK = paramList;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbgi.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbgh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */