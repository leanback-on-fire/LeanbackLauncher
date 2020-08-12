package com.amazon.tv.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Keep;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration;

public class ViewFocusAnimator implements OnFocusChangeListener {
    private final boolean mCardElevationSupported;
    private boolean mEnabled = true;
    private ObjectAnimator mFocusAnimation;
    private float mFocusProgress;
    private OnFocusLevelChangeListener mListener;
    private final float mSelectedScaleDelta;
    private final float mSelectedZDelta;
    protected View mTargetView;
    private final float mUnselectedScale;
    private final float mUnselectedZ;

    public interface OnFocusLevelChangeListener {
        void onFocusLevelChange(float f);

        void onFocusLevelSettled(boolean z);
    }

    public ViewFocusAnimator(View view) {
        boolean z = true;
        this.mTargetView = view;
        Resources res = view.getResources();
        this.mTargetView.setOnFocusChangeListener(this);
        this.mUnselectedScale = res.getFraction(R.fraction.unselected_scale, 1, 1);
        this.mSelectedScaleDelta = getFocusedScaleFactor() - this.mUnselectedScale;
        this.mUnselectedZ = (float) res.getDimensionPixelOffset(R.dimen.unselected_item_z);
        this.mSelectedZDelta = (float) res.getDimensionPixelOffset(R.dimen.selected_item_z_delta);
        this.mFocusAnimation = ObjectAnimator.ofFloat(this, "focusProgress", 0.0f);
        this.mFocusAnimation.setDuration(res.getInteger(R.integer.item_scale_anim_duration));
        this.mFocusAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        LauncherConfiguration launcherConfiguration = LauncherConfiguration.getInstance();
        if (launcherConfiguration == null || !launcherConfiguration.isCardElevationEnabled()) {
            z = false;
        }
        this.mCardElevationSupported = z;
        setFocusProgress(0.0f);
        this.mFocusAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                ViewFocusAnimator.this.mTargetView.setHasTransientState(true);
            }

            public void onAnimationEnd(Animator animation) {
                boolean z = false;
                ViewFocusAnimator.this.mTargetView.setHasTransientState(false);
                if (ViewFocusAnimator.this.mListener != null) {
                    OnFocusLevelChangeListener access$000 = ViewFocusAnimator.this.mListener;
                    if (ViewFocusAnimator.this.mFocusProgress > 0.5f) {
                        z = true;
                    }
                    access$000.onFocusLevelSettled(z);
                }
            }
        });
    }

    public float getFocusedScaleFactor() {
        return this.mTargetView.getResources().getFraction(R.fraction.lb_focus_zoom_factor_medium, 1, 1);
    }

    public void setOnFocusProgressListener(OnFocusLevelChangeListener listener) {
        this.mListener = listener;
    }

    @Keep
    public void setFocusProgress(float level) {
        this.mFocusProgress = level;
        float scale = this.mUnselectedScale + (this.mSelectedScaleDelta * level);
        float z = this.mUnselectedZ + (this.mSelectedZDelta * level);
        this.mTargetView.setScaleX(scale);
        this.mTargetView.setScaleY(scale);
        if (this.mCardElevationSupported) {
            this.mTargetView.setZ(z);
        }
        if (this.mListener != null) {
            this.mListener.onFocusLevelChange(level);
        }
    }

    public float getFocusProgress() {
        return this.mFocusProgress;
    }

    public void animateFocus(boolean focused) {
        if (this.mEnabled) {
            if (this.mFocusAnimation.isStarted()) {
                this.mFocusAnimation.cancel();
            }
            if (getFocusProgress() != (focused ? 1.0f : 0.0f)) {
                this.mFocusAnimation.setFloatValues(getFocusProgress(), focused ? 1.0f : 0.0f);
                this.mFocusAnimation.start();
                return;
            }
            return;
        }
        setFocusImmediate(focused);
    }

    public void setFocusImmediate(boolean focused) {
        if (this.mFocusAnimation.isStarted()) {
            this.mFocusAnimation.cancel();
        }
        setFocusProgress(focused ? 1.0f : 0.0f);
        if (this.mListener != null) {
            this.mListener.onFocusLevelSettled(focused);
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this.mTargetView) {
            setHasFocus(hasFocus);
        }
    }

    protected void setHasFocus(boolean hasFocus) {
        if (this.mEnabled && this.mTargetView.getVisibility() == 0 && this.mTargetView.isAttachedToWindow() && this.mTargetView.hasWindowFocus()) {
            animateFocus(hasFocus);
        } else {
            setFocusImmediate(hasFocus);
        }
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        if (!this.mEnabled && this.mFocusAnimation.isStarted()) {
            this.mFocusAnimation.end();
        }
    }
}
