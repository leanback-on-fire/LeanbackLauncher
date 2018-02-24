package android.support.v7.app;

import android.app.Notification.Builder;
import android.app.Notification.DecoratedCustomViewStyle;
import android.app.Notification.DecoratedMediaCustomViewStyle;
import android.media.session.MediaSession.Token;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;

@RequiresApi(24)
class NotificationCompatImpl24
{
  public static void addDecoratedCustomViewStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
  {
    paramNotificationBuilderWithBuilderAccessor.getBuilder().setStyle(new Notification.DecoratedCustomViewStyle());
  }
  
  public static void addDecoratedMediaCustomViewStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, int[] paramArrayOfInt, Object paramObject)
  {
    paramNotificationBuilderWithBuilderAccessor = paramNotificationBuilderWithBuilderAccessor.getBuilder();
    Notification.DecoratedMediaCustomViewStyle localDecoratedMediaCustomViewStyle = new Notification.DecoratedMediaCustomViewStyle();
    if (paramArrayOfInt != null) {
      localDecoratedMediaCustomViewStyle.setShowActionsInCompactView(paramArrayOfInt);
    }
    if (paramObject != null) {
      localDecoratedMediaCustomViewStyle.setMediaSession((MediaSession.Token)paramObject);
    }
    paramNotificationBuilderWithBuilderAccessor.setStyle(localDecoratedMediaCustomViewStyle);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/app/NotificationCompatImpl24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */