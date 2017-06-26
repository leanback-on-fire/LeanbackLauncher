package com.google.android.leanbacklauncher.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.leanbacklauncher.data.ConstData;

/**
 * Created by GaoFei on 2017/6/6.
 */

public class NetWorkUtils {
    public static int getCurrentNetWrokState(){
        ConnectivityManager connectivityManager = (ConnectivityManager) ConstData.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
