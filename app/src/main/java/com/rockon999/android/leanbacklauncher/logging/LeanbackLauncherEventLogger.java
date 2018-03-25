package com.rockon999.android.leanbacklauncher.logging;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/*
public final class LeanbackLauncherEventLogger {
    private static boolean DEBUG = false;
    private static final long MAX_FLUSH_WAIT_TIME_USEC = TimeUnit.MILLISECONDS.toMicros(100);
    private static final String TAG = LeanbackLauncherEventLogger.class.getSimpleName();
    private static LeanbackLauncherEventLogger sInstance = null;
    private final ClearcutLogger mEventLogger;
    private GoogleApiClient mGoogleApiClient = null;

    public static synchronized LeanbackLauncherEventLogger getInstance(Context context) {
        LeanbackLauncherEventLogger leanbackLauncherEventLogger;
        synchronized (LeanbackLauncherEventLogger.class) {
            if (sInstance == null) {
                sInstance = new LeanbackLauncherEventLogger(context.getApplicationContext());
            }
            leanbackLauncherEventLogger = sInstance;
        }
        return leanbackLauncherEventLogger;
    }

    private LeanbackLauncherEventLogger(Context context) {
        this.mEventLogger = new ClearcutLogger(context, 134, null, null);
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.mGoogleApiClient = googleApiClient;
    }

    public boolean flush() {
        boolean z = false;
        if (this.mGoogleApiClient == null) {
            Log.e(TAG, "GoogleApiClient hasn't been initialized.");
        } else if (this.mGoogleApiClient.isConnected()) {
            z = this.mEventLogger.flush(this.mGoogleApiClient, MAX_FLUSH_WAIT_TIME_USEC, TimeUnit.MICROSECONDS);
            if (DEBUG) {
                Log.d(TAG, "Event Logger flush result: " + z);
            }
        } else {
            Log.e(TAG, "GoogleApiClient is not connected.");
        }
        return z;
    }

    public void logEvent(String tag, LeanbackEvent leanbackEvent) {
        if (this.mGoogleApiClient == null) {
            Log.e(TAG, "GoogleApiClient hasn't been initialized.");
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "Event logged: " + leanbackEvent.type);
        }
        this.mEventLogger.newEvent(MessageNano.toByteArray(leanbackEvent)).logAsync(this.mGoogleApiClient);
    }
} */
public final class LeanbackLauncherEventLogger {
    private static boolean DEBUG;
    private static final long MAX_FLUSH_WAIT_TIME_USEC;
    private static final String TAG;
    private static LeanbackLauncherEventLogger sInstance;

    static {
        TAG = LeanbackLauncherEventLogger.class.getSimpleName();
        DEBUG = false;
        MAX_FLUSH_WAIT_TIME_USEC = TimeUnit.MILLISECONDS.toMicros(100);
        sInstance = null;
    }

    public static synchronized LeanbackLauncherEventLogger getInstance(Context context) {
        LeanbackLauncherEventLogger leanbackLauncherEventLogger;
        synchronized (LeanbackLauncherEventLogger.class) {
            if (sInstance == null) {
                try {
                    sInstance = new LeanbackLauncherEventLogger(context.getApplicationContext());
                } catch (Throwable t) {
                    Log.e(TAG, "Exception creating LeanbackLauncherEventLogger", t);
                    return null;
                }
            }
            leanbackLauncherEventLogger = sInstance;
        }
        return leanbackLauncherEventLogger;
    }

    private LeanbackLauncherEventLogger(Context context) {
    }

    public boolean flush() {
        return false;
    }

    public void logEvent(String tag, Object leanbackEvent) {

    }
}
