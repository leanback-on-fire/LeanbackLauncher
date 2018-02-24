package com.google.android.tvlauncher.instantvideo.preload;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.LruCache;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;

public class InstantVideoPreloadManager
{
  private static final boolean DEBUG = false;
  private static final int MAX_CONCURRENT_VIDEO_PRELOAD_COUNT = 10;
  private static final String TAG = "InstantVideoPreloadMgr";
  private static InstantVideoPreloadManager sInstance;
  private final Context mAppContext;
  private PreloaderManagerImpl mPreloaderManager;
  private final LruCache<Uri, Preloader> mVideoPreloaderCache = new LruCache(10)
  {
    protected void entryRemoved(boolean paramAnonymousBoolean, Uri paramAnonymousUri, Preloader paramAnonymousPreloader1, Preloader paramAnonymousPreloader2)
    {
      if (paramAnonymousPreloader2 != null) {
        InstantVideoPreloadManager.this.onEntryRemovedFromCache(paramAnonymousUri, paramAnonymousPreloader1);
      }
    }
  };
  
  @VisibleForTesting
  InstantVideoPreloadManager(Context paramContext)
  {
    this.mAppContext = paramContext.getApplicationContext();
    this.mPreloaderManager = new PreloaderManagerImpl();
  }
  
  public static InstantVideoPreloadManager getInstance(Context paramContext)
  {
    try
    {
      if (sInstance == null) {
        sInstance = new InstantVideoPreloadManager(paramContext);
      }
      paramContext = sInstance;
      return paramContext;
    }
    finally {}
  }
  
  private void onEntryRemovedFromCache(Uri paramUri, Preloader paramPreloader)
  {
    paramPreloader.stopPreload();
  }
  
  private Preloader startPreloading(final Uri paramUri)
  {
    Preloader localPreloader = this.mPreloaderManager.createPreloader(paramUri);
    if (localPreloader == null) {
      return null;
    }
    localPreloader.startPreload(new Preloader.OnPreloadFinishedListener()
    {
      public void onPreloadFinishedListener()
      {
        InstantVideoPreloadManager.this.mVideoPreloaderCache.remove(paramUri);
      }
    });
    return localPreloader;
  }
  
  public void clearCache() {}
  
  public MediaPlayer getOrCreatePlayer(Uri paramUri)
  {
    return this.mPreloaderManager.getOrCreatePlayer(paramUri);
  }
  
  public void preload(@NonNull Uri paramUri)
  {
    if (paramUri == null) {
      throw new IllegalArgumentException("The video URI shouldn't be null.");
    }
    if (this.mPreloaderManager.isPreloaded(paramUri)) {
      this.mPreloaderManager.bringPreloadedVideoToTopPriority(paramUri);
    }
    Preloader localPreloader;
    do
    {
      return;
      localPreloader = (Preloader)this.mVideoPreloaderCache.get(paramUri);
      if (localPreloader != null) {
        break;
      }
      localPreloader = startPreloading(paramUri);
    } while (localPreloader == null);
    this.mVideoPreloaderCache.put(paramUri, localPreloader);
    return;
    this.mVideoPreloaderCache.put(paramUri, localPreloader);
    this.mPreloaderManager.bringPreloadedVideoToTopPriority(paramUri);
  }
  
  public void recyclePlayer(MediaPlayer paramMediaPlayer, Uri paramUri)
  {
    this.mPreloaderManager.recycleMediaPlayer(paramMediaPlayer);
  }
  
  public void registerPreloaderManager(PreloaderManager paramPreloaderManager)
  {
    this.mPreloaderManager.registerPreloaderManager(paramPreloaderManager);
  }
  
  public void unregisterPreloaderManager(PreloaderManager paramPreloaderManager)
  {
    this.mPreloaderManager.unregisterPreloaderManager(paramPreloaderManager);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/preload/InstantVideoPreloadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */