package com.amazon.tv.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.widget.PlayingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class ViewDimmer {
    private static ColorFilter[] sFilters;
    private static ColorFilter[] sFiltersDesat;
    private static ColorMatrix[] sMatrices;
    private final float mActiveDimLevel;
    private boolean mAnimationEnabled = true;
    private ColorMatrix mConcatMatrix;
    private List<Drawable> mDesatDrawables;
    private List<ImageView> mDesatImageViews;
    private ObjectAnimator mDimAnimation;
    private float mDimLevel;
    private DimState mDimState;
    private List<Drawable> mDrawables;
    private final float mEditModeDimLevel;
    private List<ImageView> mImageViews;
    private final float mInactiveDimLevel;
    private List<Integer> mOriginalTextColors;
    private List<PlayingIndicatorView> mPlayingIndicatorViews;
    private View mTargetView;
    private List<TextView> mTextViews;

    public enum DimState {
        ACTIVE,
        INACTIVE,
        EDIT_MODE
    }

    public ViewDimmer(View view) {
        this.mTargetView = view;
        if (sFilters == null || sFiltersDesat == null) {
            sFilters = new ColorFilter[256];
            sFiltersDesat = new ColorFilter[256];
            sMatrices = new ColorMatrix[256];
            ColorMatrix desat = new ColorMatrix();
            desat.setSaturation(0.0f);
            for (int i = 0; i <= 255; i++) {
                float dimVal = 1.0f - (((float) i) / 255.0f);
                ColorMatrix dimMatrix = new ColorMatrix();
                dimMatrix.setScale(dimVal, dimVal, dimVal, 1.0f);
                sMatrices[i] = dimMatrix;
                sFilters[i] = new ColorMatrixColorFilter(dimMatrix);
                ColorMatrix dimDesatMatrix = new ColorMatrix();
                dimDesatMatrix.setScale(dimVal, dimVal, dimVal, 1.0f);
                dimDesatMatrix.postConcat(desat);
                sFiltersDesat[i] = new ColorMatrixColorFilter(dimDesatMatrix);
            }
        }
        this.mActiveDimLevel = this.mTargetView.getResources().getFraction(R.fraction.launcher_active_dim_level, 1, 1);
        this.mInactiveDimLevel = this.mTargetView.getResources().getFraction(R.fraction.launcher_inactive_dim_level, 1, 1);
        this.mEditModeDimLevel = this.mTargetView.getResources().getFraction(R.fraction.launcher_edit_mode_dim_level, 1, 1);
        int dimAnimDuration = this.mTargetView.getResources().getInteger(R.integer.item_dim_anim_duration);
        this.mDimAnimation = ObjectAnimator.ofFloat(this, "dimLevel", this.mInactiveDimLevel);
        this.mDimAnimation.setDuration(dimAnimDuration);
        this.mDimAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                ViewDimmer.this.mTargetView.setHasTransientState(true);
            }

            public void onAnimationEnd(Animator animation) {
                ViewDimmer.this.mTargetView.setHasTransientState(false);
            }
        });
    }

    static int getDimmedColor(int color, float level) {
        float factor = 1.0f - level;
        return Color.argb(Color.alpha(color), (int) (((float) Color.red(color)) * factor), (int) (((float) Color.green(color)) * factor), (int) (((float) Color.blue(color)) * factor));
    }

    public void setAnimationEnabled(boolean enabled) {
        this.mAnimationEnabled = enabled;
        if (!enabled && this.mDimAnimation.isStarted()) {
            this.mDimAnimation.end();
        }
    }

    public void setConcatMatrix(ColorMatrix matrix) {
        this.mConcatMatrix = matrix;
        // setDimLevel(this.mDimLevel);
    }

    @Keep
    private void setDimLevel(float level) {
        int size;
        int i;
        this.mDimLevel = level;
        ColorFilter filter = null;
        ColorFilter desatFilter = null;
        if (!(this.mImageViews == null && this.mDrawables == null) && this.mDimLevel > 0.0f && this.mDimLevel <= 1.0f) {
            int filterIndex = (int) (255.0f * level);
            if (this.mConcatMatrix == null) {
                filter = sFilters[filterIndex];
            } else {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setConcat(sMatrices[filterIndex], this.mConcatMatrix);
                filter = new ColorMatrixColorFilter(matrix);
            }
        }
        if (filter == null && this.mConcatMatrix != null) {
            filter = new ColorMatrixColorFilter(this.mConcatMatrix);
        }
        if (!(this.mDesatImageViews == null && this.mDesatDrawables == null) && this.mDimLevel >= 0.0f && this.mDimLevel <= 1.0f) {
            desatFilter = sFiltersDesat[(int) (255.0f * level)];
        }
        if (this.mImageViews != null) {
            size = this.mImageViews.size();
            for (i = 0; i < size; i++) {
                this.mImageViews.get(i).setColorFilter(filter);
            }
        }
        if (this.mDesatImageViews != null) {
            size = this.mDesatImageViews.size();
            for (i = 0; i < size; i++) {
                this.mDesatImageViews.get(i).setColorFilter(desatFilter);
            }
        }
        if (this.mDrawables != null) {
            size = this.mDrawables.size();
            for (i = 0; i < size; i++) {
                this.mDrawables.get(i).setColorFilter(filter);
            }
        }
        if (this.mTextViews != null) {
            size = this.mTextViews.size();
            for (i = 0; i < size; i++) {
                this.mTextViews.get(i).setTextColor(getDimmedColor(this.mOriginalTextColors.get(i).intValue(), level));
            }
        }
        if (this.mPlayingIndicatorViews != null) {
            size = this.mPlayingIndicatorViews.size();
            for (i = 0; i < size; i++) {
                this.mPlayingIndicatorViews.get(i).setColorFilter(filter);
            }
        }
        if (this.mDesatDrawables != null) {
            size = this.mDesatDrawables.size();
            for (i = 0; i < size; i++) {
                this.mDesatDrawables.get(i).mutate();
                this.mDesatDrawables.get(i).setColorFilter(desatFilter);
            }
        }
    }

    public float getDimLevel() {
        return this.mDimLevel;
    }

    public float convertToDimLevel(DimState dimState) {
        switch (dimState) {
            case ACTIVE:
                return this.mActiveDimLevel;
            case INACTIVE:
                return this.mInactiveDimLevel;
            case EDIT_MODE:
                return this.mEditModeDimLevel;
            default:
                throw new IllegalArgumentException("Illegal dimState: " + dimState);
        }
    }

    public static DimState activatedToDimState(boolean activated) {
        if (activated) {
            return DimState.ACTIVE;
        }
        return DimState.INACTIVE;
    }

    public static boolean dimStateToActivated(DimState dimState) {
        return !dimState.equals(DimState.INACTIVE);
    }

    public void animateDim(DimState dimState) {
        if (this.mAnimationEnabled) {
            if (this.mDimAnimation.isStarted()) {
                this.mDimAnimation.cancel();
            }
            if (getDimLevel() != convertToDimLevel(dimState)) {
                this.mDimAnimation.setFloatValues(getDimLevel(), R.id.end);
                this.mDimAnimation.start();
                return;
            }
            return;
        }
        // setDimLevelImmediate(dimState);
    }

    public void setDimLevelImmediate(DimState dimState) {
        if (this.mDimAnimation.isStarted()) {
            this.mDimAnimation.cancel();
        }
        // setDimLevel(convertToDimLevel(dimState));
    }

    public void setDimLevelImmediate() {
        if (this.mDimState != null) {
            //  setDimLevelImmediate(this.mDimState);
        } else {
            //  setDimLevelImmediate(DimState.INACTIVE);
        }
    }

    public void setDimState(DimState dimState, boolean immediate) {
        if (immediate) {
            //setDimLevelImmediate(dimState);
        } else {
            //animateDim(dimState);
        }
        this.mDimState = dimState;
    }

    public void addDimTarget(PlayingIndicatorView view) {
        if (this.mPlayingIndicatorViews == null) {
            this.mPlayingIndicatorViews = new ArrayList(4);
        }
        this.mPlayingIndicatorViews.add(view);
    }

    public void addDimTarget(ImageView view) {
        if (this.mImageViews == null) {
            this.mImageViews = new ArrayList(4);
        }
        this.mImageViews.add(view);
    }

    public void addDesatDimTarget(ImageView view) {
        if (this.mDesatImageViews == null) {
            this.mDesatImageViews = new ArrayList(4);
        }
        this.mDesatImageViews.add(view);
    }

    public void addDesatDimTarget(Drawable drawable) {
        if (this.mDesatDrawables == null) {
            this.mDesatDrawables = new ArrayList(4);
        }
        this.mDesatDrawables.add(drawable);
    }

    public void addDimTarget(TextView view) {
        if (this.mTextViews == null) {
            this.mTextViews = new ArrayList(4);
        }
        if (this.mOriginalTextColors == null) {
            this.mOriginalTextColors = new ArrayList(4);
        }
        this.mTextViews.add(view);
        this.mOriginalTextColors.add(Integer.valueOf(view.getCurrentTextColor()));
    }

    public void setTargetTextColor(TextView view, int newColor) {
        if (this.mTextViews != null && this.mOriginalTextColors != null) {
            int index = this.mTextViews.indexOf(view);
            if (index >= 0) {
                this.mOriginalTextColors.set(index, Integer.valueOf(newColor));
                view.setTextColor(getDimmedColor(newColor, this.mDimLevel));
            }
        }
    }

    public void addDimTarget(Drawable drawable) {
        if (this.mDrawables == null) {
            this.mDrawables = new ArrayList(4);
        }
        this.mDrawables.add(drawable);
    }

    public void removeDimTarget(Drawable drawable) {
        if (this.mDrawables != null) {
            this.mDrawables.remove(drawable);
        }
    }

    public void removeDesatDimTarget(Drawable drawable) {
        if (this.mDesatDrawables != null) {
            this.mDesatDrawables.remove(drawable);
        }
    }
}
