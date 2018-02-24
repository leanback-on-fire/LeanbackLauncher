package android.support.v17.leanback.app;

import android.content.Context;
import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.media.PlaybackGlueHost.HostCallback;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackRowPresenter.ViewHolder;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;

@Deprecated
public abstract class PlaybackControlSupportGlue
  extends PlaybackControlGlue
{
  public static final int ACTION_CUSTOM_LEFT_FIRST = 1;
  public static final int ACTION_CUSTOM_RIGHT_FIRST = 4096;
  public static final int ACTION_FAST_FORWARD = 128;
  public static final int ACTION_PLAY_PAUSE = 64;
  public static final int ACTION_REWIND = 32;
  public static final int ACTION_SKIP_TO_NEXT = 256;
  public static final int ACTION_SKIP_TO_PREVIOUS = 16;
  public static final int PLAYBACK_SPEED_FAST_L0 = 10;
  public static final int PLAYBACK_SPEED_FAST_L1 = 11;
  public static final int PLAYBACK_SPEED_FAST_L2 = 12;
  public static final int PLAYBACK_SPEED_FAST_L3 = 13;
  public static final int PLAYBACK_SPEED_FAST_L4 = 14;
  public static final int PLAYBACK_SPEED_INVALID = -1;
  public static final int PLAYBACK_SPEED_NORMAL = 1;
  public static final int PLAYBACK_SPEED_PAUSED = 0;
  
  public PlaybackControlSupportGlue(Context paramContext, PlaybackOverlaySupportFragment paramPlaybackOverlaySupportFragment, int[] paramArrayOfInt)
  {
    this(paramContext, paramPlaybackOverlaySupportFragment, paramArrayOfInt, paramArrayOfInt);
  }
  
  public PlaybackControlSupportGlue(Context paramContext, PlaybackOverlaySupportFragment paramPlaybackOverlaySupportFragment, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramContext, paramArrayOfInt1, paramArrayOfInt2);
    if (paramPlaybackOverlaySupportFragment == null) {}
    for (paramContext = null;; paramContext = new PlaybackSupportGlueHostOld(paramPlaybackOverlaySupportFragment))
    {
      setHost(paramContext);
      return;
    }
  }
  
  public PlaybackControlSupportGlue(Context paramContext, int[] paramArrayOfInt)
  {
    this(paramContext, null, paramArrayOfInt, paramArrayOfInt);
  }
  
  public PlaybackControlSupportGlue(Context paramContext, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    this(paramContext, null, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    super.onAttachedToHost(paramPlaybackGlueHost);
    if ((paramPlaybackGlueHost instanceof PlaybackSupportGlueHostOld)) {
      ((PlaybackSupportGlueHostOld)paramPlaybackGlueHost).mGlue = this;
    }
  }
  
  static final class PlaybackSupportGlueHostOld
    extends PlaybackGlueHost
  {
    OnActionClickedListener mActionClickedListener;
    final PlaybackOverlaySupportFragment mFragment;
    PlaybackControlSupportGlue mGlue;
    
    public PlaybackSupportGlueHostOld(PlaybackOverlaySupportFragment paramPlaybackOverlaySupportFragment)
    {
      this.mFragment = paramPlaybackOverlaySupportFragment;
      this.mFragment.setOnItemViewClickedListener(new OnItemViewClickedListener()
      {
        public void onItemClicked(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, RowPresenter.ViewHolder paramAnonymousViewHolder1, Row paramAnonymousRow)
        {
          if (((paramAnonymousObject instanceof Action)) && ((paramAnonymousViewHolder1 instanceof PlaybackRowPresenter.ViewHolder)) && (PlaybackControlSupportGlue.PlaybackSupportGlueHostOld.this.mActionClickedListener != null)) {
            PlaybackControlSupportGlue.PlaybackSupportGlueHostOld.this.mActionClickedListener.onActionClicked((Action)paramAnonymousObject);
          }
          while ((PlaybackControlSupportGlue.PlaybackSupportGlueHostOld.this.mGlue == null) || (PlaybackControlSupportGlue.PlaybackSupportGlueHostOld.this.mGlue.getOnItemViewClickedListener() == null)) {
            return;
          }
          PlaybackControlSupportGlue.PlaybackSupportGlueHostOld.this.mGlue.getOnItemViewClickedListener().onItemClicked(paramAnonymousViewHolder, paramAnonymousObject, paramAnonymousViewHolder1, paramAnonymousRow);
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/PlaybackControlSupportGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */