package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public abstract interface ExoPlayer
{
  public static final int STATE_BUFFERING = 2;
  public static final int STATE_ENDED = 4;
  public static final int STATE_IDLE = 1;
  public static final int STATE_READY = 3;
  
  public abstract void addListener(EventListener paramEventListener);
  
  public abstract void blockingSendMessages(ExoPlayerMessage... paramVarArgs);
  
  public abstract int getBufferedPercentage();
  
  public abstract long getBufferedPosition();
  
  public abstract Object getCurrentManifest();
  
  public abstract int getCurrentPeriodIndex();
  
  public abstract long getCurrentPosition();
  
  public abstract Timeline getCurrentTimeline();
  
  public abstract TrackGroupArray getCurrentTrackGroups();
  
  public abstract TrackSelectionArray getCurrentTrackSelections();
  
  public abstract int getCurrentWindowIndex();
  
  public abstract long getDuration();
  
  public abstract boolean getPlayWhenReady();
  
  public abstract int getPlaybackState();
  
  public abstract int getRendererCount();
  
  public abstract int getRendererType(int paramInt);
  
  public abstract boolean isCurrentWindowDynamic();
  
  public abstract boolean isCurrentWindowSeekable();
  
  public abstract boolean isLoading();
  
  public abstract void prepare(MediaSource paramMediaSource);
  
  public abstract void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void release();
  
  public abstract void removeListener(EventListener paramEventListener);
  
  public abstract void seekTo(int paramInt, long paramLong);
  
  public abstract void seekTo(long paramLong);
  
  public abstract void seekToDefaultPosition();
  
  public abstract void seekToDefaultPosition(int paramInt);
  
  public abstract void sendMessages(ExoPlayerMessage... paramVarArgs);
  
  public abstract void setPlayWhenReady(boolean paramBoolean);
  
  public abstract void stop();
  
  public static abstract interface EventListener
  {
    public abstract void onLoadingChanged(boolean paramBoolean);
    
    public abstract void onPlayerError(ExoPlaybackException paramExoPlaybackException);
    
    public abstract void onPlayerStateChanged(boolean paramBoolean, int paramInt);
    
    public abstract void onPositionDiscontinuity();
    
    public abstract void onTimelineChanged(Timeline paramTimeline, Object paramObject);
    
    public abstract void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray);
  }
  
  public static abstract interface ExoPlayerComponent
  {
    public abstract void handleMessage(int paramInt, Object paramObject)
      throws ExoPlaybackException;
  }
  
  public static final class ExoPlayerMessage
  {
    public final Object message;
    public final int messageType;
    public final ExoPlayer.ExoPlayerComponent target;
    
    public ExoPlayerMessage(ExoPlayer.ExoPlayerComponent paramExoPlayerComponent, int paramInt, Object paramObject)
    {
      this.target = paramExoPlayerComponent;
      this.messageType = paramInt;
      this.message = paramObject;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ExoPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */