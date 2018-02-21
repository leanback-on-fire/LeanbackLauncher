package com.rockon999.android.leanbacklauncher.apps;

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

import com.rockon999.android.leanbacklauncher.ActiveItemsRowView;
import com.rockon999.android.leanbacklauncher.DimmableItem;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.animation.AppViewFocusAnimator;
import com.rockon999.android.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.rockon999.android.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.rockon999.android.leanbacklauncher.animation.ViewDimmer;
import com.rockon999.android.leanbacklauncher.animation.ViewDimmer.DimState;
import com.rockon999.android.leanbacklauncher.widget.EditModeManager;

import java.util.ArrayList;

public class BannerView extends FrameLayout implements DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation, OnEditModeChangedListener, OnLongClickListener {
    private static boolean DEBUG;
    private static String TAG;
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
    private AppsAdapter.AppViewHolder mViewHolder;
    private int mBannerBackColor = -1;

    public AppsAdapter.AppViewHolder getViewHolder() {
        return mViewHolder;
    }

    /* renamed from: BannerView.1 */
    class C01841 implements Runnable {
        C01841() {
        }

        public void run() {
            BannerView.this.requestLayout();
        }
    }

    static {
        TAG = "LauncherEditMode";
        DEBUG = true;
    }

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSelectedListeners = new ArrayList<>();
        this.mFocusAnimator = new AppViewFocusAnimator(this);
        this.mSelectedListeners.add(this.mFocusAnimator);
        this.mEditModeManager = EditModeManager.getInstance();
        setSelected(false);
        setOnLongClickListener(this);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDimmer = new ViewDimmer(this);
        this.mAppBanner = findViewById(R.id.app_banner);
        this.mInstallStateOverlay = findViewById(R.id.install_state_overlay);
        if (this.mAppBanner instanceof ImageView) {
            this.mAppBanner.setClipToOutline(true);
            this.mDimmer.addDimTarget((ImageView) this.mAppBanner);
        } else {
            View inputBannerView = findViewById(R.id.input_banner);
            if (inputBannerView != null) {
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

    public void setViewHolder(AppsAdapter.AppViewHolder viewHolder) {
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
        Log.i(TAG, "setFocusedFrameState->isEditable:" + isEditable());
        Log.i(TAG, "setFocusedFrameState->mEditMode:" + mEditMode);
        Log.i(TAG, "setFocusedFrameState->hasFocus:" + hasFocus());
        if (!isEditable()) {
            return;
        }
        if (this.mEditMode && hasFocus()) {
            this.mDimmer.setDimState(DimState.ACTIVE, false);
            if (isSelected()) {
                this.mEditFocusFrame.setVisibility(View.GONE);
                //this.mEditFocusFrame.setVisibility(View.VISIBLE);
                return;
            }
            this.mEditFocusFrame.setVisibility(View.VISIBLE);
            post(new C01841());
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
        Log.i(TAG, "onFocusChanged");
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
        return parent instanceof ActiveItemsRowView && ((ActiveItemsRowView) parent).isRowEditable();
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
        Log.i(TAG, "onLongClick->v->className:" + v.getClass().getName());
        if (isEditable() && !this.mEditMode) {
            Log.i(TAG, "onLongClick1");
            this.mEditMode = true;
            setSelected(true);
            setEditMode();
            return true;
        } else if (!isEditable() || !this.mEditMode) {
            Log.i(TAG, "onLongClick2");
            return false;
        } else {
            Log.i(TAG, "onLongClick3");
            onClickInEditMode();
            return true;
        }
    }

    public void setSelected(boolean selected) {
        Log.i(TAG, "setSelected->selected:" + selected);
        Log.i(TAG, "setSelected->mEditMode:" + mEditMode);
        Log.i(TAG, "setSelected->mLeavingEditMode:" + mLeavingEditMode);
        if (this.mEditMode || this.mLeavingEditMode) {
            if (selected != isSelected()) {
                super.setSelected(selected);
                setFocusedFrameState();
                if (!(this.mSelectedListeners.isEmpty() || this.mLeavingEditMode)) {
                    for (BannerSelectedChangedListener listener : this.mSelectedListeners) {
                        listener.onSelectedChanged(this, selected);
                    }
                }
                if (selected) {
                    notifyEditModeManager(selected);
                }
                if (DEBUG || Log.isLoggable(TAG, 2)) {
                    Log.d(TAG, "BannerView selected is now: " + isSelected());
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

    public void setBannerBackColor(int bannerBackColor) {
        mBannerBackColor = bannerBackColor;
    }

    public int getBannerBackColor() {
        return mBannerBackColor;
    }
}
