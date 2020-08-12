package com.amazon.tv.leanbacklauncher.apps;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.leanback.widget.BaseCardView;

import com.amazon.tv.leanbacklauncher.DimmableItem;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer;
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator;

public class SettingsCardView extends BaseCardView implements DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation {
    private int mAnimDuration;
    private ImageView mCircle;
    private ViewDimmer mDimmer;
    private ViewFocusAnimator mFocusAnimator;
    private ImageView mIcon;

    public SettingsCardView(Context context) {
        this(context, null);
    }

    public SettingsCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnimDuration = context.getResources().getInteger(R.integer.item_scale_anim_duration);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCircle = findViewById(R.id.selection_circle);
        this.mIcon = findViewById(R.id.icon);
        this.mFocusAnimator = new ViewFocusAnimator(this);
        this.mDimmer = new ViewDimmer(this);
        this.mDimmer.addDimTarget(this.mIcon);
        this.mDimmer.addDimTarget(this.mCircle);
        this.mDimmer.setDimLevelImmediate();
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        setSelected(gainFocus);
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (this.mCircle != null) {
            this.mCircle.animate().alpha(selected ? 1.0f : 0.0f).setDuration(this.mAnimDuration).start();
        }
    }

    public void setDimState(ViewDimmer.DimState dimState, boolean immediate) {
        this.mDimmer.setDimState(dimState, immediate);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        this.mDimmer.setDimLevelImmediate();
        this.mFocusAnimator.setFocusImmediate(hasFocus());
        setAnimationsEnabled(true);
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.mDimmer.setAnimationEnabled(enabled);
        this.mFocusAnimator.setEnabled(enabled);
    }
}
