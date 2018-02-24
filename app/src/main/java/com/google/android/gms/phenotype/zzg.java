package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;

public class zzg
  implements Parcelable.Creator<FlagOverrides>
{
  static void zza(FlagOverrides paramFlagOverrides, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zzc(paramParcel, 2, paramFlagOverrides.overrides, false);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public FlagOverrides zznt(Parcel paramParcel)
  {
    int i = zzb.zzdA(paramParcel);
    ArrayList localArrayList = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        localArrayList = zzb.zzc(paramParcel, j, FlagOverride.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new FlagOverrides(localArrayList);
  }
  
  public FlagOverrides[] zzss(int paramInt)
  {
    return new FlagOverrides[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */