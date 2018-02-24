package com.google.firebase.remoteconfig;

public class FirebaseRemoteConfigSettings
{
  private final boolean wV;
  
  private FirebaseRemoteConfigSettings(Builder paramBuilder)
  {
    this.wV = Builder.zza(paramBuilder);
  }
  
  public boolean isDeveloperModeEnabled()
  {
    return this.wV;
  }
  
  public static class Builder
  {
    private boolean wV = false;
    
    public FirebaseRemoteConfigSettings build()
    {
      return new FirebaseRemoteConfigSettings(this, null);
    }
    
    public Builder setDeveloperModeEnabled(boolean paramBoolean)
    {
      this.wV = paramBoolean;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */