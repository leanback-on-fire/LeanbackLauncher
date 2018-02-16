package com.rockon999.android.leanbacklauncher.apps.notifications;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class NotificationListenerV5 extends NotificationListenerService {
    private static final String TAG = "LeanbackOnFireNotify";

    public NotificationListenerV5() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind...");
        return super.onBind(intent);
    }


    public void onListenerConnected() {
        Log.d(TAG, "listener connected");

        int amount = getNotificationCount();

        sendUpdateBroadcast(amount);
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        int amount = getNotificationCount();

        sendUpdateBroadcast(amount);
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        int amount = getNotificationCount();

        sendUpdateBroadcast(amount);
    }

    private int getNotificationCount() {
        try {
            return getActiveNotifications(new String[]{String.valueOf(1)}).length;
        } catch (SecurityException e) {
            Log.e(TAG, "Exception fetching notification count", e);
        }
        return -1;
    }

    private void sendUpdateBroadcast(int amount) {
        Log.d(TAG, "Sending broadcast to update notifications");
        Intent msgrcv = new Intent("NotificationCountUpdate");
        msgrcv.putExtra("count", amount);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);
    }

}
