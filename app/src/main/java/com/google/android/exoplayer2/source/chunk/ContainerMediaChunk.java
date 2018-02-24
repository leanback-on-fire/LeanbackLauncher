package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.DefaultTrackOutput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

public class ContainerMediaChunk
  extends BaseMediaChunk
  implements ChunkExtractorWrapper.SeekMapOutput
{
  private volatile int bytesLoaded;
  private final int chunkCount;
  private final ChunkExtractorWrapper extractorWrapper;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final Format sampleFormat;
  private final long sampleOffsetUs;
  
  public ContainerMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat1, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, int paramInt3, long paramLong3, ChunkExtractorWrapper paramChunkExtractorWrapper, Format paramFormat2)
  {
    super(paramDataSource, paramDataSpec, paramFormat1, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.chunkCount = paramInt3;
    this.sampleOffsetUs = paramLong3;
    this.extractorWrapper = paramChunkExtractorWrapper;
    this.sampleFormat = paramFormat2;
  }
  
  public final long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public final void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public int getNextChunkIndex()
  {
    return this.chunkIndex + this.chunkCount;
  }
  
  public final boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }
  
  public final void load()
    throws IOException, InterruptedException
  {
    Object localObject1 = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
    for (;;)
    {
      try
      {
        localObject1 = new DefaultExtractorInput(this.dataSource, ((DataSpec)localObject1).absoluteStreamPosition, this.dataSource.open((DataSpec)localObject1));
        Object localObject3;
        if (this.bytesLoaded == 0)
        {
          localObject3 = getTrackOutput();
          ((DefaultTrackOutput)localObject3).formatWithOffset(this.sampleFormat, this.sampleOffsetUs);
          this.extractorWrapper.init(this, (TrackOutput)localObject3);
        }
        int i;
        try
        {
          localObject3 = this.extractorWrapper.extractor;
          i = 0;
          if ((i != 0) || (this.loadCanceled)) {
            break label184;
          }
          i = ((Extractor)localObject3).read((ExtractorInput)localObject1, null);
          continue;
          Assertions.checkState(bool);
          this.bytesLoaded = ((int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition));
          Util.closeQuietly(this.dataSource);
          this.loadCompleted = true;
          return;
        }
        finally
        {
          this.bytesLoaded = ((int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition));
        }
        bool = false;
        continue;
        if (i == 1) {
          continue;
        }
      }
      finally
      {
        Util.closeQuietly(this.dataSource);
      }
      label184:
      boolean bool = true;
    }
  }
  
  public final void seekMap(SeekMap paramSeekMap) {}
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/chunk/ContainerMediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */