package com.google.android.tvlauncher.analytics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class FirebaseEventLoggerEngine
  implements EventLoggerEngine
{
  private static final String ANALYTICS_LOGGING_ENABLED_FLAG = "analytics_logging_enabled";
  private static final long CONFIG_CACHE_EXPIRATION = 14400L;
  private static final String PREF_LOGGING_ENABLED_BY_SERVER = "logging_enabled_by_server";
  private static final String PREF_LOGGING_ENABLED_BY_USER = "logging_enabled_by_user";
  private boolean mEnabledByServer;
  private boolean mEnabledByUser;
  private final FirebaseAnalytics mFirebaseAnalytics;
  private final SharedPreferences mPreferences;
  
  public FirebaseEventLoggerEngine(Context paramContext)
  {
    this.mPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
    this.mEnabledByUser = this.mPreferences.getBoolean("logging_enabled_by_user", false);
    this.mEnabledByServer = this.mPreferences.getBoolean("logging_enabled_by_server", false);
    this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(paramContext);
    fetchConfig(paramContext);
    updateFirebaseAnalyticsConfig();
  }
  
  private void fetchConfig(final Context paramContext)
  {
    if (Build.TYPE.equals("unknown"))
    {
      this.mEnabledByServer = true;
      return;
    }
    paramContext = FirebaseRemoteConfig.getInstance(paramContext);
    paramContext.fetch(14400L).addOnCompleteListener(new OnCompleteListener()
    {
      public void onComplete(@NonNull Task<Void> paramAnonymousTask)
      {
        if (paramAnonymousTask.isSuccessful()) {
          paramContext.activateFetched();
        }
        FirebaseEventLoggerEngine.access$002(FirebaseEventLoggerEngine.this, paramContext.getBoolean("analytics_logging_enabled"));
        FirebaseEventLoggerEngine.this.mPreferences.edit().putBoolean("logging_enabled_by_server", FirebaseEventLoggerEngine.this.mEnabledByServer).apply();
        FirebaseEventLoggerEngine.this.updateFirebaseAnalyticsConfig();
      }
    });
  }
  
  private void updateFirebaseAnalyticsConfig()
  {
    this.mFirebaseAnalytics.setAnalyticsCollectionEnabled(isEnabled());
  }
  
  public boolean isEnabled()
  {
    return (this.mEnabledByUser) && (this.mEnabledByServer);
  }
  
  public void logEvent(String paramString, Bundle paramBundle)
  {
    this.mFirebaseAnalytics.logEvent(paramString, paramBundle);
  }
  
  public void setCurrentScreen(Activity paramActivity, String paramString1, String paramString2)
  {
    this.mFirebaseAnalytics.setCurrentScreen(paramActivity, paramString1, paramString2);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabledByUser = paramBoolean;
    this.mPreferences.edit().putBoolean("logging_enabled_by_user", paramBoolean).apply();
    updateFirebaseAnalyticsConfig();
  }
  
  public void setUserProperty(String paramString1, String paramString2)
  {
    this.mFirebaseAnalytics.setUserProperty(paramString1, paramString2);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/FirebaseEventLoggerEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */