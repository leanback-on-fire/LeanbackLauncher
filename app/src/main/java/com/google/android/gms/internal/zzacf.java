package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.zzd;
import com.google.android.gms.config.Config;
import com.google.android.gms.config.ConfigApi;
import com.google.android.gms.config.ConfigApi.FetchConfigRequest;
import com.google.android.gms.config.ConfigApi.FetchConfigResult;

public class zzacf
  extends zzd<Api.ApiOptions.NoOptions>
{
  public zzacf(Context paramContext)
  {
    super(paramContext, Config.API, null, new zzym());
  }
  
  public PendingResult<ConfigApi.FetchConfigResult> zza(ConfigApi.FetchConfigRequest paramFetchConfigRequest)
  {
    return Config.ConfigApi.fetchConfig(asGoogleApiClient(), paramFetchConfigRequest);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */