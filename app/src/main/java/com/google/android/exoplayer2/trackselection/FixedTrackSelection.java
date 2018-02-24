package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.util.Assertions;

public final class FixedTrackSelection
  extends BaseTrackSelection
{
  private final Object data;
  private final int reason;
  
  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt)
  {
    this(paramTrackGroup, paramInt, 0, null);
  }
  
  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, Object paramObject)
  {
    super(paramTrackGroup, new int[] { paramInt1 });
    this.reason = paramInt2;
    this.data = paramObject;
  }
  
  public int getSelectedIndex()
  {
    return 0;
  }
  
  public Object getSelectionData()
  {
    return this.data;
  }
  
  public int getSelectionReason()
  {
    return this.reason;
  }
  
  public void updateSelectedTrack(long paramLong) {}
  
  public static final class Factory
    implements TrackSelection.Factory
  {
    private final Object data;
    private final int reason;
    
    public Factory()
    {
      this.reason = 0;
      this.data = null;
    }
    
    public Factory(int paramInt, Object paramObject)
    {
      this.reason = paramInt;
      this.data = paramObject;
    }
    
    public FixedTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
    {
      boolean bool = true;
      if (paramVarArgs.length == 1) {}
      for (;;)
      {
        Assertions.checkArgument(bool);
        return new FixedTrackSelection(paramTrackGroup, paramVarArgs[0], this.reason, this.data);
        bool = false;
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/trackselection/FixedTrackSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */