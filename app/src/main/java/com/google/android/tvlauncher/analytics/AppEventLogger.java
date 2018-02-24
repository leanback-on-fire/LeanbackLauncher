package com.google.android.tvlauncher.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppEventLogger implements EventLogger {
    private static final long DEFAULT_PENDING_PARAMETERS_TIMEOUT = 5000;
    private static final int MSG_FLUSH_PENDING_EVENT = 1;
    private static final String TAG = "FA-AppEventLogger";
    private static AppEventLogger sInstance;
    private final EventLoggerEngine mEngine;
    private List<String> mExpectedParameters;
    private Handler mHandler;
    private String mPendingEventName;
    private Bundle mPendingEventParameters;

    @MainThread
    public static void init(Context context, EventLoggerEngine engine) {
        sInstance = new AppEventLogger(engine);
        checkOptedInForUsageReporting(context);
    }

    @MainThread
    public static AppEventLogger getInstance() {
        return sInstance;
    }

    @VisibleForTesting
    AppEventLogger(EventLoggerEngine engine) {
        this.mEngine = engine;
    }

    private void setUsageReportingOptedIn(boolean optedIn) {
        this.mEngine.setEnabled(optedIn);
    }

    void setName(Activity activity, String name) {
        this.mEngine.setCurrentScreen(activity, null, name);
    }

    public void log(LogEvent event) {
        if (!this.mEngine.isEnabled()) {
            return;
        }
        if (event instanceof LogEventParameters) {
            mergePendingParameters(event);
            return;
        }
        flushPendingEvent();
        String[] expectedParameters = event.getExpectedParameters();
        if (expectedParameters == null || expectedParameters.length == 0) {
            this.mEngine.logEvent(event.getName(), getParameters(event));
            return;
        }
        this.mPendingEventName = event.getName();
        this.mPendingEventParameters = getParameters(event);
        this.mExpectedParameters = Arrays.asList(expectedParameters);
        long timeout = event.getParameterTimeout();
        if (timeout == 0) {
            timeout = 5000;
        }
        if (this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        AppEventLogger.this.flushPendingEvent();
                    }
                }
            };
        }
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, timeout);
    }

    private void mergePendingParameters(LogEvent event) {
        if (this.mExpectedParameters == null) {
            Log.e(TAG, "Unexpected log event parameters " + event);
        } else if (TextUtils.equals(this.mPendingEventName, event.getName())) {
            Bundle parameters = getParameters(event);
            if (parameters != null) {
                Set<String> unexpectedParameters = new HashSet(parameters.keySet());
                unexpectedParameters.removeAll(this.mExpectedParameters);
                if (unexpectedParameters.isEmpty()) {
                    if (this.mPendingEventParameters == null) {
                        this.mPendingEventParameters = new Bundle();
                    }
                    this.mPendingEventParameters.putAll(parameters);
                    if (this.mPendingEventParameters.keySet().containsAll(this.mExpectedParameters)) {
                        flushPendingEvent();
                        return;
                    }
                    return;
                }
                throw new IllegalArgumentException("Unexpected log event parameters: " + unexpectedParameters);
            }
        } else {
            Log.e(TAG, "Parameters for a previous event " + event + ", expected " + this.mPendingEventName);
        }
    }

    private void flushPendingEvent() {
        if (this.mExpectedParameters != null) {
            this.mEngine.logEvent(this.mPendingEventName, this.mPendingEventParameters);
            this.mPendingEventName = null;
            this.mPendingEventParameters = null;
            this.mExpectedParameters = null;
        }
    }

    @Nullable
    private Bundle getParameters(LogEvent event) {
        Bundle parameters = event.getParameters();
        Bundle restricted = event.getRestrictedParameters();
        if (parameters != null && restricted != null) {
            Bundle bundle = new Bundle();
            bundle.putAll(parameters);
            bundle.putAll(restricted);
            return bundle;
        } else if (parameters != null) {
            return parameters;
        } else {
            if (restricted != null) {
                return restricted;
            }
            return null;
        }
    }

    private static void checkOptedInForUsageReporting(Context context) {
    }
}
