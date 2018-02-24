package android.support.v17.leanback.media;

import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.Row;
import android.view.View.OnKeyListener;

public abstract class PlaybackGlueHost
{
  PlaybackGlue mGlue;
  
  final void attachToGlue(PlaybackGlue paramPlaybackGlue)
  {
    if (this.mGlue != null) {
      this.mGlue.onDetachedFromHost();
    }
    this.mGlue = paramPlaybackGlue;
    if (this.mGlue != null) {
      this.mGlue.onAttachedToHost(this);
    }
  }
  
  @Deprecated
  public void fadeOut() {}
  
  public PlayerCallback getPlayerCallback()
  {
    return null;
  }
  
  public void hideControlsOverlay(boolean paramBoolean) {}
  
  public boolean isControlsOverlayAutoHideEnabled()
  {
    return false;
  }
  
  public boolean isControlsOverlayVisible()
  {
    return true;
  }
  
  public void notifyPlaybackRowChanged() {}
  
  public void setControlsOverlayAutoHideEnabled(boolean paramBoolean)
  {
    setFadingEnabled(paramBoolean);
  }
  
  @Deprecated
  public void setFadingEnabled(boolean paramBoolean) {}
  
  public void setHostCallback(HostCallback paramHostCallback) {}
  
  public void setOnActionClickedListener(OnActionClickedListener paramOnActionClickedListener) {}
  
  public void setOnKeyInterceptListener(View.OnKeyListener paramOnKeyListener) {}
  
  public void setPlaybackRow(Row paramRow) {}
  
  public void setPlaybackRowPresenter(PlaybackRowPresenter paramPlaybackRowPresenter) {}
  
  public void showControlsOverlay(boolean paramBoolean) {}
  
  public static abstract class HostCallback
  {
    public void onHostDestroy() {}
    
    public void onHostPause() {}
    
    public void onHostResume() {}
    
    public void onHostStart() {}
    
    public void onHostStop() {}
  }
  
  public static class PlayerCallback
  {
    public void onBufferingStateChanged(boolean paramBoolean) {}
    
    public void onError(int paramInt, CharSequence paramCharSequence) {}
    
    public void onVideoSizeChanged(int paramInt1, int paramInt2) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/PlaybackGlueHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */