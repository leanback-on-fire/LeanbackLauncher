package com.google.android.exoplayer2.source.smoothstreaming;

import android.util.Base64;
import com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.CompositeSequenceableLoader;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader.Callback;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.ProtectionElement;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import java.io.IOException;
import java.util.ArrayList;

final class SsMediaPeriod
  implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<SsChunkSource>>
{
  private static final int INITIALIZATION_VECTOR_SIZE = 8;
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final SsChunkSource.Factory chunkSourceFactory;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private SsManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private final int minLoadableRetryCount;
  private ChunkSampleStream<SsChunkSource>[] sampleStreams;
  private CompositeSequenceableLoader sequenceableLoader;
  private final TrackEncryptionBox[] trackEncryptionBoxes;
  private final TrackGroupArray trackGroups;
  
  public SsMediaPeriod(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, int paramInt, AdaptiveMediaSourceEventListener.EventDispatcher paramEventDispatcher, LoaderErrorThrower paramLoaderErrorThrower, Allocator paramAllocator)
  {
    this.chunkSourceFactory = paramFactory;
    this.manifestLoaderErrorThrower = paramLoaderErrorThrower;
    this.minLoadableRetryCount = paramInt;
    this.eventDispatcher = paramEventDispatcher;
    this.allocator = paramAllocator;
    this.trackGroups = buildTrackGroups(paramSsManifest);
    paramFactory = paramSsManifest.protectionElement;
    if (paramFactory != null) {}
    for (this.trackEncryptionBoxes = new TrackEncryptionBox[] { new TrackEncryptionBox(true, 8, getProtectionElementKeyId(paramFactory.data)) };; this.trackEncryptionBoxes = null)
    {
      this.manifest = paramSsManifest;
      this.sampleStreams = newSampleStreamArray(0);
      this.sequenceableLoader = new CompositeSequenceableLoader(this.sampleStreams);
      return;
    }
  }
  
  private ChunkSampleStream<SsChunkSource> buildSampleStream(TrackSelection paramTrackSelection, long paramLong)
  {
    int i = this.trackGroups.indexOf(paramTrackSelection.getTrackGroup());
    paramTrackSelection = this.chunkSourceFactory.createChunkSource(this.manifestLoaderErrorThrower, this.manifest, i, paramTrackSelection, this.trackEncryptionBoxes);
    return new ChunkSampleStream(this.manifest.streamElements[i].type, paramTrackSelection, this, this.allocator, paramLong, this.minLoadableRetryCount, this.eventDispatcher);
  }
  
  private static TrackGroupArray buildTrackGroups(SsManifest paramSsManifest)
  {
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[paramSsManifest.streamElements.length];
    int i = 0;
    while (i < paramSsManifest.streamElements.length)
    {
      arrayOfTrackGroup[i] = new TrackGroup(paramSsManifest.streamElements[i].formats);
      i += 1;
    }
    return new TrackGroupArray(arrayOfTrackGroup);
  }
  
  private static byte[] getProtectionElementKeyId(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      localStringBuilder.append((char)paramArrayOfByte[i]);
      i += 2;
    }
    paramArrayOfByte = localStringBuilder.toString();
    paramArrayOfByte = Base64.decode(paramArrayOfByte.substring(paramArrayOfByte.indexOf("<KID>") + 5, paramArrayOfByte.indexOf("</KID>")), 0);
    swap(paramArrayOfByte, 0, 3);
    swap(paramArrayOfByte, 1, 2);
    swap(paramArrayOfByte, 4, 5);
    swap(paramArrayOfByte, 6, 7);
    return paramArrayOfByte;
  }
  
  private static ChunkSampleStream<SsChunkSource>[] newSampleStreamArray(int paramInt)
  {
    return new ChunkSampleStream[paramInt];
  }
  
  private static void swap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramArrayOfByte[paramInt1];
    paramArrayOfByte[paramInt1] = paramArrayOfByte[paramInt2];
    paramArrayOfByte[paramInt2] = i;
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
  
  public void onContinueLoadingRequested(ChunkSampleStream<SsChunkSource> paramChunkSampleStream)
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
  
  public void updateManifest(SsManifest paramSsManifest)
  {
    this.manifest = paramSsManifest;
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int j = arrayOfChunkSampleStream.length;
    int i = 0;
    while (i < j)
    {
      ((SsChunkSource)arrayOfChunkSampleStream[i].getChunkSource()).updateManifest(paramSsManifest);
      i += 1;
    }
    this.callback.onContinueLoadingRequested(this);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/smoothstreaming/SsMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */