package com.google.android.exoplayer2.source.dash;

import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;

final class DashWrappingSegmentIndex
  implements DashSegmentIndex
{
  private final ChunkIndex chunkIndex;
  
  public DashWrappingSegmentIndex(ChunkIndex paramChunkIndex)
  {
    this.chunkIndex = paramChunkIndex;
  }
  
  public long getDurationUs(int paramInt, long paramLong)
  {
    return this.chunkIndex.durationsUs[paramInt];
  }
  
  public int getFirstSegmentNum()
  {
    return 0;
  }
  
  public int getLastSegmentNum(long paramLong)
  {
    return this.chunkIndex.length - 1;
  }
  
  public int getSegmentNum(long paramLong1, long paramLong2)
  {
    return this.chunkIndex.getChunkIndex(paramLong1);
  }
  
  public RangedUri getSegmentUrl(int paramInt)
  {
    return new RangedUri(null, this.chunkIndex.offsets[paramInt], this.chunkIndex.sizes[paramInt]);
  }
  
  public long getTimeUs(int paramInt)
  {
    return this.chunkIndex.timesUs[paramInt];
  }
  
  public boolean isExplicit()
  {
    return true;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/DashWrappingSegmentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */