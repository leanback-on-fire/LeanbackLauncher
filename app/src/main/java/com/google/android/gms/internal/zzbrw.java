package com.google.android.gms.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.usagereporting.UsageReportingApi.OptInOptionsResult;
import com.google.android.gms.usagereporting.UsageReportingOptInOptions;

class zzbrw
  implements UsageReportingApi.OptInOptionsResult
{
  private final Status zzaiT;
  private final UsageReportingOptInOptions zzcFP;
  
  zzbrw(Status paramStatus, UsageReportingOptInOptions paramUsageReportingOptInOptions)
  {
    this.zzaiT = paramStatus;
    this.zzcFP = paramUsageReportingOptInOptions;
  }
  
  public Status getStatus()
  {
    return this.zzaiT;
  }
  
  public boolean isOptedInForUsageReporting()
  {
    zzac.zzC(this.zzcFP);
    return this.zzcFP.zzaaR() == 1;
  }
  
  public String toString()
  {
    boolean bool = true;
    if (this.zzcFP.zzaaR() == 1) {}
    for (;;)
    {
      String str = String.valueOf(Boolean.toString(bool));
      return String.valueOf(str).length() + 24 + "OptInOptionsResultImpl[" + str + "]";
      bool = false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbrw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */