package com.google.android.tvlauncher.appsview;

abstract interface InstallingLaunchItemListener
{
  public abstract void onInstallingLaunchItemAdded(LaunchItem paramLaunchItem);
  
  public abstract void onInstallingLaunchItemChanged(LaunchItem paramLaunchItem);
  
  public abstract void onInstallingLaunchItemRemoved(LaunchItem paramLaunchItem, boolean paramBoolean);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/InstallingLaunchItemListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */