package com.amazon.tv.leanbacklauncher.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ExternalAppsUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AppsManager.getInstance(context)?.refreshLaunchPointList()
    }

    companion object {
        @JvmStatic
        val intentFilter: IntentFilter
            get() {
                val filter = IntentFilter()
                filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE")
                filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")
                return filter
            }
    }
}