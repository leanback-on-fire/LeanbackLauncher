package com.rockchips.android.leanbacklauncher.notifications;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class HomeScreenMessaging {
    private boolean mConnected;
    private HomeScreenView mHomeScreenView;
    private ChangeListener mListener;
    private int mNextViewState;
    private boolean mRecommendationsVisible;
    private TimerHandler mTimer;
    private int mViewState;
    private long mWhenNextViewVisible;

    public interface ChangeListener {
        void onStateChanged(int i);
    }

    private static class TimerHandler extends Handler {
        private final WeakReference<HomeScreenMessaging> mParent;

        public TimerHandler(HomeScreenMessaging parent) {
            this.mParent = new WeakReference(parent);
        }

        public void handleMessage(Message msg) {
            HomeScreenMessaging homeScreenMessaging = (HomeScreenMessaging) this.mParent.get();
            if (homeScreenMessaging != null) {
                switch (msg.what) {
                    case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                        homeScreenMessaging.minimumViewVisibleTimerTriggered();
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                        homeScreenMessaging.viewSwitchTimerTriggered(msg.arg1);
                    default:
                }
            }
        }
    }

    private void selectView(int viewState) {
        int showView;
        int newState;
        switch (viewState) {
            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                showView = 0;
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                showView = 1;
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                showView = 2;
                break;
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                showView = 3;
                break;
            case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                showView = 4;
                break;
            default:
                showView = -1;
                break;
        }
        this.mHomeScreenView.flipToView(showView);
        this.mViewState = viewState;
        switch (viewState) {
            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                newState = 0;
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                newState = 2;
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
            case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
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
            selectView(this.mNextViewState);
            this.mNextViewState = -1;
            this.mWhenNextViewVisible = System.currentTimeMillis() + 1000;
        }
    }

    private void viewSwitchTimerTriggered(int viewState) {
        selectView(viewState);
        this.mWhenNextViewVisible = System.currentTimeMillis() + 1000;
    }

    public HomeScreenMessaging(HomeScreenView homeScreenView) {
        this.mRecommendationsVisible = false;
        this.mConnected = false;
        this.mViewState = -1;
        this.mNextViewState = -1;
        this.mWhenNextViewVisible = 0;
        this.mTimer = new TimerHandler(this);
        this.mHomeScreenView = homeScreenView;
    }

    public void setListener(ChangeListener listener) {
        this.mListener = listener;
    }

    private void setViewState(int viewState) {
        this.mTimer.removeMessages(1);
        long now = System.currentTimeMillis();
        if (now >= this.mWhenNextViewVisible) {
            selectView(viewState);
            this.mWhenNextViewVisible = now + 1000;
        } else if (viewState == 0 || this.mViewState != 0) {
            if (this.mNextViewState == -1) {
                this.mTimer.sendEmptyMessageDelayed(0, 1000);
            }
            this.mNextViewState = viewState;
        } else {
            this.mTimer.removeMessages(0);
            selectView(viewState);
            this.mNextViewState = -1;
            this.mWhenNextViewVisible = now + 1000;
        }
    }

    private void startTimer(int seconds, int viewState) {
        this.mTimer.sendMessageDelayed(this.mTimer.obtainMessage(1, viewState, 0), (long) (seconds * 1000));
    }

    private void stopTimer() {
        this.mTimer.removeMessages(1);
    }

    public void recommendationsUpdated(boolean hasRecommendations) {
        if (this.mRecommendationsVisible != hasRecommendations) {
            this.mRecommendationsVisible = hasRecommendations;
            if (hasRecommendations) {
                stopTimer();
                setViewState(0);
            } else if (this.mViewState != 1) {
                setViewState(1);
                startTimer(30, 3);
            }
        }
    }

    public void onClearRecommendations(int reason) {
        switch (reason) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                if (this.mViewState != 1) {
                    this.mRecommendationsVisible = false;
                    setViewState(1);
                    startTimer(30, 3);
                }
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                this.mRecommendationsVisible = false;
                stopTimer();
                setViewState(2);
            default:
        }
    }

    public void onConnectivityChange(boolean connected) {
        if (connected != this.mConnected) {
            this.mConnected = connected;
            if (this.mConnected) {
                if (this.mViewState == 4) {
                    setViewState(1);
                    startTimer(30, 3);
                }
            } else if (this.mViewState == 1) {
                setViewState(4);
            }
        }
    }
}
