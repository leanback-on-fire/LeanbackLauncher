package com.google.android.tvlauncher.instantvideo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer.VideoCallback;
import com.google.android.tvlauncher.instantvideo.media.impl.ExoPlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.InstantVideoPreloadManager;

public class InstantVideoView
  extends FrameLayout
{
  private static final boolean DEBUG = false;
  private static final long FADE_OUT_DURATION_MS = 2000L;
  private static final String TAG = "InstantVideoView";
  private static final int VIDEO_IDLE = 0;
  private static final int VIDEO_PREPARING = 1;
  private static final int VIDEO_SHOWN = 2;
  private ImageView mImageView;
  private ViewPropertyAnimator mImageViewFadeOut;
  private MediaPlayer mPlayer;
  private Runnable mStopVideoRunnable = new Runnable()
  {
    public void run()
    {
      InstantVideoView.this.stopVideoInternal();
    }
  };
  private boolean mVideoStarted;
  private Uri mVideoUri;
  private View mVideoView;
  private float mVolume = 1.0F;
  
  public InstantVideoView(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public InstantVideoView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public InstantVideoView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setImageViewEnabled(true);
    setDisplayedChild(0);
  }
  
  private void setDisplayedChild(int paramInt)
  {
    if (paramInt == 0)
    {
      if (this.mImageViewFadeOut != null)
      {
        this.mImageViewFadeOut.cancel();
        this.mImageViewFadeOut = null;
      }
      if (this.mImageView != null)
      {
        this.mImageView.setVisibility(0);
        this.mImageView.setAlpha(1.0F);
      }
      if (this.mVideoView != null) {
        this.mVideoView.setVisibility(8);
      }
    }
    do
    {
      do
      {
        return;
        if (paramInt != 1) {
          break;
        }
      } while (this.mVideoView == null);
      this.mVideoView.setVisibility(0);
      this.mVideoView.setAlpha(0.0F);
      return;
      if (this.mImageView != null)
      {
        this.mImageViewFadeOut = this.mImageView.animate();
        this.mImageViewFadeOut.alpha(0.0F).setDuration(2000L).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            InstantVideoView.access$302(InstantVideoView.this, null);
            InstantVideoView.this.mImageView.setVisibility(8);
          }
        }).start();
      }
    } while (this.mVideoView == null);
    this.mVideoView.setVisibility(0);
    this.mVideoView.setAlpha(1.0F);
  }
  
  private void stopVideoInternal()
  {
    removeCallbacks(this.mStopVideoRunnable);
    if (this.mVideoView != null)
    {
      this.mPlayer.setVideoCallback(null);
      this.mPlayer.stop();
      InstantVideoPreloadManager.getInstance(getContext()).recyclePlayer(this.mPlayer, this.mVideoUri);
      this.mPlayer = null;
      removeView(this.mVideoView);
      this.mVideoView = null;
    }
  }
  
  public int getCurrentPosition()
  {
    return this.mPlayer.getCurrentPosition();
  }
  
  public ImageView getImageView()
  {
    return this.mImageView;
  }
  
  public Uri getVideoUri()
  {
    return this.mVideoUri;
  }
  
  public boolean isPlaying()
  {
    return this.mVideoStarted;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    stop();
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 != 0) && (paramInt2 != 0) && (this.mPlayer != null)) {
      this.mPlayer.setDisplaySize(paramInt1, paramInt2);
    }
  }
  
  public void seekTo(int paramInt)
  {
    this.mPlayer.seekTo(paramInt);
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    this.mImageView.setImageDrawable(paramDrawable);
  }
  
  public void setImageViewEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.mImageView == null))
    {
      this.mImageView = new ImageView(getContext());
      addView(this.mImageView, new FrameLayout.LayoutParams(-1, -1, 17));
    }
    while ((paramBoolean) || (this.mImageView == null)) {
      return;
    }
    if (this.mImageViewFadeOut != null)
    {
      this.mImageViewFadeOut.cancel();
      this.mImageViewFadeOut = null;
    }
    removeView(this.mImageView);
    this.mImageView = null;
  }
  
  public void setVideoUri(Uri paramUri)
  {
    this.mVideoUri = paramUri;
  }
  
  public void setVolume(float paramFloat)
  {
    this.mVolume = paramFloat;
    if ((this.mPlayer != null) && (this.mVideoStarted)) {
      this.mPlayer.setVolume(paramFloat);
    }
  }
  
  public void start(final VideoCallback paramVideoCallback)
  {
    if (this.mVideoStarted) {
      return;
    }
    if (this.mVideoView != null) {
      stopVideoInternal();
    }
    this.mVideoStarted = true;
    this.mPlayer = InstantVideoPreloadManager.getInstance(getContext()).getOrCreatePlayer(this.mVideoUri);
    if (this.mPlayer == null)
    {
      if (paramVideoCallback != null) {
        paramVideoCallback.onVideoError(this);
      }
      this.mVideoStarted = false;
      return;
    }
    this.mVideoView = this.mPlayer.getPlayerView();
    addView(this.mVideoView, new FrameLayout.LayoutParams(-1, -1, 17));
    if (this.mImageView != null) {
      bringChildToFront(this.mImageView);
    }
    if ((getWidth() != 0) && (getHeight() != 0)) {
      this.mPlayer.setDisplaySize(getWidth(), getHeight());
    }
    if ((this.mPlayer instanceof ExoPlayerImpl)) {}
    for (;;)
    {
      this.mPlayer.prepare();
      this.mPlayer.setPlayWhenReady(true);
      this.mPlayer.setVolume(this.mVolume);
      this.mPlayer.setVideoCallback(new MediaPlayer.VideoCallback()
      {
        public void onVideoAvailable()
        {
          if (!InstantVideoView.this.mVideoStarted) {}
          do
          {
            return;
            InstantVideoView.this.setDisplayedChild(2);
          } while (paramVideoCallback == null);
          paramVideoCallback.onVideoStarted(InstantVideoView.this);
        }
        
        public void onVideoEnded()
        {
          if (!InstantVideoView.this.mVideoStarted) {}
          while (paramVideoCallback == null) {
            return;
          }
          paramVideoCallback.onVideoEnded(InstantVideoView.this);
        }
        
        public void onVideoError()
        {
          if (!InstantVideoView.this.mVideoStarted) {}
          while (paramVideoCallback == null) {
            return;
          }
          paramVideoCallback.onVideoError(InstantVideoView.this);
        }
      });
      return;
      setDisplayedChild(1);
    }
  }
  
  public void stop()
  {
    if (!this.mVideoStarted) {
      return;
    }
    this.mVideoStarted = false;
    this.mPlayer.setVideoCallback(null);
    setDisplayedChild(0);
    post(this.mStopVideoRunnable);
  }
  
  public static abstract class VideoCallback
  {
    public void onVideoEnded(InstantVideoView paramInstantVideoView) {}
    
    public void onVideoError(InstantVideoView paramInstantVideoView) {}
    
    public void onVideoStarted(InstantVideoView paramInstantVideoView) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/widget/InstantVideoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */