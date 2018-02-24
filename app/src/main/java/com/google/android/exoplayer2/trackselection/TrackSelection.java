package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import java.util.List;

public abstract interface TrackSelection
{
  public abstract boolean blacklist(int paramInt, long paramLong);
  
  public abstract int evaluateQueueSize(long paramLong, List<? extends MediaChunk> paramList);
  
  public abstract Format getFormat(int paramInt);
  
  public abstract int getIndexInTrackGroup(int paramInt);
  
  public abstract Format getSelectedFormat();
  
  public abstract int getSelectedIndex();
  
  public abstract int getSelectedIndexInTrackGroup();
  
  public abstract Object getSelectionData();
  
  public abstract int getSelectionReason();
  
  public abstract TrackGroup getTrackGroup();
  
  public abstract int indexOf(int paramInt);
  
  public abstract int indexOf(Format paramFormat);
  
  public abstract int length();
  
  public abstract void updateSelectedTrack(long paramLong);
  
  public static abstract interface Factory
  {
    public abstract TrackSelection createTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/trackselection/TrackSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */