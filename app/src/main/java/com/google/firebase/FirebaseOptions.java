package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzaa.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzam;
import com.google.android.gms.common.util.zzw;

public final class FirebaseOptions
{
  private final String hG;
  private final String hH;
  private final String hI;
  private final String hJ;
  private final String hK;
  private final String zzaxb;
  
  private FirebaseOptions(@NonNull String paramString1, @NonNull String paramString2, @Nullable String paramString3, @Nullable String paramString4, @Nullable String paramString5, @Nullable String paramString6)
  {
    if (!zzw.zzdj(paramString1)) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "ApplicationId must be set.");
      this.zzaxb = paramString1;
      this.hG = paramString2;
      this.hH = paramString3;
      this.hI = paramString4;
      this.hJ = paramString5;
      this.hK = paramString6;
      return;
    }
  }
  
  public static FirebaseOptions fromResource(Context paramContext)
  {
    paramContext = new zzam(paramContext);
    String str = paramContext.getString("google_app_id");
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    return new FirebaseOptions(str, paramContext.getString("google_api_key"), paramContext.getString("firebase_database_url"), paramContext.getString("ga_trackingId"), paramContext.getString("gcm_defaultSenderId"), paramContext.getString("google_storage_bucket"));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FirebaseOptions)) {}
    do
    {
      return false;
      paramObject = (FirebaseOptions)paramObject;
    } while ((!zzaa.equal(this.zzaxb, ((FirebaseOptions)paramObject).zzaxb)) || (!zzaa.equal(this.hG, ((FirebaseOptions)paramObject).hG)) || (!zzaa.equal(this.hH, ((FirebaseOptions)paramObject).hH)) || (!zzaa.equal(this.hI, ((FirebaseOptions)paramObject).hI)) || (!zzaa.equal(this.hJ, ((FirebaseOptions)paramObject).hJ)) || (!zzaa.equal(this.hK, ((FirebaseOptions)paramObject).hK)));
    return true;
  }
  
  public String getApiKey()
  {
    return this.hG;
  }
  
  public String getApplicationId()
  {
    return this.zzaxb;
  }
  
  public String getDatabaseUrl()
  {
    return this.hH;
  }
  
  public String getGcmSenderId()
  {
    return this.hJ;
  }
  
  public String getStorageBucket()
  {
    return this.hK;
  }
  
  public int hashCode()
  {
    return zzaa.hashCode(new Object[] { this.zzaxb, this.hG, this.hH, this.hI, this.hJ, this.hK });
  }
  
  public String toString()
  {
    return zzaa.zzB(this).zzh("applicationId", this.zzaxb).zzh("apiKey", this.hG).zzh("databaseUrl", this.hH).zzh("gcmSenderId", this.hJ).zzh("storageBucket", this.hK).toString();
  }
  
  public static final class Builder
  {
    private String hG;
    private String hH;
    private String hI;
    private String hJ;
    private String hK;
    private String zzaxb;
    
    public Builder() {}
    
    public Builder(FirebaseOptions paramFirebaseOptions)
    {
      this.zzaxb = FirebaseOptions.zza(paramFirebaseOptions);
      this.hG = FirebaseOptions.zzb(paramFirebaseOptions);
      this.hH = FirebaseOptions.zzc(paramFirebaseOptions);
      this.hI = FirebaseOptions.zzd(paramFirebaseOptions);
      this.hJ = FirebaseOptions.zze(paramFirebaseOptions);
      this.hK = FirebaseOptions.zzf(paramFirebaseOptions);
    }
    
    public FirebaseOptions build()
    {
      return new FirebaseOptions(this.zzaxb, this.hG, this.hH, this.hI, this.hJ, this.hK, null);
    }
    
    public Builder setApiKey(@NonNull String paramString)
    {
      this.hG = zzac.zzi(paramString, "ApiKey must be set.");
      return this;
    }
    
    public Builder setApplicationId(@NonNull String paramString)
    {
      this.zzaxb = zzac.zzi(paramString, "ApplicationId must be set.");
      return this;
    }
    
    public Builder setDatabaseUrl(@Nullable String paramString)
    {
      this.hH = paramString;
      return this;
    }
    
    public Builder setGcmSenderId(@Nullable String paramString)
    {
      this.hJ = paramString;
      return this;
    }
    
    public Builder setStorageBucket(@Nullable String paramString)
    {
      this.hK = paramString;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/FirebaseOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */