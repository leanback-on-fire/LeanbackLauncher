package com.google.android.tvlauncher.appsview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnDismissListener;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;

public class EditModeGridView
  extends VerticalGridView
{
  private static final int ACCESSIBILITY_MENU_DONE = 4;
  private static final int ACCESSIBILITY_MENU_DOWN = 1;
  private static final int ACCESSIBILITY_MENU_LEFT = 2;
  private static final int ACCESSIBILITY_MENU_RIGHT = 3;
  private static final int ACCESSIBILITY_MENU_UP = 0;
  private AccessibilityContextMenu mAccessibilityContextMenu;
  
  public EditModeGridView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public EditModeGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public EditModeGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setColumnWidth(getResources().getDimensionPixelSize(2131558509) + getResources().getDimensionPixelSize(2131558505));
  }
  
  private void moveLaunchPoint(int paramInt1, int paramInt2)
  {
    ((EditModeGridAdapter)getAdapter()).moveLaunchItems(paramInt1, paramInt2);
  }
  
  private View swapItemsIfNeeded(View paramView, int paramInt)
  {
    int j = getChildAdapterPosition(paramView);
    if (getItemAnimator().isRunning()) {}
    label17:
    label117:
    do
    {
      return paramView;
      int i = paramInt;
      if (getLayoutDirection() == 1) {
        if (paramInt != 17)
        {
          i = paramInt;
          if (paramInt != 66) {}
        }
        else
        {
          if (paramInt != 17) {
            break label117;
          }
        }
      }
      for (i = 66;; i = 17) {
        switch (i)
        {
        default: 
          return paramView;
        case 17: 
          if (j % 4 <= 0) {
            break label17;
          }
          moveLaunchPoint(j, j - 1);
          return paramView;
        }
      }
      moveLaunchPoint(j, j + 4);
      return paramView;
      moveLaunchPoint(j, j - 4);
      return paramView;
    } while ((j % 4 >= 3) || (j >= getAdapter().getItemCount() - 1));
    moveLaunchPoint(j, j + 1);
    return paramView;
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    if (paramView.isSelected()) {
      return swapItemsIfNeeded(paramView, paramInt);
    }
    return super.focusSearch(paramView, paramInt);
  }
  
  void hideAccessibilityMenu()
  {
    if (this.mAccessibilityContextMenu != null) {
      this.mAccessibilityContextMenu.dismiss();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (Util.isAccessibilityEnabled(getContext())) {
      showAccessibilityMenu();
    }
  }
  
  void showAccessibilityMenu()
  {
    Context localContext = getContext();
    if (this.mAccessibilityContextMenu == null)
    {
      this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity)localContext);
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, localContext.getString(2131492888), localContext.getDrawable(2130837635)));
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, localContext.getString(2131492885), localContext.getDrawable(2130837632)));
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(2, localContext.getString(2131492886), localContext.getDrawable(2130837633)));
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(3, localContext.getString(2131492887), localContext.getDrawable(2130837634)));
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(4, localContext.getString(2131492884), localContext.getDrawable(2130837648)));
      this.mAccessibilityContextMenu.setOnMenuItemClickListener(new ContextMenu.OnItemClickListener()
      {
        public void onItemClick(ContextMenuItem paramAnonymousContextMenuItem)
        {
          switch (paramAnonymousContextMenuItem.getId())
          {
          default: 
            return;
          case 0: 
            EditModeGridView.this.swapItemsIfNeeded(EditModeGridView.this.getFocusedChild(), 33);
            return;
          case 1: 
            EditModeGridView.this.swapItemsIfNeeded(EditModeGridView.this.getFocusedChild(), 130);
            return;
          case 2: 
            EditModeGridView.this.swapItemsIfNeeded(EditModeGridView.this.getFocusedChild(), 17);
            return;
          case 3: 
            EditModeGridView.this.swapItemsIfNeeded(EditModeGridView.this.getFocusedChild(), 66);
            return;
          }
          EditModeGridView.this.mAccessibilityContextMenu.dismiss();
        }
      });
      this.mAccessibilityContextMenu.setOnDismissListener(new ContextMenu.OnDismissListener()
      {
        public void onDismiss()
        {
          EditModeGridView.this.getFocusedChild().setSelected(false);
        }
      });
    }
    this.mAccessibilityContextMenu.show();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/EditModeGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */