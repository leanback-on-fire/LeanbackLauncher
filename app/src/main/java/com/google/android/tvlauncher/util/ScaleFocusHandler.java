package com.google.android.tvlauncher.util;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ScaleFocusHandler implements OnFocusChangeListener {
    public static final int FOCUS_DELAY_MILLIS = 60;
    public static final int PIVOT_CENTER = 0;
    public static final int PIVOT_START = 1;
    private final int mAnimationDuration;
    private AnimatorSet mAnimator;
    private AnimatorListener mAnimatorListener;
    private Runnable mDelayedFocusRunnable;
    private Runnable mDelayedUnfocusRunnable;
    private final float mFocusedElevation;
    private float mFocusedScale;
    private OnFocusChangeListener mOnFocusChangeListener;
    private int mPivot;
    private PivotProvider mPivotProvider;
    private int mPivotVerticalShift;
    View mView;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Pivot {
    }

    public interface PivotProvider {
        int getPivot();

        boolean shouldAnimate();
    }

    public ScaleFocusHandler(int animationDuration, float scale, float elevation) {
        this(animationDuration, scale, elevation, 0);
    }

    public ScaleFocusHandler(int animationDuration, float scale, float elevation, int pivot) {
        this.mPivot = 0;
        this.mDelayedFocusRunnable = new Runnable() {
            public void run() {
                if (ScaleFocusHandler.this.mView.isFocused()) {
                    ScaleFocusHandler.this.animateFocusedState(true);
                }
            }
        };
        this.mDelayedUnfocusRunnable = new Runnable() {
            public void run() {
                if (!ScaleFocusHandler.this.mView.isFocused()) {
                    ScaleFocusHandler.this.animateFocusedState(false);
                }
            }
        };
        this.mAnimationDuration = animationDuration;
        this.mFocusedScale = scale;
        this.mFocusedElevation = elevation;
        this.mPivot = pivot;
    }

    public ScaleFocusHandler(ScaleFocusHandler handler) {
        this(handler.mAnimationDuration, handler.mFocusedScale, handler.mFocusedElevation, handler.mPivot);
    }

    public void setView(View view) {
        this.mView = view;
        this.mView.setOnFocusChangeListener(this);
    }

    public void setFocusedScale(float focusedScale) {
        this.mFocusedScale = focusedScale;
    }

    public void setPivot(int pivot) {
        this.mPivot = pivot;
    }

    public void setPivotVerticalShift(int pivotVerticalShift) {
        this.mPivotVerticalShift = pivotVerticalShift;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

    public void setPivotProvider(PivotProvider pivotProvider) {
        this.mPivotProvider = pivotProvider;
    }

    public void resetFocusedState() {
        releaseAnimator();
        float scale = this.mView.isFocused() ? this.mFocusedScale : 1.0f;
        float elevation = this.mView.isFocused() ? this.mFocusedElevation : 0.0f;
        applyPivot();
        this.mView.setScaleX(scale);
        this.mView.setScaleY(scale);
        this.mView.setTranslationZ(elevation);
    }

    public int getAnimationDuration() {
        return this.mAnimationDuration;
    }

    private void cancelAnimation() {
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
            releaseAnimator();
        }
    }

    private void releaseAnimator() {
        if (this.mAnimator != null) {
            if (this.mAnimatorListener != null) {
                this.mAnimator.removeListener(this.mAnimatorListener);
            }
            this.mAnimator = null;
        }
        this.mAnimatorListener = null;
    }

    public void onFocusChange(View v, boolean hasFocus) {
        v.removeCallbacks(this.mDelayedFocusRunnable);
        v.removeCallbacks(this.mDelayedUnfocusRunnable);
        v.postDelayed(hasFocus ? this.mDelayedFocusRunnable : this.mDelayedUnfocusRunnable, 60);
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    protected void animateFocusedState(boolean hasFocus) {
        cancelAnimation();
        float beforePivotX = this.mView.getPivotX();
        applyPivot();
        boolean animatePivot = false;
        if (this.mPivotProvider != null) {
            animatePivot = this.mPivotProvider.shouldAnimate();
        }
        ObjectAnimator pivotAnimator = null;
        if (animatePivot) {
            pivotAnimator = ObjectAnimator.ofFloat(this.mView, "pivotX", new float[]{beforePivotX, this.mView.getPivotX()});
        }
        float scale = hasFocus ? this.mFocusedScale : 1.0f;
        float elevation = hasFocus ? this.mFocusedElevation : 0.0f;
        ObjectAnimator elevationAnimator = ObjectAnimator.ofFloat(this.mView, View.TRANSLATION_Z, new float[]{elevation}).setDuration((long) this.mAnimationDuration);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this.mView, View.SCALE_X, new float[]{scale}).setDuration((long) this.mAnimationDuration);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this.mView, View.SCALE_Y, new float[]{scale}).setDuration((long) this.mAnimationDuration);
        this.mAnimator = new AnimatorSet();
        if (pivotAnimator != null) {
            this.mAnimator.playTogether(elevationAnimator, scaleXAnimator, scaleYAnimator, pivotAnimator);
        } else {
            this.mAnimator.playTogether(elevationAnimator, scaleXAnimator, scaleYAnimator);
        }
        this.mAnimatorListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ScaleFocusHandler.this.releaseAnimator();
            }
        };
        this.mAnimator.addListener(this.mAnimatorListener);
        this.mAnimator.start();
    }

    private void applyPivot() {
        int width = this.mView.getLayoutParams().width;
        int height = this.mView.getLayoutParams().height;
        int pivotX = 0;
        if (width <= 0 || height <= 0) {
            width = this.mView.getWidth();
            height = this.mView.getHeight();
            if (width <= 0 || height <= 0) {
                return;
            }
        }
        if (this.mPivotProvider != null) {
            this.mPivot = this.mPivotProvider.getPivot();
        }
        if (this.mPivot == 0) {
            pivotX = width / 2;
        } else if (this.mPivot == 1) {
            if (this.mView.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                pivotX = width;
            } else {
                pivotX = 0;
            }
        }
        this.mView.setPivotX((float) pivotX);
        this.mView.setPivotY((float) ((height / 2) + this.mPivotVerticalShift));
    }
}
