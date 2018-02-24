package com.google.android.tvlauncher.home;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
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
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.instantvideo.preload.InstantVideoPreloadManager;
import com.google.android.tvlauncher.instantvideo.preload.impl.ExoPlayerPreloaderManager;
import com.google.android.tvlauncher.instantvideo.preload.impl.TvPlayerPreloaderManager;
import com.google.android.tvlauncher.notifications.NotificationsContract;
import com.google.android.tvlauncher.notifications.TvNotification;

public class HomeFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private static final int CACHE_SIZE_PER_VIDEO = 2000000;
    private static final boolean DEBUG = false;
    private static final int DISK_CACHE_SIZE_MB = 100;
    private static final int LOADER_ID_COUNT = 1;
    private static final int LOADER_ID_TRAY_NOTIFS = 0;
    private static final String TAG = "HomeFragment";
    private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
    private HomeController mHomeController;
    InstantVideoPreloadManager mInstantVideoPreloadManager;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getActivity();//getActivity();
        this.mInstantVideoPreloadManager = InstantVideoPreloadManager.getInstance(ctx);
        this.mInstantVideoPreloadManager.registerPreloaderManager(new ExoPlayerPreloaderManager(ctx, 100, 2000000));
        this.mInstantVideoPreloadManager.registerPreloaderManager(new TvPlayerPreloaderManager(ctx));
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VerticalGridView homeRowList = (VerticalGridView) view.findViewById(R.id.home_row_list);
        HomeBackgroundController backgroundController = new HomeBackgroundController(homeRowList);
        this.mHomeController = new HomeController(getActivity(), this.mEventLogger);
        this.mHomeController.setChannelLogoRequestManager(Glide.with((Fragment) this));
        this.mHomeController.setList(homeRowList);
        this.mHomeController.setBackgroundController(backgroundController);
        homeRowList.setAdapter(this.mHomeController);
        MainBackHomeController.getInstance().setOnBackPressedListener(this.mHomeController);
        MainBackHomeController.getInstance().setOnHomePressedListener(this.mHomeController);
        this.mHomeController.setOnBackNotHandledListener(MainBackHomeController.getInstance());
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
    }

    public void onStart() {
        super.onStart();
        this.mHomeController.onStart();
    }

    public void onStop() {
        super.onStop();
        this.mHomeController.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mInstantVideoPreloadManager.unregisterPreloaderManager(null);
    }

    public void onResume() {
        this.mEventLogger.log(new LogEvent(LogEvents.OPEN_HOME).expectParameters(LogEvents.PARAMETER_SHOWN_CHANNEL_COUNT, LogEvents.PARAMETER_NOTIFICATION_INDICATOR_TOTAL, LogEvents.PARAMETER_NOTIFICATION_INDICATOR_NEW, LogEvents.PARAMETER_TRAY_NOTIFICATION_COUNT));
        super.onResume();
        this.mHomeController.onResume();
    }

    public void onPause() {
        super.onPause();
        this.mHomeController.onPause();
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new CursorLoader(getActivity(), NotificationsContract.TRAY_CONTENT_URI, TvNotification.PROJECTION, null, null, null);
        }
        return new CursorLoader(getActivity(), NotificationsContract.NOTIF_COUNT_CONTENT_URI, null, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == 0) {
            this.mHomeController.updateNotifications(data);
        } else {
            this.mHomeController.updatePanelNotifsCount(data);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == 0) {
            this.mHomeController.updateNotifications(null);
        } else {
            this.mHomeController.updatePanelNotifsCount(null);
        }
    }
}
