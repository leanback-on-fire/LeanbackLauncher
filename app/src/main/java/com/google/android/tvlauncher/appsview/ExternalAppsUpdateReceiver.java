package com.google.android.tvlauncher.appsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ExternalAppsUpdateReceiver
  extends BroadcastReceiver
{
  public static IntentFilter getIntentFilter()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
    localIntentFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
    return localIntentFilter;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    AppsManager.getInstance(paramContext).refreshLaunchItems();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/ExternalAppsUpdateReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */