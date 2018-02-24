package com.google.android.exoplayer2.source.hls;

import android.os.Handler;
import android.text.TextUtils;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.CompositeSequenceableLoader;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

public final class HlsMediaPeriod
  implements MediaPeriod, HlsSampleStreamWrapper.Callback, HlsPlaylistTracker.PlaylistEventListener
{
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final Handler continueLoadingHandler;
  private final DataSource.Factory dataSourceFactory;
  private HlsSampleStreamWrapper[] enabledSampleStreamWrappers;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private final int minLoadableRetryCount;
  private int pendingPrepareCount;
  private final HlsPlaylistTracker playlistTracker;
  private final long preparePositionUs;
  private HlsSampleStreamWrapper[] sampleStreamWrappers;
  private boolean seenFirstTrackSelection;
  private CompositeSequenceableLoader sequenceableLoader;
  private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices;
  private final TimestampAdjusterProvider timestampAdjusterProvider;
  private TrackGroupArray trackGroups;
  
  public HlsMediaPeriod(HlsPlaylistTracker paramHlsPlaylistTracker, DataSource.Factory paramFactory, int paramInt, AdaptiveMediaSourceEventListener.EventDispatcher paramEventDispatcher, Allocator paramAllocator, long paramLong)
  {
    this.playlistTracker = paramHlsPlaylistTracker;
    this.dataSourceFactory = paramFactory;
    this.minLoadableRetryCount = paramInt;
    this.eventDispatcher = paramEventDispatcher;
    this.allocator = paramAllocator;
    this.streamWrapperIndices = new IdentityHashMap();
    this.timestampAdjusterProvider = new TimestampAdjusterProvider();
    this.continueLoadingHandler = new Handler();
    this.preparePositionUs = paramLong;
  }
  
  private void buildAndPrepareSampleStreamWrappers()
  {
    Object localObject3 = this.playlistTracker.getMasterPlaylist();
    Object localObject2 = new ArrayList(((HlsMasterPlaylist)localObject3).variants);
    Object localObject1 = new ArrayList();
    Object localObject4 = new ArrayList();
    int i = 0;
    Object localObject5;
    if (i < ((List)localObject2).size())
    {
      localObject5 = (HlsMasterPlaylist.HlsUrl)((List)localObject2).get(i);
      if ((((HlsMasterPlaylist.HlsUrl)localObject5).format.height > 0) || (variantHasExplicitCodecWithPrefix((HlsMasterPlaylist.HlsUrl)localObject5, "avc"))) {
        ((ArrayList)localObject1).add(localObject5);
      }
      for (;;)
      {
        i += 1;
        break;
        if (variantHasExplicitCodecWithPrefix((HlsMasterPlaylist.HlsUrl)localObject5, "mp4a")) {
          ((ArrayList)localObject4).add(localObject5);
        }
      }
    }
    if (!((ArrayList)localObject1).isEmpty())
    {
      localObject4 = ((HlsMasterPlaylist)localObject3).audios;
      localObject2 = ((HlsMasterPlaylist)localObject3).subtitles;
      this.sampleStreamWrappers = new HlsSampleStreamWrapper[((List)localObject4).size() + 1 + ((List)localObject2).size()];
      this.pendingPrepareCount = this.sampleStreamWrappers.length;
      if (((List)localObject1).isEmpty()) {
        break label359;
      }
    }
    label359:
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      localObject5 = new HlsMasterPlaylist.HlsUrl[((List)localObject1).size()];
      ((List)localObject1).toArray((Object[])localObject5);
      localObject1 = buildSampleStreamWrapper(0, (HlsMasterPlaylist.HlsUrl[])localObject5, ((HlsMasterPlaylist)localObject3).muxedAudioFormat, ((HlsMasterPlaylist)localObject3).muxedCaptionFormat);
      this.sampleStreamWrappers[0] = localObject1;
      ((HlsSampleStreamWrapper)localObject1).setIsTimestampMaster(true);
      ((HlsSampleStreamWrapper)localObject1).continuePreparing();
      j = 0;
      i = 0 + 1;
      while (j < ((List)localObject4).size())
      {
        localObject1 = buildSampleStreamWrapper(1, new HlsMasterPlaylist.HlsUrl[] { (HlsMasterPlaylist.HlsUrl)((List)localObject4).get(j) }, null, null);
        this.sampleStreamWrappers[i] = localObject1;
        ((HlsSampleStreamWrapper)localObject1).continuePreparing();
        j += 1;
        i += 1;
      }
      localObject1 = localObject2;
      if (((ArrayList)localObject4).size() >= ((List)localObject2).size()) {
        break;
      }
      ((List)localObject2).removeAll((Collection)localObject4);
      localObject1 = localObject2;
      break;
    }
    int j = 0;
    while (j < ((List)localObject2).size())
    {
      localObject1 = (HlsMasterPlaylist.HlsUrl)((List)localObject2).get(j);
      localObject3 = buildSampleStreamWrapper(3, new HlsMasterPlaylist.HlsUrl[] { localObject1 }, null, null);
      ((HlsSampleStreamWrapper)localObject3).prepareSingleTrack(((HlsMasterPlaylist.HlsUrl)localObject1).format);
      this.sampleStreamWrappers[i] = localObject3;
      j += 1;
      i += 1;
    }
  }
  
  private HlsSampleStreamWrapper buildSampleStreamWrapper(int paramInt, HlsMasterPlaylist.HlsUrl[] paramArrayOfHlsUrl, Format paramFormat1, Format paramFormat2)
  {
    DataSource localDataSource = this.dataSourceFactory.createDataSource();
    return new HlsSampleStreamWrapper(paramInt, this, new HlsChunkSource(this.playlistTracker, paramArrayOfHlsUrl, localDataSource, this.timestampAdjusterProvider), this.allocator, this.preparePositionUs, paramFormat1, paramFormat2, this.minLoadableRetryCount, this.eventDispatcher);
  }
  
  private void continuePreparingOrLoading()
  {
    if (this.trackGroups != null) {
      this.callback.onContinueLoadingRequested(this);
    }
    for (;;)
    {
      return;
      HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
      int j = arrayOfHlsSampleStreamWrapper.length;
      int i = 0;
      while (i < j)
      {
        arrayOfHlsSampleStreamWrapper[i].continuePreparing();
        i += 1;
      }
    }
  }
  
  private static boolean variantHasExplicitCodecWithPrefix(HlsMasterPlaylist.HlsUrl paramHlsUrl, String paramString)
  {
    paramHlsUrl = paramHlsUrl.format.codecs;
    if (TextUtils.isEmpty(paramHlsUrl)) {}
    for (;;)
    {
      return false;
      paramHlsUrl = paramHlsUrl.split("(\\s*,\\s*)|(\\s*$)");
      int j = paramHlsUrl.length;
      int i = 0;
      while (i < j)
      {
        if (paramHlsUrl[i].startsWith(paramString)) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    return this.sequenceableLoader.continueLoading(paramLong);
  }
  
  public long getBufferedPositionUs()
  {
    long l1 = Long.MAX_VALUE;
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.enabledSampleStreamWrappers;
    int j = arrayOfHlsSampleStreamWrapper.length;
    int i = 0;
    while (i < j)
    {
      long l3 = arrayOfHlsSampleStreamWrapper[i].getBufferedPositionUs();
      l2 = l1;
      if (l3 != Long.MIN_VALUE) {
        l2 = Math.min(l1, l3);
      }
      i += 1;
      l1 = l2;
    }
    long l2 = l1;
    if (l1 == Long.MAX_VALUE) {
      l2 = Long.MIN_VALUE;
    }
    return l2;
  }
  
  public long getNextLoadPositionUs()
  {
    return this.sequenceableLoader.getNextLoadPositionUs();
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.trackGroups;
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    if (this.sampleStreamWrappers != null)
    {
      HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
      int j = arrayOfHlsSampleStreamWrapper.length;
      int i = 0;
      while (i < j)
      {
        arrayOfHlsSampleStreamWrapper[i].maybeThrowPrepareError();
        i += 1;
      }
    }
  }
  
  public void onContinueLoadingRequested(HlsSampleStreamWrapper paramHlsSampleStreamWrapper)
  {
    if (this.trackGroups == null) {
      return;
    }
    this.callback.onContinueLoadingRequested(this);
  }
  
  public void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
    int j = arrayOfHlsSampleStreamWrapper.length;
    int i = 0;
    while (i < j)
    {
      arrayOfHlsSampleStreamWrapper[i].onPlaylistBlacklisted(paramHlsUrl, paramLong);
      i += 1;
    }
    continuePreparingOrLoading();
  }
  
  public void onPlaylistChanged()
  {
    continuePreparingOrLoading();
  }
  
  public void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    this.playlistTracker.refreshPlaylist(paramHlsUrl);
  }
  
  public void onPrepared()
  {
    int k = 0;
    int i = this.pendingPrepareCount - 1;
    this.pendingPrepareCount = i;
    if (i > 0) {
      return;
    }
    int j = 0;
    Object localObject = this.sampleStreamWrappers;
    int m = localObject.length;
    i = 0;
    while (i < m)
    {
      j += localObject[i].getTrackGroups().length;
      i += 1;
    }
    localObject = new TrackGroup[j];
    i = 0;
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
    m = arrayOfHlsSampleStreamWrapper.length;
    j = k;
    while (j < m)
    {
      HlsSampleStreamWrapper localHlsSampleStreamWrapper = arrayOfHlsSampleStreamWrapper[j];
      int n = localHlsSampleStreamWrapper.getTrackGroups().length;
      k = 0;
      while (k < n)
      {
        localObject[i] = localHlsSampleStreamWrapper.getTrackGroups().get(k);
        k += 1;
        i += 1;
      }
      j += 1;
    }
    this.trackGroups = new TrackGroupArray((TrackGroup[])localObject);
    this.callback.onPrepared(this);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback)
  {
    this.playlistTracker.addListener(this);
    this.callback = paramCallback;
    buildAndPrepareSampleStreamWrappers();
  }
  
  public long readDiscontinuity()
  {
    return -9223372036854775807L;
  }
  
  public void release()
  {
    this.playlistTracker.removeListener(this);
    this.continueLoadingHandler.removeCallbacksAndMessages(null);
    if (this.sampleStreamWrappers != null)
    {
      HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
      int j = arrayOfHlsSampleStreamWrapper.length;
      int i = 0;
      while (i < j)
      {
        arrayOfHlsSampleStreamWrapper[i].release();
        i += 1;
      }
    }
  }
  
  public long seekToUs(long paramLong)
  {
    this.timestampAdjusterProvider.reset();
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.enabledSampleStreamWrappers;
    int j = arrayOfHlsSampleStreamWrapper.length;
    int i = 0;
    while (i < j)
    {
      arrayOfHlsSampleStreamWrapper[i].seekTo(paramLong);
      i += 1;
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int[] arrayOfInt1 = new int[paramArrayOfTrackSelection.length];
    int[] arrayOfInt2 = new int[paramArrayOfTrackSelection.length];
    int i = 0;
    label32:
    Object localObject;
    if (i < paramArrayOfTrackSelection.length)
    {
      if (paramArrayOfSampleStream[i] == null)
      {
        j = -1;
        arrayOfInt1[i] = j;
        arrayOfInt2[i] = -1;
        if (paramArrayOfTrackSelection[i] != null)
        {
          localObject = paramArrayOfTrackSelection[i].getTrackGroup();
          j = 0;
        }
      }
      for (;;)
      {
        if (j < this.sampleStreamWrappers.length)
        {
          if (this.sampleStreamWrappers[j].getTrackGroups().indexOf((TrackGroup)localObject) != -1) {
            arrayOfInt2[i] = j;
          }
        }
        else
        {
          i += 1;
          break;
          j = ((Integer)this.streamWrapperIndices.get(paramArrayOfSampleStream[i])).intValue();
          break label32;
        }
        j += 1;
      }
    }
    int j = 0;
    this.streamWrapperIndices.clear();
    SampleStream[] arrayOfSampleStream1 = new SampleStream[paramArrayOfTrackSelection.length];
    SampleStream[] arrayOfSampleStream2 = new SampleStream[paramArrayOfTrackSelection.length];
    TrackSelection[] arrayOfTrackSelection = new TrackSelection[paramArrayOfTrackSelection.length];
    ArrayList localArrayList = new ArrayList(this.sampleStreamWrappers.length);
    i = 0;
    while (i < this.sampleStreamWrappers.length)
    {
      int k = 0;
      if (k < paramArrayOfTrackSelection.length)
      {
        if (arrayOfInt1[k] == i)
        {
          localObject = paramArrayOfSampleStream[k];
          label226:
          arrayOfSampleStream2[k] = localObject;
          if (arrayOfInt2[k] != i) {
            break label271;
          }
        }
        label271:
        for (localObject = paramArrayOfTrackSelection[k];; localObject = null)
        {
          arrayOfTrackSelection[k] = localObject;
          k += 1;
          break;
          localObject = null;
          break label226;
        }
      }
      localObject = this.sampleStreamWrappers[i];
      int n;
      label321:
      label349:
      int m;
      if (!this.seenFirstTrackSelection)
      {
        bool = true;
        n = j | ((HlsSampleStreamWrapper)localObject).selectTracks(arrayOfTrackSelection, paramArrayOfBoolean1, arrayOfSampleStream2, paramArrayOfBoolean2, bool);
        k = 0;
        j = 0;
        if (j >= paramArrayOfTrackSelection.length) {
          break label453;
        }
        if (arrayOfInt2[j] != i) {
          break label410;
        }
        if (arrayOfSampleStream2[j] == null) {
          break label404;
        }
        bool = true;
        Assertions.checkState(bool);
        arrayOfSampleStream1[j] = arrayOfSampleStream2[j];
        m = 1;
        this.streamWrapperIndices.put(arrayOfSampleStream2[j], Integer.valueOf(i));
      }
      label404:
      label410:
      do
      {
        j += 1;
        k = m;
        break label321;
        bool = false;
        break;
        bool = false;
        break label349;
        m = k;
      } while (arrayOfInt1[j] != i);
      if (arrayOfSampleStream2[j] == null) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        m = k;
        break;
      }
      label453:
      if (k != 0) {
        localArrayList.add(this.sampleStreamWrappers[i]);
      }
      i += 1;
      j = n;
    }
    System.arraycopy(arrayOfSampleStream1, 0, paramArrayOfSampleStream, 0, arrayOfSampleStream1.length);
    this.enabledSampleStreamWrappers = new HlsSampleStreamWrapper[localArrayList.size()];
    localArrayList.toArray(this.enabledSampleStreamWrappers);
    if (this.enabledSampleStreamWrappers.length > 0)
    {
      this.enabledSampleStreamWrappers[0].setIsTimestampMaster(true);
      i = 1;
      while (i < this.enabledSampleStreamWrappers.length)
      {
        this.enabledSampleStreamWrappers[i].setIsTimestampMaster(false);
        i += 1;
      }
    }
    this.sequenceableLoader = new CompositeSequenceableLoader(this.enabledSampleStreamWrappers);
    if ((this.seenFirstTrackSelection) && (j != 0))
    {
      seekToUs(paramLong);
      i = 0;
      while (i < paramArrayOfTrackSelection.length)
      {
        if (paramArrayOfSampleStream[i] != null) {
          paramArrayOfBoolean2[i] = true;
        }
        i += 1;
      }
    }
    this.seenFirstTrackSelection = true;
    return paramLong;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/HlsMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */