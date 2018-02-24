package com.google.android.leanbacklauncher.trace;

public final class AppTrace {

    public interface TraceTag {
    }

    public static void beginSection(String section) {
    }

    public static void endSection() {
    }

    public static TraceTag beginAsyncSection(String section) {
        return null;
    }

    public static void endAsyncSection(TraceTag tag) {
    }
}
