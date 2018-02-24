package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class ExtractorMediaSource
  implements MediaSource, MediaSource.Listener
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE = 6;
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND = 3;
  public static final int MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA = -1;
  private final String customCacheKey;
  private final DataSource.Factory dataSourceFactory;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private final ExtractorsFactory extractorsFactory;
  private final int minLoadableRetryCount;
  private final Timeline.Period period;
  private MediaSource.Listener sourceListener;
  private Timeline timeline;
  private boolean timelineHasDuration;
  private final Uri uri;
  
  public ExtractorMediaSource(Uri paramUri, DataSource.Factory paramFactory, ExtractorsFactory paramExtractorsFactory, int paramInt, Handler paramHandler, EventListener paramEventListener, String paramString)
  {
    this.uri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.extractorsFactory = paramExtractorsFactory;
    this.minLoadableRetryCount = paramInt;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.customCacheKey = paramString;
    this.period = new Timeline.Period();
  }
  
  public ExtractorMediaSource(Uri paramUri, DataSource.Factory paramFactory, ExtractorsFactory paramExtractorsFactory, Handler paramHandler, EventListener paramEventListener)
  {
    this(paramUri, paramFactory, paramExtractorsFactory, -1, paramHandler, paramEventListener, null);
  }
  
  public ExtractorMediaSource(Uri paramUri, DataSource.Factory paramFactory, ExtractorsFactory paramExtractorsFactory, Handler paramHandler, EventListener paramEventListener, String paramString)
  {
    this(paramUri, paramFactory, paramExtractorsFactory, -1, paramHandler, paramEventListener, paramString);
  }
  
  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      return new ExtractorMediaPeriod(this.uri, this.dataSourceFactory.createDataSource(), this.extractorsFactory.createExtractors(), this.minLoadableRetryCount, this.eventHandler, this.eventListener, this, paramAllocator, this.customCacheKey);
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {}
  
  public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
  {
    boolean bool = false;
    if (paramTimeline.getPeriod(0, this.period).getDurationUs() != -9223372036854775807L) {
      bool = true;
    }
    if ((this.timelineHasDuration) && (!bool)) {
      return;
    }
    this.timeline = paramTimeline;
    this.timelineHasDuration = bool;
    this.sourceListener.onSourceInfoRefreshed(this.timeline, null);
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    this.sourceListener = paramListener;
    this.timeline = new SinglePeriodTimeline(-9223372036854775807L, false);
    paramListener.onSourceInfoRefreshed(this.timeline, null);
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((ExtractorMediaPeriod)paramMediaPeriod).release();
  }
  
  public void releaseSource()
  {
    this.sourceListener = null;
  }
  
  public static abstract interface EventListener
  {
    public abstract void onLoadError(IOException paramIOException);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/ExtractorMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */