package com.google.android.tvlauncher.instantvideo.preload;

import android.net.Uri;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class PreloaderManagerImpl
  extends PreloaderManager
{
  private List<PreloaderManager> mPreloaderManagers = new ArrayList();
  
  private PreloaderManager choosePreloaderManager(Uri paramUri)
  {
    int i = 0;
    Object localObject = null;
    Iterator localIterator = this.mPreloaderManagers.iterator();
    while (localIterator.hasNext())
    {
      PreloaderManager localPreloaderManager = (PreloaderManager)localIterator.next();
      int j = localPreloaderManager.canPlayVideo(paramUri);
      if (i < j)
      {
        i = j;
        localObject = localPreloaderManager;
      }
    }
    return (PreloaderManager)localObject;
  }
  
  public void bringPreloadedVideoToTopPriority(Uri paramUri)
  {
    PreloaderManager localPreloaderManager = choosePreloaderManager(paramUri);
    if (localPreloaderManager != null) {
      localPreloaderManager.bringPreloadedVideoToTopPriority(paramUri);
    }
  }
  
  public int canPlayVideo(Uri paramUri)
  {
    int i = 0;
    Iterator localIterator = this.mPreloaderManagers.iterator();
    while (localIterator.hasNext())
    {
      int j = ((PreloaderManager)localIterator.next()).canPlayVideo(paramUri);
      if (i < j) {
        i = j;
      }
    }
    return i;
  }
  
  public void clearPreloadedData(Uri paramUri)
  {
    PreloaderManager localPreloaderManager = choosePreloaderManager(paramUri);
    if (localPreloaderManager != null) {
      localPreloaderManager.clearPreloadedData(paramUri);
    }
  }
  
  public Preloader createPreloader(Uri paramUri)
  {
    PreloaderManager localPreloaderManager = choosePreloaderManager(paramUri);
    if (localPreloaderManager != null) {
      return localPreloaderManager.createPreloader(paramUri);
    }
    return null;
  }
  
  public MediaPlayer getOrCreatePlayer(Uri paramUri)
  {
    PreloaderManager localPreloaderManager = choosePreloaderManager(paramUri);
    if (localPreloaderManager != null) {
      return localPreloaderManager.getOrCreatePlayer(paramUri);
    }
    return null;
  }
  
  public boolean isPreloaded(Uri paramUri)
  {
    PreloaderManager localPreloaderManager = choosePreloaderManager(paramUri);
    return (localPreloaderManager != null) && (localPreloaderManager.isPreloaded(paramUri));
  }
  
  public void recycleMediaPlayer(MediaPlayer paramMediaPlayer)
  {
    PreloaderManager localPreloaderManager = choosePreloaderManager(paramMediaPlayer.getVideoUri());
    if (localPreloaderManager != null) {
      localPreloaderManager.recycleMediaPlayer(paramMediaPlayer);
    }
  }
  
  void registerPreloaderManager(PreloaderManager paramPreloaderManager)
  {
    this.mPreloaderManagers.add(paramPreloaderManager);
  }
  
  void unregisterPreloaderManager(PreloaderManager paramPreloaderManager)
  {
    if (paramPreloaderManager == null)
    {
      this.mPreloaderManagers.clear();
      return;
    }
    this.mPreloaderManagers.remove(paramPreloaderManager);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/preload/PreloaderManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */