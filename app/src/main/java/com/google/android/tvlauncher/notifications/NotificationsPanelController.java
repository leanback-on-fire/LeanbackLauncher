package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogEventParameters;

public class NotificationsPanelController
{
  public static final String NOTIFS_SEEN = "notifs_seen";
  public static final String NOTIF_PANEL_SEEN_STATE = "notif_panel_seen_state";
  private Context mContext;
  private final EventLogger mEventLogger;
  private int mNotifCount = 0;
  private NotificationsPanelButtonView mPanelButtonView;
  private boolean mSeen;
  
  public NotificationsPanelController(Context paramContext, EventLogger paramEventLogger)
  {
    this.mEventLogger = paramEventLogger;
    this.mContext = paramContext.getApplicationContext();
    this.mSeen = this.mContext.getSharedPreferences("notif_panel_seen_state", 0).getBoolean("notifs_seen", true);
  }
  
  private void storeSeenState()
  {
    this.mContext.getSharedPreferences("notif_panel_seen_state", 0).edit().putBoolean("notifs_seen", this.mSeen).apply();
  }
  
  public void setView(NotificationsPanelButtonView paramNotificationsPanelButtonView)
  {
    if (paramNotificationsPanelButtonView != null)
    {
      this.mPanelButtonView = paramNotificationsPanelButtonView;
      this.mPanelButtonView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView.getContext().startActivity(new Intent(paramAnonymousView.getContext(), NotificationsSidePanelActivity.class));
          NotificationsPanelController.this.mPanelButtonView.setSeenState(true);
        }
      });
      updateView();
    }
  }
  
  public void updateNotificationsCount(Cursor paramCursor)
  {
    boolean bool2 = true;
    LogEvent localLogEvent;
    if ((paramCursor != null) && (paramCursor.moveToFirst()))
    {
      paramCursor.moveToFirst();
      int i = this.mNotifCount;
      this.mNotifCount = paramCursor.getInt(paramCursor.getColumnIndex("count"));
      boolean bool3 = this.mSeen;
      if (i < this.mNotifCount) {
        break label134;
      }
      bool1 = true;
      this.mSeen = bool1;
      if (bool3 != this.mSeen) {
        storeSeenState();
      }
      paramCursor = this.mEventLogger;
      localLogEvent = new LogEventParameters("open_home").putParameter("notification_indicator_total", this.mNotifCount);
      if (this.mSeen) {
        break label139;
      }
    }
    label134:
    label139:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramCursor.log(localLogEvent.putParameter("notification_indicator_new", bool1));
      updateView();
      return;
      bool1 = false;
      break;
    }
  }
  
  public void updateView()
  {
    if (this.mPanelButtonView != null)
    {
      if (this.mNotifCount == 0) {
        this.mPanelButtonView.setVisibility(8);
      }
    }
    else {
      return;
    }
    this.mPanelButtonView.setVisibility(0);
    this.mPanelButtonView.setCount(this.mNotifCount);
    this.mPanelButtonView.setSeenState(this.mSeen);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsPanelController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */