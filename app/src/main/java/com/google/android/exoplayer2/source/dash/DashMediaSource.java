package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.Listener;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.source.dash.manifest.UtcTimingElement;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower.Dummy;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class DashMediaSource
  implements MediaSource
{
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_FIXED_MS = 30000L;
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_PREFER_MANIFEST_MS = -1L;
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000L;
  private static final int NOTIFY_MANIFEST_INTERVAL_MS = 5000;
  private static final String TAG = "DashMediaSource";
  private final DashChunkSource.Factory chunkSourceFactory;
  private DataSource dataSource;
  private long elapsedRealtimeOffsetMs;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private int firstPeriodId;
  private Handler handler;
  private final long livePresentationDelayMs;
  private Loader loader;
  private LoaderErrorThrower loaderErrorThrower;
  private DashManifest manifest;
  private final ManifestCallback manifestCallback;
  private final DataSource.Factory manifestDataSourceFactory;
  private long manifestLoadEndTimestamp;
  private long manifestLoadStartTimestamp;
  private final DashManifestParser manifestParser;
  private Uri manifestUri;
  private final Object manifestUriLock;
  private final int minLoadableRetryCount;
  private final SparseArray<DashMediaPeriod> periodsById;
  private final Runnable refreshManifestRunnable;
  private final boolean sideloadedManifest;
  private final Runnable simulateManifestRefreshRunnable;
  private MediaSource.Listener sourceListener;
  
  public DashMediaSource(Uri paramUri, DataSource.Factory paramFactory, DashChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramUri, paramFactory, new DashManifestParser(), paramFactory1, paramInt, paramLong, paramHandler, paramAdaptiveMediaSourceEventListener);
  }
  
  public DashMediaSource(Uri paramUri, DataSource.Factory paramFactory, DashChunkSource.Factory paramFactory1, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramUri, paramFactory, paramFactory1, 3, -1L, paramHandler, paramAdaptiveMediaSourceEventListener);
  }
  
  public DashMediaSource(Uri paramUri, DataSource.Factory paramFactory, DashManifestParser paramDashManifestParser, DashChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(null, paramUri, paramFactory, paramDashManifestParser, paramFactory1, paramInt, paramLong, paramHandler, paramAdaptiveMediaSourceEventListener);
  }
  
  private DashMediaSource(DashManifest paramDashManifest, Uri paramUri, DataSource.Factory paramFactory, DashManifestParser paramDashManifestParser, DashChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this.manifest = paramDashManifest;
    this.manifestUri = paramUri;
    this.manifestDataSourceFactory = paramFactory;
    this.manifestParser = paramDashManifestParser;
    this.chunkSourceFactory = paramFactory1;
    this.minLoadableRetryCount = paramInt;
    this.livePresentationDelayMs = paramLong;
    if (paramDashManifest != null)
    {
      bool1 = true;
      this.sideloadedManifest = bool1;
      this.eventDispatcher = new AdaptiveMediaSourceEventListener.EventDispatcher(paramHandler, paramAdaptiveMediaSourceEventListener);
      this.manifestUriLock = new Object();
      this.periodsById = new SparseArray();
      if (!this.sideloadedManifest) {
        break label147;
      }
      if (paramDashManifest.dynamic) {
        break label141;
      }
    }
    label141:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Assertions.checkState(bool1);
      this.manifestCallback = null;
      this.refreshManifestRunnable = null;
      this.simulateManifestRefreshRunnable = null;
      return;
      bool1 = false;
      break;
    }
    label147:
    this.manifestCallback = new ManifestCallback(null);
    this.refreshManifestRunnable = new Runnable()
    {
      public void run()
      {
        DashMediaSource.this.startLoadingManifest();
      }
    };
    this.simulateManifestRefreshRunnable = new Runnable()
    {
      public void run()
      {
        DashMediaSource.this.processManifest(false);
      }
    };
  }
  
  public DashMediaSource(DashManifest paramDashManifest, DashChunkSource.Factory paramFactory, int paramInt, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramDashManifest, null, null, null, paramFactory, paramInt, -1L, paramHandler, paramAdaptiveMediaSourceEventListener);
  }
  
  public DashMediaSource(DashManifest paramDashManifest, DashChunkSource.Factory paramFactory, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramDashManifest, paramFactory, 3, paramHandler, paramAdaptiveMediaSourceEventListener);
  }
  
  private long getNowUnixTimeUs()
  {
    if (this.elapsedRealtimeOffsetMs != 0L) {
      return C.msToUs(SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs);
    }
    return C.msToUs(System.currentTimeMillis());
  }
  
  private void onUtcTimestampResolutionError(IOException paramIOException)
  {
    Log.e("DashMediaSource", "Failed to resolve UtcTiming element.", paramIOException);
    processManifest(true);
  }
  
  private void onUtcTimestampResolved(long paramLong)
  {
    this.elapsedRealtimeOffsetMs = paramLong;
    processManifest(true);
  }
  
  private void processManifest(boolean paramBoolean)
  {
    int i = 0;
    while (i < this.periodsById.size())
    {
      j = this.periodsById.keyAt(i);
      if (j >= this.firstPeriodId) {
        ((DashMediaPeriod)this.periodsById.valueAt(i)).updateManifest(this.manifest, j - this.firstPeriodId);
      }
      i += 1;
    }
    int j = 0;
    int k = this.manifest.getPeriodCount() - 1;
    Object localObject = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(0), this.manifest.getPeriodDurationUs(0));
    PeriodSeekInfo localPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(k), this.manifest.getPeriodDurationUs(k));
    long l3 = ((PeriodSeekInfo)localObject).availableStartTimeUs;
    long l1 = localPeriodSeekInfo.availableEndTimeUs;
    long l2 = l3;
    long l4 = l1;
    i = j;
    if (this.manifest.dynamic)
    {
      l2 = l3;
      l4 = l1;
      i = j;
      if (!localPeriodSeekInfo.isIndexExplicit)
      {
        l4 = Math.min(getNowUnixTimeUs() - C.msToUs(this.manifest.availabilityStartTime) - C.msToUs(this.manifest.getPeriod(k).startMs), l1);
        l1 = l3;
        if (this.manifest.timeShiftBufferDepth != -9223372036854775807L)
        {
          l1 = l4 - C.msToUs(this.manifest.timeShiftBufferDepth);
          i = k;
          while ((l1 < 0L) && (i > 0))
          {
            localObject = this.manifest;
            i -= 1;
            l1 += ((DashManifest)localObject).getPeriodDurationUs(i);
          }
          if (i != 0) {
            break label340;
          }
        }
      }
    }
    label340:
    for (l1 = Math.max(l3, l1);; l1 = this.manifest.getPeriodDurationUs(0))
    {
      i = 1;
      l2 = l1;
      l3 = l4 - l2;
      j = 0;
      while (j < this.manifest.getPeriodCount() - 1)
      {
        l3 += this.manifest.getPeriodDurationUs(j);
        j += 1;
      }
    }
    l1 = 0L;
    if (this.manifest.dynamic)
    {
      l4 = this.livePresentationDelayMs;
      l1 = l4;
      if (l4 == -1L) {
        if (this.manifest.suggestedPresentationDelay == -9223372036854775807L) {
          break label573;
        }
      }
    }
    label573:
    for (l1 = this.manifest.suggestedPresentationDelay;; l1 = 30000L)
    {
      l4 = l3 - C.msToUs(l1);
      l1 = l4;
      if (l4 < 5000000L) {
        l1 = Math.min(5000000L, l3 / 2L);
      }
      l4 = this.manifest.availabilityStartTime;
      long l5 = this.manifest.getPeriod(0).startMs;
      long l6 = C.usToMs(l2);
      localObject = new DashTimeline(this.manifest.availabilityStartTime, l4 + l5 + l6, this.firstPeriodId, l2, l3, l1, this.manifest);
      this.sourceListener.onSourceInfoRefreshed((Timeline)localObject, this.manifest);
      if (!this.sideloadedManifest)
      {
        this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
        if (i != 0) {
          this.handler.postDelayed(this.simulateManifestRefreshRunnable, 5000L);
        }
        if (paramBoolean) {
          scheduleManifestRefresh();
        }
      }
      return;
    }
  }
  
  private void resolveUtcTimingElement(UtcTimingElement paramUtcTimingElement)
  {
    String str = paramUtcTimingElement.schemeIdUri;
    if (Util.areEqual(str, "urn:mpeg:dash:utc:direct:2012"))
    {
      resolveUtcTimingElementDirect(paramUtcTimingElement);
      return;
    }
    if (Util.areEqual(str, "urn:mpeg:dash:utc:http-iso:2014"))
    {
      resolveUtcTimingElementHttp(paramUtcTimingElement, new Iso8601Parser(null));
      return;
    }
    if ((Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2012")) || (Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2014")))
    {
      resolveUtcTimingElementHttp(paramUtcTimingElement, new XsDateTimeParser(null));
      return;
    }
    onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
  }
  
  private void resolveUtcTimingElementDirect(UtcTimingElement paramUtcTimingElement)
  {
    try
    {
      onUtcTimestampResolved(Util.parseXsDateTime(paramUtcTimingElement.value) - this.manifestLoadEndTimestamp);
      return;
    }
    catch (ParserException paramUtcTimingElement)
    {
      onUtcTimestampResolutionError(paramUtcTimingElement);
    }
  }
  
  private void resolveUtcTimingElementHttp(UtcTimingElement paramUtcTimingElement, ParsingLoadable.Parser<Long> paramParser)
  {
    startLoading(new ParsingLoadable(this.dataSource, Uri.parse(paramUtcTimingElement.value), 5, paramParser), new UtcTimestampCallback(null), 1);
  }
  
  private void scheduleManifestRefresh()
  {
    if (!this.manifest.dynamic) {
      return;
    }
    long l2 = this.manifest.minUpdatePeriod;
    long l1 = l2;
    if (l2 == 0L) {
      l1 = 5000L;
    }
    l1 = Math.max(0L, this.manifestLoadStartTimestamp + l1 - SystemClock.elapsedRealtime());
    this.handler.postDelayed(this.refreshManifestRunnable, l1);
  }
  
  private <T> void startLoading(ParsingLoadable<T> paramParsingLoadable, Loader.Callback<ParsingLoadable<T>> paramCallback, int paramInt)
  {
    long l = this.loader.startLoading(paramParsingLoadable, paramCallback, paramInt);
    this.eventDispatcher.loadStarted(paramParsingLoadable.dataSpec, paramParsingLoadable.type, l);
  }
  
  private void startLoadingManifest()
  {
    synchronized (this.manifestUriLock)
    {
      Uri localUri = this.manifestUri;
      startLoading(new ParsingLoadable(this.dataSource, localUri, 4, this.manifestParser), this.manifestCallback, this.minLoadableRetryCount);
      return;
    }
  }
  
  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    AdaptiveMediaSourceEventListener.EventDispatcher localEventDispatcher = this.eventDispatcher.copyWithMediaTimeOffsetMs(this.manifest.getPeriod(paramInt).startMs);
    paramAllocator = new DashMediaPeriod(this.firstPeriodId + paramInt, this.manifest, paramInt, this.chunkSourceFactory, this.minLoadableRetryCount, localEventDispatcher, this.elapsedRealtimeOffsetMs, this.loaderErrorThrower, paramAllocator);
    this.periodsById.put(paramAllocator.id, paramAllocator);
    return paramAllocator;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    this.loaderErrorThrower.maybeThrowError();
  }
  
  void onLoadCanceled(ParsingLoadable<?> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.loadCanceled(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  void onManifestLoadCompleted(ParsingLoadable<DashManifest> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    ??? = (DashManifest)paramParsingLoadable.getResult();
    if (this.manifest == null) {}
    int j;
    for (int i = 0;; i = this.manifest.getPeriodCount())
    {
      j = 0;
      long l = ((DashManifest)???).getPeriod(0).startMs;
      while ((j < i) && (this.manifest.getPeriod(j).startMs < l)) {
        j += 1;
      }
    }
    if (i - j > ((DashManifest)???).getPeriodCount())
    {
      Log.w("DashMediaSource", "Out of sync manifest");
      scheduleManifestRefresh();
      return;
    }
    this.manifest = ((DashManifest)???);
    this.manifestLoadStartTimestamp = (paramLong1 - paramLong2);
    this.manifestLoadEndTimestamp = paramLong1;
    if (this.manifest.location != null) {}
    synchronized (this.manifestUriLock)
    {
      if (paramParsingLoadable.dataSpec.uri == this.manifestUri) {
        this.manifestUri = this.manifest.location;
      }
      if (i != 0) {
        break label233;
      }
      if (this.manifest.utcTiming != null)
      {
        resolveUtcTimingElement(this.manifest.utcTiming);
        return;
      }
    }
    processManifest(true);
    return;
    label233:
    this.firstPeriodId += j;
    processManifest(true);
  }
  
  int onManifestLoadError(ParsingLoadable<DashManifest> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = paramIOException instanceof ParserException;
    this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool) {
      return 3;
    }
    return 0;
  }
  
  void onUtcTimestampLoadCompleted(ParsingLoadable<Long> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    onUtcTimestampResolved(((Long)paramParsingLoadable.getResult()).longValue() - paramLong1);
  }
  
  int onUtcTimestampLoadError(ParsingLoadable<Long> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, true);
    onUtcTimestampResolutionError(paramIOException);
    return 2;
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    this.sourceListener = paramListener;
    if (this.sideloadedManifest)
    {
      this.loaderErrorThrower = new LoaderErrorThrower.Dummy();
      processManifest(false);
      return;
    }
    this.dataSource = this.manifestDataSourceFactory.createDataSource();
    this.loader = new Loader("Loader:DashMediaSource");
    this.loaderErrorThrower = this.loader;
    this.handler = new Handler();
    startLoadingManifest();
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    paramMediaPeriod = (DashMediaPeriod)paramMediaPeriod;
    paramMediaPeriod.release();
    this.periodsById.remove(paramMediaPeriod.id);
  }
  
  public void releaseSource()
  {
    this.dataSource = null;
    this.loaderErrorThrower = null;
    if (this.loader != null)
    {
      this.loader.release();
      this.loader = null;
    }
    this.manifestLoadStartTimestamp = 0L;
    this.manifestLoadEndTimestamp = 0L;
    this.manifest = null;
    if (this.handler != null)
    {
      this.handler.removeCallbacksAndMessages(null);
      this.handler = null;
    }
    this.elapsedRealtimeOffsetMs = 0L;
    this.periodsById.clear();
  }
  
  public void replaceManifestUri(Uri paramUri)
  {
    synchronized (this.manifestUriLock)
    {
      this.manifestUri = paramUri;
      return;
    }
  }
  
  private static final class DashTimeline
    extends Timeline
  {
    private final int firstPeriodId;
    private final DashManifest manifest;
    private final long offsetInFirstPeriodUs;
    private final long presentationStartTimeMs;
    private final long windowDefaultStartPositionUs;
    private final long windowDurationUs;
    private final long windowStartTimeMs;
    
    public DashTimeline(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4, long paramLong5, DashManifest paramDashManifest)
    {
      this.presentationStartTimeMs = paramLong1;
      this.windowStartTimeMs = paramLong2;
      this.firstPeriodId = paramInt;
      this.offsetInFirstPeriodUs = paramLong3;
      this.windowDurationUs = paramLong4;
      this.windowDefaultStartPositionUs = paramLong5;
      this.manifest = paramDashManifest;
    }
    
    private long getAdjustedWindowDefaultStartPositionUs(long paramLong)
    {
      long l2 = this.windowDefaultStartPositionUs;
      if (!this.manifest.dynamic) {
        return l2;
      }
      long l1 = l2;
      if (paramLong > 0L)
      {
        paramLong = l2 + paramLong;
        l1 = paramLong;
        if (paramLong > this.windowDurationUs) {
          return -9223372036854775807L;
        }
      }
      int i = 0;
      paramLong = this.offsetInFirstPeriodUs + l1;
      for (l2 = this.manifest.getPeriodDurationUs(0); (i < this.manifest.getPeriodCount() - 1) && (paramLong >= l2); l2 = this.manifest.getPeriodDurationUs(i))
      {
        paramLong -= l2;
        i += 1;
      }
      Object localObject = this.manifest.getPeriod(i);
      i = ((Period)localObject).getAdaptationSetIndex(2);
      if (i == -1) {
        return l1;
      }
      localObject = ((Representation)((AdaptationSet)((Period)localObject).adaptationSets.get(i)).representations.get(0)).getIndex();
      if (localObject == null) {
        return l1;
      }
      return ((DashSegmentIndex)localObject).getTimeUs(((DashSegmentIndex)localObject).getSegmentNum(paramLong, l2)) + l1 - paramLong;
    }
    
    public int getIndexOfPeriod(Object paramObject)
    {
      if (!(paramObject instanceof Integer)) {}
      int i;
      do
      {
        return -1;
        i = ((Integer)paramObject).intValue();
      } while ((i < this.firstPeriodId) || (i >= this.firstPeriodId + getPeriodCount()));
      return i - this.firstPeriodId;
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      Integer localInteger = null;
      Assertions.checkIndex(paramInt, 0, this.manifest.getPeriodCount());
      if (paramBoolean) {}
      for (String str = this.manifest.getPeriod(paramInt).id;; str = null)
      {
        if (paramBoolean) {
          localInteger = Integer.valueOf(this.firstPeriodId + Assertions.checkIndex(paramInt, 0, this.manifest.getPeriodCount()));
        }
        return paramPeriod.set(str, localInteger, 0, this.manifest.getPeriodDurationUs(paramInt), C.msToUs(this.manifest.getPeriod(paramInt).startMs - this.manifest.getPeriod(0).startMs) - this.offsetInFirstPeriodUs);
      }
    }
    
    public int getPeriodCount()
    {
      return this.manifest.getPeriodCount();
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      Assertions.checkIndex(paramInt, 0, 1);
      paramLong = getAdjustedWindowDefaultStartPositionUs(paramLong);
      return paramWindow.set(null, this.presentationStartTimeMs, this.windowStartTimeMs, true, this.manifest.dynamic, paramLong, this.windowDurationUs, 0, this.manifest.getPeriodCount() - 1, this.offsetInFirstPeriodUs);
    }
    
    public int getWindowCount()
    {
      return 1;
    }
  }
  
  private static final class Iso8601Parser
    implements ParsingLoadable.Parser<Long>
  {
    public Long parse(Uri paramUri, InputStream paramInputStream)
      throws IOException
    {
      paramUri = new BufferedReader(new InputStreamReader(paramInputStream)).readLine();
      try
      {
        paramInputStream = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        paramInputStream.setTimeZone(TimeZone.getTimeZone("UTC"));
        long l = paramInputStream.parse(paramUri).getTime();
        return Long.valueOf(l);
      }
      catch (ParseException paramUri)
      {
        throw new ParserException(paramUri);
      }
    }
  }
  
  private final class ManifestCallback
    implements Loader.Callback<ParsingLoadable<DashManifest>>
  {
    private ManifestCallback() {}
    
    public void onLoadCanceled(ParsingLoadable<DashManifest> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      DashMediaSource.this.onLoadCanceled(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public void onLoadCompleted(ParsingLoadable<DashManifest> paramParsingLoadable, long paramLong1, long paramLong2)
    {
      DashMediaSource.this.onManifestLoadCompleted(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public int onLoadError(ParsingLoadable<DashManifest> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
    {
      return DashMediaSource.this.onManifestLoadError(paramParsingLoadable, paramLong1, paramLong2, paramIOException);
    }
  }
  
  private static final class PeriodSeekInfo
  {
    public final long availableEndTimeUs;
    public final long availableStartTimeUs;
    public final boolean isIndexExplicit;
    
    private PeriodSeekInfo(boolean paramBoolean, long paramLong1, long paramLong2)
    {
      this.isIndexExplicit = paramBoolean;
      this.availableStartTimeUs = paramLong1;
      this.availableEndTimeUs = paramLong2;
    }
    
    public static PeriodSeekInfo createPeriodSeekInfo(Period paramPeriod, long paramLong)
    {
      int j = paramPeriod.adaptationSets.size();
      long l3 = 0L;
      long l1 = Long.MAX_VALUE;
      boolean bool = false;
      int i = 0;
      while (i < j)
      {
        DashSegmentIndex localDashSegmentIndex = ((Representation)((AdaptationSet)paramPeriod.adaptationSets.get(i)).representations.get(0)).getIndex();
        if (localDashSegmentIndex == null) {
          return new PeriodSeekInfo(true, 0L, paramLong);
        }
        int k = localDashSegmentIndex.getFirstSegmentNum();
        int m = localDashSegmentIndex.getLastSegmentNum(paramLong);
        bool |= localDashSegmentIndex.isExplicit();
        l3 = Math.max(l3, localDashSegmentIndex.getTimeUs(k));
        long l2 = l1;
        if (m != -1) {
          l2 = Math.min(l1, localDashSegmentIndex.getTimeUs(m) + localDashSegmentIndex.getDurationUs(m, paramLong));
        }
        i += 1;
        l1 = l2;
      }
      return new PeriodSeekInfo(bool, l3, l1);
    }
  }
  
  private final class UtcTimestampCallback
    implements Loader.Callback<ParsingLoadable<Long>>
  {
    private UtcTimestampCallback() {}
    
    public void onLoadCanceled(ParsingLoadable<Long> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      DashMediaSource.this.onLoadCanceled(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public void onLoadCompleted(ParsingLoadable<Long> paramParsingLoadable, long paramLong1, long paramLong2)
    {
      DashMediaSource.this.onUtcTimestampLoadCompleted(paramParsingLoadable, paramLong1, paramLong2);
    }
    
    public int onLoadError(ParsingLoadable<Long> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
    {
      return DashMediaSource.this.onUtcTimestampLoadError(paramParsingLoadable, paramLong1, paramLong2, paramIOException);
    }
  }
  
  private static final class XsDateTimeParser
    implements ParsingLoadable.Parser<Long>
  {
    public Long parse(Uri paramUri, InputStream paramInputStream)
      throws IOException
    {
      return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(paramInputStream)).readLine()));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/DashMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */