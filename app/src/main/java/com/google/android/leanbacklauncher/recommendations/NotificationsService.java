package com.google.android.leanbacklauncher.recommendations;

import android.service.notification.StatusBarNotification;
import com.google.android.leanbacklauncher.recommendations.GservicesRankerParameters.Factory;
import com.google.android.tvrecommendations.service.BaseNotificationsService;

public class NotificationsService extends BaseNotificationsService {
    private NotificationServiceDelegate mDelegate;

    public interface NotificationServiceDelegate {
        void onFetchExistingNotifications(StatusBarNotification[] statusBarNotificationArr);

        void onNotificationPosted(StatusBarNotification statusBarNotification);

        void onNotificationRemoved(StatusBarNotification statusBarNotification);
    }

    public NotificationsService() {
        super(false, new Factory());
    }

    public void onListenerConnected() {
        super.onListenerConnected();
    }

    protected void onFetchExistingNotifications(StatusBarNotification[] notifications) {
        if (isEnabled()) {
            super.onFetchExistingNotifications(notifications);
            if (this.mDelegate != null) {
                this.mDelegate.onFetchExistingNotifications(notifications);
            }
        }
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        if (isEnabled()) {
            super.onNotificationPosted(sbn);
            if (this.mDelegate != null) {
                this.mDelegate.onNotificationPosted(sbn);
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (isEnabled()) {
            super.onNotificationRemoved(sbn);
            if (this.mDelegate != null) {
                this.mDelegate.onNotificationRemoved(sbn);
            }
        }
    }

    private boolean isEnabled() {
        return true;
    }
}
