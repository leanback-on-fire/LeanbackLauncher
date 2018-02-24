package com.google.android.tvlauncher.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;

public class NotificationPanelDismissibleItemView extends NotificationPanelItemView {
    private View mDismissButton;
    private TextView mDismissText;
    private int mDismissTranslationX;
    private int mViewFocusTranslationX;

    public NotificationPanelDismissibleItemView(Context context) {
        super(context);
    }

    public NotificationPanelDismissibleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDismissButton = findViewById(R.id.dismiss_button);
        this.mDismissText = (TextView) findViewById(R.id.dismiss_text);
        this.mViewFocusTranslationX = getResources().getDimensionPixelSize(R.dimen.notification_panel_item_show_button_translate_x);
        this.mDismissTranslationX = getResources().getDimensionPixelSize(R.dimen.notification_panel_item_dismiss_translate_x);
        Configuration config = getResources().getConfiguration();
        updateBackgrounds(config.getLayoutDirection());
        if (config.getLayoutDirection() == 1) {
            this.mViewFocusTranslationX *= -1;
            this.mDismissTranslationX *= -1;
        }
        final AnimatorSet dismissAnimator = new AnimatorSet();
        ObjectAnimator containerSlide = ObjectAnimator.ofFloat(this.mMainContentText, View.TRANSLATION_X, new float[]{(float) this.mViewFocusTranslationX, (float) this.mDismissTranslationX});
        ObjectAnimator dismissButtonSlide = ObjectAnimator.ofFloat(this.mDismissButton, View.TRANSLATION_X, new float[]{(float) this.mViewFocusTranslationX, (float) this.mDismissTranslationX});
        dismissAnimator.playTogether(new Animator[]{dismissButtonSlide, containerSlide});
        dismissAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                NotificationPanelDismissibleItemView.this.collapseText();
                NotificationPanelDismissibleItemView.this.mDismissText.setVisibility(8);
            }

            public void onAnimationEnd(Animator animation) {
                NotificationPanelDismissibleItemView.this.mDismissButton.setVisibility(4);
                NotificationPanelDismissibleItemView.this.mMainContentText.setVisibility(4);
                NotificationPanelDismissibleItemView.this.setBackgroundColor(ContextCompat.getColor(NotificationPanelDismissibleItemView.this.getContext(), R.color.notification_selection_color));
                NotificationPanelDismissibleItemView.this.mEventLogger.log(new UserActionEvent(LogEvents.DISMISS_NOTIFICATION).putParameter("placement", LogEvents.NOTIFICATION_PLACEMENT_PANEL).putParameter("key", NotificationPanelDismissibleItemView.this.mNotificationKey));
                NotificationsUtils.dismissNotification(NotificationPanelDismissibleItemView.this.getContext(), NotificationPanelDismissibleItemView.this.mNotificationKey);
            }
        });
        final AnimatorSet gainFocus = new AnimatorSet();
        ObjectAnimator containerSlideOut = ObjectAnimator.ofFloat(this.mMainContentText, View.TRANSLATION_X, new float[]{0.0f, (float) this.mViewFocusTranslationX});
        ObjectAnimator dismissButtonFocusGain = ObjectAnimator.ofFloat(this.mDismissButton, View.TRANSLATION_X, new float[]{0.0f, (float) this.mViewFocusTranslationX});
        gainFocus.playTogether(new Animator[]{dismissButtonFocusGain, containerSlideOut});
        final AnimatorSet loseFocus = new AnimatorSet();
        ObjectAnimator containerSlideIn = ObjectAnimator.ofFloat(this.mMainContentText, View.TRANSLATION_X, new float[]{(float) this.mViewFocusTranslationX, 0.0f});
        ObjectAnimator dismissButtonFocusLost = ObjectAnimator.ofFloat(this.mDismissButton, View.TRANSLATION_X, new float[]{(float) this.mViewFocusTranslationX, 0.0f});
        loseFocus.playTogether(new Animator[]{dismissButtonFocusLost, containerSlideIn});
        loseFocus.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                NotificationPanelDismissibleItemView.this.mDismissText.setVisibility(8);
            }
        });
        this.mDismissButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (NotificationPanelDismissibleItemView.this.mNotificationKey != null) {
                    dismissAnimator.start();
                }
            }
        });
        this.mDismissButton.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    NotificationPanelDismissibleItemView.this.mDismissText.setVisibility(0);
                    gainFocus.start();
                    return;
                }
                loseFocus.start();
            }
        });
    }

    public void setNotification(TvNotification notif, EventLogger eventLogger) {
        super.setNotification(notif, eventLogger);
        this.mDismissText.setText(notif.getDismissButtonLabel());
        this.mDismissButton.setVisibility(0);
    }

    private void updateBackgrounds(int layoutDirection) {
        if (layoutDirection == 1) {
            this.mMainContentText.setBackgroundResource(R.drawable.notification_background_left);
            this.mDismissButton.setBackgroundResource(R.drawable.notification_background_right);
            return;
        }
        this.mMainContentText.setBackgroundResource(R.drawable.notification_background_right);
        this.mDismissButton.setBackgroundResource(R.drawable.notification_background_left);
    }
}
