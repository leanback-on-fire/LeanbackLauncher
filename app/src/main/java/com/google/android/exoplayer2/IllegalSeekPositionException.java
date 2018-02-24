package com.google.android.exoplayer2;

public final class IllegalSeekPositionException
  extends IllegalStateException
{
  public final long positionMs;
  public final Timeline timeline;
  public final int windowIndex;
  
  public IllegalSeekPositionException(Timeline paramTimeline, int paramInt, long paramLong)
  {
    this.timeline = paramTimeline;
    this.windowIndex = paramInt;
    this.positionMs = paramLong;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/IllegalSeekPositionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */