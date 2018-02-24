package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zza
  implements Parcelable.Creator<Configuration>
{
  static void zza(Configuration paramConfiguration, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzdB(paramParcel);
    zzc.zzc(paramParcel, 2, paramConfiguration.flagType);
    zzc.zza(paramParcel, 3, paramConfiguration.flags, paramInt, false);
    zzc.zza(paramParcel, 4, paramConfiguration.deleteFlags, false);
    zzc.zzK(paramParcel, i);
  }
  
  public Configuration zznn(Parcel paramParcel)
  {
    String[] arrayOfString = null;
    int j = zzb.zzdA(paramParcel);
    int i = 0;
    Flag[] arrayOfFlag = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
      }
      for (;;)
      {
        break;
        i = zzb.zzg(paramParcel, k);
        continue;
        arrayOfFlag = (Flag[])zzb.zzb(paramParcel, k, Flag.CREATOR);
        continue;
        arrayOfString = zzb.zzC(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new Configuration(i, arrayOfFlag, arrayOfString);
  }
  
  public Configuration[] zzsm(int paramInt)
  {
    return new Configuration[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */