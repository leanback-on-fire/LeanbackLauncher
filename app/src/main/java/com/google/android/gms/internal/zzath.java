package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzath
  implements Parcelable.Creator<zzatg>
{
  static void zza(zzatg paramzzatg, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zza(paramParcel, 2, paramzzatg.packageName, false);
    zzc.zza(paramParcel, 3, paramzzatg.zzbLI, false);
    zzc.zza(paramParcel, 4, paramzzatg.zzbAI, false);
    zzc.zza(paramParcel, 5, paramzzatg.zzbLJ, false);
    zzc.zza(paramParcel, 6, paramzzatg.zzbLK);
    zzc.zza(paramParcel, 7, paramzzatg.zzbLL);
    zzc.zza(paramParcel, 8, paramzzatg.zzbLM, false);
    zzc.zza(paramParcel, 9, paramzzatg.zzbLN);
    zzc.zza(paramParcel, 10, paramzzatg.zzbLO);
    zzc.zza(paramParcel, 11, paramzzatg.zzbLP);
    zzc.zza(paramParcel, 12, paramzzatg.zzbLQ, false);
    zzc.zza(paramParcel, 13, paramzzatg.zzbLR);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public zzatg zzlh(Parcel paramParcel)
  {
    int i = zzb.zzdA(paramParcel);
    String str6 = null;
    String str5 = null;
    String str4 = null;
    String str3 = null;
    long l4 = 0L;
    long l3 = 0L;
    String str2 = null;
    boolean bool2 = true;
    boolean bool1 = false;
    long l2 = -2147483648L;
    String str1 = null;
    long l1 = 0L;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        str6 = zzb.zzq(paramParcel, j);
        break;
      case 3: 
        str5 = zzb.zzq(paramParcel, j);
        break;
      case 4: 
        str4 = zzb.zzq(paramParcel, j);
        break;
      case 5: 
        str3 = zzb.zzq(paramParcel, j);
        break;
      case 6: 
        l4 = zzb.zzi(paramParcel, j);
        break;
      case 7: 
        l3 = zzb.zzi(paramParcel, j);
        break;
      case 8: 
        str2 = zzb.zzq(paramParcel, j);
        break;
      case 9: 
        bool2 = zzb.zzc(paramParcel, j);
        break;
      case 10: 
        bool1 = zzb.zzc(paramParcel, j);
        break;
      case 11: 
        l2 = zzb.zzi(paramParcel, j);
        break;
      case 12: 
        str1 = zzb.zzq(paramParcel, j);
        break;
      case 13: 
        l1 = zzb.zzi(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zzatg(str6, str5, str4, str3, l4, l3, str2, bool2, bool1, l2, str1, l1);
  }
  
  public zzatg[] zzpI(int paramInt)
  {
    return new zzatg[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */