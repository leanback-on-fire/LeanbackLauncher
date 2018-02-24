package android.support.v17.leanback.app;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.media.PlaybackGlue.PlayerCallback;
import android.support.v17.leanback.widget.DetailsParallax;
import android.support.v17.leanback.widget.Parallax.IntProperty;
import android.support.v17.leanback.widget.Parallax.PropertyMarkerValue;
import android.support.v17.leanback.widget.ParallaxEffect;
import android.support.v17.leanback.widget.ParallaxTarget;
import android.support.v7.widget.RecyclerView;

final class DetailsBackgroundVideoHelper
{
  private static final long BACKGROUND_CROSS_FADE_DURATION = 500L;
  private static final long CROSSFADE_DELAY = 1000L;
  static final int INITIAL = 0;
  static final int NO_VIDEO = 2;
  static final int PLAY_VIDEO = 1;
  private ValueAnimator mBackgroundAnimator;
  private Drawable mBackgroundDrawable;
  private boolean mBackgroundDrawableVisible;
  PlaybackControlStateCallback mControlStateCallback = new PlaybackControlStateCallback(null);
  private int mCurrentState = 0;
  private final DetailsParallax mDetailsParallax;
  private ParallaxEffect mParallaxEffect;
  private PlaybackGlue mPlaybackGlue;
  
  DetailsBackgroundVideoHelper(PlaybackGlue paramPlaybackGlue, DetailsParallax paramDetailsParallax, Drawable paramDrawable)
  {
    this.mPlaybackGlue = paramPlaybackGlue;
    this.mDetailsParallax = paramDetailsParallax;
    this.mBackgroundDrawable = paramDrawable;
    this.mBackgroundDrawableVisible = true;
    this.mBackgroundDrawable.setAlpha(255);
    startParallax();
  }
  
  private void applyState()
  {
    switch (this.mCurrentState)
    {
    }
    do
    {
      return;
      if (this.mPlaybackGlue != null)
      {
        if (this.mPlaybackGlue.isPrepared())
        {
          internalStartPlayback();
          return;
        }
        this.mPlaybackGlue.addPlayerCallback(this.mControlStateCallback);
        return;
      }
      crossFadeBackgroundToVideo(false);
      return;
      crossFadeBackgroundToVideo(false);
    } while (this.mPlaybackGlue == null);
    this.mPlaybackGlue.removePlayerCallback(this.mControlStateCallback);
    this.mPlaybackGlue.pause();
  }
  
  private void internalStartPlayback()
  {
    if (this.mPlaybackGlue != null) {
      this.mPlaybackGlue.play();
    }
    this.mDetailsParallax.getRecyclerView().postDelayed(new Runnable()
    {
      public void run()
      {
        DetailsBackgroundVideoHelper.this.crossFadeBackgroundToVideo(true);
      }
    }, 1000L);
  }
  
  private void updateState(int paramInt)
  {
    if (paramInt == this.mCurrentState) {
      return;
    }
    this.mCurrentState = paramInt;
    applyState();
  }
  
  void crossFadeBackgroundToVideo(boolean paramBoolean)
  {
    crossFadeBackgroundToVideo(paramBoolean, false);
  }
  
  void crossFadeBackgroundToVideo(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f2 = 0.0F;
    int j = 0;
    int i = 0;
    boolean bool;
    Drawable localDrawable;
    if (!paramBoolean1)
    {
      bool = true;
      if (this.mBackgroundDrawableVisible != bool) {
        break label87;
      }
      if (paramBoolean2)
      {
        if (this.mBackgroundAnimator != null)
        {
          this.mBackgroundAnimator.cancel();
          this.mBackgroundAnimator = null;
        }
        if (this.mBackgroundDrawable != null)
        {
          localDrawable = this.mBackgroundDrawable;
          if (!paramBoolean1) {
            break label79;
          }
        }
      }
    }
    for (;;)
    {
      localDrawable.setAlpha(i);
      return;
      bool = false;
      break;
      label79:
      i = 255;
    }
    label87:
    this.mBackgroundDrawableVisible = bool;
    if (this.mBackgroundAnimator != null)
    {
      this.mBackgroundAnimator.cancel();
      this.mBackgroundAnimator = null;
    }
    float f1;
    if (paramBoolean1)
    {
      f1 = 1.0F;
      label118:
      if (!paramBoolean1) {
        break label160;
      }
      label122:
      if (this.mBackgroundDrawable == null) {
        break label164;
      }
      if (!paramBoolean2) {
        break label174;
      }
      localDrawable = this.mBackgroundDrawable;
      if (!paramBoolean1) {
        break label166;
      }
    }
    label160:
    label164:
    label166:
    for (i = j;; i = 255)
    {
      localDrawable.setAlpha(i);
      return;
      f1 = 0.0F;
      break label118;
      f2 = 1.0F;
      break label122;
      break;
    }
    label174:
    this.mBackgroundAnimator = ValueAnimator.ofFloat(new float[] { f1, f2 });
    this.mBackgroundAnimator.setDuration(500L);
    this.mBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        DetailsBackgroundVideoHelper.this.mBackgroundDrawable.setAlpha((int)(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue() * 255.0F));
      }
    });
    this.mBackgroundAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        DetailsBackgroundVideoHelper.access$202(DetailsBackgroundVideoHelper.this, null);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    this.mBackgroundAnimator.start();
  }
  
  boolean isVideoVisible()
  {
    return this.mCurrentState == 1;
  }
  
  void setPlaybackGlue(PlaybackGlue paramPlaybackGlue)
  {
    if (this.mPlaybackGlue != null) {
      this.mPlaybackGlue.removePlayerCallback(this.mControlStateCallback);
    }
    this.mPlaybackGlue = paramPlaybackGlue;
    applyState();
  }
  
  void startParallax()
  {
    if (this.mParallaxEffect != null) {
      return;
    }
    Parallax.IntProperty localIntProperty = this.mDetailsParallax.getOverviewRowTop();
    this.mParallaxEffect = this.mDetailsParallax.addEffect(new Parallax.PropertyMarkerValue[] { localIntProperty.atFraction(1.0F), localIntProperty.atFraction(0.0F) }).target(new ParallaxTarget()
    {
      public void update(float paramAnonymousFloat)
      {
        if (paramAnonymousFloat == 1.0F)
        {
          DetailsBackgroundVideoHelper.this.updateState(2);
          return;
        }
        DetailsBackgroundVideoHelper.this.updateState(1);
      }
    });
    this.mDetailsParallax.updateValues();
  }
  
  void stopParallax()
  {
    this.mDetailsParallax.removeEffect(this.mParallaxEffect);
  }
  
  private class PlaybackControlStateCallback
    extends PlaybackGlue.PlayerCallback
  {
    private PlaybackControlStateCallback() {}
    
    public void onPreparedStateChanged(PlaybackGlue paramPlaybackGlue)
    {
      if (paramPlaybackGlue.isPrepared()) {
        DetailsBackgroundVideoHelper.this.internalStartPlayback();
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/DetailsBackgroundVideoHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */