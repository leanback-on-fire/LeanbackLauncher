package com.google.android.gms.internal;

import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class zzcbw
  implements FirebaseRemoteConfigInfo
{
  private long xd;
  private int xe;
  private FirebaseRemoteConfigSettings xf;
  
  public FirebaseRemoteConfigSettings getConfigSettings()
  {
    return this.xf;
  }
  
  public long getFetchTimeMillis()
  {
    return this.xd;
  }
  
  public int getLastFetchStatus()
  {
    return this.xe;
  }
  
  public void setConfigSettings(FirebaseRemoteConfigSettings paramFirebaseRemoteConfigSettings)
  {
    this.xf = paramFirebaseRemoteConfigSettings;
  }
  
  public void zzBf(int paramInt)
  {
    this.xe = paramInt;
  }
  
  public void zzbl(long paramLong)
  {
    this.xd = paramLong;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */