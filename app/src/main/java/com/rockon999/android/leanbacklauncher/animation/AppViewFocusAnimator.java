package com.rockon999.android.leanbacklauncher.animation;

import android.view.View;
import com.rockon999.android.leanbacklauncher.apps.BannerSelectedChangedListener;
import com.rockon999.android.leanbacklauncher.apps.BannerView;

public final class AppViewFocusAnimator extends ViewFocusAnimator implements BannerSelectedChangedListener {
    private boolean mSelected;

    public AppViewFocusAnimator(BannerView view) {
        super(view);
    }

    public void onSelectedChanged(BannerView v, boolean selected) {
        if (v == this.mTargetView) {
            this.mSelected = selected;
            setHasFocus(selected);
        }
    }

    public void onEditModeChanged(BannerView v, boolean editMode) {
        if (v == this.mTargetView && !editMode) {
            setHasFocus(v.hasFocus());
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this.mTargetView) {
            if (!((BannerView) v).isEditMode() || this.mSelected) {
                super.onFocusChange(v, hasFocus);
            }
        }
    }
}
