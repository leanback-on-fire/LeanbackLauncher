package com.google.android.tvlauncher.instantvideo.media;

import android.net.Uri;
import android.view.View;

public abstract interface MediaPlayer
{
  public static final int STATE_BUFFERING = 2;
  public static final int STATE_ENDED = 4;
  public static final int STATE_IDLE = 1;
  public static final int STATE_READY = 3;
  
  public abstract int getCurrentPosition();
  
  public abstract int getPlaybackState();
  
  public abstract View getPlayerView();
  
  public abstract Uri getVideoUri();
  
  public abstract void prepare();
  
  public abstract void seekTo(int paramInt);
  
  public abstract void setDisplaySize(int paramInt1, int paramInt2);
  
  public abstract void setPlayWhenReady(boolean paramBoolean);
  
  public abstract void setVideoCallback(VideoCallback paramVideoCallback);
  
  public abstract void setVideoUri(Uri paramUri);
  
  public abstract void setVolume(float paramFloat);
  
  public abstract void stop();
  
  public static abstract interface VideoCallback
  {
    public abstract void onVideoAvailable();
    
    public abstract void onVideoEnded();
    
    public abstract void onVideoError();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/media/MediaPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */