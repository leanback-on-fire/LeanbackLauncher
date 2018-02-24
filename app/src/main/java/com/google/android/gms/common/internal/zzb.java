package com.google.android.gms.common.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.common.api.zze;

public class zzb
{
  @NonNull
  public static zza zzW(@NonNull Status paramStatus)
  {
    if (paramStatus.hasResolution()) {
      return new zze(paramStatus);
    }
    return new zza(paramStatus);
  }
  
  @NonNull
  public static zza zzl(@NonNull ConnectionResult paramConnectionResult)
  {
    return zzW(new Status(paramConnectionResult.getErrorCode(), paramConnectionResult.getErrorMessage(), paramConnectionResult.getResolution()));
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/common/internal/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */