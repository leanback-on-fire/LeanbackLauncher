package com.google.android.exoplayer2.util;

import android.os.HandlerThread;
import android.os.Process;

public final class PriorityHandlerThread
  extends HandlerThread
{
  private final int priority;
  
  public PriorityHandlerThread(String paramString, int paramInt)
  {
    super(paramString);
    this.priority = paramInt;
  }
  
  public void run()
  {
    Process.setThreadPriority(this.priority);
    super.run();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/PriorityHandlerThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */