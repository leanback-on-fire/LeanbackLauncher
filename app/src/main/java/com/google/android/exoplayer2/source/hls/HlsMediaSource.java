package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.Listener;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PrimaryPlaylistListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.List;

public final class HlsMediaSource
  implements MediaSource, HlsPlaylistTracker.PrimaryPlaylistListener
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private final DataSource.Factory dataSourceFactory;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Uri manifestUri;
  private final int minLoadableRetryCount;
  private HlsPlaylistTracker playlistTracker;
  private MediaSource.Listener sourceListener;
  
  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, int paramInt, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this.manifestUri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.minLoadableRetryCount = paramInt;
    this.eventDispatcher = new AdaptiveMediaSourceEventListener.EventDispatcher(paramHandler, paramAdaptiveMediaSourceEventListener);
  }
  
  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramUri, paramFactory, 3, paramHandler, paramAdaptiveMediaSourceEventListener);
  }
  
  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      return new HlsMediaPeriod(this.playlistTracker, this.dataSourceFactory, this.minLoadableRetryCount, this.eventDispatcher, paramAllocator, paramLong);
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    this.playlistTracker.maybeThrowPlaylistRefreshError();
  }
  
  public void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    long l3 = paramHlsMediaPlaylist.startOffsetUs;
    long l2;
    long l1;
    label64:
    long l4;
    boolean bool;
    if (this.playlistTracker.isLive()) {
      if (paramHlsMediaPlaylist.hasEndTag)
      {
        l2 = paramHlsMediaPlaylist.startTimeUs + paramHlsMediaPlaylist.durationUs;
        localObject = paramHlsMediaPlaylist.segments;
        l1 = l3;
        if (l3 == -9223372036854775807L)
        {
          if (!((List)localObject).isEmpty()) {
            break label126;
          }
          l1 = 0L;
        }
        l3 = paramHlsMediaPlaylist.durationUs;
        l4 = paramHlsMediaPlaylist.startTimeUs;
        if (paramHlsMediaPlaylist.hasEndTag) {
          break label156;
        }
        bool = true;
      }
    }
    label86:
    for (Object localObject = new SinglePeriodTimeline(l2, l3, l4, l1, true, bool);; localObject = new SinglePeriodTimeline(paramHlsMediaPlaylist.startTimeUs + paramHlsMediaPlaylist.durationUs, paramHlsMediaPlaylist.durationUs, paramHlsMediaPlaylist.startTimeUs, l1, true, false))
    {
      this.sourceListener.onSourceInfoRefreshed((Timeline)localObject, paramHlsMediaPlaylist);
      return;
      l2 = -9223372036854775807L;
      break;
      label126:
      l1 = ((HlsMediaPlaylist.Segment)((List)localObject).get(Math.max(0, ((List)localObject).size() - 3))).relativeStartTimeUs;
      break label64;
      label156:
      bool = false;
      break label86;
      l1 = l3;
      if (l3 == -9223372036854775807L) {
        l1 = 0L;
      }
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    if (this.playlistTracker == null) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkState(paramBoolean);
      this.playlistTracker = new HlsPlaylistTracker(this.manifestUri, this.dataSourceFactory, this.eventDispatcher, this.minLoadableRetryCount, this);
      this.sourceListener = paramListener;
      this.playlistTracker.start();
      return;
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((HlsMediaPeriod)paramMediaPeriod).release();
  }
  
  public void releaseSource()
  {
    if (this.playlistTracker != null)
    {
      this.playlistTracker.release();
      this.playlistTracker = null;
    }
    this.sourceListener = null;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/HlsMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */