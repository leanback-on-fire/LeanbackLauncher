package android.support.v17.leanback.app;

import android.content.Context;
import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.media.PlaybackGlueHost.HostCallback;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PlaybackRowPresenter.ViewHolder;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;

@Deprecated
public abstract class PlaybackControlGlue
  extends android.support.v17.leanback.media.PlaybackControlGlue
{
  OnItemViewClickedListener mExternalOnItemViewClickedListener;
  
  public PlaybackControlGlue(Context paramContext, PlaybackOverlayFragment paramPlaybackOverlayFragment, int[] paramArrayOfInt)
  {
    this(paramContext, paramPlaybackOverlayFragment, paramArrayOfInt, paramArrayOfInt);
  }
  
  public PlaybackControlGlue(Context paramContext, PlaybackOverlayFragment paramPlaybackOverlayFragment, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramContext, paramArrayOfInt1, paramArrayOfInt2);
    if (paramPlaybackOverlayFragment == null) {}
    for (paramContext = (PlaybackGlueHost)null;; paramContext = new PlaybackGlueHostOld(paramPlaybackOverlayFragment))
    {
      setHost(paramContext);
      return;
    }
  }
  
  public PlaybackControlGlue(Context paramContext, int[] paramArrayOfInt)
  {
    super(paramContext, paramArrayOfInt, paramArrayOfInt);
  }
  
  public PlaybackControlGlue(Context paramContext, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramContext, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public PlaybackControlsRowPresenter createControlsRowAndPresenter()
  {
    super.onCreateControlsRowAndPresenter();
    return getControlsRowPresenter();
  }
  
  protected SparseArrayObjectAdapter createPrimaryActionsAdapter(PresenterSelector paramPresenterSelector)
  {
    return super.createPrimaryActionsAdapter(paramPresenterSelector);
  }
  
  public PlaybackOverlayFragment getFragment()
  {
    if ((getHost() instanceof PlaybackGlueHostOld)) {
      return ((PlaybackGlueHostOld)getHost()).mFragment;
    }
    return null;
  }
  
  @Deprecated
  public OnItemViewClickedListener getOnItemViewClickedListener()
  {
    return this.mExternalOnItemViewClickedListener;
  }
  
  public final void next()
  {
    skipToNext();
  }
  
  protected void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    super.onAttachedToHost(paramPlaybackGlueHost);
    if ((paramPlaybackGlueHost instanceof PlaybackGlueHostOld)) {
      ((PlaybackGlueHostOld)paramPlaybackGlueHost).mGlue = this;
    }
  }
  
  protected void onCreateControlsRowAndPresenter() {}
  
  protected void onRowChanged(PlaybackControlsRow paramPlaybackControlsRow) {}
  
  public final void pause()
  {
    pausePlayback();
  }
  
  @Deprecated
  protected void pausePlayback() {}
  
  public final void play(int paramInt)
  {
    startPlayback(paramInt);
  }
  
  public final void previous()
  {
    skipToPrevious();
  }
  
  @Deprecated
  public void setOnItemViewClickedListener(OnItemViewClickedListener paramOnItemViewClickedListener)
  {
    this.mExternalOnItemViewClickedListener = paramOnItemViewClickedListener;
  }
  
  @Deprecated
  protected void skipToNext() {}
  
  @Deprecated
  protected void skipToPrevious() {}
  
  @Deprecated
  protected void startPlayback(int paramInt) {}
  
  @Deprecated
  public static abstract interface InputEventHandler
  {
    public abstract boolean handleInputEvent(InputEvent paramInputEvent);
  }
  
  static final class PlaybackGlueHostOld
    extends PlaybackGlueHost
  {
    OnActionClickedListener mActionClickedListener;
    final PlaybackOverlayFragment mFragment;
    PlaybackControlGlue mGlue;
    
    public PlaybackGlueHostOld(PlaybackOverlayFragment paramPlaybackOverlayFragment)
    {
      this.mFragment = paramPlaybackOverlayFragment;
      this.mFragment.setOnItemViewClickedListener(new OnItemViewClickedListener()
      {
        public void onItemClicked(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, RowPresenter.ViewHolder paramAnonymousViewHolder1, Row paramAnonymousRow)
        {
          if (((paramAnonymousObject instanceof Action)) && ((paramAnonymousViewHolder1 instanceof PlaybackRowPresenter.ViewHolder)) && (PlaybackControlGlue.PlaybackGlueHostOld.this.mActionClickedListener != null)) {
            PlaybackControlGlue.PlaybackGlueHostOld.this.mActionClickedListener.onActionClicked((Action)paramAnonymousObject);
          }
          while ((PlaybackControlGlue.PlaybackGlueHostOld.this.mGlue == null) || (PlaybackControlGlue.PlaybackGlueHostOld.this.mGlue.getOnItemViewClickedListener() == null)) {
            return;
          }
          PlaybackControlGlue.PlaybackGlueHostOld.this.mGlue.getOnItemViewClickedListener().onItemClicked(paramAnonymousViewHolder, paramAnonymousObject, paramAnonymousViewHolder1, paramAnonymousRow);
        }
      });
    }
    
    public void fadeOut()
    {
      this.mFragment.fadeOut();
    }
    
    public void notifyPlaybackRowChanged()
    {
      this.mGlue.onRowChanged(this.mGlue.getControlsRow());
    }
    
    public void setFadingEnabled(boolean paramBoolean)
    {
      this.mFragment.setFadingEnabled(paramBoolean);
    }
    
    public void setHostCallback(PlaybackGlueHost.HostCallback paramHostCallback)
    {
      this.mFragment.setHostCallback(paramHostCallback);
    }
    
    public void setOnActionClickedListener(OnActionClickedListener paramOnActionClickedListener)
    {
      this.mActionClickedListener = paramOnActionClickedListener;
    }
    
    public void setOnKeyInterceptListener(final View.OnKeyListener paramOnKeyListener)
    {
      this.mFragment.setEventHandler(new PlaybackControlGlue.InputEventHandler()
      {
        public boolean handleInputEvent(InputEvent paramAnonymousInputEvent)
        {
          if ((paramAnonymousInputEvent instanceof KeyEvent))
          {
            paramAnonymousInputEvent = (KeyEvent)paramAnonymousInputEvent;
            return paramOnKeyListener.onKey(null, paramAnonymousInputEvent.getKeyCode(), paramAnonymousInputEvent);
          }
          return false;
        }
      });
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/PlaybackControlGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */