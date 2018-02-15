package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.support.annotation.Keep;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.rockon999.android.leanbacklauncher.R;

public class ViewFocusAnimator implements OnFocusChangeListener {
    private boolean mEnabled;
    ObjectAnimator mFocusAnimation;
    private float mFocusProgress;
    private final float mSelectedScaleDelta;
    private final float mSelectedZDelta;
    protected View mTargetView;
    private final float mUnselectedScale;
    private final float mUnselectedZ;

    /* renamed from: ViewFocusAnimator.1 */
    class C01801 extends AnimatorListenerAdapter {
        C01801() {
        }

        public void onAnimationStart(Animator animation) {
            ViewFocusAnimator.this.mTargetView.setHasTransientState(true);
        }

        public void onAnimationEnd(Animator animation) {
            ViewFocusAnimator.this.mTargetView.setHasTransientState(false);
        }
    }

    public ViewFocusAnimator(View view) {
        this.mEnabled = true;
        this.mTargetView = view;
        Resources res = view.getResources();
        this.mTargetView.setOnFocusChangeListener(this);
        TypedValue out = new TypedValue();
        res.getValue(R.raw.unselected_scale, out, true);
        this.mUnselectedScale = out.getFloat();
        this.mSelectedScaleDelta = res.getFraction(R.fraction.lb_focus_zoom_factor_medium, 1, 1) - this.mUnselectedScale;
        this.mUnselectedZ = (float) res.getDimensionPixelOffset(R.dimen.unselected_item_z);
        this.mSelectedZDelta = (float) res.getDimensionPixelOffset(R.dimen.selected_item_z_delta);
        this.mFocusAnimation = ObjectAnimator.ofFloat(this, "focusProgress", new float[]{0.0f});
        this.mFocusAnimation.setDuration((long) res.getInteger(R.integer.item_scale_anim_duration));
        this.mFocusAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        setFocusProgress(0.0f);
        this.mFocusAnimation.addListener(new C01801());
    }

    @Keep
    public void setFocusProgress(float level) {
        this.mFocusProgress = level;
        float scale = this.mUnselectedScale + (this.mSelectedScaleDelta * level);
        float z = this.mUnselectedZ + (this.mSelectedZDelta * level);
        this.mTargetView.setScaleX(scale);
        this.mTargetView.setScaleY(scale);
        this.mTargetView.setZ(z);
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
                this.mFocusAnimation.setFloatValues(new float[]{getFocusProgress(), focused ? 1.0f : 0.0f});
                this.mFocusAnimation.start();
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
