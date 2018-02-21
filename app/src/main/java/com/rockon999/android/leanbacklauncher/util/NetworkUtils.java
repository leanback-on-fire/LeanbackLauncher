package com.rockon999.android.leanbacklauncher.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {
    public static int getCurrentNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static int getWifiStrength(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                return WifiManager.calculateSignalLevel(info.getRssi(), 5);
            }
        }
        return 0;
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                return info.getSSID();
            }
        }
        return "";
    }
}
