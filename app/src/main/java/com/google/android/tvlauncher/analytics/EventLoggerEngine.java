package com.google.android.tvlauncher.analytics;

import android.app.Activity;
import android.os.Bundle;

public abstract interface EventLoggerEngine
{
  public abstract boolean isEnabled();
  
  public abstract void logEvent(String paramString, Bundle paramBundle);
  
  public abstract void setCurrentScreen(Activity paramActivity, String paramString1, String paramString2);
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract void setUserProperty(String paramString1, String paramString2);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/EventLoggerEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */