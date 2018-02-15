package com.rockon999.android.leanbacklauncher.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.rockon999.android.leanbacklauncher.data.ConstData;

import momo.cn.edu.fjnu.androidutils.data.CommonValues;

public class NetworkUtils {
    public static int getCurrentNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) CommonValues.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null || connectivityManager.getActiveNetworkInfo() == null)
            return ConstData.NetworkState.NO;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        int networkType = networkInfo.getType();
        if (networkType == ConnectivityManager.TYPE_WIFI)
            return ConstData.NetworkState.WIFI;
        else if (networkType == ConnectivityManager.TYPE_ETHERNET)
            return ConstData.NetworkState.ETHERNET;
        return ConstData.NetworkState.NO;
    }

    public static int getWifiStrength() {
        WifiManager wifiManager = (WifiManager) CommonValues.application.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                return WifiManager.calculateSignalLevel(info.getRssi(), 5);
            }
        }
        return 0;
    }

    public static String getWifiSSID() {
        WifiManager wifiManager = (WifiManager) CommonValues.application.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                return info.getSSID();
            }
        }
        return "";
    }
}
