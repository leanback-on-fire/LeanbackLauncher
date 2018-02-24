package android.support.v17.leanback.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter.ViewHolder;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.List;

public abstract class PlaybackControlGlue
  extends PlaybackGlue
  implements OnActionClickedListener, View.OnKeyListener
{
  public static final int ACTION_CUSTOM_LEFT_FIRST = 1;
  public static final int ACTION_CUSTOM_RIGHT_FIRST = 4096;
  public static final int ACTION_FAST_FORWARD = 128;
  public static final int ACTION_PLAY_PAUSE = 64;
  public static final int ACTION_REWIND = 32;
  public static final int ACTION_SKIP_TO_NEXT = 256;
  public static final int ACTION_SKIP_TO_PREVIOUS = 16;
  static final boolean DEBUG = false;
  static final int MSG_UPDATE_PLAYBACK_STATE = 100;
  private static final int NUMBER_OF_SEEK_SPEEDS = 5;
  public static final int PLAYBACK_SPEED_FAST_L0 = 10;
  public static final int PLAYBACK_SPEED_FAST_L1 = 11;
  public static final int PLAYBACK_SPEED_FAST_L2 = 12;
  public static final int PLAYBACK_SPEED_FAST_L3 = 13;
  public static final int PLAYBACK_SPEED_FAST_L4 = 14;
  public static final int PLAYBACK_SPEED_INVALID = -1;
  public static final int PLAYBACK_SPEED_NORMAL = 1;
  public static final int PLAYBACK_SPEED_PAUSED = 0;
  static final String TAG = "PlaybackControlGlue";
  private static final int UPDATE_PLAYBACK_STATE_DELAY_MS = 2000;
  static final Handler sHandler = new UpdatePlaybackStateHandler();
  private PlaybackControlsRow mControlsRow;
  private PlaybackRowPresenter mControlsRowPresenter;
  private boolean mFadeWhenPlaying = true;
  private PlaybackControlsRow.FastForwardAction mFastForwardAction;
  private final int[] mFastForwardSpeeds;
  final WeakReference<PlaybackControlGlue> mGlueWeakReference = new WeakReference(this);
  private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
  private int mPlaybackSpeed = 1;
  private PlaybackControlsRow.RewindAction mRewindAction;
  private final int[] mRewindSpeeds;
  private PlaybackControlsRow.SkipNextAction mSkipNextAction;
  private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
  
  public PlaybackControlGlue(Context paramContext, int[] paramArrayOfInt)
  {
    this(paramContext, paramArrayOfInt, paramArrayOfInt);
  }
  
  public PlaybackControlGlue(Context paramContext, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramContext);
    if ((paramArrayOfInt1.length == 0) || (paramArrayOfInt1.length > 5)) {
      throw new IllegalStateException("invalid fastForwardSpeeds array size");
    }
    this.mFastForwardSpeeds = paramArrayOfInt1;
    if ((paramArrayOfInt2.length == 0) || (paramArrayOfInt2.length > 5)) {
      throw new IllegalStateException("invalid rewindSpeeds array size");
    }
    this.mRewindSpeeds = paramArrayOfInt2;
  }
  
  private int getMaxForwardSpeedId()
  {
    return this.mFastForwardSpeeds.length - 1 + 10;
  }
  
  private int getMaxRewindSpeedId()
  {
    return this.mRewindSpeeds.length - 1 + 10;
  }
  
  private static String getSpeedString(int paramInt)
  {
    switch (paramInt)
    {
    case -9: 
    case -8: 
    case -7: 
    case -6: 
    case -5: 
    case -4: 
    case -3: 
    case -2: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      return null;
    case -1: 
      return "PLAYBACK_SPEED_INVALID";
    case 0: 
      return "PLAYBACK_SPEED_PAUSED";
    case 1: 
      return "PLAYBACK_SPEED_NORMAL";
    case 10: 
      return "PLAYBACK_SPEED_FAST_L0";
    case 11: 
      return "PLAYBACK_SPEED_FAST_L1";
    case 12: 
      return "PLAYBACK_SPEED_FAST_L2";
    case 13: 
      return "PLAYBACK_SPEED_FAST_L3";
    case 14: 
      return "PLAYBACK_SPEED_FAST_L4";
    case -10: 
      return "-PLAYBACK_SPEED_FAST_L0";
    case -11: 
      return "-PLAYBACK_SPEED_FAST_L1";
    case -12: 
      return "-PLAYBACK_SPEED_FAST_L2";
    case -13: 
      return "-PLAYBACK_SPEED_FAST_L3";
    }
    return "-PLAYBACK_SPEED_FAST_L4";
  }
  
  private static void notifyItemChanged(SparseArrayObjectAdapter paramSparseArrayObjectAdapter, Object paramObject)
  {
    int i = paramSparseArrayObjectAdapter.indexOf(paramObject);
    if (i >= 0) {
      paramSparseArrayObjectAdapter.notifyArrayItemRangeChanged(i, 1);
    }
  }
  
  private void updateControlsRow()
  {
    updateRowMetadata();
    updateControlButtons();
    sHandler.removeMessages(100, this.mGlueWeakReference);
    updatePlaybackState();
  }
  
  private void updatePlaybackState(int paramInt)
  {
    if (this.mControlsRow == null) {
      return;
    }
    Object localObject = (SparseArrayObjectAdapter)getControlsRow().getPrimaryActionsAdapter();
    int i;
    if (this.mFastForwardAction != null)
    {
      i = 0;
      if (paramInt >= 10) {
        i = paramInt - 10 + 1;
      }
      if (this.mFastForwardAction.getIndex() != i)
      {
        this.mFastForwardAction.setIndex(i);
        notifyItemChanged((SparseArrayObjectAdapter)localObject, this.mFastForwardAction);
      }
    }
    if (this.mRewindAction != null)
    {
      i = 0;
      if (paramInt <= -10) {
        i = -paramInt - 10 + 1;
      }
      if (this.mRewindAction.getIndex() != i)
      {
        this.mRewindAction.setIndex(i);
        notifyItemChanged((SparseArrayObjectAdapter)localObject, this.mRewindAction);
      }
    }
    label134:
    boolean bool;
    if (paramInt == 0)
    {
      updateProgress();
      enableProgressUpdating(false);
      if ((this.mFadeWhenPlaying) && (getHost() != null))
      {
        PlaybackGlueHost localPlaybackGlueHost = getHost();
        if (paramInt != 1) {
          break label264;
        }
        bool = true;
        label161:
        localPlaybackGlueHost.setControlsOverlayAutoHideEnabled(bool);
      }
      if (this.mPlayPauseAction != null) {
        if (paramInt != 0) {
          break label269;
        }
      }
    }
    label264:
    label269:
    for (paramInt = 0;; paramInt = 1)
    {
      if (this.mPlayPauseAction.getIndex() != paramInt)
      {
        this.mPlayPauseAction.setIndex(paramInt);
        notifyItemChanged((SparseArrayObjectAdapter)localObject, this.mPlayPauseAction);
      }
      localObject = getPlayerCallbacks();
      if (localObject == null) {
        break;
      }
      paramInt = 0;
      i = ((List)localObject).size();
      while (paramInt < i)
      {
        ((PlaybackGlue.PlayerCallback)((List)localObject).get(paramInt)).onPlayStateChanged(this);
        paramInt += 1;
      }
      break;
      enableProgressUpdating(true);
      break label134;
      bool = false;
      break label161;
    }
  }
  
  private void updatePlaybackStatusAfterUserAction()
  {
    updatePlaybackState(this.mPlaybackSpeed);
    sHandler.removeMessages(100, this.mGlueWeakReference);
    sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), 2000L);
  }
  
  private void updateRowMetadata()
  {
    if (this.mControlsRow == null) {}
    for (;;)
    {
      return;
      if (!hasValidMedia())
      {
        this.mControlsRow.setImageDrawable(null);
        this.mControlsRow.setTotalTime(0);
        this.mControlsRow.setCurrentTime(0);
      }
      while (getHost() != null)
      {
        getHost().notifyPlaybackRowChanged();
        return;
        this.mControlsRow.setImageDrawable(getMediaArt());
        this.mControlsRow.setTotalTime(getMediaDuration());
        this.mControlsRow.setCurrentTime(getCurrentPosition());
      }
    }
  }
  
  protected SparseArrayObjectAdapter createPrimaryActionsAdapter(PresenterSelector paramPresenterSelector)
  {
    paramPresenterSelector = new SparseArrayObjectAdapter(paramPresenterSelector);
    onCreatePrimaryActions(paramPresenterSelector);
    return paramPresenterSelector;
  }
  
  boolean dispatchAction(Action paramAction, KeyEvent paramKeyEvent)
  {
    boolean bool = false;
    int i;
    int j;
    if (paramAction == this.mPlayPauseAction) {
      if ((paramKeyEvent == null) || (paramKeyEvent.getKeyCode() == 85) || (paramKeyEvent.getKeyCode() == 126))
      {
        i = 1;
        if ((paramKeyEvent != null) && (paramKeyEvent.getKeyCode() != 85) && (paramKeyEvent.getKeyCode() != 127)) {
          break label101;
        }
        j = 1;
        label60:
        if (j == 0) {
          break label114;
        }
        if (i == 0) {
          break label107;
        }
        if (this.mPlaybackSpeed != 1) {
          break label114;
        }
        label77:
        this.mPlaybackSpeed = 0;
        pause();
        label86:
        updatePlaybackStatusAfterUserAction();
        bool = true;
      }
    }
    label101:
    label107:
    label114:
    do
    {
      return bool;
      i = 0;
      break;
      j = 0;
      break label60;
      if (this.mPlaybackSpeed != 0) {
        break label77;
      }
      if ((i == 0) || (this.mPlaybackSpeed == 1)) {
        break label86;
      }
      this.mPlaybackSpeed = 1;
      play(this.mPlaybackSpeed);
      break label86;
      if (paramAction == this.mSkipNextAction)
      {
        next();
        return true;
      }
      if (paramAction == this.mSkipPreviousAction)
      {
        previous();
        return true;
      }
      if (paramAction == this.mFastForwardAction)
      {
        if (this.mPlaybackSpeed < getMaxForwardSpeedId()) {
          switch (this.mPlaybackSpeed)
          {
          }
        }
        for (this.mPlaybackSpeed = 10;; this.mPlaybackSpeed += 1)
        {
          play(this.mPlaybackSpeed);
          updatePlaybackStatusAfterUserAction();
          return true;
        }
      }
    } while (paramAction != this.mRewindAction);
    if (this.mPlaybackSpeed > -getMaxRewindSpeedId()) {
      switch (this.mPlaybackSpeed)
      {
      }
    }
    for (this.mPlaybackSpeed = -10;; this.mPlaybackSpeed -= 1)
    {
      play(this.mPlaybackSpeed);
      updatePlaybackStatusAfterUserAction();
      return true;
    }
  }
  
  public void enableProgressUpdating(boolean paramBoolean) {}
  
  public PlaybackControlsRow getControlsRow()
  {
    return this.mControlsRow;
  }
  
  @Deprecated
  public PlaybackControlsRowPresenter getControlsRowPresenter()
  {
    if ((this.mControlsRowPresenter instanceof PlaybackControlsRowPresenter)) {
      return (PlaybackControlsRowPresenter)this.mControlsRowPresenter;
    }
    return null;
  }
  
  public abstract int getCurrentPosition();
  
  public abstract int getCurrentSpeedId();
  
  public int[] getFastForwardSpeeds()
  {
    return this.mFastForwardSpeeds;
  }
  
  public abstract Drawable getMediaArt();
  
  public abstract int getMediaDuration();
  
  public abstract CharSequence getMediaSubtitle();
  
  public abstract CharSequence getMediaTitle();
  
  public PlaybackRowPresenter getPlaybackRowPresenter()
  {
    return this.mControlsRowPresenter;
  }
  
  public int[] getRewindSpeeds()
  {
    return this.mRewindSpeeds;
  }
  
  public abstract long getSupportedActions();
  
  public int getUpdatePeriod()
  {
    return 500;
  }
  
  public abstract boolean hasValidMedia();
  
  public boolean isFadingEnabled()
  {
    return this.mFadeWhenPlaying;
  }
  
  public abstract boolean isMediaPlaying();
  
  public boolean isPlaying()
  {
    return isMediaPlaying();
  }
  
  public void onActionClicked(Action paramAction)
  {
    dispatchAction(paramAction, null);
  }
  
  protected void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    super.onAttachedToHost(paramPlaybackGlueHost);
    paramPlaybackGlueHost.setOnKeyInterceptListener(this);
    paramPlaybackGlueHost.setOnActionClickedListener(this);
    if ((getControlsRow() == null) || (getPlaybackRowPresenter() == null)) {
      onCreateControlsRowAndPresenter();
    }
    paramPlaybackGlueHost.setPlaybackRowPresenter(getPlaybackRowPresenter());
    paramPlaybackGlueHost.setPlaybackRow(getControlsRow());
  }
  
  protected void onCreateControlsRowAndPresenter()
  {
    if (getControlsRow() == null) {
      setControlsRow(new PlaybackControlsRow(this));
    }
    if (getPlaybackRowPresenter() == null) {
      setPlaybackRowPresenter(new PlaybackControlsRowPresenter(new AbstractDetailsDescriptionPresenter()
      {
        protected void onBindDescription(AbstractDetailsDescriptionPresenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject)
        {
          paramAnonymousObject = (PlaybackControlGlue)paramAnonymousObject;
          if (((PlaybackControlGlue)paramAnonymousObject).hasValidMedia())
          {
            paramAnonymousViewHolder.getTitle().setText(((PlaybackControlGlue)paramAnonymousObject).getMediaTitle());
            paramAnonymousViewHolder.getSubtitle().setText(((PlaybackControlGlue)paramAnonymousObject).getMediaSubtitle());
            return;
          }
          paramAnonymousViewHolder.getTitle().setText("");
          paramAnonymousViewHolder.getSubtitle().setText("");
        }
      })
      {
        protected void onBindRowViewHolder(RowPresenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject)
        {
          super.onBindRowViewHolder(paramAnonymousViewHolder, paramAnonymousObject);
          paramAnonymousViewHolder.setOnKeyListener(PlaybackControlGlue.this);
        }
        
        protected void onUnbindRowViewHolder(RowPresenter.ViewHolder paramAnonymousViewHolder)
        {
          super.onUnbindRowViewHolder(paramAnonymousViewHolder);
          paramAnonymousViewHolder.setOnKeyListener(null);
        }
      });
    }
  }
  
  protected void onCreatePrimaryActions(SparseArrayObjectAdapter paramSparseArrayObjectAdapter) {}
  
  protected void onCreateSecondaryActions(ArrayObjectAdapter paramArrayObjectAdapter) {}
  
  protected void onDetachedFromHost()
  {
    enableProgressUpdating(false);
    super.onDetachedFromHost();
  }
  
  protected void onHostStart()
  {
    enableProgressUpdating(true);
  }
  
  protected void onHostStop()
  {
    enableProgressUpdating(false);
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool2 = false;
    boolean bool1;
    switch (paramInt)
    {
    default: 
      paramView = (SparseArrayObjectAdapter)this.mControlsRow.getPrimaryActionsAdapter();
      Action localAction = this.mControlsRow.getActionForKeyCode(paramView, paramInt);
      bool1 = bool2;
      if (localAction != null) {
        if ((localAction != paramView.lookup(64)) && (localAction != paramView.lookup(32)) && (localAction != paramView.lookup(128)) && (localAction != paramView.lookup(16)))
        {
          bool1 = bool2;
          if (localAction != paramView.lookup(256)) {}
        }
        else
        {
          if (paramKeyEvent.getAction() == 0) {
            dispatchAction(localAction, paramKeyEvent);
          }
          bool1 = true;
        }
      }
      return bool1;
    }
    if ((this.mPlaybackSpeed >= 10) || (this.mPlaybackSpeed <= -10)) {}
    for (int i = 1;; i = 0)
    {
      bool1 = bool2;
      if (i == 0) {
        break;
      }
      this.mPlaybackSpeed = 1;
      play(this.mPlaybackSpeed);
      updatePlaybackStatusAfterUserAction();
      if (paramInt != 4)
      {
        bool1 = bool2;
        if (paramInt != 111) {
          break;
        }
      }
      return true;
    }
  }
  
  protected void onMetadataChanged()
  {
    updateRowMetadata();
  }
  
  protected void onStateChanged()
  {
    if (!hasValidMedia()) {
      return;
    }
    if (sHandler.hasMessages(100, this.mGlueWeakReference))
    {
      sHandler.removeMessages(100, this.mGlueWeakReference);
      if (getCurrentSpeedId() != this.mPlaybackSpeed)
      {
        sHandler.sendMessageDelayed(sHandler.obtainMessage(100, this.mGlueWeakReference), 2000L);
        return;
      }
      updatePlaybackState();
      return;
    }
    updatePlaybackState();
  }
  
  public final void play()
  {
    play(1);
  }
  
  public void play(int paramInt) {}
  
  public void setControlsRow(PlaybackControlsRow paramPlaybackControlsRow)
  {
    this.mControlsRow = paramPlaybackControlsRow;
    this.mControlsRow.setPrimaryActionsAdapter(createPrimaryActionsAdapter(new ControlButtonPresenterSelector()));
    paramPlaybackControlsRow = new ArrayObjectAdapter(new ControlButtonPresenterSelector());
    onCreateSecondaryActions(paramPlaybackControlsRow);
    getControlsRow().setSecondaryActionsAdapter(paramPlaybackControlsRow);
    updateControlsRow();
  }
  
  @Deprecated
  public void setControlsRowPresenter(PlaybackControlsRowPresenter paramPlaybackControlsRowPresenter)
  {
    this.mControlsRowPresenter = paramPlaybackControlsRowPresenter;
  }
  
  public void setFadingEnabled(boolean paramBoolean)
  {
    this.mFadeWhenPlaying = paramBoolean;
    if ((!this.mFadeWhenPlaying) && (getHost() != null)) {
      getHost().setControlsOverlayAutoHideEnabled(false);
    }
  }
  
  public void setPlaybackRowPresenter(PlaybackRowPresenter paramPlaybackRowPresenter)
  {
    this.mControlsRowPresenter = paramPlaybackRowPresenter;
  }
  
  void updateControlButtons()
  {
    SparseArrayObjectAdapter localSparseArrayObjectAdapter = (SparseArrayObjectAdapter)getControlsRow().getPrimaryActionsAdapter();
    long l = getSupportedActions();
    if (((0x10 & l) != 0L) && (this.mSkipPreviousAction == null))
    {
      this.mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getContext());
      localSparseArrayObjectAdapter.set(16, this.mSkipPreviousAction);
    }
    label105:
    label147:
    label238:
    label301:
    label332:
    label364:
    do
    {
      break label238;
      if (((0x20 & l) != 0L) && (this.mRewindAction == null))
      {
        this.mRewindAction = new PlaybackControlsRow.RewindAction(getContext(), this.mRewindSpeeds.length);
        localSparseArrayObjectAdapter.set(32, this.mRewindAction);
        if (((0x40 & l) == 0L) || (this.mPlayPauseAction != null)) {
          break label301;
        }
        this.mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
        localSparseArrayObjectAdapter.set(64, this.mPlayPauseAction);
        if (((0x80 & l) == 0L) || (this.mFastForwardAction != null)) {
          break label332;
        }
        this.mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getContext(), this.mFastForwardSpeeds.length);
        localSparseArrayObjectAdapter.set(128, this.mFastForwardAction);
      }
      for (;;)
      {
        if (((0x100 & l) == 0L) || (this.mSkipNextAction != null)) {
          break label364;
        }
        this.mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getContext());
        localSparseArrayObjectAdapter.set(256, this.mSkipNextAction);
        return;
        if (((0x10 & l) != 0L) || (this.mSkipPreviousAction == null)) {
          break;
        }
        localSparseArrayObjectAdapter.clear(16);
        this.mSkipPreviousAction = null;
        break;
        if (((0x20 & l) != 0L) || (this.mRewindAction == null)) {
          break label105;
        }
        localSparseArrayObjectAdapter.clear(32);
        this.mRewindAction = null;
        break label105;
        if (((0x40 & l) != 0L) || (this.mPlayPauseAction == null)) {
          break label147;
        }
        localSparseArrayObjectAdapter.clear(64);
        this.mPlayPauseAction = null;
        break label147;
        if (((0x80 & l) == 0L) && (this.mFastForwardAction != null))
        {
          localSparseArrayObjectAdapter.clear(128);
          this.mFastForwardAction = null;
        }
      }
    } while (((0x100 & l) != 0L) || (this.mSkipNextAction == null));
    localSparseArrayObjectAdapter.clear(256);
    this.mSkipNextAction = null;
  }
  
  void updatePlaybackState()
  {
    if (hasValidMedia())
    {
      this.mPlaybackSpeed = getCurrentSpeedId();
      updatePlaybackState(this.mPlaybackSpeed);
    }
  }
  
  public void updateProgress()
  {
    int i = getCurrentPosition();
    if (this.mControlsRow != null) {
      this.mControlsRow.setCurrentTime(i);
    }
  }
  
  static class UpdatePlaybackStateHandler
    extends Handler
  {
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what == 100)
      {
        paramMessage = (PlaybackControlGlue)((WeakReference)paramMessage.obj).get();
        if (paramMessage != null) {
          paramMessage.updatePlaybackState();
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/PlaybackControlGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */