package com.google.android.gms.config;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzacb;
import com.google.android.gms.phenotype.Configuration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract interface ConfigApi
{
  public abstract PendingResult<FetchConfigResult> fetchConfig(GoogleApiClient paramGoogleApiClient, FetchConfigRequest paramFetchConfigRequest);
  
  @Deprecated
  public abstract PendingResult<GetValueResult> getValue(GoogleApiClient paramGoogleApiClient, String paramString);
  
  @Deprecated
  public abstract PendingResult<GetValueResult> getValue(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2);
  
  @Deprecated
  public abstract PendingResult<GetValuesResult> getValues(GoogleApiClient paramGoogleApiClient, String paramString, List<String> paramList);
  
  @Deprecated
  public abstract PendingResult<GetValuesResult> getValues(GoogleApiClient paramGoogleApiClient, List<String> paramList);
  
  @Deprecated
  public abstract PendingResult<GetValuesResult> getValuesByPrefix(GoogleApiClient paramGoogleApiClient, String paramString);
  
  @Deprecated
  public abstract PendingResult<GetValuesResult> getValuesByPrefix(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2);
  
  public abstract PendingResult<Status> setPhenotypeValues(GoogleApiClient paramGoogleApiClient, Configuration paramConfiguration, String paramString);
  
  public static class FetchConfigRequest
  {
    private final long zzaVq;
    private final Map<String, String> zzaVr;
    private final int zzaVs;
    private final List<zzacb> zzaVt;
    private final int zzaVu;
    private final int zzaVv;
    
    private FetchConfigRequest(Builder paramBuilder)
    {
      this.zzaVq = Builder.zza(paramBuilder);
      this.zzaVr = Builder.zzb(paramBuilder);
      this.zzaVs = Builder.zzc(paramBuilder);
      this.zzaVt = null;
      this.zzaVu = Builder.zzd(paramBuilder);
      this.zzaVv = Builder.zze(paramBuilder);
    }
    
    public long getCacheExpirationSeconds()
    {
      return this.zzaVq;
    }
    
    public Map<String, String> getCustomVariables()
    {
      if (this.zzaVr == null) {
        return Collections.emptyMap();
      }
      return this.zzaVr;
    }
    
    public int zzBj()
    {
      return this.zzaVs;
    }
    
    public int zzBk()
    {
      return this.zzaVv;
    }
    
    public int zzBl()
    {
      return this.zzaVu;
    }
    
    public static class Builder
    {
      private long zzaVq = 43200L;
      private Map<String, String> zzaVr;
      private int zzaVs;
      private int zzaVu = -1;
      private int zzaVv = -1;
      
      public Builder addCustomVariable(String paramString1, String paramString2)
      {
        if (this.zzaVr == null) {
          this.zzaVr = new HashMap();
        }
        this.zzaVr.put(paramString1, paramString2);
        return this;
      }
      
      public ConfigApi.FetchConfigRequest build()
      {
        return new ConfigApi.FetchConfigRequest(this, null);
      }
      
      public Builder setCacheExpirationSeconds(long paramLong)
      {
        this.zzaVq = paramLong;
        return this;
      }
      
      public Builder zzgA(int paramInt)
      {
        this.zzaVv = paramInt;
        return this;
      }
      
      public Builder zzgy(int paramInt)
      {
        this.zzaVs = paramInt;
        return this;
      }
      
      public Builder zzgz(int paramInt)
      {
        this.zzaVu = paramInt;
        return this;
      }
    }
  }
  
  public static abstract interface FetchConfigResult
    extends Result
  {
    public abstract Map<String, Set<String>> getAllConfigKeys();
    
    public abstract boolean getAsBoolean(String paramString, boolean paramBoolean);
    
    public abstract boolean getAsBoolean(String paramString1, boolean paramBoolean, String paramString2);
    
    public abstract byte[] getAsByteArray(String paramString, byte[] paramArrayOfByte);
    
    public abstract byte[] getAsByteArray(String paramString1, byte[] paramArrayOfByte, String paramString2);
    
    public abstract long getAsLong(String paramString, long paramLong);
    
    public abstract long getAsLong(String paramString1, long paramLong, String paramString2);
    
    public abstract String getAsString(String paramString1, String paramString2);
    
    public abstract String getAsString(String paramString1, String paramString2, String paramString3);
    
    public abstract Status getStatus();
    
    public abstract long getThrottleEndTimeMillis();
    
    public abstract boolean hasConfiguredValue(String paramString);
    
    public abstract boolean hasConfiguredValue(String paramString1, String paramString2);
    
    public abstract List<byte[]> zzBm();
  }
  
  @Deprecated
  public static abstract interface GetValueResult
    extends Result
  {
    @Deprecated
    public abstract ConfigApi.Value getValue();
  }
  
  @Deprecated
  public static abstract interface GetValuesResult
    extends Result
  {
    @Deprecated
    public abstract Map<String, ConfigApi.Value> getValues();
  }
  
  @Deprecated
  public static abstract interface Value
  {
    @Deprecated
    public abstract boolean getAsBoolean(boolean paramBoolean);
    
    @Deprecated
    public abstract byte[] getAsByteArray(byte[] paramArrayOfByte);
    
    @Deprecated
    public abstract long getAsLong(long paramLong);
    
    @Deprecated
    public abstract String getAsString(String paramString);
    
    @Deprecated
    public abstract boolean hasConfiguredValue();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/config/ConfigApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */