package com.amazon.tv.leanbacklauncher.notifications;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

public class HomeScreenMessaging {
    static final int VIEW_DISABLED = 2;
    static final int VIEW_NO_CONNECTION = 4;
    static final int VIEW_VISIBLE = 0;
    private boolean mConnected = true;
    private final HomeScreenView mHomeScreenView;
    private ChangeListener mListener;
    private int mNextViewState = -1;
    private boolean mRecommendationsVisible = false;
    private int mTimeoutViewState = 3;
    private TimerHandler mTimer = new TimerHandler(this);
    private int mViewState = -1;
    private long mWhenNextViewVisible = 0;

    public interface ChangeListener {
        void onStateChanged(int i);
    }

    private static class TimerHandler extends Handler {
        private final WeakReference<HomeScreenMessaging> mParent;

        TimerHandler(HomeScreenMessaging parent) {
            this.mParent = new WeakReference(parent);
        }

        public void handleMessage(Message msg) {
            HomeScreenMessaging homeScreenMessaging = this.mParent.get();
            if (homeScreenMessaging != null) {
                switch (msg.what) {
                    case 0:
                        homeScreenMessaging.minimumViewVisibleTimerTriggered();
                        return;
                    case 1:
                        homeScreenMessaging.preparingTimeoutTriggered();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public HomeScreenMessaging(HomeScreenView homeScreenView) {
        this.mHomeScreenView = homeScreenView;
    }

    public void setListener(ChangeListener listener) {
        this.mListener = listener;
    }

    private void applyViewState(int viewState) {
        int showView;
        int newState;
        switch (viewState) {
            case 0:
                showView = 0;
                break;
            case 1:
                showView = 1;
                break;
            case 2:
                showView = 2;
                break;
            case 3:
                showView = 3;
                break;
            case 4:
                showView = 4;
                break;
            default:
                showView = -1;
                break;
        }
        this.mHomeScreenView.flipToView(showView);
        this.mViewState = viewState;
        switch (viewState) {
            case 0:
                newState = 0;
                break;
            case 1:
                newState = 2;
                break;
            case 2:
            case 3:
            case 4:
                newState = 1;
                break;
            default:
                newState = -1;
                break;
        }
        if (this.mListener != null) {
            this.mListener.onStateChanged(newState);
        }
    }

    private void minimumViewVisibleTimerTriggered() {
        if (this.mNextViewState != -1) {
            applyViewState(this.mNextViewState);
            this.mNextViewState = -1;
            this.mWhenNextViewVisible = SystemClock.elapsedRealtime() + 1000;
        }
    }

    private void preparingTimeoutTriggered() {
        applyViewState(this.mTimeoutViewState);
        this.mNextViewState = -1;
        this.mWhenNextViewVisible = SystemClock.elapsedRealtime() + 1000;
    }

    void setViewState(int newViewState) {
        stopPreparingTimeout();
        long now = SystemClock.elapsedRealtime();
        int resolvedViewState = newViewState;
        if ((newViewState == 2 || newViewState == 3) && !this.mConnected) {
            resolvedViewState = 4;
        }
        if (now >= this.mWhenNextViewVisible || (this.mViewState == 0 && resolvedViewState != 0)) {
            this.mNextViewState = -1;
            this.mTimer.removeMessages(0);
            applyViewState(resolvedViewState);
            this.mWhenNextViewVisible = now + 1000;
            return;
        }
        if (this.mNextViewState == -1) {
            this.mTimer.sendEmptyMessageDelayed(0, 1000);
        }
        this.mNextViewState = resolvedViewState;
    }

    void setViewStateForTesting(int viewState) {
        this.mViewState = viewState;
    }

    int getViewState() {
        return this.mViewState;
    }

    private void startPreparingTimeout(int timeoutViewState) {
        stopPreparingTimeout();
        this.mTimeoutViewState = timeoutViewState;
        this.mTimer.sendEmptyMessageDelayed(1, 30000);
    }

    private void stopPreparingTimeout() {
        this.mTimer.removeMessages(1);
    }

    public void recommendationsUpdated(boolean hasRecommendations) {
        if (this.mRecommendationsVisible != hasRecommendations) {
            this.mRecommendationsVisible = hasRecommendations;
            if (hasRecommendations) {
                setViewState(0);
            } else if (this.mViewState != 1) {
                setViewState(1);
                startPreparingTimeout(3);
            }
        }
    }

    public void onClearRecommendations(int reason) {
        switch (reason) {
            case 2:
                this.mRecommendationsVisible = false;
                setViewState(2);
                return;
            case 4:
                if (this.mViewState != 1) {
                    resetToPreparing(2);
                    return;
                }
                return;
            default:
                if (this.mViewState != 1) {
                    resetToPreparing(3);
                    return;
                }
                return;
        }
    }

    private void resetToPreparing(int timeoutViewState) {
        this.mRecommendationsVisible = false;
        setViewState(1);
        startPreparingTimeout(timeoutViewState);
    }

    public void resetToPreparing() {
        resetToPreparing(3);
    }

    public void onConnectivityChange(boolean connected) {
        if (connected != this.mConnected) {
            this.mConnected = connected;
            if (this.mConnected) {
                if (this.mViewState == 4) {
                    setViewState(1);
                    startPreparingTimeout(this.mTimeoutViewState);
                }
            } else if (this.mViewState == 1 || this.mViewState == 2 || this.mViewState == 3) {
                setViewState(4);
            }
        }
    }
}
