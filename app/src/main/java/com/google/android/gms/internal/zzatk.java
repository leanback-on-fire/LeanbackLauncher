package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzatk
  implements Parcelable.Creator<zzatj>
{
  static void zza(zzatj paramzzatj, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzdB(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzatj.versionCode);
    zzc.zza(paramParcel, 2, paramzzatj.packageName, false);
    zzc.zza(paramParcel, 3, paramzzatj.zzaIu, false);
    zzc.zza(paramParcel, 4, paramzzatj.zzbLS, paramInt, false);
    zzc.zza(paramParcel, 5, paramzzatj.zzbLT);
    zzc.zza(paramParcel, 6, paramzzatj.zzbLU);
    zzc.zza(paramParcel, 7, paramzzatj.zzbLV, false);
    zzc.zza(paramParcel, 8, paramzzatj.zzbLW, paramInt, false);
    zzc.zza(paramParcel, 9, paramzzatj.zzbLX);
    zzc.zza(paramParcel, 10, paramzzatj.zzbLY, paramInt, false);
    zzc.zza(paramParcel, 11, paramzzatj.zzbLZ);
    zzc.zza(paramParcel, 12, paramzzatj.zzbMa, paramInt, false);
    zzc.zzK(paramParcel, i);
  }
  
  public zzatj zzli(Parcel paramParcel)
  {
    int j = zzb.zzdA(paramParcel);
    int i = 0;
    String str3 = null;
    String str2 = null;
    zzaut localzzaut = null;
    long l3 = 0L;
    boolean bool = false;
    String str1 = null;
    zzatt localzzatt3 = null;
    long l2 = 0L;
    zzatt localzzatt2 = null;
    long l1 = 0L;
    zzatt localzzatt1 = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 2: 
        str3 = zzb.zzq(paramParcel, k);
        break;
      case 3: 
        str2 = zzb.zzq(paramParcel, k);
        break;
      case 4: 
        localzzaut = (zzaut)zzb.zza(paramParcel, k, zzaut.CREATOR);
        break;
      case 5: 
        l3 = zzb.zzi(paramParcel, k);
        break;
      case 6: 
        bool = zzb.zzc(paramParcel, k);
        break;
      case 7: 
        str1 = zzb.zzq(paramParcel, k);
        break;
      case 8: 
        localzzatt3 = (zzatt)zzb.zza(paramParcel, k, zzatt.CREATOR);
        break;
      case 9: 
        l2 = zzb.zzi(paramParcel, k);
        break;
      case 10: 
        localzzatt2 = (zzatt)zzb.zza(paramParcel, k, zzatt.CREATOR);
        break;
      case 11: 
        l1 = zzb.zzi(paramParcel, k);
        break;
      case 12: 
        localzzatt1 = (zzatt)zzb.zza(paramParcel, k, zzatt.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzatj(i, str3, str2, localzzaut, l3, bool, str1, localzzatt3, l2, localzzatt2, l1, localzzatt1);
  }
  
  public zzatj[] zzpJ(int paramInt)
  {
    return new zzatj[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */