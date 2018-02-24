package com.google.android.gms.config;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzace;
import com.google.android.gms.internal.zzacg;

public final class Config
{
  public static final Api<Api.ApiOptions.NoOptions> API = new Api("Config.API", zzaiG, zzaiF);
  public static final String CHANGED_ACTION = "com.google.android.gms.config.CHANGED";
  public static final ConfigApi ConfigApi = new zzace();
  public static final Api.zzf<zzacg> zzaiF = new Api.zzf();
  static final Api.zza<zzacg, Api.ApiOptions.NoOptions> zzaiG = new Api.zza()
  {
    public zzacg zzo(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, Api.ApiOptions.NoOptions paramAnonymousNoOptions, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
    {
      return new zzacg(paramAnonymousContext, paramAnonymousLooper, paramAnonymouszzg, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
    }
  };
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/config/Config.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */