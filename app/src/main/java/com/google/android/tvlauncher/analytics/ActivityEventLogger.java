package com.google.android.tvlauncher.analytics;

import android.app.Activity;

class ActivityEventLogger
  implements EventLogger
{
  private final Activity mActivity;
  private final String mName;
  
  public ActivityEventLogger(Activity paramActivity, String paramString)
  {
    this.mActivity = paramActivity;
    this.mName = paramString;
  }
  
  public void log(LogEvent paramLogEvent)
  {
    AppEventLogger.getInstance().log(paramLogEvent);
  }
  
  public void onResume()
  {
    AppEventLogger.getInstance().setName(this.mActivity, this.mName);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/ActivityEventLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */