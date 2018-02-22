package com.rockon999.android.leanbacklauncher.apps.notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.apps.SettingsAdapter;

import java.util.Objects;


public class NotificationListenerV12 extends NotificationListenerService {
    private static final String TAG = "LeanbackOnFireNotify";

    public static final String LISTENER_INTENT_OUT = "NotificationCountUpdate";
    public static final String LISTENER_INTENT_IN = "NotificationCountUpdateRequest";

    public NotificationListenerV12() {
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

        BroadcastReceiver onNotice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendUpdateBroadcast(getNotificationCount());
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onNotice, new IntentFilter(NotificationListenerV12.LISTENER_INTENT_IN));
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
            int count = 0;

            for (StatusBarNotification notification : getActiveNotifications()) {
                //Notific
                if (!Objects.equals(notification.getNotification().category, Notification.CATEGORY_RECOMMENDATION)) {
                    count++;
                }
            }

            return count;
        } catch (SecurityException e) {
            Log.e(TAG, "Exception fetching notification count", e);
        }
        return -1;
    }

    private void sendUpdateBroadcast(int amount) {
        Log.d(TAG, "Sending broadcast to update notifications");
        Intent msgrcv = new Intent(LISTENER_INTENT_OUT);
        msgrcv.putExtra("count", amount);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);
    }

}
