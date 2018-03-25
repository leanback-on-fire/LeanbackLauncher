package com.rockon999.android.leanbacklauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MediaButtonBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.i("LeanbackLauncher", "Received media button event in LeanbackLauncher, ignoring.");
        abortBroadcast();
    }
}
