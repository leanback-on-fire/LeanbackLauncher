package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzf
  implements Parcelable.Creator<FlagOverride>
{
  static void zza(FlagOverride paramFlagOverride, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramFlagOverride.configurationName, false);
    zzc.zza(paramParcel, 3, paramFlagOverride.userName, false);
    zzc.zza(paramParcel, 4, paramFlagOverride.flag, paramInt, false);
    zzc.zza(paramParcel, 5, paramFlagOverride.committed);
    zzc.zzK(paramParcel, i);
  }
  
  public FlagOverride zzns(Parcel paramParcel)
  {
    Object localObject2 = null;
    int i = zzb.zzdA(paramParcel);
    boolean bool = false;
    String str = null;
    Object localObject1 = null;
    if (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzdz(paramParcel);
      Object localObject3;
      switch (zzb.zzgg(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
      }
      for (;;)
      {
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
        break;
        localObject3 = zzb.zzq(paramParcel, j);
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        str = zzb.zzq(paramParcel, j);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        localObject3 = (Flag)zzb.zza(paramParcel, j, Flag.CREATOR);
        localObject2 = localObject1;
        localObject1 = localObject3;
        continue;
        bool = zzb.zzc(paramParcel, j);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new FlagOverride((String)localObject1, str, (Flag)localObject2, bool);
  }
  
  public FlagOverride[] zzsr(int paramInt)
  {
    return new FlagOverride[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */