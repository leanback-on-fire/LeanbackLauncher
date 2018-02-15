package com.rockon999.android.leanbacklauncher.wallpaper;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.rockon999.android.leanbacklauncher.R;

public class AnimatedLayer extends WallpaperImage {
    private final Animator mFadeInAnim;
    private final Animator mFadeOutAnim;
    private AnimationListener mListener;
    private Animator mRunningAnimation;

    /* renamed from: AnimatedLayer.1 */
    class C02031 extends AnimatorListenerAdapter {
        C02031() {
        }

        public void onAnimationEnd(Animator animation) {
            if (AnimatedLayer.this.mRunningAnimation == AnimatedLayer.this.mFadeInAnim) {
                AnimatedLayer.this.mRunningAnimation = null;
            }
            if (AnimatedLayer.this.mListener != null) {
                AnimatedLayer.this.mListener.animationDone(true);
            }
        }
    }

    /* renamed from: AnimatedLayer.2 */
    class C02042 extends AnimatorListenerAdapter {
        C02042() {
        }

        public void onAnimationEnd(Animator animation) {
            if (AnimatedLayer.this.mRunningAnimation == AnimatedLayer.this.mFadeOutAnim) {
                AnimatedLayer.this.mRunningAnimation = null;
            }
            AnimatedLayer.this.setVisibility(8);
            if (AnimatedLayer.this.mListener != null) {
                AnimatedLayer.this.mListener.animationDone(false);
            }
        }
    }

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
        this.mFadeInAnim.addListener(new C02031());
        this.mFadeOutAnim.addListener(new C02042());
    }

    public boolean isAnimating() {
        return this.mRunningAnimation != null && this.mRunningAnimation.isRunning();
    }

    public void cancelAnimation() {
        if (isAnimating()) {
            this.mRunningAnimation.cancel();
        }
    }

    public void animateIn(Drawable image) {
        cancelAnimation();
        setVisibility(0);
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
        setVisibility(0);
        setImageDrawable(image);
        this.mRunningAnimation = this.mFadeOutAnim;
        this.mFadeOutAnim.start();
    }
}
