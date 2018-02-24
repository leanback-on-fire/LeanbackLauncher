package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogEventParameters;
import com.google.android.tvlauncher.analytics.LogEvents;

public class NotificationsPanelController {
    public static final String NOTIFS_SEEN = "notifs_seen";
    public static final String NOTIF_PANEL_SEEN_STATE = "notif_panel_seen_state";
    private Context mContext;
    private final EventLogger mEventLogger;
    private int mNotifCount = 0;
    private NotificationsPanelButtonView mPanelButtonView;
    private boolean mSeen;

    public NotificationsPanelController(Context context, EventLogger eventLogger) {
        this.mEventLogger = eventLogger;
        this.mContext = context.getApplicationContext();
        this.mSeen = this.mContext.getSharedPreferences(NOTIF_PANEL_SEEN_STATE, 0).getBoolean(NOTIFS_SEEN, true);
    }

    public void setView(NotificationsPanelButtonView view) {
        if (view != null) {
            this.mPanelButtonView = view;
            this.mPanelButtonView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), NotificationsSidePanelActivity.class));
                    NotificationsPanelController.this.mPanelButtonView.setSeenState(true);
                }
            });
            updateView();
        }
    }

    public void updateNotificationsCount(Cursor data) {
        boolean z = true;
        if (data != null && data.moveToFirst()) {
            boolean z2;
            data.moveToFirst();
            int oldCount = this.mNotifCount;
            this.mNotifCount = data.getInt(data.getColumnIndex("count"));
            boolean oldSeenState = this.mSeen;
            if (oldCount >= this.mNotifCount) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.mSeen = z2;
            if (oldSeenState != this.mSeen) {
                storeSeenState();
            }
            EventLogger eventLogger = this.mEventLogger;
            LogEvent putParameter = new LogEventParameters(LogEvents.OPEN_HOME).putParameter(LogEvents.PARAMETER_NOTIFICATION_INDICATOR_TOTAL, this.mNotifCount);
            String str = LogEvents.PARAMETER_NOTIFICATION_INDICATOR_NEW;
            if (this.mSeen) {
                z = false;
            }
            eventLogger.log(putParameter.putParameter(str, z));
        }
        updateView();
    }

    public void updateView() {
        if (this.mPanelButtonView == null) {
            return;
        }
        if (this.mNotifCount == 0) {
            this.mPanelButtonView.setVisibility(8);
            return;
        }
        this.mPanelButtonView.setVisibility(0);
        this.mPanelButtonView.setCount(this.mNotifCount);
        this.mPanelButtonView.setSeenState(this.mSeen);
    }

    private void storeSeenState() {
        this.mContext.getSharedPreferences(NOTIF_PANEL_SEEN_STATE, 0).edit().putBoolean(NOTIFS_SEEN, this.mSeen).apply();
    }
}
