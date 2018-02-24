package com.google.android.gms.phenotype;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

@Deprecated
public abstract interface PhenotypeApi
{
  public abstract PendingResult<Status> commitToConfiguration(GoogleApiClient paramGoogleApiClient, String paramString);
  
  public abstract PendingResult<FlagOverridesResult> deleteFlagOverrides(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, String paramString3);
  
  public abstract PendingResult<ConfigurationsResult> getAlternateConfigurationSnapshot(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract PendingResult<ConfigurationsResult> getCommittedConfiguration(GoogleApiClient paramGoogleApiClient, String paramString);
  
  @Deprecated
  public abstract PendingResult<ConfigurationsResult> getConfigurationSnapshot(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2);
  
  public abstract PendingResult<ConfigurationsResult> getConfigurationSnapshot(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, String paramString3);
  
  public abstract PendingResult<DogfoodsTokenResult> getDogfoodsToken(GoogleApiClient paramGoogleApiClient);
  
  public abstract PendingResult<ExperimentTokensResult> getExperiments(GoogleApiClient paramGoogleApiClient, String paramString);
  
  public abstract PendingResult<ExperimentTokensResult> getExperimentsForLogging(GoogleApiClient paramGoogleApiClient, String paramString);
  
  public abstract PendingResult<FlagResult> getFlag(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, int paramInt);
  
  public abstract PendingResult<FlagOverridesResult> listFlagOverrides(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, String paramString3);
  
  public abstract PendingResult<Status> register(GoogleApiClient paramGoogleApiClient, String paramString, int paramInt, String[] paramArrayOfString, byte[] paramArrayOfByte);
  
  public abstract PendingResult<ConfigurationsResult> registerSync(GoogleApiClient paramGoogleApiClient, String paramString1, int paramInt, String[] paramArrayOfString, byte[] paramArrayOfByte, String paramString2, String paramString3);
  
  public abstract PendingResult<Status> setDogfoodsToken(GoogleApiClient paramGoogleApiClient, byte[] paramArrayOfByte);
  
  public abstract PendingResult<Status> setFlagOverride(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4);
  
  public abstract PendingResult<Status> syncAfter(GoogleApiClient paramGoogleApiClient, ServingVersion paramServingVersion);
  
  public abstract PendingResult<Status> unRegister(GoogleApiClient paramGoogleApiClient, String paramString);
  
  public abstract PendingResult<Status> weakRegister(GoogleApiClient paramGoogleApiClient, String paramString, int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt, byte[] paramArrayOfByte);
  
  public static abstract interface ConfigurationsResult
    extends Result
  {
    public abstract Configurations getConfigurations();
  }
  
  public static abstract interface DogfoodsTokenResult
    extends Result
  {
    public abstract DogfoodsToken getDogfoodsToken();
  }
  
  public static abstract interface ExperimentTokensResult
    extends Result
  {
    public abstract ExperimentTokens getExperimentTokens();
  }
  
  public static abstract interface FlagOverridesResult
    extends Result
  {
    public abstract FlagOverrides getFlagOverrides();
  }
  
  public static abstract interface FlagResult
    extends Result
  {
    public abstract Flag getFlag();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/PhenotypeApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */