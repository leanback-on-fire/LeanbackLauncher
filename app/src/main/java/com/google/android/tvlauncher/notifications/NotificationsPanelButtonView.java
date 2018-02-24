package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.tvlauncher.R;

public class NotificationsPanelButtonView extends FrameLayout {
    private int mAllNotifsSeenFocusedColor;
    private int mAllNotifsSeenUnfocusedColor;
    private Drawable mBackground;
    private ImageView mCircle;
    private String mNumberFormat;
    private boolean mSeen;
    private TextView mTextView;
    private int mUnseenNotifsColor;

    public NotificationsPanelButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = context.getResources();
        this.mAllNotifsSeenUnfocusedColor = ResourcesCompat.getColor(resources, R.color.white_80, null);
        this.mAllNotifsSeenFocusedColor = ResourcesCompat.getColor(resources, R.color.notification_panel_icon_seen_focused_color, null);
        this.mUnseenNotifsColor = ResourcesCompat.getColor(resources, R.color.notification_panel_icon_unseen_color, null);
        this.mNumberFormat = resources.getString(R.string.number_format);
        this.mBackground = resources.getDrawable(R.drawable.circle_background, null);
        this.mBackground.setTint(this.mAllNotifsSeenUnfocusedColor);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCircle = (ImageView) findViewById(R.id.notification_panel_background_circle);
        this.mCircle.setImageDrawable(this.mBackground);
        this.mTextView = (TextView) findViewById(R.id.notification_panel_count);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        });
        setClipToOutline(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean focused) {
                if (NotificationsPanelButtonView.this.mSeen) {
                    NotificationsPanelButtonView.this.mBackground.setTint(focused ? NotificationsPanelButtonView.this.mAllNotifsSeenFocusedColor : NotificationsPanelButtonView.this.mAllNotifsSeenUnfocusedColor);
                    NotificationsPanelButtonView.this.mTextView.setTextColor(focused ? NotificationsPanelButtonView.this.mAllNotifsSeenFocusedColor : NotificationsPanelButtonView.this.mAllNotifsSeenUnfocusedColor);
                }
            }
        });
    }

    public void setCount(int count) {
        this.mTextView.setText(String.format(this.mNumberFormat, new Object[]{Integer.valueOf(count)}));
        setContentDescription(getResources().getQuantityString(R.plurals.notification_panel_icon_accessibility_description, count, new Object[]{Integer.valueOf(count)}));
    }

    public void setSeenState(boolean seen) {
        this.mSeen = seen;
        if (this.mSeen) {
            this.mBackground.setTint(hasFocus() ? this.mAllNotifsSeenFocusedColor : this.mAllNotifsSeenUnfocusedColor);
            this.mTextView.setTextColor(hasFocus() ? this.mAllNotifsSeenFocusedColor : this.mAllNotifsSeenUnfocusedColor);
            return;
        }
        this.mBackground.setTint(this.mUnseenNotifsColor);
        this.mTextView.setTextColor(this.mUnseenNotifsColor);
    }
}
