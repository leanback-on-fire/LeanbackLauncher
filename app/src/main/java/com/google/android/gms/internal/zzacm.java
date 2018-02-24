package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;

public class zzacm
  implements Parcelable.Creator<zzacl>
{
  static void zza(zzacl paramzzacl, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramzzacl.getPackageName(), false);
    zzc.zza(paramParcel, 3, paramzzacl.zzBq());
    zzc.zza(paramParcel, 4, paramzzacl.zzBr(), paramInt, false);
    zzc.zza(paramParcel, 5, paramzzacl.zzBs(), false);
    zzc.zza(paramParcel, 6, paramzzacl.getAppInstanceId(), false);
    zzc.zza(paramParcel, 7, paramzzacl.zzBt(), false);
    zzc.zzb(paramParcel, 8, paramzzacl.zzBu(), false);
    zzc.zzc(paramParcel, 9, paramzzacl.zzBj());
    zzc.zzc(paramParcel, 10, paramzzacl.zzBv(), false);
    zzc.zzc(paramParcel, 11, paramzzacl.zzBk());
    zzc.zzc(paramParcel, 12, paramzzacl.zzBl());
    zzc.zzK(paramParcel, i);
  }
  
  public zzacl zzdQ(Parcel paramParcel)
  {
    int m = zzb.zzdA(paramParcel);
    String str4 = null;
    long l = 0L;
    DataHolder localDataHolder = null;
    String str3 = null;
    String str2 = null;
    String str1 = null;
    ArrayList localArrayList2 = null;
    int k = 0;
    ArrayList localArrayList1 = null;
    int j = 0;
    int i = 0;
    while (paramParcel.dataPosition() < m)
    {
      int n = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(n))
      {
      default: 
        zzb.zzb(paramParcel, n);
        break;
      case 2: 
        str4 = zzb.zzq(paramParcel, n);
        break;
      case 3: 
        l = zzb.zzi(paramParcel, n);
        break;
      case 4: 
        localDataHolder = (DataHolder)zzb.zza(paramParcel, n, DataHolder.CREATOR);
        break;
      case 5: 
        str3 = zzb.zzq(paramParcel, n);
        break;
      case 6: 
        str2 = zzb.zzq(paramParcel, n);
        break;
      case 7: 
        str1 = zzb.zzq(paramParcel, n);
        break;
      case 8: 
        localArrayList2 = zzb.zzF(paramParcel, n);
        break;
      case 9: 
        k = zzb.zzg(paramParcel, n);
        break;
      case 10: 
        localArrayList1 = zzb.zzc(paramParcel, n, zzacb.CREATOR);
        break;
      case 11: 
        j = zzb.zzg(paramParcel, n);
        break;
      case 12: 
        i = zzb.zzg(paramParcel, n);
      }
    }
    if (paramParcel.dataPosition() != m) {
      throw new zzb.zza(37 + "Overread allowed size end=" + m, paramParcel);
    }
    return new zzacl(str4, l, localDataHolder, str3, str2, str1, localArrayList2, k, localArrayList1, j, i);
  }
  
  public zzacl[] zzgG(int paramInt)
  {
    return new zzacl[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */