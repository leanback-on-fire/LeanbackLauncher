package com.google.android.tvlauncher.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.android.tvlauncher.analytics.AppEventLogger;
import com.google.android.tvlauncher.analytics.EventLoggerEngine;
import com.google.android.tvlauncher.appsview.AppsManager;

public class TvLauncherApplication extends Application {
    public void onCreate() {
        super.onCreate();
        // todo implement
        AppEventLogger.init(this, new EventLoggerEngine() {
            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void logEvent(String paramString, Bundle paramBundle) {

            }

            @Override
            public void setCurrentScreen(Activity paramActivity, String paramString1, String paramString2) {

            }

            @Override
            public void setEnabled(boolean paramBoolean) {

            }

            @Override
            public void setUserProperty(String paramString1, String paramString2) {

            }
        });
        AppsManager.getInstance(this).registerUpdateListeners();
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/application/TvLauncherApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */