package com.google.android.tvlauncher.home;

import android.app.Activity;
import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.home.util.ChannelUtil;
import com.google.android.tvlauncher.util.AccessibilityContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;

class WatchNextRowController
  implements HomeRow, ChannelView.OnPerformMainActionListener, ChannelView.OnRemoveListener, ChannelView.OnStateChangeGesturePerformedListener, BackHomeControllerListeners.OnBackPressedListener, BackHomeControllerListeners.OnHomePressedListener
{
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
  private BackHomeControllerListeners.OnBackNotHandledListener mOnBackNotHandledListener;
  private BackHomeControllerListeners.OnHomeNotHandledListener mOnHomeNotHandledListener;
  private OnHomeRowRemovedListener mOnHomeRowRemovedListener;
  private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
  private OnHomeStateChangeListener mOnHomeStateChangeListener;
  private OnProgramSelectedListener mOnProgramSelectedListener;
  
  WatchNextRowController(ChannelView paramChannelView, EventLogger paramEventLogger)
  {
    this.mChannelView = paramChannelView;
    this.mEventLogger = paramEventLogger;
    this.mChannelView.setOnPerformMainActionListener(this);
    this.mChannelView.setOnRemoveListener(this);
    this.mChannelView.setOnStateChangeGesturePerformedListener(this);
    this.mChannelView.setAllowMoving(false);
    this.mChannelView.setStateSettings(ChannelUtil.getDefaultChannelStateSettings(paramChannelView.getContext()));
    paramChannelView = this.mChannelView.getContext();
    this.mChannelView.setChannelTitle(paramChannelView.getString(R.string.watch_next_channel_title));
    ChannelUtil.setWatchNextLogo(this.mChannelView.getChannelLogoImageView());
    this.mItemsListView = this.mChannelView.getItemsListView();
    ChannelUtil.configureItemsListAlignment(this.mItemsListView);
    this.mLastItemMetaTitle = paramChannelView.getString(R.string.watch_next_channel_title);
    this.mLastItemMetaMessage = paramChannelView.getString(R.string.watch_next_last_item_message);
  }
  
  private void ensureItemListIsSetUp()
  {
    if (this.mItemsAdapter != null) {
      return;
    }
    this.mItemsAdapter = new WatchNextItemsAdapter(this.mChannelView.getContext(), this.mEventLogger);
    this.mItemsAdapter.setOnProgramSelectedListener(this.mOnProgramSelectedListener);
    this.mItemsListView.setAdapter(this.mItemsAdapter);
    this.mItemsListView.setItemAnimator(null);
    this.mMetadataController = new ChannelItemMetadataController(this.mChannelView.getItemMetadataView());
  }
  
  private int getProgramState(int paramInt)
  {
    switch (paramInt)
    {
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
  
  private void showAccessibilityMenu()
  {
    if (this.mAccessibilityContextMenu == null)
    {
      Context localContext = this.mChannelView.getContext();
      this.mAccessibilityContextMenu = new AccessibilityContextMenu((Activity)localContext);
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(0, localContext.getString(R.string.channel_action_remove), localContext.getDrawable(R.drawable.ic_remove_circle_black)));
      this.mAccessibilityContextMenu.addItem(new ContextMenuItem(1, localContext.getString(R.string.accessibility_menu_item_done), localContext.getDrawable(R.drawable.ic_done_black_24dp)));
      this.mAccessibilityContextMenu.setOnMenuItemClickListener(new ContextMenu.OnItemClickListener()
      {
        public void onItemClick(ContextMenuItem paramAnonymousContextMenuItem)
        {
          switch (paramAnonymousContextMenuItem.getId())
          {
          default: 
            return;
          case 0: 
            WatchNextRowController.this.mAccessibilityContextMenu.dismiss();
            WatchNextRowController.this.onRemove(WatchNextRowController.this.mChannelView);
            return;
          }
          WatchNextRowController.this.mAccessibilityContextMenu.dismiss();
        }
      });
    }
    this.mAccessibilityContextMenu.show();
  }
  
  private void updateItemsListPosition(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {}
    do
    {
      return;
      if ((paramInt1 == 2) && (this.mItemsAdapter.getItemCount() > 1) && (this.mItemsListView.getSelectedPosition() != 0))
      {
        this.mLastSelectedItemPosition = this.mItemsListView.getSelectedPosition();
        this.mItemsListView.scrollToPosition(0);
        return;
      }
    } while ((this.mItemsAdapter.getItemCount() <= 1) || (this.mLastSelectedItemPosition == -1));
    paramInt1 = this.mLastSelectedItemPosition;
    this.mLastSelectedItemPosition = -1;
    this.mItemsListView.scrollToPosition(paramInt1);
  }
  
  void bind(int paramInt)
  {
    ensureItemListIsSetUp();
    this.mChannelView.setState(paramInt);
    int i = this.mItemsAdapter.getProgramState();
    paramInt = getProgramState(paramInt);
    this.mItemsAdapter.bind(paramInt);
    updateItemsListPosition(paramInt, i);
  }
  
  void bindItemMetadata()
  {
    int i = this.mItemsListView.getSelectedPosition();
    TvDataManager localTvDataManager = TvDataManager.getInstance(this.mItemsListView.getContext());
    if ((i == -1) || (this.mItemsAdapter.getItemCount() == 0)) {
      this.mMetadataController.clear();
    }
    do
    {
      return;
      if ((this.mItemsAdapter.getItemCount() > 0) && (this.mItemsAdapter.getItemViewType(i) == 0))
      {
        this.mMetadataController.clear();
        this.mMetadataController.setFirstRow(this.mLastItemMetaTitle);
        this.mMetadataController.setThirdRow(this.mLastItemMetaMessage, 3);
        return;
      }
    } while ((i < 0) || (i >= localTvDataManager.getWatchNextProgramsCount()));
    this.mMetadataController.bindView(localTvDataManager.getWatchNextProgram(i));
  }
  
  public View getView()
  {
    return this.mChannelView;
  }
  
  public void onBackPressed(Context paramContext)
  {
    if ((this.mChannelView.getState() == 0) && (this.mItemsListView.getSelectedPosition() != 0) && (this.mItemsListView.getAdapter().getItemCount() > 0)) {
      this.mItemsListView.setSelectedPositionSmooth(0);
    }
    do
    {
      return;
      if (this.mChannelView.getState() == 6)
      {
        onStateChangeGesturePerformed(this.mChannelView, 4);
        return;
      }
    } while (this.mOnBackNotHandledListener == null);
    this.mOnBackNotHandledListener.onBackNotHandled(paramContext);
  }
  
  public void onHomePressed(Context paramContext)
  {
    if ((this.mAccessibilityContextMenu != null) && (this.mAccessibilityContextMenu.isShowing())) {
      this.mAccessibilityContextMenu.dismiss();
    }
    do
    {
      return;
      if (this.mChannelView.getState() == 6)
      {
        onStateChangeGesturePerformed(this.mChannelView, 4);
        return;
      }
    } while (this.mOnHomeNotHandledListener == null);
    this.mOnHomeNotHandledListener.onHomeNotHandled(paramContext);
  }
  
  public void onPerformMainAction(ChannelView paramChannelView)
  {
    if (Util.isAccessibilityEnabled(paramChannelView.getContext())) {
      showAccessibilityMenu();
    }
  }
  
  public void onRemove(ChannelView paramChannelView)
  {
    if (this.mOnHomeRowRemovedListener != null) {
      this.mOnHomeRowRemovedListener.onHomeRowRemoved(this);
    }
  }
  
  public void onStart()
  {
    this.mItemsAdapter.onStart();
  }
  
  public void onStateChangeGesturePerformed(ChannelView paramChannelView, int paramInt)
  {
    switch (paramInt)
    {
    default: 
    case 0: 
    case 1: 
    case 4: 
    case 5: 
    case 6: 
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
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
    }
    throw new IllegalStateException("Unsupported ChannelView state change gesture: " + ChannelView.stateToString(paramInt));
  }
  
  public void onStop()
  {
    this.mItemsAdapter.onStop();
  }
  
  void recycle()
  {
    this.mLastSelectedItemPosition = -1;
    this.mItemsAdapter.recycle();
    this.mMetadataController.clear();
    if (this.mAccessibilityContextMenu != null) {
      this.mAccessibilityContextMenu.dismiss();
    }
  }
  
  void setOnBackNotHandledListener(BackHomeControllerListeners.OnBackNotHandledListener paramOnBackNotHandledListener)
  {
    this.mOnBackNotHandledListener = paramOnBackNotHandledListener;
  }
  
  void setOnHomeNotHandledListener(BackHomeControllerListeners.OnHomeNotHandledListener paramOnHomeNotHandledListener)
  {
    this.mOnHomeNotHandledListener = paramOnHomeNotHandledListener;
  }
  
  public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener paramOnHomeRowRemovedListener)
  {
    this.mOnHomeRowRemovedListener = paramOnHomeRowRemovedListener;
  }
  
  public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener paramOnHomeRowSelectedListener)
  {
    this.mOnHomeRowSelectedListener = paramOnHomeRowSelectedListener;
  }
  
  public void setOnHomeStateChangeListener(OnHomeStateChangeListener paramOnHomeStateChangeListener)
  {
    this.mOnHomeStateChangeListener = paramOnHomeStateChangeListener;
  }
  
  void setOnProgramSelectedListener(OnProgramSelectedListener paramOnProgramSelectedListener)
  {
    this.mOnProgramSelectedListener = paramOnProgramSelectedListener;
  }
  
  public void setState(int paramInt)
  {
    this.mChannelView.setState(paramInt);
    int i = this.mItemsAdapter.getProgramState();
    paramInt = getProgramState(paramInt);
    this.mItemsAdapter.setProgramState(paramInt);
    updateItemsListPosition(paramInt, i);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/WatchNextRowController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */