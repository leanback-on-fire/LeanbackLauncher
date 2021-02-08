package com.amazon.tv.leanbacklauncher.apps

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory
import com.amazon.tv.leanbacklauncher.R
import java.lang.ref.WeakReference
import java.util.*

class SettingsAdapter(context: Context?, private val mConnectivityListener: ConnectivityListener) : AppsAdapter(context!!, null, AppCategory.SETTINGS) {
    private val mHandler: Handler = NetworkUpdateHandler(this)
    private var mNetResources: Resources? = null
    private var mNetResourcesSet = false

    private class NetworkUpdateHandler(adapter: SettingsAdapter?) : Handler() {
        private val mAdapterRef: WeakReference<SettingsAdapter?>
        override fun handleMessage(msg: Message) {
            val adapter = mAdapterRef.get()
            adapter?.let {
                when (msg.what) {
                    1 -> {
                        val index = it.updateNetwork()
                        if (index >= 0) {
                            it.notifyItemChanged(index)
                            return
                        }
                        return
                    }
                    else -> return
                }
            }
        }

        init {
            mAdapterRef = WeakReference<SettingsAdapter?>(adapter)
        }
    }

    private inner class SettingsComparator : Comparator<LaunchPoint> {
        override fun compare(lhs: LaunchPoint, rhs: LaunchPoint): Int {
            if (lhs.priority != rhs.priority) {
                return rhs.priority - lhs.priority
            }
            if (lhs.title == null) {
                return -1
            }
            return if (rhs.title == null) {
                1
            } else lhs.title!!.compareTo(rhs.title!!, ignoreCase = true)
        }
    }

    override fun onPostRefresh() {
        mNetResourcesSet = false
        updateNetwork()
    }

    fun onConnectivityChange() {
        mHandler.sendEmptyMessage(1)
    }

    private fun updateNetwork(): Int {
        for (i in mLaunchPoints.indices) {
            val launchPoint = mLaunchPoints[i]
            if (launchPoint?.settingsType == 0) {
                setNetwork(mContext.resources, launchPoint)
                return i
            }
        }
        return -1
    }

    private fun setNetwork(res: Resources, launchPoint: LaunchPoint): LaunchPoint {
        if (!mNetResourcesSet) {
            setNetworkResources(launchPoint)
        }
        val connectivityStatus = mConnectivityListener.connectivityStatus
        var titleId = 0
        var title: String? = null
        var hasNetworkName = false
        launchPoint.iconDrawable = null
        when (connectivityStatus.mNetworkType) {
            1 -> {
                titleId = R.string.settings_network
                launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_disconnected")
            }
            3, 5 -> {
                title = connectivityStatus.mWifiSsid
                hasNetworkName = true
                when (connectivityStatus.mWifiSignalStrength) {
                    0 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_wifi_0")
                    1 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_wifi_1")
                    2 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_wifi_2")
                    3 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_wifi_3")
                    4 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_wifi_4")
                    else -> {
                    }
                }
            }
            7 -> {
                launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_ethernet")
                titleId = R.string.settings_network
            }
            9 -> {
                title = connectivityStatus.mWifiSsid
                hasNetworkName = true
                launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_wifi_no_internet")
            }
            11 -> {
                titleId = R.string.settings_network
                launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_ethernet_no_internet")
            }
            13 -> {
                title = connectivityStatus.mMobileNetworkName
                hasNetworkName = true
                when (connectivityStatus.mMobileSignalStrength) {
                    0 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_0")
                    1 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_1")
                    2 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_2")
                    3 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_3")
                    4 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_4")
                    else -> {
                    }
                }
            }
            15 -> {
                title = connectivityStatus.mMobileNetworkName
                hasNetworkName = true
                when (connectivityStatus.mMobileSignalStrength) {
                    0 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_0")
                    1 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_1")
                    2 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_2")
                    3 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_3")
                    4 -> launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_cellular_no_internet_4")
                    else -> {
                    }
                }
            }
        }
        if (launchPoint.iconDrawable == null) {
            launchPoint.iconDrawable = getNetResourceDrawable(launchPoint, "network_state_wifi_0")
        }
        if (titleId != 0) {
            title = res.getString(titleId)
        }
        if (title == null) {
            title = res.getString(R.string.settings_network)
        }
        launchPoint.title = title
        if (hasNetworkName) {
            launchPoint.contentDescription = res.getString(R.string.settings_network)
        }
        return launchPoint
    }

    override fun onRankerReady() {}
    override fun sortLaunchPoints(launchPoints: ArrayList<LaunchPoint>) {
        Collections.sort(launchPoints, SettingsComparator())
    }

    private fun setNetworkResources(lp: LaunchPoint) {
        try {
            mNetResources = mContext.packageManager.getResourcesForApplication(lp.launchIntent!!.component!!.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        mNetResourcesSet = true
    }

    private fun getNetResourceDrawable(launchPoint: LaunchPoint, resName: String): Drawable {
        var resId = 0
        if (mNetResources != null) {
            resId = mNetResources!!.getIdentifier(resName, "drawable", launchPoint.packageName)
        }
        if (resId != 0) {
            val ret = mNetResources!!.getDrawable(resId, null)
            if (ret != null) {
                return ret
            }
        }
        val launcherRes = mContext.resources
        return launcherRes.getDrawable(launcherRes.getIdentifier(resName, "drawable", mContext.packageName), null)
    }

    override fun onSettingsChanged() {
        refreshDataSetAsync()
    }

//    override fun onLaunchPointsAddedOrUpdated(arrayList: ArrayList<LaunchPoint>) {}

//    override fun onLaunchPointsRemoved(arrayList: ArrayList<LaunchPoint>) {}

    companion object {
        private const val TAG = "SettingsAdapter"
    }
}