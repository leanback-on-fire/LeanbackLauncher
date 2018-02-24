package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.upstream.Allocator;
import java.io.IOException;

public abstract interface MediaSource
{
  public abstract MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong);
  
  public abstract void maybeThrowSourceInfoRefreshError()
    throws IOException;
  
  public abstract void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, Listener paramListener);
  
  public abstract void releasePeriod(MediaPeriod paramMediaPeriod);
  
  public abstract void releaseSource();
  
  public static abstract interface Listener
  {
    public abstract void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/MediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */