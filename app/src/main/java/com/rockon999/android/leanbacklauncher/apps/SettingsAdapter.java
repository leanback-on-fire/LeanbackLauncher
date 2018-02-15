package com.rockon999.android.leanbacklauncher.apps;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.data.ConstData;
import com.rockon999.android.leanbacklauncher.util.NetworkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SettingsAdapter extends AppsAdapter {
    private static final String TAG = "SettingsAdapter";
    private final ConnectivityListener mConnectivityListener;
    private final Handler mHandler;
    private Resources mNetResources;
    private boolean mNetResourcesSet;

    /* renamed from: SettingsAdapter.1 */
    class C01871 extends Handler {
        C01871() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    int index = SettingsAdapter.this.updateNetwork();
                    if (index >= 0) {
                        SettingsAdapter.this.notifyItemChanged(index);
                    }
                default:
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
            if (lhs.getTitle() == null) {
                return -1;
            }
            if (rhs.getTitle() == null) {
                return 1;
            }
            return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
        }
    }

    public SettingsAdapter(Context context, LaunchPointListGenerator launchPointListGenerator, ConnectivityListener listener, AppsRanker appsRanker) {
        super(context, 2, launchPointListGenerator, appsRanker, null);
        this.mNetResourcesSet = false;
        this.mHandler = new C01871();
        this.mConnectivityListener = listener;
    }

    protected final void onPostRefresh() {
        this.mNetResourcesSet = false;
        updateNetwork();
    }

    public void onConnectivityChange() {
        this.mHandler.sendEmptyMessage(1);
    }

    public void onNotificationCountUpdated(int count) {
    }

    private int updateNetwork() {
        Log.i(TAG, "updateNetwork");
        for (int i = 0; i < this.mLaunchPoints.size(); i++) {
            LaunchPoint launchPoint = this.mLaunchPoints.get(i);
            if (launchPoint.getSettingsType() == 0) {
                Log.i(TAG, "updateNetwork 1");
                setNetwork(this.mContext.getResources(), launchPoint);
                return i;
            }
        }
        return -1;
    }

    private LaunchPoint setNetwork(Resources res, LaunchPoint launchPoint) {
        Log.i(TAG, "setNetwork->mNetResourcesSet:" + mNetResourcesSet);
        String str = null;
        if (!this.mNetResourcesSet) {
            setNetworkResources(launchPoint);
        }
        int titleId = 0;
        String title = null;
        boolean hasNetworkName = false;
        launchPoint.setIconDrawable(null);
        int networkState = NetworkUtils.getCurrentNetworkState();
        if (networkState == ConstData.NetworkState.NO) {
            titleId = R.string.settings_network;
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_disconnected"));
        } else if (networkState == ConstData.NetworkState.WIFI) {
            hasNetworkName = true;
            title = NetworkUtils.getWifiSSID();
            int wifiStrength = NetworkUtils.getWifiStrength();
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
        return launchPoint;
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
