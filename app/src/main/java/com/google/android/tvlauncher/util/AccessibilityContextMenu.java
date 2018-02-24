package com.google.android.tvlauncher.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccessibilityContextMenu
{
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
  private ContextMenu.OnDismissListener mOnDismissListener;
  private ContextMenu.OnItemClickListener mOnItemClickListener;
  private final PopupWindow mPopupWindow;
  
  public AccessibilityContextMenu(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    paramActivity = this.mActivity.getResources();
    this.mMenuContainer = ((LinearLayout)((LayoutInflater)this.mActivity.getSystemService("layout_inflater")).inflate(2130968603, null));
    this.mPopupWindow = new PopupWindow(this.mMenuContainer, -2, -2);
    this.mPopupWindow.setFocusable(true);
    this.mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
    {
      public void onDismiss()
      {
        AccessibilityContextMenu.access$002(AccessibilityContextMenu.this, false);
        if (AccessibilityContextMenu.this.mOnDismissListener != null) {
          AccessibilityContextMenu.this.mOnDismissListener.onDismiss();
        }
      }
    });
    this.mMenuItemFocusedColor = this.mActivity.getColor(R.color.context_menu_background_focused_color);
    this.mMenuItemUnfocusedColor = this.mActivity.getColor(R.color.context_menu_background_unfocused_color);
    this.mMenuItemHeight = paramActivity.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_height);
    this.mMenuItemWidth = paramActivity.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_width);
    this.mMenuItemMarginEnd = paramActivity.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_margin_end);
    this.mMenuItemMarginTop = paramActivity.getDimensionPixelSize(R.dimen.accessibility_context_menu_item_margin_top);
  }
  
  private void bindMenuItemView(final ContextMenuItem paramContextMenuItem, final View paramView)
  {
    ((TextView)paramView.findViewById(R.id.title)).setText(paramContextMenuItem.getTitle());
    ImageView localImageView = (ImageView)paramView.findViewById(R.id.icon);
    localImageView.setImageTintList(paramView.getContext().getResources().getColorStateList(2131820592, null));
    localImageView.setImageDrawable(paramContextMenuItem.getIcon());
    localImageView.setBackgroundColor(this.mMenuItemUnfocusedColor);
    paramView.setFocusable(localImageView.isEnabled());
    paramView.setEnabled(localImageView.isEnabled());
    paramView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((paramContextMenuItem.isEnabled()) && (AccessibilityContextMenu.this.mOnItemClickListener != null)) {
          AccessibilityContextMenu.this.mOnItemClickListener.onItemClick(paramContextMenuItem);
        }
      }
    });
    paramView.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        paramAnonymousView = paramView;
        if (paramAnonymousBoolean) {}
        for (int i = AccessibilityContextMenu.this.mMenuItemFocusedColor;; i = AccessibilityContextMenu.this.mMenuItemUnfocusedColor)
        {
          paramAnonymousView.setBackgroundColor(i);
          return;
        }
      }
    });
  }
  
  public void addItem(ContextMenuItem paramContextMenuItem)
  {
    this.mContextItems.add(paramContextMenuItem);
  }
  
  public void dismiss()
  {
    this.mPopupWindow.dismiss();
  }
  
  public boolean isShowing()
  {
    return this.mIsShowing;
  }
  
  public void setOnDismissListener(ContextMenu.OnDismissListener paramOnDismissListener)
  {
    this.mOnDismissListener = paramOnDismissListener;
  }
  
  public void setOnMenuItemClickListener(ContextMenu.OnItemClickListener paramOnItemClickListener)
  {
    this.mOnItemClickListener = paramOnItemClickListener;
  }
  
  public void show()
  {
    LayoutInflater localLayoutInflater = (LayoutInflater)this.mActivity.getSystemService("layout_inflater");
    this.mMenuContainer.removeAllViews();
    Iterator localIterator = this.mContextItems.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (ContextMenuItem)localIterator.next();
      LinearLayout localLinearLayout = (LinearLayout)localLayoutInflater.inflate(2130968604, null);
      bindMenuItemView((ContextMenuItem)localObject, localLinearLayout);
      localObject = new LinearLayout.LayoutParams(this.mMenuItemWidth, this.mMenuItemHeight);
      ((LinearLayout.LayoutParams)localObject).setMargins(0, this.mMenuItemMarginTop, 0, 0);
      ((LinearLayout.LayoutParams)localObject).setMarginEnd(this.mMenuItemMarginEnd);
      this.mMenuContainer.addView(localLinearLayout, (ViewGroup.LayoutParams)localObject);
    }
    this.mMenuContainer.setAccessibilityDelegate(new View.AccessibilityDelegate()
    {
      public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfo paramAnonymousAccessibilityNodeInfo)
      {
        super.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfo);
        paramAnonymousAccessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(AccessibilityContextMenu.this.mContextItems.size(), 0, false));
      }
    });
    this.mPopupWindow.showAtLocation(this.mActivity.getWindow().getDecorView().getRootView(), 80, 0, 0);
    this.mIsShowing = true;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/AccessibilityContextMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */