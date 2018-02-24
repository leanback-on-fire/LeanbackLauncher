package com.google.android.exoplayer2.source.hls;

import android.util.SparseArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;

public final class TimestampAdjusterProvider
{
  private final SparseArray<TimestampAdjuster> timestampAdjusters = new SparseArray();
  
  public TimestampAdjuster getAdjuster(int paramInt, long paramLong)
  {
    TimestampAdjuster localTimestampAdjuster2 = (TimestampAdjuster)this.timestampAdjusters.get(paramInt);
    TimestampAdjuster localTimestampAdjuster1 = localTimestampAdjuster2;
    if (localTimestampAdjuster2 == null)
    {
      localTimestampAdjuster1 = new TimestampAdjuster(paramLong);
      this.timestampAdjusters.put(paramInt, localTimestampAdjuster1);
    }
    return localTimestampAdjuster1;
  }
  
  public void reset()
  {
    this.timestampAdjusters.clear();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/TimestampAdjusterProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */