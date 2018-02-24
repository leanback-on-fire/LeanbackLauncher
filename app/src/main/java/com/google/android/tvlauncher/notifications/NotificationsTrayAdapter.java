package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEventParameters;

public class NotificationsTrayAdapter<VH extends RecyclerView.ViewHolder>
  extends RecyclerView.Adapter<NotificationsTrayViewHolder>
{
  private static final boolean DEBUG = false;
  public static final String TAG = "NotifsTrayAdapter";
  private final Context mContext;
  private Cursor mCursor;
  private final EventLogger mEventLogger;
  
  public NotificationsTrayAdapter(Context paramContext, EventLogger paramEventLogger, Cursor paramCursor)
  {
    this.mContext = paramContext;
    this.mCursor = paramCursor;
    this.mEventLogger = paramEventLogger;
  }
  
  public void changeCursor(Cursor paramCursor)
  {
    this.mCursor = paramCursor;
    notifyDataSetChanged();
    paramCursor = this.mEventLogger;
    LogEventParameters localLogEventParameters = new LogEventParameters("open_home");
    if (this.mCursor != null) {}
    for (int i = this.mCursor.getCount();; i = 0)
    {
      paramCursor.log(localLogEventParameters.putParameter("tray_notification_count", i));
      return;
    }
  }
  
  public int getItemCount()
  {
    if (this.mCursor != null) {
      return this.mCursor.getCount();
    }
    return 0;
  }
  
  public void onBindViewHolder(NotificationsTrayViewHolder paramNotificationsTrayViewHolder, int paramInt)
  {
    if (!this.mCursor.moveToPosition(paramInt)) {
      throw new IllegalStateException("Index out of bounds for cursor: " + paramInt);
    }
    paramNotificationsTrayViewHolder.setNotification(TvNotification.fromCursor(this.mCursor), this.mEventLogger);
  }
  
  public NotificationsTrayViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new NotificationsTrayViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968714, paramViewGroup, false));
  }
  
  public static class NotificationsTrayViewHolder
    extends RecyclerView.ViewHolder
  {
    public NotificationsTrayViewHolder(View paramView)
    {
      super();
    }
    
    public void setNotification(TvNotification paramTvNotification, EventLogger paramEventLogger)
    {
      ((NotificationsTrayItemView)this.itemView).setNotification(paramTvNotification, paramEventLogger);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsTrayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */