package com.rockon999.android.leanbacklauncher.apps.notifications;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class NotificationListenerMonitor extends Service {
    private static final String TAG = "NotifyListenerMonitor";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Notification listener monitor created.");

        ensureNotificationPermissions(getApplicationContext());

        ensureListenerIsRunning();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void ensureListenerIsRunning() {
        ComponentName notificationListenerComp = new ComponentName(this, NotificationListenerV12.class);
        Log.v(TAG, "Ensuring the notification listener is running: " + notificationListenerComp);

        boolean running = false;

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);

        if (services == null) {
            Log.w(TAG, "No running services found. Aborting listener monitoring.");
            return;
        }

        for (ActivityManager.RunningServiceInfo service : services) {
            if (notificationListenerComp.equals(service.service)) {
                Log.w(TAG, "Ensuring Notification Listener { PID: " + service.pid + " | currentPID: " + Process.myPid() + " | clientPackage: " + service.clientPackage + " | clientCount: " + service.clientCount
                        + " | clientLabel: " + ((service.clientLabel == 0) ? "0" : "(" + getResources().getString(service.clientLabel) + ")}"));

                if (service.pid == Process.myPid()) {
                    running = true;
                }
            }
        }

        if (running) {
            Log.d(TAG, "Listener is running!");
            return;
        }

        Log.d(TAG, "The listener has been killed... Attempting to start.");
        toggleNotificationListenerService();
    }

    private void toggleNotificationListenerService() {
        Log.d(TAG, "Toggling notification listener...");
        ComponentName thisComponent = new ComponentName(this, NotificationListenerV12.class);

        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    private void ensureNotificationPermissions(Context context) {
        Log.d(TAG, "Checking notify perms");
        if (context.getPackageManager().checkPermission("android.permission.WRITE_SECURE_SETTINGS", context.getPackageName()) != PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Perms granted");

            String listeners = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
            String component = new ComponentName(context, NotificationListenerV12.class).flattenToShortString();
            String[] list = listeners == null ? new String[0] : listeners.split("\\s*:\\s*");
            boolean enabled = false;
            for (CharSequence equals : list) {
                if (TextUtils.equals(equals, component)) {
                    enabled = true;
                    break;
                }
            }
            if (!enabled) {
                if (listeners == null || listeners.length() == 0) {
                    listeners = component;
                } else {
                    listeners = listeners + ":" + component;
                }
                Settings.Secure.putString(context.getContentResolver(), "enabled_notification_listeners", listeners);
                Log.d(TAG, "Perms enabled");
            } else {
                Log.d(TAG, "Perms enabled (2)");
            }

        } else {
            Log.d(TAG, "Perms denied");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}