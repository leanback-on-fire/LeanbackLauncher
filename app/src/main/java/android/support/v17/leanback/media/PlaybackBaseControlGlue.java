package android.support.v17.leanback.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import java.util.List;

public abstract class PlaybackBaseControlGlue<T extends PlayerAdapter>
  extends PlaybackGlue
  implements OnActionClickedListener, View.OnKeyListener
{
  static final boolean DEBUG = false;
  static final String TAG = "PlaybackTransportGlue";
  final PlayerAdapter.Callback mAdapterCallback = new PlayerAdapter.Callback()
  {
    public void onBufferedPositionChanged(PlayerAdapter paramAnonymousPlayerAdapter)
    {
      PlaybackBaseControlGlue.this.onUpdateBufferedProgress();
    }
    
    public void onBufferingStateChanged(PlayerAdapter paramAnonymousPlayerAdapter, boolean paramAnonymousBoolean)
    {
      PlaybackBaseControlGlue.this.mBuffering = paramAnonymousBoolean;
      if (PlaybackBaseControlGlue.this.mPlayerCallback != null) {
        PlaybackBaseControlGlue.this.mPlayerCallback.onBufferingStateChanged(paramAnonymousBoolean);
      }
    }
    
    public void onCurrentPositionChanged(PlayerAdapter paramAnonymousPlayerAdapter)
    {
      PlaybackBaseControlGlue.this.onUpdateProgress();
    }
    
    public void onDurationChanged(PlayerAdapter paramAnonymousPlayerAdapter)
    {
      PlaybackBaseControlGlue.this.onUpdateDuration();
    }
    
    public void onError(PlayerAdapter paramAnonymousPlayerAdapter, int paramAnonymousInt, String paramAnonymousString)
    {
      PlaybackBaseControlGlue.this.mErrorSet = true;
      PlaybackBaseControlGlue.this.mErrorCode = paramAnonymousInt;
      PlaybackBaseControlGlue.this.mErrorMessage = paramAnonymousString;
      if (PlaybackBaseControlGlue.this.mPlayerCallback != null) {
        PlaybackBaseControlGlue.this.mPlayerCallback.onError(paramAnonymousInt, paramAnonymousString);
      }
    }
    
    public void onPlayCompleted(PlayerAdapter paramAnonymousPlayerAdapter)
    {
      PlaybackBaseControlGlue.this.onPlayCompleted();
    }
    
    public void onPlayStateChanged(PlayerAdapter paramAnonymousPlayerAdapter)
    {
      PlaybackBaseControlGlue.this.onPlayStateChanged();
    }
    
    public void onPreparedStateChanged(PlayerAdapter paramAnonymousPlayerAdapter)
    {
      PlaybackBaseControlGlue.this.onPreparedStateChanged();
    }
    
    public void onVideoSizeChanged(PlayerAdapter paramAnonymousPlayerAdapter, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      PlaybackBaseControlGlue.this.mVideoWidth = paramAnonymousInt1;
      PlaybackBaseControlGlue.this.mVideoHeight = paramAnonymousInt2;
      if (PlaybackBaseControlGlue.this.mPlayerCallback != null) {
        PlaybackBaseControlGlue.this.mPlayerCallback.onVideoSizeChanged(paramAnonymousInt1, paramAnonymousInt2);
      }
    }
  };
  boolean mBuffering = false;
  PlaybackControlsRow mControlsRow;
  PlaybackRowPresenter mControlsRowPresenter;
  Drawable mCover;
  int mErrorCode;
  String mErrorMessage;
  boolean mErrorSet = false;
  boolean mFadeWhenPlaying = true;
  boolean mIsPlaying = false;
  PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
  final T mPlayerAdapter;
  PlaybackGlueHost.PlayerCallback mPlayerCallback;
  CharSequence mSubtitle;
  CharSequence mTitle;
  int mVideoHeight = 0;
  int mVideoWidth = 0;
  
  public PlaybackBaseControlGlue(Context paramContext, T paramT)
  {
    super(paramContext);
    this.mPlayerAdapter = paramT;
    this.mPlayerAdapter.setCallback(this.mAdapterCallback);
  }
  
  protected static void notifyItemChanged(ArrayObjectAdapter paramArrayObjectAdapter, Object paramObject)
  {
    int i = paramArrayObjectAdapter.indexOf(paramObject);
    if (i >= 0) {
      paramArrayObjectAdapter.notifyArrayItemRangeChanged(i, 1);
    }
  }
  
  private void updateControlsRow()
  {
    onMetadataChanged();
  }
  
  public Drawable getArt()
  {
    return this.mCover;
  }
  
  public final long getBufferedPosition()
  {
    return this.mPlayerAdapter.getBufferedPosition();
  }
  
  public PlaybackControlsRow getControlsRow()
  {
    return this.mControlsRow;
  }
  
  public long getCurrentPosition()
  {
    return this.mPlayerAdapter.getCurrentPosition();
  }
  
  public final long getDuration()
  {
    return this.mPlayerAdapter.getDuration();
  }
  
  public PlaybackRowPresenter getPlaybackRowPresenter()
  {
    return this.mControlsRowPresenter;
  }
  
  public final T getPlayerAdapter()
  {
    return this.mPlayerAdapter;
  }
  
  public CharSequence getSubtitle()
  {
    return this.mSubtitle;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public boolean isControlsOverlayAutoHideEnabled()
  {
    return this.mFadeWhenPlaying;
  }
  
  public final boolean isPlaying()
  {
    return this.mPlayerAdapter.isPlaying();
  }
  
  public final boolean isPrepared()
  {
    return this.mPlayerAdapter.isPrepared();
  }
  
  public abstract void onActionClicked(Action paramAction);
  
  void onAttachHostCallback()
  {
    if (this.mPlayerCallback != null)
    {
      if ((this.mVideoWidth != 0) && (this.mVideoHeight != 0)) {
        this.mPlayerCallback.onVideoSizeChanged(this.mVideoWidth, this.mVideoHeight);
      }
      if (this.mErrorSet) {
        this.mPlayerCallback.onError(this.mErrorCode, this.mErrorMessage);
      }
      this.mPlayerCallback.onBufferingStateChanged(this.mBuffering);
    }
  }
  
  protected void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    super.onAttachedToHost(paramPlaybackGlueHost);
    paramPlaybackGlueHost.setOnKeyInterceptListener(this);
    paramPlaybackGlueHost.setOnActionClickedListener(this);
    onCreateDefaultControlsRow();
    onCreateDefaultRowPresenter();
    paramPlaybackGlueHost.setPlaybackRowPresenter(getPlaybackRowPresenter());
    paramPlaybackGlueHost.setPlaybackRow(getControlsRow());
    this.mPlayerCallback = paramPlaybackGlueHost.getPlayerCallback();
    onAttachHostCallback();
    this.mPlayerAdapter.onAttachedToHost(paramPlaybackGlueHost);
  }
  
  void onCreateDefaultControlsRow()
  {
    if (this.mControlsRow == null) {
      setControlsRow(new PlaybackControlsRow(this));
    }
  }
  
  void onCreateDefaultRowPresenter()
  {
    if (this.mControlsRowPresenter == null) {
      setPlaybackRowPresenter(onCreateRowPresenter());
    }
  }
  
  protected void onCreatePrimaryActions(ArrayObjectAdapter paramArrayObjectAdapter) {}
  
  protected abstract PlaybackRowPresenter onCreateRowPresenter();
  
  protected void onCreateSecondaryActions(ArrayObjectAdapter paramArrayObjectAdapter) {}
  
  void onDetachHostCallback()
  {
    this.mErrorSet = false;
    this.mErrorCode = 0;
    this.mErrorMessage = null;
    if (this.mPlayerCallback != null) {
      this.mPlayerCallback.onBufferingStateChanged(false);
    }
  }
  
  protected void onDetachedFromHost()
  {
    onDetachHostCallback();
    this.mPlayerCallback = null;
    this.mPlayerAdapter.onDetachedFromHost();
    this.mPlayerAdapter.setProgressUpdatingEnabled(false);
    super.onDetachedFromHost();
  }
  
  protected void onHostStart()
  {
    this.mPlayerAdapter.setProgressUpdatingEnabled(true);
  }
  
  protected void onHostStop()
  {
    this.mPlayerAdapter.setProgressUpdatingEnabled(false);
  }
  
  public abstract boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent);
  
  void onMetadataChanged()
  {
    if (this.mControlsRow == null) {}
    do
    {
      return;
      this.mControlsRow.setImageDrawable(getArt());
      this.mControlsRow.setDuration(this.mPlayerAdapter.getDuration());
      this.mControlsRow.setCurrentPosition(getCurrentPosition());
    } while (getHost() == null);
    getHost().notifyPlaybackRowChanged();
  }
  
  @CallSuper
  protected void onPlayCompleted()
  {
    List localList = getPlayerCallbacks();
    if (localList != null)
    {
      int i = 0;
      int j = localList.size();
      while (i < j)
      {
        ((PlaybackGlue.PlayerCallback)localList.get(i)).onPlayCompleted(this);
        i += 1;
      }
    }
  }
  
  @CallSuper
  protected void onPlayStateChanged()
  {
    List localList = getPlayerCallbacks();
    if (localList != null)
    {
      int i = 0;
      int j = localList.size();
      while (i < j)
      {
        ((PlaybackGlue.PlayerCallback)localList.get(i)).onPlayStateChanged(this);
        i += 1;
      }
    }
  }
  
  @CallSuper
  protected void onPreparedStateChanged()
  {
    onUpdateDuration();
    List localList = getPlayerCallbacks();
    if (localList != null)
    {
      int i = 0;
      int j = localList.size();
      while (i < j)
      {
        ((PlaybackGlue.PlayerCallback)localList.get(i)).onPreparedStateChanged(this);
        i += 1;
      }
    }
  }
  
  void onUpdateBufferedProgress()
  {
    if (this.mControlsRow != null) {
      this.mControlsRow.setBufferedPosition(this.mPlayerAdapter.getBufferedPosition());
    }
  }
  
  void onUpdateDuration()
  {
    PlaybackControlsRow localPlaybackControlsRow;
    if (this.mControlsRow != null)
    {
      localPlaybackControlsRow = this.mControlsRow;
      if (!this.mPlayerAdapter.isPrepared()) {
        break label36;
      }
    }
    label36:
    for (long l = this.mPlayerAdapter.getDuration();; l = -1L)
    {
      localPlaybackControlsRow.setDuration(l);
      return;
    }
  }
  
  void onUpdateProgress()
  {
    PlaybackControlsRow localPlaybackControlsRow;
    if (this.mControlsRow != null)
    {
      localPlaybackControlsRow = this.mControlsRow;
      if (!this.mPlayerAdapter.isPrepared()) {
        break label33;
      }
    }
    label33:
    for (long l = getCurrentPosition();; l = -1L)
    {
      localPlaybackControlsRow.setCurrentPosition(l);
      return;
    }
  }
  
  public void pause()
  {
    this.mPlayerAdapter.pause();
  }
  
  public void play()
  {
    this.mPlayerAdapter.play();
  }
  
  public final void seekTo(long paramLong)
  {
    this.mPlayerAdapter.seekTo(paramLong);
  }
  
  public void setArt(Drawable paramDrawable)
  {
    if (this.mCover == paramDrawable) {}
    do
    {
      return;
      this.mCover = paramDrawable;
      this.mControlsRow.setImageDrawable(this.mCover);
    } while (getHost() == null);
    getHost().notifyPlaybackRowChanged();
  }
  
  public void setControlsOverlayAutoHideEnabled(boolean paramBoolean)
  {
    this.mFadeWhenPlaying = paramBoolean;
    if ((!this.mFadeWhenPlaying) && (getHost() != null)) {
      getHost().setControlsOverlayAutoHideEnabled(false);
    }
  }
  
  public void setControlsRow(PlaybackControlsRow paramPlaybackControlsRow)
  {
    this.mControlsRow = paramPlaybackControlsRow;
    this.mControlsRow.setCurrentPosition(-1L);
    this.mControlsRow.setDuration(-1L);
    this.mControlsRow.setBufferedPosition(-1L);
    if (this.mControlsRow.getPrimaryActionsAdapter() == null)
    {
      paramPlaybackControlsRow = new ArrayObjectAdapter(new ControlButtonPresenterSelector());
      onCreatePrimaryActions(paramPlaybackControlsRow);
      this.mControlsRow.setPrimaryActionsAdapter(paramPlaybackControlsRow);
    }
    if (this.mControlsRow.getSecondaryActionsAdapter() == null)
    {
      paramPlaybackControlsRow = new ArrayObjectAdapter(new ControlButtonPresenterSelector());
      onCreateSecondaryActions(paramPlaybackControlsRow);
      getControlsRow().setSecondaryActionsAdapter(paramPlaybackControlsRow);
    }
    updateControlsRow();
  }
  
  public void setPlaybackRowPresenter(PlaybackRowPresenter paramPlaybackRowPresenter)
  {
    this.mControlsRowPresenter = paramPlaybackRowPresenter;
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    if (TextUtils.equals(paramCharSequence, this.mSubtitle)) {}
    do
    {
      return;
      this.mSubtitle = paramCharSequence;
    } while (getHost() == null);
    getHost().notifyPlaybackRowChanged();
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if (TextUtils.equals(paramCharSequence, this.mTitle)) {}
    do
    {
      return;
      this.mTitle = paramCharSequence;
    } while (getHost() == null);
    getHost().notifyPlaybackRowChanged();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/PlaybackBaseControlGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */