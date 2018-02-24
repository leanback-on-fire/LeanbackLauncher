package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;

public class NotificationsPanelAdapter<VH extends ViewHolder> extends Adapter<NotificationsPanelAdapter.NotificationPanelViewHolder> implements EventLogger {
    private static final boolean DEBUG = false;
    private static final String TAG = "NotifsPanelAdapter";
    private static final int TYPE_DISMISSIBLE = 0;
    private static final int TYPE_PERSISTENT = 1;
    private Cursor mCursor;
    private final EventLogger mEventLogger;

    public static class NotificationPanelViewHolder extends ViewHolder {
        public NotificationPanelViewHolder(View itemView) {
            super(itemView);
        }

        public void setNotification(TvNotification notification, EventLogger eventLogger) {
            ((NotificationPanelItemView) this.itemView).setNotification(notification, eventLogger);
        }
    }

    public NotificationsPanelAdapter(Context context, EventLogger eventLogger, Cursor cursor) {
        this.mCursor = cursor;
        this.mEventLogger = eventLogger;
        setHasStableIds(true);
    }

    public Cursor getCursor() {
        return this.mCursor;
    }

    public int getItemCount() {
        if (this.mCursor != null) {
            return this.mCursor.getCount();
        }
        return 0;
    }

    public NotificationPanelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View trayItem;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            trayItem = inflater.inflate(R.layout.notification_panel_item_dismissible, parent, false);
        } else {
            trayItem = inflater.inflate(R.layout.notification_panel_item, parent, false);
        }
        return new NotificationPanelViewHolder(trayItem);
    }

    public void onBindViewHolder(NotificationPanelViewHolder holder, int position) {
        if (this.mCursor.moveToPosition(position)) {
            onBindViewHolder(holder, this.mCursor);
            return;
        }
        throw new IllegalStateException("Can't move cursor to position " + position);
    }

    public int getItemViewType(int position) {
        if (this.mCursor.moveToPosition(position)) {
            boolean ongoing;
            boolean dismissible;
            if (this.mCursor.getInt(4) != 0) {
                dismissible = true;
            } else {
                dismissible = false;
            }
            if (this.mCursor.getInt(5) != 0) {
                ongoing = true;
            } else {
                ongoing = false;
            }
            if (ongoing || !dismissible) {
                return 1;
            }
            return 0;
        }
        throw new IllegalStateException("Can't move cursor to position " + position);
    }

    public long getItemId(int position) {
        if (this.mCursor.moveToPosition(position)) {
            return (long) this.mCursor.getString(0).hashCode();
        }
        Log.wtf(TAG, "Can't move cursor to position " + position);
        return -1;
    }

    public void onBindViewHolder(NotificationPanelViewHolder viewHolder, Cursor cursor) {
        viewHolder.setNotification(TvNotification.fromCursor(cursor), this);
    }

    public void log(LogEvent event) {
        event.putParameter("count", getItemCount());
        this.mEventLogger.log(event);
    }

    public void changeCursor(Cursor newCursor) {
        this.mCursor = newCursor;
        notifyDataSetChanged();
    }
}
