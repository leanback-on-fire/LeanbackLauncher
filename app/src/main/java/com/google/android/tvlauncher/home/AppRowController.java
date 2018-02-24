package com.google.android.tvlauncher.home;

import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.appsview.AppsViewActivity;
import com.google.android.tvlauncher.home.ChannelView.OnPerformMainActionListener;
import com.google.android.tvlauncher.home.ChannelView.OnStateChangeGesturePerformedListener;
import com.google.android.tvlauncher.home.util.ChannelUtil;

class AppRowController implements HomeRow, OnPerformMainActionListener, OnStateChangeGesturePerformedListener, OnBackPressedListener, OnHomePressedListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "AppRowController";
    private AppsRowEditModeActionCallbacks mAppsRowEditModeActionCallbacks;
    private final ChannelView mChannelView;
    private final EventLogger mEventLogger;
    private AppItemsAdapter mItemsAdapter;
    private final HorizontalGridView mItemsListView;
    private int mLastSelectedItemPosition = -1;
    private OnBackNotHandledListener mOnBackNotHandledListener;
    private OnHomeNotHandledListener mOnHomeNotHandledListener;
    private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
    private OnHomeStateChangeListener mOnHomeStateChangeListener;

    AppRowController(ChannelView channelView, EventLogger eventLogger) {
        this.mChannelView = channelView;
        this.mEventLogger = eventLogger;
        this.mChannelView.setOnPerformMainActionListener(this);
        this.mChannelView.setOnStateChangeGesturePerformedListener(this);
        this.mChannelView.setAllowMoving(false);
        this.mChannelView.setAllowRemoving(false);
        this.mChannelView.setShowItemMeta(false);
        this.mChannelView.setStateSettings(ChannelUtil.getAppsRowStateSettings(channelView.getContext()));
        Context context = this.mChannelView.getContext();
        this.mChannelView.setChannelTitle(context.getString(R.string.action_apps_view));
        this.mItemsListView = this.mChannelView.getItemsListView();
        ImageView logoView = this.mChannelView.getChannelLogoImageView();
        logoView.setContentDescription(context.getString(R.string.action_apps_view));
        logoView.setBackgroundColor(ContextCompat.getColor(context, R.color.apps_row_logo_background));
        logoView.setImageDrawable(context.getDrawable(R.drawable.ic_action_apps_view_black));
        logoView.setImageTintList(ContextCompat.getColorStateList(context, R.color.apps_row_logo_icon_tint));
        logoView.setScaleType(ScaleType.FIT_CENTER);
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.home_app_channel_logo_icon_padding);
        logoView.setPadding(padding, padding, padding, padding);
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

    public void setOnHomeStateChangeListener(OnHomeStateChangeListener listener) {
        this.mOnHomeStateChangeListener = listener;
    }

    public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener listener) {
        this.mOnHomeRowSelectedListener = listener;
    }

    public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener listener) {
    }

    public View getView() {
        return this.mChannelView;
    }

    void setSelectedItemPosition(int position) {
        if (position >= 0 && position < this.mItemsListView.getAdapter().getItemCount()) {
            this.mItemsListView.setSelectedPositionSmooth(position);
        }
    }

    void setOnBackNotHandledListener(OnBackNotHandledListener listener) {
        this.mOnBackNotHandledListener = listener;
    }

    void setOnHomeNotHandledListener(OnHomeNotHandledListener listener) {
        this.mOnHomeNotHandledListener = listener;
    }

    private void ensureItemListIsSetUp() {
        if (this.mItemsAdapter == null) {
            this.mItemsAdapter = new AppItemsAdapter(this.mChannelView.getContext(), this.mEventLogger);
            this.mItemsAdapter.setAppsRowEditModeActionCallbacks(this.mAppsRowEditModeActionCallbacks);
            this.mItemsListView.setAdapter(this.mItemsAdapter);
            this.mItemsListView.setItemAnimator(null);
        }
    }

    void bind(int channelViewState) {
        ensureItemListIsSetUp();
        this.mChannelView.setState(channelViewState);
        int oldAppState = this.mItemsAdapter.getAppState();
        int newAppState = getAppState(channelViewState);
        this.mItemsAdapter.setAppState(newAppState);
        updateItemsListPosition(newAppState, oldAppState);
    }

    private void updateItemsListPosition(int newState, int oldState) {
        if (newState != oldState) {
            if (newState == 3 && this.mItemsAdapter.getItemCount() > 1 && this.mItemsListView.getSelectedPosition() != 0) {
                this.mLastSelectedItemPosition = this.mItemsListView.getSelectedPosition();
                this.mItemsListView.scrollToPosition(0);
            } else if (this.mItemsAdapter.getItemCount() > 1 && this.mLastSelectedItemPosition != -1) {
                int position = this.mLastSelectedItemPosition;
                this.mLastSelectedItemPosition = -1;
                this.mItemsListView.scrollToPosition(position);
            }
        }
    }

    private int getAppState(int channelViewState) {
        switch (channelViewState) {
            case 0:
                return 2;
            case 1:
            case 3:
                return 0;
            case 2:
                return 1;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return 3;
            default:
                return 0;
        }
    }

    public void onPerformMainAction(ChannelView v) {
        AppsViewActivity.startAppsViewActivity(null, v.getContext());
    }

    public void onStateChangeGesturePerformed(ChannelView v, int newState) {
        switch (newState) {
            case 0:
                if (this.mOnHomeRowSelectedListener != null) {
                    this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                }
                if (this.mOnHomeStateChangeListener != null) {
                    this.mOnHomeStateChangeListener.onHomeStateChange(0);
                    return;
                }
                return;
            case 1:
                if (this.mOnHomeStateChangeListener != null) {
                    this.mOnHomeStateChangeListener.onHomeStateChange(0);
                    return;
                }
                return;
            case 2:
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
                throw new IllegalStateException("Unsupported ChannelView state change gesture: " + ChannelView.stateToString(newState));
            case 4:
                if (this.mOnHomeRowSelectedListener != null) {
                    this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                }
                if (this.mOnHomeStateChangeListener != null) {
                    this.mOnHomeStateChangeListener.onHomeStateChange(1);
                    return;
                }
                return;
            case 5:
                if (this.mOnHomeStateChangeListener != null) {
                    this.mOnHomeStateChangeListener.onHomeStateChange(1);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onBackPressed(Context c) {
        if (!handleHomeBackPress() && this.mOnBackNotHandledListener != null) {
            this.mOnBackNotHandledListener.onBackNotHandled(c);
        }
    }

    public void onHomePressed(Context c) {
        if (!handleHomeBackPress() && this.mOnHomeNotHandledListener != null) {
            this.mOnHomeNotHandledListener.onHomeNotHandled(c);
        }
    }

    private boolean handleHomeBackPress() {
        if (this.mChannelView.getState() == 0 && this.mItemsListView.getAdapter().getItemCount() > 0) {
            View selectedChild = this.mItemsListView.getFocusedChild();
            if (selectedChild instanceof FavoriteAppBannerView) {
                FavoriteAppBannerView selectedApp = (FavoriteAppBannerView) selectedChild;
                if (selectedApp.isBeingMoved()) {
                    this.mAppsRowEditModeActionCallbacks.onExitEditMode();
                    selectedApp.setIsBeingMoved(false);
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
}
