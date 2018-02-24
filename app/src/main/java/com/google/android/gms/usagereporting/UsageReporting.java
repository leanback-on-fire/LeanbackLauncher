package com.google.android.gms.usagereporting;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzbrx;
import com.google.android.gms.internal.zzbry;
import com.google.android.gms.internal.zzyr.zza;

public class UsageReporting
{
  public static final Api<Api.ApiOptions.NoOptions> API = new Api("UsageReporting.API", zzaiG, zzaiF);
  public static final UsageReportingApi UsageReportingApi = new zzbrx();
  private static final Api.zzf<zzbry> zzaiF = new Api.zzf();
  private static final Api.zza<zzbry, Api.ApiOptions.NoOptions> zzaiG = new Api.zza()
  {
    public zzbry zzaf(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, Api.ApiOptions.NoOptions paramAnonymousNoOptions, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
    {
      return new zzbry(paramAnonymousContext, paramAnonymousLooper, paramAnonymouszzg, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
    }
  };
  
  public static boolean isUsageReportingServiceAvailable(Context paramContext)
  {
    return zzbry.isUsageReportingServiceAvailable(paramContext);
  }
  
  public static abstract class zza<R extends Result>
    extends zzyr.zza<R, zzbry>
  {
    public zza(GoogleApiClient paramGoogleApiClient)
    {
      super(paramGoogleApiClient);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/usagereporting/UsageReporting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */