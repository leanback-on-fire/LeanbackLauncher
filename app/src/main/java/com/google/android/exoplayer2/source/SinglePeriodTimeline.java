package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.util.Assertions;

public final class SinglePeriodTimeline
  extends Timeline
{
  private static final Object ID = new Object();
  private final boolean isDynamic;
  private final boolean isSeekable;
  private final long periodDurationUs;
  private final long windowDefaultStartPositionUs;
  private final long windowDurationUs;
  private final long windowPositionInPeriodUs;
  
  public SinglePeriodTimeline(long paramLong1, long paramLong2, long paramLong3, long paramLong4, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.periodDurationUs = paramLong1;
    this.windowDurationUs = paramLong2;
    this.windowPositionInPeriodUs = paramLong3;
    this.windowDefaultStartPositionUs = paramLong4;
    this.isSeekable = paramBoolean1;
    this.isDynamic = paramBoolean2;
  }
  
  public SinglePeriodTimeline(long paramLong, boolean paramBoolean)
  {
    this(paramLong, paramLong, 0L, 0L, paramBoolean, false);
  }
  
  public int getIndexOfPeriod(Object paramObject)
  {
    if (ID.equals(paramObject)) {
      return 0;
    }
    return -1;
  }
  
  public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    Assertions.checkIndex(paramInt, 0, 1);
    if (paramBoolean) {}
    for (Object localObject = ID;; localObject = null) {
      return paramPeriod.set(localObject, localObject, 0, this.periodDurationUs, -this.windowPositionInPeriodUs);
    }
  }
  
  public int getPeriodCount()
  {
    return 1;
  }
  
  public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    Assertions.checkIndex(paramInt, 0, 1);
    if (paramBoolean) {}
    for (Object localObject = ID;; localObject = null)
    {
      long l2 = this.windowDefaultStartPositionUs;
      long l1 = l2;
      if (this.isDynamic)
      {
        paramLong = l2 + paramLong;
        l1 = paramLong;
        if (paramLong > this.windowDurationUs) {
          l1 = -9223372036854775807L;
        }
      }
      return paramWindow.set(localObject, -9223372036854775807L, -9223372036854775807L, this.isSeekable, this.isDynamic, l1, this.windowDurationUs, 0, 0, this.windowPositionInPeriodUs);
    }
  }
  
  public int getWindowCount()
  {
    return 1;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/SinglePeriodTimeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */