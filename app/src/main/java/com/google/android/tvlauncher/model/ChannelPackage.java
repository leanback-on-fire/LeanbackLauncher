package com.google.android.tvlauncher.model;

public class ChannelPackage
{
  private int mChannelCount;
  private String mPackageName;
  
  public ChannelPackage(String paramString, int paramInt)
  {
    this.mPackageName = paramString;
    this.mChannelCount = paramInt;
  }
  
  public int getChannelCount()
  {
    return this.mChannelCount;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public String toString()
  {
    return "ChannelPackage{mPackageName='" + this.mPackageName + '\'' + ", mChannelCount=" + this.mChannelCount + '}';
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/model/ChannelPackage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */