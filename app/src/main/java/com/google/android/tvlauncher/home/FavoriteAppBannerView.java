package com.google.android.tvlauncher.home;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewOutlineProvider;
import com.google.android.tvlauncher.appsview.AppBannerView;
import com.google.android.tvlauncher.appsview.AppsManager;
import com.google.android.tvlauncher.appsview.LaunchItem;
import com.google.android.tvlauncher.appsview.OnAppsViewActionListener;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnDismissListener;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;

public class FavoriteAppBannerView
  extends AppBannerView
{
  private static final int ACCESSIBILITY_MENU_DONE = 2;
  private static final int ACCESSIBILITY_MENU_LEFT = 0;
  private static final int ACCESSIBILITY_MENU_RIGHT = 1;
  private AccessibilityContextMenu mAccessibilityContextMenu;
  private View mFocusedFrame;
  private boolean mIsBeingMoved;
  private Drawable mMoveIconLeftRight;
  
  public FavoriteAppBannerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public FavoriteAppBannerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public FavoriteAppBannerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setOnFocusChangeListener(null);
    this.mMoveIconLeftRight = paramContext.getDrawable(R.drawable.ic_context_menu_move_left_right_black);
  }
  
  private boolean checkExitEditMode()
  {
    if (this.mIsBeingMoved)
    {
      this.mOnAppsViewActionListener.onExitEditModeView();
      return true;
    }
    return false;
  }
  
  private void showAccessibilityMenu()
  {
    Context localContext = getContext();
    if (this.mAccessibilityContextMenu == null)
    {
      this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity)localContext);
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, localContext.getString(R.string.accessibility_menu_item_move_left), localContext.getDrawable(R.drawable.ic_arrow_left_black_24dp)));
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, localContext.getString(R.string.accessibility_menu_item_move_right), localContext.getDrawable(R.drawable.ic_arrow_right_black_24dp)));
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(2, localContext.getString(R.string.accessibility_menu_item_done), localContext.getDrawable(R.drawable.ic_done_black_24dp)));
      this.mAccessibilityContextMenu.setOnMenuItemClickListener(new ContextMenu.OnItemClickListener()
      {
        public void onItemClick(ContextMenuItem paramAnonymousContextMenuItem)
        {
          switch (paramAnonymousContextMenuItem.getId())
          {
          default: 
            return;
          case 0: 
            FavoriteAppBannerView.this.swapItemsIfNeeded(17);
            return;
          case 1: 
            FavoriteAppBannerView.this.swapItemsIfNeeded(66);
            return;
          }
          FavoriteAppBannerView.this.mAccessibilityContextMenu.dismiss();
        }
      });
      this.mAccessibilityContextMenu.setOnDismissListener(new ContextMenu.OnDismissListener()
      {
        public void onDismiss()
        {
          FavoriteAppBannerView.this.checkExitEditMode();
        }
      });
    }
    this.mAccessibilityContextMenu.show();
  }
  
  private View swapItemsIfNeeded(int paramInt)
  {
    View localView = super.focusSearch(paramInt);
    if ((this.mIsBeingMoved) && (!(localView instanceof FavoriteAppBannerView))) {
      return this;
    }
    if ((this.mIsBeingMoved) && ((localView instanceof FavoriteAppBannerView)))
    {
      this.mAppsManager.swapFavoriteAppOrder(getItem(), ((FavoriteAppBannerView)localView).getItem());
      getOnFocusChangeListener().onFocusChange(this, true);
      return this;
    }
    return localView;
  }
  
  public View focusSearch(int paramInt)
  {
    return swapItemsIfNeeded(paramInt);
  }
  
  public boolean isBeingMoved()
  {
    return this.mIsBeingMoved;
  }
  
  public void onClick(View paramView)
  {
    if (checkExitEditMode()) {
      return;
    }
    super.onClick(paramView);
  }
  
  protected void onEnterEditMode()
  {
    super.onEnterEditMode();
    if (Util.isAccessibilityEnabled(getContext())) {
      showAccessibilityMenu();
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mFocusedFrame = findViewById(R.id.edit_focused_frame);
    this.mFocusedFrame.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), FavoriteAppBannerView.this.mCornerRadius);
      }
    });
    this.mFocusedFrame.setClipToOutline(true);
    setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getResources().getDimensionPixelSize(R.dimen.home_app_banner_width), paramAnonymousView.getResources().getDimensionPixelSize(2131558594), FavoriteAppBannerView.this.mCornerRadius);
      }
    });
  }
  
  public boolean onLongClick(View paramView)
  {
    if (this.mIsBeingMoved) {
      return false;
    }
    return super.onLongClick(paramView);
  }
  
  public void setAppBannerItems(LaunchItem paramLaunchItem, boolean paramBoolean, OnAppsViewActionListener paramOnAppsViewActionListener)
  {
    super.setAppBannerItems(paramLaunchItem, paramBoolean, paramOnAppsViewActionListener);
    setContentDescription(paramLaunchItem.getLabel());
  }
  
  void setIsBeingMoved(boolean paramBoolean)
  {
    if ((this.mAccessibilityContextMenu != null) && (!paramBoolean)) {
      this.mAccessibilityContextMenu.dismiss();
    }
    this.mIsBeingMoved = paramBoolean;
    View localView = this.mFocusedFrame;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localView.setVisibility(i);
      return;
    }
  }
  
  protected void setMenuItems()
  {
    boolean bool = true;
    super.setMenuItems();
    ContextMenuItem localContextMenuItem = this.mAppMenu.findItem(1);
    if (localContextMenuItem != null) {
      if (this.mAppsManager.isOnlyFavorite(this.mItem)) {
        break label83;
      }
    }
    for (;;)
    {
      localContextMenuItem.setEnabled(bool);
      localContextMenuItem.setIcon(this.mMoveIconLeftRight);
      localContextMenuItem = this.mAppMenu.findItem(3);
      if (localContextMenuItem != null) {
        localContextMenuItem.setVisible(false);
      }
      localContextMenuItem = this.mAppMenu.findItem(4);
      if (localContextMenuItem != null) {
        localContextMenuItem.setVisible(false);
      }
      return;
      label83:
      bool = false;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/FavoriteAppBannerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */