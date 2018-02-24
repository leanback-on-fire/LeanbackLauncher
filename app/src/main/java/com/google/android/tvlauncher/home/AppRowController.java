package com.google.android.tvlauncher.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.google.android.tvlauncher.BackHomeControllerListeners;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.appsview.AppsViewActivity;
import com.google.android.tvlauncher.home.util.ChannelUtil;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

class AppRowController
        implements HomeRow, ChannelView.OnPerformMainActionListener, ChannelView.OnStateChangeGesturePerformedListener, BackHomeControllerListeners.OnBackPressedListener, BackHomeControllerListeners.OnHomePressedListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "AppRowController";
    private AppsRowEditModeActionCallbacks mAppsRowEditModeActionCallbacks;
    private final ChannelView mChannelView;
    private final EventLogger mEventLogger;
    private AppItemsAdapter mItemsAdapter;
    private final HorizontalGridView mItemsListView;
    private int mLastSelectedItemPosition = -1;
    private BackHomeControllerListeners.OnBackNotHandledListener mOnBackNotHandledListener;
    private BackHomeControllerListeners.OnHomeNotHandledListener mOnHomeNotHandledListener;
    private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
    private OnHomeStateChangeListener mOnHomeStateChangeListener;

    AppRowController(ChannelView paramChannelView, EventLogger paramEventLogger) {
        this.mChannelView = paramChannelView;
        this.mEventLogger = paramEventLogger;
        this.mChannelView.setOnPerformMainActionListener(this);
        this.mChannelView.setOnStateChangeGesturePerformedListener(this);
        this.mChannelView.setAllowMoving(false);
        this.mChannelView.setAllowRemoving(false);
        this.mChannelView.setShowItemMeta(false);
        this.mChannelView.setStateSettings(ChannelUtil.getAppsRowStateSettings(paramChannelView.getContext()));
        Context context = this.mChannelView.getContext();
        this.mChannelView.setChannelTitle(context.getString(R.string.action_apps_view));
        this.mItemsListView = this.mChannelView.getItemsListView();
        ImageView view = this.mChannelView.getChannelLogoImageView();
        view.setContentDescription(context.getString(R.string.action_apps_view));
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.apps_row_logo_background)); // edited for API 22
        view.setImageDrawable(context.getDrawable(R.drawable.ic_action_apps_view_black));
        view.setImageTintList(ContextCompat.getColorStateList(context, 2131820554));
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int i = context.getResources().getDimensionPixelOffset(R.dimen.home_app_channel_logo_icon_padding);
        view.setPadding(i, i, i, i);
        this.mAppsRowEditModeActionCallbacks = new AppsRowEditModeActionCallbacks() {
            public void onEnterEditMode() {
                AppRowController.this.mChannelView.setAllowZoomOut(false);
                AppRowController.this.mItemsListView.setItemAnimator(new DefaultItemAnimator());
            }

            public void onExitEditMode() {
                AppRowController.this.mChannelView.setAllowZoomOut(true);
                AppRowController.this.mItemsListView.setItemAnimator(null);
            }
        };
    }

    private void ensureItemListIsSetUp() {
        if (this.mItemsAdapter != null) {
            return;
        }
        this.mItemsAdapter = new AppItemsAdapter(this.mChannelView.getContext(), this.mEventLogger);
        this.mItemsAdapter.setAppsRowEditModeActionCallbacks(this.mAppsRowEditModeActionCallbacks);
        this.mItemsListView.setAdapter(this.mItemsAdapter);
        this.mItemsListView.setItemAnimator(null);
    }

    // todo find these constants
    private int getAppState(int paramInt) {
        switch (paramInt) {
            case 0:
                return 2;
            case 1:
            case 3:
                return 0;
            case 2:
                return 1;
            case 4: // todo wrong?
                return 3;
            default:
                return 0;
        }
    }

    @SuppressLint("RestrictedApi")
    private boolean handleHomeBackPress() {
        if ((this.mChannelView.getState() == 0) && (this.mItemsListView.getAdapter().getItemCount() > 0)) {
            Object localObject = this.mItemsListView.getFocusedChild();
            if ((localObject instanceof FavoriteAppBannerView)) {
                FavoriteAppBannerView bannerView = (FavoriteAppBannerView) localObject;
                if (bannerView.isBeingMoved()) {
                    this.mAppsRowEditModeActionCallbacks.onExitEditMode();
                    bannerView.setIsBeingMoved(false);
                    return true;
                }
            }
            if (this.mItemsListView.getSelectedPosition() != 0) {
                setSelectedItemPosition(0);
                return true;
            }
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    private void updateItemsListPosition(int paramInt1, int paramInt2) {
        if (paramInt1 == paramInt2) {
            return;
        }
        if ((paramInt1 == 3) && (this.mItemsAdapter.getItemCount() > 1) && (this.mItemsListView.getSelectedPosition() != 0)) {
            paramInt1 = this.mLastSelectedItemPosition;
            this.mLastSelectedItemPosition = -1;
            this.mItemsListView.scrollToPosition(paramInt1);
        } else if ((this.mItemsAdapter.getItemCount() <= 1) || (this.mLastSelectedItemPosition == -1)) {
            this.mLastSelectedItemPosition = this.mItemsListView.getSelectedPosition();
            this.mItemsListView.scrollToPosition(0);
        }
    }

    void bind(int paramInt) {
        ensureItemListIsSetUp();
        this.mChannelView.setState(paramInt);
        int i = this.mItemsAdapter.getAppState();
        paramInt = getAppState(paramInt);
        this.mItemsAdapter.setAppState(paramInt);
        updateItemsListPosition(paramInt, i);
    }

    public View getView() {
        return this.mChannelView;
    }

    public void onBackPressed(Context paramContext) {
        if (handleHomeBackPress()) {
        }
        while (this.mOnBackNotHandledListener == null) {
            return;
        }
        this.mOnBackNotHandledListener.onBackNotHandled(paramContext);
    }

    public void onHomePressed(Context paramContext) {
        if (handleHomeBackPress()) {
        }
        while (this.mOnHomeNotHandledListener == null) {
            return;
        }
        this.mOnHomeNotHandledListener.onHomeNotHandled(paramContext);
    }

    public void onPerformMainAction(ChannelView paramChannelView) {
        AppsViewActivity.startAppsViewActivity(null, paramChannelView.getContext());
    }

    // todo messy, probably wrong
    public void onStateChangeGesturePerformed(ChannelView paramChannelView, int paramInt) {
        switch (paramInt) {
            case 0:
                if (this.mOnHomeRowSelectedListener != null) {
                    this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                }
                this.mOnHomeStateChangeListener.onHomeStateChange(1);
                break;
            case 1:
                this.mOnHomeStateChangeListener.onHomeStateChange(0);
                break;

            case 4:
                this.mOnHomeStateChangeListener.onHomeStateChange(0);
                break;
            case 5:
                if (this.mOnHomeRowSelectedListener != null) {
                    this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                }
                this.mOnHomeStateChangeListener.onHomeStateChange(1);
                break;
            default:
                throw new IllegalStateException("Unsupported ChannelView state change gesture: " + ChannelView.stateToString(paramInt));
        }
    }

    void setOnBackNotHandledListener(BackHomeControllerListeners.OnBackNotHandledListener paramOnBackNotHandledListener) {
        this.mOnBackNotHandledListener = paramOnBackNotHandledListener;
    }

    void setOnHomeNotHandledListener(BackHomeControllerListeners.OnHomeNotHandledListener paramOnHomeNotHandledListener) {
        this.mOnHomeNotHandledListener = paramOnHomeNotHandledListener;
    }

    public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener paramOnHomeRowRemovedListener) {
    }

    public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener paramOnHomeRowSelectedListener) {
        this.mOnHomeRowSelectedListener = paramOnHomeRowSelectedListener;
    }

    public void setOnHomeStateChangeListener(OnHomeStateChangeListener paramOnHomeStateChangeListener) {
        this.mOnHomeStateChangeListener = paramOnHomeStateChangeListener;
    }

    void setSelectedItemPosition(int paramInt) {
        if ((paramInt >= 0) && (paramInt < this.mItemsListView.getAdapter().getItemCount())) {
            this.mItemsListView.setSelectedPositionSmooth(paramInt);
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/AppRowController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */