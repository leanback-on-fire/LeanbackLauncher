package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.google.android.tvlauncher.R;

public class NotificationsTrayView extends FrameLayout {
    private HorizontalGridView mNotificationsRow;

    public NotificationsTrayView(Context context) {
        super(context);
    }

    public NotificationsTrayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mNotificationsRow = (HorizontalGridView) findViewById(R.id.notifications_row);
        this.mNotificationsRow.setWindowAlignment(0);
        this.mNotificationsRow.setWindowAlignmentOffsetPercent(0.0f);
        this.mNotificationsRow.setWindowAlignmentOffset(getContext().getResources().getDimensionPixelSize(R.dimen.notifications_list_padding_start));
        this.mNotificationsRow.setItemAlignmentOffsetPercent(0.0f);
    }

    public void updateVisibility() {
        int i = (this.mNotificationsRow.getAdapter() == null || this.mNotificationsRow.getAdapter().getItemCount() <= 0) ? 8 : 0;
        setVisibility(i);
    }

    public void setTrayAdapter(NotificationsTrayAdapter adapter) {
        this.mNotificationsRow.setAdapter(adapter);
        updateVisibility();
    }

    public NotificationsTrayAdapter getTrayAdapter() {
        return this.mNotificationsRow != null ? (NotificationsTrayAdapter) this.mNotificationsRow.getAdapter() : null;
    }
}
