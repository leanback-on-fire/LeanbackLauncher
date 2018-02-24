package android.support.v7.app;

import android.app.Notification.BigTextStyle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;

@RequiresApi(16)
class NotificationCompatImplJellybean
{
  public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence)
  {
    new Notification.BigTextStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).bigText(paramCharSequence);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/app/NotificationCompatImplJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */