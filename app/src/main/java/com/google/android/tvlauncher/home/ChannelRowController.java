package com.google.android.tvlauncher.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.tvlauncher.BackHomeControllerListeners;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.data.ChannelOrderManager;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.home.util.ChannelUtil;
import com.google.android.tvlauncher.model.HomeChannel;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.IntentLauncher;
import com.google.android.tvlauncher.util.Util;
import com.google.android.tvlauncher.util.palette.PaletteBitmapContainer;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

class ChannelRowController
        implements HomeRow, ChannelView.OnPerformMainActionListener, ChannelView.OnMoveChannelUpListener, ChannelView.OnMoveChannelDownListener, ChannelView.OnRemoveListener, ChannelView.OnStateChangeGesturePerformedListener, OnPerformLastProgramActionListener, EventLogger, BackHomeControllerListeners.OnBackPressedListener, BackHomeControllerListeners.OnHomePressedListener {
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
    private BackHomeControllerListeners.OnBackNotHandledListener mOnBackNotHandledListener;
    private BackHomeControllerListeners.OnHomeNotHandledListener mOnHomeNotHandledListener;
    private OnHomeRowRemovedListener mOnHomeRowRemovedListener;
    private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
    private OnHomeStateChangeListener mOnHomeStateChangeListener;
    private OnProgramSelectedListener mOnProgramSelectedListener;
    private String mPackageName;
    private String mTitle;

    private class ImageViewTargetImpl extends ImageViewTarget<PaletteBitmapContainer> {

        ImageViewTargetImpl() {
            super(ChannelRowController.this.mChannelView.getChannelLogoImageView());
        }

        protected void setResource(PaletteBitmapContainer paramAnonymousPaletteBitmapContainer) {
            if (paramAnonymousPaletteBitmapContainer != null) {
                Bitmap localBitmap = paramAnonymousPaletteBitmapContainer.getBitmap();
                this.view.setImageBitmap(localBitmap);
                int i = paramAnonymousPaletteBitmapContainer.getPalette().getVibrantColor(paramAnonymousPaletteBitmapContainer.getPalette().getMutedColor(((ImageView) this.view).getContext().getColor(R.color.channel_last_item_focused_default_background)));

                if ((localBitmap == null) || (localBitmap.hasAlpha())) {
                    ((ImageView) this.view).setBackgroundColor(i);
                    ChannelRowController.this.mItemsAdapter.setChannelLogoAndPrimaryColor(paramAnonymousPaletteBitmapContainer.getBitmap(), i);

                } else {
                    ((ImageView) this.view).setImageDrawable(null);
                    ((ImageView) this.view).setBackgroundColor(i);
                    ((ImageView) this.view).setBackground(null);
                }
            }

        }
    }

    ChannelRowController(ChannelView paramChannelView, RequestManager paramRequestManager, EventLogger paramEventLogger) {
        this.mChannelView = paramChannelView;
        this.mChannelLogoRequestManager = paramRequestManager;
        this.mEventLogger = paramEventLogger;
        this.mChannelView.setOnPerformMainActionListener(this);
        this.mChannelView.setOnMoveUpListener(this);
        this.mChannelView.setOnMoveDownListener(this);
        this.mChannelView.setOnRemoveListener(this);
        this.mChannelView.setOnStateChangeGesturePerformedListener(this);
        this.mChannelView.setStateSettings(ChannelUtil.getDefaultChannelStateSettings(paramChannelView.getContext()));
        this.mItemsListView = this.mChannelView.getItemsListView();
        ChannelUtil.configureItemsListAlignment(this.mItemsListView);
        this.mChannelOrderManager = TvDataManager.getInstance(this.mChannelView.getContext()).getChannelOrderManager();
        @Edited(reason = Reason.API_CHANGE) final int i = ContextCompat.getColor(paramChannelView.getContext(), R.color.channel_logo_default_background);
        this.mLogoPaletteGlideTarget =;
    }

    private void ensureItemListIsSetUp() {
        if (this.mItemsAdapter != null) {
            return;
        }
        this.mItemsAdapter = new ChannelItemsAdapter(this.mChannelView.getContext(), this);
        this.mItemsAdapter.setOnPerformLastProgramActionListener(this);
        this.mItemsAdapter.setOnProgramSelectedListener(this.mOnProgramSelectedListener);
        this.mItemsListView.setAdapter(this.mItemsAdapter);
        this.mItemsListView.setItemAnimator(null);
        this.mMetadataController = new ChannelItemMetadataController(this.mChannelView.getItemMetadataView());
    }

    private int getProgramState(int paramInt) {
        switch (paramInt) {
            default:
                return 0;
            case 0:
                return 1;
            case 1:
            case 2:
            case 3:
                return 0;
        }
        return 2;
    }

    private void performMainChannelAction() {
        if (this.mActionUri != null) {
            this.mEventLogger.log(new UserActionEvent("start_app").putParameter("placement", "channel_logo").putParameter("package_name", this.mPackageName));
            IntentLauncher.launchIntentFromUri(this.mChannelView.getContext(), this.mActionUri);
        }
    }

    private void showAccessibilityMenu() {
        if (this.mAccessibilityContextMenu == null) {
            Context localContext = this.mChannelView.getContext();
            this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity) localContext);
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, localContext.getString(R.string.banner_sidebar_primary_action_text), localContext.getDrawable(R.drawable.ic_context_menu_open_black)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, localContext.getString(R.string.accessibility_menu_item_move_up), localContext.getDrawable(R.drawable.ic_arrow_up_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(2, localContext.getString(R.string.accessibility_menu_item_move_down), localContext.getDrawable(R.drawable.ic_arrow_down_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(3, localContext.getString(R.string.channel_action_remove), localContext.getDrawable(R.drawable.ic_remove_circle_black)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(4, localContext.getString(R.string.accessibility_menu_item_done), localContext.getDrawable(R.drawable.ic_done_black_24dp)));
            this.mAccessibilityContextMenu.setOnMenuItemClickListener(new ContextMenu.OnItemClickListener() {
                public void onItemClick(ContextMenuItem paramAnonymousContextMenuItem) {
                    switch (paramAnonymousContextMenuItem.getId()) {
                        default:
                            return;
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
                    }
                    ChannelRowController.this.mAccessibilityContextMenu.dismiss();
                }
            });
        }
        this.mAccessibilityContextMenu.show();
    }

    private void updateItemsListPosition(int paramInt1, int paramInt2) {
        if (paramInt1 == paramInt2) {
        }
        do {
            return;
            if ((paramInt1 == 2) && (this.mItemsAdapter.getItemCount() > 1) && (this.mItemsListView.getSelectedPosition() != 0)) {
                this.mLastSelectedItemPosition = this.mItemsListView.getSelectedPosition();
                this.mItemsListView.scrollToPosition(0);
                return;
            }
        }
        while ((this.mItemsAdapter.getItemCount() <= 1) || (this.mLastSelectedItemPosition == -1));
        paramInt1 = this.mLastSelectedItemPosition;
        this.mLastSelectedItemPosition = -1;
        this.mItemsListView.scrollToPosition(paramInt1);
    }

    void bindChannel(HomeChannel paramHomeChannel, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
        ensureItemListIsSetUp();
        if ((this.mAccessibilityContextMenu != null) && (this.mChannelId != paramHomeChannel.getId())) {
            this.mAccessibilityContextMenu.dismiss();
        }
        this.mChannelId = paramHomeChannel.getId();
        this.mActionUri = paramHomeChannel.getLaunchUri();
        this.mTitle = paramHomeChannel.getDisplayName();
        this.mPackageName = paramHomeChannel.getPackageName();
        this.mIsLegacy = paramHomeChannel.isLegacy();
        this.mMetadataController.setLegacy(this.mIsLegacy);
        if (this.mIsLegacy) {
            paramBoolean1 = false;
            paramBoolean2 = false;
        }
        this.mChannelView.setChannelTitle(this.mTitle);
        this.mChannelView.getChannelLogoImageView().setContentDescription(this.mTitle);
        bindChannelMoveAction();
        Uri localUri = TvDataManager.getInstance(this.mChannelView.getContext()).getChannelLogoUri(Long.valueOf(paramHomeChannel.getId()));
        this.mChannelLogoRequestManager.as(PaletteBitmapContainer.class).load(localUri).into(this.mLogoPaletteGlideTarget);
        this.mChannelView.setState(paramInt);
        int i = this.mItemsAdapter.getProgramState();
        paramInt = getProgramState(paramInt);
        this.mItemsAdapter.bind(paramHomeChannel.getId(), this.mPackageName, paramInt, paramBoolean1, paramBoolean2, this.mIsLegacy);
        updateItemsListPosition(paramInt, i);
    }

    void bindChannelMoveAction() {
        this.mChannelView.updateChannelMoveAction(this.mChannelOrderManager.canMoveChannelUp(this.mChannelId), this.mChannelOrderManager.canMoveChannelDown(this.mChannelId));
    }

    void bindItemMetadata() {
        int i = this.mItemsListView.getSelectedPosition();
        TvDataManager localTvDataManager = TvDataManager.getInstance(this.mItemsListView.getContext());
        if ((i == -1) || (i == this.mItemsAdapter.getItemCount() - 1)) {
            this.mMetadataController.clear();
        }
        while ((i < 0) || (i >= localTvDataManager.getProgramCount(this.mChannelId))) {
            return;
        }
        this.mMetadataController.bindView(localTvDataManager.getProgram(this.mChannelId, i));
    }

    public View getView() {
        return this.mChannelView;
    }

    public void log(LogEvent paramLogEvent) {
        paramLogEvent.putRestrictedParameter("package_name", this.mPackageName);
        if (this.mIsLegacy) {
            paramLogEvent.putParameter("is_legacy", true);
        }
        this.mEventLogger.log(paramLogEvent);
    }

    public void onBackPressed(Context paramContext) {
        if ((this.mChannelView.getState() == 0) && (this.mItemsListView.getSelectedPosition() != 0) && (this.mItemsListView.getAdapter().getItemCount() > 0)) {
            this.mItemsListView.setSelectedPositionSmooth(0);
        }
        do {
            return;
            if ((this.mChannelView.getState() == 8) || (this.mChannelView.getState() == 6)) {
                onStateChangeGesturePerformed(this.mChannelView, 4);
                return;
            }
        } while (this.mOnBackNotHandledListener == null);
        this.mOnBackNotHandledListener.onBackNotHandled(paramContext);
    }

    public void onHomePressed(Context paramContext) {
        if ((this.mAccessibilityContextMenu != null) && (this.mAccessibilityContextMenu.isShowing())) {
            this.mAccessibilityContextMenu.dismiss();
        }
        do {
            return;
            if ((this.mChannelView.getState() == 8) || (this.mChannelView.getState() == 6)) {
                onStateChangeGesturePerformed(this.mChannelView, 4);
                return;
            }
        } while (this.mOnHomeNotHandledListener == null);
        this.mOnHomeNotHandledListener.onHomeNotHandled(paramContext);
    }

    public void onMoveChannelDown(ChannelView paramChannelView) {
        if (this.mChannelOrderManager.canMoveChannelDown(this.mChannelId)) {
            int i = this.mChannelOrderManager.moveChannelDown(this.mChannelId);
            this.mEventLogger.log(new UserActionEvent("move_channel_down").putParameter("package_name", this.mPackageName).putParameter("index", i));
            this.mChannelView.updateChannelMoveAction(this.mChannelOrderManager.canMoveChannelUp(this.mChannelId), this.mChannelOrderManager.canMoveChannelDown(this.mChannelId));
        }
    }

    public void onMoveChannelUp(ChannelView paramChannelView) {
        if (this.mChannelOrderManager.canMoveChannelUp(this.mChannelId)) {
            int i = this.mChannelOrderManager.moveChannelUp(this.mChannelId);
            this.mEventLogger.log(new UserActionEvent("move_channel_up").putParameter("package_name", this.mPackageName).putParameter("index", i));
            this.mChannelView.updateChannelMoveAction(this.mChannelOrderManager.canMoveChannelUp(this.mChannelId), this.mChannelOrderManager.canMoveChannelDown(this.mChannelId));
        }
    }

    public void onPerformLastProgramAction() {
        if (this.mActionUri != null) {
            this.mEventLogger.log(new UserActionEvent("start_app").putParameter("placement", "last_program").putParameter("package_name", this.mPackageName));
            IntentLauncher.launchIntentFromUri(this.mChannelView.getContext(), this.mActionUri);
        }
    }

    public void onPerformMainAction(ChannelView paramChannelView) {
        if (Util.isAccessibilityEnabled(paramChannelView.getContext())) {
            showAccessibilityMenu();
            return;
        }
        performMainChannelAction();
    }

    public void onRemove(ChannelView paramChannelView) {
        paramChannelView = TvDataManager.getInstance(paramChannelView.getContext());
        paramChannelView.removeHomeChannel(this.mChannelId);
        if (this.mOnHomeRowRemovedListener != null) {
            this.mOnHomeRowRemovedListener.onHomeRowRemoved(this);
            this.mEventLogger.log(new UserActionEvent("remove_channel_from_home").putParameter("package_name", this.mPackageName).putParameter("count", paramChannelView.getHomeChannelCount()));
        }
    }

    public void onStart() {
        this.mItemsAdapter.onStart();
    }

    public void onStateChangeGesturePerformed(ChannelView paramChannelView, int paramInt) {
        switch (paramInt) {
            default:
            case 0:
            case 1:
            case 4:
            case 5:
            case 6:
            case 8:
                do {
                    do {
                        do {
                            do {
                                do {
                                    do {
                                        return;
                                        if (this.mOnHomeRowSelectedListener != null) {
                                            this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                                        }
                                    } while (this.mOnHomeStateChangeListener == null);
                                    this.mOnHomeStateChangeListener.onHomeStateChange(0);
                                    return;
                                } while (this.mOnHomeStateChangeListener == null);
                                this.mOnHomeStateChangeListener.onHomeStateChange(0);
                                return;
                                if (this.mOnHomeRowSelectedListener != null) {
                                    this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                                }
                            } while (this.mOnHomeStateChangeListener == null);
                            this.mOnHomeStateChangeListener.onHomeStateChange(1);
                            return;
                        } while (this.mOnHomeStateChangeListener == null);
                        this.mOnHomeStateChangeListener.onHomeStateChange(1);
                        return;
                        if (this.mOnHomeRowSelectedListener != null) {
                            this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                        }
                    } while (this.mOnHomeStateChangeListener == null);
                    this.mOnHomeStateChangeListener.onHomeStateChange(2);
                    return;
                    if (this.mOnHomeRowSelectedListener != null) {
                        this.mOnHomeRowSelectedListener.onHomeRowSelected(this);
                    }
                } while (this.mOnHomeStateChangeListener == null);
                this.mOnHomeStateChangeListener.onHomeStateChange(3);
                return;
        }
        throw new IllegalStateException("Unsupported ChannelView state change gesture: " + ChannelView.stateToString(paramInt));
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

    void setOnBackNotHandledListener(BackHomeControllerListeners.OnBackNotHandledListener paramOnBackNotHandledListener) {
        this.mOnBackNotHandledListener = paramOnBackNotHandledListener;
    }

    void setOnHomeNotHandledListener(BackHomeControllerListeners.OnHomeNotHandledListener paramOnHomeNotHandledListener) {
        this.mOnHomeNotHandledListener = paramOnHomeNotHandledListener;
    }

    public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener paramOnHomeRowRemovedListener) {
        this.mOnHomeRowRemovedListener = paramOnHomeRowRemovedListener;
    }

    public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener paramOnHomeRowSelectedListener) {
        this.mOnHomeRowSelectedListener = paramOnHomeRowSelectedListener;
    }

    public void setOnHomeStateChangeListener(OnHomeStateChangeListener paramOnHomeStateChangeListener) {
        this.mOnHomeStateChangeListener = paramOnHomeStateChangeListener;
    }

    void setOnProgramSelectedListener(OnProgramSelectedListener paramOnProgramSelectedListener) {
        this.mOnProgramSelectedListener = paramOnProgramSelectedListener;
    }

    public void setState(int paramInt) {
        this.mChannelView.setState(paramInt);
        int i = this.mItemsAdapter.getProgramState();
        paramInt = getProgramState(paramInt);
        this.mItemsAdapter.setProgramState(paramInt);
        updateItemsListPosition(paramInt, i);
    }

    public String toString() {
        return '{' + super.toString() + ", mChannelId='" + this.mChannelId + '\'' + ", mTitle='" + this.mTitle + '\'' + '}';
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ChannelRowController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */