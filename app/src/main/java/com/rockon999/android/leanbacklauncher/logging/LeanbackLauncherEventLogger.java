package com.rockon999.android.leanbacklauncher.logging;

import android.content.Context;
import android.util.Log;
//import com.rockon999.android.leanbacklauncher.gms.clearcut.ClearcutLogger;
//import com.rockon999.android.leanbacklauncher.gms.common.api.GoogleApiClient;
//import com.google.protobuf.nano.MessageNano;
import java.util.concurrent.TimeUnit;

public final class LeanbackLauncherEventLogger {
    private static boolean DEBUG;
    private static final long MAX_FLUSH_WAIT_TIME_USEC;
    private static final String TAG;
    private static LeanbackLauncherEventLogger sInstance;
    //private final ClearcutLogger mEventLogger;
    //private GoogleApiClient mGoogleApiClient;

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
        //this.mGoogleApiClient = null;
        //this.mEventLogger = new ClearcutLogger(context, 134, null, null);
    }

    public void setGoogleApiClient(Object googleApiClient) {
        //this.mGoogleApiClient = googleApiClient;
    }

    public boolean flush() {
       return false;
    }

    public void logEvent(String tag, LeanbackProto$LeanbackEvent leanbackEvent) {

    }
}
