package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;

public abstract interface LoadControl
{
  public abstract Allocator getAllocator();
  
  public abstract void onPrepared();
  
  public abstract void onReleased();
  
  public abstract void onStopped();
  
  public abstract void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray);
  
  public abstract boolean shouldContinueLoading(long paramLong);
  
  public abstract boolean shouldStartPlayback(long paramLong, boolean paramBoolean);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/LoadControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */