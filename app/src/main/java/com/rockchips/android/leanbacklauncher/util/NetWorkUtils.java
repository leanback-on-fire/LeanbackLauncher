package com.rockchips.android.leanbacklauncher.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.rockchips.android.leanbacklauncher.data.ConstData;

import momo.cn.edu.fjnu.androidutils.data.CommonValues;

/**
 * Created by GaoFei on 2017/6/6.
 */

public class NetWorkUtils {
    public static int getCurrentNetWrokState(){
        ConnectivityManager connectivityManager = (ConnectivityManager) CommonValues.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null || connectivityManager.getActiveNetworkInfo() == null)
            return ConstData.NetWorkState.NO;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        int networkType = networkInfo.getType();
        if(networkType == ConnectivityManager.TYPE_WIFI)
            return  ConstData.NetWorkState.WIFI;
        else if(networkType == ConnectivityManager.TYPE_ETHERNET)
            return ConstData.NetWorkState.ETHERNET;
        return ConstData.NetWorkState.NO;
    }
    public static int getWifiStrength(){
        int strength = 0;
        WifiManager wifiManager = (WifiManager) CommonValues.application.getSystemService(Context.WIFI_SERVICE); // 取得WifiManager对象
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
        }
        return strength;
    }

    public static String getWifiSSID(){
        WifiManager wifiManager = (WifiManager) CommonValues.application.getSystemService(Context.WIFI_SERVICE); // 取得WifiManager对象
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            return  info.getSSID();
        }
        return "";
    }
}
