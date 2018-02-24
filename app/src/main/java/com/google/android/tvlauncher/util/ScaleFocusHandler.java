package com.google.android.tvlauncher.util;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ScaleFocusHandler
  implements View.OnFocusChangeListener
{
  public static final int FOCUS_DELAY_MILLIS = 60;
  public static final int PIVOT_CENTER = 0;
  public static final int PIVOT_START = 1;
  private final int mAnimationDuration;
  private AnimatorSet mAnimator;
  private Animator.AnimatorListener mAnimatorListener;
  private Runnable mDelayedFocusRunnable = new Runnable()
  {
    public void run()
    {
      if (ScaleFocusHandler.this.mView.isFocused()) {
        ScaleFocusHandler.this.animateFocusedState(true);
      }
    }
  };
  private Runnable mDelayedUnfocusRunnable = new Runnable()
  {
    public void run()
    {
      if (!ScaleFocusHandler.this.mView.isFocused()) {
        ScaleFocusHandler.this.animateFocusedState(false);
      }
    }
  };
  private final float mFocusedElevation;
  private float mFocusedScale;
  private View.OnFocusChangeListener mOnFocusChangeListener;
  private int mPivot = 0;
  private PivotProvider mPivotProvider;
  private int mPivotVerticalShift;
  View mView;
  
  public ScaleFocusHandler(int paramInt, float paramFloat1, float paramFloat2)
  {
    this(paramInt, paramFloat1, paramFloat2, 0);
  }
  
  public ScaleFocusHandler(int paramInt1, float paramFloat1, float paramFloat2, int paramInt2)
  {
    this.mAnimationDuration = paramInt1;
    this.mFocusedScale = paramFloat1;
    this.mFocusedElevation = paramFloat2;
    this.mPivot = paramInt2;
  }
  
  public ScaleFocusHandler(ScaleFocusHandler paramScaleFocusHandler)
  {
    this(paramScaleFocusHandler.mAnimationDuration, paramScaleFocusHandler.mFocusedScale, paramScaleFocusHandler.mFocusedElevation, paramScaleFocusHandler.mPivot);
  }
  
  private void applyPivot()
  {
    int j = this.mView.getLayoutParams().width;
    int m = this.mView.getLayoutParams().height;
    int i = 0;
    int k;
    if (j > 0)
    {
      k = m;
      if (m > 0) {}
    }
    else
    {
      j = this.mView.getWidth();
      m = this.mView.getHeight();
      if (j > 0)
      {
        k = m;
        if (m > 0) {}
      }
      else
      {
        return;
      }
    }
    if (this.mPivotProvider != null) {
      this.mPivot = this.mPivotProvider.getPivot();
    }
    if (this.mPivot == 0) {
      i = j / 2;
    }
    for (;;)
    {
      this.mView.setPivotX(i);
      this.mView.setPivotY(k / 2 + this.mPivotVerticalShift);
      return;
      if (this.mPivot == 1) {
        if (this.mView.getLayoutDirection() == 1) {
          i = j;
        } else {
          i = 0;
        }
      }
    }
  }
  
  private void cancelAnimation()
  {
    if (this.mAnimator != null)
    {
      this.mAnimator.cancel();
      releaseAnimator();
    }
  }
  
  private void releaseAnimator()
  {
    if (this.mAnimator != null)
    {
      if (this.mAnimatorListener != null) {
        this.mAnimator.removeListener(this.mAnimatorListener);
      }
      this.mAnimator = null;
    }
    this.mAnimatorListener = null;
  }
  
  protected void animateFocusedState(boolean paramBoolean)
  {
    cancelAnimation();
    float f1 = this.mView.getPivotX();
    applyPivot();
    boolean bool = false;
    if (this.mPivotProvider != null) {
      bool = this.mPivotProvider.shouldAnimate();
    }
    ObjectAnimator localObjectAnimator1 = null;
    if (bool) {
      localObjectAnimator1 = ObjectAnimator.ofFloat(this.mView, "pivotX", new float[] { f1, this.mView.getPivotX() });
    }
    float f2;
    label91:
    ObjectAnimator localObjectAnimator2;
    ObjectAnimator localObjectAnimator3;
    ObjectAnimator localObjectAnimator4;
    if (paramBoolean)
    {
      f1 = this.mFocusedScale;
      if (!paramBoolean) {
        break label264;
      }
      f2 = this.mFocusedElevation;
      localObjectAnimator2 = ObjectAnimator.ofFloat(this.mView, View.TRANSLATION_Z, new float[] { f2 });
      localObjectAnimator2.setDuration(this.mAnimationDuration);
      localObjectAnimator3 = ObjectAnimator.ofFloat(this.mView, View.SCALE_X, new float[] { f1 });
      localObjectAnimator3.setDuration(this.mAnimationDuration);
      localObjectAnimator4 = ObjectAnimator.ofFloat(this.mView, View.SCALE_Y, new float[] { f1 });
      localObjectAnimator4.setDuration(this.mAnimationDuration);
      this.mAnimator = new AnimatorSet();
      if (localObjectAnimator1 == null) {
        break label269;
      }
      this.mAnimator.playTogether(new Animator[] { localObjectAnimator2, localObjectAnimator3, localObjectAnimator4, localObjectAnimator1 });
    }
    for (;;)
    {
      this.mAnimatorListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ScaleFocusHandler.this.releaseAnimator();
        }
      };
      this.mAnimator.addListener(this.mAnimatorListener);
      this.mAnimator.start();
      return;
      f1 = 1.0F;
      break;
      label264:
      f2 = 0.0F;
      break label91;
      label269:
      this.mAnimator.playTogether(new Animator[] { localObjectAnimator2, localObjectAnimator3, localObjectAnimator4 });
    }
  }
  
  public int getAnimationDuration()
  {
    return this.mAnimationDuration;
  }
  
  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    paramView.removeCallbacks(this.mDelayedFocusRunnable);
    paramView.removeCallbacks(this.mDelayedUnfocusRunnable);
    if (paramBoolean) {}
    for (Runnable localRunnable = this.mDelayedFocusRunnable;; localRunnable = this.mDelayedUnfocusRunnable)
    {
      paramView.postDelayed(localRunnable, 60L);
      if (this.mOnFocusChangeListener != null) {
        this.mOnFocusChangeListener.onFocusChange(paramView, paramBoolean);
      }
      return;
    }
  }
  
  public void resetFocusedState()
  {
    releaseAnimator();
    float f1;
    if (this.mView.isFocused())
    {
      f1 = this.mFocusedScale;
      if (!this.mView.isFocused()) {
        break label68;
      }
    }
    label68:
    for (float f2 = this.mFocusedElevation;; f2 = 0.0F)
    {
      applyPivot();
      this.mView.setScaleX(f1);
      this.mView.setScaleY(f1);
      this.mView.setTranslationZ(f2);
      return;
      f1 = 1.0F;
      break;
    }
  }
  
  public void setFocusedScale(float paramFloat)
  {
    this.mFocusedScale = paramFloat;
  }
  
  public void setOnFocusChangeListener(View.OnFocusChangeListener paramOnFocusChangeListener)
  {
    this.mOnFocusChangeListener = paramOnFocusChangeListener;
  }
  
  public void setPivot(int paramInt)
  {
    this.mPivot = paramInt;
  }
  
  public void setPivotProvider(PivotProvider paramPivotProvider)
  {
    this.mPivotProvider = paramPivotProvider;
  }
  
  public void setPivotVerticalShift(int paramInt)
  {
    this.mPivotVerticalShift = paramInt;
  }
  
  public void setView(View paramView)
  {
    this.mView = paramView;
    this.mView.setOnFocusChangeListener(this);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Pivot {}
  
  public static abstract interface PivotProvider
  {
    public abstract int getPivot();
    
    public abstract boolean shouldAnimate();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/ScaleFocusHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */