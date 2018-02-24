package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zze
  implements Parcelable.Creator<Flag>
{
  static void zza(Flag paramFlag, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramFlag.name, false);
    zzc.zza(paramParcel, 3, paramFlag.zzceZ);
    zzc.zza(paramParcel, 4, paramFlag.booleanValue);
    zzc.zza(paramParcel, 5, paramFlag.doubleValue);
    zzc.zza(paramParcel, 6, paramFlag.stringValue, false);
    zzc.zza(paramParcel, 7, paramFlag.zzcfa, false);
    zzc.zzc(paramParcel, 8, paramFlag.flagValueType);
    zzc.zzc(paramParcel, 9, paramFlag.flagStorageType);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public Flag zznr(Parcel paramParcel)
  {
    byte[] arrayOfByte = null;
    int i = 0;
    int k = zzb.zzdA(paramParcel);
    long l = 0L;
    double d = 0.0D;
    int j = 0;
    String str1 = null;
    boolean bool = false;
    String str2 = null;
    while (paramParcel.dataPosition() < k)
    {
      int m = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(m))
      {
      default: 
        zzb.zzb(paramParcel, m);
        break;
      case 2: 
        str2 = zzb.zzq(paramParcel, m);
        break;
      case 3: 
        l = zzb.zzi(paramParcel, m);
        break;
      case 4: 
        bool = zzb.zzc(paramParcel, m);
        break;
      case 5: 
        d = zzb.zzn(paramParcel, m);
        break;
      case 6: 
        str1 = zzb.zzq(paramParcel, m);
        break;
      case 7: 
        arrayOfByte = zzb.zzt(paramParcel, m);
        break;
      case 8: 
        j = zzb.zzg(paramParcel, m);
        break;
      case 9: 
        i = zzb.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zzb.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new Flag(str2, l, bool, d, str1, arrayOfByte, j, i);
  }
  
  public Flag[] zzsq(int paramInt)
  {
    return new Flag[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */