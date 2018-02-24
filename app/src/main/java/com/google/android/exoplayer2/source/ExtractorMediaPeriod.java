package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.DefaultTrackOutput;
import com.google.android.exoplayer2.extractor.DefaultTrackOutput.UpstreamFormatChangedListener;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ConditionVariable;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class ExtractorMediaPeriod
  implements MediaPeriod, ExtractorOutput, Loader.Callback<ExtractingLoadable>, DefaultTrackOutput.UpstreamFormatChangedListener
{
  private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000L;
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final String customCacheKey;
  private final DataSource dataSource;
  private long durationUs;
  private int enabledTrackCount;
  private final Handler eventHandler;
  private final ExtractorMediaSource.EventListener eventListener;
  private int extractedSamplesCountAtStartOfLoad;
  private final ExtractorHolder extractorHolder;
  private final Handler handler;
  private boolean haveAudioVideoTracks;
  private long lastSeekPositionUs;
  private long length;
  private final ConditionVariable loadCondition;
  private final Loader loader;
  private boolean loadingFinished;
  private final Runnable maybeFinishPrepareRunnable;
  private final int minLoadableRetryCount;
  private boolean notifyReset;
  private final Runnable onContinueLoadingRequestedRunnable;
  private long pendingResetPositionUs;
  private boolean prepared;
  private boolean released;
  private final SparseArray<DefaultTrackOutput> sampleQueues;
  private SeekMap seekMap;
  private boolean seenFirstTrackSelection;
  private final MediaSource.Listener sourceListener;
  private boolean[] trackEnabledStates;
  private boolean[] trackIsAudioVideoFlags;
  private TrackGroupArray tracks;
  private boolean tracksBuilt;
  private final Uri uri;
  
  public ExtractorMediaPeriod(Uri paramUri, DataSource paramDataSource, Extractor[] paramArrayOfExtractor, int paramInt, Handler paramHandler, ExtractorMediaSource.EventListener paramEventListener, MediaSource.Listener paramListener, Allocator paramAllocator, String paramString)
  {
    this.uri = paramUri;
    this.dataSource = paramDataSource;
    this.minLoadableRetryCount = paramInt;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.sourceListener = paramListener;
    this.allocator = paramAllocator;
    this.customCacheKey = paramString;
    this.loader = new Loader("Loader:ExtractorMediaPeriod");
    this.extractorHolder = new ExtractorHolder(paramArrayOfExtractor, this);
    this.loadCondition = new ConditionVariable();
    this.maybeFinishPrepareRunnable = new Runnable()
    {
      public void run()
      {
        ExtractorMediaPeriod.this.maybeFinishPrepare();
      }
    };
    this.onContinueLoadingRequestedRunnable = new Runnable()
    {
      public void run()
      {
        if (!ExtractorMediaPeriod.this.released) {
          ExtractorMediaPeriod.this.callback.onContinueLoadingRequested(ExtractorMediaPeriod.this);
        }
      }
    };
    this.handler = new Handler();
    this.pendingResetPositionUs = -9223372036854775807L;
    this.sampleQueues = new SparseArray();
    this.length = -1L;
  }
  
  private void configureRetry(ExtractingLoadable paramExtractingLoadable)
  {
    if ((this.length != -1L) || ((this.seekMap != null) && (this.seekMap.getDurationUs() != -9223372036854775807L))) {
      return;
    }
    this.lastSeekPositionUs = 0L;
    this.notifyReset = this.prepared;
    int j = this.sampleQueues.size();
    int i = 0;
    if (i < j)
    {
      DefaultTrackOutput localDefaultTrackOutput = (DefaultTrackOutput)this.sampleQueues.valueAt(i);
      if ((!this.prepared) || (this.trackEnabledStates[i] != 0)) {}
      for (boolean bool = true;; bool = false)
      {
        localDefaultTrackOutput.reset(bool);
        i += 1;
        break;
      }
    }
    paramExtractingLoadable.setLoadPosition(0L, 0L);
  }
  
  private void copyLengthFromLoader(ExtractingLoadable paramExtractingLoadable)
  {
    if (this.length == -1L) {
      this.length = paramExtractingLoadable.length;
    }
  }
  
  private int getExtractedSamplesCount()
  {
    int j = 0;
    int k = this.sampleQueues.size();
    int i = 0;
    while (i < k)
    {
      j += ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getWriteIndex();
      i += 1;
    }
    return j;
  }
  
  private long getLargestQueuedTimestampUs()
  {
    long l = Long.MIN_VALUE;
    int j = this.sampleQueues.size();
    int i = 0;
    while (i < j)
    {
      l = Math.max(l, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
      i += 1;
    }
    return l;
  }
  
  private boolean isLoadableExceptionFatal(IOException paramIOException)
  {
    return paramIOException instanceof UnrecognizedInputFormatException;
  }
  
  private boolean isPendingReset()
  {
    return this.pendingResetPositionUs != -9223372036854775807L;
  }
  
  private void maybeFinishPrepare()
  {
    if ((this.released) || (this.prepared) || (this.seekMap == null) || (!this.tracksBuilt)) {
      return;
    }
    int j = this.sampleQueues.size();
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        break label68;
      }
      if (((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getUpstreamFormat() == null) {
        break;
      }
      i += 1;
    }
    label68:
    this.loadCondition.close();
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[j];
    this.trackIsAudioVideoFlags = new boolean[j];
    this.trackEnabledStates = new boolean[j];
    this.durationUs = this.seekMap.getDurationUs();
    i = 0;
    if (i < j)
    {
      Object localObject = ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getUpstreamFormat();
      arrayOfTrackGroup[i] = new TrackGroup(new Format[] { localObject });
      localObject = ((Format)localObject).sampleMimeType;
      if ((MimeTypes.isVideo((String)localObject)) || (MimeTypes.isAudio((String)localObject))) {}
      for (int k = 1;; k = 0)
      {
        this.trackIsAudioVideoFlags[i] = k;
        this.haveAudioVideoTracks |= k;
        i += 1;
        break;
      }
    }
    this.tracks = new TrackGroupArray(arrayOfTrackGroup);
    this.prepared = true;
    this.sourceListener.onSourceInfoRefreshed(new SinglePeriodTimeline(this.durationUs, this.seekMap.isSeekable()), null);
    this.callback.onPrepared(this);
  }
  
  private void notifyLoadError(final IOException paramIOException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ExtractorMediaPeriod.this.eventListener.onLoadError(paramIOException);
        }
      });
    }
  }
  
  private void startLoading()
  {
    ExtractingLoadable localExtractingLoadable = new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.loadCondition);
    if (this.prepared)
    {
      Assertions.checkState(isPendingReset());
      if ((this.durationUs != -9223372036854775807L) && (this.pendingResetPositionUs >= this.durationUs))
      {
        this.loadingFinished = true;
        this.pendingResetPositionUs = -9223372036854775807L;
        return;
      }
      localExtractingLoadable.setLoadPosition(this.seekMap.getPosition(this.pendingResetPositionUs), this.pendingResetPositionUs);
      this.pendingResetPositionUs = -9223372036854775807L;
    }
    this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
    int j = this.minLoadableRetryCount;
    int i = j;
    if (j == -1) {
      if ((this.prepared) && (this.length == -1L) && ((this.seekMap == null) || (this.seekMap.getDurationUs() == -9223372036854775807L))) {
        break label178;
      }
    }
    label178:
    for (i = 3;; i = 6)
    {
      this.loader.startLoading(localExtractingLoadable, this, i);
      return;
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool;
    if ((this.loadingFinished) || ((this.prepared) && (this.enabledTrackCount == 0))) {
      bool = false;
    }
    do
    {
      return bool;
      bool = this.loadCondition.open();
    } while (this.loader.isLoading());
    startLoading();
    return true;
  }
  
  public void endTracks()
  {
    this.tracksBuilt = true;
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public long getBufferedPositionUs()
  {
    long l1;
    if (this.loadingFinished) {
      l1 = Long.MIN_VALUE;
    }
    long l2;
    do
    {
      return l1;
      if (isPendingReset()) {
        return this.pendingResetPositionUs;
      }
      if (this.haveAudioVideoTracks)
      {
        l1 = Long.MAX_VALUE;
        int j = this.sampleQueues.size();
        int i = 0;
        for (;;)
        {
          l2 = l1;
          if (i >= j) {
            break;
          }
          l2 = l1;
          if (this.trackIsAudioVideoFlags[i] != 0) {
            l2 = Math.min(l1, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
          }
          i += 1;
          l1 = l2;
        }
      }
      l2 = getLargestQueuedTimestampUs();
      l1 = l2;
    } while (l2 != Long.MIN_VALUE);
    return this.lastSeekPositionUs;
  }
  
  public long getNextLoadPositionUs()
  {
    if (this.enabledTrackCount == 0) {
      return Long.MIN_VALUE;
    }
    return getBufferedPositionUs();
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.tracks;
  }
  
  boolean isReady(int paramInt)
  {
    return (this.loadingFinished) || ((!isPendingReset()) && (!((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).isEmpty()));
  }
  
  void maybeThrowError()
    throws IOException
  {
    this.loader.maybeThrowError();
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    maybeThrowError();
  }
  
  public void onLoadCanceled(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    copyLengthFromLoader(paramExtractingLoadable);
    if ((!paramBoolean) && (this.enabledTrackCount > 0))
    {
      int j = this.sampleQueues.size();
      int i = 0;
      while (i < j)
      {
        ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).reset(this.trackEnabledStates[i]);
        i += 1;
      }
      this.callback.onContinueLoadingRequested(this);
    }
  }
  
  public void onLoadCompleted(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2)
  {
    copyLengthFromLoader(paramExtractingLoadable);
    this.loadingFinished = true;
    if (this.durationUs == -9223372036854775807L)
    {
      paramLong1 = getLargestQueuedTimestampUs();
      if (paramLong1 != Long.MIN_VALUE) {
        break label72;
      }
    }
    label72:
    for (paramLong1 = 0L;; paramLong1 = 10000L + paramLong1)
    {
      this.durationUs = paramLong1;
      this.sourceListener.onSourceInfoRefreshed(new SinglePeriodTimeline(this.durationUs, this.seekMap.isSeekable()), null);
      return;
    }
  }
  
  public int onLoadError(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    int j = 1;
    copyLengthFromLoader(paramExtractingLoadable);
    notifyLoadError(paramIOException);
    if (isLoadableExceptionFatal(paramIOException))
    {
      j = 3;
      return j;
    }
    if (getExtractedSamplesCount() > this.extractedSamplesCountAtStartOfLoad) {}
    for (int i = 1;; i = 0)
    {
      configureRetry(paramExtractingLoadable);
      this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
      if (i != 0) {
        break;
      }
      return 0;
    }
  }
  
  public void onUpstreamFormatChanged(Format paramFormat)
  {
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback)
  {
    this.callback = paramCallback;
    this.loadCondition.open();
    startLoading();
  }
  
  int readData(int paramInt, FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    if ((this.notifyReset) || (isPendingReset())) {
      return -3;
    }
    return ((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).readData(paramFormatHolder, paramDecoderInputBuffer, this.loadingFinished, this.lastSeekPositionUs);
  }
  
  public long readDiscontinuity()
  {
    if (this.notifyReset)
    {
      this.notifyReset = false;
      return this.lastSeekPositionUs;
    }
    return -9223372036854775807L;
  }
  
  public void release()
  {
    final ExtractorHolder localExtractorHolder = this.extractorHolder;
    this.loader.release(new Runnable()
    {
      public void run()
      {
        localExtractorHolder.release();
        int j = ExtractorMediaPeriod.this.sampleQueues.size();
        int i = 0;
        while (i < j)
        {
          ((DefaultTrackOutput)ExtractorMediaPeriod.this.sampleQueues.valueAt(i)).disable();
          i += 1;
        }
      }
    });
    this.handler.removeCallbacksAndMessages(null);
    this.released = true;
  }
  
  public void seekMap(SeekMap paramSeekMap)
  {
    this.seekMap = paramSeekMap;
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public long seekToUs(long paramLong)
  {
    int j;
    if (this.seekMap.isSeekable())
    {
      this.lastSeekPositionUs = paramLong;
      j = this.sampleQueues.size();
      if (isPendingReset()) {
        break label87;
      }
    }
    int i;
    label87:
    for (boolean bool = true;; bool = false)
    {
      i = 0;
      while ((bool) && (i < j))
      {
        if (this.trackEnabledStates[i] != 0) {
          bool = ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).skipToKeyframeBefore(paramLong);
        }
        i += 1;
      }
      paramLong = 0L;
      break;
    }
    if (!bool)
    {
      this.pendingResetPositionUs = paramLong;
      this.loadingFinished = false;
      if (!this.loader.isLoading()) {
        break label132;
      }
      this.loader.cancelLoading();
    }
    for (;;)
    {
      this.notifyReset = false;
      return paramLong;
      label132:
      i = 0;
      while (i < j)
      {
        ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).reset(this.trackEnabledStates[i]);
        i += 1;
      }
    }
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    Assertions.checkState(this.prepared);
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfSampleStream[i] != null) && ((paramArrayOfTrackSelection[i] == null) || (paramArrayOfBoolean1[i] == 0)))
      {
        j = ((SampleStreamImpl)paramArrayOfSampleStream[i]).track;
        Assertions.checkState(this.trackEnabledStates[j]);
        this.enabledTrackCount -= 1;
        this.trackEnabledStates[j] = false;
        ((DefaultTrackOutput)this.sampleQueues.valueAt(j)).disable();
        paramArrayOfSampleStream[i] = null;
      }
      i += 1;
    }
    i = 0;
    int j = 0;
    int k;
    if (j < paramArrayOfTrackSelection.length)
    {
      k = i;
      if (paramArrayOfSampleStream[j] == null)
      {
        k = i;
        if (paramArrayOfTrackSelection[j] != null)
        {
          paramArrayOfBoolean1 = paramArrayOfTrackSelection[j];
          if (paramArrayOfBoolean1.length() != 1) {
            break label270;
          }
          bool = true;
          label160:
          Assertions.checkState(bool);
          if (paramArrayOfBoolean1.getIndexInTrackGroup(0) != 0) {
            break label276;
          }
          bool = true;
          label178:
          Assertions.checkState(bool);
          i = this.tracks.indexOf(paramArrayOfBoolean1.getTrackGroup());
          if (this.trackEnabledStates[i] != 0) {
            break label282;
          }
        }
      }
      label270:
      label276:
      label282:
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.enabledTrackCount += 1;
        this.trackEnabledStates[i] = true;
        paramArrayOfSampleStream[j] = new SampleStreamImpl(i);
        paramArrayOfBoolean2[j] = true;
        k = 1;
        j += 1;
        i = k;
        break;
        bool = false;
        break label160;
        bool = false;
        break label178;
      }
    }
    if (!this.seenFirstTrackSelection)
    {
      k = this.sampleQueues.size();
      j = 0;
      while (j < k)
      {
        if (this.trackEnabledStates[j] == 0) {
          ((DefaultTrackOutput)this.sampleQueues.valueAt(j)).disable();
        }
        j += 1;
      }
    }
    long l;
    if (this.enabledTrackCount == 0)
    {
      this.notifyReset = false;
      l = paramLong;
      if (this.loader.isLoading())
      {
        this.loader.cancelLoading();
        l = paramLong;
      }
    }
    do
    {
      this.seenFirstTrackSelection = true;
      return l;
      if (!this.seenFirstTrackSelection) {
        break;
      }
      l = paramLong;
    } while (i == 0);
    for (;;)
    {
      paramLong = seekToUs(paramLong);
      i = 0;
      for (;;)
      {
        l = paramLong;
        if (i >= paramArrayOfSampleStream.length) {
          break;
        }
        if (paramArrayOfSampleStream[i] != null) {
          paramArrayOfBoolean2[i] = true;
        }
        i += 1;
      }
      l = paramLong;
      if (paramLong == 0L) {
        break;
      }
    }
  }
  
  public TrackOutput track(int paramInt)
  {
    DefaultTrackOutput localDefaultTrackOutput2 = (DefaultTrackOutput)this.sampleQueues.get(paramInt);
    DefaultTrackOutput localDefaultTrackOutput1 = localDefaultTrackOutput2;
    if (localDefaultTrackOutput2 == null)
    {
      localDefaultTrackOutput1 = new DefaultTrackOutput(this.allocator);
      localDefaultTrackOutput1.setUpstreamFormatChangeListener(this);
      this.sampleQueues.put(paramInt, localDefaultTrackOutput1);
    }
    return localDefaultTrackOutput1;
  }
  
  final class ExtractingLoadable
    implements Loader.Loadable
  {
    private static final int CONTINUE_LOADING_CHECK_INTERVAL_BYTES = 1048576;
    private final DataSource dataSource;
    private final ExtractorMediaPeriod.ExtractorHolder extractorHolder;
    private long length;
    private volatile boolean loadCanceled;
    private final ConditionVariable loadCondition;
    private boolean pendingExtractorSeek;
    private final PositionHolder positionHolder;
    private long seekTimeUs;
    private final Uri uri;
    
    public ExtractingLoadable(Uri paramUri, DataSource paramDataSource, ExtractorMediaPeriod.ExtractorHolder paramExtractorHolder, ConditionVariable paramConditionVariable)
    {
      this.uri = ((Uri)Assertions.checkNotNull(paramUri));
      this.dataSource = ((DataSource)Assertions.checkNotNull(paramDataSource));
      this.extractorHolder = ((ExtractorMediaPeriod.ExtractorHolder)Assertions.checkNotNull(paramExtractorHolder));
      this.loadCondition = paramConditionVariable;
      this.positionHolder = new PositionHolder();
      this.pendingExtractorSeek = true;
      this.length = -1L;
    }
    
    public void cancelLoad()
    {
      this.loadCanceled = true;
    }
    
    public boolean isLoadCanceled()
    {
      return this.loadCanceled;
    }
    
    public void load()
      throws IOException, InterruptedException
    {
      int i = 0;
      if ((i == 0) && (!this.loadCanceled)) {
        for (;;)
        {
          try
          {
            long l2 = this.positionHolder.position;
            this.length = this.dataSource.open(new DataSpec(this.uri, l2, -1L, ExtractorMediaPeriod.this.customCacheKey));
            if (this.length != -1L) {
              this.length += l2;
            }
            localDefaultExtractorInput = new DefaultExtractorInput(this.dataSource, l2, this.length);
            int k = i;
            try
            {
              Extractor localExtractor = this.extractorHolder.selectExtractor(localDefaultExtractorInput, this.dataSource.getUri());
              long l1 = l2;
              j = i;
              k = i;
              if (this.pendingExtractorSeek)
              {
                k = i;
                localExtractor.seek(l2, this.seekTimeUs);
                k = i;
                this.pendingExtractorSeek = false;
                j = i;
                l1 = l2;
              }
              if (j != 0) {
                continue;
              }
              k = j;
              if (this.loadCanceled) {
                continue;
              }
              k = j;
              this.loadCondition.block();
              k = j;
              i = localExtractor.read(localDefaultExtractorInput, this.positionHolder);
              j = i;
              k = i;
              if (localDefaultExtractorInput.getPosition() <= 1048576L + l1) {
                continue;
              }
              k = i;
              l1 = localDefaultExtractorInput.getPosition();
              k = i;
              this.loadCondition.close();
              k = i;
              ExtractorMediaPeriod.this.handler.post(ExtractorMediaPeriod.this.onContinueLoadingRequestedRunnable);
              j = i;
              continue;
              if (i != 1) {
                continue;
              }
            }
            finally
            {
              i = k;
            }
          }
          finally
          {
            int j;
            DefaultExtractorInput localDefaultExtractorInput = null;
            continue;
          }
          Util.closeQuietly(this.dataSource);
          throw ((Throwable)localObject1);
          if (j == 1)
          {
            i = 0;
            Util.closeQuietly(this.dataSource);
            break;
          }
          i = j;
          if (localDefaultExtractorInput != null)
          {
            this.positionHolder.position = localDefaultExtractorInput.getPosition();
            i = j;
            continue;
            if (localDefaultExtractorInput != null) {
              this.positionHolder.position = localDefaultExtractorInput.getPosition();
            }
          }
        }
      }
    }
    
    public void setLoadPosition(long paramLong1, long paramLong2)
    {
      this.positionHolder.position = paramLong1;
      this.seekTimeUs = paramLong2;
      this.pendingExtractorSeek = true;
    }
  }
  
  private static final class ExtractorHolder
  {
    private Extractor extractor;
    private final ExtractorOutput extractorOutput;
    private final Extractor[] extractors;
    
    public ExtractorHolder(Extractor[] paramArrayOfExtractor, ExtractorOutput paramExtractorOutput)
    {
      this.extractors = paramArrayOfExtractor;
      this.extractorOutput = paramExtractorOutput;
    }
    
    public void release()
    {
      if (this.extractor != null)
      {
        this.extractor.release();
        this.extractor = null;
      }
    }
    
    /* Error */
    public Extractor selectExtractor(ExtractorInput paramExtractorInput, Uri paramUri)
      throws IOException, InterruptedException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 26	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
      //   4: ifnull +8 -> 12
      //   7: aload_0
      //   8: getfield 26	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
      //   11: areturn
      //   12: aload_0
      //   13: getfield 20	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractors	[Lcom/google/android/exoplayer2/extractor/Extractor;
      //   16: astore 5
      //   18: aload 5
      //   20: arraylength
      //   21: istore 4
      //   23: iconst_0
      //   24: istore_3
      //   25: iload_3
      //   26: iload 4
      //   28: if_icmpge +32 -> 60
      //   31: aload 5
      //   33: iload_3
      //   34: aaload
      //   35: astore 6
      //   37: aload 6
      //   39: aload_1
      //   40: invokeinterface 42 2 0
      //   45: ifeq +61 -> 106
      //   48: aload_0
      //   49: aload 6
      //   51: putfield 26	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
      //   54: aload_1
      //   55: invokeinterface 47 1 0
      //   60: aload_0
      //   61: getfield 26	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
      //   64: ifnonnull +75 -> 139
      //   67: new 49	com/google/android/exoplayer2/source/UnrecognizedInputFormatException
      //   70: dup
      //   71: new 51	java/lang/StringBuilder
      //   74: dup
      //   75: invokespecial 52	java/lang/StringBuilder:<init>	()V
      //   78: ldc 54
      //   80: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   83: aload_0
      //   84: getfield 20	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractors	[Lcom/google/android/exoplayer2/extractor/Extractor;
      //   87: invokestatic 64	com/google/android/exoplayer2/util/Util:getCommaDelimitedSimpleClassNames	([Ljava/lang/Object;)Ljava/lang/String;
      //   90: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   93: ldc 66
      //   95: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   98: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   101: aload_2
      //   102: invokespecial 73	com/google/android/exoplayer2/source/UnrecognizedInputFormatException:<init>	(Ljava/lang/String;Landroid/net/Uri;)V
      //   105: athrow
      //   106: aload_1
      //   107: invokeinterface 47 1 0
      //   112: iload_3
      //   113: iconst_1
      //   114: iadd
      //   115: istore_3
      //   116: goto -91 -> 25
      //   119: astore 6
      //   121: aload_1
      //   122: invokeinterface 47 1 0
      //   127: goto -15 -> 112
      //   130: astore_2
      //   131: aload_1
      //   132: invokeinterface 47 1 0
      //   137: aload_2
      //   138: athrow
      //   139: aload_0
      //   140: getfield 26	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
      //   143: aload_0
      //   144: getfield 22	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractorOutput	Lcom/google/android/exoplayer2/extractor/ExtractorOutput;
      //   147: invokeinterface 77 2 0
      //   152: aload_0
      //   153: getfield 26	com/google/android/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
      //   156: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	157	0	this	ExtractorHolder
      //   0	157	1	paramExtractorInput	ExtractorInput
      //   0	157	2	paramUri	Uri
      //   24	92	3	i	int
      //   21	8	4	j	int
      //   16	16	5	arrayOfExtractor	Extractor[]
      //   35	15	6	localExtractor	Extractor
      //   119	1	6	localEOFException	java.io.EOFException
      // Exception table:
      //   from	to	target	type
      //   37	54	119	java/io/EOFException
      //   37	54	130	finally
    }
  }
  
  private final class SampleStreamImpl
    implements SampleStream
  {
    private final int track;
    
    public SampleStreamImpl(int paramInt)
    {
      this.track = paramInt;
    }
    
    public boolean isReady()
    {
      return ExtractorMediaPeriod.this.isReady(this.track);
    }
    
    public void maybeThrowError()
      throws IOException
    {
      ExtractorMediaPeriod.this.maybeThrowError();
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
    {
      return ExtractorMediaPeriod.this.readData(this.track, paramFormatHolder, paramDecoderInputBuffer);
    }
    
    public void skipToKeyframeBefore(long paramLong)
    {
      ((DefaultTrackOutput)ExtractorMediaPeriod.this.sampleQueues.valueAt(this.track)).skipToKeyframeBefore(paramLong);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/ExtractorMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */