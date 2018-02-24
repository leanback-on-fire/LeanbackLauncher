package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzach
  extends zza
{
  public static final Parcelable.Creator<zzach> CREATOR = new zzaci();
  private final byte[] zzaVP;
  
  public zzach(byte[] paramArrayOfByte)
  {
    this.zzaVP = paramArrayOfByte;
  }
  
  public byte[] getPayload()
  {
    return this.zzaVP;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzaci.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzach.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */