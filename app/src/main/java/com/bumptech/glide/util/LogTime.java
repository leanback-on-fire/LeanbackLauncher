package com.bumptech.glide.util;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.SystemClock;

public final class LogTime
{
  private static final double MILLIS_MULTIPLIER;
  
  static
  {
    double d = 1.0D;
    if (17 <= Build.VERSION.SDK_INT) {
      d = 1.0D / Math.pow(10.0D, 6.0D);
    }
    MILLIS_MULTIPLIER = d;
  }
  
  public static double getElapsedMillis(long paramLong)
  {
    return (getLogTime() - paramLong) * MILLIS_MULTIPLIER;
  }
  
  @TargetApi(17)
  public static long getLogTime()
  {
    if (17 <= Build.VERSION.SDK_INT) {
      return SystemClock.elapsedRealtimeNanos();
    }
    return System.currentTimeMillis();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/util/LogTime.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */