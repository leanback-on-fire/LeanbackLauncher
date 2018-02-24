package com.google.android.tvlauncher.util;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.util.ContextMenu.OnDismissListener;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityContextMenu {
    private final Activity mActivity;
    private final List<ContextMenuItem> mContextItems = new ArrayList();
    private boolean mIsShowing;
    private final LinearLayout mMenuContainer;
    private final int mMenuItemFocusedColor;
    private final int mMenuItemHeight;
    private final int mMenuItemMarginEnd;
    private final int mMenuItemMarginTop;
    private final int mMenuItemUnfocusedColor;
    private final int mMenuItemWidth;
    private OnDismissListener mOnDismissListener;
    private OnItemClickListener mOnItemClickListener;
    private final PopupWindow mPopupWindow;

    public AccessibilityContextMenu(Activity activity) {
        this.mActivity = activity;
        Resources res = this.mActivity.getResources();
        this.mMenuContainer = (LinearLayout) ((LayoutInflater) this.mActivity.getSystemService("layout_inflater")).inflate(R.layout.accessibility_context_menu_container, null);
        this.mPopupWindow = new PopupWindow(this.mMenuContainer, -2, -2);
        this.mPopupWindow.setFocusable(true);
        this.mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                AccessibilityContextMenu.this.mIsShowing = false;
                if (AccessibilityContextMenu.this.mOnDismissListener != null) {
                    AccessibilityContextMenu.this.mOnDismissListener.onDismiss();
                }
            }
        });
        this.mMenuItemFocusedColor = ContextCompat.getColor(this.mActivity, R.color.context_menu_background_focused_color);
        this.mMenuItemUnfocusedColor = ContextCompat.getColor(this.mActivity, R.color.context_menu_background_unfocused_color);
        this.mMenuItemHeight = res.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_height);
        this.mMenuItemWidth = res.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_width);
        this.mMenuItemMarginEnd = res.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_margin_end);
        this.mMenuItemMarginTop = res.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_margin_top);
    }

    public void show() {
        LayoutInflater layoutInflater = (LayoutInflater) this.mActivity.getSystemService("layout_inflater");
        this.mMenuContainer.removeAllViews();
        for (ContextMenuItem item : this.mContextItems) {
            LinearLayout itemView = (LinearLayout) layoutInflater.inflate(R.layout.accessibility_context_menu_item, null);
            bindMenuItemView(item, itemView);
            LayoutParams params = new LayoutParams(this.mMenuItemWidth, this.mMenuItemHeight);
            params.setMargins(0, this.mMenuItemMarginTop, 0, 0);
            params.setMarginEnd(this.mMenuItemMarginEnd);
            this.mMenuContainer.addView(itemView, params);
        }
        this.mMenuContainer.setAccessibilityDelegate(new AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCollectionInfo(CollectionInfo.obtain(AccessibilityContextMenu.this.mContextItems.size(), 0, false));
            }
        });
        this.mPopupWindow.showAtLocation(this.mActivity.getWindow().getDecorView().getRootView(), 80, 0, 0);
        this.mIsShowing = true;
    }

    public void addItem(ContextMenuItem item) {
        this.mContextItems.add(item);
    }

    public void setOnMenuItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    public void dismiss() {
        this.mPopupWindow.dismiss();
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    private void bindMenuItemView(final ContextMenuItem item, final View view) {
        ((TextView) view.findViewById(R.id.title)).setText(item.getTitle());
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageTintList(ResourcesCompat.getColorStateList(view.getContext().getResources(), R.color.context_menu_icon_enabled_color, null));
        iconView.setImageDrawable(item.getIcon());
        iconView.setBackgroundColor(this.mMenuItemUnfocusedColor);
        view.setFocusable(iconView.isEnabled());
        view.setEnabled(iconView.isEnabled());
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (item.isEnabled() && AccessibilityContextMenu.this.mOnItemClickListener != null) {
                    AccessibilityContextMenu.this.mOnItemClickListener.onItemClick(item);
                }
            }
        });
        view.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                view.setBackgroundColor(hasFocus ? AccessibilityContextMenu.this.mMenuItemFocusedColor : AccessibilityContextMenu.this.mMenuItemUnfocusedColor);
            }
        });
    }
}
