package com.google.android.tvlauncher.util;

import android.view.ViewGroup.MarginLayoutParams;

public class ScaleAndExpandFocusHandler extends ScaleFocusHandler {
    private static final double EPSILON = 0.01d;
    private double mFocusedAspectRatio;
    private double mUnfocusedAspectRatio;

    public ScaleAndExpandFocusHandler(int animationDuration, float scale, float elevation, int pivot) {
        super(animationDuration, scale, elevation, pivot);
    }

    public void resetFocusedState() {
        updateExpandedState(this.mView.isFocused());
        super.resetFocusedState();
    }

    public double getFocusedAspectRatio() {
        return this.mFocusedAspectRatio;
    }

    public void setFocusedAspectRatio(double focusedAspectRatio) {
        this.mFocusedAspectRatio = focusedAspectRatio;
    }

    public double getUnfocusedAspectRatio() {
        return this.mUnfocusedAspectRatio;
    }

    public void setUnfocusedAspectRatio(double unfocusedAspectRatio) {
        this.mUnfocusedAspectRatio = unfocusedAspectRatio;
    }

    private void updateExpandedState(boolean focused) {
        MarginLayoutParams lp = (MarginLayoutParams) this.mView.getLayoutParams();
        double targetAspectRatio = focused ? this.mFocusedAspectRatio : this.mUnfocusedAspectRatio;
        double currentAspectRatio = 0.0d;
        if (lp.height != 0) {
            currentAspectRatio = ((double) lp.width) / ((double) lp.height);
        }
        if (Math.abs(targetAspectRatio - currentAspectRatio) > EPSILON) {
            lp.width = (int) Math.round(((double) lp.height) * targetAspectRatio);
            this.mView.setLayoutParams(lp);
        }
    }

    protected void animateFocusedState(boolean hasFocus) {
        updateExpandedState(hasFocus);
        super.animateFocusedState(hasFocus);
    }
}
