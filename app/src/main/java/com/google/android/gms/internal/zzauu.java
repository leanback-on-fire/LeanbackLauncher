package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzauu
  implements Parcelable.Creator<zzaut>
{
  static void zza(zzaut paramzzaut, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzaut.versionCode);
    zzc.zza(paramParcel, 2, paramzzaut.name, false);
    zzc.zza(paramParcel, 3, paramzzaut.zzbQW);
    zzc.zza(paramParcel, 4, paramzzaut.zzbQX, false);
    zzc.zza(paramParcel, 5, paramzzaut.zzbQY, false);
    zzc.zza(paramParcel, 6, paramzzaut.stringValue, false);
    zzc.zza(paramParcel, 7, paramzzaut.zzaIu, false);
    zzc.zza(paramParcel, 8, paramzzaut.zzbQZ, false);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public zzaut zzll(Parcel paramParcel)
  {
    Double localDouble = null;
    int j = zzb.zzdA(paramParcel);
    int i = 0;
    long l = 0L;
    String str1 = null;
    String str2 = null;
    Float localFloat = null;
    Long localLong = null;
    String str3 = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 2: 
        str3 = zzb.zzq(paramParcel, k);
        break;
      case 3: 
        l = zzb.zzi(paramParcel, k);
        break;
      case 4: 
        localLong = zzb.zzj(paramParcel, k);
        break;
      case 5: 
        localFloat = zzb.zzm(paramParcel, k);
        break;
      case 6: 
        str2 = zzb.zzq(paramParcel, k);
        break;
      case 7: 
        str1 = zzb.zzq(paramParcel, k);
        break;
      case 8: 
        localDouble = zzb.zzo(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzaut(i, str3, l, localLong, localFloat, str2, str1, localDouble);
  }
  
  public zzaut[] zzpN(int paramInt)
  {
    return new zzaut[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */