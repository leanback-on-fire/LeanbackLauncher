package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;

public class zzc
  implements Parcelable.Creator<DogfoodsToken>
{
  static void zza(DogfoodsToken paramDogfoodsToken, Parcel paramParcel, int paramInt)
  {
    paramInt = com.google.android.gms.common.internal.safeparcel.zzc.zzdB(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzc.zza(paramParcel, 2, paramDogfoodsToken.token, false);
    com.google.android.gms.common.internal.safeparcel.zzc.zzK(paramParcel, paramInt);
  }
  
  public DogfoodsToken zznp(Parcel paramParcel)
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
    return new DogfoodsToken(arrayOfByte);
  }
  
  public DogfoodsToken[] zzso(int paramInt)
  {
    return new DogfoodsToken[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */