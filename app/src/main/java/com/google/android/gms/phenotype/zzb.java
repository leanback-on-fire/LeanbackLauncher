package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb
  implements Parcelable.Creator<Configurations>
{
  static void zza(Configurations paramConfigurations, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramConfigurations.snapshotToken, false);
    zzc.zza(paramParcel, 3, paramConfigurations.serverToken, false);
    zzc.zza(paramParcel, 4, paramConfigurations.configurations, paramInt, false);
    zzc.zza(paramParcel, 5, paramConfigurations.isDelta);
    zzc.zza(paramParcel, 6, paramConfigurations.experimentToken, false);
    zzc.zzK(paramParcel, i);
  }
  
  public Configurations zzno(Parcel paramParcel)
  {
    byte[] arrayOfByte = null;
    int i = com.google.android.gms.common.internal.safeparcel.zzb.zzdA(paramParcel);
    boolean bool = false;
    Configuration[] arrayOfConfiguration = null;
    String str1 = null;
    String str2 = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = com.google.android.gms.common.internal.safeparcel.zzb.zzdz(paramParcel);
      switch (com.google.android.gms.common.internal.safeparcel.zzb.zzgg(j))
      {
      default: 
        com.google.android.gms.common.internal.safeparcel.zzb.zzb(paramParcel, j);
        break;
      case 2: 
        str2 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(paramParcel, j);
        break;
      case 3: 
        str1 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(paramParcel, j);
        break;
      case 4: 
        arrayOfConfiguration = (Configuration[])com.google.android.gms.common.internal.safeparcel.zzb.zzb(paramParcel, j, Configuration.CREATOR);
        break;
      case 5: 
        bool = com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, j);
        break;
      case 6: 
        arrayOfByte = com.google.android.gms.common.internal.safeparcel.zzb.zzt(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new Configurations(str2, str1, arrayOfConfiguration, bool, arrayOfByte);
  }
  
  public Configurations[] zzsn(int paramInt)
  {
    return new Configurations[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */