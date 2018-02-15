package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.ImageView;

final class CircleTakeoverAnimator extends ForwardingAnimator<Animator> {
    private final ImageView mCircleLayerView;
    private boolean mFinished;

    private final class CircleTakeoverAnimatorListener extends AnimatorListenerAdapter {
        private CircleTakeoverAnimatorListener() {
        }

        public void onAnimationStart(Animator animator) {
            CircleTakeoverAnimator.this.mCircleLayerView.setVisibility(0);
        }

        public void onAnimationCancel(Animator animation) {
            CircleTakeoverAnimator.this.reset();
        }

        public void onAnimationEnd(Animator animator) {
            animator.removeListener(this);
            CircleTakeoverAnimator.this.mFinished = true;
        }
    }

    public CircleTakeoverAnimator(View target, ImageView circleLayerView, int color) {
        super(getDelegate(target, circleLayerView, color));
        this.mCircleLayerView = circleLayerView;
        this.mDelegate.addListener(new CircleTakeoverAnimatorListener());
    }

    public void reset() {
        this.mCircleLayerView.setVisibility(4);
    }

    public void pause() {
    }

    public void resume() {
    }

    public boolean isStarted() {
        return !this.mFinished && super.isStarted();
    }

    public void addPauseListener(AnimatorPauseListener listener) {
    }

    public void removePauseListener(AnimatorPauseListener listener) {
    }

    private static Animator getDelegate(View target, ImageView circleLayerView, int color) {
        Point screenSize = getScreenSize(circleLayerView);
        int displayWidth = screenSize.x;
        int displayHeight = screenSize.y;
        int[] pos = new int[2];
        target.getLocationInWindow(pos);
        float scale = target.getScaleX();
        int x = (int) (((float) pos[0]) + ((((float) target.getMeasuredWidth()) * scale) / 2.0f));
        int y = (int) (((float) pos[1]) + ((((float) target.getMeasuredHeight()) * scale) / 2.0f));
        int w = displayWidth - x;
        int h = displayHeight - y;
        int r = (int) Math.max((double) ((int) Math.max((double) ((int) Math.max((double) ((int) Math.ceil(Math.sqrt((double) ((x * x) + (y * y))))), Math.ceil(Math.sqrt((double) ((w * w) + (y * y)))))), Math.ceil(Math.sqrt((double) ((w * w) + (h * h)))))), Math.ceil(Math.sqrt((double) ((x * x) + (h * h)))));
        circleLayerView.setBackgroundColor(-16777216 | color);
        circleLayerView.setAlpha(1.0f);
        return ViewAnimationUtils.createCircularReveal(circleLayerView, x, y, 0.0f, (float) r);
    }

    public String toString() {
        return "CircleTakeoverAnimator@" + Integer.toHexString(hashCode());
    }

    private static Point getScreenSize(View v) {
        Display display = ((WindowManager) v.getContext().getSystemService("window")).getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        return screenSize;
    }
}
