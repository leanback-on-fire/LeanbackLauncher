package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResults;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.phenotype.Configurations;
import com.google.android.gms.phenotype.DogfoodsToken;
import com.google.android.gms.phenotype.ExperimentTokens;
import com.google.android.gms.phenotype.Flag;
import com.google.android.gms.phenotype.FlagOverrides;
import com.google.android.gms.phenotype.Phenotype;
import com.google.android.gms.phenotype.PhenotypeApi;
import com.google.android.gms.phenotype.PhenotypeApi.ConfigurationsResult;
import com.google.android.gms.phenotype.PhenotypeApi.DogfoodsTokenResult;
import com.google.android.gms.phenotype.PhenotypeApi.ExperimentTokensResult;
import com.google.android.gms.phenotype.PhenotypeApi.FlagOverridesResult;
import com.google.android.gms.phenotype.PhenotypeApi.FlagResult;
import com.google.android.gms.phenotype.ServingVersion;

public class zzbcj
  implements PhenotypeApi
{
  private static long zzcfb = 0L;
  
  public static boolean zzl(long paramLong1, long paramLong2)
  {
    return (paramLong1 > paramLong2) || ((paramLong2 == Long.MAX_VALUE) && (paramLong1 != Long.MAX_VALUE) && (paramLong1 > 0L));
  }
  
  public PendingResult<Status> commitToConfiguration(GoogleApiClient paramGoogleApiClient, final String paramString)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zzdl(Status paramAnonymous2Status)
          {
            zzbcj.13.this.zzb(paramAnonymous2Status);
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zzb(local1, paramString);
      }
      
      public Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  public PendingResult<PhenotypeApi.FlagOverridesResult> deleteFlagOverrides(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2, final String paramString3)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, FlagOverrides paramAnonymous2FlagOverrides)
          {
            zzbcj.7.this.zzb(new zzbcj.zzf(paramAnonymous2Status, paramAnonymous2FlagOverrides));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zzb(local1, paramString1, paramString2, paramString3);
      }
      
      public PhenotypeApi.FlagOverridesResult zzdq(Status paramAnonymousStatus)
      {
        return new zzbcj.zzf(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<PhenotypeApi.ConfigurationsResult> getAlternateConfigurationSnapshot(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2, final String paramString3, final String paramString4)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, Configurations paramAnonymous2Configurations)
          {
            zzbcj.9.this.zzb(new zzbcj.zzc(paramAnonymous2Status, paramAnonymous2Configurations));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString1, paramString2, paramString3, paramString4);
      }
      
      public PhenotypeApi.ConfigurationsResult zzdp(Status paramAnonymousStatus)
      {
        return new zzbcj.zzc(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<PhenotypeApi.ConfigurationsResult> getCommittedConfiguration(GoogleApiClient paramGoogleApiClient, final String paramString)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zzb(Status paramAnonymous2Status, Configurations paramAnonymous2Configurations)
          {
            zzbcj.3.this.zzb(new zzbcj.zzc(paramAnonymous2Status, paramAnonymous2Configurations));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zzc(local1, paramString);
      }
      
      public PhenotypeApi.ConfigurationsResult zzdp(Status paramAnonymousStatus)
      {
        return new zzbcj.zzc(paramAnonymousStatus, null);
      }
    });
  }
  
  @Deprecated
  public PendingResult<PhenotypeApi.ConfigurationsResult> getConfigurationSnapshot(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2)
  {
    return getConfigurationSnapshot(paramGoogleApiClient, paramString1, paramString2, null);
  }
  
  public PendingResult<PhenotypeApi.ConfigurationsResult> getConfigurationSnapshot(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2, final String paramString3)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, Configurations paramAnonymous2Configurations)
          {
            zzbcj.12.this.zzb(new zzbcj.zzc(paramAnonymous2Status, paramAnonymous2Configurations));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString1, paramString2, paramString3);
      }
      
      public PhenotypeApi.ConfigurationsResult zzdp(Status paramAnonymousStatus)
      {
        return new zzbcj.zzc(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<PhenotypeApi.DogfoodsTokenResult> getDogfoodsToken(GoogleApiClient paramGoogleApiClient)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, DogfoodsToken paramAnonymous2DogfoodsToken)
          {
            zzbcj.16.this.zzb(new zzbcj.zzd(paramAnonymous2Status, paramAnonymous2DogfoodsToken));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1);
      }
      
      public PhenotypeApi.DogfoodsTokenResult zzds(Status paramAnonymousStatus)
      {
        return new zzbcj.zzd(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<PhenotypeApi.ExperimentTokensResult> getExperiments(GoogleApiClient paramGoogleApiClient, final String paramString)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, ExperimentTokens paramAnonymous2ExperimentTokens)
          {
            zzbcj.15.this.zzb(new zzbcj.zze(paramAnonymous2Status, paramAnonymous2ExperimentTokens));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zzb(local1, paramString, null);
      }
      
      public PhenotypeApi.ExperimentTokensResult zzdr(Status paramAnonymousStatus)
      {
        return new zzbcj.zze(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<PhenotypeApi.ExperimentTokensResult> getExperimentsForLogging(GoogleApiClient paramGoogleApiClient, final String paramString)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, ExperimentTokens paramAnonymous2ExperimentTokens)
          {
            zzbcj.14.this.zzb(new zzbcj.zze(paramAnonymous2Status, paramAnonymous2ExperimentTokens));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zzb(local1, null, paramString);
      }
      
      public PhenotypeApi.ExperimentTokensResult zzdr(Status paramAnonymousStatus)
      {
        return new zzbcj.zze(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<PhenotypeApi.FlagResult> getFlag(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2, final int paramInt)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, Flag paramAnonymous2Flag)
          {
            zzbcj.2.this.zzb(new zzbcj.zzg(paramAnonymous2Status, paramAnonymous2Flag));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString1, paramString2, paramInt);
      }
      
      public PhenotypeApi.FlagResult zzdo(Status paramAnonymousStatus)
      {
        return new zzbcj.zzg(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<PhenotypeApi.FlagOverridesResult> listFlagOverrides(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2, final String paramString3)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, FlagOverrides paramAnonymous2FlagOverrides)
          {
            zzbcj.8.this.zzb(new zzbcj.zzf(paramAnonymous2Status, paramAnonymous2FlagOverrides));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zzc(local1, paramString1, paramString2, paramString3);
      }
      
      public PhenotypeApi.FlagOverridesResult zzdq(Status paramAnonymousStatus)
      {
        return new zzbcj.zzf(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<Status> register(GoogleApiClient paramGoogleApiClient, final String paramString, final int paramInt, final String[] paramArrayOfString, final byte[] paramArrayOfByte)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zzdi(Status paramAnonymous2Status)
          {
            zzbcj.1.this.zzb(paramAnonymous2Status);
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString, paramInt, paramArrayOfString, paramArrayOfByte);
      }
      
      public Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  public PendingResult<PhenotypeApi.ConfigurationsResult> registerSync(GoogleApiClient paramGoogleApiClient, final String paramString1, final int paramInt, final String[] paramArrayOfString, final byte[] paramArrayOfByte, final String paramString2, final String paramString3)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zza(Status paramAnonymous2Status, Configurations paramAnonymous2Configurations)
          {
            zzbcj.5.this.zzb(new zzbcj.zzc(paramAnonymous2Status, paramAnonymous2Configurations));
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString1, paramInt, paramArrayOfString, paramArrayOfByte, paramString2, paramString3);
      }
      
      public PhenotypeApi.ConfigurationsResult zzdp(Status paramAnonymousStatus)
      {
        return new zzbcj.zzc(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<Status> setDogfoodsToken(GoogleApiClient paramGoogleApiClient, final byte[] paramArrayOfByte)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zzdm(Status paramAnonymous2Status)
          {
            zzbcj.17.this.zzb(paramAnonymous2Status);
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramArrayOfByte);
      }
      
      public Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  public PendingResult<Status> setFlagOverride(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2, final String paramString3, final int paramInt1, final int paramInt2, final String paramString4)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zzdn(Status paramAnonymous2Status)
          {
            zzbcj.6.this.zzb(paramAnonymous2Status);
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4);
      }
      
      public Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  public PendingResult<Status> syncAfter(GoogleApiClient paramGoogleApiClient, final ServingVersion paramServingVersion)
  {
    try
    {
      if (!zzl(paramServingVersion.getServingVersion(), zzcfb))
      {
        paramGoogleApiClient = PendingResults.immediatePendingResult(Status.zzaLc);
        return paramGoogleApiClient;
      }
      paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
      {
        protected void zza(zzbck paramAnonymouszzbck)
          throws RemoteException
        {
          zzbcj.zza local1 = new zzbcj.zza()
          {
            public void zzdh(Status paramAnonymous2Status)
            {
              zzbcj.4.this.zzb(paramAnonymous2Status);
              if (paramAnonymous2Status.isSuccess()) {
                try
                {
                  zzbcj.zzaL(zzbcj.4.this.zzcfi.getServingVersion());
                  return;
                }
                finally {}
              }
            }
          };
          ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramServingVersion.getServingVersion());
        }
        
        public Status zzd(Status paramAnonymousStatus)
        {
          return paramAnonymousStatus;
        }
      });
    }
    finally {}
  }
  
  public PendingResult<Status> unRegister(GoogleApiClient paramGoogleApiClient, final String paramString)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zzdk(Status paramAnonymous2Status)
          {
            zzbcj.11.this.zzb(paramAnonymous2Status);
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString);
      }
      
      public Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  public PendingResult<Status> weakRegister(GoogleApiClient paramGoogleApiClient, final String paramString, final int paramInt, final String[] paramArrayOfString, final int[] paramArrayOfInt, final byte[] paramArrayOfByte)
  {
    paramGoogleApiClient.zza(new zzb(paramGoogleApiClient)
    {
      protected void zza(zzbck paramAnonymouszzbck)
        throws RemoteException
      {
        zzbcj.zza local1 = new zzbcj.zza()
        {
          public void zzdj(Status paramAnonymous2Status)
          {
            zzbcj.10.this.zzb(paramAnonymous2Status);
          }
        };
        ((zzbci)paramAnonymouszzbck.zzzw()).zza(local1, paramString, paramInt, paramArrayOfString, paramArrayOfInt, paramArrayOfByte);
      }
      
      public Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  public static class zza
    extends zzbch.zza
  {
    public void zza(Status paramStatus, Configurations paramConfigurations)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zza(Status paramStatus, DogfoodsToken paramDogfoodsToken)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zza(Status paramStatus, ExperimentTokens paramExperimentTokens)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zza(Status paramStatus, Flag paramFlag)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zza(Status paramStatus, FlagOverrides paramFlagOverrides)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzb(Status paramStatus, Configurations paramConfigurations)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzdh(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzdi(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzdj(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzdk(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzdl(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzdm(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zzdn(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  static abstract class zzb<R extends Result>
    extends zzyr.zza<R, zzbck>
  {
    public zzb(GoogleApiClient paramGoogleApiClient)
    {
      super(paramGoogleApiClient);
    }
  }
  
  static class zzc
    implements PhenotypeApi.ConfigurationsResult
  {
    private final Status zzaiT;
    private final Configurations zzcgi;
    
    public zzc(Status paramStatus, Configurations paramConfigurations)
    {
      this.zzaiT = paramStatus;
      this.zzcgi = paramConfigurations;
    }
    
    public Configurations getConfigurations()
    {
      return this.zzcgi;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
  }
  
  static class zzd
    implements PhenotypeApi.DogfoodsTokenResult
  {
    private final Status zzaiT;
    private final DogfoodsToken zzcgj;
    
    public zzd(Status paramStatus, DogfoodsToken paramDogfoodsToken)
    {
      this.zzaiT = paramStatus;
      this.zzcgj = paramDogfoodsToken;
    }
    
    public DogfoodsToken getDogfoodsToken()
    {
      return this.zzcgj;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
  }
  
  static class zze
    implements PhenotypeApi.ExperimentTokensResult
  {
    private final Status zzaiT;
    private final ExperimentTokens zzcgk;
    
    public zze(Status paramStatus, ExperimentTokens paramExperimentTokens)
    {
      this.zzaiT = paramStatus;
      this.zzcgk = paramExperimentTokens;
    }
    
    public ExperimentTokens getExperimentTokens()
    {
      return this.zzcgk;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
  }
  
  static class zzf
    implements PhenotypeApi.FlagOverridesResult
  {
    private final Status zzaiT;
    private final FlagOverrides zzcgl;
    
    public zzf(Status paramStatus, FlagOverrides paramFlagOverrides)
    {
      this.zzaiT = paramStatus;
      this.zzcgl = paramFlagOverrides;
    }
    
    public FlagOverrides getFlagOverrides()
    {
      return this.zzcgl;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
  }
  
  static class zzg
    implements PhenotypeApi.FlagResult
  {
    private final Status zzaiT;
    private final Flag zzcgm;
    
    public zzg(Status paramStatus, Flag paramFlag)
    {
      this.zzaiT = paramStatus;
      this.zzcgm = paramFlag;
    }
    
    public Flag getFlag()
    {
      return this.zzcgm;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbcj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */