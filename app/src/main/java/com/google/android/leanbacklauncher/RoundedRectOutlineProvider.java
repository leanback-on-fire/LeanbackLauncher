package com.google.android.leanbacklauncher;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

public class RoundedRectOutlineProvider extends ViewOutlineProvider {
    private final float mRadius;

    public RoundedRectOutlineProvider(float radius) {
        this.mRadius = radius;
    }

    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), this.mRadius);
    }
}
