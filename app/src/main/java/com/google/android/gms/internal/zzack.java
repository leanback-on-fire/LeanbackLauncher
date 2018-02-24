package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzack
  implements Parcelable.Creator<zzacj>
{
  static void zza(zzacj paramzzacj, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramzzacj.getName(), false);
    zzc.zza(paramParcel, 3, paramzzacj.getValue(), false);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public zzacj zzdP(Parcel paramParcel)
  {
    String str2 = null;
    int i = zzb.zzdA(paramParcel);
    String str1 = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        str1 = zzb.zzq(paramParcel, j);
        break;
      case 3: 
        str2 = zzb.zzq(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zzacj(str1, str2);
  }
  
  public zzacj[] zzgF(int paramInt)
  {
    return new zzacj[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */