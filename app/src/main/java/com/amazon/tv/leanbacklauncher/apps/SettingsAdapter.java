package com.amazon.tv.leanbacklauncher.apps;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory;
import com.amazon.tv.leanbacklauncher.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SettingsAdapter extends AppsAdapter {
    private final ConnectivityListener mConnectivityListener;
    private final Handler mHandler = new NetworkUpdateHandler(this);
    private Resources mNetResources;
    private boolean mNetResourcesSet = false;
    private static String TAG = "SettingsAdapter";

    private static class NetworkUpdateHandler extends Handler {
        private final WeakReference<SettingsAdapter> mAdapterRef;

        public NetworkUpdateHandler(SettingsAdapter adapter) {
            this.mAdapterRef = new WeakReference(adapter);
        }

        public void handleMessage(Message msg) {
            SettingsAdapter adapter = this.mAdapterRef.get();
            if (adapter != null) {
                switch (msg.what) {
                    case 1:
                        int index = adapter.updateNetwork();
                        if (index >= 0) {
                            adapter.notifyItemChanged(index);
                            return;
                        }
                        return;
                    default:
                        return;
                }
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

    public SettingsAdapter(Context context, ConnectivityListener listener) {
        super(context, null, AppCategory.SETTINGS);
        this.mConnectivityListener = listener;
    }

    protected final void onPostRefresh() {
        this.mNetResourcesSet = false;
        updateNetwork();
    }

    public void onConnectivityChange() {
        this.mHandler.sendEmptyMessage(1);
    }

    private int updateNetwork() {
        for (int i = 0; i < this.mLaunchPoints.size(); i++) {
            LaunchPoint launchPoint = this.mLaunchPoints.get(i);
            if (launchPoint.getSettingsType() == 0) {
                setNetwork(this.mContext.getResources(), launchPoint);
                return i;
            }
        }
        return -1;
    }

    private LaunchPoint setNetwork(Resources res, LaunchPoint launchPoint) {
        String str = null;
        if (!this.mNetResourcesSet) {
            setNetworkResources(launchPoint);
        }
        ConnectivityListener.ConnectivityStatus connectivityStatus = this.mConnectivityListener.getConnectivityStatus();
        int titleId = 0;
        String title = null;
        boolean hasNetworkName = false;
        launchPoint.setIconDrawable(null);
        switch (connectivityStatus.mNetworkType) {
            case 1:
                titleId = R.string.settings_network;
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_disconnected"));
                break;
            case 3:
            case 5:
                title = connectivityStatus.mWifiSsid;
                hasNetworkName = true;
                switch (connectivityStatus.mWifiSignalStrength) {
                    case 0:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_0"));
                        break;
                    case 1:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_1"));
                        break;
                    case 2:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_2"));
                        break;
                    case 3:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_3"));
                        break;
                    case 4:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_4"));
                        break;
                    default:
                        break;
                }
                break;
            case 7:
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_ethernet"));
                titleId = R.string.settings_network;
                break;
            case 9:
                title = connectivityStatus.mWifiSsid;
                hasNetworkName = true;
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_wifi_no_internet"));
                break;
            case 11:
                titleId = R.string.settings_network;
                launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_ethernet_no_internet"));
                break;
            case 13:
                title = connectivityStatus.mMobileNetworkName;
                hasNetworkName = true;
                switch (connectivityStatus.mMobileSignalStrength) {
                    case 0:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_0"));
                        break;
                    case 1:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_1"));
                        break;
                    case 2:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_2"));
                        break;
                    case 3:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_3"));
                        break;
                    case 4:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_4"));
                        break;
                    default:
                        break;
                }
                break;
            case 15:
                title = connectivityStatus.mMobileNetworkName;
                hasNetworkName = true;
                switch (connectivityStatus.mMobileSignalStrength) {
                    case 0:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_0"));
                        break;
                    case 1:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_1"));
                        break;
                    case 2:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_2"));
                        break;
                    case 3:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_3"));
                        break;
                    case 4:
                        launchPoint.setIconDrawable(getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_4"));
                        break;
                    default:
                        break;
                }
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
            launchPoint.setContentDescription(str);
        }
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
