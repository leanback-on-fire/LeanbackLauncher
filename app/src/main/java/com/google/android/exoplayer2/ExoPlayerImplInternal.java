package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.Listener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.PriorityHandlerThread;
import com.google.android.exoplayer2.util.StandaloneMediaClock;
import com.google.android.exoplayer2.util.TraceUtil;
import java.io.IOException;

final class ExoPlayerImplInternal
  implements Handler.Callback, MediaPeriod.Callback, TrackSelector.InvalidationListener, MediaSource.Listener
{
  private static final int IDLE_INTERVAL_MS = 1000;
  private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
  private static final int MSG_CUSTOM = 10;
  private static final int MSG_DO_SOME_WORK = 2;
  public static final int MSG_ERROR = 7;
  public static final int MSG_LOADING_CHANGED = 2;
  private static final int MSG_PERIOD_PREPARED = 7;
  public static final int MSG_POSITION_DISCONTINUITY = 5;
  private static final int MSG_PREPARE = 0;
  private static final int MSG_REFRESH_SOURCE_INFO = 6;
  private static final int MSG_RELEASE = 5;
  public static final int MSG_SEEK_ACK = 4;
  private static final int MSG_SEEK_TO = 3;
  private static final int MSG_SET_PLAY_WHEN_READY = 1;
  private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 8;
  public static final int MSG_SOURCE_INFO_REFRESHED = 6;
  public static final int MSG_STATE_CHANGED = 1;
  private static final int MSG_STOP = 4;
  public static final int MSG_TRACKS_CHANGED = 3;
  private static final int MSG_TRACK_SELECTION_INVALIDATED = 9;
  private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
  private static final int RENDERER_TIMESTAMP_OFFSET_US = 60000000;
  private static final int RENDERING_INTERVAL_MS = 10;
  private static final String TAG = "ExoPlayerImplInternal";
  private int customMessagesProcessed;
  private int customMessagesSent;
  private long elapsedRealtimeUs;
  private Renderer[] enabledRenderers;
  private final Handler eventHandler;
  private final Handler handler;
  private final HandlerThread internalPlaybackThread;
  private boolean isLoading;
  private final LoadControl loadControl;
  private MediaPeriodHolder loadingPeriodHolder;
  private MediaSource mediaSource;
  private int pendingInitialSeekCount;
  private SeekPosition pendingSeekPosition;
  private final Timeline.Period period;
  private boolean playWhenReady;
  private PlaybackInfo playbackInfo;
  private final ExoPlayer player;
  private MediaPeriodHolder playingPeriodHolder;
  private MediaPeriodHolder readingPeriodHolder;
  private boolean rebuffering;
  private boolean released;
  private final RendererCapabilities[] rendererCapabilities;
  private MediaClock rendererMediaClock;
  private Renderer rendererMediaClockSource;
  private long rendererPositionUs;
  private final Renderer[] renderers;
  private final StandaloneMediaClock standaloneMediaClock;
  private int state;
  private Timeline timeline;
  private final TrackSelector trackSelector;
  private final Timeline.Window window;
  
  public ExoPlayerImplInternal(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl, boolean paramBoolean, Handler paramHandler, PlaybackInfo paramPlaybackInfo, ExoPlayer paramExoPlayer)
  {
    this.renderers = paramArrayOfRenderer;
    this.trackSelector = paramTrackSelector;
    this.loadControl = paramLoadControl;
    this.playWhenReady = paramBoolean;
    this.eventHandler = paramHandler;
    this.state = 1;
    this.playbackInfo = paramPlaybackInfo;
    this.player = paramExoPlayer;
    this.rendererCapabilities = new RendererCapabilities[paramArrayOfRenderer.length];
    int i = 0;
    while (i < paramArrayOfRenderer.length)
    {
      paramArrayOfRenderer[i].setIndex(i);
      this.rendererCapabilities[i] = paramArrayOfRenderer[i].getCapabilities();
      i += 1;
    }
    this.standaloneMediaClock = new StandaloneMediaClock();
    this.enabledRenderers = new Renderer[0];
    this.window = new Timeline.Window();
    this.period = new Timeline.Period();
    paramTrackSelector.init(this);
    this.internalPlaybackThread = new PriorityHandlerThread("ExoPlayerImplInternal:Handler", -16);
    this.internalPlaybackThread.start();
    this.handler = new Handler(this.internalPlaybackThread.getLooper(), this);
  }
  
  private void doSomeWork()
    throws ExoPlaybackException, IOException
  {
    long l1 = SystemClock.elapsedRealtime();
    updatePeriods();
    if (this.playingPeriodHolder == null)
    {
      maybeThrowPeriodPrepareError();
      scheduleNextWork(l1, 10L);
      return;
    }
    TraceUtil.beginSection("doSomeWork");
    updatePlaybackPositions();
    int i = 1;
    boolean bool = true;
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      localRenderer.render(this.rendererPositionUs, this.elapsedRealtimeUs);
      label100:
      int k;
      if ((i != 0) && (localRenderer.isEnded()))
      {
        i = 1;
        if ((!localRenderer.isReady()) && (!localRenderer.isEnded())) {
          break label157;
        }
        k = 1;
        label122:
        if (k == 0) {
          localRenderer.maybeThrowStreamError();
        }
        if ((!bool) || (k == 0)) {
          break label162;
        }
      }
      label157:
      label162:
      for (bool = true;; bool = false)
      {
        j += 1;
        break;
        i = 0;
        break label100;
        k = 0;
        break label122;
      }
    }
    if (!bool) {
      maybeThrowPeriodPrepareError();
    }
    long l2 = this.timeline.getPeriod(this.playingPeriodHolder.index, this.period).getDurationUs();
    if ((i != 0) && ((l2 == -9223372036854775807L) || (l2 <= this.playbackInfo.positionUs)) && (this.playingPeriodHolder.isLast))
    {
      setState(4);
      stopRenderers();
    }
    label360:
    label412:
    while (this.state == 2)
    {
      arrayOfRenderer = this.enabledRenderers;
      j = arrayOfRenderer.length;
      i = 0;
      while (i < j)
      {
        arrayOfRenderer[i].maybeThrowStreamError();
        i += 1;
      }
      if (this.state == 2)
      {
        if (this.enabledRenderers.length > 0) {
          if ((bool) && (haveSufficientBuffer(this.rebuffering))) {
            bool = true;
          }
        }
        for (;;)
        {
          if (!bool) {
            break label360;
          }
          setState(3);
          if (!this.playWhenReady) {
            break;
          }
          startRenderers();
          break;
          bool = false;
          continue;
          bool = isTimelineReady(l2);
        }
      }
      else if (this.state == 3)
      {
        if (this.enabledRenderers.length > 0) {}
        for (;;)
        {
          if (bool) {
            break label412;
          }
          this.rebuffering = this.playWhenReady;
          setState(2);
          stopRenderers();
          break;
          bool = isTimelineReady(l2);
        }
      }
    }
    if (((this.playWhenReady) && (this.state == 3)) || (this.state == 2)) {
      scheduleNextWork(l1, 10L);
    }
    for (;;)
    {
      TraceUtil.endSection();
      return;
      if (this.enabledRenderers.length != 0) {
        scheduleNextWork(l1, 1000L);
      } else {
        this.handler.removeMessages(2);
      }
    }
  }
  
  private void enableRenderers(boolean[] paramArrayOfBoolean, int paramInt)
    throws ExoPlaybackException
  {
    this.enabledRenderers = new Renderer[paramInt];
    int i = 0;
    paramInt = 0;
    while (paramInt < this.renderers.length)
    {
      Renderer localRenderer = this.renderers[paramInt];
      Object localObject = this.playingPeriodHolder.trackSelectorResult.selections.get(paramInt);
      int j = i;
      if (localObject != null)
      {
        this.enabledRenderers[i] = localRenderer;
        if (localRenderer.getState() == 0)
        {
          RendererConfiguration localRendererConfiguration = this.playingPeriodHolder.trackSelectorResult.rendererConfigurations[paramInt];
          if ((this.playWhenReady) && (this.state == 3))
          {
            j = 1;
            if ((paramArrayOfBoolean[paramInt] != 0) || (j == 0)) {
              break label169;
            }
          }
          Format[] arrayOfFormat;
          label169:
          for (boolean bool = true;; bool = false)
          {
            arrayOfFormat = new Format[((TrackSelection)localObject).length()];
            int k = 0;
            while (k < arrayOfFormat.length)
            {
              arrayOfFormat[k] = ((TrackSelection)localObject).getFormat(k);
              k += 1;
            }
            j = 0;
            break;
          }
          localRenderer.enable(localRendererConfiguration, arrayOfFormat, this.playingPeriodHolder.sampleStreams[paramInt], this.rendererPositionUs, bool, this.playingPeriodHolder.getRendererOffset());
          localObject = localRenderer.getMediaClock();
          if (localObject != null)
          {
            if (this.rendererMediaClock != null) {
              throw ExoPlaybackException.createForUnexpected(new IllegalStateException("Multiple renderer media clocks enabled."));
            }
            this.rendererMediaClock = ((MediaClock)localObject);
            this.rendererMediaClockSource = localRenderer;
          }
          if (j != 0) {
            localRenderer.start();
          }
        }
        j = i + 1;
      }
      paramInt += 1;
      i = j;
    }
  }
  
  private void ensureStopped(Renderer paramRenderer)
    throws ExoPlaybackException
  {
    if (paramRenderer.getState() == 2) {
      paramRenderer.stop();
    }
  }
  
  private Pair<Integer, Long> getPeriodPosition(int paramInt, long paramLong)
  {
    return getPeriodPosition(this.timeline, paramInt, paramLong);
  }
  
  private Pair<Integer, Long> getPeriodPosition(Timeline paramTimeline, int paramInt, long paramLong)
  {
    return getPeriodPosition(paramTimeline, paramInt, paramLong, 0L);
  }
  
  private Pair<Integer, Long> getPeriodPosition(Timeline paramTimeline, int paramInt, long paramLong1, long paramLong2)
  {
    Assertions.checkIndex(paramInt, 0, paramTimeline.getWindowCount());
    paramTimeline.getWindow(paramInt, this.window, false, paramLong2);
    paramLong2 = paramLong1;
    if (paramLong1 == -9223372036854775807L)
    {
      paramLong1 = this.window.getDefaultPositionUs();
      paramLong2 = paramLong1;
      if (paramLong1 == -9223372036854775807L) {
        return null;
      }
    }
    paramInt = this.window.firstPeriodIndex;
    paramLong2 = this.window.getPositionInFirstPeriodUs() + paramLong2;
    for (paramLong1 = paramTimeline.getPeriod(paramInt, this.period).getDurationUs(); (paramLong1 != -9223372036854775807L) && (paramLong2 >= paramLong1) && (paramInt < this.window.lastPeriodIndex); paramLong1 = paramTimeline.getPeriod(paramInt, this.period).getDurationUs())
    {
      paramLong2 -= paramLong1;
      paramInt += 1;
    }
    return Pair.create(Integer.valueOf(paramInt), Long.valueOf(paramLong2));
  }
  
  private void handleContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    if ((this.loadingPeriodHolder == null) || (this.loadingPeriodHolder.mediaPeriod != paramMediaPeriod)) {
      return;
    }
    maybeContinueLoading();
  }
  
  private void handlePeriodPrepared(MediaPeriod paramMediaPeriod)
    throws ExoPlaybackException
  {
    if ((this.loadingPeriodHolder == null) || (this.loadingPeriodHolder.mediaPeriod != paramMediaPeriod)) {
      return;
    }
    this.loadingPeriodHolder.handlePrepared();
    if (this.playingPeriodHolder == null)
    {
      this.readingPeriodHolder = this.loadingPeriodHolder;
      resetRendererPosition(this.readingPeriodHolder.startPositionUs);
      setPlayingPeriodHolder(this.readingPeriodHolder);
    }
    maybeContinueLoading();
  }
  
  private void handleSourceInfoRefreshEndedPlayback(Object paramObject, int paramInt)
  {
    this.playbackInfo = new PlaybackInfo(0, 0L);
    notifySourceInfoRefresh(paramObject, paramInt);
    this.playbackInfo = new PlaybackInfo(0, -9223372036854775807L);
    setState(4);
    resetInternal(false);
  }
  
  private void handleSourceInfoRefreshed(Pair<Timeline, Object> paramPair)
    throws ExoPlaybackException
  {
    Object localObject1 = this.timeline;
    this.timeline = ((Timeline)paramPair.first);
    Object localObject2 = paramPair.second;
    int i = 0;
    int j = i;
    if (localObject1 == null)
    {
      if (this.pendingInitialSeekCount > 0)
      {
        paramPair = resolveSeekPosition(this.pendingSeekPosition);
        j = this.pendingInitialSeekCount;
        this.pendingInitialSeekCount = 0;
        this.pendingSeekPosition = null;
        if (paramPair == null)
        {
          handleSourceInfoRefreshEndedPlayback(localObject2, j);
          return;
        }
        this.playbackInfo = new PlaybackInfo(((Integer)paramPair.first).intValue(), ((Long)paramPair.second).longValue());
      }
    }
    else {
      if (this.playingPeriodHolder == null) {
        break label209;
      }
    }
    label209:
    for (paramPair = this.playingPeriodHolder;; paramPair = this.loadingPeriodHolder)
    {
      if (paramPair != null) {
        break label217;
      }
      notifySourceInfoRefresh(localObject2, j);
      return;
      j = i;
      if (this.playbackInfo.startPositionUs != -9223372036854775807L) {
        break;
      }
      if (this.timeline.isEmpty())
      {
        handleSourceInfoRefreshEndedPlayback(localObject2, 0);
        return;
      }
      paramPair = getPeriodPosition(0, -9223372036854775807L);
      this.playbackInfo = new PlaybackInfo(((Integer)paramPair.first).intValue(), ((Long)paramPair.second).longValue());
      j = i;
      break;
    }
    label217:
    int n = this.timeline.getIndexOfPeriod(paramPair.uid);
    int k;
    if (n == -1)
    {
      i = resolveSubsequentPeriod(paramPair.index, (Timeline)localObject1, this.timeline);
      if (i == -1)
      {
        handleSourceInfoRefreshEndedPlayback(localObject2, j);
        return;
      }
      localObject1 = getPeriodPosition(this.timeline.getPeriod(i, this.period).windowIndex, -9223372036854775807L);
      k = ((Integer)((Pair)localObject1).first).intValue();
      long l = ((Long)((Pair)localObject1).second).longValue();
      this.timeline.getPeriod(k, this.period, true);
      localObject1 = this.period.uid;
      paramPair.index = -1;
      if (paramPair.next != null)
      {
        paramPair = paramPair.next;
        if (paramPair.uid.equals(localObject1)) {}
        for (i = k;; i = -1)
        {
          paramPair.index = i;
          break;
        }
      }
      this.playbackInfo = new PlaybackInfo(k, seekToPeriodPosition(k, l));
      notifySourceInfoRefresh(localObject2, j);
      return;
    }
    this.timeline.getPeriod(n, this.period);
    boolean bool;
    if ((n == this.timeline.getPeriodCount() - 1) && (!this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic))
    {
      bool = true;
      paramPair.setIndex(n, bool);
      if (paramPair != this.readingPeriodHolder) {
        break label659;
      }
      i = 1;
      label485:
      localObject1 = paramPair;
      int m = n;
      k = i;
      if (n != this.playbackInfo.periodIndex)
      {
        this.playbackInfo = this.playbackInfo.copyWithPeriodIndex(n);
        k = i;
        m = n;
        localObject1 = paramPair;
      }
      label530:
      if (((MediaPeriodHolder)localObject1).next == null) {
        break label712;
      }
      paramPair = ((MediaPeriodHolder)localObject1).next;
      m += 1;
      this.timeline.getPeriod(m, this.period, true);
      if ((m != this.timeline.getPeriodCount() - 1) || (this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic)) {
        break label664;
      }
      bool = true;
      label606:
      if (!paramPair.uid.equals(this.period.uid)) {
        break label675;
      }
      paramPair.setIndex(m, bool);
      if (paramPair != this.readingPeriodHolder) {
        break label670;
      }
    }
    label659:
    label664:
    label670:
    for (i = 1;; i = 0)
    {
      k |= i;
      localObject1 = paramPair;
      break label530;
      bool = false;
      break;
      i = 0;
      break label485;
      bool = false;
      break label606;
    }
    label675:
    if (k == 0)
    {
      i = this.playingPeriodHolder.index;
      this.playbackInfo = new PlaybackInfo(i, seekToPeriodPosition(i, this.playbackInfo.positionUs));
    }
    for (;;)
    {
      label712:
      notifySourceInfoRefresh(localObject2, j);
      return;
      this.loadingPeriodHolder = ((MediaPeriodHolder)localObject1);
      this.loadingPeriodHolder.next = null;
      releasePeriodHoldersFrom(paramPair);
    }
  }
  
  private boolean haveSufficientBuffer(boolean paramBoolean)
  {
    if (!this.loadingPeriodHolder.prepared) {}
    for (long l1 = this.loadingPeriodHolder.startPositionUs;; l1 = this.loadingPeriodHolder.mediaPeriod.getBufferedPositionUs())
    {
      l2 = l1;
      if (l1 != Long.MIN_VALUE) {
        break label80;
      }
      if (!this.loadingPeriodHolder.isLast) {
        break;
      }
      return true;
    }
    long l2 = this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
    label80:
    return this.loadControl.shouldStartPlayback(l2 - this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs), paramBoolean);
  }
  
  private boolean isTimelineReady(long paramLong)
  {
    return (paramLong == -9223372036854775807L) || (this.playbackInfo.positionUs < paramLong) || ((this.playingPeriodHolder.next != null) && (this.playingPeriodHolder.next.prepared));
  }
  
  private void maybeContinueLoading()
  {
    if (!this.loadingPeriodHolder.prepared) {}
    for (long l1 = 0L; l1 == Long.MIN_VALUE; l1 = this.loadingPeriodHolder.mediaPeriod.getNextLoadPositionUs())
    {
      setIsLoading(false);
      return;
    }
    long l2 = this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs);
    boolean bool = this.loadControl.shouldContinueLoading(l1 - l2);
    setIsLoading(bool);
    if (bool)
    {
      this.loadingPeriodHolder.needsContinueLoading = false;
      this.loadingPeriodHolder.mediaPeriod.continueLoading(l2);
      return;
    }
    this.loadingPeriodHolder.needsContinueLoading = true;
  }
  
  private void maybeThrowPeriodPrepareError()
    throws IOException
  {
    Renderer[] arrayOfRenderer;
    int j;
    int i;
    if ((this.loadingPeriodHolder != null) && (!this.loadingPeriodHolder.prepared) && ((this.readingPeriodHolder == null) || (this.readingPeriodHolder.next == this.loadingPeriodHolder)))
    {
      arrayOfRenderer = this.enabledRenderers;
      j = arrayOfRenderer.length;
      i = 0;
    }
    while (i < j)
    {
      if (!arrayOfRenderer[i].hasReadStreamToEnd()) {
        return;
      }
      i += 1;
    }
    this.loadingPeriodHolder.mediaPeriod.maybeThrowPrepareError();
  }
  
  private void maybeUpdateLoadingPeriod()
    throws IOException
  {
    if (this.loadingPeriodHolder == null) {}
    for (int i = this.playbackInfo.periodIndex; i >= this.timeline.getPeriodCount(); i = this.loadingPeriodHolder.index + 1)
    {
      this.mediaSource.maybeThrowSourceInfoRefreshError();
      do
      {
        return;
        i = this.loadingPeriodHolder.index;
      } while ((this.loadingPeriodHolder.isLast) || (!this.loadingPeriodHolder.isFullyBuffered()) || (this.timeline.getPeriod(i, this.period).getDurationUs() == -9223372036854775807L) || ((this.playingPeriodHolder != null) && (i - this.playingPeriodHolder.index == 100)));
    }
    long l1;
    label135:
    long l2;
    if (this.loadingPeriodHolder == null)
    {
      l1 = this.playbackInfo.positionUs;
      if (this.loadingPeriodHolder != null) {
        break label420;
      }
      l2 = l1 + 60000000L;
      label149:
      this.timeline.getPeriod(i, this.period, true);
      if ((i != this.timeline.getPeriodCount() - 1) || (this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic)) {
        break label454;
      }
    }
    label420:
    label454:
    for (boolean bool = true;; bool = false)
    {
      Object localObject = new MediaPeriodHolder(this.renderers, this.rendererCapabilities, l2, this.trackSelector, this.loadControl, this.mediaSource, this.period.uid, i, bool, l1);
      if (this.loadingPeriodHolder != null) {
        this.loadingPeriodHolder.next = ((MediaPeriodHolder)localObject);
      }
      this.loadingPeriodHolder = ((MediaPeriodHolder)localObject);
      this.loadingPeriodHolder.mediaPeriod.prepare(this);
      setIsLoading(true);
      return;
      int j = this.timeline.getPeriod(i, this.period).windowIndex;
      if (i != this.timeline.getWindow(j, this.window).firstPeriodIndex)
      {
        l1 = 0L;
        break label135;
      }
      l1 = this.loadingPeriodHolder.getRendererOffset();
      l2 = this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
      long l3 = this.rendererPositionUs;
      localObject = getPeriodPosition(this.timeline, j, -9223372036854775807L, Math.max(0L, l1 + l2 - l3));
      if (localObject == null) {
        break;
      }
      i = ((Integer)((Pair)localObject).first).intValue();
      l1 = ((Long)((Pair)localObject).second).longValue();
      break label135;
      l2 = this.loadingPeriodHolder.getRendererOffset() + this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
      break label149;
    }
  }
  
  private void notifySourceInfoRefresh(Object paramObject, int paramInt)
  {
    this.eventHandler.obtainMessage(6, new SourceInfo(this.timeline, paramObject, this.playbackInfo, paramInt)).sendToTarget();
  }
  
  private void prepareInternal(MediaSource paramMediaSource, boolean paramBoolean)
  {
    resetInternal(true);
    this.loadControl.onPrepared();
    if (paramBoolean) {
      this.playbackInfo = new PlaybackInfo(0, -9223372036854775807L);
    }
    this.mediaSource = paramMediaSource;
    paramMediaSource.prepareSource(this.player, true, this);
    setState(2);
    this.handler.sendEmptyMessage(2);
  }
  
  private void releaseInternal()
  {
    resetInternal(true);
    this.loadControl.onReleased();
    setState(1);
    try
    {
      this.released = true;
      notifyAll();
      return;
    }
    finally {}
  }
  
  private void releasePeriodHoldersFrom(MediaPeriodHolder paramMediaPeriodHolder)
  {
    while (paramMediaPeriodHolder != null)
    {
      paramMediaPeriodHolder.release();
      paramMediaPeriodHolder = paramMediaPeriodHolder.next;
    }
  }
  
  private void reselectTracksInternal()
    throws ExoPlaybackException
  {
    if (this.playingPeriodHolder == null) {
      return;
    }
    MediaPeriodHolder localMediaPeriodHolder = this.playingPeriodHolder;
    int i = 1;
    label16:
    boolean bool;
    label55:
    boolean[] arrayOfBoolean1;
    long l;
    int j;
    boolean[] arrayOfBoolean2;
    label160:
    Renderer localRenderer;
    label190:
    int k;
    if ((localMediaPeriodHolder != null) && (localMediaPeriodHolder.prepared)) {
      if (localMediaPeriodHolder.selectTracks())
      {
        if (i == 0) {
          break label394;
        }
        if (this.readingPeriodHolder == this.playingPeriodHolder) {
          break label320;
        }
        bool = true;
        releasePeriodHoldersFrom(this.playingPeriodHolder.next);
        this.playingPeriodHolder.next = null;
        this.loadingPeriodHolder = this.playingPeriodHolder;
        this.readingPeriodHolder = this.playingPeriodHolder;
        arrayOfBoolean1 = new boolean[this.renderers.length];
        l = this.playingPeriodHolder.updatePeriodTrackSelection(this.playbackInfo.positionUs, bool, arrayOfBoolean1);
        if (l != this.playbackInfo.positionUs)
        {
          this.playbackInfo.positionUs = l;
          resetRendererPosition(l);
        }
        j = 0;
        arrayOfBoolean2 = new boolean[this.renderers.length];
        i = 0;
        if (i >= this.renderers.length) {
          break label353;
        }
        localRenderer = this.renderers[i];
        if (localRenderer.getState() == 0) {
          break label326;
        }
        bool = true;
        arrayOfBoolean2[i] = bool;
        SampleStream localSampleStream = this.playingPeriodHolder.sampleStreams[i];
        k = j;
        if (localSampleStream != null) {
          k = j + 1;
        }
        if (arrayOfBoolean2[i] != 0)
        {
          if (localSampleStream == localRenderer.getStream()) {
            break label332;
          }
          if (localRenderer == this.rendererMediaClockSource)
          {
            if (localSampleStream == null) {
              this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
            }
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
          }
          ensureStopped(localRenderer);
          localRenderer.disable();
        }
      }
    }
    for (;;)
    {
      i += 1;
      j = k;
      break label160;
      if (localMediaPeriodHolder == this.readingPeriodHolder) {
        i = 0;
      }
      localMediaPeriodHolder = localMediaPeriodHolder.next;
      break label16;
      break;
      label320:
      bool = false;
      break label55;
      label326:
      bool = false;
      break label190;
      label332:
      if (arrayOfBoolean1[i] != 0) {
        localRenderer.resetPosition(this.rendererPositionUs);
      }
    }
    label353:
    this.eventHandler.obtainMessage(3, localMediaPeriodHolder.trackSelectorResult).sendToTarget();
    enableRenderers(arrayOfBoolean2, j);
    for (;;)
    {
      maybeContinueLoading();
      updatePlaybackPositions();
      this.handler.sendEmptyMessage(2);
      return;
      label394:
      this.loadingPeriodHolder = localMediaPeriodHolder;
      for (localMediaPeriodHolder = this.loadingPeriodHolder.next; localMediaPeriodHolder != null; localMediaPeriodHolder = localMediaPeriodHolder.next) {
        localMediaPeriodHolder.release();
      }
      this.loadingPeriodHolder.next = null;
      if (this.loadingPeriodHolder.prepared)
      {
        l = Math.max(this.loadingPeriodHolder.startPositionUs, this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs));
        this.loadingPeriodHolder.updatePeriodTrackSelection(l, false);
      }
    }
  }
  
  private void resetInternal(boolean paramBoolean)
  {
    this.handler.removeMessages(2);
    this.rebuffering = false;
    this.standaloneMediaClock.stop();
    this.rendererMediaClock = null;
    this.rendererMediaClockSource = null;
    this.rendererPositionUs = 60000000L;
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    for (;;)
    {
      Renderer localRenderer;
      if (i < j) {
        localRenderer = arrayOfRenderer[i];
      }
      try
      {
        ensureStopped(localRenderer);
        localRenderer.disable();
        i += 1;
      }
      catch (RuntimeException localRuntimeException)
      {
        for (;;)
        {
          Log.e("ExoPlayerImplInternal", "Stop failed.", localRuntimeException);
        }
        this.enabledRenderers = new Renderer[0];
        if (this.playingPeriodHolder != null) {}
        for (MediaPeriodHolder localMediaPeriodHolder = this.playingPeriodHolder;; localMediaPeriodHolder = this.loadingPeriodHolder)
        {
          releasePeriodHoldersFrom(localMediaPeriodHolder);
          this.loadingPeriodHolder = null;
          this.readingPeriodHolder = null;
          this.playingPeriodHolder = null;
          setIsLoading(false);
          if (paramBoolean)
          {
            if (this.mediaSource != null)
            {
              this.mediaSource.releaseSource();
              this.mediaSource = null;
            }
            this.timeline = null;
          }
          return;
        }
      }
      catch (ExoPlaybackException localExoPlaybackException)
      {
        for (;;) {}
      }
    }
  }
  
  private void resetRendererPosition(long paramLong)
    throws ExoPlaybackException
  {
    if (this.playingPeriodHolder == null) {}
    for (paramLong = 60000000L + paramLong;; paramLong = this.playingPeriodHolder.toRendererTime(paramLong))
    {
      this.rendererPositionUs = paramLong;
      this.standaloneMediaClock.setPositionUs(this.rendererPositionUs);
      Renderer[] arrayOfRenderer = this.enabledRenderers;
      int j = arrayOfRenderer.length;
      int i = 0;
      while (i < j)
      {
        arrayOfRenderer[i].resetPosition(this.rendererPositionUs);
        i += 1;
      }
    }
  }
  
  private Pair<Integer, Long> resolveSeekPosition(SeekPosition paramSeekPosition)
  {
    Object localObject2 = paramSeekPosition.timeline;
    Object localObject1 = localObject2;
    if (((Timeline)localObject2).isEmpty()) {
      localObject1 = this.timeline;
    }
    try
    {
      localObject2 = getPeriodPosition((Timeline)localObject1, paramSeekPosition.windowIndex, paramSeekPosition.windowPositionUs);
      if (this.timeline == localObject1) {
        return (Pair<Integer, Long>)localObject2;
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new IllegalSeekPositionException(this.timeline, paramSeekPosition.windowIndex, paramSeekPosition.windowPositionUs);
    }
    int i = this.timeline.getIndexOfPeriod(localIndexOutOfBoundsException.getPeriod(((Integer)((Pair)localObject2).first).intValue(), this.period, true).uid);
    if (i != -1) {
      return Pair.create(Integer.valueOf(i), ((Pair)localObject2).second);
    }
    i = resolveSubsequentPeriod(((Integer)((Pair)localObject2).first).intValue(), localIndexOutOfBoundsException, this.timeline);
    if (i != -1) {
      return getPeriodPosition(this.timeline.getPeriod(i, this.period).windowIndex, -9223372036854775807L);
    }
    return null;
  }
  
  private int resolveSubsequentPeriod(int paramInt, Timeline paramTimeline1, Timeline paramTimeline2)
  {
    int j = -1;
    int i = paramInt;
    for (paramInt = j; (paramInt == -1) && (i < paramTimeline1.getPeriodCount() - 1); paramInt = paramTimeline2.getIndexOfPeriod(paramTimeline1.getPeriod(i, this.period, true).uid)) {
      i += 1;
    }
    return paramInt;
  }
  
  private void scheduleNextWork(long paramLong1, long paramLong2)
  {
    this.handler.removeMessages(2);
    paramLong1 = paramLong1 + paramLong2 - SystemClock.elapsedRealtime();
    if (paramLong1 <= 0L)
    {
      this.handler.sendEmptyMessage(2);
      return;
    }
    this.handler.sendEmptyMessageDelayed(2, paramLong1);
  }
  
  private void seekToInternal(SeekPosition paramSeekPosition)
    throws ExoPlaybackException
  {
    if (this.timeline == null)
    {
      this.pendingInitialSeekCount += 1;
      this.pendingSeekPosition = paramSeekPosition;
      return;
    }
    Object localObject = resolveSeekPosition(paramSeekPosition);
    if (localObject == null)
    {
      this.playbackInfo = new PlaybackInfo(0, 0L);
      this.eventHandler.obtainMessage(4, 1, 0, this.playbackInfo).sendToTarget();
      this.playbackInfo = new PlaybackInfo(0, -9223372036854775807L);
      setState(4);
      resetInternal(false);
      return;
    }
    int i;
    if (paramSeekPosition.windowPositionUs == -9223372036854775807L) {
      i = 1;
    }
    for (;;)
    {
      int k = ((Integer)((Pair)localObject).first).intValue();
      long l1 = ((Long)((Pair)localObject).second).longValue();
      try
      {
        if (k == this.playbackInfo.periodIndex)
        {
          l2 = l1 / 1000L;
          long l3 = this.playbackInfo.positionUs / 1000L;
          if (l2 == l3)
          {
            this.playbackInfo = new PlaybackInfo(k, l1);
            paramSeekPosition = this.eventHandler;
            if (i != 0) {}
            for (i = 1;; i = 0)
            {
              paramSeekPosition.obtainMessage(4, i, 0, this.playbackInfo).sendToTarget();
              return;
              i = 0;
              break;
            }
          }
        }
        long l2 = seekToPeriodPosition(k, l1);
        int j;
        if (l1 != l2)
        {
          j = 1;
          this.playbackInfo = new PlaybackInfo(k, l2);
          paramSeekPosition = this.eventHandler;
          if ((i | j) == 0) {
            break label290;
          }
        }
        label290:
        for (i = 1;; i = 0)
        {
          paramSeekPosition.obtainMessage(4, i, 0, this.playbackInfo).sendToTarget();
          return;
          j = 0;
          break;
        }
        i = 1;
      }
      finally
      {
        this.playbackInfo = new PlaybackInfo(k, l1);
        localObject = this.eventHandler;
        if (i == 0) {}
      }
    }
    for (;;)
    {
      ((Handler)localObject).obtainMessage(4, i, 0, this.playbackInfo).sendToTarget();
      throw paramSeekPosition;
      i = 0;
    }
  }
  
  private long seekToPeriodPosition(int paramInt, long paramLong)
    throws ExoPlaybackException
  {
    stopRenderers();
    this.rebuffering = false;
    setState(2);
    Object localObject2 = null;
    Object localObject1 = null;
    Object localObject3;
    if (this.playingPeriodHolder == null)
    {
      localObject3 = localObject1;
      if (this.loadingPeriodHolder != null)
      {
        this.loadingPeriodHolder.release();
        localObject3 = localObject1;
      }
    }
    while ((this.playingPeriodHolder != localObject3) || (this.playingPeriodHolder != this.readingPeriodHolder))
    {
      localObject1 = this.enabledRenderers;
      int i = localObject1.length;
      paramInt = 0;
      for (;;)
      {
        if (paramInt < i)
        {
          localObject1[paramInt].disable();
          paramInt += 1;
          continue;
          localObject1 = this.playingPeriodHolder;
          localObject3 = localObject2;
          if (localObject1 == null) {
            break;
          }
          if ((((MediaPeriodHolder)localObject1).index == paramInt) && (((MediaPeriodHolder)localObject1).prepared)) {
            localObject2 = localObject1;
          }
          for (;;)
          {
            localObject1 = ((MediaPeriodHolder)localObject1).next;
            break;
            ((MediaPeriodHolder)localObject1).release();
          }
        }
      }
      this.enabledRenderers = new Renderer[0];
      this.rendererMediaClock = null;
      this.rendererMediaClockSource = null;
      this.playingPeriodHolder = null;
    }
    if (localObject3 != null)
    {
      ((MediaPeriodHolder)localObject3).next = null;
      this.loadingPeriodHolder = ((MediaPeriodHolder)localObject3);
      this.readingPeriodHolder = ((MediaPeriodHolder)localObject3);
      setPlayingPeriodHolder((MediaPeriodHolder)localObject3);
      long l = paramLong;
      if (this.playingPeriodHolder.hasEnabledTracks) {
        l = this.playingPeriodHolder.mediaPeriod.seekToUs(paramLong);
      }
      resetRendererPosition(l);
      maybeContinueLoading();
      paramLong = l;
    }
    for (;;)
    {
      this.handler.sendEmptyMessage(2);
      return paramLong;
      this.loadingPeriodHolder = null;
      this.readingPeriodHolder = null;
      this.playingPeriodHolder = null;
      resetRendererPosition(paramLong);
    }
  }
  
  /* Error */
  private void sendMessagesInternal(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
    throws ExoPlaybackException
  {
    // Byte code:
    //   0: aload_1
    //   1: arraylength
    //   2: istore_3
    //   3: iconst_0
    //   4: istore_2
    //   5: iload_2
    //   6: iload_3
    //   7: if_icmpge +35 -> 42
    //   10: aload_1
    //   11: iload_2
    //   12: aaload
    //   13: astore 4
    //   15: aload 4
    //   17: getfield 741	com/google/android/exoplayer2/ExoPlayer$ExoPlayerMessage:target	Lcom/google/android/exoplayer2/ExoPlayer$ExoPlayerComponent;
    //   20: aload 4
    //   22: getfield 744	com/google/android/exoplayer2/ExoPlayer$ExoPlayerMessage:messageType	I
    //   25: aload 4
    //   27: getfield 747	com/google/android/exoplayer2/ExoPlayer$ExoPlayerMessage:message	Ljava/lang/Object;
    //   30: invokeinterface 753 3 0
    //   35: iload_2
    //   36: iconst_1
    //   37: iadd
    //   38: istore_2
    //   39: goto -34 -> 5
    //   42: aload_0
    //   43: getfield 595	com/google/android/exoplayer2/ExoPlayerImplInternal:mediaSource	Lcom/google/android/exoplayer2/source/MediaSource;
    //   46: ifnull +12 -> 58
    //   49: aload_0
    //   50: getfield 199	com/google/android/exoplayer2/ExoPlayerImplInternal:handler	Landroid/os/Handler;
    //   53: iconst_2
    //   54: invokevirtual 643	android/os/Handler:sendEmptyMessage	(I)Z
    //   57: pop
    //   58: aload_0
    //   59: monitorenter
    //   60: aload_0
    //   61: aload_0
    //   62: getfield 755	com/google/android/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   65: iconst_1
    //   66: iadd
    //   67: putfield 755	com/google/android/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   70: aload_0
    //   71: invokevirtual 652	java/lang/Object:notifyAll	()V
    //   74: aload_0
    //   75: monitorexit
    //   76: return
    //   77: astore_1
    //   78: aload_0
    //   79: monitorexit
    //   80: aload_1
    //   81: athrow
    //   82: astore_1
    //   83: aload_0
    //   84: monitorenter
    //   85: aload_0
    //   86: aload_0
    //   87: getfield 755	com/google/android/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   90: iconst_1
    //   91: iadd
    //   92: putfield 755	com/google/android/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   95: aload_0
    //   96: invokevirtual 652	java/lang/Object:notifyAll	()V
    //   99: aload_0
    //   100: monitorexit
    //   101: aload_1
    //   102: athrow
    //   103: astore_1
    //   104: aload_0
    //   105: monitorexit
    //   106: aload_1
    //   107: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	this	ExoPlayerImplInternal
    //   0	108	1	paramArrayOfExoPlayerMessage	ExoPlayer.ExoPlayerMessage[]
    //   4	35	2	i	int
    //   2	6	3	j	int
    //   13	13	4	localExoPlayerMessage	ExoPlayer.ExoPlayerMessage
    // Exception table:
    //   from	to	target	type
    //   60	76	77	finally
    //   78	80	77	finally
    //   0	3	82	finally
    //   15	35	82	finally
    //   42	58	82	finally
    //   85	101	103	finally
    //   104	106	103	finally
  }
  
  private void setIsLoading(boolean paramBoolean)
  {
    Handler localHandler;
    if (this.isLoading != paramBoolean)
    {
      this.isLoading = paramBoolean;
      localHandler = this.eventHandler;
      if (!paramBoolean) {
        break label35;
      }
    }
    label35:
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(2, i, 0).sendToTarget();
      return;
    }
  }
  
  private void setPlayWhenReadyInternal(boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.rebuffering = false;
    this.playWhenReady = paramBoolean;
    if (!paramBoolean)
    {
      stopRenderers();
      updatePlaybackPositions();
    }
    do
    {
      return;
      if (this.state == 3)
      {
        startRenderers();
        this.handler.sendEmptyMessage(2);
        return;
      }
    } while (this.state != 2);
    this.handler.sendEmptyMessage(2);
  }
  
  private void setPlayingPeriodHolder(MediaPeriodHolder paramMediaPeriodHolder)
    throws ExoPlaybackException
  {
    if (this.playingPeriodHolder == paramMediaPeriodHolder) {
      return;
    }
    int j = 0;
    boolean[] arrayOfBoolean = new boolean[this.renderers.length];
    int i = 0;
    if (i < this.renderers.length)
    {
      Renderer localRenderer = this.renderers[i];
      if (localRenderer.getState() != 0) {}
      for (int m = 1;; m = 0)
      {
        arrayOfBoolean[i] = m;
        TrackSelection localTrackSelection = paramMediaPeriodHolder.trackSelectorResult.selections.get(i);
        int k = j;
        if (localTrackSelection != null) {
          k = j + 1;
        }
        if ((arrayOfBoolean[i] != 0) && ((localTrackSelection == null) || ((localRenderer.isCurrentStreamFinal()) && (localRenderer.getStream() == this.playingPeriodHolder.sampleStreams[i]))))
        {
          if (localRenderer == this.rendererMediaClockSource)
          {
            this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
          }
          ensureStopped(localRenderer);
          localRenderer.disable();
        }
        i += 1;
        j = k;
        break;
      }
    }
    this.playingPeriodHolder = paramMediaPeriodHolder;
    this.eventHandler.obtainMessage(3, paramMediaPeriodHolder.trackSelectorResult).sendToTarget();
    enableRenderers(arrayOfBoolean, j);
  }
  
  private void setState(int paramInt)
  {
    if (this.state != paramInt)
    {
      this.state = paramInt;
      this.eventHandler.obtainMessage(1, paramInt, 0).sendToTarget();
    }
  }
  
  private void startRenderers()
    throws ExoPlaybackException
  {
    int i = 0;
    this.rebuffering = false;
    this.standaloneMediaClock.start();
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    while (i < j)
    {
      arrayOfRenderer[i].start();
      i += 1;
    }
  }
  
  private void stopInternal()
  {
    resetInternal(true);
    this.loadControl.onStopped();
    setState(1);
  }
  
  private void stopRenderers()
    throws ExoPlaybackException
  {
    this.standaloneMediaClock.stop();
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      ensureStopped(arrayOfRenderer[i]);
      i += 1;
    }
  }
  
  private void updatePeriods()
    throws ExoPlaybackException, IOException
  {
    if (this.timeline == null) {
      this.mediaSource.maybeThrowSourceInfoRefreshError();
    }
    int i;
    label313:
    do
    {
      for (;;)
      {
        return;
        maybeUpdateLoadingPeriod();
        if ((this.loadingPeriodHolder == null) || (this.loadingPeriodHolder.isFullyBuffered())) {
          setIsLoading(false);
        }
        while (this.playingPeriodHolder != null)
        {
          while ((this.playingPeriodHolder != this.readingPeriodHolder) && (this.rendererPositionUs >= this.playingPeriodHolder.next.rendererPositionOffsetUs))
          {
            this.playingPeriodHolder.release();
            setPlayingPeriodHolder(this.playingPeriodHolder.next);
            this.playbackInfo = new PlaybackInfo(this.playingPeriodHolder.index, this.playingPeriodHolder.startPositionUs);
            updatePlaybackPositions();
            this.eventHandler.obtainMessage(5, this.playbackInfo).sendToTarget();
          }
          if ((this.loadingPeriodHolder != null) && (this.loadingPeriodHolder.needsContinueLoading)) {
            maybeContinueLoading();
          }
        }
        continue;
        if (!this.readingPeriodHolder.isLast) {
          break;
        }
        i = 0;
        while (i < this.renderers.length)
        {
          localObject1 = this.renderers[i];
          localObject2 = this.readingPeriodHolder.sampleStreams[i];
          if ((localObject2 != null) && (((Renderer)localObject1).getStream() == localObject2) && (((Renderer)localObject1).hasReadStreamToEnd())) {
            ((Renderer)localObject1).setCurrentStreamFinal();
          }
          i += 1;
        }
      }
      i = 0;
      for (;;)
      {
        if (i >= this.renderers.length) {
          break label313;
        }
        localObject1 = this.renderers[i];
        localObject2 = this.readingPeriodHolder.sampleStreams[i];
        if ((((Renderer)localObject1).getStream() != localObject2) || ((localObject2 != null) && (!((Renderer)localObject1).hasReadStreamToEnd()))) {
          break;
        }
        i += 1;
      }
    } while ((this.readingPeriodHolder.next == null) || (!this.readingPeriodHolder.next.prepared));
    Object localObject1 = this.readingPeriodHolder.trackSelectorResult;
    this.readingPeriodHolder = this.readingPeriodHolder.next;
    Object localObject2 = this.readingPeriodHolder.trackSelectorResult;
    label386:
    int j;
    label388:
    Renderer localRenderer;
    if (this.readingPeriodHolder.mediaPeriod.readDiscontinuity() != -9223372036854775807L)
    {
      i = 1;
      j = 0;
      if (j < this.renderers.length)
      {
        localRenderer = this.renderers[j];
        if (((TrackSelectorResult)localObject1).selections.get(j) != null) {
          break label429;
        }
      }
    }
    for (;;)
    {
      j += 1;
      break label388;
      break;
      i = 0;
      break label386;
      label429:
      if (i != 0)
      {
        localRenderer.setCurrentStreamFinal();
      }
      else if (!localRenderer.isCurrentStreamFinal())
      {
        TrackSelection localTrackSelection = ((TrackSelectorResult)localObject2).selections.get(j);
        Object localObject3 = localObject1.rendererConfigurations[j];
        RendererConfiguration localRendererConfiguration = localObject2.rendererConfigurations[j];
        if ((localTrackSelection != null) && (localRendererConfiguration.equals(localObject3)))
        {
          localObject3 = new Format[localTrackSelection.length()];
          int k = 0;
          while (k < localObject3.length)
          {
            localObject3[k] = localTrackSelection.getFormat(k);
            k += 1;
          }
          localRenderer.replaceStream((Format[])localObject3, this.readingPeriodHolder.sampleStreams[j], this.readingPeriodHolder.getRendererOffset());
        }
        else
        {
          localRenderer.setCurrentStreamFinal();
        }
      }
    }
  }
  
  private void updatePlaybackPositions()
    throws ExoPlaybackException
  {
    if (this.playingPeriodHolder == null) {
      return;
    }
    long l1 = this.playingPeriodHolder.mediaPeriod.readDiscontinuity();
    if (l1 != -9223372036854775807L)
    {
      resetRendererPosition(l1);
      this.playbackInfo.positionUs = l1;
      this.elapsedRealtimeUs = (SystemClock.elapsedRealtime() * 1000L);
      if (this.enabledRenderers.length != 0) {
        break label182;
      }
    }
    label182:
    for (l1 = Long.MIN_VALUE;; l1 = this.playingPeriodHolder.mediaPeriod.getBufferedPositionUs())
    {
      PlaybackInfo localPlaybackInfo = this.playbackInfo;
      long l2 = l1;
      if (l1 == Long.MIN_VALUE) {
        l2 = this.timeline.getPeriod(this.playingPeriodHolder.index, this.period).getDurationUs();
      }
      localPlaybackInfo.bufferedPositionUs = l2;
      return;
      if ((this.rendererMediaClockSource != null) && (!this.rendererMediaClockSource.isEnded()))
      {
        this.rendererPositionUs = this.rendererMediaClock.getPositionUs();
        this.standaloneMediaClock.setPositionUs(this.rendererPositionUs);
      }
      for (;;)
      {
        l1 = this.playingPeriodHolder.toPeriodTime(this.rendererPositionUs);
        break;
        this.rendererPositionUs = this.standaloneMediaClock.getPositionUs();
      }
    }
  }
  
  /* Error */
  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 649	com/google/android/exoplayer2/ExoPlayerImplInternal:released	Z
    //   6: ifeq +15 -> 21
    //   9: ldc 66
    //   11: ldc_w 796
    //   14: invokestatic 800	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   17: pop
    //   18: aload_0
    //   19: monitorexit
    //   20: return
    //   21: aload_0
    //   22: getfield 802	com/google/android/exoplayer2/ExoPlayerImplInternal:customMessagesSent	I
    //   25: istore_2
    //   26: aload_0
    //   27: iload_2
    //   28: iconst_1
    //   29: iadd
    //   30: putfield 802	com/google/android/exoplayer2/ExoPlayerImplInternal:customMessagesSent	I
    //   33: aload_0
    //   34: getfield 199	com/google/android/exoplayer2/ExoPlayerImplInternal:handler	Landroid/os/Handler;
    //   37: bipush 10
    //   39: aload_1
    //   40: invokevirtual 625	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
    //   43: invokevirtual 630	android/os/Message:sendToTarget	()V
    //   46: aload_0
    //   47: getfield 755	com/google/android/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   50: istore_3
    //   51: iload_3
    //   52: iload_2
    //   53: if_icmpgt -35 -> 18
    //   56: aload_0
    //   57: invokevirtual 805	java/lang/Object:wait	()V
    //   60: goto -14 -> 46
    //   63: astore_1
    //   64: invokestatic 811	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   67: invokevirtual 814	java/lang/Thread:interrupt	()V
    //   70: goto -24 -> 46
    //   73: astore_1
    //   74: aload_0
    //   75: monitorexit
    //   76: aload_1
    //   77: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	78	0	this	ExoPlayerImplInternal
    //   0	78	1	paramVarArgs	ExoPlayer.ExoPlayerMessage[]
    //   25	29	2	i	int
    //   50	4	3	j	int
    // Exception table:
    //   from	to	target	type
    //   56	60	63	java/lang/InterruptedException
    //   2	18	73	finally
    //   21	46	73	finally
    //   46	51	73	finally
    //   56	60	73	finally
    //   64	70	73	finally
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    try
    {
      switch (paramMessage.what)
      {
      case 0: 
        MediaSource localMediaSource = (MediaSource)paramMessage.obj;
        if (paramMessage.arg1 != 0) {
          bool1 = true;
        }
        prepareInternal(localMediaSource, bool1);
        return true;
      }
    }
    catch (ExoPlaybackException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Renderer error.", paramMessage);
      this.eventHandler.obtainMessage(7, paramMessage).sendToTarget();
      stopInternal();
      return true;
    }
    catch (IOException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Source error.", paramMessage);
      this.eventHandler.obtainMessage(7, ExoPlaybackException.createForSource(paramMessage)).sendToTarget();
      stopInternal();
      return true;
    }
    catch (RuntimeException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Internal runtime error.", paramMessage);
      this.eventHandler.obtainMessage(7, ExoPlaybackException.createForUnexpected(paramMessage)).sendToTarget();
      stopInternal();
      return true;
    }
    bool1 = bool2;
    if (paramMessage.arg1 != 0) {
      bool1 = true;
    }
    setPlayWhenReadyInternal(bool1);
    return true;
    doSomeWork();
    return true;
    seekToInternal((SeekPosition)paramMessage.obj);
    return true;
    stopInternal();
    return true;
    releaseInternal();
    return true;
    handlePeriodPrepared((MediaPeriod)paramMessage.obj);
    return true;
    handleSourceInfoRefreshed((Pair)paramMessage.obj);
    return true;
    handleContinueLoadingRequested((MediaPeriod)paramMessage.obj);
    return true;
    reselectTracksInternal();
    return true;
    sendMessagesInternal((ExoPlayer.ExoPlayerMessage[])paramMessage.obj);
    return true;
    return false;
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    this.handler.obtainMessage(8, paramMediaPeriod).sendToTarget();
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    this.handler.obtainMessage(7, paramMediaPeriod).sendToTarget();
  }
  
  public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
  {
    this.handler.obtainMessage(6, Pair.create(paramTimeline, paramObject)).sendToTarget();
  }
  
  public void onTrackSelectionsInvalidated()
  {
    this.handler.sendEmptyMessage(9);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean)
  {
    Handler localHandler = this.handler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(0, i, 0, paramMediaSource).sendToTarget();
      return;
    }
  }
  
  public void release()
  {
    for (;;)
    {
      try
      {
        boolean bool = this.released;
        if (bool) {
          return;
        }
        this.handler.sendEmptyMessage(5);
        bool = this.released;
        if (!bool) {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        } else {
          this.internalPlaybackThread.quit();
        }
      }
      finally {}
    }
  }
  
  public void seekTo(Timeline paramTimeline, int paramInt, long paramLong)
  {
    this.handler.obtainMessage(3, new SeekPosition(paramTimeline, paramInt, paramLong)).sendToTarget();
  }
  
  public void sendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    if (this.released)
    {
      Log.w("ExoPlayerImplInternal", "Ignoring messages sent after release.");
      return;
    }
    this.customMessagesSent += 1;
    this.handler.obtainMessage(10, paramVarArgs).sendToTarget();
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    Handler localHandler = this.handler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(1, i, 0).sendToTarget();
      return;
    }
  }
  
  public void stop()
  {
    this.handler.sendEmptyMessage(4);
  }
  
  private static final class MediaPeriodHolder
  {
    public boolean hasEnabledTracks;
    public int index;
    public boolean isLast;
    private final LoadControl loadControl;
    public final boolean[] mayRetainStreamFlags;
    public final MediaPeriod mediaPeriod;
    private final MediaSource mediaSource;
    public boolean needsContinueLoading;
    public MediaPeriodHolder next;
    private TrackSelectorResult periodTrackSelectorResult;
    public boolean prepared;
    private final RendererCapabilities[] rendererCapabilities;
    public final long rendererPositionOffsetUs;
    private final Renderer[] renderers;
    public final SampleStream[] sampleStreams;
    public long startPositionUs;
    private final TrackSelector trackSelector;
    public TrackSelectorResult trackSelectorResult;
    public final Object uid;
    
    public MediaPeriodHolder(Renderer[] paramArrayOfRenderer, RendererCapabilities[] paramArrayOfRendererCapabilities, long paramLong1, TrackSelector paramTrackSelector, LoadControl paramLoadControl, MediaSource paramMediaSource, Object paramObject, int paramInt, boolean paramBoolean, long paramLong2)
    {
      this.renderers = paramArrayOfRenderer;
      this.rendererCapabilities = paramArrayOfRendererCapabilities;
      this.rendererPositionOffsetUs = paramLong1;
      this.trackSelector = paramTrackSelector;
      this.loadControl = paramLoadControl;
      this.mediaSource = paramMediaSource;
      this.uid = Assertions.checkNotNull(paramObject);
      this.index = paramInt;
      this.isLast = paramBoolean;
      this.startPositionUs = paramLong2;
      this.sampleStreams = new SampleStream[paramArrayOfRenderer.length];
      this.mayRetainStreamFlags = new boolean[paramArrayOfRenderer.length];
      this.mediaPeriod = paramMediaSource.createPeriod(paramInt, paramLoadControl.getAllocator(), paramLong2);
    }
    
    public long getRendererOffset()
    {
      return this.rendererPositionOffsetUs - this.startPositionUs;
    }
    
    public void handlePrepared()
      throws ExoPlaybackException
    {
      this.prepared = true;
      selectTracks();
      this.startPositionUs = updatePeriodTrackSelection(this.startPositionUs, false);
    }
    
    public boolean isFullyBuffered()
    {
      return (this.prepared) && ((!this.hasEnabledTracks) || (this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE));
    }
    
    public void release()
    {
      try
      {
        this.mediaSource.releasePeriod(this.mediaPeriod);
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        Log.e("ExoPlayerImplInternal", "Period release failed.", localRuntimeException);
      }
    }
    
    public boolean selectTracks()
      throws ExoPlaybackException
    {
      TrackSelectorResult localTrackSelectorResult = this.trackSelector.selectTracks(this.rendererCapabilities, this.mediaPeriod.getTrackGroups());
      if (localTrackSelectorResult.isEquivalent(this.periodTrackSelectorResult)) {
        return false;
      }
      this.trackSelectorResult = localTrackSelectorResult;
      return true;
    }
    
    public void setIndex(int paramInt, boolean paramBoolean)
    {
      this.index = paramInt;
      this.isLast = paramBoolean;
    }
    
    public long toPeriodTime(long paramLong)
    {
      return paramLong - getRendererOffset();
    }
    
    public long toRendererTime(long paramLong)
    {
      return getRendererOffset() + paramLong;
    }
    
    public long updatePeriodTrackSelection(long paramLong, boolean paramBoolean)
    {
      return updatePeriodTrackSelection(paramLong, paramBoolean, new boolean[this.renderers.length]);
    }
    
    public long updatePeriodTrackSelection(long paramLong, boolean paramBoolean, boolean[] paramArrayOfBoolean)
    {
      TrackSelectionArray localTrackSelectionArray = this.trackSelectorResult.selections;
      int i = 0;
      if (i < localTrackSelectionArray.length)
      {
        boolean[] arrayOfBoolean = this.mayRetainStreamFlags;
        if ((!paramBoolean) && (this.trackSelectorResult.isEquivalent(this.periodTrackSelectorResult, i))) {}
        for (int j = 1;; j = 0)
        {
          arrayOfBoolean[i] = j;
          i += 1;
          break;
        }
      }
      paramLong = this.mediaPeriod.selectTracks(localTrackSelectionArray.getAll(), this.mayRetainStreamFlags, this.sampleStreams, paramArrayOfBoolean, paramLong);
      this.periodTrackSelectorResult = this.trackSelectorResult;
      this.hasEnabledTracks = false;
      i = 0;
      if (i < this.sampleStreams.length)
      {
        if (this.sampleStreams[i] != null)
        {
          if (localTrackSelectionArray.get(i) != null) {}
          for (paramBoolean = true;; paramBoolean = false)
          {
            Assertions.checkState(paramBoolean);
            this.hasEnabledTracks = true;
            i += 1;
            break;
          }
        }
        if (localTrackSelectionArray.get(i) == null) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          Assertions.checkState(paramBoolean);
          break;
        }
      }
      this.loadControl.onTracksSelected(this.renderers, this.trackSelectorResult.groups, localTrackSelectionArray);
      return paramLong;
    }
  }
  
  public static final class PlaybackInfo
  {
    public volatile long bufferedPositionUs;
    public final int periodIndex;
    public volatile long positionUs;
    public final long startPositionUs;
    
    public PlaybackInfo(int paramInt, long paramLong)
    {
      this.periodIndex = paramInt;
      this.startPositionUs = paramLong;
      this.positionUs = paramLong;
      this.bufferedPositionUs = paramLong;
    }
    
    public PlaybackInfo copyWithPeriodIndex(int paramInt)
    {
      PlaybackInfo localPlaybackInfo = new PlaybackInfo(paramInt, this.startPositionUs);
      localPlaybackInfo.positionUs = this.positionUs;
      localPlaybackInfo.bufferedPositionUs = this.bufferedPositionUs;
      return localPlaybackInfo;
    }
  }
  
  private static final class SeekPosition
  {
    public final Timeline timeline;
    public final int windowIndex;
    public final long windowPositionUs;
    
    public SeekPosition(Timeline paramTimeline, int paramInt, long paramLong)
    {
      this.timeline = paramTimeline;
      this.windowIndex = paramInt;
      this.windowPositionUs = paramLong;
    }
  }
  
  public static final class SourceInfo
  {
    public final Object manifest;
    public final ExoPlayerImplInternal.PlaybackInfo playbackInfo;
    public final int seekAcks;
    public final Timeline timeline;
    
    public SourceInfo(Timeline paramTimeline, Object paramObject, ExoPlayerImplInternal.PlaybackInfo paramPlaybackInfo, int paramInt)
    {
      this.timeline = paramTimeline;
      this.manifest = paramObject;
      this.playbackInfo = paramPlaybackInfo;
      this.seekAcks = paramInt;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ExoPlayerImplInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */