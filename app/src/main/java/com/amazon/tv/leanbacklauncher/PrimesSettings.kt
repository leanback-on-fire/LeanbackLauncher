package com.amazon.tv.leanbacklauncher

import android.content.ContentResolver
import android.content.Context

internal class PrimesSettings(context: Context) {

    private val mContentResolver: ContentResolver

    val isPrimesEnabled: Boolean
        get() = getGservicesBoolean(mContentResolver, "leanbacklauncher:primes_enabled", false)

    val isPackageStatsMetricEnabled: Boolean
        get() = getGservicesBoolean(mContentResolver, "leanbacklauncher:primes_package_stats_metric_enabled", false)

    val isMemoryMetricEnabled: Boolean
        get() = getGservicesBoolean(mContentResolver, "leanbacklauncher:primes_memory_metric_enabled", false)

    val isCrashMetricEnabled: Boolean
        get() = getGservicesBoolean(mContentResolver, "leanbacklauncher:primes_crash_metric_enabled", false)

    private fun getGservicesBoolean(cr: ContentResolver, key: String, defaultValue: Boolean): Boolean {
        return false
    }

    init {
        mContentResolver = context.applicationContext.contentResolver
    }
}