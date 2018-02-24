package com.google.android.tvlauncher.analytics;

import android.content.ComponentName;
import android.content.Intent;

public final class LogUtils
{
  public static String getPackage(Intent paramIntent)
  {
    ComponentName localComponentName = paramIntent.getComponent();
    if (localComponentName != null) {
      return localComponentName.getPackageName();
    }
    return paramIntent.getPackage();
  }
  
  public static String programTypeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "movie";
    case 1: 
      return "tv-series";
    case 2: 
      return "tv-season";
    case 3: 
      return "tv-episode";
    case 4: 
      return "clip";
    case 5: 
      return "event";
    case 6: 
      return "channel";
    case 7: 
      return "track";
    case 8: 
      return "album";
    case 9: 
      return "artist";
    case 10: 
      return "playlist";
    }
    return "station";
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/LogUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */