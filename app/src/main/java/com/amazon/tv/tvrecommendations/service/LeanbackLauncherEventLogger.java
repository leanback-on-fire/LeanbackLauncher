package com.amazon.tv.tvrecommendations.service;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public final class LeanbackLauncherEventLogger {
    private static boolean DEBUG = false;
    private static final long MAX_FLUSH_WAIT_TIME_USEC = TimeUnit.MILLISECONDS.toMicros(100);
    private static final String TAG = LeanbackLauncherEventLogger.class.getSimpleName();
    private static LeanbackLauncherEventLogger sInstance = null;
    // private GoogleApiClient mGoogleApiClient = null;

    public static synchronized LeanbackLauncherEventLogger getInstance(Context context) {
        LeanbackLauncherEventLogger leanbackLauncherEventLogger;
        synchronized (LeanbackLauncherEventLogger.class) {
            if (sInstance == null) {
                try {
                    sInstance = new LeanbackLauncherEventLogger(context.getApplicationContext());
                } catch (Throwable t) {
                    Log.e(TAG, "Exception creating LeanbackLauncherEventLogger", t);
                    leanbackLauncherEventLogger = null;
                }
            }
            leanbackLauncherEventLogger = sInstance;
        }
        return leanbackLauncherEventLogger;
    }

    private LeanbackLauncherEventLogger(Context context) {
        //   this.mEventLogger = new ClearcutLogger(context, 134, null, null);
    }

    public boolean flush() {
        boolean z = false;

        return z;
    }

    public void logEvent(String tag, Object leanbackEvent) {
//leanbackEvent.type
        if (DEBUG) {
            Log.d(TAG, "Event logged: " + leanbackEvent);
        }
    }
}
