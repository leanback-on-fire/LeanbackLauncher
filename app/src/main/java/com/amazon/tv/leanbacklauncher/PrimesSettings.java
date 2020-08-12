package com.amazon.tv.leanbacklauncher;

import android.content.ContentResolver;
import android.content.Context;

class PrimesSettings {
    private final ContentResolver mContentResolver;

    PrimesSettings(Context context) {
        this.mContentResolver = context.getApplicationContext().getContentResolver();
    }

    boolean isPrimesEnabled() {
        return getGservicesBoolean(this.mContentResolver, "leanbacklauncher:primes_enabled", false);
    }

    boolean isPackageStatsMetricEnabled() {
        return getGservicesBoolean(this.mContentResolver, "leanbacklauncher:primes_package_stats_metric_enabled", false);
    }

    boolean isMemoryMetricEnabled() {
        return getGservicesBoolean(this.mContentResolver, "leanbacklauncher:primes_memory_metric_enabled", false);
    }

    boolean isCrashMetricEnabled() {
        return getGservicesBoolean(this.mContentResolver, "leanbacklauncher:primes_crash_metric_enabled", false);
    }

    private boolean getGservicesBoolean(ContentResolver cr, String key, boolean defaultValue) {
        return false;
    }
}
