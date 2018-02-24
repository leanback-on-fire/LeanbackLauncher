package com.google.android.tvlauncher.instantvideo.preload;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;

public abstract class PreloaderManager
{
  public abstract void bringPreloadedVideoToTopPriority(Uri paramUri);
  
  public abstract int canPlayVideo(Uri paramUri);
  
  public abstract void clearPreloadedData(Uri paramUri);
  
  @Nullable
  public abstract Preloader createPreloader(Uri paramUri);
  
  public abstract MediaPlayer getOrCreatePlayer(Uri paramUri);
  
  public abstract boolean isPreloaded(Uri paramUri);
  
  public abstract void recycleMediaPlayer(MediaPlayer paramMediaPlayer);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/preload/PreloaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */