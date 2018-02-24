package com.google.android.gms.usagereporting;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zza
  implements Parcelable.Creator<UsageReportingOptInOptions>
{
  static void zza(UsageReportingOptInOptions paramUsageReportingOptInOptions, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzdB(paramParcel);
    zzc.zzc(paramParcel, 1, paramUsageReportingOptInOptions.mVersionCode);
    zzc.zzc(paramParcel, 2, paramUsageReportingOptInOptions.zzcFO);
    zzc.zzK(paramParcel, paramInt);
  }
  
  public UsageReportingOptInOptions zzrZ(Parcel paramParcel)
  {
    int j = 0;
    int k = zzb.zzdA(paramParcel);
    int i = 0;
    while (paramParcel.dataPosition() < k)
    {
      int m = zzb.zzdz(paramParcel);
      switch (zzb.zzgg(m))
      {
      default: 
        zzb.zzb(paramParcel, m);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, m);
        break;
      case 2: 
        j = zzb.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zzb.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new UsageReportingOptInOptions(i, j);
  }
  
  public UsageReportingOptInOptions[] zzxB(int paramInt)
  {
    return new UsageReportingOptInOptions[paramInt];
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/usagereporting/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */