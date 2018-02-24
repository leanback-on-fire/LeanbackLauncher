package com.google.android.gms.usagereporting;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public abstract interface UsageReportingApi
{
  public abstract PendingResult<OptInOptionsResult> getOptInOptions(GoogleApiClient paramGoogleApiClient);
  
  public abstract PendingResult<Status> setOptInOptions(GoogleApiClient paramGoogleApiClient, UsageReportingOptInOptions paramUsageReportingOptInOptions);
  
  public abstract PendingResult<Status> setOptInOptionsChangedListener(GoogleApiClient paramGoogleApiClient, OptInOptionsChangedListener paramOptInOptionsChangedListener);
  
  public static abstract interface OptInOptions
  {
    public abstract boolean isOptedInForUsageReporting();
  }
  
  public static abstract interface OptInOptionsChangedListener
  {
    public abstract void onOptInOptionsChanged();
  }
  
  public static abstract interface OptInOptionsResult
    extends Result, UsageReportingApi.OptInOptions
  {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/usagereporting/UsageReportingApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */