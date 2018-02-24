package com.bumptech.glide.request.target;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Preconditions;

public class NotificationTarget
  extends SimpleTarget<Bitmap>
{
  private final Context context;
  private final Notification notification;
  private final int notificationId;
  private final String notificationTag;
  private final RemoteViews remoteViews;
  private final int viewId;
  
  public NotificationTarget(Context paramContext, int paramInt1, int paramInt2, int paramInt3, RemoteViews paramRemoteViews, Notification paramNotification, int paramInt4, String paramString)
  {
    super(paramInt1, paramInt2);
    this.context = ((Context)Preconditions.checkNotNull(paramContext, "Context must not be null!"));
    this.notification = ((Notification)Preconditions.checkNotNull(paramNotification, "Notification object can not be null!"));
    this.remoteViews = ((RemoteViews)Preconditions.checkNotNull(paramRemoteViews, "RemoteViews object can not be null!"));
    this.viewId = paramInt3;
    this.notificationId = paramInt4;
    this.notificationTag = paramString;
  }
  
  public NotificationTarget(Context paramContext, int paramInt1, RemoteViews paramRemoteViews, Notification paramNotification, int paramInt2)
  {
    this(paramContext, paramInt1, paramRemoteViews, paramNotification, paramInt2, null);
  }
  
  public NotificationTarget(Context paramContext, int paramInt1, RemoteViews paramRemoteViews, Notification paramNotification, int paramInt2, String paramString)
  {
    this(paramContext, Integer.MIN_VALUE, Integer.MIN_VALUE, paramInt1, paramRemoteViews, paramNotification, paramInt2, paramString);
  }
  
  private void update()
  {
    ((NotificationManager)this.context.getSystemService("notification")).notify(this.notificationTag, this.notificationId, this.notification);
  }
  
  public void onResourceReady(Bitmap paramBitmap, Transition<? super Bitmap> paramTransition)
  {
    this.remoteViews.setImageViewBitmap(this.viewId, paramBitmap);
    update();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/NotificationTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */