package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.usagereporting.UsageReporting.zza;
import com.google.android.gms.usagereporting.UsageReportingApi;
import com.google.android.gms.usagereporting.UsageReportingApi.OptInOptionsChangedListener;
import com.google.android.gms.usagereporting.UsageReportingApi.OptInOptionsResult;
import com.google.android.gms.usagereporting.UsageReportingOptInOptions;

public class zzbrx
  implements UsageReportingApi
{
  public PendingResult<UsageReportingApi.OptInOptionsResult> getOptInOptions(GoogleApiClient paramGoogleApiClient)
  {
    paramGoogleApiClient.zza(new UsageReporting.zza(paramGoogleApiClient)
    {
      protected void zza(zzbry paramAnonymouszzbry)
        throws RemoteException
      {
        paramAnonymouszzbry.zzM(this);
      }
      
      public UsageReportingApi.OptInOptionsResult zzfi(Status paramAnonymousStatus)
      {
        return new zzbrw(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<Status> setOptInOptions(GoogleApiClient paramGoogleApiClient, final UsageReportingOptInOptions paramUsageReportingOptInOptions)
  {
    paramGoogleApiClient.zzb(new UsageReporting.zza(paramGoogleApiClient)
    {
      protected void zza(zzbry paramAnonymouszzbry)
        throws RemoteException
      {
        paramAnonymouszzbry.zza(paramUsageReportingOptInOptions, this);
      }
      
      public Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  public PendingResult<Status> setOptInOptionsChangedListener(GoogleApiClient paramGoogleApiClient, final UsageReportingApi.OptInOptionsChangedListener paramOptInOptionsChangedListener)
  {
    if (paramOptInOptionsChangedListener == null) {}
    for (paramOptInOptionsChangedListener = null;; paramOptInOptionsChangedListener = paramGoogleApiClient.zzu(paramOptInOptionsChangedListener)) {
      paramGoogleApiClient.zza(new UsageReporting.zza(paramGoogleApiClient)
      {
        protected void zza(zzbry paramAnonymouszzbry)
          throws RemoteException
        {
          paramAnonymouszzbry.zza(paramOptInOptionsChangedListener, this);
        }
        
        public Status zzd(Status paramAnonymousStatus)
        {
          return paramAnonymousStatus;
        }
      });
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbrx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */