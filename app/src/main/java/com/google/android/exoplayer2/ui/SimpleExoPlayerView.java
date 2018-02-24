package com.google.android.exoplayer2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.R.id;
import com.google.android.exoplayer2.R.layout;
import com.google.android.exoplayer2.R.styleable;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer.VideoListener;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer.Output;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Assertions;
import java.util.List;

@TargetApi(16)
public final class SimpleExoPlayerView
  extends FrameLayout
{
  private static final int SURFACE_TYPE_NONE = 0;
  private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
  private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
  private final ImageView artworkView;
  private final ComponentListener componentListener;
  private final AspectRatioFrameLayout contentFrame;
  private final PlaybackControlView controller;
  private int controllerShowTimeoutMs;
  private Bitmap defaultArtwork;
  private final FrameLayout overlayFrameLayout;
  private SimpleExoPlayer player;
  private final View shutterView;
  private final SubtitleView subtitleView;
  private final View surfaceView;
  private boolean useArtwork;
  private boolean useController;
  
  public SimpleExoPlayerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SimpleExoPlayerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SimpleExoPlayerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    int n = R.layout.exo_simple_player_view;
    boolean bool2 = true;
    int m = 0;
    boolean bool1 = true;
    int k = 1;
    int j = 0;
    paramInt = 5000;
    int i = n;
    Object localObject;
    if (paramAttributeSet != null) {
      localObject = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.SimpleExoPlayerView, 0, 0);
    }
    for (;;)
    {
      try
      {
        i = ((TypedArray)localObject).getResourceId(R.styleable.SimpleExoPlayerView_player_layout_id, n);
        bool2 = ((TypedArray)localObject).getBoolean(R.styleable.SimpleExoPlayerView_use_artwork, true);
        m = ((TypedArray)localObject).getResourceId(R.styleable.SimpleExoPlayerView_default_artwork, 0);
        bool1 = ((TypedArray)localObject).getBoolean(R.styleable.SimpleExoPlayerView_use_controller, true);
        k = ((TypedArray)localObject).getInt(R.styleable.SimpleExoPlayerView_surface_type, 1);
        j = ((TypedArray)localObject).getInt(R.styleable.SimpleExoPlayerView_resize_mode, 0);
        paramInt = ((TypedArray)localObject).getInt(R.styleable.SimpleExoPlayerView_show_timeout, 5000);
        ((TypedArray)localObject).recycle();
        LayoutInflater.from(paramContext).inflate(i, this);
        this.componentListener = new ComponentListener(null);
        setDescendantFocusability(262144);
        this.contentFrame = ((AspectRatioFrameLayout)findViewById(R.id.exo_content_frame));
        if (this.contentFrame != null) {
          setResizeModeRaw(this.contentFrame, j);
        }
        this.shutterView = findViewById(R.id.exo_shutter);
        if ((this.contentFrame == null) || (k == 0)) {
          break label508;
        }
        ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-1, -1);
        if (k == 2)
        {
          localObject = new TextureView(paramContext);
          this.surfaceView = ((View)localObject);
          this.surfaceView.setLayoutParams(localLayoutParams);
          this.contentFrame.addView(this.surfaceView, 0);
          this.overlayFrameLayout = ((FrameLayout)findViewById(R.id.exo_overlay));
          this.artworkView = ((ImageView)findViewById(R.id.exo_artwork));
          if ((!bool2) || (this.artworkView == null)) {
            break label516;
          }
          bool2 = true;
          this.useArtwork = bool2;
          if (m != 0) {
            this.defaultArtwork = BitmapFactory.decodeResource(paramContext.getResources(), m);
          }
          this.subtitleView = ((SubtitleView)findViewById(R.id.exo_subtitles));
          if (this.subtitleView != null)
          {
            this.subtitleView.setUserDefaultStyle();
            this.subtitleView.setUserDefaultTextSize();
          }
          localObject = findViewById(R.id.exo_controller_placeholder);
          if (localObject == null) {
            break label522;
          }
          this.controller = new PlaybackControlView(paramContext, paramAttributeSet);
          this.controller.setLayoutParams(((View)localObject).getLayoutParams());
          paramContext = (ViewGroup)((View)localObject).getParent();
          i = paramContext.indexOfChild((View)localObject);
          paramContext.removeView((View)localObject);
          paramContext.addView(this.controller, i);
          if (this.controller == null) {
            break label530;
          }
          this.controllerShowTimeoutMs = paramInt;
          if ((!bool1) || (this.controller == null)) {
            break label535;
          }
          bool1 = true;
          this.useController = bool1;
          hideController();
          return;
        }
      }
      finally
      {
        ((TypedArray)localObject).recycle();
      }
      localObject = new SurfaceView(paramContext);
      continue;
      label508:
      this.surfaceView = null;
      continue;
      label516:
      bool2 = false;
      continue;
      label522:
      this.controller = null;
      continue;
      label530:
      paramInt = 0;
      continue;
      label535:
      bool1 = false;
    }
  }
  
  private void hideArtwork()
  {
    if (this.artworkView != null)
    {
      this.artworkView.setImageResource(17170445);
      this.artworkView.setVisibility(4);
    }
  }
  
  private void maybeShowController(boolean paramBoolean)
  {
    int k = 0;
    if ((!this.useController) || (this.player == null)) {
      return;
    }
    int i = this.player.getPlaybackState();
    label48:
    int j;
    label70:
    PlaybackControlView localPlaybackControlView;
    if ((i == 1) || (i == 4) || (!this.player.getPlayWhenReady()))
    {
      i = 1;
      if ((!this.controller.isVisible()) || (this.controller.getShowTimeoutMs() > 0)) {
        break label112;
      }
      j = 1;
      localPlaybackControlView = this.controller;
      if (i == 0) {
        break label117;
      }
    }
    for (;;)
    {
      localPlaybackControlView.setShowTimeoutMs(k);
      if ((!paramBoolean) && (i == 0) && (j == 0)) {
        break;
      }
      this.controller.show();
      return;
      i = 0;
      break label48;
      label112:
      j = 0;
      break label70;
      label117:
      k = this.controllerShowTimeoutMs;
    }
  }
  
  private boolean setArtworkFromBitmap(Bitmap paramBitmap)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramBitmap != null)
    {
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      bool1 = bool2;
      if (i > 0)
      {
        bool1 = bool2;
        if (j > 0)
        {
          if (this.contentFrame != null) {
            this.contentFrame.setAspectRatio(i / j);
          }
          this.artworkView.setImageBitmap(paramBitmap);
          this.artworkView.setVisibility(0);
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private boolean setArtworkFromMetadata(Metadata paramMetadata)
  {
    boolean bool2 = false;
    int i = 0;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i < paramMetadata.length())
      {
        Metadata.Entry localEntry = paramMetadata.get(i);
        if ((localEntry instanceof ApicFrame))
        {
          paramMetadata = ((ApicFrame)localEntry).pictureData;
          bool1 = setArtworkFromBitmap(BitmapFactory.decodeByteArray(paramMetadata, 0, paramMetadata.length));
        }
      }
      else
      {
        return bool1;
      }
      i += 1;
    }
  }
  
  private static void setResizeModeRaw(AspectRatioFrameLayout paramAspectRatioFrameLayout, int paramInt)
  {
    paramAspectRatioFrameLayout.setResizeMode(paramInt);
  }
  
  private void updateForCurrentTrackSelections()
  {
    if (this.player == null) {}
    label149:
    label156:
    do
    {
      return;
      TrackSelectionArray localTrackSelectionArray = this.player.getCurrentTrackSelections();
      int i = 0;
      while (i < localTrackSelectionArray.length)
      {
        if ((this.player.getRendererType(i) == 2) && (localTrackSelectionArray.get(i) != null))
        {
          hideArtwork();
          return;
        }
        i += 1;
      }
      if (this.shutterView != null) {
        this.shutterView.setVisibility(0);
      }
      if (!this.useArtwork) {
        break;
      }
      i = 0;
      for (;;)
      {
        if (i >= localTrackSelectionArray.length) {
          break label156;
        }
        TrackSelection localTrackSelection = localTrackSelectionArray.get(i);
        if (localTrackSelection != null)
        {
          int j = 0;
          for (;;)
          {
            if (j >= localTrackSelection.length()) {
              break label149;
            }
            Metadata localMetadata = localTrackSelection.getFormat(j).metadata;
            if ((localMetadata != null) && (setArtworkFromMetadata(localMetadata))) {
              break;
            }
            j += 1;
          }
        }
        i += 1;
      }
    } while (setArtworkFromBitmap(this.defaultArtwork));
    hideArtwork();
  }
  
  public boolean dispatchMediaKeyEvent(KeyEvent paramKeyEvent)
  {
    return (this.useController) && (this.controller.dispatchMediaKeyEvent(paramKeyEvent));
  }
  
  public int getControllerShowTimeoutMs()
  {
    return this.controllerShowTimeoutMs;
  }
  
  public Bitmap getDefaultArtwork()
  {
    return this.defaultArtwork;
  }
  
  public FrameLayout getOverlayFrameLayout()
  {
    return this.overlayFrameLayout;
  }
  
  public SimpleExoPlayer getPlayer()
  {
    return this.player;
  }
  
  public SubtitleView getSubtitleView()
  {
    return this.subtitleView;
  }
  
  public boolean getUseArtwork()
  {
    return this.useArtwork;
  }
  
  public boolean getUseController()
  {
    return this.useController;
  }
  
  public View getVideoSurfaceView()
  {
    return this.surfaceView;
  }
  
  public void hideController()
  {
    if (this.controller != null) {
      this.controller.hide();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((!this.useController) || (this.player == null) || (paramMotionEvent.getActionMasked() != 0)) {
      return false;
    }
    if (this.controller.isVisible())
    {
      this.controller.hide();
      return true;
    }
    maybeShowController(true);
    return true;
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    if ((!this.useController) || (this.player == null)) {
      return false;
    }
    maybeShowController(true);
    return true;
  }
  
  public void setControllerShowTimeoutMs(int paramInt)
  {
    if (this.controller != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.controllerShowTimeoutMs = paramInt;
      return;
    }
  }
  
  public void setControllerVisibilityListener(PlaybackControlView.VisibilityListener paramVisibilityListener)
  {
    if (this.controller != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.controller.setVisibilityListener(paramVisibilityListener);
      return;
    }
  }
  
  public void setDefaultArtwork(Bitmap paramBitmap)
  {
    if (this.defaultArtwork != paramBitmap)
    {
      this.defaultArtwork = paramBitmap;
      updateForCurrentTrackSelections();
    }
  }
  
  public void setFastForwardIncrementMs(int paramInt)
  {
    if (this.controller != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.controller.setFastForwardIncrementMs(paramInt);
      return;
    }
  }
  
  public void setPlayer(SimpleExoPlayer paramSimpleExoPlayer)
  {
    if (this.player == paramSimpleExoPlayer) {
      return;
    }
    if (this.player != null)
    {
      this.player.setTextOutput(null);
      this.player.setVideoListener(null);
      this.player.removeListener(this.componentListener);
      this.player.setVideoSurface(null);
    }
    this.player = paramSimpleExoPlayer;
    if (this.useController) {
      this.controller.setPlayer(paramSimpleExoPlayer);
    }
    if (this.shutterView != null) {
      this.shutterView.setVisibility(0);
    }
    if (paramSimpleExoPlayer != null)
    {
      if ((this.surfaceView instanceof TextureView)) {
        paramSimpleExoPlayer.setVideoTextureView((TextureView)this.surfaceView);
      }
      for (;;)
      {
        paramSimpleExoPlayer.setVideoListener(this.componentListener);
        paramSimpleExoPlayer.addListener(this.componentListener);
        paramSimpleExoPlayer.setTextOutput(this.componentListener);
        maybeShowController(false);
        updateForCurrentTrackSelections();
        return;
        if ((this.surfaceView instanceof SurfaceView)) {
          paramSimpleExoPlayer.setVideoSurfaceView((SurfaceView)this.surfaceView);
        }
      }
    }
    hideController();
    hideArtwork();
  }
  
  public void setResizeMode(int paramInt)
  {
    if (this.contentFrame != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.contentFrame.setResizeMode(paramInt);
      return;
    }
  }
  
  public void setRewindIncrementMs(int paramInt)
  {
    if (this.controller != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.controller.setRewindIncrementMs(paramInt);
      return;
    }
  }
  
  public void setSeekDispatcher(PlaybackControlView.SeekDispatcher paramSeekDispatcher)
  {
    if (this.controller != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.controller.setSeekDispatcher(paramSeekDispatcher);
      return;
    }
  }
  
  public void setUseArtwork(boolean paramBoolean)
  {
    if ((!paramBoolean) || (this.artworkView != null)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (this.useArtwork != paramBoolean)
      {
        this.useArtwork = paramBoolean;
        updateForCurrentTrackSelections();
      }
      return;
    }
  }
  
  public void setUseController(boolean paramBoolean)
  {
    boolean bool;
    if ((!paramBoolean) || (this.controller != null))
    {
      bool = true;
      Assertions.checkState(bool);
      if (this.useController != paramBoolean) {
        break label31;
      }
    }
    label31:
    do
    {
      return;
      bool = false;
      break;
      this.useController = paramBoolean;
      if (paramBoolean)
      {
        this.controller.setPlayer(this.player);
        return;
      }
    } while (this.controller == null);
    this.controller.hide();
    this.controller.setPlayer(null);
  }
  
  public void showController()
  {
    if (this.useController) {
      maybeShowController(true);
    }
  }
  
  private final class ComponentListener
    implements SimpleExoPlayer.VideoListener, TextRenderer.Output, ExoPlayer.EventListener
  {
    private ComponentListener() {}
    
    public void onCues(List<Cue> paramList)
    {
      if (SimpleExoPlayerView.this.subtitleView != null) {
        SimpleExoPlayerView.this.subtitleView.onCues(paramList);
      }
    }
    
    public void onLoadingChanged(boolean paramBoolean) {}
    
    public void onPlayerError(ExoPlaybackException paramExoPlaybackException) {}
    
    public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
    {
      SimpleExoPlayerView.this.maybeShowController(false);
    }
    
    public void onPositionDiscontinuity() {}
    
    public void onRenderedFirstFrame()
    {
      if (SimpleExoPlayerView.this.shutterView != null) {
        SimpleExoPlayerView.this.shutterView.setVisibility(4);
      }
    }
    
    public void onTimelineChanged(Timeline paramTimeline, Object paramObject) {}
    
    public void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
    {
      SimpleExoPlayerView.this.updateForCurrentTrackSelections();
    }
    
    public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      if (SimpleExoPlayerView.this.contentFrame != null) {
        if (paramInt2 != 0) {
          break label30;
        }
      }
      label30:
      for (paramFloat = 1.0F;; paramFloat = paramInt1 * paramFloat / paramInt2)
      {
        SimpleExoPlayerView.this.contentFrame.setAspectRatio(paramFloat);
        return;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ui/SimpleExoPlayerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */