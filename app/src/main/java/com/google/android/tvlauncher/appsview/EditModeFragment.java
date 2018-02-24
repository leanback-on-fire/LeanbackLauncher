package com.google.android.tvlauncher.appsview;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.util.Util;
import java.util.ArrayList;

public class EditModeFragment
  extends Fragment
{
  public static final int EDIT_TYPE_APPS = 0;
  public static final int EDIT_TYPE_GAMES = 1;
  private static final String KEY_BOTTOM_KEYLINE = "key_bottom_keyline";
  public static final String KEY_EDIT_MODE_FOCUSED_POSITION = "key_edit_mode_focused_position";
  public static final String KEY_EDIT_MODE_TYPE = "key_edit_mode_type";
  private static final String KEY_TOP_KEYLINE = "key_top_keyline";
  private static final String TAG = "EditModeFragment";
  private EditModeGridAdapter mEditAdapter;
  private View mEditModeView;
  private int mEditType;
  private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
  private int mFocusPosition;
  private EditModeGridView mGridView;
  private OnShowAccessibilityMenuListener mOnShowAccessibilityMenuListener;
  
  public static EditModeFragment newInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("key_edit_mode_type", paramInt1);
    localBundle.putInt("key_edit_mode_focused_position", paramInt2);
    localBundle.putInt("key_top_keyline", paramInt3);
    localBundle.putInt("key_bottom_keyline", paramInt4);
    EditModeFragment localEditModeFragment = new EditModeFragment();
    localEditModeFragment.setArguments(localBundle);
    return localEditModeFragment;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getArguments();
    this.mEditType = paramBundle.getInt("key_edit_mode_type");
    this.mFocusPosition = paramBundle.getInt("key_edit_mode_focused_position");
    this.mEditAdapter = new EditModeGridAdapter(getContext());
    AppsManager.getInstance(getContext()).refreshLaunchItems();
    AppsManager.getInstance(getContext()).registerAppsViewChangeListener(this.mEditAdapter);
    this.mEditAdapter.setTopKeyline(paramBundle.getInt("key_top_keyline"));
    this.mEditAdapter.setBottomKeyline(paramBundle.getInt("key_bottom_keyline"));
    this.mOnShowAccessibilityMenuListener = new OnShowAccessibilityMenuListener()
    {
      public void onShowAccessibilityMenu(boolean paramAnonymousBoolean)
      {
        if ((Util.isAccessibilityEnabled(EditModeFragment.this.getContext())) && (paramAnonymousBoolean))
        {
          EditModeFragment.this.mGridView.showAccessibilityMenu();
          return;
        }
        EditModeFragment.this.mGridView.hideAccessibilityMenu();
      }
    };
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mEditModeView = paramLayoutInflater.inflate(2130968616, paramViewGroup, false);
    return this.mEditModeView;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    AppsManager.getInstance(getContext()).unregisterAppsViewChangeListener(this.mEditAdapter);
  }
  
  public void onPause()
  {
    boolean bool = true;
    int i = this.mGridView.getSelectedPosition();
    ArrayList localArrayList = this.mEditAdapter.getLaunchItems();
    Object localObject = AppsManager.getInstance(getContext());
    UserActionEvent localUserActionEvent;
    if (this.mEditType == 1)
    {
      ((AppsManager)localObject).onAppOrderChange(localArrayList, bool, LaunchItemsHolder.getRowColIndexFromListIndex(i));
      this.mOnShowAccessibilityMenuListener.onShowAccessibilityMenu(false);
      super.onPause();
      localUserActionEvent = new UserActionEvent("exit_edit_apps_mode");
      if (this.mEditType != 0) {
        break label150;
      }
    }
    label150:
    for (localObject = "app";; localObject = "game")
    {
      localUserActionEvent.putParameter("package_category", (String)localObject);
      int j = localArrayList.size();
      i = 0;
      while ((i < j) && (i < LogEvents.PACKAGE_NAME_PARAMETERS.length))
      {
        localUserActionEvent.putParameter(LogEvents.PACKAGE_NAME_PARAMETERS[i], ((LaunchItem)localArrayList.get(i)).getPackageName());
        i += 1;
      }
      bool = false;
      break;
    }
    this.mEventLogger.log(localUserActionEvent);
  }
  
  public void onResume()
  {
    super.onResume();
    FragmentEventLogger localFragmentEventLogger = this.mEventLogger;
    UserActionEvent localUserActionEvent = new UserActionEvent("enter_edit_apps_mode");
    if (this.mEditType == 0) {}
    for (String str = "app";; str = "game")
    {
      localFragmentEventLogger.log(localUserActionEvent.putParameter("package_category", str));
      return;
    }
  }
  
  public void onViewCreated(View paramView, @Nullable Bundle paramBundle)
  {
    this.mGridView = ((EditModeGridView)this.mEditModeView.findViewById(2131951781));
    if (this.mEditType == 0) {
      this.mEditAdapter.setLaunchItems(AppsManager.getInstance(getContext()).getAppLaunchItems());
    }
    for (;;)
    {
      this.mEditAdapter.setOnShowAccessibilityMenuListener(this.mOnShowAccessibilityMenuListener);
      if (this.mEditAdapter.getItemCount() <= 0) {
        getActivity().getFragmentManager().popBackStack();
      }
      this.mGridView.setNumColumns(4);
      this.mGridView.setAdapter(this.mEditAdapter);
      this.mGridView.setSelectedPosition(this.mFocusPosition);
      this.mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          View localView = EditModeFragment.this.mGridView.getFocusedChild();
          if ((localView instanceof EditModeBannerView))
          {
            localView.setSelected(true);
            EditModeFragment.this.mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
        }
      });
      this.mGridView.setWindowAlignment(0);
      this.mGridView.setWindowAlignmentOffsetPercent(-1.0F);
      this.mGridView.requestFocus();
      return;
      if (this.mEditType == 1) {
        this.mEditAdapter.setLaunchItems(AppsManager.getInstance(getContext()).getGameLaunchItems());
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/EditModeFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */