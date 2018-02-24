package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;

public class NotificationsPanelAdapter<VH extends RecyclerView.ViewHolder>
  extends RecyclerView.Adapter<NotificationPanelViewHolder>
  implements EventLogger
{
  private static final boolean DEBUG = false;
  private static final String TAG = "NotifsPanelAdapter";
  private static final int TYPE_DISMISSIBLE = 0;
  private static final int TYPE_PERSISTENT = 1;
  private Cursor mCursor;
  private final EventLogger mEventLogger;
  
  public NotificationsPanelAdapter(Context paramContext, EventLogger paramEventLogger, Cursor paramCursor)
  {
    this.mCursor = paramCursor;
    this.mEventLogger = paramEventLogger;
    setHasStableIds(true);
  }
  
  public void changeCursor(Cursor paramCursor)
  {
    this.mCursor = paramCursor;
    notifyDataSetChanged();
  }
  
  public Cursor getCursor()
  {
    return this.mCursor;
  }
  
  public int getItemCount()
  {
    if (this.mCursor != null) {
      return this.mCursor.getCount();
    }
    return 0;
  }
  
  public long getItemId(int paramInt)
  {
    if (!this.mCursor.moveToPosition(paramInt))
    {
      Log.wtf("NotifsPanelAdapter", "Can't move cursor to position " + paramInt);
      return -1L;
    }
    return this.mCursor.getString(0).hashCode();
  }
  
  public int getItemViewType(int paramInt)
  {
    int j = 0;
    if (!this.mCursor.moveToPosition(paramInt)) {
      throw new IllegalStateException("Can't move cursor to position " + paramInt);
    }
    if (this.mCursor.getInt(4) != 0)
    {
      paramInt = 1;
      if (this.mCursor.getInt(5) == 0) {
        break label91;
      }
    }
    label91:
    for (int i = 1;; i = 0)
    {
      if (i == 0)
      {
        i = j;
        if (paramInt != 0) {}
      }
      else
      {
        i = 1;
      }
      return i;
      paramInt = 0;
      break;
    }
  }
  
  public void log(LogEvent paramLogEvent)
  {
    paramLogEvent.putParameter("count", getItemCount());
    this.mEventLogger.log(paramLogEvent);
  }
  
  public void onBindViewHolder(NotificationPanelViewHolder paramNotificationPanelViewHolder, int paramInt)
  {
    if (!this.mCursor.moveToPosition(paramInt)) {
      throw new IllegalStateException("Can't move cursor to position " + paramInt);
    }
    onBindViewHolder(paramNotificationPanelViewHolder, this.mCursor);
  }
  
  public void onBindViewHolder(NotificationPanelViewHolder paramNotificationPanelViewHolder, Cursor paramCursor)
  {
    paramNotificationPanelViewHolder.setNotification(TvNotification.fromCursor(paramCursor), this);
  }
  
  public NotificationPanelViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    LayoutInflater localLayoutInflater = LayoutInflater.from(paramViewGroup.getContext());
    if (paramInt == 0) {}
    for (paramViewGroup = localLayoutInflater.inflate(2130968700, paramViewGroup, false);; paramViewGroup = localLayoutInflater.inflate(2130968699, paramViewGroup, false)) {
      return new NotificationPanelViewHolder(paramViewGroup);
    }
  }
  
  public static class NotificationPanelViewHolder
    extends RecyclerView.ViewHolder
  {
    public NotificationPanelViewHolder(View paramView)
    {
      super();
    }
    
    public void setNotification(TvNotification paramTvNotification, EventLogger paramEventLogger)
    {
      ((NotificationPanelItemView)this.itemView).setNotification(paramTvNotification, paramEventLogger);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsPanelAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */