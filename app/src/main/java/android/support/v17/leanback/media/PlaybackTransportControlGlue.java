package android.support.v17.leanback.media;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter.ViewHolder;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PlaybackSeekDataProvider;
import android.support.v17.leanback.widget.PlaybackSeekUi;
import android.support.v17.leanback.widget.PlaybackSeekUi.Client;
import android.support.v17.leanback.widget.PlaybackTransportRowPresenter;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import java.lang.ref.WeakReference;

public class PlaybackTransportControlGlue<T extends PlayerAdapter>
  extends PlaybackBaseControlGlue<T>
{
  static final boolean DEBUG = false;
  static final int MSG_UPDATE_PLAYBACK_STATE = 100;
  static final String TAG = "PlaybackTransportGlue";
  static final int UPDATE_PLAYBACK_STATE_DELAY_MS = 2000;
  static final Handler sHandler = new UpdatePlaybackStateHandler();
  final WeakReference<PlaybackBaseControlGlue> mGlueWeakReference = new WeakReference(this);
  final PlaybackTransportControlGlue<T>.SeekUiClient mPlaybackSeekUiClient = new SeekUiClient();
  boolean mSeekEnabled;
  PlaybackSeekDataProvider mSeekProvider;
  
  public PlaybackTransportControlGlue(Context paramContext, T paramT)
  {
    super(paramContext, paramT);
  }
  
  private void updatePlaybackState(boolean paramBoolean)
  {
    int i = 1;
    if (this.mControlsRow == null) {
      return;
    }
    if (!paramBoolean)
    {
      onUpdateProgress();
      this.mPlayerAdapter.setProgressUpdatingEnabled(this.mPlaybackSeekUiClient.mIsSeek);
    }
    for (;;)
    {
      if ((this.mFadeWhenPlaying) && (getHost() != null)) {
        getHost().setControlsOverlayAutoHideEnabled(paramBoolean);
      }
      if (this.mPlayPauseAction == null) {
        break;
      }
      if (!paramBoolean) {
        i = 0;
      }
      if (this.mPlayPauseAction.getIndex() == i) {
        break;
      }
      this.mPlayPauseAction.setIndex(i);
      notifyItemChanged((ArrayObjectAdapter)getControlsRow().getPrimaryActionsAdapter(), this.mPlayPauseAction);
      return;
      this.mPlayerAdapter.setProgressUpdatingEnabled(true);
    }
  }
  
  boolean dispatchAction(Action paramAction, KeyEvent paramKeyEvent)
  {
    boolean bool = false;
    int i;
    int j;
    if ((paramAction instanceof PlaybackControlsRow.PlayPauseAction)) {
      if ((paramKeyEvent == null) || (paramKeyEvent.getKeyCode() == 85) || (paramKeyEvent.getKeyCode() == 126))
      {
        i = 1;
        if ((paramKeyEvent != null) && (paramKeyEvent.getKeyCode() != 85) && (paramKeyEvent.getKeyCode() != 127)) {
          break label99;
        }
        j = 1;
        label59:
        if (j == 0) {
          break label112;
        }
        if (i == 0) {
          break label105;
        }
        if (!this.mIsPlaying) {
          break label112;
        }
        label75:
        this.mIsPlaying = false;
        pause();
        label84:
        onUpdatePlaybackStatusAfterUserAction();
        bool = true;
      }
    }
    label99:
    label105:
    label112:
    do
    {
      return bool;
      i = 0;
      break;
      j = 0;
      break label59;
      if (!this.mIsPlaying) {
        break label75;
      }
      if ((i == 0) || (this.mIsPlaying)) {
        break label84;
      }
      this.mIsPlaying = true;
      play();
      break label84;
      if ((paramAction instanceof PlaybackControlsRow.SkipNextAction))
      {
        next();
        return true;
      }
    } while (!(paramAction instanceof PlaybackControlsRow.SkipPreviousAction));
    previous();
    return true;
  }
  
  public final PlaybackSeekDataProvider getSeekProvider()
  {
    return this.mSeekProvider;
  }
  
  public final boolean isSeekEnabled()
  {
    return this.mSeekEnabled;
  }
  
  public void onActionClicked(Action paramAction)
  {
    dispatchAction(paramAction, null);
  }
  
  protected void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    super.onAttachedToHost(paramPlaybackGlueHost);
    if ((paramPlaybackGlueHost instanceof PlaybackSeekUi)) {
      ((PlaybackSeekUi)paramPlaybackGlueHost).setPlaybackSeekUiClient(this.mPlaybackSeekUiClient);
    }
  }
  
  protected void onCreatePrimaryActions(ArrayObjectAdapter paramArrayObjectAdapter)
  {
    PlaybackControlsRow.PlayPauseAction localPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
    this.mPlayPauseAction = localPlayPauseAction;
    paramArrayObjectAdapter.add(localPlayPauseAction);
  }
  
  protected PlaybackRowPresenter onCreateRowPresenter()
  {
    AbstractDetailsDescriptionPresenter local1 = new AbstractDetailsDescriptionPresenter()
    {
      protected void onBindDescription(AbstractDetailsDescriptionPresenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject)
      {
        paramAnonymousObject = (PlaybackBaseControlGlue)paramAnonymousObject;
        paramAnonymousViewHolder.getTitle().setText(((PlaybackBaseControlGlue)paramAnonymousObject).getTitle());
        paramAnonymousViewHolder.getSubtitle().setText(((PlaybackBaseControlGlue)paramAnonymousObject).getSubtitle());
      }
    };
    PlaybackTransportRowPresenter local2 = new PlaybackTransportRowPresenter()
    {
      protected void onBindRowViewHolder(RowPresenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject)
      {
        super.onBindRowViewHolder(paramAnonymousViewHolder, paramAnonymousObject);
        paramAnonymousViewHolder.setOnKeyListener(PlaybackTransportControlGlue.this);
      }
      
      protected void onUnbindRowViewHolder(RowPresenter.ViewHolder paramAnonymousViewHolder)
      {
        super.onUnbindRowViewHolder(paramAnonymousViewHolder);
        paramAnonymousViewHolder.setOnKeyListener(null);
      }
    };
    local2.setDescriptionPresenter(local1);
    return local2;
  }
  
  protected void onDetachedFromHost()
  {
    super.onDetachedFromHost();
    if ((getHost() instanceof PlaybackSeekUi)) {
      ((PlaybackSeekUi)getHost()).setPlaybackSeekUiClient(null);
    }
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    switch (paramInt)
    {
    default: 
      paramView = this.mControlsRow.getPrimaryActionsAdapter();
      Action localAction = this.mControlsRow.getActionForKeyCode(paramView, paramInt);
      paramView = localAction;
      if (localAction == null) {
        paramView = this.mControlsRow.getActionForKeyCode(this.mControlsRow.getSecondaryActionsAdapter(), paramInt);
      }
      bool1 = bool2;
      if (paramView != null)
      {
        if (paramKeyEvent.getAction() == 0) {
          dispatchAction(paramView, paramKeyEvent);
        }
        bool1 = true;
      }
      break;
    }
    return bool1;
  }
  
  protected void onPlayStateChanged()
  {
    if (sHandler.hasMessages(100, this.mGlueWeakReference))
    {
      sHandler.removeMessages(100, this.mGlueWeakReference);
      if (this.mPlayerAdapter.isPlaying() != this.mIsPlaying) {
        sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), 2000L);
      }
    }
    for (;;)
    {
      super.onPlayStateChanged();
      return;
      onUpdatePlaybackState();
      continue;
      onUpdatePlaybackState();
    }
  }
  
  void onUpdatePlaybackState()
  {
    this.mIsPlaying = this.mPlayerAdapter.isPlaying();
    updatePlaybackState(this.mIsPlaying);
  }
  
  void onUpdatePlaybackStatusAfterUserAction()
  {
    updatePlaybackState(this.mIsPlaying);
    sHandler.removeMessages(100, this.mGlueWeakReference);
    sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), 2000L);
  }
  
  void onUpdateProgress()
  {
    PlaybackControlsRow localPlaybackControlsRow;
    if ((this.mControlsRow != null) && (!this.mPlaybackSeekUiClient.mIsSeek))
    {
      localPlaybackControlsRow = this.mControlsRow;
      if (!this.mPlayerAdapter.isPrepared()) {
        break label46;
      }
    }
    label46:
    for (long l = this.mPlayerAdapter.getCurrentPosition();; l = -1L)
    {
      localPlaybackControlsRow.setCurrentPosition(l);
      return;
    }
  }
  
  public void setControlsRow(PlaybackControlsRow paramPlaybackControlsRow)
  {
    super.setControlsRow(paramPlaybackControlsRow);
    sHandler.removeMessages(100, this.mGlueWeakReference);
    onUpdatePlaybackState();
  }
  
  public final void setSeekEnabled(boolean paramBoolean)
  {
    this.mSeekEnabled = paramBoolean;
  }
  
  public final void setSeekProvider(PlaybackSeekDataProvider paramPlaybackSeekDataProvider)
  {
    this.mSeekProvider = paramPlaybackSeekDataProvider;
  }
  
  class SeekUiClient
    extends PlaybackSeekUi.Client
  {
    boolean mIsSeek;
    long mLastUserPosition;
    boolean mPausedBeforeSeek;
    long mPositionBeforeSeek;
    
    SeekUiClient() {}
    
    public PlaybackSeekDataProvider getPlaybackSeekDataProvider()
    {
      return PlaybackTransportControlGlue.this.mSeekProvider;
    }
    
    public boolean isSeekEnabled()
    {
      return (PlaybackTransportControlGlue.this.mSeekProvider != null) || (PlaybackTransportControlGlue.this.mSeekEnabled);
    }
    
    public void onSeekFinished(boolean paramBoolean)
    {
      if (!paramBoolean) {
        if (this.mLastUserPosition > 0L) {
          PlaybackTransportControlGlue.this.seekTo(this.mLastUserPosition);
        }
      }
      for (;;)
      {
        this.mIsSeek = false;
        if (this.mPausedBeforeSeek) {
          break;
        }
        PlaybackTransportControlGlue.this.play();
        return;
        if (this.mPositionBeforeSeek >= 0L) {
          PlaybackTransportControlGlue.this.seekTo(this.mPositionBeforeSeek);
        }
      }
      PlaybackTransportControlGlue.this.mPlayerAdapter.setProgressUpdatingEnabled(false);
      PlaybackTransportControlGlue.this.onUpdateProgress();
    }
    
    public void onSeekPositionChanged(long paramLong)
    {
      if (PlaybackTransportControlGlue.this.mSeekProvider == null) {
        PlaybackTransportControlGlue.this.mPlayerAdapter.seekTo(paramLong);
      }
      for (;;)
      {
        if (PlaybackTransportControlGlue.this.mControlsRow != null) {
          PlaybackTransportControlGlue.this.mControlsRow.setCurrentPosition(paramLong);
        }
        return;
        this.mLastUserPosition = paramLong;
      }
    }
    
    public void onSeekStarted()
    {
      this.mIsSeek = true;
      boolean bool;
      if (!PlaybackTransportControlGlue.this.isPlaying())
      {
        bool = true;
        this.mPausedBeforeSeek = bool;
        PlaybackTransportControlGlue.this.mPlayerAdapter.setProgressUpdatingEnabled(true);
        if (PlaybackTransportControlGlue.this.mSeekProvider != null) {
          break label79;
        }
      }
      label79:
      for (long l = PlaybackTransportControlGlue.this.mPlayerAdapter.getCurrentPosition();; l = -1L)
      {
        this.mPositionBeforeSeek = l;
        this.mLastUserPosition = -1L;
        PlaybackTransportControlGlue.this.pause();
        return;
        bool = false;
        break;
      }
    }
  }
  
  static class UpdatePlaybackStateHandler
    extends Handler
  {
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what == 100)
      {
        paramMessage = (PlaybackTransportControlGlue)((WeakReference)paramMessage.obj).get();
        if (paramMessage != null) {
          paramMessage.onUpdatePlaybackState();
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/PlaybackTransportControlGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */