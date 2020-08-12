package com.amazon.tv.leanbacklauncher.trace;

public final class AppTrace {

    static String TAG = "AppTrace";

    public interface TraceTag {
    }

    public static void beginSection(String section) {
        // if (BuildConfig.DEBUG) Log.d(TAG, section);
    }

    public static void endSection() {
        // if (BuildConfig.DEBUG) Log.d(TAG, "endSection");
    }

    public static TraceTag beginAsyncSection(String section) {
        return null;
    }

    public static void endAsyncSection(TraceTag tag) {
    }
}
