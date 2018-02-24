package com.google.android.exoplayer2.source.hls;

import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.SampleStream;
import java.io.IOException;

final class HlsSampleStream
  implements SampleStream
{
  public final int group;
  private final HlsSampleStreamWrapper sampleStreamWrapper;
  
  public HlsSampleStream(HlsSampleStreamWrapper paramHlsSampleStreamWrapper, int paramInt)
  {
    this.sampleStreamWrapper = paramHlsSampleStreamWrapper;
    this.group = paramInt;
  }
  
  public boolean isReady()
  {
    return this.sampleStreamWrapper.isReady(this.group);
  }
  
  public void maybeThrowError()
    throws IOException
  {
    this.sampleStreamWrapper.maybeThrowError();
  }
  
  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    return this.sampleStreamWrapper.readData(this.group, paramFormatHolder, paramDecoderInputBuffer);
  }
  
  public void skipToKeyframeBefore(long paramLong)
  {
    this.sampleStreamWrapper.skipToKeyframeBefore(this.group, paramLong);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/HlsSampleStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */