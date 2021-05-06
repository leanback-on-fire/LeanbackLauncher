package com.amazon.tv.leanbacklauncher.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.amazon.tv.leanbacklauncher.R
import java.util.*

class MarketUpdateReceiver(private val mListener: InstallingLaunchPointListener) : BroadcastReceiver() {
    private val mInstallLaunchPoints: HashMap<String?, LaunchPoint?> = HashMap<String?, LaunchPoint?>()

    private fun isNonLeanbackApp(context: Context, pkgName: String): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES)
            pm.getLeanbackLaunchIntentForPackage(pkgName) == null
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val uri = intent.data
        if (uri == null) {
            Log.d("MarketUpdateReceiver", "Null URI")
            return
        }
        val pkgName = uri.schemeSpecificPart
        when {
            TextUtils.isEmpty(pkgName) -> {
                Log.d("MarketUpdateReceiver", "Missing package name")
            }
            isNonLeanbackApp(context, pkgName) -> {
                Log.d("MarketUpdateReceiver", "$pkgName is non-leanback App")
            }
            else -> {
                val action = intent.action
                var installLaunchPoint = mInstallLaunchPoints[pkgName]
                if (installLaunchPoint != null || intent.getBooleanExtra("user_initiated", false)) {
                    var wasAdded = false
                    if (installLaunchPoint == null) {
                        if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED" == action) {
                            Log.d("MarketUpdateReceiver", "Removing app $pkgName we don't actually know about, ignore")
                            return
                        }
                        wasAdded = true
                        installLaunchPoint = createInstallLaunchPoint(context, pkgName, intent)
                        mInstallLaunchPoints[pkgName] = installLaunchPoint
                    }
                    var success = false
                    if ("com.android.launcher.action.ACTION_PACKAGE_ENQUEUED" == action) {
                        installLaunchPoint.installProgressPercent = -1
                        var reason: String? = "install"
                        if (intent.hasExtra("reason")) {
                            reason = intent.getStringExtra("reason")
                        }
                        when (reason) {
                            "install" -> {
                                installLaunchPoint.setInstallStateStringResourceId(R.string.install_pending)
                            }
                            "update" -> {
                                installLaunchPoint.setInstallStateStringResourceId(R.string.update_pending)
                            }
                            "restore" -> {
                                installLaunchPoint.setInstallStateStringResourceId(R.string.restore_pending)
                            }
                            else -> {
                                installLaunchPoint.setInstallStateStringResourceId(R.string.unknown_state)
                            }
                        }
                        Log.d("MarketUpdateReceiver", "market has promised to " + reason + ": " + pkgName + " user initiated: " + intent.getBooleanExtra("user_initiated", false))
                    } else if ("com.android.launcher.action.ACTION_PACKAGE_DOWNLOADING" == action) {
                        val progress = intent.getIntExtra("progress", 0)
                        installLaunchPoint.installProgressPercent = progress
                        installLaunchPoint.setInstallStateStringResourceId(R.string.downloading)
                        Log.d("MarketUpdateReceiver", "market is downloading ($progress%): $pkgName")
                    } else if ("com.android.launcher.action.ACTION_PACKAGE_INSTALLING" == action) {
                        installLaunchPoint.installProgressPercent = -1
                        installLaunchPoint.setInstallStateStringResourceId(R.string.installing)
                        Log.d("MarketUpdateReceiver", "market is installing: $pkgName")
                    } else if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED" == action) {
                        mInstallLaunchPoints.remove(pkgName)
                        installLaunchPoint.setInstallStateStringResourceId(0)
                        installLaunchPoint.installProgressPercent = -1
                        success = intent.getBooleanExtra("com.android.launcher.action.INSTALL_COMPLETED", false)
                        if (success) {
                            Log.d("MarketUpdateReceiver", "market has installed: $pkgName")
                        } else {
                            Log.d("MarketUpdateReceiver", "market has decided not to install: $pkgName")
                        }
                    } else {
                        installLaunchPoint.installProgressPercent = -1
                        installLaunchPoint.setInstallStateStringResourceId(R.string.unknown_state)
                        Log.d("MarketUpdateReceiver", "unknown message $action")
                    }
                    when {
                        "com.android.launcher.action.ACTION_PACKAGE_DEQUEUED" == action -> {
                            mListener.onInstallingLaunchPointRemoved(installLaunchPoint, success)
                            return
                        }
                        wasAdded -> {
                            mListener.onInstallingLaunchPointAdded(installLaunchPoint)
                            return
                        }
                        else -> {
                            mListener.onInstallingLaunchPointChanged(installLaunchPoint)
                            return
                        }
                    }
                }
                if (intent.hasExtra("user_initiated")) {
                    Log.d("MarketUpdateReceiver", "EXTRA_USER_INITIATED was specified and false")
                } else {
                    Log.d("MarketUpdateReceiver", "EXTRA_USER_INITIATED was not specified")
                }
                Log.d("MarketUpdateReceiver", "Extras = " + intent.extras)
            }
        }
    }

    private fun createInstallLaunchPoint(context: Context, pkgName: String, intent: Intent): LaunchPoint {
        // todo category
        return LaunchPoint(context, intent.getStringExtra("app_name"), intent.getStringExtra("app_icon"), pkgName, intent.getParcelableExtra<Parcelable>("app_detailIntent") as Intent?, mListener)
    }

    companion object {
        @JvmStatic
        val intentFilter: IntentFilter
            get() {
                val filter = IntentFilter()
                filter.addAction("com.android.launcher.action.ACTION_PACKAGE_ENQUEUED")
                filter.addAction("com.android.launcher.action.ACTION_PACKAGE_DOWNLOADING")
                filter.addAction("com.android.launcher.action.ACTION_PACKAGE_INSTALLING")
                filter.addAction("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED")
                filter.addDataScheme("package")
                return filter
            }
        @JvmStatic
        val broadcastPermission: String
            get() = "android.permission.INSTALL_PACKAGES"
    }
}