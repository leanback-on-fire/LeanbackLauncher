package android.support.v7.app;

import android.app.Notification.MediaStyle;
import android.media.session.MediaSession.Token;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;

@RequiresApi(21)
class NotificationCompatImpl21
{
  public static void addMediaStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, int[] paramArrayOfInt, Object paramObject)
  {
    paramNotificationBuilderWithBuilderAccessor = new Notification.MediaStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder());
    if (paramArrayOfInt != null) {
      paramNotificationBuilderWithBuilderAccessor.setShowActionsInCompactView(paramArrayOfInt);
    }
    if (paramObject != null) {
      paramNotificationBuilderWithBuilderAccessor.setMediaSession((MediaSession.Token)paramObject);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/app/NotificationCompatImpl21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */