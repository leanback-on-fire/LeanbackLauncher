package com.google.android.tvlauncher.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ExtendableTimer {
    private static final boolean DEBUG = false;
    private static final int MAXIMUM_TIMEOUT_MSG = 2;
    private static final int MAX_POOL_SIZE = 10;
    private static final Object POOL_SYNC = new Object();
    private static final String TAG = "ExtendableTimer";
    private static final int TIMEOUT_MSG = 1;
    private static ExtendableTimer sPool;
    private static int sPoolSize = 0;
    private final InternalHandler mHandler = new InternalHandler();
    private long mId;
    private Listener mListener;
    private long mMaximumTimeoutMillis;
    private ExtendableTimer mNext;
    private boolean mStarted;
    private long mTimeoutMillis;

    @SuppressLint({"HandlerLeak"})
    private class InternalHandler extends Handler {
        InternalHandler() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message msg) {
            ExtendableTimer.this.fireTimer();
        }
    }

    public interface Listener {
        void onTimerFired(ExtendableTimer extendableTimer);
    }

    public static ExtendableTimer obtain() {
        synchronized (POOL_SYNC) {
            if (sPool != null) {
                ExtendableTimer t = sPool;
                sPool = t.mNext;
                t.mNext = null;
                sPoolSize--;
                return t;
            }
            return new ExtendableTimer();
        }
    }

    public void recycle() {
        synchronized (POOL_SYNC) {
            if (sPoolSize < 10 && sPool != this && this.mNext == null) {
                stopTimers();
                resetFields();
                this.mNext = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    private void resetFields() {
        this.mTimeoutMillis = 0;
        this.mMaximumTimeoutMillis = 0;
        this.mListener = null;
        this.mStarted = false;
    }

    public void start() {
        checkTimeouts();
        if (this.mStarted) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, this.mTimeoutMillis);
            return;
        }
        this.mStarted = true;
        this.mHandler.sendEmptyMessageDelayed(1, this.mTimeoutMillis);
        this.mHandler.sendEmptyMessageDelayed(2, this.mMaximumTimeoutMillis);
    }

    public void cancel() {
        stopTimers();
    }

    private void checkTimeouts() {
        if (this.mTimeoutMillis <= 0 || this.mMaximumTimeoutMillis <= 0) {
            throw new IllegalArgumentException("Both timeout and maximum timeout must be provided");
        } else if (this.mMaximumTimeoutMillis <= this.mTimeoutMillis) {
            throw new IllegalArgumentException("Maximum timeout must be larger than timeout");
        }
    }

    private void stopTimers() {
        if (this.mStarted) {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mStarted = false;
        }
    }

    private void fireTimer() {
        stopTimers();
        if (this.mListener != null) {
            this.mListener.onTimerFired(this);
        }
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setTimeout(long timeoutMillis) {
        this.mTimeoutMillis = timeoutMillis;
    }

    public void setMaximumTimeout(long timeoutMillis) {
        this.mMaximumTimeoutMillis = timeoutMillis;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public boolean isStarted() {
        return this.mStarted;
    }
}
