package com.google.android.tvlauncher.analytics;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

public class FragmentEventLogger
  implements EventLogger
{
  private static final String TAG = "EventLogger";
  private final Fragment mFragment;
  
  public FragmentEventLogger(Fragment paramFragment)
  {
    this.mFragment = paramFragment;
  }
  
  public void log(LogEvent paramLogEvent)
  {
    Activity localActivity = this.mFragment.getActivity();
    if (localActivity == null)
    {
      Log.e("EventLogger", "Cannot log fragment event: not attached to activity. Event: " + paramLogEvent);
      return;
    }
    if (!(localActivity instanceof EventLoggerProvider))
    {
      Log.e("EventLogger", "Cannot log fragment event: activity is not an EventLoggerProvider. Event: " + paramLogEvent);
      return;
    }
    ((EventLoggerProvider)localActivity).getEventLogger().log(paramLogEvent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/FragmentEventLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */