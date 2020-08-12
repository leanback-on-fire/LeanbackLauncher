package com.amazon.tv.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.apps.LaunchPoint;
import com.amazon.tv.leanbacklauncher.core.LaunchException;
import com.amazon.tv.leanbacklauncher.util.Util;

public class HomeScreenView extends ViewFlipper {
    private TextView mErrorMessageText;
    private boolean mHasNowPlayingCard;
    private HomeScreenMessaging mHomeScreenMessaging = new HomeScreenMessaging(this);
    private int mNotifRowViewIndex;
    // private final NowPlayCardListener mNowPlayCardListener;
    // private NowPlayingCardView mNowPlayingCardView;
    /*OnClickListener mOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (v != null && v == HomeScreenView.this.mNowPlayingCardView) {
                ((MainActivity) HomeScreenView.this.getContext()).beginLaunchAnimation(v, HomeScreenView.this.isTranslucentTheme(), HomeScreenView.this.mNowPlayingCardView.getLaunchAnimationColor(), new Runnable() {
                    public void run() {
                        try {
                            HomeScreenView.this.performLaunch();
                        } catch (LaunchException e) {
                            Log.e("HomeScreenView", "Could not perform launch:", e);
                            Toast.makeText(HomeScreenView.this.getContext(), R.string.failed_launch, 0).show();
                        }
                    }
                });
            }
        }
    };*/
    // private NowPlayingCardView mPreparingCard;
    private int mPreparingViewIndex;
    private NotificationRowView mRow;
    // private NowPlayingCardView mTimeoutCard;
    private int mTimeoutViewIndex;

    public HomeScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this.mNowPlayCardListener = new NowPlayCardListener(context);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mNotifRowViewIndex = indexOfChild(findViewById(R.id.list));
        this.mPreparingViewIndex = indexOfChild(findViewById(R.id.notification_preparing));
        this.mTimeoutViewIndex = indexOfChild(findViewById(R.id.notification_timeout));
        this.mErrorMessageText = findViewById(R.id.text_error_message);
        this.mRow = findViewById(R.id.list);
        //this.mTimeoutCard = (NowPlayingCardView) findViewById(R.id.now_playing_card_timeout);
        //this.mPreparingCard = (NowPlayingCardView) findViewById(R.id.now_playing_card_preparing);
        //this.mTimeoutCard.setOnClickListener(this.mOnClickListener);
        //this.mPreparingCard.setOnClickListener(this.mOnClickListener);
        this.mHomeScreenMessaging.resetToPreparing();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
      /*  try {
       //     this.mNowPlayCardListener.setRemoteControlListener(this);
       //     this.mNowPlayCardListener.forceUpdate();
        } catch (RemoteException e) {
            Log.e("HomeScreenView", "Exception", e);
        }*/
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
       /* try {
            this.mNowPlayCardListener.setRemoteControlListener(null);
        } catch (RemoteException e) {
            Log.e("HomeScreenView", "Exception", e);
        }*/
    }

    public HomeScreenMessaging getHomeScreenMessaging() {
        return this.mHomeScreenMessaging;
    }

    public NotificationRowView getNotificationRow() {
        return this.mRow;
    }

    public boolean isRowViewVisible() {
        return getDisplayedChild() == this.mNotifRowViewIndex;
    }

    public void flipToTimeout() {
        // if (this.mNowPlayingCardView != null) {
        //     this.mNowPlayingCardView.stopIconAnimation();
        // }
        // this.mNowPlayingCardView = this.mTimeoutCard;
        //this.mNowPlayCardListener.forceUpdate();
        setDisplayedChild(this.mTimeoutViewIndex);
    }

    public void flipToPreparing() {
        //  if (this.mNowPlayingCardView != null) {
        //      this.mNowPlayingCardView.stopIconAnimation();
        //  }
        //  this.mNowPlayingCardView = this.mPreparingCard;
        // this.mNowPlayCardListener.forceUpdate();
        //  setDisplayedChild(this.mPreparingViewIndex);
    }

    public void flipToNotifications() {
        // if (this.mNowPlayingCardView != null) {
        //  this.mNowPlayCardListener.forceUpdate();
        //      this.mNowPlayingCardView.stopIconAnimation();
        //  }
        setDisplayedChild(this.mNotifRowViewIndex);
    }

    public void flipToView(int view) {
        if (this.mHasNowPlayingCard) {
            //this.mNowPlayingCardView.setVisibility(8);
            this.mHasNowPlayingCard = false;
        }
        switch (view) {
            case 0:
                flipToNotifications();
                return;
            case 1:
                flipToPreparing();
                return;
            case 2:
                flipToTimeout();
                this.mErrorMessageText.setText(R.string.recommendation_row_empty_message_recs_disabled);
                return;
            case 3:
                flipToTimeout();
                this.mErrorMessageText.setText(R.string.recommendation_row_empty_message_no_recs);
                return;
            case 4:
                flipToTimeout();
                this.mErrorMessageText.setText(R.string.recommendation_row_empty_message_no_connection);
                return;
            default:
                return;
        }
    }

    public void onClientChanged(boolean clearing) {
        // if (this.mHasNowPlayingCard && clearing && this.mNowPlayingCardView != null) {
        // this.mNowPlayingCardView.stopSelfUpdate();
        // this.mNowPlayingCardView.setVisibility(8);
        //   this.mHasNowPlayingCard = false;
        // }
    }

    //public void onMediaDataUpdated(NowPlayingCardData mediaData) {
    //  if (!isRowViewVisible()) {
    //    this.mNowPlayingCardView.setNowPlayingContent(mediaData);
    //    if (!this.mHasNowPlayingCard) {
    //        this.mHasNowPlayingCard = true;
    //        this.mNowPlayingCardView.setVisibility(0);
    //    }
    // }
    //}

    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs) {
        //  if (this.mNowPlayingCardView != null && !isRowViewVisible()) {
        //      this.mNowPlayingCardView.setPlayerState(state, currentPosMs, stateChangeTimeMs);
        //  }
    }

    public PendingIntent getPendingIntent() {

        // return this.mNowPlayingCardView.getClickedIntent();
        return null;
    }

    protected void performLaunch() {
        PendingIntent intent = getPendingIntent();
        if (intent != null) {
            try {
                Util.startActivity(getContext(), intent);
            } catch (Throwable t) {
                LaunchException launchException = new LaunchException("Could not launch notification intent", t);
            }
        } else {
            throw new LaunchException("No pending intent");
        }
    }

    boolean isTranslucentTheme() {
        PendingIntent pendingIntent = getPendingIntent();
        if (pendingIntent == null) {
            return false;
        }
        String packageName = pendingIntent.getCreatorPackage();
        Intent intent = new Intent();
        intent.setPackage(packageName);
        intent.addCategory("android.intent.category.LAUNCHER");
        ResolveInfo info = getContext().getPackageManager().resolveActivity(intent, 0);
        if (info != null) {
            return LaunchPoint.isTranslucentTheme(getContext(), info);
        }
        return false;
    }
}
