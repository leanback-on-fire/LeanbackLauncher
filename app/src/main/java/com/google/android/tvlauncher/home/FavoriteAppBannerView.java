package com.google.android.tvlauncher.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.appsview.AppBannerView;
import com.google.android.tvlauncher.appsview.LaunchItem;
import com.google.android.tvlauncher.appsview.OnAppsViewActionListener;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnDismissListener;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;

public class FavoriteAppBannerView extends AppBannerView {
    private static final int ACCESSIBILITY_MENU_DONE = 2;
    private static final int ACCESSIBILITY_MENU_LEFT = 0;
    private static final int ACCESSIBILITY_MENU_RIGHT = 1;
    private AccessibilityContextMenu mAccessibilityContextMenu;
    private View mFocusedFrame;
    private boolean mIsBeingMoved;
    private Drawable mMoveIconLeftRight;

    public FavoriteAppBannerView(Context context) {
        this(context, null);
    }

    public FavoriteAppBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FavoriteAppBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnFocusChangeListener(null);
        this.mMoveIconLeftRight = context.getDrawable(R.drawable.ic_context_menu_move_left_right_black);
    }

    public boolean isBeingMoved() {
        return this.mIsBeingMoved;
    }

    public View focusSearch(int direction) {
        return swapItemsIfNeeded(direction);
    }

    public boolean onLongClick(View v) {
        if (this.mIsBeingMoved) {
            return false;
        }
        return super.onLongClick(v);
    }

    public void onClick(View v) {
        if (!checkExitEditMode()) {
            super.onClick(v);
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mFocusedFrame = findViewById(R.id.edit_focused_frame);
        this.mFocusedFrame.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) FavoriteAppBannerView.this.mCornerRadius);
            }
        });
        this.mFocusedFrame.setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getResources().getDimensionPixelSize(R.dimen.home_app_banner_width), view.getResources().getDimensionPixelSize(R.dimen.home_app_banner_image_height), (float) FavoriteAppBannerView.this.mCornerRadius);
            }
        });
    }

    protected void setMenuItems() {
        boolean z = true;
        super.setMenuItems();
        ContextMenuItem moveItem = this.mAppMenu.findItem(1);
        if (moveItem != null) {
            if (this.mAppsManager.isOnlyFavorite(this.mItem)) {
                z = false;
            }
            moveItem.setEnabled(z);
            moveItem.setIcon(this.mMoveIconLeftRight);
        }
        ContextMenuItem infoItem = this.mAppMenu.findItem(3);
        if (infoItem != null) {
            infoItem.setVisible(false);
        }
        ContextMenuItem uninstallItem = this.mAppMenu.findItem(4);
        if (uninstallItem != null) {
            uninstallItem.setVisible(false);
        }
    }

    protected void onEnterEditMode() {
        super.onEnterEditMode();
        if (Util.isAccessibilityEnabled(getContext())) {
            showAccessibilityMenu();
        }
    }

    void setIsBeingMoved(boolean moved) {
        if (!(this.mAccessibilityContextMenu == null || moved)) {
            this.mAccessibilityContextMenu.dismiss();
        }
        this.mIsBeingMoved = moved;
        this.mFocusedFrame.setVisibility(moved ? 0 : 8);
    }

    public void setAppBannerItems(LaunchItem item, boolean isOem, OnAppsViewActionListener listener) {
        super.setAppBannerItems(item, isOem, listener);
        setContentDescription(item.getLabel());
    }

    private void showAccessibilityMenu() {
        Context context = getContext();
        if (this.mAccessibilityContextMenu == null) {
            this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity) context);
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, context.getString(R.string.accessibility_menu_item_move_left), context.getDrawable(R.drawable.ic_arrow_left_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, context.getString(R.string.accessibility_menu_item_move_right), context.getDrawable(R.drawable.ic_arrow_right_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(2, context.getString(R.string.accessibility_menu_item_done), context.getDrawable(R.drawable.ic_done_black_24dp)));
            this.mAccessibilityContextMenu.setOnMenuItemClickListener(new OnItemClickListener() {
                public void onItemClick(ContextMenuItem item) {
                    switch (item.getId()) {
                        case 0:
                            FavoriteAppBannerView.this.swapItemsIfNeeded(17);
                            return;
                        case 1:
                            FavoriteAppBannerView.this.swapItemsIfNeeded(66);
                            return;
                        case 2:
                            FavoriteAppBannerView.this.mAccessibilityContextMenu.dismiss();
                            return;
                        default:
                            return;
                    }
                }
            });
            this.mAccessibilityContextMenu.setOnDismissListener(new OnDismissListener() {
                public void onDismiss() {
                    FavoriteAppBannerView.this.checkExitEditMode();
                }
            });
        }
        this.mAccessibilityContextMenu.show();
    }

    private View swapItemsIfNeeded(int direction) {
        View searchedView = super.focusSearch(direction);
        if (this.mIsBeingMoved && !(searchedView instanceof FavoriteAppBannerView)) {
            return this;
        }
        if (!this.mIsBeingMoved || !(searchedView instanceof FavoriteAppBannerView)) {
            return searchedView;
        }
        this.mAppsManager.swapFavoriteAppOrder(getItem(), ((FavoriteAppBannerView) searchedView).getItem());
        getOnFocusChangeListener().onFocusChange(this, true);
        return this;
    }

    private boolean checkExitEditMode() {
        if (!this.mIsBeingMoved) {
            return false;
        }
        this.mOnAppsViewActionListener.onExitEditModeView();
        return true;
    }
}
