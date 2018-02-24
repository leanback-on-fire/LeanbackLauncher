package com.google.android.tvlauncher.appsview;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogUtils;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.util.ContextMenu;

public class AppsViewFragment
  extends Fragment
{
  private static final String TAG = "AppsViewFragment";
  private AppsManager mAppsManager;
  private View mAppsView;
  private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
  private VerticalGridView mGridView;
  private OnAppsViewActionListener mOnAppsViewActionListener;
  private final OnEditModeOrderChangeCallback mOnEditModeOrderChangeCallback = new OnEditModeOrderChangeCallback();
  private RowListAdapter mRowListAdapter;
  
  private OnAppsViewActionListener createOnShowAppsViewListener()
  {
    new OnAppsViewActionListener()
    {
      public void onExitEditModeView() {}
      
      public void onLaunchApp(Intent paramAnonymousIntent)
      {
        try
        {
          AppsViewFragment.this.mEventLogger.log(new UserActionEvent("start_app").putParameter("placement", "apps_view").putParameter("package_name", LogUtils.getPackage(paramAnonymousIntent)));
          AppsViewFragment.this.startActivity(paramAnonymousIntent);
          return;
        }
        catch (ActivityNotFoundException paramAnonymousIntent)
        {
          Toast.makeText(AppsViewFragment.this.getContext(), 2131492990, 0).show();
          Log.e("AppsViewFragment", "Cannot start activity : " + paramAnonymousIntent);
        }
      }
      
      public void onShowAppInfo(String paramAnonymousString)
      {
        AppsViewFragment.this.mEventLogger.log(new UserActionEvent("get_app_info").putParameter("package_name", paramAnonymousString));
        Intent localIntent = new Intent();
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.parse("package:" + paramAnonymousString));
        AppsViewFragment.this.startActivity(localIntent);
      }
      
      public void onShowEditModeView(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        AppsViewFragment.this.getFragmentManager().beginTransaction().replace(16908290, EditModeFragment.newInstance(paramAnonymousInt1, paramAnonymousInt2, AppsViewFragment.this.mRowListAdapter.getTopKeylineForEditMode(paramAnonymousInt1), AppsViewFragment.this.mRowListAdapter.getBottomKeylineForEditMode(paramAnonymousInt1)), "edit_mode_fragment").addToBackStack(null).commit();
      }
      
      public void onStoreLaunch(Intent paramAnonymousIntent)
      {
        try
        {
          AppsViewFragment.this.mEventLogger.log(new UserActionEvent("start_app").putParameter("placement", "store_button").putParameter("package_name", LogUtils.getPackage(paramAnonymousIntent)));
          AppsViewFragment.this.startActivity(paramAnonymousIntent);
          return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          Toast.makeText(AppsViewFragment.this.getContext(), 2131492990, 0).show();
          Log.e("AppsViewFragment", "Cannot start store with package: " + LogUtils.getPackage(paramAnonymousIntent) + ", due to : " + localActivityNotFoundException);
        }
      }
      
      public void onToggleFavorite(LaunchItem paramAnonymousLaunchItem)
      {
        if (AppsViewFragment.this.mAppsManager.isFavorite(paramAnonymousLaunchItem))
        {
          AppsViewFragment.this.mEventLogger.log(new UserActionEvent("unfavorite_app").putParameter("package_name", paramAnonymousLaunchItem.getPackageName()));
          AppsViewFragment.this.mAppsManager.removeFromFavorites(paramAnonymousLaunchItem);
          return;
        }
        AppsViewFragment.this.mEventLogger.log(new UserActionEvent("favorite_app").putParameter("package_name", paramAnonymousLaunchItem.getPackageName()));
        AppsViewFragment.this.mAppsManager.addToFavorites(paramAnonymousLaunchItem);
      }
      
      public void onUninstallApp(String paramAnonymousString)
      {
        AppsViewFragment.this.mEventLogger.log(new UserActionEvent("uninstall_app").putParameter("package_name", paramAnonymousString));
        Intent localIntent = new Intent();
        localIntent.setAction("android.intent.action.UNINSTALL_PACKAGE");
        localIntent.setData(Uri.parse("package:" + paramAnonymousString));
        AppsViewFragment.this.startActivity(localIntent);
      }
    };
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mAppsManager = AppsManager.getInstance(getContext());
    this.mAppsManager.refreshLaunchItems();
    this.mRowListAdapter = new RowListAdapter(getContext(), this.mEventLogger);
    this.mAppsManager.registerAppsViewChangeListener(this.mRowListAdapter);
    this.mOnAppsViewActionListener = createOnShowAppsViewListener();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mAppsView = paramLayoutInflater.inflate(2130968612, paramViewGroup, false);
    return this.mAppsView;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mAppsManager.unregisterAppsViewChangeListener(this.mRowListAdapter);
  }
  
  public void onResume()
  {
    this.mEventLogger.log(new UserActionEvent("open_apps_view").expectParameters(new String[] { "app_count", "game_count" }));
    RecyclerView.Adapter localAdapter;
    int i;
    if (this.mGridView != null)
    {
      localAdapter = this.mGridView.getAdapter();
      i = 0;
    }
    for (;;)
    {
      if (i < localAdapter.getItemCount())
      {
        if (localAdapter.getItemViewType(i) != 6) {
          this.mGridView.scrollToPosition(i);
        }
      }
      else
      {
        super.onResume();
        return;
      }
      i += 1;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mRowListAdapter.onStart();
  }
  
  public void onStop()
  {
    super.onStop();
    this.mRowListAdapter.onStop();
    Object localObject = this.mGridView.getFocusedChild();
    if ((localObject instanceof AppRowView))
    {
      localObject = ((AppRowView)localObject).getFocusedChild();
      if ((localObject instanceof AppBannerView))
      {
        localObject = ((AppBannerView)localObject).getAppMenu();
        if ((localObject != null) && (((ContextMenu)localObject).isShowing())) {
          ((ContextMenu)localObject).forceDismiss();
        }
      }
    }
  }
  
  public void onViewCreated(View paramView, @Nullable Bundle paramBundle)
  {
    this.mGridView = ((VerticalGridView)this.mAppsView.findViewById(2131951792));
    this.mRowListAdapter.setAppLaunchItems(AppsManager.getInstance(getContext()).getAppLaunchItems());
    this.mRowListAdapter.setGameLaunchItems(AppsManager.getInstance(getContext()).getGameLaunchItems());
    this.mRowListAdapter.setOemLaunchItems(AppsManager.getInstance(getContext()).getOemLaunchItems());
    this.mRowListAdapter.setOnAppsViewActionListener(this.mOnAppsViewActionListener);
    this.mRowListAdapter.setOnEditModeOrderChangeCallback(this.mOnEditModeOrderChangeCallback);
    this.mRowListAdapter.initRows();
    this.mGridView.setWindowAlignment(0);
    this.mGridView.setWindowAlignmentOffsetPercent(-1.0F);
    this.mGridView.setAdapter(this.mRowListAdapter);
    this.mAppsView.requestFocus();
  }
  
  public void startEditMode(int paramInt)
  {
    if (this.mOnAppsViewActionListener != null) {
      this.mOnAppsViewActionListener.onShowEditModeView(paramInt, 0);
    }
  }
  
  class OnEditModeOrderChangeCallback
  {
    OnEditModeOrderChangeCallback() {}
    
    void onEditModeExited(int paramInt1, final int paramInt2)
    {
      AppsViewFragment.this.mGridView.scrollToPosition(paramInt1);
      AppsViewFragment.this.mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          View localView = AppsViewFragment.this.mGridView.getFocusedChild();
          if ((localView instanceof AppRowView)) {
            ((AppRowView)localView).setOneTimeFocusPosition(paramInt2);
          }
          AppsViewFragment.this.mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      });
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppsViewFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */