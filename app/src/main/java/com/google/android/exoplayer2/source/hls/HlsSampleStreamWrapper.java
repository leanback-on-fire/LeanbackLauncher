package com.google.android.exoplayer2.source.hls;

import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.DefaultTrackOutput;
import com.google.android.exoplayer2.extractor.DefaultTrackOutput.UpstreamFormatChangedListener;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.IOException;
import java.util.LinkedList;

final class HlsSampleStreamWrapper
  implements Loader.Callback<Chunk>, SequenceableLoader, ExtractorOutput, DefaultTrackOutput.UpstreamFormatChangedListener
{
  private static final int PRIMARY_TYPE_AUDIO = 2;
  private static final int PRIMARY_TYPE_NONE = 0;
  private static final int PRIMARY_TYPE_TEXT = 1;
  private static final int PRIMARY_TYPE_VIDEO = 3;
  private final Allocator allocator;
  private final Callback callback;
  private final HlsChunkSource chunkSource;
  private Format downstreamTrackFormat;
  private int enabledTrackCount;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private boolean[] groupEnabledStates;
  private final Handler handler;
  private long lastSeekPositionUs;
  private final Loader loader;
  private boolean loadingFinished;
  private final Runnable maybeFinishPrepareRunnable;
  private final LinkedList<HlsMediaChunk> mediaChunks;
  private final int minLoadableRetryCount;
  private final Format muxedAudioFormat;
  private final Format muxedCaptionFormat;
  private final HlsChunkSource.HlsChunkHolder nextChunkHolder;
  private long pendingResetPositionUs;
  private boolean prepared;
  private int primaryTrackGroupIndex;
  private boolean released;
  private final SparseArray<DefaultTrackOutput> sampleQueues;
  private boolean sampleQueuesBuilt;
  private TrackGroupArray trackGroups;
  private final int trackType;
  private int upstreamChunkUid;
  
  public HlsSampleStreamWrapper(int paramInt1, Callback paramCallback, HlsChunkSource paramHlsChunkSource, Allocator paramAllocator, long paramLong, Format paramFormat1, Format paramFormat2, int paramInt2, AdaptiveMediaSourceEventListener.EventDispatcher paramEventDispatcher)
  {
    this.trackType = paramInt1;
    this.callback = paramCallback;
    this.chunkSource = paramHlsChunkSource;
    this.allocator = paramAllocator;
    this.muxedAudioFormat = paramFormat1;
    this.muxedCaptionFormat = paramFormat2;
    this.minLoadableRetryCount = paramInt2;
    this.eventDispatcher = paramEventDispatcher;
    this.loader = new Loader("Loader:HlsSampleStreamWrapper");
    this.nextChunkHolder = new HlsChunkSource.HlsChunkHolder();
    this.sampleQueues = new SparseArray();
    this.mediaChunks = new LinkedList();
    this.maybeFinishPrepareRunnable = new Runnable()
    {
      public void run()
      {
        HlsSampleStreamWrapper.this.maybeFinishPrepare();
      }
    };
    this.handler = new Handler();
    this.lastSeekPositionUs = paramLong;
    this.pendingResetPositionUs = paramLong;
  }
  
  private void buildTracks()
  {
    int m = 0;
    int n = -1;
    int i2 = this.sampleQueues.size();
    int j = 0;
    Object localObject1;
    if (j < i2)
    {
      localObject1 = ((DefaultTrackOutput)this.sampleQueues.valueAt(j)).getUpstreamFormat().sampleMimeType;
      label52:
      int i1;
      if (MimeTypes.isVideo((String)localObject1))
      {
        i = 3;
        if (i <= m) {
          break label108;
        }
        k = j;
        i1 = i;
      }
      for (;;)
      {
        j += 1;
        n = k;
        m = i1;
        break;
        if (MimeTypes.isAudio((String)localObject1))
        {
          i = 2;
          break label52;
        }
        if (MimeTypes.isText((String)localObject1))
        {
          i = 1;
          break label52;
        }
        i = 0;
        break label52;
        label108:
        k = n;
        i1 = m;
        if (i == m)
        {
          k = n;
          i1 = m;
          if (n != -1)
          {
            k = -1;
            i1 = m;
          }
        }
      }
    }
    TrackGroup localTrackGroup = this.chunkSource.getTrackGroup();
    int k = localTrackGroup.length;
    this.primaryTrackGroupIndex = -1;
    this.groupEnabledStates = new boolean[i2];
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[i2];
    int i = 0;
    while (i < i2)
    {
      Format localFormat = ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getUpstreamFormat();
      if (i == n)
      {
        localObject1 = new Format[k];
        j = 0;
        while (j < k)
        {
          localObject1[j] = deriveFormat(localTrackGroup.getFormat(j), localFormat);
          j += 1;
        }
        arrayOfTrackGroup[i] = new TrackGroup((Format[])localObject1);
        this.primaryTrackGroupIndex = i;
        i += 1;
      }
      else
      {
        Object localObject2 = null;
        localObject1 = localObject2;
        if (m == 3)
        {
          if (!MimeTypes.isAudio(localFormat.sampleMimeType)) {
            break label326;
          }
          localObject1 = this.muxedAudioFormat;
        }
        for (;;)
        {
          arrayOfTrackGroup[i] = new TrackGroup(new Format[] { deriveFormat((Format)localObject1, localFormat) });
          break;
          label326:
          localObject1 = localObject2;
          if ("application/cea-608".equals(localFormat.sampleMimeType)) {
            localObject1 = this.muxedCaptionFormat;
          }
        }
      }
    }
    this.trackGroups = new TrackGroupArray(arrayOfTrackGroup);
  }
  
  private static Format deriveFormat(Format paramFormat1, Format paramFormat2)
  {
    if (paramFormat1 == null) {
      return paramFormat2;
    }
    String str = null;
    int i = MimeTypes.getTrackType(paramFormat2.sampleMimeType);
    if (i == 1) {
      str = getAudioCodecs(paramFormat1.codecs);
    }
    for (;;)
    {
      return paramFormat2.copyWithContainerInfo(paramFormat1.id, str, paramFormat1.bitrate, paramFormat1.width, paramFormat1.height, paramFormat1.selectionFlags, paramFormat1.language);
      if (i == 2) {
        str = getVideoCodecs(paramFormat1.codecs);
      }
    }
  }
  
  private boolean finishedReadingChunk(HlsMediaChunk paramHlsMediaChunk)
  {
    int j = paramHlsMediaChunk.uid;
    int i = 0;
    while (i < this.sampleQueues.size())
    {
      if ((this.groupEnabledStates[i] != 0) && (((DefaultTrackOutput)this.sampleQueues.valueAt(i)).peekSourceId() == j)) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private static String getAudioCodecs(String paramString)
  {
    return getCodecsOfType(paramString, 1);
  }
  
  private static String getCodecsOfType(String paramString, int paramInt)
  {
    if (TextUtils.isEmpty(paramString)) {}
    StringBuilder localStringBuilder;
    do
    {
      return null;
      paramString = paramString.split("(\\s*,\\s*)|(\\s*$)");
      localStringBuilder = new StringBuilder();
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        String str = paramString[i];
        if (paramInt == MimeTypes.getTrackTypeOfCodec(str))
        {
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(",");
          }
          localStringBuilder.append(str);
        }
        i += 1;
      }
    } while (localStringBuilder.length() <= 0);
    return localStringBuilder.toString();
  }
  
  private static String getVideoCodecs(String paramString)
  {
    return getCodecsOfType(paramString, 2);
  }
  
  private boolean isMediaChunk(Chunk paramChunk)
  {
    return paramChunk instanceof HlsMediaChunk;
  }
  
  private boolean isPendingReset()
  {
    return this.pendingResetPositionUs != -9223372036854775807L;
  }
  
  private void maybeFinishPrepare()
  {
    if ((this.released) || (this.prepared) || (!this.sampleQueuesBuilt)) {
      return;
    }
    int j = this.sampleQueues.size();
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        break label61;
      }
      if (((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getUpstreamFormat() == null) {
        break;
      }
      i += 1;
    }
    label61:
    buildTracks();
    this.prepared = true;
    this.callback.onPrepared();
  }
  
  private void setTrackGroupEnabledState(int paramInt, boolean paramBoolean)
  {
    int i = 1;
    boolean bool;
    int j;
    if (this.groupEnabledStates[paramInt] != paramBoolean)
    {
      bool = true;
      Assertions.checkState(bool);
      this.groupEnabledStates[paramInt] = paramBoolean;
      j = this.enabledTrackCount;
      if (!paramBoolean) {
        break label54;
      }
    }
    label54:
    for (paramInt = i;; paramInt = -1)
    {
      this.enabledTrackCount = (j + paramInt);
      return;
      bool = false;
      break;
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    if ((this.loadingFinished) || (this.loader.isLoading())) {
      return false;
    }
    Object localObject2 = this.chunkSource;
    if (this.mediaChunks.isEmpty()) {}
    for (Object localObject1 = null;; localObject1 = (HlsMediaChunk)this.mediaChunks.getLast())
    {
      if (this.pendingResetPositionUs != -9223372036854775807L) {
        paramLong = this.pendingResetPositionUs;
      }
      ((HlsChunkSource)localObject2).getNextChunk((HlsMediaChunk)localObject1, paramLong, this.nextChunkHolder);
      boolean bool = this.nextChunkHolder.endOfStream;
      localObject1 = this.nextChunkHolder.chunk;
      localObject2 = this.nextChunkHolder.playlist;
      this.nextChunkHolder.clear();
      if (!bool) {
        break;
      }
      this.loadingFinished = true;
      return true;
    }
    if (localObject1 == null)
    {
      if (localObject2 != null) {
        this.callback.onPlaylistRefreshRequired((HlsMasterPlaylist.HlsUrl)localObject2);
      }
      return false;
    }
    if (isMediaChunk((Chunk)localObject1))
    {
      this.pendingResetPositionUs = -9223372036854775807L;
      localObject2 = (HlsMediaChunk)localObject1;
      ((HlsMediaChunk)localObject2).init(this);
      this.mediaChunks.add(localObject2);
    }
    paramLong = this.loader.startLoading((Loader.Loadable)localObject1, this, this.minLoadableRetryCount);
    this.eventDispatcher.loadStarted(((Chunk)localObject1).dataSpec, ((Chunk)localObject1).type, this.trackType, ((Chunk)localObject1).trackFormat, ((Chunk)localObject1).trackSelectionReason, ((Chunk)localObject1).trackSelectionData, ((Chunk)localObject1).startTimeUs, ((Chunk)localObject1).endTimeUs, paramLong);
    return true;
  }
  
  public void continuePreparing()
  {
    if (!this.prepared) {
      continueLoading(this.lastSeekPositionUs);
    }
  }
  
  public void endTracks()
  {
    this.sampleQueuesBuilt = true;
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public long getBufferedPositionUs()
  {
    if (this.loadingFinished)
    {
      l2 = Long.MIN_VALUE;
      return l2;
    }
    if (isPendingReset()) {
      return this.pendingResetPositionUs;
    }
    long l2 = this.lastSeekPositionUs;
    HlsMediaChunk localHlsMediaChunk = (HlsMediaChunk)this.mediaChunks.getLast();
    if (localHlsMediaChunk.isLoadCompleted()) {}
    for (;;)
    {
      long l1 = l2;
      if (localHlsMediaChunk != null) {
        l1 = Math.max(l2, localHlsMediaChunk.endTimeUs);
      }
      int j = this.sampleQueues.size();
      int i = 0;
      for (;;)
      {
        l2 = l1;
        if (i >= j) {
          break;
        }
        l1 = Math.max(l1, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
        i += 1;
      }
      if (this.mediaChunks.size() > 1) {
        localHlsMediaChunk = (HlsMediaChunk)this.mediaChunks.get(this.mediaChunks.size() - 2);
      } else {
        localHlsMediaChunk = null;
      }
    }
  }
  
  public long getLargestQueuedTimestampUs()
  {
    long l = Long.MIN_VALUE;
    int i = 0;
    while (i < this.sampleQueues.size())
    {
      l = Math.max(l, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
      i += 1;
    }
    return l;
  }
  
  public long getNextLoadPositionUs()
  {
    if (isPendingReset()) {
      return this.pendingResetPositionUs;
    }
    if (this.loadingFinished) {
      return Long.MIN_VALUE;
    }
    return ((HlsMediaChunk)this.mediaChunks.getLast()).endTimeUs;
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.trackGroups;
  }
  
  public void init(int paramInt, boolean paramBoolean)
  {
    this.upstreamChunkUid = paramInt;
    int i = 0;
    while (i < this.sampleQueues.size())
    {
      ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).sourceId(paramInt);
      i += 1;
    }
    if (paramBoolean)
    {
      paramInt = 0;
      while (paramInt < this.sampleQueues.size())
      {
        ((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).splice();
        paramInt += 1;
      }
    }
  }
  
  boolean isReady(int paramInt)
  {
    return (this.loadingFinished) || ((!isPendingReset()) && (!((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).isEmpty()));
  }
  
  void maybeThrowError()
    throws IOException
  {
    this.loader.maybeThrowError();
    this.chunkSource.maybeThrowError();
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    maybeThrowError();
  }
  
  public void onLoadCanceled(Chunk paramChunk, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!paramBoolean)
    {
      int j = this.sampleQueues.size();
      int i = 0;
      while (i < j)
      {
        ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).reset(this.groupEnabledStates[i]);
        i += 1;
      }
      this.callback.onContinueLoadingRequested(this);
    }
  }
  
  public void onLoadCompleted(Chunk paramChunk, long paramLong1, long paramLong2)
  {
    this.chunkSource.onChunkLoadCompleted(paramChunk);
    this.eventDispatcher.loadCompleted(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!this.prepared)
    {
      continueLoading(this.lastSeekPositionUs);
      return;
    }
    this.callback.onContinueLoadingRequested(this);
  }
  
  public int onLoadError(Chunk paramChunk, long paramLong1, long paramLong2, IOException paramIOException)
  {
    long l = paramChunk.bytesLoaded();
    boolean bool3 = isMediaChunk(paramChunk);
    boolean bool1;
    if ((!bool3) || (l == 0L))
    {
      bool1 = true;
      boolean bool2 = false;
      if (this.chunkSource.onChunkLoadError(paramChunk, bool1, paramIOException))
      {
        if (bool3)
        {
          if ((HlsMediaChunk)this.mediaChunks.removeLast() != paramChunk) {
            break label173;
          }
          bool1 = true;
          label68:
          Assertions.checkState(bool1);
          if (this.mediaChunks.isEmpty()) {
            this.pendingResetPositionUs = this.lastSeekPositionUs;
          }
        }
        bool2 = true;
      }
      this.eventDispatcher.loadError(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded(), paramIOException, bool2);
      if (!bool2) {
        break label192;
      }
      if (this.prepared) {
        break label179;
      }
      continueLoading(this.lastSeekPositionUs);
    }
    for (;;)
    {
      return 2;
      bool1 = false;
      break;
      label173:
      bool1 = false;
      break label68;
      label179:
      this.callback.onContinueLoadingRequested(this);
    }
    label192:
    return 0;
  }
  
  public void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    this.chunkSource.onPlaylistBlacklisted(paramHlsUrl, paramLong);
  }
  
  public void onUpstreamFormatChanged(Format paramFormat)
  {
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public void prepareSingleTrack(Format paramFormat)
  {
    track(0).format(paramFormat);
    this.sampleQueuesBuilt = true;
    maybeFinishPrepare();
  }
  
  int readData(int paramInt, FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    if (isPendingReset()) {
      return -3;
    }
    while ((this.mediaChunks.size() > 1) && (finishedReadingChunk((HlsMediaChunk)this.mediaChunks.getFirst()))) {
      this.mediaChunks.removeFirst();
    }
    HlsMediaChunk localHlsMediaChunk = (HlsMediaChunk)this.mediaChunks.getFirst();
    Format localFormat = localHlsMediaChunk.trackFormat;
    if (!localFormat.equals(this.downstreamTrackFormat)) {
      this.eventDispatcher.downstreamFormatChanged(this.trackType, localFormat, localHlsMediaChunk.trackSelectionReason, localHlsMediaChunk.trackSelectionData, localHlsMediaChunk.startTimeUs);
    }
    this.downstreamTrackFormat = localFormat;
    return ((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).readData(paramFormatHolder, paramDecoderInputBuffer, this.loadingFinished, this.lastSeekPositionUs);
  }
  
  public void release()
  {
    int j = this.sampleQueues.size();
    int i = 0;
    while (i < j)
    {
      ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).disable();
      i += 1;
    }
    this.loader.release();
    this.handler.removeCallbacksAndMessages(null);
    this.released = true;
  }
  
  public void seekMap(SeekMap paramSeekMap) {}
  
  public void seekTo(long paramLong)
  {
    this.lastSeekPositionUs = paramLong;
    this.pendingResetPositionUs = paramLong;
    this.loadingFinished = false;
    this.mediaChunks.clear();
    if (this.loader.isLoading()) {
      this.loader.cancelLoading();
    }
    for (;;)
    {
      return;
      int j = this.sampleQueues.size();
      int i = 0;
      while (i < j)
      {
        ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).reset(this.groupEnabledStates[i]);
        i += 1;
      }
    }
  }
  
  public boolean selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, boolean paramBoolean)
  {
    Assertions.checkState(this.prepared);
    int i = 0;
    int j;
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfSampleStream[i] != null) && ((paramArrayOfTrackSelection[i] == null) || (paramArrayOfBoolean1[i] == 0)))
      {
        j = ((HlsSampleStream)paramArrayOfSampleStream[i]).group;
        setTrackGroupEnabledState(j, false);
        ((DefaultTrackOutput)this.sampleQueues.valueAt(j)).disable();
        paramArrayOfSampleStream[i] = null;
      }
      i += 1;
    }
    boolean bool1 = false;
    i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      boolean bool2 = bool1;
      if (paramArrayOfSampleStream[i] == null)
      {
        bool2 = bool1;
        if (paramArrayOfTrackSelection[i] != null)
        {
          paramArrayOfBoolean1 = paramArrayOfTrackSelection[i];
          j = this.trackGroups.indexOf(paramArrayOfBoolean1.getTrackGroup());
          setTrackGroupEnabledState(j, true);
          if (j == this.primaryTrackGroupIndex) {
            this.chunkSource.selectTracks(paramArrayOfBoolean1);
          }
          paramArrayOfSampleStream[i] = new HlsSampleStream(this, j);
          paramArrayOfBoolean2[i] = true;
          bool2 = true;
        }
      }
      i += 1;
      bool1 = bool2;
    }
    if (paramBoolean)
    {
      j = this.sampleQueues.size();
      i = 0;
      while (i < j)
      {
        if (this.groupEnabledStates[i] == 0) {
          ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).disable();
        }
        i += 1;
      }
    }
    if (this.enabledTrackCount == 0)
    {
      this.chunkSource.reset();
      this.downstreamTrackFormat = null;
      this.mediaChunks.clear();
      if (this.loader.isLoading()) {
        this.loader.cancelLoading();
      }
    }
    return bool1;
  }
  
  public void setIsTimestampMaster(boolean paramBoolean)
  {
    this.chunkSource.setIsTimestampMaster(paramBoolean);
  }
  
  void skipToKeyframeBefore(int paramInt, long paramLong)
  {
    ((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).skipToKeyframeBefore(paramLong);
  }
  
  public DefaultTrackOutput track(int paramInt)
  {
    if (this.sampleQueues.indexOfKey(paramInt) >= 0) {
      return (DefaultTrackOutput)this.sampleQueues.get(paramInt);
    }
    DefaultTrackOutput localDefaultTrackOutput = new DefaultTrackOutput(this.allocator);
    localDefaultTrackOutput.setUpstreamFormatChangeListener(this);
    localDefaultTrackOutput.sourceId(this.upstreamChunkUid);
    this.sampleQueues.put(paramInt, localDefaultTrackOutput);
    return localDefaultTrackOutput;
  }
  
  public static abstract interface Callback
    extends SequenceableLoader.Callback<HlsSampleStreamWrapper>
  {
    public abstract void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl paramHlsUrl);
    
    public abstract void onPrepared();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/HlsSampleStreamWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */