package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzacr
  extends zza
{
  public static final Parcelable.Creator<zzacr> CREATOR = new zzacs();
  private final Bundle zzaVY;
  
  public zzacr(Bundle paramBundle)
  {
    this.zzaVY = paramBundle;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzacs.zza(this, paramParcel, paramInt);
  }
  
  public Bundle zzBA()
  {
    return this.zzaVY;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */