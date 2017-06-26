package com.google.android.leanbacklauncher.notifications;

import android.content.Context;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.leanbacklauncher.ActiveItemsRowView;
import com.google.android.leanbacklauncher.animation.ViewDimmer;

public class NotificationRowView extends ActiveItemsRowView {
    private boolean mIgnoreActivateForBckChange;
    private String mLastReportedBackground;
    private NotificationRowListener mNotifListener;

    public interface NotificationRowListener {
        void onBackgroundImageChanged(String str, boolean z);

        void onSelectedRecommendationChanged(int i);
    }

    /* renamed from: com.google.android.leanbacklauncher.notifications.NotificationRowView.1 */
    class C01921 implements OnChildSelectedListener {
        C01921() {
        }

        public void onChildSelected(ViewGroup parent, View child, int position, long id) {
            //super.onChildSelected(parent, child, position, id);
            NotificationRowView.this.updateLauncherBackground(0);
            NotificationRowView.this.mNotifListener.onSelectedRecommendationChanged(position);
        }
    }

    public NotificationRowView(Context context) {
        this(context, null, 0);
    }

    public NotificationRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLastReportedBackground = null;
        this.mIgnoreActivateForBckChange = false;
        setOnChildSelectedListener(new C01921());
    }

    public void setListener(NotificationRowListener listener) {
        this.mNotifListener = listener;
    }

    public void refreshSelectedBackground() {
        updateLauncherBackground(1);
    }

    public void setIgnoreNextActivateBackgroundChange() {
        if (!isRowActive()) {
            this.mIgnoreActivateForBckChange = true;
        }
    }

    public void setActivated(boolean activated) {
        if (ViewDimmer.dimStateToActivated(this.mDimState) != activated) {
            if (this.mIgnoreActivateForBckChange) {
                updateLauncherBackground(2);
                this.mIgnoreActivateForBckChange = false;
            } else {
                updateLauncherBackground(0);
            }
        }
        super.setActivated(activated);
    }

    public void requestChildFocus(View child, View focused) {
        if (!isRowActive()) {
            ((NotificationCardView) child).setSelectedAnimationDelayed(false);
        }
        super.requestChildFocus(child, focused);
    }

    private void updateLauncherBackground(int type) {
        String backgroundUri = null;
        if (this.mNotifListener != null && ((isRowActive() || type == 1) && getChildCount() > 0)) {
            ViewHolder holder = findViewHolderForLayoutPosition(getSelectedPosition());
            if (holder != null) {
                View child = holder.itemView;
                if (child != null) {
                    NotificationCardView card = (NotificationCardView) child;
                    if (card != null) {
                        backgroundUri = card.getWallpaperUri();
                    }
                }
            }
        }
        if (type == 2) {
            this.mLastReportedBackground = backgroundUri;
        } else if (type == 1 || this.mLastReportedBackground != backgroundUri) {
            this.mLastReportedBackground = backgroundUri;
            this.mNotifListener.onBackgroundImageChanged(this.mLastReportedBackground, isRowActive());
        } else if (this.mLastReportedBackground == null && backgroundUri == null) {
            this.mNotifListener.onBackgroundImageChanged(this.mLastReportedBackground, isRowActive());
        }
    }
}
