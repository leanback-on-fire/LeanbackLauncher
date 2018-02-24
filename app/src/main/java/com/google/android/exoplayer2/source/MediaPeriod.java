package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.trackselection.TrackSelection;
import java.io.IOException;

public abstract interface MediaPeriod
  extends SequenceableLoader
{
  public abstract boolean continueLoading(long paramLong);
  
  public abstract long getBufferedPositionUs();
  
  public abstract long getNextLoadPositionUs();
  
  public abstract TrackGroupArray getTrackGroups();
  
  public abstract void maybeThrowPrepareError()
    throws IOException;
  
  public abstract void prepare(Callback paramCallback);
  
  public abstract long readDiscontinuity();
  
  public abstract long seekToUs(long paramLong);
  
  public abstract long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong);
  
  public static abstract interface Callback
    extends SequenceableLoader.Callback<MediaPeriod>
  {
    public abstract void onPrepared(MediaPeriod paramMediaPeriod);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/MediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */