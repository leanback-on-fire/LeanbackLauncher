package android.support.v17.leanback.media;

import android.content.Context;
import android.support.annotation.CallSuper;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaybackGlue
{
  private final Context mContext;
  private PlaybackGlueHost mPlaybackGlueHost;
  ArrayList<PlayerCallback> mPlayerCallbacks;
  
  public PlaybackGlue(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public void addPlayerCallback(PlayerCallback paramPlayerCallback)
  {
    if (this.mPlayerCallbacks == null) {
      this.mPlayerCallbacks = new ArrayList();
    }
    this.mPlayerCallbacks.add(paramPlayerCallback);
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public PlaybackGlueHost getHost()
  {
    return this.mPlaybackGlueHost;
  }
  
  protected List<PlayerCallback> getPlayerCallbacks()
  {
    if (this.mPlayerCallbacks == null) {
      return null;
    }
    return new ArrayList(this.mPlayerCallbacks);
  }
  
  public boolean isPlaying()
  {
    return false;
  }
  
  public boolean isPrepared()
  {
    return isReadyForPlayback();
  }
  
  @Deprecated
  public boolean isReadyForPlayback()
  {
    return true;
  }
  
  public void next() {}
  
  @CallSuper
  protected void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    this.mPlaybackGlueHost = paramPlaybackGlueHost;
    this.mPlaybackGlueHost.setHostCallback(new PlaybackGlueHost.HostCallback()
    {
      public void onHostDestroy()
      {
        PlaybackGlue.this.setHost(null);
      }
      
      public void onHostPause()
      {
        PlaybackGlue.this.onHostPause();
      }
      
      public void onHostResume()
      {
        PlaybackGlue.this.onHostResume();
      }
      
      public void onHostStart()
      {
        PlaybackGlue.this.onHostStart();
      }
      
      public void onHostStop()
      {
        PlaybackGlue.this.onHostStop();
      }
    });
  }
  
  @CallSuper
  protected void onDetachedFromHost()
  {
    if (this.mPlaybackGlueHost != null)
    {
      this.mPlaybackGlueHost.setHostCallback(null);
      this.mPlaybackGlueHost = null;
    }
  }
  
  protected void onHostPause() {}
  
  protected void onHostResume() {}
  
  protected void onHostStart() {}
  
  protected void onHostStop() {}
  
  public void pause() {}
  
  public void play() {}
  
  public void previous() {}
  
  public void removePlayerCallback(PlayerCallback paramPlayerCallback)
  {
    if (this.mPlayerCallbacks != null) {
      this.mPlayerCallbacks.remove(paramPlayerCallback);
    }
  }
  
  public final void setHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    if (this.mPlaybackGlueHost == paramPlaybackGlueHost) {}
    do
    {
      return;
      if (this.mPlaybackGlueHost != null) {
        this.mPlaybackGlueHost.attachToGlue(null);
      }
      this.mPlaybackGlueHost = paramPlaybackGlueHost;
    } while (this.mPlaybackGlueHost == null);
    this.mPlaybackGlueHost.attachToGlue(this);
  }
  
  @Deprecated
  public void setPlayerCallback(PlayerCallback paramPlayerCallback)
  {
    if (paramPlayerCallback == null)
    {
      if (this.mPlayerCallbacks != null) {
        this.mPlayerCallbacks.clear();
      }
      return;
    }
    addPlayerCallback(paramPlayerCallback);
  }
  
  public static abstract class PlayerCallback
  {
    public void onPlayCompleted(PlaybackGlue paramPlaybackGlue) {}
    
    public void onPlayStateChanged(PlaybackGlue paramPlaybackGlue) {}
    
    public void onPreparedStateChanged(PlaybackGlue paramPlaybackGlue)
    {
      if (paramPlaybackGlue.isPrepared()) {
        onReadyForPlayback();
      }
    }
    
    @Deprecated
    public void onReadyForPlayback() {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/PlaybackGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */