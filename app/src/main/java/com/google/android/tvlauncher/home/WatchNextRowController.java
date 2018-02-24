package com.google.android.tvlauncher.home;

import android.app.Activity;
import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.view.View;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.home.ChannelView.OnPerformMainActionListener;
import com.google.android.tvlauncher.home.ChannelView.OnRemoveListener;
import com.google.android.tvlauncher.home.ChannelView.OnStateChangeGesturePerformedListener;
import com.google.android.tvlauncher.home.util.ChannelUtil;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;

class WatchNextRowController implements HomeRow, OnPerformMainActionListener, OnRemoveListener, OnStateChangeGesturePerformedListener, OnBackPressedListener, OnHomePressedListener {
    private static final int ACCESSIBILITY_MENU_DONE = 1;
    private static final int ACCESSIBILITY_MENU_REMOVE = 0;
    private static final boolean DEBUG = false;
    private static final int LAST_ITEM_META_MESSAGE_LINE_COUNT = 3;
    private static final String TAG = "WatchNextRowController";
    private AccessibilityContextMenu mAccessibilityContextMenu;
    private final ChannelView mChannelView;
    private final EventLogger mEventLogger;
    private WatchNextItemsAdapter mItemsAdapter;
    private final HorizontalGridView mItemsListView;
    private final String mLastItemMetaMessage;
    private final String mLastItemMetaTitle;
    private int mLastSelectedItemPosition = -1;
    private ChannelItemMetadataController mMetadataController;
    private OnBackNotHandledListener mOnBackNotHandledListener;
    private OnHomeNotHandledListener mOnHomeNotHandledListener;
    private OnHomeRowRemovedListener mOnHomeRowRemovedListener;
    private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
    private OnHomeStateChangeListener mOnHomeStateChangeListener;
    private OnProgramSelectedListener mOnProgramSelectedListener;

    WatchNextRowController(ChannelView channelView, EventLogger eventLogger) {
        this.mChannelView = channelView;
        this.mEventLogger = eventLogger;
        this.mChannelView.setOnPerformMainActionListener(this);
        this.mChannelView.setOnRemoveListener(this);
        this.mChannelView.setOnStateChangeGesturePerformedListener(this);
        this.mChannelView.setAllowMoving(false);
        this.mChannelView.setStateSettings(ChannelUtil.getDefaultChannelStateSettings(channelView.getContext()));
        Context context = this.mChannelView.getContext();
        this.mChannelView.setChannelTitle(context.getString(R.string.watch_next_channel_title));
        ChannelUtil.setWatchNextLogo(this.mChannelView.getChannelLogoImageView());
        this.mItemsListView = this.mChannelView.getItemsListView();
        ChannelUtil.configureItemsListAlignment(this.mItemsListView);
        this.mLastItemMetaTitle = context.getString(R.string.watch_next_channel_title);
        this.mLastItemMetaMessage = context.getString(R.string.watch_next_last_item_message);
    }

    public void setOnHomeStateChangeListener(OnHomeStateChangeListener listener) {
        this.mOnHomeStateChangeListener = listener;
    }

    public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener listener) {
        this.mOnHomeRowSelectedListener = listener;
    }

    public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener listener) {
        this.mOnHomeRowRemovedListener = listener;
    }

    public View getView() {
        return this.mChannelView;
    }

    void setOnProgramSelectedListener(OnProgramSelectedListener listener) {
        this.mOnProgramSelectedListener = listener;
    }

    void setOnBackNotHandledListener(OnBackNotHandledListener listener) {
        this.mOnBackNotHandledListener = listener;
    }

    void setOnHomeNotHandledListener(OnHomeNotHandledListener listener) {
        this.mOnHomeNotHandledListener = listener;
    }

    private void ensureItemListIsSetUp() {
        if (this.mItemsAdapter == null) {
            this.mItemsAdapter = new WatchNextItemsAdapter(this.mChannelView.getContext(), this.mEventLogger);
            this.mItemsAdapter.setOnProgramSelectedListener(this.mOnProgramSelectedListener);
            this.mItemsListView.setAdapter(this.mItemsAdapter);
            this.mItemsListView.setItemAnimator(null);
            this.mMetadataController = new ChannelItemMetadataController(this.mChannelView.getItemMetadataView());
        }
    }

    public void onStart() {
        this.mItemsAdapter.onStart();
    }

    public void onStop() {
        this.mItemsAdapter.onStop();
    }

    void recycle() {
        this.mLastSelectedItemPosition = -1;
        this.mItemsAdapter.recycle();
        this.mMetadataController.clear();
        if (this.mAccessibilityContextMenu != null) {
            this.mAccessibilityContextMenu.dismiss();
        }
    }

    void bind(int channelViewState) {
        ensureItemListIsSetUp();
        this.mChannelView.setState(channelViewState);
        int oldProgramState = this.mItemsAdapter.getProgramState();
        int newProgramState = getProgramState(channelViewState);
        this.mItemsAdapter.bind(newProgramState);
        updateItemsListPosition(newProgramState, oldProgramState);
    }

    public void setState(int channelViewState) {
        this.mChannelView.setState(channelViewState);
        int oldProgramState = this.mItemsAdapter.getProgramState();
        int newProgramState = getProgramState(channelViewState);
        this.mItemsAdapter.setProgramState(newProgramState);
        updateItemsListPosition(newProgramState, oldProgramState);
    }

    private void updateItemsListPosition(int newState, int oldState) {
        if (newState != oldState) {
            if (newState == 2 && this.mItemsAdapter.getItemCount() > 1 && this.mItemsListView.getSelectedPosition() != 0) {
                this.mLastSelectedItemPosition = this.mItemsListView.getSelectedPosition();
                this.mItemsListView.scrollToPosition(0);
            } else if (this.mItemsAdapter.getItemCount() > 1 && this.mLastSelectedItemPosition != -1) {
                int position = this.mLastSelectedItemPosition;
                this.mLastSelectedItemPosition = -1;
                this.mItemsListView.scrollToPosition(position);
            }
        }
    }

    private int getProgramState(int channelViewState) {
        switch (channelViewState) {
            case 0:
                return 1;
            case 1:
            case 2:
            case 3:
                return 0;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return 2;
            default:
                return 0;
        }
    }

    void bindItemMetadata() {
        int position = this.mItemsListView.getSelectedPosition();
        TvDataManager dataManager = TvDataManager.getInstance(this.mItemsListView.getContext());
        if (position == -1 || this.mItemsAdapter.getItemCount() == 0) {
            this.mMetadataController.clear();
        } else if (this.mItemsAdapter.getItemCount() > 0 && this.mItemsAdapter.getItemViewType(position) == 0) {
            this.mMetadataController.clear();
            this.mMetadataController.setFirstRow(this.mLastItemMetaTitle);
            this.mMetadataController.setThirdRow(this.mLastItemMetaMessage, 3);
        } else if (position >= 0 && position < dataManager.getWatchNextProgramsCount()) {
            this.mMetadataController.bindView(dataManager.getWatchNextProgram(position));
        }
    }

    public void onPerformMainAction(ChannelView v) {
        if (Util.isAccessibilityEnabled(v.getContext())) {
            showAccessibilityMenu();
        }
    }

    private void showAccessibilityMenu() {
        if (this.mAccessibilityContextMenu == null) {
            Context context = this.mChannelView.getContext();
            this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity) context);
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, context.getString(R.string.channel_action_remove), context.getDrawable(R.drawable.ic_remove_circle_black)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, context.getString(R.string.accessibility_menu_item_done), context.getDrawable(R.drawable.ic_done_black_24dp)));
            this.mAccessibilityContextMenu.setOnMenuItemClickListener(new OnItemClickListener() {
                public void onItemClick(ContextMenuItem item) {
                    switch (item.getId()) {
                        case 0:
                            WatchNextRowController.this.mAccessibilityContextMenu.dismiss();
                            WatchNextRowController.this.onRemove(WatchNextRowController.this.mChannelView);
                            return;
                        case 1:
                            WatchNextRowController.this.mAccessibilityContextMenu.dismiss();
                            return;
                        default:
                            return;
                    }
                }
            });
        }
        this.mAccessibilityContextMenu.show();
    }

    public void onRemove(ChannelView v) {
        if (this.mOnHomeRowRemovedListener != null) {
            this.mOnHomeRowRemovedListener.onHomeRowRemoved(this);
        }
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
            case 6:
                if (this.mOnHomeRowSelectedListener != null) {
                    this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                }
                if (this.mOnHomeStateChangeListener != null) {
                    this.mOnHomeStateChangeListener.onHomeStateChange(2);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onBackPressed(Context c) {
        if (this.mChannelView.getState() == 0 && this.mItemsListView.getSelectedPosition() != 0 && this.mItemsListView.getAdapter().getItemCount() > 0) {
            this.mItemsListView.setSelectedPositionSmooth(0);
        } else if (this.mChannelView.getState() == 6) {
            onStateChangeGesturePerformed(this.mChannelView, 4);
        } else if (this.mOnBackNotHandledListener != null) {
            this.mOnBackNotHandledListener.onBackNotHandled(c);
        }
    }

    public void onHomePressed(Context c) {
        if (this.mAccessibilityContextMenu != null && this.mAccessibilityContextMenu.isShowing()) {
            this.mAccessibilityContextMenu.dismiss();
        } else if (this.mChannelView.getState() == 6) {
            onStateChangeGesturePerformed(this.mChannelView, 4);
        } else if (this.mOnHomeNotHandledListener != null) {
            this.mOnHomeNotHandledListener.onHomeNotHandled(c);
        }
    }
}
