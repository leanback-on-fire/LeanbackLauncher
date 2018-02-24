package com.google.android.tvlauncher.notifications;

import android.net.Uri;

public final class NotificationsContract
{
  public static final String ACTION_NOTIFICATION_HIDE = "android.tvservice.action.NOTIFICATION_HIDE";
  public static final String ACTION_SHOW_UNSHOWN_NOTIFICATIONS = "android.tvservice.action.SHOW_UNSHOWN_NOTIFICATIONS";
  private static final String AUTHORITY = "com.google.android.tvrecommendations.NotificationContentProvider";
  public static final String COLUMN_AUTODISMISS = "is_auto_dismiss";
  public static final String COLUMN_BIG_PICTURE = "big_picture";
  public static final String COLUMN_CHANNEL = "channel";
  public static final String COLUMN_CONTENT_BUTTON_LABEL = "content_button_label";
  public static final String COLUMN_COUNT = "count";
  public static final String COLUMN_DISMISSIBLE = "dismissible";
  public static final String COLUMN_DISMISS_BUTTON_LABEL = "dismiss_button_label";
  public static final String COLUMN_FLAGS = "flags";
  public static final String COLUMN_HAS_CONTENT_INTENT = "has_content_intent";
  public static final String COLUMN_NOTIFICATION_HIDDEN = "notification_hidden";
  public static final String COLUMN_NOTIF_TEXT = "text";
  public static final String COLUMN_NOTIF_TITLE = "title";
  public static final String COLUMN_ONGOING = "ongoing";
  public static final String COLUMN_PACKAGE_NAME = "package_name";
  public static final String COLUMN_PROGRESS = "progress";
  public static final String COLUMN_PROGRESS_MAX = "progress_max";
  public static final String COLUMN_SBN_KEY = "sbn_key";
  public static final String COLUMN_SMALL_ICON = "small_icon";
  public static final String COLUMN_TAG = "tag";
  public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.tvrecommendations.NotificationContentProvider/notifications");
  public static final String NOTIFICATION_KEY = "sbn_key";
  public static final Uri NOTIF_COUNT_CONTENT_URI = Uri.parse("content://com.google.android.tvrecommendations.NotificationContentProvider/notifications/count");
  public static final String NOW_PLAYING_NOTIF_TAG = "Notification.NowPlaying";
  static final Uri PANEL_CONTENT_URI;
  public static final Uri PANEL_COUNT_CONTENT_URI;
  private static final String PATH_NOTIFICATIONS = "notifications";
  private static final String PATH_NOTIFS_COUNT = "notifications/count";
  private static final String PATH_PANEL_COUNT = "notifications/panel_count";
  private static final String PATH_PANEL_NOTIFS = "notifications/panel";
  private static final String PATH_TRAY_COUNT = "notifications/tray_count";
  private static final String PATH_TRAY_NOTIFS = "notifications/tray";
  public static final String PIP_NOTIF_TAG = "com.android.systemui.pip.tv.PipNotification";
  public static final Uri TRAY_CONTENT_URI = Uri.parse("content://com.google.android.tvrecommendations.NotificationContentProvider/notifications/tray");
  public static final Uri TRAY_COUNT_CONTENT_URI;
  
  static
  {
    PANEL_CONTENT_URI = Uri.parse("content://com.google.android.tvrecommendations.NotificationContentProvider/notifications/panel");
    PANEL_COUNT_CONTENT_URI = Uri.parse("content://com.google.android.tvrecommendations.NotificationContentProvider/notifications/panel_count");
    TRAY_COUNT_CONTENT_URI = Uri.parse("content://com.google.android.tvrecommendations.NotificationContentProvider/notifications/tray_count");
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsContract.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */