package com.amazon.tv.leanbacklauncher.wallpaper;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.amazon.tv.leanbacklauncher.R;

public class AnimatedLayer extends WallpaperImage {
    private final Animator mFadeInAnim;
    private final Animator mFadeOutAnim;
    private AnimationListener mListener;
    private Animator mRunningAnimation;

    public interface AnimationListener {
        void animationDone(boolean z);
    }

    public AnimatedLayer(Context context) {
        this(context, null);
    }

    public AnimatedLayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setAnimationListener(AnimationListener listener) {
        this.mListener = listener;
    }

    public AnimatedLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFadeInAnim = AnimatorInflater.loadAnimator(context, R.animator.wallpaper_fade_in);
        this.mFadeOutAnim = AnimatorInflater.loadAnimator(context, R.animator.wallpaper_fade_out);
        this.mFadeInAnim.setTarget(this);
        this.mFadeOutAnim.setTarget(this);
        this.mFadeInAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (AnimatedLayer.this.mRunningAnimation == AnimatedLayer.this.mFadeInAnim) {
                    AnimatedLayer.this.mRunningAnimation = null;
                }
                if (AnimatedLayer.this.mListener != null) {
                    AnimatedLayer.this.mListener.animationDone(true);
                }
            }
        });
        this.mFadeOutAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (AnimatedLayer.this.mRunningAnimation == AnimatedLayer.this.mFadeOutAnim) {
                    AnimatedLayer.this.mRunningAnimation = null;
                }
                AnimatedLayer.this.setVisibility(View.GONE);
                if (AnimatedLayer.this.mListener != null) {
                    AnimatedLayer.this.mListener.animationDone(false);
                }
            }
        });
    }

    public boolean isAnimating() {
        if (this.mRunningAnimation != null) {
            return this.mRunningAnimation.isRunning();
        }
        return false;
    }

    public void cancelAnimation() {
        if (isAnimating()) {
            this.mRunningAnimation.cancel();
        }
    }

    public void animateIn(Drawable image) {
        cancelAnimation();
        setVisibility(View.VISIBLE);
        setImageDrawable(image);
        this.mRunningAnimation = this.mFadeInAnim;
        this.mFadeInAnim.start();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setPivotX(0.0f);
        setPivotY((float) (getMeasuredHeight() / 2));
    }

    public void animateOut(Drawable image) {
        cancelAnimation();
        setVisibility(View.VISIBLE);
        setImageDrawable(image);
        this.mRunningAnimation = this.mFadeOutAnim;
        this.mFadeOutAnim.start();
    }
}
