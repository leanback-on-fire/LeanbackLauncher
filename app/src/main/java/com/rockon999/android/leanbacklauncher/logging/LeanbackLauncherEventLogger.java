package com.rockon999.android.leanbacklauncher.logging;

import android.content.Context;
import android.util.Log;
import java.util.concurrent.TimeUnit;

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

    public void logEvent(String tag, LeanbackProto$LeanbackEvent leanbackEvent) {

    }
}
