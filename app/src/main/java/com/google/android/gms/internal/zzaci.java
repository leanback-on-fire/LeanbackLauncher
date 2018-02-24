package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaci
  implements Parcelable.Creator<zzach>
{
  static void zza(zzach paramzzach, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramzzach.getPayload(), false);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public zzach zzdO(Parcel paramParcel)
  {
    int i = zzb.zzdA(paramParcel);
    byte[] arrayOfByte = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        arrayOfByte = zzb.zzt(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zzach(arrayOfByte);
  }
  
  public zzach[] zzgE(int paramInt)
  {
    return new zzach[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaci.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */