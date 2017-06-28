package com.rockchips.android.leanbacklauncher.apps;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.data.ConstData;
import com.rockchips.android.leanbacklauncher.notifications.NotificationsAdapter;
import com.rockchips.android.leanbacklauncher.util.NetWorkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SettingsAdapter extends AppsAdapter implements NotificationsAdapter.NotificationCountListener {
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
            LaunchPoint launchPoint = (LaunchPoint) this.mLaunchPoints.get(i);
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
        int netWorkState = NetWorkUtils.getCurrentNetWrokState();
        if(netWorkState == ConstData.NetWorkState.NO){
            //未连接网络
            titleId = R.string.settings_network;
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_disconnected"));
        }else if(netWorkState == ConstData.NetWorkState.WIFI){
            hasNetworkName = true;
            title = NetWorkUtils.getWifiSSID();
            //已连接WIFI
            int wifiStrength = NetWorkUtils.getWifiStrength();
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_" + wifiStrength));
        }else if(netWorkState == ConstData.NetWorkState.ETHERNET){
            //已连接以太网
            launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_ethernet"));
            titleId = R.string.settings_network;
        }
/*        ConnectivityListener.ConnectivityStatus connectivityStatus = this.mConnectivityListener.getConnectivityStatus();
        Log.i(TAG, "setNetwork->mNetworkType:" + connectivityStatus.mNetworkType);
        switch (connectivityStatus.mNetworkType) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability *//*1*//*:
                titleId = R.string.settings_network;
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_disconnected"));
                break;
            case android.support.v7.preference.R.styleable.Preference_android_layout *//*3*//*:
            case android.support.v7.preference.R.styleable.Preference_android_selectable *//*5*//*:
                title = connectivityStatus.mWifiSsid;
                hasNetworkName = true;
                switch (connectivityStatus.mWifiSignalStrength) {
                    case android.support.v7.preference.R.styleable.Preference_android_icon *//*0*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_0"));
                        break;
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability *//*1*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_1"));
                        break;
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager *//*2*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_2"));
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_layout *//*3*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_3"));
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_title *//*4*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_4"));
                        break;
                    default:
                        break;
                }
            case android.support.v7.preference.R.styleable.Preference_android_summary *//*7*//*:
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_ethernet"));
                titleId = R.string.settings_network;
                break;
            case android.support.v7.preference.R.styleable.Preference_android_widgetLayout *//*9*//*:
                title = connectivityStatus.mWifiSsid;
                hasNetworkName = true;
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_no_internet"));
                break;
            case android.support.v7.preference.R.styleable.Preference_android_defaultValue *//*11*//*:
                titleId = R.string.settings_network;
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_ethernet_no_internet"));
                break;
            case android.support.v7.preference.R.styleable.Preference_android_fragment *//*13*//*:
                title = connectivityStatus.mMobileNetworkName;
                hasNetworkName = true;
                switch (connectivityStatus.mMobileSignalStrength) {
                    case android.support.v7.preference.R.styleable.Preference_android_icon *//*0*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_0"));
                        break;
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability *//*1*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_1"));
                        break;
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager *//*2*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_2"));
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_layout *//*3*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_3"));
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_title *//*4*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_4"));
                        break;
                    default:
                        break;
                }
            case android.support.v7.preference.R.styleable.Preference_summary *//*15*//*:
                title = connectivityStatus.mMobileNetworkName;
                hasNetworkName = true;
                switch (connectivityStatus.mMobileSignalStrength) {
                    case android.support.v7.preference.R.styleable.Preference_android_icon *//*0*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_0"));
                        break;
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability *//*1*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_1"));
                        break;
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager *//*2*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_2"));
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_layout *//*3*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_3"));
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_title *//*4*//*:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_4"));
                        break;
                    default:
                        break;
                }
        }*/
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
            this.mNetResources = this.mContext.getPackageManager().getResourcesForApplication(lp.getLaunchIntent().getComponent().getPackageName());
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
