package com.google.android.leanbacklauncher.apps;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.leanbacklauncher.DimmableItem;
import com.google.android.leanbacklauncher.EditableAppsRowView;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.RoundedRectOutlineProvider;
import com.google.android.leanbacklauncher.animation.AppViewFocusAnimator;
import com.google.android.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.google.android.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.google.android.leanbacklauncher.animation.ViewDimmer;
import com.google.android.leanbacklauncher.animation.ViewDimmer.DimState;
import com.google.android.leanbacklauncher.widget.EditModeManager;
import java.util.ArrayList;
import java.util.Iterator;

public class BannerView extends FrameLayout implements OnLongClickListener, DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation, OnEditModeChangedListener {
    private static RoundedRectOutlineProvider sOutline;
    private View mAppBanner;
    private ViewDimmer mDimmer;
    private ImageView mEditFocusFrame;
    private OnEditModeChangedListener mEditListener;
    private boolean mEditMode;
    private EditModeManager mEditModeManager;
    private AppViewFocusAnimator mFocusAnimator;
    private View mInstallStateOverlay;
    private BannerView mLastFocusedBanner;
    private boolean mLeavingEditMode;
    private ArrayList<BannerSelectedChangedListener> mSelectedListeners;
    private AppViewHolder mViewHolder;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSelectedListeners = new ArrayList();
        this.mFocusAnimator = new AppViewFocusAnimator(this);
        this.mSelectedListeners.add(this.mFocusAnimator);
        this.mEditModeManager = EditModeManager.getInstance();
        setSelected(false);
        setOnLongClickListener(this);
        if (sOutline == null) {
            sOutline = new RoundedRectOutlineProvider((float) getResources().getDimensionPixelOffset(R.dimen.banner_corner_radius));
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDimmer = new ViewDimmer(this);
        this.mAppBanner = findViewById(R.id.app_banner);
        this.mInstallStateOverlay = findViewById(R.id.install_state_overlay);
        if (this.mAppBanner instanceof ImageView) {
            this.mAppBanner.setOutlineProvider(sOutline);
            this.mAppBanner.setClipToOutline(true);
            this.mDimmer.addDimTarget((ImageView) this.mAppBanner);
        } else {
            View inputBannerView = findViewById(R.id.input_banner);
            if (inputBannerView != null) {
                inputBannerView.setOutlineProvider(sOutline);
                inputBannerView.setClipToOutline(true);
            }
            View bannerView = findViewById(R.id.banner_icon);
            if (bannerView instanceof ImageView) {
                this.mDimmer.addDimTarget((ImageView) bannerView);
            }
            View bannerLabel = findViewById(R.id.banner_label);
            if (bannerLabel instanceof TextView) {
                this.mDimmer.addDimTarget((TextView) bannerLabel);
            }
            bannerView = findViewById(R.id.input_image);
            if (bannerView instanceof ImageView) {
                this.mDimmer.addDimTarget((ImageView) bannerView);
            }
            bannerLabel = findViewById(R.id.input_label);
            if (bannerLabel instanceof TextView) {
                this.mDimmer.addDimTarget((TextView) bannerLabel);
            }
            if (getBackground() != null) {
                this.mDimmer.addDimTarget(getBackground());
            }
        }
        this.mDimmer.setDimLevelImmediate();
        View view = findViewById(R.id.edit_focused_frame);
        if (view instanceof ImageView) {
            this.mEditFocusFrame = (ImageView) view;
        }
    }

    public ViewDimmer getViewDimmer() {
        return this.mDimmer;
    }

    public void setViewHolder(AppViewHolder viewHolder) {
        this.mViewHolder = viewHolder;
    }

    public void setTextViewColor(TextView textView, int color) {
        if (this.mDimmer != null) {
            this.mDimmer.setTargetTextColor(textView, color);
        }
    }

    public boolean isEditMode() {
        return this.mEditMode;
    }

    public void setEditMode(boolean editMode) {
        if (isEditable()) {
            this.mEditMode = editMode;
        }
        if (!editMode) {
            this.mLastFocusedBanner = null;
        }
    }

    private void setEditMode() {
        if (this.mEditListener != null) {
            this.mEditListener.onEditModeChanged(this.mEditMode);
        }
    }

    public void addSelectedListener(BannerSelectedChangedListener listener) {
        this.mSelectedListeners.add(listener);
    }

    public void clearBannerForRecycle() {
        clearFocus();
        this.mEditFocusFrame.setVisibility(8);
    }

    public void removeSelectedListener(BannerSelectedChangedListener listener) {
        this.mSelectedListeners.remove(listener);
    }

    public void setOnEditModeChangedListener(OnEditModeChangedListener listener) {
        this.mEditListener = listener;
    }

    public void setFocusedFrameState() {
        if (!isEditable()) {
            return;
        }
        if (this.mEditMode && hasFocus()) {
            this.mDimmer.setDimState(DimState.ACTIVE, false);
            if (isSelected()) {
                this.mEditFocusFrame.setVisibility(8);
                return;
            }
            this.mEditFocusFrame.setVisibility(0);
            post(new Runnable() {
                public void run() {
                    BannerView.this.requestLayout();
                }
            });
            return;
        }
        this.mEditFocusFrame.setVisibility(8);
    }

    public void onEditModeChanged(boolean editMode) {
        if (isEditable()) {
            this.mEditMode = editMode;
            if (hasFocus()) {
                if (!editMode) {
                    this.mLeavingEditMode = true;
                }
                setSelected(editMode);
                setFocusedFrameState();
                this.mFocusAnimator.onEditModeChanged(this, editMode);
            }
        }
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (this.mEditMode && gainFocus && isEditModeSelected()) {
            setSelected(true);
            sendAccessibilityEvent(8);
        }
        setFocusedFrameState();
    }

    public boolean requestFocus(int direction, Rect previous) {
        if (this.mLastFocusedBanner == null || this.mLastFocusedBanner == this) {
            return super.requestFocus(direction, previous);
        }
        this.mLastFocusedBanner.requestFocus();
        return false;
    }

    public void sendAccessibilityEvent(int eventType) {
        if (!this.mEditMode || isSelected() || this.mEditModeManager.getSelectedComponentName() == null) {
            super.sendAccessibilityEvent(eventType);
        }
    }

    public boolean isEditable() {
        ViewParent parent = getParent();
        if (parent instanceof EditableAppsRowView) {
            return ((EditableAppsRowView) parent).isRowEditable();
        }
        return false;
    }

    public void onClickInEditMode() {
        boolean z = false;
        if (isEditable()) {
            notifyEditModeManager(false);
            if (!isSelected()) {
                z = true;
            }
            setSelected(z);
        }
    }

    public boolean onLongClick(View v) {
        if (isEditable() && !this.mEditMode) {
            this.mEditMode = true;
            setSelected(true);
            setEditMode();
            return true;
        } else if (!isEditable() || !this.mEditMode) {
            return false;
        } else {
            onClickInEditMode();
            return true;
        }
    }

    public void setSelected(boolean selected) {
        if (this.mEditMode || this.mLeavingEditMode) {
            if (selected != isSelected()) {
                super.setSelected(selected);
                setFocusedFrameState();
                if (!(this.mSelectedListeners.isEmpty() || this.mLeavingEditMode)) {
                    Iterator it = this.mSelectedListeners.iterator();
                    while (it.hasNext()) {
                        ((BannerSelectedChangedListener) it.next()).onSelectedChanged(this, selected);
                    }
                }
                if (selected) {
                    notifyEditModeManager(selected);
                }
                if (Log.isLoggable("LauncherEditMode", 2)) {
                    Log.d("LauncherEditMode", "BannerView selected is now: " + isSelected());
                }
            }
            this.mLeavingEditMode = false;
        }
    }

    private boolean isEditModeSelected() {
        return TextUtils.equals(this.mViewHolder.getComponentName(), this.mEditModeManager.getSelectedComponentName());
    }

    public void notifyEditModeManager(boolean selected) {
        if (selected) {
            this.mEditModeManager.setSelectedComponentName(this.mViewHolder.getComponentName());
        } else {
            this.mEditModeManager.setSelectedComponentName(null);
        }
    }

    public void setDimState(DimState dimState, boolean immediate) {
        this.mDimmer.setDimState(dimState, immediate);
    }

    public void setLastFocusedBanner(BannerView view) {
        this.mLastFocusedBanner = view;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        this.mDimmer.setDimLevelImmediate();
        this.mFocusAnimator.setFocusImmediate(hasFocus());
        setAnimationsEnabled(true);
    }

    public void setZ(float z) {
        if ((this.mAppBanner instanceof ImageView) || (this.mAppBanner instanceof LinearLayout)) {
            this.mAppBanner.setZ(z);
            this.mInstallStateOverlay.setZ(z);
            return;
        }
        super.setZ(z);
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.mFocusAnimator.setEnabled(enabled);
        this.mDimmer.setAnimationEnabled(enabled);
    }
}
