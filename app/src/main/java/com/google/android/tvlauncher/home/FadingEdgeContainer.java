package com.google.android.tvlauncher.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.home.util.ProgramUtil;

public class FadingEdgeContainer extends FrameLayout {
    private static final boolean DEBUG = false;
    private static final int[] FADE_COLORS_LTR = new int[21];
    private static final int[] FADE_COLORS_RTL = new int[21];
    private static final float[] FADE_COLOR_POSITIONS = new float[21];
    private static final int GRADIENT_CURVE_STEEPNESS = 100;
    private static final int GRADIENT_STEPS = 20;
    private static final String TAG = "FadingEdgeContainer";
    private boolean mFadeEnabled = true;
    private int mFadeWidth;
    private Paint mGradientPaint;
    private Rect mGradientRect;

    static {
        int i;
        FADE_COLORS_LTR[0] = 0;
        for (i = 1; i <= 20; i++) {
            // todo (converted to int)
            FADE_COLORS_LTR[i] = Color.argb((int) Math.pow(100.0d, (((double) i) / 20.0d) - ProgramUtil.ASPECT_RATIO_1_1), 0, 0, 0);
            FADE_COLOR_POSITIONS[i] = ((float) i) / 20.0f;
        }
        for (i = 0; i < FADE_COLORS_LTR.length; i++) {
            FADE_COLORS_RTL[(FADE_COLORS_LTR.length - i) - 1] = FADE_COLORS_LTR[i];
        }
    }

    public FadingEdgeContainer(@NonNull Context context) {
        super(context);
        init();
    }

    public FadingEdgeContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FadingEdgeContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.mFadeWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.channel_items_list_fade_width);
        this.mGradientRect = new Rect();
    }

    public void setFadeEnabled(boolean enabled) {
        this.mFadeEnabled = enabled;
    }

    private void setUpPaint(int layoutWidth) {
        LinearGradient gradient;
        this.mGradientPaint = new Paint();
        // todo check constant
        if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
            gradient = new LinearGradient(0.0f, 0.0f, (float) this.mFadeWidth, 0.0f, FADE_COLORS_LTR, FADE_COLOR_POSITIONS, TileMode.CLAMP);
        } else {
            gradient = new LinearGradient((float) (layoutWidth - this.mFadeWidth), 0.0f, (float) layoutWidth, 0.0f, FADE_COLORS_RTL, FADE_COLOR_POSITIONS, TileMode.CLAMP);
        }
        this.mGradientPaint.setShader(gradient);
        this.mGradientPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
    }

    protected void dispatchDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        if (this.mFadeEnabled && this.mGradientPaint == null) {
            setUpPaint(width);
        }
        if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
            if (this.mFadeEnabled) {
                canvas.saveLayer(0.0f, 0.0f, (float) this.mFadeWidth, (float) height, null);
                super.dispatchDraw(canvas);
                this.mGradientRect.set(0, 0, this.mFadeWidth, height);
                canvas.drawRect(this.mGradientRect, this.mGradientPaint);
                canvas.restore();
            }
            canvas.clipRect(this.mFadeWidth, 0, width, height);
            super.dispatchDraw(canvas);
            return;
        }
        if (this.mFadeEnabled) {
            canvas.saveLayer(0.0f, 0.0f, (float) width, (float) height, null);
            canvas.clipRect(width - this.mFadeWidth, 0, width, height);
            super.dispatchDraw(canvas);
            this.mGradientRect.set(width - this.mFadeWidth, 0, width, height);
            canvas.drawRect(this.mGradientRect, this.mGradientPaint);
            canvas.restore();
        }
        canvas.clipRect(0, 0, width - this.mFadeWidth, height);
        super.dispatchDraw(canvas);
    }
}
