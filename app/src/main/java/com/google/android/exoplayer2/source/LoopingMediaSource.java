package com.google.android.exoplayer2.source;

import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class LoopingMediaSource
  implements MediaSource
{
  private static final String TAG = "LoopingMediaSource";
  private int childPeriodCount;
  private final MediaSource childSource;
  private final int loopCount;
  
  public LoopingMediaSource(MediaSource paramMediaSource)
  {
    this(paramMediaSource, Integer.MAX_VALUE);
  }
  
  public LoopingMediaSource(MediaSource paramMediaSource, int paramInt)
  {
    if (paramInt > 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.childSource = paramMediaSource;
      this.loopCount = paramInt;
      return;
    }
  }
  
  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    return this.childSource.createPeriod(paramInt % this.childPeriodCount, paramAllocator, paramLong);
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    this.childSource.maybeThrowSourceInfoRefreshError();
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, final MediaSource.Listener paramListener)
  {
    this.childSource.prepareSource(paramExoPlayer, false, new MediaSource.Listener()
    {
      public void onSourceInfoRefreshed(Timeline paramAnonymousTimeline, Object paramAnonymousObject)
      {
        LoopingMediaSource.access$002(LoopingMediaSource.this, paramAnonymousTimeline.getPeriodCount());
        paramListener.onSourceInfoRefreshed(new LoopingMediaSource.LoopingTimeline(paramAnonymousTimeline, LoopingMediaSource.this.loopCount), paramAnonymousObject);
      }
    });
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    this.childSource.releasePeriod(paramMediaPeriod);
  }
  
  public void releaseSource()
  {
    this.childSource.releaseSource();
  }
  
  private static final class LoopingTimeline
    extends Timeline
  {
    private final int childPeriodCount;
    private final Timeline childTimeline;
    private final int childWindowCount;
    private final int loopCount;
    
    public LoopingTimeline(Timeline paramTimeline, int paramInt)
    {
      this.childTimeline = paramTimeline;
      this.childPeriodCount = paramTimeline.getPeriodCount();
      this.childWindowCount = paramTimeline.getWindowCount();
      int i = Integer.MAX_VALUE / this.childPeriodCount;
      if (paramInt > i)
      {
        if (paramInt != Integer.MAX_VALUE) {
          Log.w("LoopingMediaSource", "Capped loops to avoid overflow: " + paramInt + " -> " + i);
        }
        this.loopCount = i;
        return;
      }
      this.loopCount = paramInt;
    }
    
    public int getIndexOfPeriod(Object paramObject)
    {
      if (!(paramObject instanceof Pair)) {}
      do
      {
        return -1;
        paramObject = (Pair)paramObject;
      } while (!(((Pair)paramObject).first instanceof Integer));
      int i = ((Integer)((Pair)paramObject).first).intValue();
      int j = this.childPeriodCount;
      return this.childTimeline.getIndexOfPeriod(((Pair)paramObject).second) + i * j;
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      this.childTimeline.getPeriod(paramInt % this.childPeriodCount, paramPeriod, paramBoolean);
      paramInt /= this.childPeriodCount;
      paramPeriod.windowIndex += this.childWindowCount * paramInt;
      if (paramBoolean) {
        paramPeriod.uid = Pair.create(Integer.valueOf(paramInt), paramPeriod.uid);
      }
      return paramPeriod;
    }
    
    public int getPeriodCount()
    {
      return this.childPeriodCount * this.loopCount;
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      this.childTimeline.getWindow(paramInt % this.childWindowCount, paramWindow, paramBoolean, paramLong);
      paramInt = paramInt / this.childWindowCount * this.childPeriodCount;
      paramWindow.firstPeriodIndex += paramInt;
      paramWindow.lastPeriodIndex += paramInt;
      return paramWindow;
    }
    
    public int getWindowCount()
    {
      return this.childWindowCount * this.loopCount;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/LoopingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */