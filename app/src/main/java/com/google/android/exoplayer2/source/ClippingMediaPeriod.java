package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class ClippingMediaPeriod
  implements MediaPeriod, MediaPeriod.Callback
{
  private MediaPeriod.Callback callback;
  private long endUs;
  public final MediaPeriod mediaPeriod;
  private boolean pendingInitialDiscontinuity;
  private ClippingSampleStream[] sampleStreams;
  private long startUs;
  
  public ClippingMediaPeriod(MediaPeriod paramMediaPeriod)
  {
    this.mediaPeriod = paramMediaPeriod;
    this.startUs = -9223372036854775807L;
    this.endUs = -9223372036854775807L;
    this.sampleStreams = new ClippingSampleStream[0];
  }
  
  public boolean continueLoading(long paramLong)
  {
    return this.mediaPeriod.continueLoading(this.startUs + paramLong);
  }
  
  public long getBufferedPositionUs()
  {
    long l = this.mediaPeriod.getBufferedPositionUs();
    if ((l == Long.MIN_VALUE) || ((this.endUs != Long.MIN_VALUE) && (l >= this.endUs))) {
      return Long.MIN_VALUE;
    }
    return Math.max(0L, l - this.startUs);
  }
  
  public long getNextLoadPositionUs()
  {
    long l = this.mediaPeriod.getNextLoadPositionUs();
    if ((l == Long.MIN_VALUE) || ((this.endUs != Long.MIN_VALUE) && (l >= this.endUs))) {
      return Long.MIN_VALUE;
    }
    return l - this.startUs;
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.mediaPeriod.getTrackGroups();
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    this.mediaPeriod.maybeThrowPrepareError();
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    this.callback.onContinueLoadingRequested(this);
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    boolean bool2 = true;
    if ((this.startUs != -9223372036854775807L) && (this.endUs != -9223372036854775807L))
    {
      bool1 = true;
      Assertions.checkState(bool1);
      if (this.startUs == 0L) {
        break label62;
      }
    }
    label62:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.pendingInitialDiscontinuity = bool1;
      this.callback.onPrepared(this);
      return;
      bool1 = false;
      break;
    }
  }
  
  public void prepare(MediaPeriod.Callback paramCallback)
  {
    this.callback = paramCallback;
    this.mediaPeriod.prepare(this);
  }
  
  public long readDiscontinuity()
  {
    boolean bool2 = false;
    if (this.pendingInitialDiscontinuity)
    {
      ClippingSampleStream[] arrayOfClippingSampleStream = this.sampleStreams;
      int j = arrayOfClippingSampleStream.length;
      int i = 0;
      while (i < j)
      {
        ClippingSampleStream localClippingSampleStream = arrayOfClippingSampleStream[i];
        if (localClippingSampleStream != null) {
          localClippingSampleStream.clearPendingDiscontinuity();
        }
        i += 1;
      }
      this.pendingInitialDiscontinuity = false;
      l = readDiscontinuity();
      if (l != -9223372036854775807L) {
        return l;
      }
      return 0L;
    }
    long l = this.mediaPeriod.readDiscontinuity();
    if (l == -9223372036854775807L) {
      return -9223372036854775807L;
    }
    if (l >= this.startUs) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      Assertions.checkState(bool1);
      if (this.endUs != Long.MIN_VALUE)
      {
        bool1 = bool2;
        if (l > this.endUs) {}
      }
      else
      {
        bool1 = true;
      }
      Assertions.checkState(bool1);
      return l - this.startUs;
    }
  }
  
  public long seekToUs(long paramLong)
  {
    boolean bool2 = false;
    ClippingSampleStream[] arrayOfClippingSampleStream = this.sampleStreams;
    int j = arrayOfClippingSampleStream.length;
    int i = 0;
    while (i < j)
    {
      ClippingSampleStream localClippingSampleStream = arrayOfClippingSampleStream[i];
      if (localClippingSampleStream != null) {
        localClippingSampleStream.clearSentEos();
      }
      i += 1;
    }
    long l = this.mediaPeriod.seekToUs(this.startUs + paramLong);
    if (l != this.startUs + paramLong)
    {
      bool1 = bool2;
      if (l < this.startUs) {
        break label116;
      }
      if (this.endUs != Long.MIN_VALUE)
      {
        bool1 = bool2;
        if (l > this.endUs) {
          break label116;
        }
      }
    }
    boolean bool1 = true;
    label116:
    Assertions.checkState(bool1);
    return l - this.startUs;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    this.sampleStreams = new ClippingSampleStream[paramArrayOfSampleStream.length];
    SampleStream[] arrayOfSampleStream = new SampleStream[paramArrayOfSampleStream.length];
    int i = 0;
    if (i < paramArrayOfSampleStream.length)
    {
      this.sampleStreams[i] = ((ClippingSampleStream)paramArrayOfSampleStream[i]);
      if (this.sampleStreams[i] != null) {}
      for (SampleStream localSampleStream = this.sampleStreams[i].stream;; localSampleStream = null)
      {
        arrayOfSampleStream[i] = localSampleStream;
        i += 1;
        break;
      }
    }
    long l = this.mediaPeriod.selectTracks(paramArrayOfTrackSelection, paramArrayOfBoolean1, arrayOfSampleStream, paramArrayOfBoolean2, paramLong + this.startUs);
    boolean bool;
    if ((l == this.startUs + paramLong) || ((l >= this.startUs) && ((this.endUs == Long.MIN_VALUE) || (l <= this.endUs))))
    {
      bool = true;
      Assertions.checkState(bool);
      i = 0;
      label163:
      if (i >= paramArrayOfSampleStream.length) {
        break label272;
      }
      if (arrayOfSampleStream[i] != null) {
        break label212;
      }
      this.sampleStreams[i] = null;
    }
    for (;;)
    {
      paramArrayOfSampleStream[i] = this.sampleStreams[i];
      i += 1;
      break label163;
      bool = false;
      break;
      label212:
      if ((paramArrayOfSampleStream[i] == null) || (this.sampleStreams[i].stream != arrayOfSampleStream[i])) {
        this.sampleStreams[i] = new ClippingSampleStream(this, arrayOfSampleStream[i], this.startUs, this.endUs, this.pendingInitialDiscontinuity);
      }
    }
    label272:
    return l - this.startUs;
  }
  
  public void setClipping(long paramLong1, long paramLong2)
  {
    this.startUs = paramLong1;
    this.endUs = paramLong2;
  }
  
  private static final class ClippingSampleStream
    implements SampleStream
  {
    private final long endUs;
    private final MediaPeriod mediaPeriod;
    private boolean pendingDiscontinuity;
    private boolean sentEos;
    private final long startUs;
    private final SampleStream stream;
    
    public ClippingSampleStream(MediaPeriod paramMediaPeriod, SampleStream paramSampleStream, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      this.mediaPeriod = paramMediaPeriod;
      this.stream = paramSampleStream;
      this.startUs = paramLong1;
      this.endUs = paramLong2;
      this.pendingDiscontinuity = paramBoolean;
    }
    
    public void clearPendingDiscontinuity()
    {
      this.pendingDiscontinuity = false;
    }
    
    public void clearSentEos()
    {
      this.sentEos = false;
    }
    
    public boolean isReady()
    {
      return this.stream.isReady();
    }
    
    public void maybeThrowError()
      throws IOException
    {
      this.stream.maybeThrowError();
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
    {
      int i;
      if (this.pendingDiscontinuity) {
        i = -3;
      }
      int j;
      do
      {
        do
        {
          return i;
          if (paramDecoderInputBuffer == null) {
            return this.stream.readData(paramFormatHolder, null);
          }
          if (this.sentEos)
          {
            paramDecoderInputBuffer.setFlags(4);
            return -4;
          }
          j = this.stream.readData(paramFormatHolder, paramDecoderInputBuffer);
          if ((this.endUs != Long.MIN_VALUE) && (((j == -4) && (paramDecoderInputBuffer.timeUs >= this.endUs)) || ((j == -3) && (this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE))))
          {
            paramDecoderInputBuffer.clear();
            paramDecoderInputBuffer.setFlags(4);
            this.sentEos = true;
            return -4;
          }
          i = j;
        } while (j != -4);
        i = j;
      } while (paramDecoderInputBuffer.isEndOfStream());
      paramDecoderInputBuffer.timeUs -= this.startUs;
      return j;
    }
    
    public void skipToKeyframeBefore(long paramLong)
    {
      this.stream.skipToKeyframeBefore(this.startUs + paramLong);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/ClippingMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */