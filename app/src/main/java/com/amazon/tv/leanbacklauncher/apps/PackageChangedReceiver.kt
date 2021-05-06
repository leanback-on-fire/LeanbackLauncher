package com.amazon.tv.leanbacklauncher.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class PackageChangedReceiver(private val mListener: Listener) : BroadcastReceiver() {
    interface Listener {
        fun onPackageAdded(str: String?)
        fun onPackageChanged(str: String?)
        fun onPackageFullyRemoved(str: String?)
        fun onPackageRemoved(str: String?)
        fun onPackageReplaced(str: String?)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = getPackageName(intent)
        if (packageName != null && packageName.isNotEmpty()) {
            val action = intent.action
            if ("android.intent.action.PACKAGE_ADDED" == action) {
                mListener.onPackageAdded(packageName)
            } else if ("android.intent.action.PACKAGE_CHANGED" == action) {
                mListener.onPackageChanged(packageName)
            } else if ("android.intent.action.PACKAGE_FULLY_REMOVED" == action) {
                mListener.onPackageFullyRemoved(packageName)
            } else if ("android.intent.action.PACKAGE_REMOVED" == action) {
                if (!intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    mListener.onPackageRemoved(packageName)
                }
            } else if ("android.intent.action.PACKAGE_REPLACED" == action) {
                mListener.onPackageReplaced(packageName)
            }
        }
    }

    private fun getPackageName(intent: Intent): String? {
        val uri = intent.data
        return uri?.schemeSpecificPart
    }

    companion object {
        @JvmStatic
        val intentFilter: IntentFilter
            get() {
                val filter = IntentFilter()
                filter.addAction("android.intent.action.PACKAGE_ADDED")
                filter.addAction("android.intent.action.PACKAGE_CHANGED")
                filter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED")
                filter.addAction("android.intent.action.PACKAGE_REMOVED")
                filter.addAction("android.intent.action.PACKAGE_REPLACED")
                filter.addDataScheme("package")
                return filter
            }
    }
}