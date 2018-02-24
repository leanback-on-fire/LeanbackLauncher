package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;

public class NotificationsTrayItemView extends LinearLayout {
    private ImageView mBigPicture;
    private Button mDismissButton;
    private EventLogger mEventLogger;
    private ImageView mIcon;
    private String mNotificationKey;
    private View mNowPlayingIndicator;
    private Button mSeeMoreButton;
    private TextView mText;
    private TextView mTitle;

    public NotificationsTrayItemView(Context context) {
        super(context);
    }

    public NotificationsTrayItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationsTrayItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBigPicture = (ImageView) findViewById(R.id.big_picture);
        this.mIcon = (ImageView) findViewById(R.id.small_icon);
        this.mTitle = (TextView) findViewById(R.id.notif_title);
        this.mText = (TextView) findViewById(R.id.notif_text);
        this.mDismissButton = (Button) findViewById(R.id.tray_dismiss);
        this.mSeeMoreButton = (Button) findViewById(R.id.tray_see_more);
        this.mNowPlayingIndicator = findViewById(R.id.now_playing_bars);
    }

    public void setNotification(TvNotification notif, EventLogger eventLogger) {
        this.mNotificationKey = notif.getNotificationKey();
        this.mEventLogger = eventLogger;
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (am == null || !am.isEnabled()) {
            setFocusable(false);
        } else {
            setFocusable(true);
        }
        if (NotificationsContract.NOW_PLAYING_NOTIF_TAG.equals(notif.getTag()) && notif.getBigPicture() != null) {
            this.mBigPicture.setImageBitmap(notif.getBigPicture());
            this.mBigPicture.setVisibility(0);
            this.mNowPlayingIndicator.setVisibility(0);
            this.mIcon.setVisibility(8);
        } else if (NotificationsContract.PIP_NOTIF_TAG.equals(notif.getTag())) {
            this.mNowPlayingIndicator.setVisibility(8);
            Icon icon = notif.getSmallIcon();
            // icon.setTint(ResourcesCompat.getColor(getResources(), R.color.notification_icon_tint, null));
            //    this.mIcon.setImageBitmap(icon.);
            this.mIcon.setVisibility(0);
            this.mBigPicture.setVisibility(8);
        } else {
            this.mNowPlayingIndicator.setVisibility(8);
            this.mBigPicture.setVisibility(8);
            this.mIcon.setVisibility(8);
        }
        this.mTitle.setText(notif.getTitle());
        if (TextUtils.isEmpty(notif.getText())) {
            this.mText.setVisibility(8);
        } else {
            this.mText.setText(notif.getText());
            this.mText.setVisibility(0);
        }
        if (TextUtils.isEmpty(notif.getTitle())) {
            setContentDescription(notif.getText());
        } else if (TextUtils.isEmpty(notif.getText())) {
            setContentDescription(notif.getTitle());
        } else {
            setContentDescription(String.format(getResources().getString(R.string.notification_content_description_format), new Object[]{notif.getTitle(), notif.getText()}));
        }
        if (notif.hasContentIntent()) {
            this.mSeeMoreButton.setVisibility(0);
            this.mSeeMoreButton.setText(notif.getContentButtonLabel());
            this.mSeeMoreButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NotificationsTrayItemView.this.mEventLogger.log(new UserActionEvent(LogEvents.OPEN_NOTIFICATION).putParameter("placement", LogEvents.NOTIFICATION_PLACEMENT_TRAY).putParameter("key", NotificationsTrayItemView.this.mNotificationKey));
                    NotificationsUtils.openNotification(NotificationsTrayItemView.this.getContext(), NotificationsTrayItemView.this.mNotificationKey);
                }
            });
        } else {
            this.mSeeMoreButton.setVisibility(8);
        }
        this.mDismissButton.setText(notif.getDismissButtonLabel());
        if (!notif.isDismissible() || notif.isOngoing()) {
            this.mDismissButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NotificationsTrayItemView.this.mEventLogger.log(new UserActionEvent(LogEvents.HIDE_NOTIFICATION).putParameter("key", NotificationsTrayItemView.this.mNotificationKey));
                    NotificationsUtils.hideNotification(view.getContext(), NotificationsTrayItemView.this.mNotificationKey);
                }
            });
        } else {
            this.mDismissButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NotificationsTrayItemView.this.mEventLogger.log(new UserActionEvent(LogEvents.DISMISS_NOTIFICATION).putParameter("placement", LogEvents.NOTIFICATION_PLACEMENT_TRAY).putParameter("key", NotificationsTrayItemView.this.mNotificationKey));
                    NotificationsUtils.dismissNotification(view.getContext(), NotificationsTrayItemView.this.mNotificationKey);
                }
            });
        }
    }
}
