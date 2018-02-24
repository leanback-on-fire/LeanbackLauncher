package com.google.android.tvlauncher.instantvideo.preload.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.tv.TvContract;
import android.media.tv.TvView;
import android.net.Uri;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.impl.TvPlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.Preloader;
import com.google.android.tvlauncher.instantvideo.preload.PreloaderManager;
import java.util.Deque;
import java.util.LinkedList;

@TargetApi(24)
public class TvPlayerPreloaderManager
  extends PreloaderManager
{
  private static final int TV_VIEW_POOL_SIZE = 2;
  private final Context mContext;
  private final Deque<TvView> mTvViewPool = new LinkedList();
  
  public TvPlayerPreloaderManager(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  public void bringPreloadedVideoToTopPriority(Uri paramUri) {}
  
  public int canPlayVideo(Uri paramUri)
  {
    if ((TvContract.isChannelUri(paramUri)) || (TvPlayerImpl.isRecordedProgramUri(paramUri)) || (TvPlayerImpl.isPreviewProgramUri(paramUri))) {
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
    TvView localTvView = (TvView)this.mTvViewPool.pollFirst();
    Object localObject = localTvView;
    if (localTvView == null) {
      localObject = new TvView(this.mContext);
    }
    localObject = new TvPlayerImpl(this.mContext, (TvView)localObject);
    ((TvPlayerImpl)localObject).setVideoUri(paramUri);
    return (MediaPlayer)localObject;
  }
  
  public boolean isPreloaded(Uri paramUri)
  {
    return false;
  }
  
  public void recycleMediaPlayer(MediaPlayer paramMediaPlayer)
  {
    if (paramMediaPlayer.getPlaybackState() != 1) {
      paramMediaPlayer.stop();
    }
    if (this.mTvViewPool.size() < 2) {
      this.mTvViewPool.addFirst((TvView)paramMediaPlayer.getPlayerView());
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/preload/impl/TvPlayerPreloaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */