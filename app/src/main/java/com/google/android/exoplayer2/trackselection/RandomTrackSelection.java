package com.google.android.exoplayer2.trackselection;

import android.os.SystemClock;
import com.google.android.exoplayer2.source.TrackGroup;
import java.util.Random;

public final class RandomTrackSelection
  extends BaseTrackSelection
{
  private final Random random;
  private int selectedIndex;
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
  {
    super(paramTrackGroup, paramVarArgs);
    this.random = new Random();
    this.selectedIndex = this.random.nextInt(this.length);
  }
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, long paramLong)
  {
    this(paramTrackGroup, paramArrayOfInt, new Random(paramLong));
  }
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, Random paramRandom)
  {
    super(paramTrackGroup, paramArrayOfInt);
    this.random = paramRandom;
    this.selectedIndex = paramRandom.nextInt(this.length);
  }
  
  public int getSelectedIndex()
  {
    return this.selectedIndex;
  }
  
  public Object getSelectionData()
  {
    return null;
  }
  
  public int getSelectionReason()
  {
    return 3;
  }
  
  public void updateSelectedTrack(long paramLong)
  {
    paramLong = SystemClock.elapsedRealtime();
    int j = 0;
    int i = 0;
    int k;
    while (i < this.length)
    {
      k = j;
      if (!isBlacklisted(i, paramLong)) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    this.selectedIndex = this.random.nextInt(j);
    if (j != this.length)
    {
      j = 0;
      i = 0;
    }
    for (;;)
    {
      if (i < this.length)
      {
        k = j;
        if (isBlacklisted(i, paramLong)) {
          break label116;
        }
        if (this.selectedIndex == j) {
          this.selectedIndex = i;
        }
      }
      else
      {
        return;
      }
      k = j + 1;
      label116:
      i += 1;
      j = k;
    }
  }
  
  public static final class Factory
    implements TrackSelection.Factory
  {
    private final Random random;
    
    public Factory()
    {
      this.random = new Random();
    }
    
    public Factory(int paramInt)
    {
      this.random = new Random(paramInt);
    }
    
    public RandomTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
    {
      return new RandomTrackSelection(paramTrackGroup, paramVarArgs, this.random);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/trackselection/RandomTrackSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */