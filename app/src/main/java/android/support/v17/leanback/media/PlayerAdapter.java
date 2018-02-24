package android.support.v17.leanback.media;

public abstract class PlayerAdapter
{
  Callback mCallback;
  
  public long getBufferedPosition()
  {
    return 0L;
  }
  
  public final Callback getCallback()
  {
    return this.mCallback;
  }
  
  public long getCurrentPosition()
  {
    return 0L;
  }
  
  public long getDuration()
  {
    return 0L;
  }
  
  public boolean isPlaying()
  {
    return false;
  }
  
  public boolean isPrepared()
  {
    return true;
  }
  
  public void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost) {}
  
  public void onDetachedFromHost() {}
  
  public abstract void pause();
  
  public abstract void play();
  
  public void seekTo(long paramLong) {}
  
  public final void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setProgressUpdatingEnabled(boolean paramBoolean) {}
  
  public static class Callback
  {
    public void onBufferedPositionChanged(PlayerAdapter paramPlayerAdapter) {}
    
    public void onBufferingStateChanged(PlayerAdapter paramPlayerAdapter, boolean paramBoolean) {}
    
    public void onCurrentPositionChanged(PlayerAdapter paramPlayerAdapter) {}
    
    public void onDurationChanged(PlayerAdapter paramPlayerAdapter) {}
    
    public void onError(PlayerAdapter paramPlayerAdapter, int paramInt, String paramString) {}
    
    public void onPlayCompleted(PlayerAdapter paramPlayerAdapter) {}
    
    public void onPlayStateChanged(PlayerAdapter paramPlayerAdapter) {}
    
    public void onPreparedStateChanged(PlayerAdapter paramPlayerAdapter) {}
    
    public void onVideoSizeChanged(PlayerAdapter paramPlayerAdapter, int paramInt1, int paramInt2) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/PlayerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */