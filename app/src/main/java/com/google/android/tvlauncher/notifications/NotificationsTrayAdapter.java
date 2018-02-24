package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEventParameters;
import com.google.android.tvlauncher.analytics.LogEvents;

public class NotificationsTrayAdapter<VH extends ViewHolder> extends Adapter<NotificationsTrayAdapter.NotificationsTrayViewHolder> {
    private static final boolean DEBUG = false;
    public static final String TAG = "NotifsTrayAdapter";
    private final Context mContext;
    private Cursor mCursor;
    private final EventLogger mEventLogger;

    public static class NotificationsTrayViewHolder extends ViewHolder {
        public NotificationsTrayViewHolder(View itemView) {
            super(itemView);
        }

        public void setNotification(TvNotification notification, EventLogger eventLogger) {
            ((NotificationsTrayItemView) this.itemView).setNotification(notification, eventLogger);
        }
    }

    public NotificationsTrayAdapter(Context context, EventLogger eventLogger, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        this.mEventLogger = eventLogger;
    }

    public NotificationsTrayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationsTrayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_tray_item, parent, false));
    }

    public void onBindViewHolder(NotificationsTrayViewHolder holder, int position) {
        if (this.mCursor.moveToPosition(position)) {
            holder.setNotification(TvNotification.fromCursor(this.mCursor), this.mEventLogger);
            return;
        }
        throw new IllegalStateException("Index out of bounds for cursor: " + position);
    }

    public int getItemCount() {
        if (this.mCursor != null) {
            return this.mCursor.getCount();
        }
        return 0;
    }

    public void changeCursor(Cursor newCursor) {
        this.mCursor = newCursor;
        notifyDataSetChanged();
        this.mEventLogger.log(new LogEventParameters(LogEvents.OPEN_HOME).putParameter(LogEvents.PARAMETER_TRAY_NOTIFICATION_COUNT, this.mCursor != null ? this.mCursor.getCount() : 0));
    }
}
