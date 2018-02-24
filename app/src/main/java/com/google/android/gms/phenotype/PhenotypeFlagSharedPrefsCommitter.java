package com.google.android.gms.phenotype;

import android.content.SharedPreferences;
import com.google.android.gms.common.api.GoogleApiClient;

public class PhenotypeFlagSharedPrefsCommitter
  extends PhenotypeFlagCommitter
{
  private final SharedPreferences zzcfM;
  
  @Deprecated
  public PhenotypeFlagSharedPrefsCommitter(GoogleApiClient paramGoogleApiClient, PhenotypeApi paramPhenotypeApi, String paramString, SharedPreferences paramSharedPreferences)
  {
    super(paramGoogleApiClient, paramPhenotypeApi, paramString);
    this.zzcfM = paramSharedPreferences;
  }
  
  @Deprecated
  public PhenotypeFlagSharedPrefsCommitter(GoogleApiClient paramGoogleApiClient, String paramString, SharedPreferences paramSharedPreferences)
  {
    super(paramGoogleApiClient, paramString);
    this.zzcfM = paramSharedPreferences;
  }
  
  public PhenotypeFlagSharedPrefsCommitter(PhenotypeClient paramPhenotypeClient, String paramString, SharedPreferences paramSharedPreferences)
  {
    super(paramPhenotypeClient, paramString);
    this.zzcfM = paramSharedPreferences;
  }
  
  protected String getSnapshotToken()
  {
    return this.zzcfM.getString("__phenotype_snapshot_token", null);
  }
  
  protected void handleConfigurations(Configurations paramConfigurations)
  {
    writeToSharedPrefs(this.zzcfM, paramConfigurations);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/PhenotypeFlagSharedPrefsCommitter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */