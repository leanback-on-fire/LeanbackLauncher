package com.amazon.tv.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;

import com.amazon.tv.leanbacklauncher.util.Preconditions;

public class FadeAnimator extends ValueAnimator implements Resettable {
    private final FadeAnimatorListener mListener = new FadeAnimatorListener();
    private final float mStartAlpha;
    private final View mTarget;

    public enum Direction {
        FADE_IN,
        FADE_OUT
    }

    private final class FadeAnimatorListener extends AnimatorListenerAdapter implements AnimatorUpdateListener {
        private FadeAnimatorListener() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            FadeAnimator.this.mTarget.setAlpha(((Float) FadeAnimator.this.getAnimatedValue()).floatValue());
        }

        public void onAnimationCancel(Animator animation) {
            FadeAnimator.this.reset();
        }

        public void onAnimationEnd(Animator animation) {
            animation.removeListener(this);
        }
    }

    public FadeAnimator(View target, Direction direction) {
        float endAlpha;
        this.mTarget = Preconditions.checkNotNull(target);
        switch (direction) {
            case FADE_IN:
                this.mStartAlpha = 0.0f;
                endAlpha = 1.0f;
                break;
            case FADE_OUT:
                this.mStartAlpha = 1.0f;
                endAlpha = 0.0f;
                break;
            default:
                throw new IllegalArgumentException("Illegal direction: " + direction);
        }
        setFloatValues(this.mStartAlpha, endAlpha);
        addListener(this.mListener);
        addUpdateListener(this.mListener);
    }

    public void setupStartValues() {
        this.mTarget.setAlpha(this.mStartAlpha);
    }

    public void reset() {
        this.mTarget.setAlpha(1.0f);
    }

    public String toString() {
        return "FadeAnimator@" + Integer.toHexString(hashCode()) + ':' + (this.mStartAlpha == 0.0f ? "FADE_IN" : "FADE_OUT") + '{' + this.mTarget.getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this.mTarget)) + '}';
    }
}
