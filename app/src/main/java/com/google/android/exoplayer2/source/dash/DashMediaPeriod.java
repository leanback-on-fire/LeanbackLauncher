package com.google.android.exoplayer2.source.dash;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.CompositeSequenceableLoader;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class DashMediaPeriod
  implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<DashChunkSource>>
{
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final DashChunkSource.Factory chunkSourceFactory;
  private final long elapsedRealtimeOffset;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  final int id;
  private int index;
  private DashManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private final int minLoadableRetryCount;
  private Period period;
  private ChunkSampleStream<DashChunkSource>[] sampleStreams;
  private CompositeSequenceableLoader sequenceableLoader;
  private final TrackGroupArray trackGroups;
  
  public DashMediaPeriod(int paramInt1, DashManifest paramDashManifest, int paramInt2, DashChunkSource.Factory paramFactory, int paramInt3, AdaptiveMediaSourceEventListener.EventDispatcher paramEventDispatcher, long paramLong, LoaderErrorThrower paramLoaderErrorThrower, Allocator paramAllocator)
  {
    this.id = paramInt1;
    this.manifest = paramDashManifest;
    this.index = paramInt2;
    this.chunkSourceFactory = paramFactory;
    this.minLoadableRetryCount = paramInt3;
    this.eventDispatcher = paramEventDispatcher;
    this.elapsedRealtimeOffset = paramLong;
    this.manifestLoaderErrorThrower = paramLoaderErrorThrower;
    this.allocator = paramAllocator;
    this.sampleStreams = newSampleStreamArray(0);
    this.sequenceableLoader = new CompositeSequenceableLoader(this.sampleStreams);
    this.period = paramDashManifest.getPeriod(paramInt2);
    this.trackGroups = buildTrackGroups(this.period);
  }
  
  private ChunkSampleStream<DashChunkSource> buildSampleStream(TrackSelection paramTrackSelection, long paramLong)
  {
    int i = this.trackGroups.indexOf(paramTrackSelection.getTrackGroup());
    AdaptationSet localAdaptationSet = (AdaptationSet)this.period.adaptationSets.get(i);
    paramTrackSelection = this.chunkSourceFactory.createDashChunkSource(this.manifestLoaderErrorThrower, this.manifest, this.index, i, paramTrackSelection, this.elapsedRealtimeOffset);
    return new ChunkSampleStream(localAdaptationSet.type, paramTrackSelection, this, this.allocator, paramLong, this.minLoadableRetryCount, this.eventDispatcher);
  }
  
  private static TrackGroupArray buildTrackGroups(Period paramPeriod)
  {
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[paramPeriod.adaptationSets.size()];
    int i = 0;
    while (i < paramPeriod.adaptationSets.size())
    {
      List localList = ((AdaptationSet)paramPeriod.adaptationSets.get(i)).representations;
      Format[] arrayOfFormat = new Format[localList.size()];
      int j = 0;
      while (j < arrayOfFormat.length)
      {
        arrayOfFormat[j] = ((Representation)localList.get(j)).format;
        j += 1;
      }
      arrayOfTrackGroup[i] = new TrackGroup(arrayOfFormat);
      i += 1;
    }
    return new TrackGroupArray(arrayOfTrackGroup);
  }
  
  private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int paramInt)
  {
    return new ChunkSampleStream[paramInt];
  }
  
  public boolean continueLoading(long paramLong)
  {
    return this.sequenceableLoader.continueLoading(paramLong);
  }
  
  public long getBufferedPositionUs()
  {
    long l1 = Long.MAX_VALUE;
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      long l3 = arrayOfChunkSampleStream[i].getBufferedPositionUs();
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
    this.manifestLoaderErrorThrower.maybeThrowError();
  }
  
  public void onContinueLoadingRequested(ChunkSampleStream<DashChunkSource> paramChunkSampleStream)
  {
    this.callback.onContinueLoadingRequested(this);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback)
  {
    this.callback = paramCallback;
    paramCallback.onPrepared(this);
  }
  
  public long readDiscontinuity()
  {
    return -9223372036854775807L;
  }
  
  public void release()
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      arrayOfChunkSampleStream[i].release();
      i += 1;
    }
  }
  
  public long seekToUs(long paramLong)
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      arrayOfChunkSampleStream[i].seekToUs(paramLong);
      i += 1;
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i < paramArrayOfTrackSelection.length)
    {
      ChunkSampleStream localChunkSampleStream;
      if (paramArrayOfSampleStream[i] != null)
      {
        localChunkSampleStream = (ChunkSampleStream)paramArrayOfSampleStream[i];
        if ((paramArrayOfTrackSelection[i] != null) && (paramArrayOfBoolean1[i] != 0)) {
          break label114;
        }
        localChunkSampleStream.release();
        paramArrayOfSampleStream[i] = null;
      }
      for (;;)
      {
        if ((paramArrayOfSampleStream[i] == null) && (paramArrayOfTrackSelection[i] != null))
        {
          localChunkSampleStream = buildSampleStream(paramArrayOfTrackSelection[i], paramLong);
          localArrayList.add(localChunkSampleStream);
          paramArrayOfSampleStream[i] = localChunkSampleStream;
          paramArrayOfBoolean2[i] = true;
        }
        i += 1;
        break;
        label114:
        localArrayList.add(localChunkSampleStream);
      }
    }
    this.sampleStreams = newSampleStreamArray(localArrayList.size());
    localArrayList.toArray(this.sampleStreams);
    this.sequenceableLoader = new CompositeSequenceableLoader(this.sampleStreams);
    return paramLong;
  }
  
  public void updateManifest(DashManifest paramDashManifest, int paramInt)
  {
    this.manifest = paramDashManifest;
    this.index = paramInt;
    this.period = paramDashManifest.getPeriod(paramInt);
    if (this.sampleStreams != null)
    {
      ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
      int j = arrayOfChunkSampleStream.length;
      int i = 0;
      while (i < j)
      {
        ((DashChunkSource)arrayOfChunkSampleStream[i].getChunkSource()).updateManifest(paramDashManifest, paramInt);
        i += 1;
      }
      this.callback.onContinueLoadingRequested(this);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/DashMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */