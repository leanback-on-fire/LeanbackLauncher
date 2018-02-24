package com.google.android.tvlauncher.analytics;

import android.app.Activity;

public abstract class LoggingActivity
  extends Activity
  implements EventLoggerProvider
{
  protected final ActivityEventLogger mActivityEventLogger;
  
  public LoggingActivity(String paramString)
  {
    this.mActivityEventLogger = new ActivityEventLogger(this, paramString);
  }
  
  public EventLogger getEventLogger()
  {
    return this.mActivityEventLogger;
  }
  
  protected void onResume()
  {
    super.onResume();
    this.mActivityEventLogger.onResume();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/LoggingActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */