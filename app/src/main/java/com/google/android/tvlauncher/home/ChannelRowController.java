package com.google.android.tvlauncher.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.data.ChannelOrderManager;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.home.ChannelView.OnMoveChannelDownListener;
import com.google.android.tvlauncher.home.ChannelView.OnMoveChannelUpListener;
import com.google.android.tvlauncher.home.ChannelView.OnPerformMainActionListener;
import com.google.android.tvlauncher.home.ChannelView.OnRemoveListener;
import com.google.android.tvlauncher.home.ChannelView.OnStateChangeGesturePerformedListener;
import com.google.android.tvlauncher.home.util.ChannelUtil;
import com.google.android.tvlauncher.model.HomeChannel;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.IntentLauncher;
import com.google.android.tvlauncher.util.Util;
import com.google.android.tvlauncher.util.palette.PaletteBitmapContainer;

class ChannelRowController implements HomeRow, OnPerformMainActionListener, OnMoveChannelUpListener, OnMoveChannelDownListener, OnRemoveListener, OnStateChangeGesturePerformedListener, OnPerformLastProgramActionListener, EventLogger, OnBackPressedListener, OnHomePressedListener {
    private static final int ACCESSIBILITY_MENU_DONE = 4;
    private static final int ACCESSIBILITY_MENU_DOWN = 2;
    private static final int ACCESSIBILITY_MENU_OPEN = 0;
    private static final int ACCESSIBILITY_MENU_REMOVE = 3;
    private static final int ACCESSIBILITY_MENU_UP = 1;
    private static final boolean DEBUG = false;
    private static final String TAG = "ChannelRowController";
    private AccessibilityContextMenu mAccessibilityContextMenu;
    private String mActionUri;
    private long mChannelId;
    private final RequestManager mChannelLogoRequestManager;
    private ChannelOrderManager mChannelOrderManager;
    private final ChannelView mChannelView;
    private final EventLogger mEventLogger;
    private boolean mIsLegacy;
    private ChannelItemsAdapter mItemsAdapter;
    private final HorizontalGridView mItemsListView;
    private int mLastSelectedItemPosition = -1;
    private ImageViewTarget<PaletteBitmapContainer> mLogoPaletteGlideTarget;
    private ChannelItemMetadataController mMetadataController;
    private OnBackNotHandledListener mOnBackNotHandledListener;
    private OnHomeNotHandledListener mOnHomeNotHandledListener;
    private OnHomeRowRemovedListener mOnHomeRowRemovedListener;
    private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
    private OnHomeStateChangeListener mOnHomeStateChangeListener;
    private OnProgramSelectedListener mOnProgramSelectedListener;
    private String mPackageName;
    private String mTitle;

    ChannelRowController(ChannelView channelView, RequestManager channelLogoRequestManager, EventLogger eventLogger) {
        this.mChannelView = channelView;
        this.mChannelLogoRequestManager = channelLogoRequestManager;
        this.mEventLogger = eventLogger;
        this.mChannelView.setOnPerformMainActionListener(this);
        this.mChannelView.setOnMoveUpListener(this);
        this.mChannelView.setOnMoveDownListener(this);
        this.mChannelView.setOnRemoveListener(this);
        this.mChannelView.setOnStateChangeGesturePerformedListener(this);
        this.mChannelView.setStateSettings(ChannelUtil.getDefaultChannelStateSettings(channelView.getContext()));
        this.mItemsListView = this.mChannelView.getItemsListView();
        ChannelUtil.configureItemsListAlignment(this.mItemsListView);
        this.mChannelOrderManager = TvDataManager.getInstance(this.mChannelView.getContext()).getChannelOrderManager();
        final int programLogoDefaultBackground = ContextCompat.getColor(channelView.getContext(), R.color.channel_logo_default_background);
        this.mLogoPaletteGlideTarget = new ImageViewTarget<PaletteBitmapContainer>(this.mChannelView.getChannelLogoImageView()) {
            protected void setResource(PaletteBitmapContainer resource) {
                if (resource != null) {
                    Bitmap bitmap = resource.getBitmap();
                    ((ImageView) this.view).setImageBitmap(bitmap);
                    if (bitmap == null || bitmap.hasAlpha()) {
                        ((ImageView) this.view).setBackgroundColor(programLogoDefaultBackground);
                    } else {
                        ((ImageView) this.view).setBackground(null);
                    }
                    ChannelRowController.this.mItemsAdapter.setChannelLogoAndPrimaryColor(resource.getBitmap(), resource.getPalette().getVibrantColor(resource.getPalette().getMutedColor(ContextCompat.getColor(((ImageView) this.view).getContext(), R.color.channel_last_item_focused_default_background))));
                    return;
                }
                ((ImageView) this.view).setImageDrawable(null);
                ((ImageView) this.view).setBackgroundColor(programLogoDefaultBackground);
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
            this.mItemsAdapter = new ChannelItemsAdapter(this.mChannelView.getContext(), this);
            this.mItemsAdapter.setOnPerformLastProgramActionListener(this);
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

    void bindChannel(HomeChannel channel, int channelViewState, boolean canAddToWatchNext, boolean canRemoveProgram) {
        ensureItemListIsSetUp();
        if (!(this.mAccessibilityContextMenu == null || this.mChannelId == channel.getId())) {
            this.mAccessibilityContextMenu.dismiss();
        }
        this.mChannelId = channel.getId();
        this.mActionUri = channel.getLaunchUri();
        this.mTitle = channel.getDisplayName();
        this.mPackageName = channel.getPackageName();
        this.mIsLegacy = channel.isLegacy();
        this.mMetadataController.setLegacy(this.mIsLegacy);
        if (this.mIsLegacy) {
            canAddToWatchNext = false;
            canRemoveProgram = false;
        }
        this.mChannelView.setChannelTitle(this.mTitle);
        this.mChannelView.getChannelLogoImageView().setContentDescription(this.mTitle);
        bindChannelMoveAction();
        this.mChannelLogoRequestManager.as(PaletteBitmapContainer.class).load(TvDataManager.getInstance(this.mChannelView.getContext()).getChannelLogoUri(Long.valueOf(channel.getId()))).into(this.mLogoPaletteGlideTarget);
        this.mChannelView.setState(channelViewState);
        int oldProgramState = this.mItemsAdapter.getProgramState();
        int newProgramState = getProgramState(channelViewState);
        this.mItemsAdapter.bind(channel.getId(), this.mPackageName, newProgramState, canAddToWatchNext, canRemoveProgram, this.mIsLegacy);
        updateItemsListPosition(newProgramState, oldProgramState);
    }

    void bindChannelMoveAction() {
        this.mChannelView.updateChannelMoveAction(this.mChannelOrderManager.canMoveChannelUp(this.mChannelId), this.mChannelOrderManager.canMoveChannelDown(this.mChannelId));
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
        if (position == -1 || position == this.mItemsAdapter.getItemCount() - 1) {
            this.mMetadataController.clear();
        } else if (position >= 0 && position < dataManager.getProgramCount(this.mChannelId)) {
            this.mMetadataController.bindView(dataManager.getProgram(this.mChannelId, position));
        }
    }

    public void onPerformMainAction(ChannelView v) {
        if (Util.isAccessibilityEnabled(v.getContext())) {
            showAccessibilityMenu();
        } else {
            performMainChannelAction();
        }
    }

    private void performMainChannelAction() {
        if (this.mActionUri != null) {
            this.mEventLogger.log(new UserActionEvent(LogEvents.START_APP).putParameter("placement", LogEvents.APP_PLACEMENT_CHANNEL_LOGO).putParameter("package_name", this.mPackageName));
            IntentLauncher.launchIntentFromUri(this.mChannelView.getContext(), this.mActionUri);
        }
    }

    private void showAccessibilityMenu() {
        if (this.mAccessibilityContextMenu == null) {
            Context context = this.mChannelView.getContext();
            this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity) context);
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, context.getString(R.string.banner_sidebar_primary_action_text), context.getDrawable(R.drawable.ic_context_menu_open_black)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, context.getString(R.string.accessibility_menu_item_move_up), context.getDrawable(R.drawable.ic_arrow_up_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(2, context.getString(R.string.accessibility_menu_item_move_down), context.getDrawable(R.drawable.ic_arrow_down_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(3, context.getString(R.string.channel_action_remove), context.getDrawable(R.drawable.ic_remove_circle_black)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(4, context.getString(R.string.accessibility_menu_item_done), context.getDrawable(R.drawable.ic_done_black_24dp)));
            this.mAccessibilityContextMenu.setOnMenuItemClickListener(new OnItemClickListener() {
                public void onItemClick(ContextMenuItem item) {
                    switch (item.getId()) {
                        case 0:
                            ChannelRowController.this.performMainChannelAction();
                            return;
                        case 1:
                            ChannelRowController.this.onMoveChannelUp(ChannelRowController.this.mChannelView);
                            return;
                        case 2:
                            ChannelRowController.this.onMoveChannelDown(ChannelRowController.this.mChannelView);
                            return;
                        case 3:
                            ChannelRowController.this.mAccessibilityContextMenu.dismiss();
                            ChannelRowController.this.onRemove(ChannelRowController.this.mChannelView);
                            return;
                        case 4:
                            ChannelRowController.this.mAccessibilityContextMenu.dismiss();
                            return;
                        default:
                            return;
                    }
                }
            });
        }
        this.mAccessibilityContextMenu.show();
    }

    public void onMoveChannelUp(ChannelView v) {
        if (this.mChannelOrderManager.canMoveChannelUp(this.mChannelId)) {
            this.mEventLogger.log(new UserActionEvent(LogEvents.MOVE_CHANNEL_UP).putParameter("package_name", this.mPackageName).putParameter(LogEvents.PARAMETER_INDEX, this.mChannelOrderManager.moveChannelUp(this.mChannelId)));
            this.mChannelView.updateChannelMoveAction(this.mChannelOrderManager.canMoveChannelUp(this.mChannelId), this.mChannelOrderManager.canMoveChannelDown(this.mChannelId));
        }
    }

    public void onMoveChannelDown(ChannelView v) {
        if (this.mChannelOrderManager.canMoveChannelDown(this.mChannelId)) {
            this.mEventLogger.log(new UserActionEvent(LogEvents.MOVE_CHANNEL_DOWN).putParameter("package_name", this.mPackageName).putParameter(LogEvents.PARAMETER_INDEX, this.mChannelOrderManager.moveChannelDown(this.mChannelId)));
            this.mChannelView.updateChannelMoveAction(this.mChannelOrderManager.canMoveChannelUp(this.mChannelId), this.mChannelOrderManager.canMoveChannelDown(this.mChannelId));
        }
    }

    public void onRemove(ChannelView v) {
        TvDataManager dataManager = TvDataManager.getInstance(v.getContext());
        dataManager.removeHomeChannel(this.mChannelId);
        if (this.mOnHomeRowRemovedListener != null) {
            this.mOnHomeRowRemovedListener.onHomeRowRemoved(this);
            this.mEventLogger.log(new UserActionEvent(LogEvents.REMOVE_CHANNEL_FROM_HOME).putParameter("package_name", this.mPackageName).putParameter("count", dataManager.getHomeChannelCount()));
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
            case 8:
                if (this.mOnHomeRowSelectedListener != null) {
                    this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                }
                if (this.mOnHomeStateChangeListener != null) {
                    this.mOnHomeStateChangeListener.onHomeStateChange(3);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onPerformLastProgramAction() {
        if (this.mActionUri != null) {
            this.mEventLogger.log(new UserActionEvent(LogEvents.START_APP).putParameter("placement", LogEvents.APP_PLACEMENT_LAST_PROGRAM).putParameter("package_name", this.mPackageName));
            IntentLauncher.launchIntentFromUri(this.mChannelView.getContext(), this.mActionUri);
        }
    }

    public void log(LogEvent event) {
        event.putRestrictedParameter("package_name", this.mPackageName);
        if (this.mIsLegacy) {
            event.putParameter(LogEvents.PARAMETER_IS_LEGACY, true);
        }
        this.mEventLogger.log(event);
    }

    public void onBackPressed(Context c) {
        if (this.mChannelView.getState() == 0 && this.mItemsListView.getSelectedPosition() != 0 && this.mItemsListView.getAdapter().getItemCount() > 0) {
            this.mItemsListView.setSelectedPositionSmooth(0);
        } else if (this.mChannelView.getState() == 8 || this.mChannelView.getState() == 6) {
            onStateChangeGesturePerformed(this.mChannelView, 4);
        } else if (this.mOnBackNotHandledListener != null) {
            this.mOnBackNotHandledListener.onBackNotHandled(c);
        }
    }

    public void onHomePressed(Context c) {
        if (this.mAccessibilityContextMenu != null && this.mAccessibilityContextMenu.isShowing()) {
            this.mAccessibilityContextMenu.dismiss();
        } else if (this.mChannelView.getState() == 8 || this.mChannelView.getState() == 6) {
            onStateChangeGesturePerformed(this.mChannelView, 4);
        } else if (this.mOnHomeNotHandledListener != null) {
            this.mOnHomeNotHandledListener.onHomeNotHandled(c);
        }
    }

    public String toString() {
        return '{' + super.toString() + ", mChannelId='" + this.mChannelId + '\'' + ", mTitle='" + this.mTitle + '\'' + '}';
    }
}
