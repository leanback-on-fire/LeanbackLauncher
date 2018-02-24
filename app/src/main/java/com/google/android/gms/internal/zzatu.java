package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzatu
  implements Parcelable.Creator<zzatt>
{
  static void zza(zzatt paramzzatt, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramzzatt.name, false);
    zzc.zza(paramParcel, 3, paramzzatt.zzbMC, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzatt.zzaIu, false);
    zzc.zza(paramParcel, 5, paramzzatt.zzbMD);
    zzc.zzK(paramParcel, i);
  }
  
  public zzatt zzlk(Parcel paramParcel)
  {
    String str1 = null;
    int i = zzb.zzdA(paramParcel);
    long l = 0L;
    zzatr localzzatr = null;
    String str2 = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        str2 = zzb.zzq(paramParcel, j);
        break;
      case 3: 
        localzzatr = (zzatr)zzb.zza(paramParcel, j, zzatr.CREATOR);
        break;
      case 4: 
        str1 = zzb.zzq(paramParcel, j);
        break;
      case 5: 
        l = zzb.zzi(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zzatt(str2, localzzatr, str1, l);
  }
  
  public zzatt[] zzpL(int paramInt)
  {
    return new zzatt[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */