package com.rockon999.android.leanbacklauncher.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.rockon999.android.leanbacklauncher.R;

public class PlayingIndicatorView extends View {
    private static final int[][] LEVELS = new int[][]{new int[]{5, 3, 5, 7, 9, 10, 11, 12, 11, 12, 10, 8, 7, 4, 2, 4, 6, 7, 9, 11, 9, 7, 5, 3, 5, 8, 5, 3, 4}, new int[]{12, 11, 10, 11, 12, 11, 9, 7, 9, 11, 12, 10, 8, 10, 12, 11, 9, 5, 3, 5, 8, 10, 12, 10, 9, 8}, new int[]{8, 9, 10, 12, 11, 9, 7, 5, 7, 8, 9, 12, 11, 12, 9, 7, 9, 11, 12, 10, 8, 9, 7, 5, 3}};
    private final ValueAnimator mAnimator;
    private final int mBarSeparationPx;
    private final int mBarWidthPx;
    private final Rect mDrawRect = new Rect();
    private final Paint mPaint;
    private boolean mPlaying;
    private float mProgress;

    public PlayingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        this.mBarWidthPx = res.getDimensionPixelSize(R.dimen.leanback_card_now_playing_bar_width);
        this.mBarSeparationPx = res.getDimensionPixelSize(R.dimen.leanback_card_now_playing_bar_margin);
        this.mAnimator = new ValueAnimator();
        this.mAnimator.setInterpolator(new LinearInterpolator());
        this.mAnimator.setRepeatCount(-1);
        this.mAnimator.setDuration(100000000);
        this.mAnimator.setFloatValues(new float[]{0.0f, (float) (this.mAnimator.getDuration() / 80)});
        this.mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                PlayingIndicatorView.this.mProgress = ((Float) animation.getAnimatedValue()).floatValue();
                PlayingIndicatorView.this.invalidate();
            }
        });
        this.mPaint = new Paint();
        this.mPaint.setColor(-1);
        setLayerType(1, null);
        setImportantForAccessibility(2);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (this.mBarWidthPx * 3) + (this.mBarSeparationPx * 2);
        setMeasuredDimension(width, width);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimationIfVisible();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    public void stopAnimation() {
        this.mAnimator.cancel();
        postInvalidate();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public void startAnimationIfVisible() {
        if (getVisibility() == 0) {
            this.mAnimator.start();
            postInvalidate();
        }
    }

    public void setPlaying(boolean playing) {
        this.mPlaying = playing;
    }

    public boolean getPlaying() {
        return this.mPlaying;
    }

    protected void onDraw(Canvas canvas) {
        drawRectangles(canvas);
    }

    private void drawRectangles(Canvas canvas) {
        for (int barIndex = 0; barIndex < 3; barIndex++) {
            this.mDrawRect.left = (this.mBarWidthPx + this.mBarSeparationPx) * barIndex;
            this.mDrawRect.right = this.mDrawRect.left + this.mBarWidthPx;
            this.mDrawRect.bottom = getHeight();
            this.mDrawRect.top = (int) ((((float) getHeight()) * (15.0f - (this.mPlaying ? linearlyInterpolateWithWrapping(this.mProgress, LEVELS[barIndex]) : 0.5f))) / 15.0f);
            canvas.drawRect(this.mDrawRect, this.mPaint);
        }
    }

    private static float linearlyInterpolateWithWrapping(float position, int[] array) {
        int positionRoundedDown = (int) position;
        int beforeIndex = positionRoundedDown % array.length;
        float weight = position - ((float) positionRoundedDown);
        return (((float) array[beforeIndex]) * (1.0f - weight)) + (((float) array[(beforeIndex + 1) % array.length]) * weight);
    }
}
