package com.google.firebase.remoteconfig;

public abstract interface FirebaseRemoteConfigValue
{
  public abstract boolean asBoolean()
    throws IllegalArgumentException;
  
  public abstract byte[] asByteArray();
  
  public abstract double asDouble()
    throws IllegalArgumentException;
  
  public abstract long asLong()
    throws IllegalArgumentException;
  
  public abstract String asString();
  
  public abstract int getSource();
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/remoteconfig/FirebaseRemoteConfigValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */