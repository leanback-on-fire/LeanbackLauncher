package com.amazon.tv.leanbacklauncher.apps

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.amazon.tv.leanbacklauncher.BuildConfig
import java.lang.ref.WeakReference

class ConnectivityListener(private val mContext: Context, listener: Listener) {
    private val mConnectivityManager: ConnectivityManager =
        mContext.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val connectivityStatus = ConnectivityStatus()
    private val mFilter: IntentFilter
    private var mIsRegistered = false
    private val mListener: Listener
    private val mPhoneStateListener: LeanbackLauncherPhoneStateListener =
        LeanbackLauncherPhoneStateListener(this)
    private val mReceiver: BroadcastReceiver
    private val mTelephonyManager: TelephonyManager?
    private val mWifiManager: WifiManager =
        mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    interface Listener {
        fun onConnectivityChange()
    }

    init {
        mTelephonyManager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mListener = listener
        mFilter = IntentFilter()
        mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        mFilter.addAction("android.net.wifi.RSSI_CHANGED")
        mFilter.addAction("android.net.conn.INET_CONDITION_ACTION")
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                var z = false
                val intentAction = intent.action
                val connectionStatus = intent.getIntExtra("inetCondition", -551)
                val info =
                    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
                if (!(info != null && info.isAvailable && info.isConnected)) {
                    writeConnectivity(context, false)
                }
                if (intentAction == "android.net.conn.INET_CONDITION_ACTION" || intentAction == "android.net.conn.CONNECTIVITY_CHANGE" && !readConnectivity(
                        mContext
                    )
                ) {
                    if (connectionStatus > 50) {
                        z = true
                    }
                    writeConnectivity(context, z)
                }
                updateConnectivityStatus()
                mListener.onConnectivityChange()
            }
        }
    }

    companion object {
        private val TAG =
            if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName

        @TargetApi(23)
        private fun getLevel(signalStrength: SignalStrength): Int {
            val e: LinkageError
            return try {
                signalStrength.level
            } catch (e2: NoClassDefFoundError) {
                e = e2
                Log.e(TAG, "Exception fetching signal level", e)
                0
            } catch (e3: IncompatibleClassChangeError) {
                e = e3
                Log.e(TAG, "Exception fetching signal level", e)
                0
            }
        }

        private fun writeConnectivity(context: Context, inetConnected: Boolean) {
            context.getSharedPreferences("inet-prefs", 0).edit()
                .putBoolean("inetCondition", inetConnected).apply()
        }

        @JvmStatic
        fun readConnectivity(context: Context): Boolean {
            return context.getSharedPreferences("inet-prefs", 0).getBoolean("inetCondition", true)
        }

        fun removeDoubleQuotes(string: String?): String? {
            if (string == null) {
                return null
            }
            return string.removeSurrounding("\"")
//            val length = string.length
//            return if (length > 1 && string[0] == '\"' && string[length - 1] == '\"') {
//                string.substring(1, length - 1)
//            } else string
        }
    }

    class ConnectivityStatus {
        var mMobileNetworkName: String? = null
        var mMobileSignalStrength = 0
        var mNetworkType = 0
        var mWifiSignalStrength = 0
        var mWifiSsid: String? = null
    }

    private class LeanbackLauncherPhoneStateListener(listener: ConnectivityListener?) :
        PhoneStateListener() {
        private val mListener: WeakReference<ConnectivityListener?> =
            WeakReference<ConnectivityListener?>(listener)

        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            super.onSignalStrengthsChanged(signalStrength)
            val listener = mListener.get()
            if (listener != null) {
                listener.connectivityStatus.mMobileSignalStrength = getLevel(signalStrength)
            }
        }

    }

    fun start() {
        if (!mIsRegistered) {
            updateConnectivityStatus()
            mContext.registerReceiver(mReceiver, mFilter)
            mTelephonyManager!!.listen(mPhoneStateListener, 256)
            mIsRegistered = true
        }
    }

    fun stop() {
        if (mIsRegistered) {
            mContext.unregisterReceiver(mReceiver)
            mTelephonyManager!!.listen(mPhoneStateListener, 0)
            mIsRegistered = false
        }
    }

    private fun setNoConnection() {
        connectivityStatus.mNetworkType = 1
        connectivityStatus.mWifiSsid = null
        connectivityStatus.mWifiSignalStrength = 0
    }

    private fun isSecureWifi(wifiInfo: WifiInfo?): Boolean {
        if (wifiInfo == null) {
            return false
        }
        val networkId = wifiInfo.networkId
        val configuredNetworks = if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        } else
            mWifiManager.configuredNetworks ?: return false
        for (configuredNetwork in configuredNetworks) {
            if (configuredNetwork.networkId == networkId) {
                return configuredNetwork.allowedKeyManagement[1] || configuredNetwork.allowedKeyManagement[2] || configuredNetwork.allowedKeyManagement[3]
            }
        }
        return false
    }

    private fun updateConnectivityStatus() {
        // deprecated in API29
        val networkInfo = mConnectivityManager.activeNetworkInfo
        if (networkInfo == null) {
            setNoConnection()
            return
        }
        val isConnected = readConnectivity(mContext)
        when (networkInfo.type) {
            0 -> {
                if (isConnected) {
                    connectivityStatus.mNetworkType = 13
                } else {
                    connectivityStatus.mNetworkType = 15
                }
                var operator: String? = null
                if (mTelephonyManager != null) {
                    operator = mTelephonyManager.networkOperatorName
                    if (operator != null) {
                        operator = removeDoubleQuotes(operator)
                    }
                }
                if (operator != null) {
                    connectivityStatus.mMobileNetworkName = operator
                } else {
                    connectivityStatus.mMobileNetworkName = ""
                }
                connectivityStatus.mWifiSsid = null
                connectivityStatus.mWifiSignalStrength = 0
                return
            }
            1 -> {
                val wifiInfo = mWifiManager.connectionInfo
                if (isSecureWifi(wifiInfo)) {
                    connectivityStatus.mNetworkType = 5
                } else {
                    connectivityStatus.mNetworkType = 3
                }
                if (!isConnected) {
                    connectivityStatus.mNetworkType = 9
                }
                var ssid: String? = null
                if (wifiInfo != null) {
                    ssid = wifiInfo.ssid
                    if (ssid != null) {
                        ssid = removeDoubleQuotes(ssid)
                    }
                }
                if (ssid != null) {
                    connectivityStatus.mWifiSsid = if (ssid == "<unknown ssid>") "" else ssid
                } else {
                    connectivityStatus.mWifiSsid = ""
                }
                if (wifiInfo != null) {
                    connectivityStatus.mWifiSignalStrength =
                        WifiManager.calculateSignalLevel(wifiInfo.rssi, 5)
                } else {
                    connectivityStatus.mWifiSignalStrength = 0
                }
                connectivityStatus.mMobileNetworkName = null
                connectivityStatus.mMobileSignalStrength = 0
                return
            }
            9 -> {
                if (isConnected) {
                    connectivityStatus.mNetworkType = 7
                } else {
                    connectivityStatus.mNetworkType = 11
                }
                connectivityStatus.mWifiSsid = null
                connectivityStatus.mWifiSignalStrength = 0
                connectivityStatus.mMobileNetworkName = null
                connectivityStatus.mMobileSignalStrength = 0
                return
            }
            else -> {
                setNoConnection()
                return
            }
        }
    }
}