package com.google.android.leanbacklauncher.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.R.styleable;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.leanbacklauncher.R;

public class HomeScreenView extends ViewFlipper {
    private TextView mErrorMessageText;
    private final String mErrorNoConnection;
    private final String mErrorNoRecs;
    private final String mErrorRecsDisabled;
    private HomeScreenMessaging mHomeScreenMessaging;
    private NotificationRowView mRow;

    public void flipToView(int view) {
        switch (view) {
            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                super.setDisplayedChild(0);
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                super.setDisplayedChild(2);
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                super.setDisplayedChild(3);
                this.mErrorMessageText.setText(this.mErrorRecsDisabled);
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                super.setDisplayedChild(3);
                this.mErrorMessageText.setText(this.mErrorNoRecs);
            case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                super.setDisplayedChild(3);
                this.mErrorMessageText.setText(this.mErrorNoConnection);
            default:
        }
    }

    public HomeScreenMessaging getHomeScreenMessaging() {
        return this.mHomeScreenMessaging;
    }

    public HomeScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        this.mErrorRecsDisabled = res.getString(R.string.recommendation_row_empty_message_recs_disabled);
        this.mErrorNoRecs = res.getString(R.string.recommendation_row_empty_message_no_recs);
        this.mErrorNoConnection = res.getString(R.string.recommendation_row_empty_message_no_connection);
        this.mHomeScreenMessaging = new HomeScreenMessaging(this);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mErrorMessageText = (TextView) findViewById(R.id.text_error_message);
        this.mRow = (NotificationRowView) getChildAt(0);
    }

    public NotificationRowView getNotificationRow() {
        return this.mRow;
    }

    public boolean isRowViewVisible() {
        return getDisplayedChild() == 0;
    }
}
