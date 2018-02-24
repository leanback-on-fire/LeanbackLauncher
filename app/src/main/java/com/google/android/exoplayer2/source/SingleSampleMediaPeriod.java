package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.Loadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

final class SingleSampleMediaPeriod
  implements MediaPeriod, Loader.Callback<SourceLoadable>
{
  private static final int INITIAL_SAMPLE_SIZE = 1024;
  private final DataSource.Factory dataSourceFactory;
  private final Handler eventHandler;
  private final SingleSampleMediaSource.EventListener eventListener;
  private final int eventSourceId;
  final Format format;
  final Loader loader;
  boolean loadingFinished;
  private final int minLoadableRetryCount;
  byte[] sampleData;
  int sampleSize;
  private final ArrayList<SampleStreamImpl> sampleStreams;
  private final TrackGroupArray tracks;
  private final Uri uri;
  
  public SingleSampleMediaPeriod(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, int paramInt1, Handler paramHandler, SingleSampleMediaSource.EventListener paramEventListener, int paramInt2)
  {
    this.uri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.format = paramFormat;
    this.minLoadableRetryCount = paramInt1;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.eventSourceId = paramInt2;
    this.tracks = new TrackGroupArray(new TrackGroup[] { new TrackGroup(new Format[] { paramFormat }) });
    this.sampleStreams = new ArrayList();
    this.loader = new Loader("Loader:SingleSampleMediaPeriod");
  }
  
  private void notifyLoadError(final IOException paramIOException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          SingleSampleMediaPeriod.this.eventListener.onLoadError(SingleSampleMediaPeriod.this.eventSourceId, paramIOException);
        }
      });
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    if ((this.loadingFinished) || (this.loader.isLoading())) {
      return false;
    }
    this.loader.startLoading(new SourceLoadable(this.uri, this.dataSourceFactory.createDataSource()), this, this.minLoadableRetryCount);
    return true;
  }
  
  public long getBufferedPositionUs()
  {
    if (this.loadingFinished) {
      return Long.MIN_VALUE;
    }
    return 0L;
  }
  
  public long getNextLoadPositionUs()
  {
    if ((this.loadingFinished) || (this.loader.isLoading())) {
      return Long.MIN_VALUE;
    }
    return 0L;
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.tracks;
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    this.loader.maybeThrowError();
  }
  
  public void onLoadCanceled(SourceLoadable paramSourceLoadable, long paramLong1, long paramLong2, boolean paramBoolean) {}
  
  public void onLoadCompleted(SourceLoadable paramSourceLoadable, long paramLong1, long paramLong2)
  {
    this.sampleSize = paramSourceLoadable.sampleSize;
    this.sampleData = paramSourceLoadable.sampleData;
    this.loadingFinished = true;
  }
  
  public int onLoadError(SourceLoadable paramSourceLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    notifyLoadError(paramIOException);
    return 0;
  }
  
  public void prepare(MediaPeriod.Callback paramCallback)
  {
    paramCallback.onPrepared(this);
  }
  
  public long readDiscontinuity()
  {
    return -9223372036854775807L;
  }
  
  public void release()
  {
    this.loader.release();
  }
  
  public long seekToUs(long paramLong)
  {
    int i = 0;
    while (i < this.sampleStreams.size())
    {
      ((SampleStreamImpl)this.sampleStreams.get(i)).seekToUs(paramLong);
      i += 1;
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfSampleStream[i] != null) && ((paramArrayOfTrackSelection[i] == null) || (paramArrayOfBoolean1[i] == 0)))
      {
        this.sampleStreams.remove(paramArrayOfSampleStream[i]);
        paramArrayOfSampleStream[i] = null;
      }
      if ((paramArrayOfSampleStream[i] == null) && (paramArrayOfTrackSelection[i] != null))
      {
        SampleStreamImpl localSampleStreamImpl = new SampleStreamImpl(null);
        this.sampleStreams.add(localSampleStreamImpl);
        paramArrayOfSampleStream[i] = localSampleStreamImpl;
        paramArrayOfBoolean2[i] = true;
      }
      i += 1;
    }
    return paramLong;
  }
  
  private final class SampleStreamImpl
    implements SampleStream
  {
    private static final int STREAM_STATE_END_OF_STREAM = 2;
    private static final int STREAM_STATE_SEND_FORMAT = 0;
    private static final int STREAM_STATE_SEND_SAMPLE = 1;
    private int streamState;
    
    private SampleStreamImpl() {}
    
    public boolean isReady()
    {
      return SingleSampleMediaPeriod.this.loadingFinished;
    }
    
    public void maybeThrowError()
      throws IOException
    {
      SingleSampleMediaPeriod.this.loader.maybeThrowError();
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
    {
      if ((paramDecoderInputBuffer == null) || (this.streamState == 0))
      {
        paramFormatHolder.format = SingleSampleMediaPeriod.this.format;
        this.streamState = 1;
        return -5;
      }
      if (this.streamState == 2)
      {
        paramDecoderInputBuffer.addFlag(4);
        return -4;
      }
      if (this.streamState == 1) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        if (SingleSampleMediaPeriod.this.loadingFinished) {
          break;
        }
        return -3;
      }
      paramDecoderInputBuffer.timeUs = 0L;
      paramDecoderInputBuffer.addFlag(1);
      paramDecoderInputBuffer.ensureSpaceForWrite(SingleSampleMediaPeriod.this.sampleSize);
      paramDecoderInputBuffer.data.put(SingleSampleMediaPeriod.this.sampleData, 0, SingleSampleMediaPeriod.this.sampleSize);
      this.streamState = 2;
      return -4;
    }
    
    public void seekToUs(long paramLong)
    {
      if (this.streamState == 2) {
        this.streamState = 1;
      }
    }
    
    public void skipToKeyframeBefore(long paramLong) {}
  }
  
  static final class SourceLoadable
    implements Loader.Loadable
  {
    private final DataSource dataSource;
    private byte[] sampleData;
    private int sampleSize;
    private final Uri uri;
    
    public SourceLoadable(Uri paramUri, DataSource paramDataSource)
    {
      this.uri = paramUri;
      this.dataSource = paramDataSource;
    }
    
    public void cancelLoad() {}
    
    public boolean isLoadCanceled()
    {
      return false;
    }
    
    public void load()
      throws IOException, InterruptedException
    {
      this.sampleSize = 0;
      try
      {
        this.dataSource.open(new DataSpec(this.uri));
        int i = 0;
        if (i != -1)
        {
          this.sampleSize += i;
          if (this.sampleData == null) {
            this.sampleData = new byte['Ѐ'];
          }
          for (;;)
          {
            i = this.dataSource.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
            break;
            if (this.sampleSize == this.sampleData.length) {
              this.sampleData = Arrays.copyOf(this.sampleData, this.sampleData.length * 2);
            }
          }
        }
      }
      finally
      {
        Util.closeQuietly(this.dataSource);
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/SingleSampleMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */