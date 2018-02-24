package com.google.android.gms.phenotype;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzbcj;
import com.google.android.gms.internal.zzbck;

public final class Phenotype
{
  public static final String ACTION_UPDATE = "com.google.android.gms.phenotype.UPDATE";
  @Deprecated
  public static final Api<Api.ApiOptions.NoOptions> API = new Api("Phenotype.API", zzaiG, zzaiF);
  public static final String EMPTY_ALTERNATE = "com.google.EMPTY";
  public static final String EXTRA_PACKAGE_NAME = "com.google.android.gms.phenotype.PACKAGE_NAME";
  public static final String EXTRA_URGENT_UPDATE = "com.google.android.gms.phenotype.URGENT";
  @Deprecated
  public static final PhenotypeApi PhenotypeApi = new zzbcj();
  public static final String SAFE_CONFIGURATION_PREFIX = "_SAFE_";
  public static final Api.zzf<zzbck> zzaiF = new Api.zzf();
  public static final Api.zza<zzbck, Api.ApiOptions.NoOptions> zzaiG = new Api.zza()
  {
    public zzbck zzM(Context paramAnonymousContext, Looper paramAnonymousLooper, zzg paramAnonymouszzg, Api.ApiOptions.NoOptions paramAnonymousNoOptions, GoogleApiClient.ConnectionCallbacks paramAnonymousConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramAnonymousOnConnectionFailedListener)
    {
      return new zzbck(paramAnonymousContext, paramAnonymousLooper, paramAnonymouszzg, paramAnonymousConnectionCallbacks, paramAnonymousOnConnectionFailedListener);
    }
  };
  
  public static PhenotypeClient getInstance(Activity paramActivity)
  {
    return new PhenotypeClient(paramActivity);
  }
  
  public static PhenotypeClient getInstance(Context paramContext)
  {
    return new PhenotypeClient(paramContext);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/Phenotype.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */