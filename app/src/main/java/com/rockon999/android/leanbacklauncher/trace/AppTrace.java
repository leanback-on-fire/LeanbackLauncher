package com.rockon999.android.leanbacklauncher.trace;

import android.util.Log;

public final class AppTrace {

    static String TAG = "AppTrace";

    public interface TraceTag {
    }

    public static void beginSection(String section) {
        Log.w(TAG, section);
    }

    public static void endSection() {
    }

    public static TraceTag beginAsyncSection(String section) {
        return null;
    }

    public static void endAsyncSection(TraceTag tag) {
    }
}
