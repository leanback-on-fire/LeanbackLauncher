package com.google.android.tvlauncher.appsview;

import android.app.Activity;
import android.content.Context;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;

public class EditModeGridView extends VerticalGridView {
    private static final int ACCESSIBILITY_MENU_DONE = 4;
    private static final int ACCESSIBILITY_MENU_DOWN = 1;
    private static final int ACCESSIBILITY_MENU_LEFT = 2;
    private static final int ACCESSIBILITY_MENU_RIGHT = 3;
    private static final int ACCESSIBILITY_MENU_UP = 0;
    private AccessibilityContextMenu mAccessibilityContextMenu;

    public EditModeGridView(Context paramContext) {
        this(paramContext, null);
    }

    public EditModeGridView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public EditModeGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setColumnWidth(getResources().getDimensionPixelSize(R.dimen.banner_width) + getResources().getDimensionPixelSize(R.dimen.banner_margin_end)); // 2131558505
    }

    private void moveLaunchPoint(int paramInt1, int paramInt2) {
        ((EditModeGridAdapter) getAdapter()).moveLaunchItems(paramInt1, paramInt2);
    }

    private View swapItemsIfNeeded(View focused, int direction) {
        int position = getChildAdapterPosition(focused);
        if (!getItemAnimator().isRunning()) {
            if (getLayoutDirection() == LAYOUT_DIRECTION_RTL && (direction == 17 || direction == 66)) {
                direction = direction == 17 ? 66 : 17;
            }
            switch (direction) {
                case 17:
                    if (position % 4 > 0) {
                        moveLaunchPoint(position, position - 1);
                        break;
                    }
                    break;
                case 33:
                    moveLaunchPoint(position, position - 4);
                    break;
                case 66:
                    if (position % 4 < 3 && position < getAdapter().getItemCount() - 1) {
                        moveLaunchPoint(position, position + 1);
                        break;
                    }
                case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                    moveLaunchPoint(position, position + 4);
                    break;
                default:
                    break;
            }
        }
        return focused;
    }

    public View focusSearch(View paramView, int paramInt) {
        if (paramView.isSelected()) {
            return swapItemsIfNeeded(paramView, paramInt);
        }
        return super.focusSearch(paramView, paramInt);
    }

    void hideAccessibilityMenu() {
        if (this.mAccessibilityContextMenu != null) {
            this.mAccessibilityContextMenu.dismiss();
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Util.isAccessibilityEnabled(getContext())) {
            showAccessibilityMenu();
        }
    }

    void showAccessibilityMenu() {
        Context localContext = getContext();
        if (this.mAccessibilityContextMenu == null) {
            this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity) localContext);
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, localContext.getString(R.string.accessibility_menu_item_move_up), localContext.getDrawable(R.drawable.ic_arrow_up_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, localContext.getString(R.string.accessibility_menu_item_move_down), localContext.getDrawable(R.drawable.ic_arrow_down_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(2, localContext.getString(R.string.accessibility_menu_item_move_left), localContext.getDrawable(R.drawable.ic_arrow_left_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(3, localContext.getString(R.string.accessibility_menu_item_move_right), localContext.getDrawable(R.drawable.ic_arrow_right_black_24dp)));
            this.mAccessibilityContextMenu.addItem(new ContextMenuItem(4, localContext.getString(R.string.accessibility_menu_item_done), localContext.getDrawable(R.drawable.ic_done_black_24dp)));
            this.mAccessibilityContextMenu.setOnMenuItemClickListener(new ContextMenu.OnItemClickListener() {
                public void onItemClick(ContextMenuItem paramAnonymousContextMenuItem) {
                    switch (paramAnonymousContextMenuItem.getId()) {
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
                        case 4:
                        case 5:
                        case 6:
                            EditModeGridView.this.mAccessibilityContextMenu.dismiss();
                        default:
                    }
                }
            });
            this.mAccessibilityContextMenu.setOnDismissListener(new ContextMenu.OnDismissListener() {
                public void onDismiss() {
                    EditModeGridView.this.getFocusedChild().setSelected(false);
                }
            });
        }
        this.mAccessibilityContextMenu.show();
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/EditModeGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */