package android.support.v17.leanback.app;

import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.media.PlaybackGlueHost.HostCallback;
import android.support.v17.leanback.media.PlaybackGlueHost.PlayerCallback;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PlaybackSeekUi;
import android.support.v17.leanback.widget.PlaybackSeekUi.Client;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.view.View.OnKeyListener;

public class PlaybackFragmentGlueHost
  extends PlaybackGlueHost
  implements PlaybackSeekUi
{
  private final PlaybackFragment mFragment;
  final PlaybackGlueHost.PlayerCallback mPlayerCallback = new PlaybackGlueHost.PlayerCallback()
  {
    public void onBufferingStateChanged(boolean paramAnonymousBoolean)
    {
      PlaybackFragmentGlueHost.this.mFragment.onBufferingStateChanged(paramAnonymousBoolean);
    }
    
    public void onError(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
    {
      PlaybackFragmentGlueHost.this.mFragment.onError(paramAnonymousInt, paramAnonymousCharSequence);
    }
    
    public void onVideoSizeChanged(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      PlaybackFragmentGlueHost.this.mFragment.onVideoSizeChanged(paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  
  public PlaybackFragmentGlueHost(PlaybackFragment paramPlaybackFragment)
  {
    this.mFragment = paramPlaybackFragment;
  }
  
  public void fadeOut()
  {
    this.mFragment.fadeOut();
  }
  
  public PlaybackGlueHost.PlayerCallback getPlayerCallback()
  {
    return this.mPlayerCallback;
  }
  
  public void hideControlsOverlay(boolean paramBoolean)
  {
    this.mFragment.hideControlsOverlay(paramBoolean);
  }
  
  public boolean isControlsOverlayAutoHideEnabled()
  {
    return this.mFragment.isControlsOverlayAutoHideEnabled();
  }
  
  public boolean isControlsOverlayVisible()
  {
    return this.mFragment.isControlsOverlayVisible();
  }
  
  public void notifyPlaybackRowChanged()
  {
    this.mFragment.notifyPlaybackRowChanged();
  }
  
  public void setControlsOverlayAutoHideEnabled(boolean paramBoolean)
  {
    this.mFragment.setControlsOverlayAutoHideEnabled(paramBoolean);
  }
  
  public void setHostCallback(PlaybackGlueHost.HostCallback paramHostCallback)
  {
    this.mFragment.setHostCallback(paramHostCallback);
  }
  
  public void setOnActionClickedListener(final OnActionClickedListener paramOnActionClickedListener)
  {
    if (paramOnActionClickedListener == null)
    {
      this.mFragment.setOnPlaybackItemViewClickedListener(null);
      return;
    }
    this.mFragment.setOnPlaybackItemViewClickedListener(new OnItemViewClickedListener()
    {
      public void onItemClicked(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, RowPresenter.ViewHolder paramAnonymousViewHolder1, Row paramAnonymousRow)
      {
        if ((paramAnonymousObject instanceof Action)) {
          paramOnActionClickedListener.onActionClicked((Action)paramAnonymousObject);
        }
      }
    });
  }
  
  public void setOnKeyInterceptListener(View.OnKeyListener paramOnKeyListener)
  {
    this.mFragment.setOnKeyInterceptListener(paramOnKeyListener);
  }
  
  public void setPlaybackRow(Row paramRow)
  {
    this.mFragment.setPlaybackRow(paramRow);
  }
  
  public void setPlaybackRowPresenter(PlaybackRowPresenter paramPlaybackRowPresenter)
  {
    this.mFragment.setPlaybackRowPresenter(paramPlaybackRowPresenter);
  }
  
  public void setPlaybackSeekUiClient(PlaybackSeekUi.Client paramClient)
  {
    this.mFragment.setPlaybackSeekUiClient(paramClient);
  }
  
  public void showControlsOverlay(boolean paramBoolean)
  {
    this.mFragment.showControlsOverlay(paramBoolean);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/PlaybackFragmentGlueHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */