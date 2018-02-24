package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaco
  implements Parcelable.Creator<zzacn>
{
  static void zza(zzacn paramzzacn, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzdB(paramParcel);
    zzc.zzc(paramParcel, 2, paramzzacn.getStatusCode());
    zzc.zza(paramParcel, 3, paramzzacn.zzBw(), paramInt, false);
    zzc.zza(paramParcel, 4, paramzzacn.getThrottleEndTimeMillis());
    zzc.zza(paramParcel, 5, paramzzacn.zzBx(), paramInt, false);
    zzc.zzK(paramParcel, i);
  }
  
  public zzacn zzdR(Parcel paramParcel)
  {
    DataHolder localDataHolder1 = null;
    int j = zzb.zzdA(paramParcel);
    int i = 0;
    long l = 0L;
    DataHolder localDataHolder2 = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 2: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 3: 
        localDataHolder2 = (DataHolder)zzb.zza(paramParcel, k, DataHolder.CREATOR);
        break;
      case 4: 
        l = zzb.zzi(paramParcel, k);
        break;
      case 5: 
        localDataHolder1 = (DataHolder)zzb.zza(paramParcel, k, DataHolder.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzacn(i, localDataHolder2, l, localDataHolder1);
  }
  
  public zzacn[] zzgH(int paramInt)
  {
    return new zzacn[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaco.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */