package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.source.dash.DashSegmentIndex;

final class SingleSegmentIndex
  implements DashSegmentIndex
{
  private final RangedUri uri;
  
  public SingleSegmentIndex(RangedUri paramRangedUri)
  {
    this.uri = paramRangedUri;
  }
  
  public long getDurationUs(int paramInt, long paramLong)
  {
    return paramLong;
  }
  
  public int getFirstSegmentNum()
  {
    return 0;
  }
  
  public int getLastSegmentNum(long paramLong)
  {
    return 0;
  }
  
  public int getSegmentNum(long paramLong1, long paramLong2)
  {
    return 0;
  }
  
  public RangedUri getSegmentUrl(int paramInt)
  {
    return this.uri;
  }
  
  public long getTimeUs(int paramInt)
  {
    return 0L;
  }
  
  public boolean isExplicit()
  {
    return true;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/SingleSegmentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */