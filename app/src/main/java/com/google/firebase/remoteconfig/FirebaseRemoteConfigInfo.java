package com.google.firebase.remoteconfig;

public abstract interface FirebaseRemoteConfigInfo
{
  public abstract FirebaseRemoteConfigSettings getConfigSettings();
  
  public abstract long getFetchTimeMillis();
  
  public abstract int getLastFetchStatus();
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/remoteconfig/FirebaseRemoteConfigInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */