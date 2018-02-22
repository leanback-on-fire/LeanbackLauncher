package com.rockon999.android.leanbacklauncher.apps;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.notifications.NotificationListenerV12;
import com.rockon999.android.leanbacklauncher.util.ConstData;
import com.rockon999.android.leanbacklauncher.util.NetworkUtils;
import com.rockon999.android.leanbacklauncher.util.SettingsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SettingsAdapter extends AppsAdapter {
    private static final String TAG = "SettingsAdapter";
    private final ConnectivityListener mConnectivityListener;
    private final Handler mHandler;
    private Resources mNetResources;
    private boolean mNetResourcesSet;

    private NotificationManager manager;


    class SettingsHandler extends Handler {
        SettingsHandler() {
        }

        @SuppressLint("PrivateResource")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int index = SettingsAdapter.this.updateNetwork();

                    if (index >= 0) {
                        SettingsAdapter.this.notifyItemChanged(index);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class SettingsComparator implements Comparator<LaunchPoint> {
        private SettingsComparator() {
        }

        public int compare(LaunchPoint lhs, LaunchPoint rhs) {
            if (lhs.getPriority() != rhs.getPriority()) {
                return rhs.getPriority() - lhs.getPriority();
            }

            // todo magic numbers
            if (lhs.getSettingsType() < 0) {
                return 1;
            }
            if (rhs.getSettingsType() < 0) {
                return -1;
            }

            return Integer.compare(lhs.getSettingsType(), rhs.getSettingsType());
        }
    }

    public SettingsAdapter(Context context, LaunchPointListGenerator launchPointListGenerator, ConnectivityListener listener, AppsRanker appsRanker) {
        super(context, launchPointListGenerator, appsRanker, false, AppCategory.SETTINGS);

        this.mNetResourcesSet = false;
        this.mHandler = new SettingsHandler();
        this.mConnectivityListener = listener;

        BroadcastReceiver onNotice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Count updating...");
                int amt = intent.getIntExtra("count", -2);
                SettingsAdapter.this.onNotificationCountUpdated(amt);
            }
        };

        LocalBroadcastManager.getInstance(mContext.getApplicationContext()).registerReceiver(onNotice, new IntentFilter(NotificationListenerV12.LISTENER_INTENT_OUT));
    }

    protected final void onPostRefresh() {
        this.mNetResourcesSet = false;
        updateNetwork();
        updateNotificationCount();
    }

    public void updateNotificationCount() {
        Log.d(TAG, "Sending broadcast to request an update for notifications");
        Intent msgrcv = new Intent(NotificationListenerV12.LISTENER_INTENT_IN);
        LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(msgrcv);
    }

    public void onConnectivityChange() {
        this.mHandler.sendEmptyMessage(1);
    }

    public void onNotificationCountUpdated(int count) {
        Log.i(TAG, "count updated");
        for (int i = 0; i < this.mLaunchPoints.size(); i++) {
            LaunchPoint launchPoint = this.mLaunchPoints.get(i);
            if (launchPoint.getSettingsType() == SettingsUtil.Type.NOTIFICATIONS.getCode()) {
                Log.i(TAG, "count: " + count);
                launchPoint.setTitle("Notifications (" + count + ")"); // todo rtl
                notifyDataSetChanged();
            }
        }
    }

    private int updateNetwork() {
        Log.i(TAG, "updateNetwork");
        for (int i = 0; i < this.mLaunchPoints.size(); i++) {
            LaunchPoint launchPoint = this.mLaunchPoints.get(i);
            if (launchPoint.getSettingsType() == SettingsUtil.Type.WIFI.getCode()) {
                Log.i(TAG, "updateNetwork 1");
                setNetwork(this.mContext.getResources(), launchPoint);
                return i;
            }
        }
        return -1;
    }

    private void setNetwork(Resources res, LaunchPoint launchPoint) {
        Log.i(TAG, "setNetwork->mNetResourcesSet:" + mNetResourcesSet);
        String str = null;
        if (!this.mNetResourcesSet) {
            setNetworkResources(launchPoint);
        }
        int titleId = 0;
        String title = null;
        boolean hasNetworkName = false;
        launchPoint.setIconDrawable(null);
        int networkState = NetworkUtils.getCurrentNetworkState(mContext);
        if (networkState == ConstData.NetworkState.NO) {
            titleId = R.string.settings_network;
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_disconnected"));
        } else if (networkState == ConstData.NetworkState.WIFI) {
            hasNetworkName = true;
            title = NetworkUtils.getWifiSSID(mContext);
            int wifiStrength = NetworkUtils.getWifiStrength(mContext);
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_" + wifiStrength));
        } else if (networkState == ConstData.NetworkState.ETHERNET) {
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_ethernet"));
            titleId = R.string.settings_network;
        }

        if (launchPoint.getIconDrawable() == null) {
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_0"));
        }
        if (titleId != 0) {
            title = res.getString(titleId);
        }
        if (title == null) {
            title = res.getString(R.string.settings_network);
        }
        launchPoint.setTitle(title);

        if (hasNetworkName) {
            str = res.getString(R.string.settings_network);
        }
        launchPoint.setContentDescription(str);
    }

    public void onRankerReady() {
    }

    protected final void sortLaunchPoints(ArrayList<LaunchPoint> launchPoints) {
        Collections.sort(launchPoints, new SettingsComparator());
    }

    private void setNetworkResources(LaunchPoint lp) {
        try {
            ComponentName name = lp.getLaunchIntent().getComponent();

            if (name != null) {
                this.mNetResources = this.mContext.getPackageManager().getResourcesForApplication(name.getPackageName());
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        this.mNetResourcesSet = true;
    }

    private Drawable getNetResourceDrawable(LaunchPoint launchPoint, String resName) {
        int resId = 0;
        if (this.mNetResources != null) {
            resId = this.mNetResources.getIdentifier(resName, "drawable", launchPoint.getPackageName());
        }
        if (resId != 0) {
            Drawable ret = this.mNetResources.getDrawable(resId, null);
            if (ret != null) {
                return ret;
            }
        }
        Resources launcherRes = this.mContext.getResources();
        return launcherRes.getDrawable(launcherRes.getIdentifier(resName, "drawable", this.mContext.getPackageName()), null);
    }

    public void onSettingsChanged() {
        refreshDataSetAsync();
    }

    public void onLaunchPointsAddedOrUpdated(ArrayList<LaunchPoint> arrayList) {
    }

    public void onLaunchPointsRemoved(ArrayList<LaunchPoint> arrayList) {
    }


}
