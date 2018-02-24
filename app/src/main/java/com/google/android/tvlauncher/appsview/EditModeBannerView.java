package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import com.google.android.tvlauncher.R;

public class EditModeBannerView
        extends AppBannerView {
    private View mFrame;
    private OnShowAccessibilityMenuListener mOnShowAccessibilityMenuListener;
    private int mUnselectedTint;

    public EditModeBannerView(Context paramContext) {
        this(paramContext, null);
    }

    public EditModeBannerView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public EditModeBannerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setOnClickListener(this);
        setOnLongClickListener(null);
        setOnFocusChangeListener(null);
        this.mUnselectedTint = ContextCompat.getColor(getContext(), 2131820552);
    }

    public void onClick(View paramView) {
        if (!paramView.isSelected()) {
        }
        for (boolean bool = true; ; bool = false) {
            setSelected(bool);
            if (isSelected()) {
                this.mOnShowAccessibilityMenuListener.onShowAccessibilityMenu(true);
            }
            return;
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mFrame = findViewById(R.id.edit_focused_frame);
        this.mFrame.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline) {
                paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), EditModeBannerView.this.mCornerRadius);
            }
        });
        this.mFrame.setClipToOutline(true);
        setDimmingEnabled(true);
    }

    protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect) {
        super.onFocusChanged(paramBoolean, paramInt, paramRect);
        View v = this.mFrame;

        v.setVisibility(paramBoolean ? 0 : 8);
    }

    public void setAppBannerItems(LaunchItem paramLaunchItem, boolean paramBoolean, OnAppsViewActionListener paramOnAppsViewActionListener, OnShowAccessibilityMenuListener paramOnShowAccessibilityMenuListener) {
        setAppBannerItems(paramLaunchItem, paramBoolean, paramOnAppsViewActionListener);
        this.mOnShowAccessibilityMenuListener = paramOnShowAccessibilityMenuListener;
    }

    public void setDimmingEnabled(boolean paramBoolean) {
        if (paramBoolean) {
            this.mBannerImage.setColorFilter(this.mUnselectedTint);
            return;
        }
        this.mBannerImage.clearColorFilter();
    }

    public void setLaunchItem(LaunchItem paramLaunchItem) {
        super.setLaunchItem(paramLaunchItem);
        this.mBannerImage.setContentDescription(this.mItem.getLabel());
    }

    public void setSelected(boolean paramBoolean) {
        super.setSelected(paramBoolean);


        float f1 = paramBoolean ? this.mFocusedScale : 1.0F;
        float f2 = paramBoolean ? this.mFocusedZDelta : 0.0F;

        setDimmingEnabled(paramBoolean);
        animate().z(f2).scaleX(f1).scaleY(f1).setDuration(this.mAnimDuration);
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/EditModeBannerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */