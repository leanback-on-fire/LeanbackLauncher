package com.google.firebase.remoteconfig;

public class FirebaseRemoteConfigFetchThrottledException
  extends FirebaseRemoteConfigFetchException
{
  private final long zzaVH;
  
  public FirebaseRemoteConfigFetchThrottledException(long paramLong)
  {
    this.zzaVH = paramLong;
  }
  
  public long getThrottleEndTimeMillis()
  {
    return this.zzaVH;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/remoteconfig/FirebaseRemoteConfigFetchThrottledException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */