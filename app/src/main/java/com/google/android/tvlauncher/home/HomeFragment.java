package com.google.android.tvlauncher.home;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.google.android.tvlauncher.MainBackHomeController;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.instantvideo.preload.InstantVideoPreloadManager;
import com.google.android.tvlauncher.instantvideo.preload.impl.ExoPlayerPreloaderManager;
import com.google.android.tvlauncher.instantvideo.preload.impl.TvPlayerPreloaderManager;
import com.google.android.tvlauncher.notifications.NotificationsContract;
import com.google.android.tvlauncher.notifications.TvNotification;

public class HomeFragment
  extends Fragment
  implements LoaderManager.LoaderCallbacks<Cursor>
{
  private static final int CACHE_SIZE_PER_VIDEO = 2000000;
  private static final boolean DEBUG = false;
  private static final int DISK_CACHE_SIZE_MB = 100;
  private static final int LOADER_ID_COUNT = 1;
  private static final int LOADER_ID_TRAY_NOTIFS = 0;
  private static final String TAG = "HomeFragment";
  private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
  private HomeController mHomeController;
  InstantVideoPreloadManager mInstantVideoPreloadManager;
  
  public void onCreate(@Nullable Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getContext();
    this.mInstantVideoPreloadManager = InstantVideoPreloadManager.getInstance(getContext());
    this.mInstantVideoPreloadManager.registerPreloaderManager(new ExoPlayerPreloaderManager(paramBundle, 100L, 2000000L));
    this.mInstantVideoPreloadManager.registerPreloaderManager(new TvPlayerPreloaderManager(paramBundle));
  }
  
  public Loader<Cursor> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    if (paramInt == 0) {
      return new CursorLoader(getContext(), NotificationsContract.TRAY_CONTENT_URI, TvNotification.PROJECTION, null, null, null);
    }
    return new CursorLoader(getContext(), NotificationsContract.NOTIF_COUNT_CONTENT_URI, null, null, null, null);
  }
  
  @Nullable
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130968620, paramViewGroup, false);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mInstantVideoPreloadManager.unregisterPreloaderManager(null);
  }
  
  public void onLoadFinished(Loader<Cursor> paramLoader, Cursor paramCursor)
  {
    if (paramLoader.getId() == 0)
    {
      this.mHomeController.updateNotifications(paramCursor);
      return;
    }
    this.mHomeController.updatePanelNotifsCount(paramCursor);
  }
  
  public void onLoaderReset(Loader<Cursor> paramLoader)
  {
    if (paramLoader.getId() == 0)
    {
      this.mHomeController.updateNotifications(null);
      return;
    }
    this.mHomeController.updatePanelNotifsCount(null);
  }
  
  public void onPause()
  {
    super.onPause();
    this.mHomeController.onPause();
  }
  
  public void onResume()
  {
    this.mEventLogger.log(new LogEvent("open_home").expectParameters(new String[] { "shown_channel_count", "notification_indicator_total", "notification_indicator_new", "tray_notification_count" }));
    super.onResume();
    this.mHomeController.onResume();
  }
  
  public void onStart()
  {
    super.onStart();
    this.mHomeController.onStart();
  }
  
  public void onStop()
  {
    super.onStop();
    this.mHomeController.onStop();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = (VerticalGridView)paramView.findViewById(2131951796);
    paramBundle = new HomeBackgroundController(paramView);
    this.mHomeController = new HomeController(getActivity(), this.mEventLogger);
    this.mHomeController.setChannelLogoRequestManager(Glide.with(this));
    this.mHomeController.setList(paramView);
    this.mHomeController.setBackgroundController(paramBundle);
    paramView.setAdapter(this.mHomeController);
    MainBackHomeController.getInstance().setOnBackPressedListener(this.mHomeController);
    MainBackHomeController.getInstance().setOnHomePressedListener(this.mHomeController);
    this.mHomeController.setOnBackNotHandledListener(MainBackHomeController.getInstance());
    getLoaderManager().initLoader(0, null, this);
    getLoaderManager().initLoader(1, null, this);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/HomeFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */