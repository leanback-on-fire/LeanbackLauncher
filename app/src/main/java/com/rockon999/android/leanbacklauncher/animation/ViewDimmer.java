package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockon999.android.leanbacklauncher.R;

import java.util.ArrayList;
import java.util.List;

public class ViewDimmer {
    private static  /* synthetic */ int[] f6x1d4ac58a;
    private static ColorFilter[] sFilters;
    private static ColorFilter[] sFiltersDesat;
    private final float mActiveDimLevel;
    private boolean mAnimationEnabled;
    private List<ImageView> mDesatImageViews;
    private ObjectAnimator mDimAnimation;
    private float mDimLevel;
    private DimState mDimState;
    private List<Drawable> mDrawables;
    private final float mEditModeDimLevel;
    private List<ImageView> mImageViews;
    private final float mInactiveDimLevel;
    private List<Integer> mOriginalTextColors;
    private View mTargetView;
    private List<TextView> mTextViews;

    /* renamed from: ViewDimmer.1 */
    class C01791 extends AnimatorListenerAdapter {
        C01791() {
        }

        public void onAnimationStart(Animator animation) {
            ViewDimmer.this.mTargetView.setHasTransientState(true);
        }

        public void onAnimationEnd(Animator animation) {
            ViewDimmer.this.mTargetView.setHasTransientState(false);
        }
    }

    public enum DimState {
        ACTIVE,
        INACTIVE,
        EDIT_MODE
    }

    private static /* synthetic */ int[] m1986x75d1ee2e() {
        if (f6x1d4ac58a != null) {
            return f6x1d4ac58a;
        }
        int[] iArr = new int[DimState.values().length];
        try {
            iArr[DimState.ACTIVE.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[DimState.EDIT_MODE.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[DimState.INACTIVE.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f6x1d4ac58a = iArr;
        return iArr;
    }

    public ViewDimmer(View view) {
        this.mAnimationEnabled = true;
        this.mTargetView = view;
        if (sFilters == null || sFiltersDesat == null) {
            sFilters = new ColorFilter[256];
            sFiltersDesat = new ColorFilter[256];
            ColorMatrix desat = new ColorMatrix();
            desat.setSaturation(0.0f);
            for (int i = 0; i <= 255; i++) {
                float dimVal = 1.0f - (((float) i) / 255.0f);
                ColorMatrix dimMatrix = new ColorMatrix();
                dimMatrix.setScale(dimVal, dimVal, dimVal, 1.0f);
                sFilters[i] = new ColorMatrixColorFilter(dimMatrix);
                ColorMatrix dimDesatMatrix = new ColorMatrix();
                dimDesatMatrix.setScale(dimVal, dimVal, dimVal, 1.0f);
                dimDesatMatrix.postConcat(desat);
                sFiltersDesat[i] = new ColorMatrixColorFilter(dimDesatMatrix);
            }
        }
        TypedValue out = new TypedValue();
        this.mTargetView.getResources().getValue(R.raw.launcher_active_dim_level, out, true);
        this.mActiveDimLevel = out.getFloat();
        this.mTargetView.getResources().getValue(R.raw.launcher_inactive_dim_level, out, true);
        this.mInactiveDimLevel = out.getFloat();
        this.mTargetView.getResources().getValue(R.raw.launcher_edit_mode_dim_level, out, true);
        this.mEditModeDimLevel = out.getFloat();
        int dimAnimDuration = this.mTargetView.getResources().getInteger(R.integer.item_dim_anim_duration);
        this.mDimAnimation = ObjectAnimator.ofFloat(this, "dimLevel", new float[]{this.mInactiveDimLevel});
        this.mDimAnimation.setDuration((long) dimAnimDuration);
        this.mDimAnimation.addListener(new C01791());
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

    @Keep
    public void setDimLevel(float level) {
        int size;
        int i;
        this.mDimLevel = level;
        ColorFilter filter = null;
        ColorFilter desatFilter = null;
        if (!(this.mImageViews == null && this.mDrawables == null) && this.mDimLevel > 0.0f && this.mDimLevel <= 1.0f) {
            filter = sFilters[(int) (255.0f * level)];
        }
        if (this.mDesatImageViews != null && this.mDimLevel >= 0.0f && this.mDimLevel <= 1.0f) {
            desatFilter = sFiltersDesat[(int) (255.0f * level)];
        }
        if (this.mImageViews != null) {
            size = this.mImageViews.size();
            for (i = 0; i < size; i++) {
                ((ImageView) this.mImageViews.get(i)).setColorFilter(filter);
            }
        }
        if (this.mDesatImageViews != null) {
            size = this.mDesatImageViews.size();
            for (i = 0; i < size; i++) {
                ((ImageView) this.mDesatImageViews.get(i)).setColorFilter(desatFilter);
            }
        }
        if (this.mDrawables != null) {
            size = this.mDrawables.size();
            for (i = 0; i < size; i++) {
                ((Drawable) this.mDrawables.get(i)).setColorFilter(filter);
            }
        }
        /*if (this.mTextViews != null) {
            size = this.mTextViews.size();
            for (i = 0; i < size; i++) {
                ((TextView) this.mTextViews.get(i)).setTextColor(getDimmedColor(((Integer) this.mOriginalTextColors.get(i)).intValue(), level));
            }
        }*/
    }

    public float getDimLevel() {
        return this.mDimLevel;
    }

    public float convertToDimLevel(DimState dimState) {
        switch (m1986x75d1ee2e()[dimState.ordinal()]) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability:
                return this.mActiveDimLevel;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager:
                return this.mEditModeDimLevel;
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                return this.mInactiveDimLevel;
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
                this.mDimAnimation.setFloatValues(new float[]{getDimLevel(), R.id.end});
                this.mDimAnimation.start();
            }
            return;
        }
        setDimLevelImmediate(dimState);
    }

    public void setDimLevelImmediate(DimState dimState) {
        if (this.mDimAnimation.isStarted()) {
            this.mDimAnimation.cancel();
        }
        //setDimLevel(convertToDimLevel(dimState));
    }

    public void setDimLevelImmediate() {
        if (this.mDimState != null) {
            setDimLevelImmediate(this.mDimState);
        } else {
            setDimLevelImmediate(DimState.INACTIVE);
        }
    }

    public void setDimState(DimState dimState, boolean immediate) {
        if (immediate) {
            setDimLevelImmediate(dimState);
        } else {
            animateDim(dimState);
        }
        this.mDimState = dimState;
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
}
