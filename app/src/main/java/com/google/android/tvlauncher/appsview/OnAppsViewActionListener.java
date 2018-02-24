package com.google.android.tvlauncher.appsview;

import android.content.Intent;

public abstract interface OnAppsViewActionListener
{
  public abstract void onExitEditModeView();
  
  public abstract void onLaunchApp(Intent paramIntent);
  
  public abstract void onShowAppInfo(String paramString);
  
  public abstract void onShowEditModeView(int paramInt1, int paramInt2);
  
  public abstract void onStoreLaunch(Intent paramIntent);
  
  public abstract void onToggleFavorite(LaunchItem paramLaunchItem);
  
  public abstract void onUninstallApp(String paramString);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/OnAppsViewActionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */