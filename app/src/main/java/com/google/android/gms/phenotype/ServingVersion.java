package com.google.android.gms.phenotype;

public final class ServingVersion
{
  private final long zzcfN;
  
  private ServingVersion(long paramLong)
  {
    this.zzcfN = paramLong;
  }
  
  public static ServingVersion fromServer(long paramLong)
  {
    return new ServingVersion(paramLong);
  }
  
  public long getServingVersion()
  {
    return this.zzcfN;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/ServingVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */