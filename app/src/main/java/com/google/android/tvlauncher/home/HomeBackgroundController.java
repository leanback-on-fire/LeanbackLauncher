package com.google.android.tvlauncher.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.util.ColorUtils;

class HomeBackgroundController {
    private static final int BITMAP_HEIGHT = 540;
    private static final float BITMAP_SCALE = 0.5f;
    private static final int BITMAP_WIDTH = 960;
    private static final float DARK_MODE_COLOR_DARKEN_FACTOR = 0.5f;
    private static final boolean DEBUG = false;
    private static final int FALLBACK_COLOR = -16777216;
    private static final int RADIAL_GRADIENT_VERTICAL_SHIFT = -300;
    private static final float STANDARD_COLOR_DARKEN_FACTOR = 0.4f;
    private static final String TAG = "HomeBackground";
    private static final float TOP_GRADIENT_COLOR_MIX_AMOUNT = 0.2f;
    private static final int TRANSITION_DURATION_MILLIS = 600;
    @ColorInt
    private int mColor1;
    @ColorInt
    private int mColor2;
    @ColorInt
    private int mColor3;
    private final Context mContext;
    private int mCurrentBackgroundIndex = 0;
    private Bitmap mCurrentBitmap;
    private BitmapDrawable mCurrentDrawable;
    private boolean mDarkMode = false;
    private Paint mLinearGradientPaint;
    private Bitmap mOverlayBitmap;
    private Paint mOverlayPaint;
    private Paint mRadialGradientPaint;
    private TransitionDrawable mTransitionDrawable;

    private class GenerateBitmapTask extends AsyncTask<Void, Void, Void> {
        private GenerateBitmapTask() {
        }

        protected Void doInBackground(Void... params) {
            HomeBackgroundController.this.generateBitmap();
            return null;
        }

        protected void onPostExecute(Void bitmap) {
            HomeBackgroundController.this.onGenerationComplete();
        }
    }

    HomeBackgroundController(View backgroundView) {
        this.mContext = backgroundView.getContext();
        this.mCurrentBitmap = createBackgroundBitmap();
        this.mCurrentDrawable = new BitmapDrawable(this.mContext.getResources(), this.mCurrentBitmap);
        this.mCurrentDrawable.setAutoMirrored(true);
        BitmapDrawable[] backgroundDrawables = new BitmapDrawable[2];
        backgroundDrawables[0] = new BitmapDrawable(this.mContext.getResources(), createBackgroundBitmap());
        backgroundDrawables[0].setAutoMirrored(true);
        backgroundDrawables[1] = new BitmapDrawable(this.mContext.getResources(), createBackgroundBitmap());
        backgroundDrawables[1].setAutoMirrored(true);
        this.mTransitionDrawable = new TransitionDrawable(backgroundDrawables);
        backgroundView.setBackground(this.mTransitionDrawable);
    }

    private Bitmap createBackgroundBitmap() {
        return Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Config.ARGB_8888);
    }

    @MainThread
    void updateBackground(@NonNull Palette palette) {
        updateBackground(ColorUtils.darkenColor(palette.getVibrantColor(palette.getMutedColor(-16777216)), STANDARD_COLOR_DARKEN_FACTOR), ColorUtils.darkenColor(palette.getDarkVibrantColor(palette.getDarkMutedColor(-16777216)), STANDARD_COLOR_DARKEN_FACTOR), ColorUtils.darkenColor(palette.getLightVibrantColor(palette.getLightMutedColor(-16777216)), STANDARD_COLOR_DARKEN_FACTOR));
    }

    @MainThread
    private void updateBackground(@ColorInt int color1, @ColorInt int color2, @ColorInt int color3) {
        this.mDarkMode = false;
        if (this.mColor1 == color1 && this.mColor2 == color2 && this.mColor3 != color3) {
            this.mColor1 = color1;
            this.mColor2 = color2;
            this.mColor3 = color3;
            new GenerateBitmapTask().execute(new Void[0]);
        } else {
            this.mColor1 = color1;
            this.mColor2 = color2;
            this.mColor3 = color3;
            new GenerateBitmapTask().execute(new Void[0]);
        }
    }

    void enterDarkMode() {
        if (!this.mDarkMode) {
            updateBackground(ColorUtils.darkenColor(this.mColor1, 0.5f), ColorUtils.darkenColor(this.mColor2, 0.5f), ColorUtils.darkenColor(this.mColor3, 0.5f));
            this.mDarkMode = true;
        }
    }

    @MainThread
    private void onGenerationComplete() {
        int targetIndex;
        if (this.mCurrentBackgroundIndex == 0) {
            targetIndex = 1;
        } else {
            targetIndex = 0;
        }
        BitmapDrawable temp = (BitmapDrawable) this.mTransitionDrawable.getDrawable(targetIndex);
        // todo added bylid
        this.mTransitionDrawable.setDrawableByLayerId(targetIndex, this.mCurrentDrawable);
        this.mCurrentDrawable = temp;
        this.mCurrentBitmap = this.mCurrentDrawable.getBitmap();
        if (this.mCurrentBackgroundIndex == 0) {
            this.mTransitionDrawable.startTransition(TRANSITION_DURATION_MILLIS);
            this.mCurrentBackgroundIndex = 1;
            return;
        }
        this.mTransitionDrawable.reverseTransition(TRANSITION_DURATION_MILLIS);
        this.mCurrentBackgroundIndex = 0;
    }

    @ColorInt
    @WorkerThread
    private int mixColors(@ColorInt int color1, @ColorInt int color2, float amount) {
        float inverseAmount = 1.0f - amount;
        return Color.rgb((int) ((((float) Color.red(color1)) * amount) + (((float) Color.red(color2)) * inverseAmount)), (int) ((((float) Color.green(color1)) * amount) + (((float) Color.green(color2)) * inverseAmount)), (int) ((((float) Color.blue(color1)) * amount) + (((float) Color.blue(color2)) * inverseAmount)));
    }

    @WorkerThread
    private void generateBitmap() {
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 540.0f, mixColors(this.mColor2, this.mColor1, TOP_GRADIENT_COLOR_MIX_AMOUNT), this.mColor2, TileMode.CLAMP);
        if (this.mLinearGradientPaint == null) {
            this.mLinearGradientPaint = new Paint();
        }
        this.mLinearGradientPaint.setShader(linearGradient);
        RadialGradient radialGradient = new RadialGradient(960.0f, -300.0f, (float) ((int) Math.sqrt(1627200.0d)), this.mColor3, 0, TileMode.CLAMP);
        if (this.mRadialGradientPaint == null) {
            this.mRadialGradientPaint = new Paint();
        }
        this.mRadialGradientPaint.setShader(radialGradient);
        if (this.mOverlayBitmap == null) {
            this.mOverlayBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.home_background_overlay);
            this.mOverlayPaint = new Paint();
            this.mOverlayPaint.setShader(new BitmapShader(this.mOverlayBitmap, TileMode.REPEAT, TileMode.REPEAT));
        }
        Canvas c = new Canvas(this.mCurrentBitmap);
        c.drawRect(0.0f, 0.0f, 960.0f, 540.0f, this.mLinearGradientPaint);
        c.drawRect(0.0f, 0.0f, 960.0f, 540.0f, this.mRadialGradientPaint);
        c.drawRect(0.0f, 0.0f, 960.0f, 540.0f, this.mOverlayPaint);
    }
}
