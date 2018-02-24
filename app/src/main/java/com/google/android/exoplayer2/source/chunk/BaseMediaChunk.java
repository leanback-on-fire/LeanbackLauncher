package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultTrackOutput;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

public abstract class BaseMediaChunk
  extends MediaChunk
{
  private int firstSampleIndex;
  private DefaultTrackOutput trackOutput;
  
  public BaseMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2)
  {
    super(paramDataSource, paramDataSpec, paramFormat, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
  }
  
  public final int getFirstSampleIndex()
  {
    return this.firstSampleIndex;
  }
  
  protected final DefaultTrackOutput getTrackOutput()
  {
    return this.trackOutput;
  }
  
  public void init(DefaultTrackOutput paramDefaultTrackOutput)
  {
    this.trackOutput = paramDefaultTrackOutput;
    this.firstSampleIndex = paramDefaultTrackOutput.getWriteIndex();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/chunk/BaseMediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */