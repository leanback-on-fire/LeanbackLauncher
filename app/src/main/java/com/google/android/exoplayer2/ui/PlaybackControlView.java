package com.google.android.exoplayer2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.R.id;
import com.google.android.exoplayer2.R.layout;
import com.google.android.exoplayer2.R.styleable;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Util;
import java.util.Formatter;
import java.util.Locale;

public class PlaybackControlView
  extends FrameLayout
{
  public static final int DEFAULT_FAST_FORWARD_MS = 15000;
  public static final int DEFAULT_REWIND_MS = 5000;
  public static final SeekDispatcher DEFAULT_SEEK_DISPATCHER = new SeekDispatcher()
  {
    public boolean dispatchSeek(ExoPlayer paramAnonymousExoPlayer, int paramAnonymousInt, long paramAnonymousLong)
    {
      paramAnonymousExoPlayer.seekTo(paramAnonymousInt, paramAnonymousLong);
      return true;
    }
  };
  public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;
  private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000L;
  private static final int PROGRESS_BAR_MAX = 1000;
  private final ComponentListener componentListener;
  private final Timeline.Window currentWindow;
  private boolean dragging;
  private final TextView durationView;
  private final View fastForwardButton;
  private int fastForwardMs;
  private final StringBuilder formatBuilder;
  private final Formatter formatter;
  private final Runnable hideAction = new Runnable()
  {
    public void run()
    {
      PlaybackControlView.this.hide();
    }
  };
  private long hideAtMs;
  private boolean isAttachedToWindow;
  private final View nextButton;
  private final View pauseButton;
  private final View playButton;
  private ExoPlayer player;
  private final TextView positionView;
  private final View previousButton;
  private final SeekBar progressBar;
  private final View rewindButton;
  private int rewindMs;
  private SeekDispatcher seekDispatcher;
  private int showTimeoutMs;
  private final Runnable updateProgressAction = new Runnable()
  {
    public void run()
    {
      PlaybackControlView.this.updateProgress();
    }
  };
  private VisibilityListener visibilityListener;
  
  public PlaybackControlView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PlaybackControlView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public PlaybackControlView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    int i = R.layout.exo_playback_control_view;
    this.rewindMs = 5000;
    this.fastForwardMs = 15000;
    this.showTimeoutMs = 5000;
    paramInt = i;
    if (paramAttributeSet != null) {
      paramAttributeSet = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.PlaybackControlView, 0, 0);
    }
    try
    {
      this.rewindMs = paramAttributeSet.getInt(R.styleable.PlaybackControlView_rewind_increment, this.rewindMs);
      this.fastForwardMs = paramAttributeSet.getInt(R.styleable.PlaybackControlView_fastforward_increment, this.fastForwardMs);
      this.showTimeoutMs = paramAttributeSet.getInt(R.styleable.PlaybackControlView_show_timeout, this.showTimeoutMs);
      paramInt = paramAttributeSet.getResourceId(R.styleable.PlaybackControlView_controller_layout_id, i);
      paramAttributeSet.recycle();
      this.currentWindow = new Timeline.Window();
      this.formatBuilder = new StringBuilder();
      this.formatter = new Formatter(this.formatBuilder, Locale.getDefault());
      this.componentListener = new ComponentListener(null);
      this.seekDispatcher = DEFAULT_SEEK_DISPATCHER;
      LayoutInflater.from(paramContext).inflate(paramInt, this);
      setDescendantFocusability(262144);
      this.durationView = ((TextView)findViewById(R.id.exo_duration));
      this.positionView = ((TextView)findViewById(R.id.exo_position));
      this.progressBar = ((SeekBar)findViewById(R.id.exo_progress));
      if (this.progressBar != null)
      {
        this.progressBar.setOnSeekBarChangeListener(this.componentListener);
        this.progressBar.setMax(1000);
      }
      this.playButton = findViewById(R.id.exo_play);
      if (this.playButton != null) {
        this.playButton.setOnClickListener(this.componentListener);
      }
      this.pauseButton = findViewById(R.id.exo_pause);
      if (this.pauseButton != null) {
        this.pauseButton.setOnClickListener(this.componentListener);
      }
      this.previousButton = findViewById(R.id.exo_prev);
      if (this.previousButton != null) {
        this.previousButton.setOnClickListener(this.componentListener);
      }
      this.nextButton = findViewById(R.id.exo_next);
      if (this.nextButton != null) {
        this.nextButton.setOnClickListener(this.componentListener);
      }
      this.rewindButton = findViewById(R.id.exo_rew);
      if (this.rewindButton != null) {
        this.rewindButton.setOnClickListener(this.componentListener);
      }
      this.fastForwardButton = findViewById(R.id.exo_ffwd);
      if (this.fastForwardButton != null) {
        this.fastForwardButton.setOnClickListener(this.componentListener);
      }
      return;
    }
    finally
    {
      paramAttributeSet.recycle();
    }
  }
  
  private void fastForward()
  {
    if (this.fastForwardMs <= 0) {
      return;
    }
    seekTo(Math.min(this.player.getCurrentPosition() + this.fastForwardMs, this.player.getDuration()));
  }
  
  private void hideAfterTimeout()
  {
    removeCallbacks(this.hideAction);
    if (this.showTimeoutMs > 0)
    {
      this.hideAtMs = (SystemClock.uptimeMillis() + this.showTimeoutMs);
      if (this.isAttachedToWindow) {
        postDelayed(this.hideAction, this.showTimeoutMs);
      }
      return;
    }
    this.hideAtMs = -9223372036854775807L;
  }
  
  private static boolean isHandledMediaKey(int paramInt)
  {
    return (paramInt == 90) || (paramInt == 89) || (paramInt == 85) || (paramInt == 126) || (paramInt == 127) || (paramInt == 87) || (paramInt == 88);
  }
  
  private void next()
  {
    Timeline localTimeline = this.player.getCurrentTimeline();
    if (localTimeline.isEmpty()) {}
    int i;
    do
    {
      return;
      i = this.player.getCurrentWindowIndex();
      if (i < localTimeline.getWindowCount() - 1)
      {
        seekTo(i + 1, -9223372036854775807L);
        return;
      }
    } while (!localTimeline.getWindow(i, this.currentWindow, false).isDynamic);
    seekTo(i, -9223372036854775807L);
  }
  
  private long positionValue(int paramInt)
  {
    if (this.player == null) {}
    for (long l = -9223372036854775807L; l == -9223372036854775807L; l = this.player.getDuration()) {
      return 0L;
    }
    return paramInt * l / 1000L;
  }
  
  private void previous()
  {
    Timeline localTimeline = this.player.getCurrentTimeline();
    if (localTimeline.isEmpty()) {
      return;
    }
    int i = this.player.getCurrentWindowIndex();
    localTimeline.getWindow(i, this.currentWindow);
    if ((i > 0) && ((this.player.getCurrentPosition() <= 3000L) || ((this.currentWindow.isDynamic) && (!this.currentWindow.isSeekable))))
    {
      seekTo(i - 1, -9223372036854775807L);
      return;
    }
    seekTo(0L);
  }
  
  private int progressBarValue(long paramLong)
  {
    if (this.player == null) {}
    for (long l = -9223372036854775807L; (l == -9223372036854775807L) || (l == 0L); l = this.player.getDuration()) {
      return 0;
    }
    return (int)(1000L * paramLong / l);
  }
  
  private void requestPlayPauseFocus()
  {
    int i;
    if ((this.player != null) && (this.player.getPlayWhenReady()))
    {
      i = 1;
      if ((i != 0) || (this.playButton == null)) {
        break label46;
      }
      this.playButton.requestFocus();
    }
    label46:
    while ((i == 0) || (this.pauseButton == null))
    {
      return;
      i = 0;
      break;
    }
    this.pauseButton.requestFocus();
  }
  
  private void rewind()
  {
    if (this.rewindMs <= 0) {
      return;
    }
    seekTo(Math.max(this.player.getCurrentPosition() - this.rewindMs, 0L));
  }
  
  private void seekTo(int paramInt, long paramLong)
  {
    if (!this.seekDispatcher.dispatchSeek(this.player, paramInt, paramLong)) {
      updateProgress();
    }
  }
  
  private void seekTo(long paramLong)
  {
    seekTo(this.player.getCurrentWindowIndex(), paramLong);
  }
  
  private void setButtonEnabled(boolean paramBoolean, View paramView)
  {
    if (paramView == null) {
      return;
    }
    paramView.setEnabled(paramBoolean);
    if (Util.SDK_INT >= 11)
    {
      if (paramBoolean) {}
      for (float f = 1.0F;; f = 0.3F)
      {
        setViewAlphaV11(paramView, f);
        paramView.setVisibility(0);
        return;
      }
    }
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      paramView.setVisibility(i);
      return;
    }
  }
  
  @TargetApi(11)
  private void setViewAlphaV11(View paramView, float paramFloat)
  {
    paramView.setAlpha(paramFloat);
  }
  
  private String stringForTime(long paramLong)
  {
    long l1 = paramLong;
    if (paramLong == -9223372036854775807L) {
      l1 = 0L;
    }
    long l2 = (500L + l1) / 1000L;
    paramLong = l2 % 60L;
    l1 = l2 / 60L % 60L;
    l2 /= 3600L;
    this.formatBuilder.setLength(0);
    if (l2 > 0L) {
      return this.formatter.format("%d:%02d:%02d", new Object[] { Long.valueOf(l2), Long.valueOf(l1), Long.valueOf(paramLong) }).toString();
    }
    return this.formatter.format("%02d:%02d", new Object[] { Long.valueOf(l1), Long.valueOf(paramLong) }).toString();
  }
  
  private void updateAll()
  {
    updatePlayPauseButton();
    updateNavigation();
    updateProgress();
  }
  
  private void updateNavigation()
  {
    boolean bool4 = true;
    if ((!isVisible()) || (!this.isAttachedToWindow)) {
      return;
    }
    Timeline localTimeline;
    label36:
    int i;
    label51:
    boolean bool3;
    boolean bool2;
    if (this.player != null)
    {
      localTimeline = this.player.getCurrentTimeline();
      if ((localTimeline == null) || (localTimeline.isEmpty())) {
        break label224;
      }
      i = 1;
      bool3 = false;
      bool2 = false;
      bool1 = false;
      if (i != 0)
      {
        i = this.player.getCurrentWindowIndex();
        localTimeline.getWindow(i, this.currentWindow);
        bool3 = this.currentWindow.isSeekable;
        if ((i <= 0) && (!bool3) && (this.currentWindow.isDynamic)) {
          break label229;
        }
        bool2 = true;
        label113:
        if ((i >= localTimeline.getWindowCount() - 1) && (!this.currentWindow.isDynamic)) {
          break label234;
        }
        bool1 = true;
      }
      label136:
      setButtonEnabled(bool2, this.previousButton);
      setButtonEnabled(bool1, this.nextButton);
      if ((this.fastForwardMs <= 0) || (!bool3)) {
        break label239;
      }
      bool1 = true;
      label168:
      setButtonEnabled(bool1, this.fastForwardButton);
      if ((this.rewindMs <= 0) || (!bool3)) {
        break label244;
      }
    }
    label224:
    label229:
    label234:
    label239:
    label244:
    for (boolean bool1 = bool4;; bool1 = false)
    {
      setButtonEnabled(bool1, this.rewindButton);
      if (this.progressBar == null) {
        break;
      }
      this.progressBar.setEnabled(bool3);
      return;
      localTimeline = null;
      break label36;
      i = 0;
      break label51;
      bool2 = false;
      break label113;
      bool1 = false;
      break label136;
      bool1 = false;
      break label168;
    }
  }
  
  private void updatePlayPauseButton()
  {
    int m = 8;
    int n = 1;
    if ((!isVisible()) || (!this.isAttachedToWindow)) {
      return;
    }
    int i = 0;
    int j;
    label45:
    label68:
    int k;
    View localView;
    if ((this.player != null) && (this.player.getPlayWhenReady()))
    {
      j = 1;
      if (this.playButton != null)
      {
        if ((j == 0) || (!this.playButton.isFocused())) {
          break label156;
        }
        i = 1;
        k = 0x0 | i;
        localView = this.playButton;
        if (j == 0) {
          break label161;
        }
        i = 8;
        label85:
        localView.setVisibility(i);
        i = k;
      }
      k = i;
      if (this.pauseButton != null)
      {
        if ((j != 0) || (!this.pauseButton.isFocused())) {
          break label166;
        }
        k = n;
        label119:
        k = i | k;
        localView = this.pauseButton;
        if (j != 0) {
          break label171;
        }
      }
    }
    label156:
    label161:
    label166:
    label171:
    for (i = m;; i = 0)
    {
      localView.setVisibility(i);
      if (k == 0) {
        break;
      }
      requestPlayPauseFocus();
      return;
      j = 0;
      break label45;
      i = 0;
      break label68;
      i = 0;
      break label85;
      k = 0;
      break label119;
    }
  }
  
  private void updateProgress()
  {
    if ((!isVisible()) || (!this.isAttachedToWindow)) {
      return;
    }
    long l2;
    label25:
    label34:
    label116:
    int i;
    if (this.player == null)
    {
      l2 = 0L;
      if (this.player != null) {
        break label229;
      }
      l1 = 0L;
      if (this.durationView != null) {
        this.durationView.setText(stringForTime(l2));
      }
      if ((this.positionView != null) && (!this.dragging)) {
        this.positionView.setText(stringForTime(l1));
      }
      if (this.progressBar != null)
      {
        if (!this.dragging) {
          this.progressBar.setProgress(progressBarValue(l1));
        }
        if (this.player != null) {
          break label242;
        }
        l2 = 0L;
        this.progressBar.setSecondaryProgress(progressBarValue(l2));
      }
      removeCallbacks(this.updateProgressAction);
      if (this.player != null) {
        break label256;
      }
      i = 1;
      label147:
      if ((i == 1) || (i == 4)) {
        break label267;
      }
      if ((!this.player.getPlayWhenReady()) || (i != 3)) {
        break label269;
      }
      l2 = 1000L - l1 % 1000L;
      l1 = l2;
      if (l2 >= 200L) {}
    }
    label229:
    label242:
    label256:
    label267:
    label269:
    for (long l1 = l2 + 1000L;; l1 = 1000L)
    {
      postDelayed(this.updateProgressAction, l1);
      return;
      l2 = this.player.getDuration();
      break label25;
      l1 = this.player.getCurrentPosition();
      break label34;
      l2 = this.player.getBufferedPosition();
      break label116;
      i = this.player.getPlaybackState();
      break label147;
      break;
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((dispatchMediaKeyEvent(paramKeyEvent)) || (super.dispatchKeyEvent(paramKeyEvent))) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool) {
        show();
      }
      return bool;
    }
  }
  
  public boolean dispatchMediaKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool = false;
    int i = paramKeyEvent.getKeyCode();
    if ((this.player == null) || (!isHandledMediaKey(i))) {
      return false;
    }
    if (paramKeyEvent.getAction() == 0) {
      switch (i)
      {
      }
    }
    for (;;)
    {
      show();
      return true;
      fastForward();
      continue;
      rewind();
      continue;
      paramKeyEvent = this.player;
      if (!this.player.getPlayWhenReady()) {
        bool = true;
      }
      paramKeyEvent.setPlayWhenReady(bool);
      continue;
      this.player.setPlayWhenReady(true);
      continue;
      this.player.setPlayWhenReady(false);
      continue;
      next();
      continue;
      previous();
    }
  }
  
  public ExoPlayer getPlayer()
  {
    return this.player;
  }
  
  public int getShowTimeoutMs()
  {
    return this.showTimeoutMs;
  }
  
  public void hide()
  {
    if (isVisible())
    {
      setVisibility(8);
      if (this.visibilityListener != null) {
        this.visibilityListener.onVisibilityChange(getVisibility());
      }
      removeCallbacks(this.updateProgressAction);
      removeCallbacks(this.hideAction);
      this.hideAtMs = -9223372036854775807L;
    }
  }
  
  public boolean isVisible()
  {
    return getVisibility() == 0;
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.isAttachedToWindow = true;
    long l;
    if (this.hideAtMs != -9223372036854775807L)
    {
      l = this.hideAtMs - SystemClock.uptimeMillis();
      if (l > 0L) {
        break label44;
      }
      hide();
    }
    for (;;)
    {
      updateAll();
      return;
      label44:
      postDelayed(this.hideAction, l);
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.isAttachedToWindow = false;
    removeCallbacks(this.updateProgressAction);
    removeCallbacks(this.hideAction);
  }
  
  public void setFastForwardIncrementMs(int paramInt)
  {
    this.fastForwardMs = paramInt;
    updateNavigation();
  }
  
  public void setPlayer(ExoPlayer paramExoPlayer)
  {
    if (this.player == paramExoPlayer) {
      return;
    }
    if (this.player != null) {
      this.player.removeListener(this.componentListener);
    }
    this.player = paramExoPlayer;
    if (paramExoPlayer != null) {
      paramExoPlayer.addListener(this.componentListener);
    }
    updateAll();
  }
  
  public void setRewindIncrementMs(int paramInt)
  {
    this.rewindMs = paramInt;
    updateNavigation();
  }
  
  public void setSeekDispatcher(SeekDispatcher paramSeekDispatcher)
  {
    SeekDispatcher localSeekDispatcher = paramSeekDispatcher;
    if (paramSeekDispatcher == null) {
      localSeekDispatcher = DEFAULT_SEEK_DISPATCHER;
    }
    this.seekDispatcher = localSeekDispatcher;
  }
  
  public void setShowTimeoutMs(int paramInt)
  {
    this.showTimeoutMs = paramInt;
  }
  
  public void setVisibilityListener(VisibilityListener paramVisibilityListener)
  {
    this.visibilityListener = paramVisibilityListener;
  }
  
  public void show()
  {
    if (!isVisible())
    {
      setVisibility(0);
      if (this.visibilityListener != null) {
        this.visibilityListener.onVisibilityChange(getVisibility());
      }
      updateAll();
      requestPlayPauseFocus();
    }
    hideAfterTimeout();
  }
  
  private final class ComponentListener
    implements ExoPlayer.EventListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener
  {
    private ComponentListener() {}
    
    public void onClick(View paramView)
    {
      if (PlaybackControlView.this.player != null)
      {
        if (PlaybackControlView.this.nextButton != paramView) {
          break label36;
        }
        PlaybackControlView.this.next();
      }
      for (;;)
      {
        PlaybackControlView.this.hideAfterTimeout();
        return;
        label36:
        if (PlaybackControlView.this.previousButton == paramView) {
          PlaybackControlView.this.previous();
        } else if (PlaybackControlView.this.fastForwardButton == paramView) {
          PlaybackControlView.this.fastForward();
        } else if (PlaybackControlView.this.rewindButton == paramView) {
          PlaybackControlView.this.rewind();
        } else if (PlaybackControlView.this.playButton == paramView) {
          PlaybackControlView.this.player.setPlayWhenReady(true);
        } else if (PlaybackControlView.this.pauseButton == paramView) {
          PlaybackControlView.this.player.setPlayWhenReady(false);
        }
      }
    }
    
    public void onLoadingChanged(boolean paramBoolean) {}
    
    public void onPlayerError(ExoPlaybackException paramExoPlaybackException) {}
    
    public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
    {
      PlaybackControlView.this.updatePlayPauseButton();
      PlaybackControlView.this.updateProgress();
    }
    
    public void onPositionDiscontinuity()
    {
      PlaybackControlView.this.updateNavigation();
      PlaybackControlView.this.updateProgress();
    }
    
    public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        long l = PlaybackControlView.this.positionValue(paramInt);
        if (PlaybackControlView.this.positionView != null) {
          PlaybackControlView.this.positionView.setText(PlaybackControlView.this.stringForTime(l));
        }
        if ((PlaybackControlView.this.player != null) && (!PlaybackControlView.this.dragging)) {
          PlaybackControlView.this.seekTo(l);
        }
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramSeekBar)
    {
      PlaybackControlView.this.removeCallbacks(PlaybackControlView.this.hideAction);
      PlaybackControlView.access$302(PlaybackControlView.this, true);
    }
    
    public void onStopTrackingTouch(SeekBar paramSeekBar)
    {
      PlaybackControlView.access$302(PlaybackControlView.this, false);
      if (PlaybackControlView.this.player != null) {
        PlaybackControlView.this.seekTo(PlaybackControlView.access$400(PlaybackControlView.this, paramSeekBar.getProgress()));
      }
      PlaybackControlView.this.hideAfterTimeout();
    }
    
    public void onTimelineChanged(Timeline paramTimeline, Object paramObject)
    {
      PlaybackControlView.this.updateNavigation();
      PlaybackControlView.this.updateProgress();
    }
    
    public void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray) {}
  }
  
  public static abstract interface SeekDispatcher
  {
    public abstract boolean dispatchSeek(ExoPlayer paramExoPlayer, int paramInt, long paramLong);
  }
  
  public static abstract interface VisibilityListener
  {
    public abstract void onVisibilityChange(int paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ui/PlaybackControlView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */