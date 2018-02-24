package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzd
  implements Parcelable.Creator<ExperimentTokens>
{
  static void zza(ExperimentTokens paramExperimentTokens, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramExperimentTokens.user, false);
    zzc.zza(paramParcel, 3, paramExperimentTokens.directExperimentToken, false);
    zzc.zza(paramParcel, 4, paramExperimentTokens.gaiaCrossExperimentTokens, false);
    zzc.zza(paramParcel, 5, paramExperimentTokens.pseudonymousCrossExperimentTokens, false);
    zzc.zza(paramParcel, 6, paramExperimentTokens.alwaysCrossExperimentTokens, false);
    zzc.zza(paramParcel, 7, paramExperimentTokens.otherCrossExperimentTokens, false);
    zzc.zza(paramParcel, 8, paramExperimentTokens.weakExperimentIds, false);
    zzc.zza(paramParcel, 9, paramExperimentTokens.additionalDirectExperimentTokens, false);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public ExperimentTokens zznq(Parcel paramParcel)
  {
    byte[][] arrayOfByte1 = null;
    int i = zzb.zzdA(paramParcel);
    int[] arrayOfInt = null;
    byte[][] arrayOfByte2 = null;
    byte[][] arrayOfByte3 = null;
    byte[][] arrayOfByte4 = null;
    byte[][] arrayOfByte5 = null;
    byte[] arrayOfByte = null;
    String str = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        str = zzb.zzq(paramParcel, j);
        break;
      case 3: 
        arrayOfByte = zzb.zzt(paramParcel, j);
        break;
      case 4: 
        arrayOfByte5 = zzb.zzu(paramParcel, j);
        break;
      case 5: 
        arrayOfByte4 = zzb.zzu(paramParcel, j);
        break;
      case 6: 
        arrayOfByte3 = zzb.zzu(paramParcel, j);
        break;
      case 7: 
        arrayOfByte2 = zzb.zzu(paramParcel, j);
        break;
      case 8: 
        arrayOfInt = zzb.zzw(paramParcel, j);
        break;
      case 9: 
        arrayOfByte1 = zzb.zzu(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new ExperimentTokens(str, arrayOfByte, arrayOfByte5, arrayOfByte4, arrayOfByte3, arrayOfByte2, arrayOfInt, arrayOfByte1);
  }
  
  public ExperimentTokens[] zzsp(int paramInt)
  {
    return new ExperimentTokens[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */