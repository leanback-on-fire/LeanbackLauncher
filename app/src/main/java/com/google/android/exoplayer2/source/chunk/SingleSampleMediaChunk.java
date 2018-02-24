package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.DefaultTrackOutput;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

public final class SingleSampleMediaChunk
  extends BaseMediaChunk
{
  private volatile int bytesLoaded;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final Format sampleFormat;
  
  public SingleSampleMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat1, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, Format paramFormat2)
  {
    super(paramDataSource, paramDataSpec, paramFormat1, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.sampleFormat = paramFormat2;
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }
  
  public void load()
    throws IOException, InterruptedException
  {
    Object localObject1 = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
    try
    {
      long l2 = this.dataSource.open((DataSpec)localObject1);
      long l1 = l2;
      if (l2 != -1L) {
        l1 = l2 + this.bytesLoaded;
      }
      localObject1 = new DefaultExtractorInput(this.dataSource, this.bytesLoaded, l1);
      DefaultTrackOutput localDefaultTrackOutput = getTrackOutput();
      localDefaultTrackOutput.formatWithOffset(this.sampleFormat, 0L);
      for (int i = 0; i != -1; i = localDefaultTrackOutput.sampleData((ExtractorInput)localObject1, Integer.MAX_VALUE, true)) {
        this.bytesLoaded += i;
      }
      i = this.bytesLoaded;
      localDefaultTrackOutput.sampleMetadata(this.startTimeUs, 1, i, 0, null);
      Util.closeQuietly(this.dataSource);
      this.loadCompleted = true;
      return;
    }
    finally
    {
      Util.closeQuietly(this.dataSource);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/chunk/SingleSampleMediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */