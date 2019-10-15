package com.amazon.tv.leanbacklauncher.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ExternalAppsUpdateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        AppsManager.getInstance(context).refreshLaunchPointList();
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        return filter;
    }
}
