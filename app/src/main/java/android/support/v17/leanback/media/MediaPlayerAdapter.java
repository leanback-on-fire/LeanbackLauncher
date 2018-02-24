package android.support.v17.leanback.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.support.v17.leanback.R.string;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import java.io.IOException;

public class MediaPlayerAdapter
  extends PlayerAdapter
{
  long mBufferedProgress;
  boolean mBufferingStart;
  Context mContext;
  final Handler mHandler = new Handler();
  boolean mHasDisplay;
  boolean mInitialized = false;
  Uri mMediaSourceUri = null;
  final MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener()
  {
    public void onBufferingUpdate(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt)
    {
      MediaPlayerAdapter.this.mBufferedProgress = (MediaPlayerAdapter.this.getDuration() * paramAnonymousInt / 100L);
      MediaPlayerAdapter.this.getCallback().onBufferedPositionChanged(MediaPlayerAdapter.this);
    }
  };
  final MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener()
  {
    public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
    {
      MediaPlayerAdapter.this.getCallback().onPlayStateChanged(MediaPlayerAdapter.this);
      MediaPlayerAdapter.this.getCallback().onPlayCompleted(MediaPlayerAdapter.this);
    }
  };
  final MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener()
  {
    public boolean onError(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      MediaPlayerAdapter.this.getCallback().onError(MediaPlayerAdapter.this, paramAnonymousInt1, MediaPlayerAdapter.this.mContext.getString(R.string.lb_media_player_error, new Object[] { Integer.valueOf(paramAnonymousInt1), Integer.valueOf(paramAnonymousInt2) }));
      return MediaPlayerAdapter.this.onError(paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  final MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener()
  {
    public boolean onInfo(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      boolean bool1 = false;
      int i = 0;
      switch (paramAnonymousInt1)
      {
      }
      for (;;)
      {
        boolean bool2 = MediaPlayerAdapter.this.onInfo(paramAnonymousInt1, paramAnonymousInt2);
        if ((i != 0) || (bool2)) {
          bool1 = true;
        }
        return bool1;
        MediaPlayerAdapter.this.mBufferingStart = true;
        MediaPlayerAdapter.this.notifyBufferingStartEnd();
        i = 1;
        continue;
        MediaPlayerAdapter.this.mBufferingStart = false;
        MediaPlayerAdapter.this.notifyBufferingStartEnd();
        i = 1;
      }
    }
  };
  MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener()
  {
    public void onPrepared(MediaPlayer paramAnonymousMediaPlayer)
    {
      MediaPlayerAdapter.this.mInitialized = true;
      MediaPlayerAdapter.this.notifyBufferingStartEnd();
      if ((MediaPlayerAdapter.this.mSurfaceHolderGlueHost == null) || (MediaPlayerAdapter.this.mHasDisplay)) {
        MediaPlayerAdapter.this.getCallback().onPreparedStateChanged(MediaPlayerAdapter.this);
      }
    }
  };
  final MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener()
  {
    public void onSeekComplete(MediaPlayer paramAnonymousMediaPlayer)
    {
      MediaPlayerAdapter.this.onSeekComplete();
    }
  };
  final MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener()
  {
    public void onVideoSizeChanged(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      MediaPlayerAdapter.this.getCallback().onVideoSizeChanged(MediaPlayerAdapter.this, paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  final MediaPlayer mPlayer = new MediaPlayer();
  final Runnable mRunnable = new Runnable()
  {
    public void run()
    {
      MediaPlayerAdapter.this.getCallback().onCurrentPositionChanged(MediaPlayerAdapter.this);
      MediaPlayerAdapter.this.mHandler.postDelayed(this, MediaPlayerAdapter.this.getUpdatePeriod());
    }
  };
  SurfaceHolderGlueHost mSurfaceHolderGlueHost;
  
  public MediaPlayerAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void prepareMediaForPlaying()
  {
    reset();
    try
    {
      if (this.mMediaSourceUri != null)
      {
        this.mPlayer.setDataSource(this.mContext, this.mMediaSourceUri);
        this.mPlayer.setAudioStreamType(3);
        this.mPlayer.setOnPreparedListener(this.mOnPreparedListener);
        this.mPlayer.setOnVideoSizeChangedListener(this.mOnVideoSizeChangedListener);
        this.mPlayer.setOnErrorListener(this.mOnErrorListener);
        this.mPlayer.setOnSeekCompleteListener(this.mOnSeekCompleteListener);
        this.mPlayer.setOnCompletionListener(this.mOnCompletionListener);
        this.mPlayer.setOnInfoListener(this.mOnInfoListener);
        this.mPlayer.setOnBufferingUpdateListener(this.mOnBufferingUpdateListener);
        notifyBufferingStartEnd();
        this.mPlayer.prepareAsync();
        getCallback().onPlayStateChanged(this);
      }
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      throw new RuntimeException(localIOException);
    }
  }
  
  void changeToUnitialized()
  {
    if (this.mInitialized)
    {
      this.mInitialized = false;
      notifyBufferingStartEnd();
      if (this.mHasDisplay) {
        getCallback().onPreparedStateChanged(this);
      }
    }
  }
  
  public long getBufferedPosition()
  {
    return this.mBufferedProgress;
  }
  
  public long getCurrentPosition()
  {
    if (this.mInitialized) {
      return this.mPlayer.getCurrentPosition();
    }
    return -1L;
  }
  
  public long getDuration()
  {
    if (this.mInitialized) {
      return this.mPlayer.getDuration();
    }
    return -1L;
  }
  
  int getUpdatePeriod()
  {
    return 16;
  }
  
  public boolean isPlaying()
  {
    return (this.mInitialized) && (this.mPlayer.isPlaying());
  }
  
  public boolean isPrepared()
  {
    return (this.mInitialized) && ((this.mSurfaceHolderGlueHost == null) || (this.mHasDisplay));
  }
  
  void notifyBufferingStartEnd()
  {
    PlayerAdapter.Callback localCallback = getCallback();
    if ((this.mBufferingStart) || (!this.mInitialized)) {}
    for (boolean bool = true;; bool = false)
    {
      localCallback.onBufferingStateChanged(this, bool);
      return;
    }
  }
  
  public void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    if ((paramPlaybackGlueHost instanceof SurfaceHolderGlueHost))
    {
      this.mSurfaceHolderGlueHost = ((SurfaceHolderGlueHost)paramPlaybackGlueHost);
      this.mSurfaceHolderGlueHost.setSurfaceHolderCallback(new VideoPlayerSurfaceHolderCallback());
    }
  }
  
  public void onDetachedFromHost()
  {
    if (this.mSurfaceHolderGlueHost != null)
    {
      this.mSurfaceHolderGlueHost.setSurfaceHolderCallback(null);
      this.mSurfaceHolderGlueHost = null;
    }
    reset();
    release();
  }
  
  protected boolean onError(int paramInt1, int paramInt2)
  {
    return false;
  }
  
  protected boolean onInfo(int paramInt1, int paramInt2)
  {
    return false;
  }
  
  protected void onSeekComplete() {}
  
  public void pause()
  {
    if (isPlaying())
    {
      this.mPlayer.pause();
      getCallback().onPlayStateChanged(this);
    }
  }
  
  public void play()
  {
    if ((!this.mInitialized) || (this.mPlayer.isPlaying())) {
      return;
    }
    this.mPlayer.start();
    getCallback().onPlayStateChanged(this);
    getCallback().onCurrentPositionChanged(this);
  }
  
  public void release()
  {
    changeToUnitialized();
    this.mHasDisplay = false;
    this.mPlayer.release();
  }
  
  public void reset()
  {
    changeToUnitialized();
    this.mPlayer.reset();
  }
  
  public void seekTo(long paramLong)
  {
    if (!this.mInitialized) {
      return;
    }
    this.mPlayer.seekTo((int)paramLong);
  }
  
  public boolean setDataSource(Uri paramUri)
  {
    if (this.mMediaSourceUri != null)
    {
      if (!this.mMediaSourceUri.equals(paramUri)) {}
    }
    else {
      while (paramUri == null) {
        return false;
      }
    }
    this.mMediaSourceUri = paramUri;
    prepareMediaForPlaying();
    return true;
  }
  
  void setDisplay(SurfaceHolder paramSurfaceHolder)
  {
    boolean bool2 = this.mHasDisplay;
    boolean bool1;
    if (paramSurfaceHolder != null)
    {
      bool1 = true;
      this.mHasDisplay = bool1;
      if (bool2 != this.mHasDisplay) {
        break label30;
      }
    }
    label30:
    label61:
    do
    {
      do
      {
        return;
        bool1 = false;
        break;
        this.mPlayer.setDisplay(paramSurfaceHolder);
        if (!this.mHasDisplay) {
          break label61;
        }
      } while (!this.mInitialized);
      getCallback().onPreparedStateChanged(this);
      return;
    } while (!this.mInitialized);
    getCallback().onPreparedStateChanged(this);
  }
  
  public void setProgressUpdatingEnabled(boolean paramBoolean)
  {
    this.mHandler.removeCallbacks(this.mRunnable);
    if (!paramBoolean) {
      return;
    }
    this.mHandler.postDelayed(this.mRunnable, getUpdatePeriod());
  }
  
  class VideoPlayerSurfaceHolderCallback
    implements SurfaceHolder.Callback
  {
    VideoPlayerSurfaceHolderCallback() {}
    
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      MediaPlayerAdapter.this.setDisplay(paramSurfaceHolder);
    }
    
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      MediaPlayerAdapter.this.setDisplay(null);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/MediaPlayerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */