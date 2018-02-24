package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.Intent;

public class NotificationsUtils
{
  static void dismissNotification(Context paramContext, String paramString)
  {
    Intent localIntent = new Intent("android.intent.action.DELETE");
    localIntent.setPackage("com.google.android.tvrecommendations");
    localIntent.putExtra("sbn_key", paramString);
    paramContext.sendBroadcast(localIntent);
  }
  
  static void hideNotification(Context paramContext, String paramString)
  {
    Intent localIntent = new Intent("android.tvservice.action.NOTIFICATION_HIDE");
    localIntent.setPackage("com.google.android.tvrecommendations");
    localIntent.putExtra("sbn_key", paramString);
    paramContext.sendBroadcast(localIntent);
  }
  
  static void openNotification(Context paramContext, String paramString)
  {
    Intent localIntent = new Intent("android.intent.action.VIEW");
    localIntent.setPackage("com.google.android.tvrecommendations");
    localIntent.putExtra("sbn_key", paramString);
    paramContext.sendBroadcast(localIntent);
  }
  
  public static void showUnshownNotifications(Context paramContext)
  {
    Intent localIntent = new Intent("android.tvservice.action.SHOW_UNSHOWN_NOTIFICATIONS");
    localIntent.setPackage("com.google.android.tvrecommendations");
    paramContext.sendBroadcast(localIntent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */