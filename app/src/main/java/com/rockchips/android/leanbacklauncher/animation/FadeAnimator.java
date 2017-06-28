package com.rockchips.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import com.rockchips.android.leanbacklauncher.util.Preconditions;

public class FadeAnimator extends ValueAnimator implements Resettable {
    private static /* synthetic */ int[] f2x7e91832;
    private final FadeAnimatorListener mListener;
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

    private static /* synthetic */ int[] m1982x207b7d0e() {
        if (f2x7e91832 != null) {
            return f2x7e91832;
        }
        int[] iArr = new int[Direction.values().length];
        try {
            iArr[Direction.FADE_IN.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Direction.FADE_OUT.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        f2x7e91832 = iArr;
        return iArr;
    }

    public FadeAnimator(View target, Direction direction) {
        float endAlpha;
        this.mListener = new FadeAnimatorListener();
        this.mTarget = (View) Preconditions.checkNotNull(target);
        switch (m1982x207b7d0e()[direction.ordinal()]) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                this.mStartAlpha = 0.0f;
                endAlpha = 1.0f;
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                this.mStartAlpha = 1.0f;
                endAlpha = 0.0f;
                break;
            default:
                throw new IllegalArgumentException("Illegal direction: " + direction);
        }
        setFloatValues(new float[]{this.mStartAlpha, endAlpha});
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
