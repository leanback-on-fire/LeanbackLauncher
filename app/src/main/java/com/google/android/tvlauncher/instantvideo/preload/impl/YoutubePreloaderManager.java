package com.google.android.tvlauncher.instantvideo.preload.impl;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.impl.YoutubePlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.Preloader;
import com.google.android.tvlauncher.instantvideo.preload.PreloaderManager;
import java.util.ArrayList;
import java.util.Iterator;

public class YoutubePreloaderManager
  extends PreloaderManager
{
  private static final boolean DEBUG = false;
  private static final String TAG = "YoutubePreloaderManager";
  private static final int YOUTUBE_PLAYER_CACHE_SIZE = 2;
  private Context mContext;
  private final YoutubePlayerLruCache mYoutubePlayerCache = new YoutubePlayerLruCache(2, null);
  
  public YoutubePreloaderManager(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  public void bringPreloadedVideoToTopPriority(Uri paramUri) {}
  
  public int canPlayVideo(Uri paramUri)
  {
    if (YoutubePlayerImpl.isYoutubeUri(paramUri)) {
      return 100;
    }
    return 0;
  }
  
  public void clearPreloadedData(Uri paramUri) {}
  
  public Preloader createPreloader(Uri paramUri)
  {
    return null;
  }
  
  public MediaPlayer getOrCreatePlayer(Uri paramUri)
  {
    return this.mYoutubePlayerCache.get(paramUri);
  }
  
  public boolean isPreloaded(Uri paramUri)
  {
    return false;
  }
  
  public void recycleMediaPlayer(MediaPlayer paramMediaPlayer)
  {
    YoutubePlayerImpl localYoutubePlayerImpl = (YoutubePlayerImpl)paramMediaPlayer;
    if (localYoutubePlayerImpl.getPlaybackState() != 1) {
      localYoutubePlayerImpl.stop();
    }
    this.mYoutubePlayerCache.put(paramMediaPlayer.getVideoUri(), localYoutubePlayerImpl);
  }
  
  private class YoutubePlayerLruCache
  {
    private final ArrayList<Pair<Uri, YoutubePlayerImpl>> mCache = new ArrayList();
    private final int mMaxCapacity;
    private YoutubePlayerImpl mReleasedPlayer;
    
    private YoutubePlayerLruCache(int paramInt)
    {
      this.mMaxCapacity = paramInt;
    }
    
    private YoutubePlayerImpl get(Uri paramUri)
    {
      int i = 0;
      Object localObject = this.mCache.iterator();
      for (;;)
      {
        if ((!((Iterator)localObject).hasNext()) || (((Uri)((Pair)((Iterator)localObject).next()).first).equals(paramUri)))
        {
          if (i >= this.mCache.size()) {
            break;
          }
          paramUri = (YoutubePlayerImpl)((Pair)this.mCache.get(i)).second;
          this.mCache.remove(i);
          return paramUri;
        }
        i += 1;
      }
      localObject = this.mReleasedPlayer;
      if (localObject != null) {
        this.mReleasedPlayer = null;
      }
      for (;;)
      {
        ((YoutubePlayerImpl)localObject).setVideoUri(paramUri);
        return (YoutubePlayerImpl)localObject;
        localObject = new YoutubePlayerImpl(YoutubePreloaderManager.this.mContext);
      }
    }
    
    private void put(Uri paramUri, YoutubePlayerImpl paramYoutubePlayerImpl)
    {
      if (this.mMaxCapacity <= 0)
      {
        this.mReleasedPlayer = paramYoutubePlayerImpl;
        this.mReleasedPlayer.release();
      }
      do
      {
        return;
        if (this.mCache.size() >= this.mMaxCapacity)
        {
          this.mReleasedPlayer = ((YoutubePlayerImpl)((Pair)this.mCache.get(0)).second);
          this.mReleasedPlayer.release();
          this.mCache.remove(0);
        }
      } while (this.mCache.size() >= this.mMaxCapacity);
      this.mCache.add(new Pair(paramUri, paramYoutubePlayerImpl));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/preload/impl/YoutubePreloaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */