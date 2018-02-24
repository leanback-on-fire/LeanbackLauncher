package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;

public final class ClippingMediaSource
  implements MediaSource, MediaSource.Listener
{
  private ClippingTimeline clippingTimeline;
  private final long endUs;
  private final ArrayList<ClippingMediaPeriod> mediaPeriods;
  private final MediaSource mediaSource;
  private MediaSource.Listener sourceListener;
  private final long startUs;
  
  public ClippingMediaSource(MediaSource paramMediaSource, long paramLong1, long paramLong2)
  {
    if (paramLong1 >= 0L) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.mediaSource = ((MediaSource)Assertions.checkNotNull(paramMediaSource));
      this.startUs = paramLong1;
      this.endUs = paramLong2;
      this.mediaPeriods = new ArrayList();
      return;
    }
  }
  
  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    paramAllocator = new ClippingMediaPeriod(this.mediaSource.createPeriod(paramInt, paramAllocator, this.startUs + paramLong));
    this.mediaPeriods.add(paramAllocator);
    paramAllocator.setClipping(this.clippingTimeline.startUs, this.clippingTimeline.endUs);
    return paramAllocator;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    this.mediaSource.maybeThrowSourceInfoRefreshError();
  }
  
  public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
  {
    this.clippingTimeline = new ClippingTimeline(paramTimeline, this.startUs, this.endUs);
    this.sourceListener.onSourceInfoRefreshed(this.clippingTimeline, paramObject);
    long l2 = this.clippingTimeline.startUs;
    if (this.clippingTimeline.endUs == -9223372036854775807L) {}
    for (long l1 = Long.MIN_VALUE;; l1 = this.clippingTimeline.endUs)
    {
      int j = this.mediaPeriods.size();
      int i = 0;
      while (i < j)
      {
        ((ClippingMediaPeriod)this.mediaPeriods.get(i)).setClipping(l2, l1);
        i += 1;
      }
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    this.sourceListener = paramListener;
    this.mediaSource.prepareSource(paramExoPlayer, false, this);
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    Assertions.checkState(this.mediaPeriods.remove(paramMediaPeriod));
    this.mediaSource.releasePeriod(((ClippingMediaPeriod)paramMediaPeriod).mediaPeriod);
  }
  
  public void releaseSource()
  {
    this.mediaSource.releaseSource();
  }
  
  private static final class ClippingTimeline
    extends Timeline
  {
    private final long endUs;
    private final long startUs;
    private final Timeline timeline;
    
    public ClippingTimeline(Timeline paramTimeline, long paramLong1, long paramLong2)
    {
      if (paramTimeline.getWindowCount() == 1)
      {
        bool = true;
        Assertions.checkArgument(bool);
        if (paramTimeline.getPeriodCount() != 1) {
          break label202;
        }
        bool = true;
        label31:
        Assertions.checkArgument(bool);
        Timeline.Window localWindow = paramTimeline.getWindow(0, new Timeline.Window(), false);
        if (localWindow.isDynamic) {
          break label208;
        }
        bool = true;
        label62:
        Assertions.checkArgument(bool);
        if (paramLong2 != Long.MIN_VALUE) {
          break label214;
        }
        paramLong2 = localWindow.durationUs;
        label83:
        if (localWindow.durationUs != -9223372036854775807L)
        {
          if ((paramLong1 != 0L) && (!localWindow.isSeekable)) {
            break label217;
          }
          bool = true;
          label112:
          Assertions.checkArgument(bool);
          if (paramLong2 > localWindow.durationUs) {
            break label223;
          }
          bool = true;
          label131:
          Assertions.checkArgument(bool);
          if (paramLong1 > paramLong2) {
            break label229;
          }
          bool = true;
          label146:
          Assertions.checkArgument(bool);
        }
        if (paramTimeline.getPeriod(0, new Timeline.Period()).getPositionInWindowUs() != 0L) {
          break label235;
        }
      }
      label202:
      label208:
      label214:
      label217:
      label223:
      label229:
      label235:
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool);
        this.timeline = paramTimeline;
        this.startUs = paramLong1;
        this.endUs = paramLong2;
        return;
        bool = false;
        break;
        bool = false;
        break label31;
        bool = false;
        break label62;
        break label83;
        bool = false;
        break label112;
        bool = false;
        break label131;
        bool = false;
        break label146;
      }
    }
    
    public int getIndexOfPeriod(Object paramObject)
    {
      return this.timeline.getIndexOfPeriod(paramObject);
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      long l = -9223372036854775807L;
      paramPeriod = this.timeline.getPeriod(0, paramPeriod, paramBoolean);
      if (this.endUs != -9223372036854775807L) {
        l = this.endUs - this.startUs;
      }
      paramPeriod.durationUs = l;
      return paramPeriod;
    }
    
    public int getPeriodCount()
    {
      return 1;
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      paramWindow = this.timeline.getWindow(0, paramWindow, paramBoolean, paramLong);
      if (this.endUs != -9223372036854775807L)
      {
        paramLong = this.endUs - this.startUs;
        paramWindow.durationUs = paramLong;
        if (paramWindow.defaultPositionUs != -9223372036854775807L)
        {
          paramWindow.defaultPositionUs = Math.max(paramWindow.defaultPositionUs, this.startUs);
          if (this.endUs != -9223372036854775807L) {
            break label166;
          }
        }
      }
      label166:
      for (paramLong = paramWindow.defaultPositionUs;; paramLong = Math.min(paramWindow.defaultPositionUs, this.endUs))
      {
        paramWindow.defaultPositionUs = paramLong;
        paramWindow.defaultPositionUs -= this.startUs;
        paramLong = C.usToMs(this.startUs);
        if (paramWindow.presentationStartTimeMs != -9223372036854775807L) {
          paramWindow.presentationStartTimeMs += paramLong;
        }
        if (paramWindow.windowStartTimeMs != -9223372036854775807L) {
          paramWindow.windowStartTimeMs += paramLong;
        }
        return paramWindow;
        paramLong = -9223372036854775807L;
        break;
      }
    }
    
    public int getWindowCount()
    {
      return 1;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/ClippingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */