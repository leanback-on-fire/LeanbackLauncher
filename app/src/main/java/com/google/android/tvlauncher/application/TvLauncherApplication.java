package com.google.android.tvlauncher.application;

import android.app.Application;
import com.google.android.tvlauncher.analytics.AppEventLogger;
import com.google.android.tvlauncher.analytics.FirebaseEventLoggerEngine;
import com.google.android.tvlauncher.appsview.AppsManager;

public class TvLauncherApplication
  extends Application
{
  public void onCreate()
  {
    super.onCreate();
    AppEventLogger.init(this, new FirebaseEventLoggerEngine(this));
    AppsManager.getInstance(this).registerUpdateListeners();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/application/TvLauncherApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */