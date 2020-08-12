package com.amazon.tv.leanbacklauncher.notifications;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;

import com.amazon.tv.leanbacklauncher.ActiveItemsRowView;

import java.util.Objects;

public class NotificationRowView extends ActiveItemsRowView {
    private boolean mIgnoreActivateForBckChange;
    private String mLastReportedBackground;
    private NotificationRowListener mNotifListener;

    public interface NotificationRowListener {
        void onBackgroundImageChanged(String str, String str2);

        void onSelectedRecommendationChanged(int i);
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
        setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            public void onChildViewHolderSelected(RecyclerView parent, ViewHolder child, int position, int subposition) {
                NotificationRowView.this.updateLauncherBackground(0);
                NotificationRowView.this.mNotifListener.onSelectedRecommendationChanged(position);
            }
        });
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
        boolean oldActivated = isRowActive();
        super.setActivated(activated);
        if (oldActivated == activated) {
            return;
        }
        if (this.mIgnoreActivateForBckChange) {
            updateLauncherBackground(2);
            this.mIgnoreActivateForBckChange = false;
            return;
        }
        updateLauncherBackground(0);
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 0 && this.mLastReportedBackground == null) {
            updateLauncherBackground(1);
        }
    }

    public void requestChildFocus(View child, View focused) {
        if (!isRowActive() && (child instanceof NotificationCardView)) {
            ((NotificationCardView) child).setSelectedAnimationDelayed(false);
        }
        super.requestChildFocus(child, focused);
    }

    private void updateLauncherBackground(int type) {
        String backgroundUri = null;
        String signature = null;
        if (getVisibility() == View.VISIBLE || this.mNotifListener == null) {
            boolean rowActive = isRowActive();
            if (this.mNotifListener != null && ((rowActive || type == 1) && getAdapter() != null && getAdapter().getItemCount() > 0)) {
                ViewHolder holder = findViewHolderForLayoutPosition(getSelectedPosition());
                if (holder != null) {
                    View child = holder.itemView;
                    if (child instanceof RecommendationView) {
                        RecommendationView card = (RecommendationView) child;
                        backgroundUri = card.getWallpaperUri();
                        signature = card.getSignature();
                    }
                }
            }
            if (type == 2) {
                this.mLastReportedBackground = backgroundUri;
                return;
            } else if (type == 1 || !Objects.equals(this.mLastReportedBackground, backgroundUri)) {
                this.mLastReportedBackground = backgroundUri;
                if ((rowActive || this.mLastReportedBackground != null) && this.mNotifListener != null) {
                    this.mNotifListener.onBackgroundImageChanged(this.mLastReportedBackground, signature);
                    return;
                }
                return;
            } else if (this.mLastReportedBackground == null && backgroundUri == null && rowActive && this.mNotifListener != null) {
                this.mNotifListener.onBackgroundImageChanged(null, signature);
                return;
            } else {
                return;
            }
        }
        this.mLastReportedBackground = null;
        this.mNotifListener.onBackgroundImageChanged(null, null);
    }
}
