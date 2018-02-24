package android.support.v17.leanback.media;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter.ViewHolder;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PlaybackBannerControlGlue<T extends PlayerAdapter>
  extends PlaybackBaseControlGlue<T>
{
  public static final int ACTION_CUSTOM_LEFT_FIRST = 1;
  public static final int ACTION_CUSTOM_RIGHT_FIRST = 4096;
  public static final int ACTION_FAST_FORWARD = 128;
  public static final int ACTION_PLAY_PAUSE = 64;
  public static final int ACTION_REWIND = 32;
  public static final int ACTION_SKIP_TO_NEXT = 256;
  public static final int ACTION_SKIP_TO_PREVIOUS = 16;
  private static final int NUMBER_OF_SEEK_SPEEDS = 5;
  public static final int PLAYBACK_SPEED_FAST_L0 = 10;
  public static final int PLAYBACK_SPEED_FAST_L1 = 11;
  public static final int PLAYBACK_SPEED_FAST_L2 = 12;
  public static final int PLAYBACK_SPEED_FAST_L3 = 13;
  public static final int PLAYBACK_SPEED_FAST_L4 = 14;
  public static final int PLAYBACK_SPEED_INVALID = -1;
  public static final int PLAYBACK_SPEED_NORMAL = 1;
  public static final int PLAYBACK_SPEED_PAUSED = 0;
  private static final String TAG = PlaybackBannerControlGlue.class.getSimpleName();
  private PlaybackControlsRow.FastForwardAction mFastForwardAction;
  private final int[] mFastForwardSpeeds;
  private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
  private int mPlaybackSpeed = 0;
  private PlaybackControlsRow.RewindAction mRewindAction;
  private final int[] mRewindSpeeds;
  private PlaybackControlsRow.SkipNextAction mSkipNextAction;
  private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
  private long mStartPosition = 0L;
  private long mStartTime;
  
  public PlaybackBannerControlGlue(Context paramContext, int[] paramArrayOfInt, T paramT)
  {
    this(paramContext, paramArrayOfInt, paramArrayOfInt, paramT);
  }
  
  public PlaybackBannerControlGlue(Context paramContext, int[] paramArrayOfInt1, int[] paramArrayOfInt2, T paramT)
  {
    super(paramContext, paramT);
    if ((paramArrayOfInt1.length == 0) || (paramArrayOfInt1.length > 5)) {
      throw new IllegalArgumentException("invalid fastForwardSpeeds array size");
    }
    this.mFastForwardSpeeds = paramArrayOfInt1;
    if ((paramArrayOfInt2.length == 0) || (paramArrayOfInt2.length > 5)) {
      throw new IllegalArgumentException("invalid rewindSpeeds array size");
    }
    this.mRewindSpeeds = paramArrayOfInt2;
  }
  
  private void fakePause()
  {
    this.mIsPlaying = true;
    this.mStartPosition = getCurrentPosition();
    this.mStartTime = System.currentTimeMillis();
    super.pause();
    onUpdatePlaybackState();
  }
  
  private int getMaxForwardSpeedId()
  {
    return this.mFastForwardSpeeds.length - 1 + 10;
  }
  
  private int getMaxRewindSpeedId()
  {
    return this.mRewindSpeeds.length - 1 + 10;
  }
  
  private void updatePlaybackState(boolean paramBoolean)
  {
    if (this.mControlsRow == null) {
      return;
    }
    label24:
    ArrayObjectAdapter localArrayObjectAdapter;
    if (!paramBoolean)
    {
      onUpdateProgress();
      this.mPlayerAdapter.setProgressUpdatingEnabled(false);
      if ((this.mFadeWhenPlaying) && (getHost() != null)) {
        getHost().setControlsOverlayAutoHideEnabled(paramBoolean);
      }
      localArrayObjectAdapter = (ArrayObjectAdapter)getControlsRow().getPrimaryActionsAdapter();
      if (this.mPlayPauseAction != null) {
        if (paramBoolean) {
          break label222;
        }
      }
    }
    label222:
    for (int i = PlaybackControlsRow.PlayPauseAction.PLAY;; i = PlaybackControlsRow.PlayPauseAction.PAUSE)
    {
      if (this.mPlayPauseAction.getIndex() != i)
      {
        this.mPlayPauseAction.setIndex(i);
        notifyItemChanged(localArrayObjectAdapter, this.mPlayPauseAction);
      }
      if (this.mFastForwardAction != null)
      {
        i = 0;
        if (this.mPlaybackSpeed >= 10) {
          i = this.mPlaybackSpeed - 10 + 1;
        }
        if (this.mFastForwardAction.getIndex() != i)
        {
          this.mFastForwardAction.setIndex(i);
          notifyItemChanged(localArrayObjectAdapter, this.mFastForwardAction);
        }
      }
      if (this.mRewindAction == null) {
        break;
      }
      i = 0;
      if (this.mPlaybackSpeed <= -10) {
        i = -this.mPlaybackSpeed - 10 + 1;
      }
      if (this.mRewindAction.getIndex() == i) {
        break;
      }
      this.mRewindAction.setIndex(i);
      notifyItemChanged(localArrayObjectAdapter, this.mRewindAction);
      return;
      this.mPlayerAdapter.setProgressUpdatingEnabled(true);
      break label24;
    }
  }
  
  boolean dispatchAction(Action paramAction, KeyEvent paramKeyEvent)
  {
    int j = 0;
    boolean bool = false;
    int i;
    if (paramAction == this.mPlayPauseAction) {
      if ((paramKeyEvent == null) || (paramKeyEvent.getKeyCode() == 85) || (paramKeyEvent.getKeyCode() == 126))
      {
        i = 1;
        if ((paramKeyEvent == null) || (paramKeyEvent.getKeyCode() == 85) || (paramKeyEvent.getKeyCode() == 127)) {
          j = 1;
        }
        if (j == 0) {
          break label106;
        }
        if (i == 0) {
          break label99;
        }
        if (this.mPlaybackSpeed != 1) {
          break label106;
        }
        label80:
        pause();
        label84:
        onUpdatePlaybackStatusAfterUserAction();
        bool = true;
      }
    }
    label99:
    label106:
    do
    {
      return bool;
      i = 0;
      break;
      if (this.mPlaybackSpeed != 0) {
        break label80;
      }
      if ((i == 0) || (this.mPlaybackSpeed == 1)) {
        break label84;
      }
      play();
      break label84;
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
        if ((this.mPlayerAdapter.isPrepared()) && (this.mPlaybackSpeed < getMaxForwardSpeedId()))
        {
          fakePause();
          switch (this.mPlaybackSpeed)
          {
          }
        }
        for (this.mPlaybackSpeed = 10;; this.mPlaybackSpeed += 1)
        {
          onUpdatePlaybackStatusAfterUserAction();
          return true;
        }
      }
    } while (paramAction != this.mRewindAction);
    if ((this.mPlayerAdapter.isPrepared()) && (this.mPlaybackSpeed > -getMaxRewindSpeedId()))
    {
      fakePause();
      switch (this.mPlaybackSpeed)
      {
      }
    }
    for (this.mPlaybackSpeed = -10;; this.mPlaybackSpeed -= 1)
    {
      onUpdatePlaybackStatusAfterUserAction();
      return true;
    }
  }
  
  public long getCurrentPosition()
  {
    long l1;
    if ((this.mPlaybackSpeed == 0) || (this.mPlaybackSpeed == 1)) {
      l1 = this.mPlayerAdapter.getCurrentPosition();
    }
    long l2;
    label139:
    do
    {
      return l1;
      if (this.mPlaybackSpeed >= 10) {
        i = this.mPlaybackSpeed;
      }
      for (int i = getFastForwardSpeeds()[(i - 10)];; i = -getRewindSpeeds()[(i - 10)])
      {
        l2 = this.mStartPosition + (System.currentTimeMillis() - this.mStartTime) * i;
        if (l2 <= getDuration()) {
          break label139;
        }
        this.mPlaybackSpeed = 0;
        l1 = getDuration();
        this.mPlayerAdapter.seekTo(l1);
        this.mStartPosition = 0L;
        pause();
        return l1;
        if (this.mPlaybackSpeed > -10) {
          break;
        }
        i = -this.mPlaybackSpeed;
      }
      return -1L;
      l1 = l2;
    } while (l2 >= 0L);
    this.mPlaybackSpeed = 0;
    this.mPlayerAdapter.seekTo(0L);
    this.mStartPosition = 0L;
    pause();
    return 0L;
  }
  
  @NonNull
  public int[] getFastForwardSpeeds()
  {
    return this.mFastForwardSpeeds;
  }
  
  @NonNull
  public int[] getRewindSpeeds()
  {
    return this.mRewindSpeeds;
  }
  
  public long getSupportedActions()
  {
    return 64L;
  }
  
  public void onActionClicked(Action paramAction)
  {
    dispatchAction(paramAction, null);
  }
  
  protected void onCreatePrimaryActions(ArrayObjectAdapter paramArrayObjectAdapter)
  {
    long l = getSupportedActions();
    Object localObject;
    if (((l & 0x10) != 0L) && (this.mSkipPreviousAction == null))
    {
      localObject = new PlaybackControlsRow.SkipPreviousAction(getContext());
      this.mSkipPreviousAction = ((PlaybackControlsRow.SkipPreviousAction)localObject);
      paramArrayObjectAdapter.add(localObject);
    }
    label94:
    label151:
    label260:
    label329:
    label363:
    label397:
    do
    {
      break label260;
      if (((l & 0x20) != 0L) && (this.mRewindAction == null))
      {
        localObject = new PlaybackControlsRow.RewindAction(getContext(), this.mRewindSpeeds.length);
        this.mRewindAction = ((PlaybackControlsRow.RewindAction)localObject);
        paramArrayObjectAdapter.add(localObject);
        if (((l & 0x40) == 0L) || (this.mPlayPauseAction != null)) {
          break label329;
        }
        this.mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
        localObject = new PlaybackControlsRow.PlayPauseAction(getContext());
        this.mPlayPauseAction = ((PlaybackControlsRow.PlayPauseAction)localObject);
        paramArrayObjectAdapter.add(localObject);
        if (((0x80 & l) == 0L) || (this.mFastForwardAction != null)) {
          break label363;
        }
        this.mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getContext(), this.mFastForwardSpeeds.length);
        localObject = new PlaybackControlsRow.FastForwardAction(getContext(), this.mFastForwardSpeeds.length);
        this.mFastForwardAction = ((PlaybackControlsRow.FastForwardAction)localObject);
        paramArrayObjectAdapter.add(localObject);
      }
      for (;;)
      {
        if (((0x100 & l) == 0L) || (this.mSkipNextAction != null)) {
          break label397;
        }
        localObject = new PlaybackControlsRow.SkipNextAction(getContext());
        this.mSkipNextAction = ((PlaybackControlsRow.SkipNextAction)localObject);
        paramArrayObjectAdapter.add(localObject);
        return;
        if (((l & 0x10) != 0L) || (this.mSkipPreviousAction == null)) {
          break;
        }
        paramArrayObjectAdapter.remove(this.mSkipPreviousAction);
        this.mSkipPreviousAction = null;
        break;
        if (((l & 0x20) != 0L) || (this.mRewindAction == null)) {
          break label94;
        }
        paramArrayObjectAdapter.remove(this.mRewindAction);
        this.mRewindAction = null;
        break label94;
        if (((l & 0x40) != 0L) || (this.mPlayPauseAction == null)) {
          break label151;
        }
        paramArrayObjectAdapter.remove(this.mPlayPauseAction);
        this.mPlayPauseAction = null;
        break label151;
        if (((0x80 & l) == 0L) && (this.mFastForwardAction != null))
        {
          paramArrayObjectAdapter.remove(this.mFastForwardAction);
          this.mFastForwardAction = null;
        }
      }
    } while (((0x100 & l) != 0L) || (this.mSkipNextAction == null));
    paramArrayObjectAdapter.remove(this.mSkipNextAction);
    this.mSkipNextAction = null;
  }
  
  protected PlaybackRowPresenter onCreateRowPresenter()
  {
    new PlaybackControlsRowPresenter(new AbstractDetailsDescriptionPresenter()
    {
      protected void onBindDescription(AbstractDetailsDescriptionPresenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject)
      {
        paramAnonymousObject = (PlaybackBannerControlGlue)paramAnonymousObject;
        paramAnonymousViewHolder.getTitle().setText(((PlaybackBannerControlGlue)paramAnonymousObject).getTitle());
        paramAnonymousViewHolder.getSubtitle().setText(((PlaybackBannerControlGlue)paramAnonymousObject).getSubtitle());
      }
    })
    {
      protected void onBindRowViewHolder(RowPresenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject)
      {
        super.onBindRowViewHolder(paramAnonymousViewHolder, paramAnonymousObject);
        paramAnonymousViewHolder.setOnKeyListener(PlaybackBannerControlGlue.this);
      }
      
      protected void onUnbindRowViewHolder(RowPresenter.ViewHolder paramAnonymousViewHolder)
      {
        super.onUnbindRowViewHolder(paramAnonymousViewHolder);
        paramAnonymousViewHolder.setOnKeyListener(null);
      }
    };
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool2 = false;
    boolean bool1;
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
      return bool1;
    }
    if ((this.mPlaybackSpeed >= 10) || (this.mPlaybackSpeed <= -10)) {}
    for (int i = 1;; i = 0)
    {
      bool1 = bool2;
      if (i == 0) {
        break;
      }
      play();
      onUpdatePlaybackStatusAfterUserAction();
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
  
  protected void onPlayCompleted()
  {
    super.onPlayCompleted();
    this.mIsPlaying = false;
    this.mPlaybackSpeed = 0;
    this.mStartPosition = getCurrentPosition();
    this.mStartTime = System.currentTimeMillis();
    onUpdatePlaybackState();
  }
  
  protected void onPlayStateChanged()
  {
    onUpdatePlaybackState();
    super.onPlayStateChanged();
  }
  
  void onUpdatePlaybackState()
  {
    updatePlaybackState(this.mIsPlaying);
  }
  
  void onUpdatePlaybackStatusAfterUserAction()
  {
    updatePlaybackState(this.mIsPlaying);
  }
  
  public void pause()
  {
    this.mIsPlaying = false;
    this.mPlaybackSpeed = 0;
    this.mStartPosition = getCurrentPosition();
    this.mStartTime = System.currentTimeMillis();
    super.pause();
    onUpdatePlaybackState();
  }
  
  public void play()
  {
    if (!this.mPlayerAdapter.isPrepared()) {
      return;
    }
    if ((this.mPlaybackSpeed == 0) && (this.mPlayerAdapter.getCurrentPosition() >= this.mPlayerAdapter.getDuration())) {}
    for (this.mStartPosition = 0L;; this.mStartPosition = getCurrentPosition())
    {
      this.mStartTime = System.currentTimeMillis();
      this.mIsPlaying = true;
      this.mPlaybackSpeed = 1;
      this.mPlayerAdapter.seekTo(this.mStartPosition);
      super.play();
      onUpdatePlaybackState();
      return;
    }
  }
  
  public void setControlsRow(PlaybackControlsRow paramPlaybackControlsRow)
  {
    super.setControlsRow(paramPlaybackControlsRow);
    onUpdatePlaybackState();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface ACTION_ {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/PlaybackBannerControlGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */