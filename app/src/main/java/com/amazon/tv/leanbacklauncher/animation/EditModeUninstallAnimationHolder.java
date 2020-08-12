package com.amazon.tv.leanbacklauncher.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.amazon.tv.leanbacklauncher.EditableAppsRowView;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.apps.BannerView;
import com.amazon.tv.leanbacklauncher.widget.EditModeView;

public class EditModeUninstallAnimationHolder {
    private long mAnimationDuration;
    private TranslateAnimation mBannerAnimation;
    private View mUninstallBanner;
    private View mUninstallCircle;
    private View mUninstallIcon;
    private View mUninstallIconCircle;
    private TextView mUninstallText;

    public enum EditModeUninstallState {
        ENTER,
        EXIT
    }

    public EditModeUninstallAnimationHolder(EditModeView editMode) {
        this.mUninstallBanner = editMode.getUninstallApp();
        this.mUninstallCircle = editMode.getUninstallCircle();
        this.mUninstallIcon = editMode.getUninstallIcon();
        this.mUninstallText = editMode.getUninstallText();
        this.mUninstallIconCircle = editMode.getUninstallIconCircle();
        this.mAnimationDuration = this.mUninstallBanner.getResources().getInteger(R.integer.edit_mode_uninstall_anim_duration);
    }

    private void addPositionDependantAnimations(EditModeUninstallState uninstallState, BannerView curBanner, EditableAppsRowView activeitems) {
        addBannerDragAnimation(uninstallState, curBanner, activeitems);
    }

    private void animateIconCircle(EditModeUninstallState state) {
        this.mUninstallIconCircle.animate().setDuration(this.mAnimationDuration);
        if (state == EditModeUninstallState.ENTER) {
            this.mUninstallIconCircle.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f);
        } else {
            this.mUninstallIconCircle.animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f);
        }
    }

    private void animateUninstallText(EditModeUninstallState state) {
        this.mUninstallText.animate().setDuration(this.mAnimationDuration);
        if (state == EditModeUninstallState.ENTER) {
            this.mUninstallText.animate().alpha(1.0f);
        } else {
            this.mUninstallText.animate().alpha(0.0f);
        }
    }

    private void animateUninstallCircle(EditModeUninstallState state) {
        int focusWidth = this.mUninstallCircle.getResources().getDimensionPixelSize(R.dimen.edit_uninstall_area_circle_focus_width);
        int focusHeight = this.mUninstallCircle.getResources().getDimensionPixelSize(R.dimen.edit_uninstall_area_circle_focus_height);
        int unfocusWidth = this.mUninstallCircle.getResources().getDimensionPixelSize(R.dimen.edit_uninstall_area_circle_width);
        int unfocusHeight = this.mUninstallCircle.getResources().getDimensionPixelSize(R.dimen.edit_uninstall_area_circle_height);
        this.mUninstallCircle.animate().setDuration(this.mAnimationDuration);
        if (state == EditModeUninstallState.ENTER) {
            this.mUninstallCircle.animate().scaleX(((float) focusWidth) / ((float) unfocusWidth)).scaleY(((float) focusHeight) / ((float) unfocusHeight)).alpha(0.15f);
        } else {
            this.mUninstallCircle.animate().scaleX(1.0f).scaleY(1.0f).alpha(0.05f);
        }
    }

    private void animateUninstallIcon(EditModeUninstallState state) {
        int focusSize = this.mUninstallIcon.getResources().getDimensionPixelSize(R.dimen.edit_uninstall_icon_focused_size);
        int unFocusSize = this.mUninstallIcon.getResources().getDimensionPixelSize(R.dimen.edit_uninstall_icon_unfocused_size);
        int circleFocusBottomMargin = this.mUninstallIcon.getResources().getDimensionPixelSize(R.dimen.edit_uninstall_icon_circle_focused_bottom_margin);
        int[] circleLocation = new int[2];
        this.mUninstallIconCircle.getLocationOnScreen(circleLocation);
        int[] iconLocation = new int[2];
        this.mUninstallIcon.getLocationOnScreen(iconLocation);
        this.mUninstallIcon.animate().setDuration(this.mAnimationDuration);
        if (state == EditModeUninstallState.ENTER) {
            this.mUninstallIcon.animate().scaleX(((float) focusSize) / ((float) unFocusSize)).scaleY(((float) focusSize) / ((float) unFocusSize)).translationY((float) (((circleLocation[1] - iconLocation[1]) - focusSize) + circleFocusBottomMargin));
        } else {
            this.mUninstallIcon.animate().scaleX(1.0f).scaleY(1.0f).translationY(0.0f);
        }
    }

    private void addBannerDragAnimation(final EditModeUninstallState uninstallState, BannerView curBanner, final EditableAppsRowView activeitems) {
        float toXDelta;
        float toYDelta;
        int[] curBannerLocation = new int[2];
        int[] uninstallBannerLocation = new int[2];
        float bannerSelectedScaleDelta = this.mUninstallBanner.getResources().getFraction(R.fraction.lb_focus_zoom_factor_medium, 1, 1) - this.mUninstallBanner.getResources().getFraction(R.fraction.unselected_scale, 1, 1);
        curBanner.getLocationOnScreen(curBannerLocation);
        this.mUninstallBanner.getLocationOnScreen(uninstallBannerLocation);
        float fromXDelta = uninstallState == EditModeUninstallState.ENTER ? (float) (curBannerLocation[0] - uninstallBannerLocation[0]) : 0.0f;
        if (uninstallState == EditModeUninstallState.ENTER) {
            toXDelta = 0.0f;
        } else {
            toXDelta = ((float) (curBannerLocation[0] - uninstallBannerLocation[0])) - ((((float) curBanner.getWidth()) * bannerSelectedScaleDelta) / 2.0f);
        }
        float fromYDelta = uninstallState == EditModeUninstallState.ENTER ? (float) (curBannerLocation[1] - uninstallBannerLocation[1]) : 0.0f;
        if (uninstallState == EditModeUninstallState.ENTER) {
            toYDelta = 0.0f;
        } else {
            toYDelta = ((float) (curBannerLocation[1] - uninstallBannerLocation[1])) - ((((float) curBanner.getHeight()) * bannerSelectedScaleDelta) / 2.0f);
        }
        this.mBannerAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        this.mBannerAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                if (uninstallState == EditModeUninstallState.ENTER) {
                    EditModeUninstallAnimationHolder.this.mUninstallBanner.setVisibility(View.VISIBLE);
                    return;
                }
                EditModeUninstallAnimationHolder.this.mUninstallBanner.setVisibility(View.GONE);
                activeitems.setBannerDrawableUninstallState(false);
            }

            public void onAnimationStart(Animation arg0) {
                EditModeUninstallAnimationHolder.this.mUninstallBanner.setVisibility(View.VISIBLE);
                if (uninstallState == EditModeUninstallState.ENTER) {
                    activeitems.setBannerDrawableUninstallState(true);
                }
            }

            public void onAnimationRepeat(Animation arg0) {
            }
        });
        this.mBannerAnimation.setDuration(this.mUninstallBanner.getResources().getInteger(R.integer.edit_mode_uninstall_anim_duration));
    }

    public void startAnimation(EditModeUninstallState uninstallState, BannerView curBanner, EditableAppsRowView activeitems) {
        addPositionDependantAnimations(uninstallState, curBanner, activeitems);
        this.mUninstallBanner.startAnimation(this.mBannerAnimation);
        animateIconCircle(uninstallState);
        animateUninstallText(uninstallState);
        animateUninstallCircle(uninstallState);
        animateUninstallIcon(uninstallState);
    }

    public void setViewsToExitState() {
        long tempDuration = this.mAnimationDuration;
        this.mAnimationDuration = 0;
        animateIconCircle(EditModeUninstallState.EXIT);
        animateUninstallText(EditModeUninstallState.EXIT);
        animateUninstallCircle(EditModeUninstallState.EXIT);
        animateUninstallIcon(EditModeUninstallState.EXIT);
        this.mAnimationDuration = tempDuration;
    }
}
