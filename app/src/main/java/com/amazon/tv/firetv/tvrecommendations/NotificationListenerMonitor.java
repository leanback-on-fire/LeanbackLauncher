package com.amazon.tv.firetv.tvrecommendations;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.recommendations.NotificationsServiceV4;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.amazon.tv.leanbacklauncher.util.Util.isAmazonDev;

public class NotificationListenerMonitor extends Service {
    private static final String TAG = "NotifyListenerMonitor";
    private static final int MAXIMUM_RECONNECT_ATTEMPTS = 15;
    private int mReconnectAttempts = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Notification listener monitor created.");

        if (!listenerIsRunning()) {
            ensureNotificationPermissions(getApplicationContext());

            toggleNotificationListenerService();

            final Timer[] timer = new Timer[]{new Timer()};
            timer[0].schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!listenerIsRunning() && mReconnectAttempts < MAXIMUM_RECONNECT_ATTEMPTS) {
                        ensureNotificationPermissions(getApplicationContext());

                        toggleNotificationListenerService();

                        mReconnectAttempts++;
                    } else {
                        Log.d(TAG, "Exit Notification listener monitor. Max 15 attempts reached.");
                        timer[0].cancel();
                    }
                }
            }, 5000, 5000);
        }
    }

    private boolean listenerIsRunning() {
        ComponentName notificationListenerComp = new ComponentName(this, NotificationsServiceV4.class);
        Log.v(TAG, "Ensuring the notification listener is running: " + notificationListenerComp);

        boolean running = false;

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);

        if (services == null) {
            Log.w(TAG, "No running services found. Aborting listener monitoring.");
            return false;
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
            return true;
        }

        Log.d(TAG, "The listener has been killed... Attempting to start.");
        return false;
    }

    private void toggleNotificationListenerService() {
        Log.d(TAG, "Toggling notification listener...");
        ComponentName thisComponent = new ComponentName(this, NotificationsServiceV4.class);

        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void ensureNotificationPermissions(Context context) {
        Log.d(TAG, "Checking notify perms...");
        if (context.getPackageManager().checkPermission("android.permission.WRITE_SECURE_SETTINGS", context.getPackageName()) != PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Perms: granted");

            String listeners = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");

            String component = new ComponentName(context, NotificationsServiceV4.class).flattenToShortString();
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
                Log.d(TAG, "Perms: enabled");
            } else {
                Log.d(TAG, "Perms: (already) enabled");
            }
            // register notification listener
            Settings.Secure.putString(context.getContentResolver(), "enabled_notification_listeners", listeners);
        } else {
            Log.d(TAG, "Perms: denied");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String CHANNEL_ID = "com.amazon.tv.leanbacklauncher.recommendations.NotificationsServiceV4";
        String CHANNEL_NAME = "LeanbackOnFire";
        int NOTIFICATION_ID = 1111;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = null;
            notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(CHANNEL_NAME)
                .setContentText(getResources().getString(R.string.notification_text));
        if (isAmazonDev(this))
            builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_notification));
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
        startForeground(NOTIFICATION_ID, builder.build());

        return START_STICKY;
    }
}
