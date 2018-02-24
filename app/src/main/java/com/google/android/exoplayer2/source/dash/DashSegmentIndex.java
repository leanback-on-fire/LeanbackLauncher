package com.google.android.exoplayer2.source.dash;

import com.google.android.exoplayer2.source.dash.manifest.RangedUri;

public abstract interface DashSegmentIndex
{
  public static final int INDEX_UNBOUNDED = -1;
  
  public abstract long getDurationUs(int paramInt, long paramLong);
  
  public abstract int getFirstSegmentNum();
  
  public abstract int getLastSegmentNum(long paramLong);
  
  public abstract int getSegmentNum(long paramLong1, long paramLong2);
  
  public abstract RangedUri getSegmentUrl(int paramInt);
  
  public abstract long getTimeUs(int paramInt);
  
  public abstract boolean isExplicit();
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/DashSegmentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */