package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import java.util.Collections;
import java.util.List;

public class DashManifest
{
  public final long availabilityStartTime;
  public final long duration;
  public final boolean dynamic;
  public final Uri location;
  public final long minBufferTime;
  public final long minUpdatePeriod;
  private final List<Period> periods;
  public final long suggestedPresentationDelay;
  public final long timeShiftBufferDepth;
  public final UtcTimingElement utcTiming;
  
  public DashManifest(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, long paramLong6, UtcTimingElement paramUtcTimingElement, Uri paramUri, List<Period> paramList)
  {
    this.availabilityStartTime = paramLong1;
    this.duration = paramLong2;
    this.minBufferTime = paramLong3;
    this.dynamic = paramBoolean;
    this.minUpdatePeriod = paramLong4;
    this.timeShiftBufferDepth = paramLong5;
    this.suggestedPresentationDelay = paramLong6;
    this.utcTiming = paramUtcTimingElement;
    this.location = paramUri;
    paramUtcTimingElement = paramList;
    if (paramList == null) {
      paramUtcTimingElement = Collections.emptyList();
    }
    this.periods = paramUtcTimingElement;
  }
  
  public final Period getPeriod(int paramInt)
  {
    return (Period)this.periods.get(paramInt);
  }
  
  public final int getPeriodCount()
  {
    return this.periods.size();
  }
  
  public final long getPeriodDurationMs(int paramInt)
  {
    if (paramInt == this.periods.size() - 1)
    {
      if (this.duration == -9223372036854775807L) {
        return -9223372036854775807L;
      }
      return this.duration - ((Period)this.periods.get(paramInt)).startMs;
    }
    return ((Period)this.periods.get(paramInt + 1)).startMs - ((Period)this.periods.get(paramInt)).startMs;
  }
  
  public final long getPeriodDurationUs(int paramInt)
  {
    return C.msToUs(getPeriodDurationMs(paramInt));
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/DashManifest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */