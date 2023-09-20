package com.amazon.tv.leanbacklauncher

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import com.amazon.tv.leanbacklauncher.capabilities.HighEndLauncherConfiguration
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration
import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient
import com.amazon.tv.leanbacklauncher.util.Updater
import com.amazon.tv.tvrecommendations.IRecommendationsService
import com.amazon.tv.tvrecommendations.RecommendationsClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LauncherApp : Application() {
    private var mNewBlacklistClient: NewBlacklistClient? = null
    private var mOldBlacklistClient: OldBlacklistClient? = null

    companion object {
        private val TAG = if (BuildConfig.DEBUG) "[*]LeanbackOnFire" else "LeanbackOnFire"
        private lateinit var appContext: Context
        var inForeground: Boolean = false
        private var sBlacklistMigrated = false
        private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                if (BuildConfig.DEBUG) Log.d(TAG, "in background")
                inForeground = false
            } else if (event == Lifecycle.Event.ON_START) {
                if (BuildConfig.DEBUG) Log.d(TAG, "in foreground")
                inForeground = true
            }
        }

        val context: Context
            get() {
                return appContext
            }

        fun toast(txt: String?, long: Boolean = false) {
            Handler(Looper.getMainLooper()).post {
                val show = if (long)
                    Toast.LENGTH_LONG
                else
                    Toast.LENGTH_SHORT
                if (!txt.isNullOrEmpty())
                    Toast.makeText(appContext, txt, show).show()
            }
        }

        fun toast(resId: Int?, long: Boolean = false) {
            Handler(Looper.getMainLooper()).post {
                val show = if (long)
                    Toast.LENGTH_LONG
                else
                    Toast.LENGTH_SHORT
                if (resId != null)
                    Toast.makeText(appContext, resId, show).show()
            }
        }
    }

    private inner class NewBlacklistClient(context: Context?) :
        SwitchingRecommendationsClient(context) {
        private var mBlacklist: Array<String> = emptyArray()
        fun saveBlackList(blacklist: Array<String>) {
            mBlacklist = blacklist
            connect()
        }

        override fun onConnected(service: IRecommendationsService) {
            try {
                val newBlacklist: MutableList<String?> =
                    ArrayList(listOf(*service.blacklistedPackages))
                for (pkg in mBlacklist) {
                    if (!newBlacklist.contains(pkg)) {
                        newBlacklist.add(pkg)
                    }
                }
                service.blacklistedPackages = newBlacklist.toTypedArray()
            } catch (e: RemoteException) {
                Log.e(TAG, "Could not save migrated blacklist", e)
            }
            disconnect()
            mNewBlacklistClient = null
        }

        override fun onDisconnected() {}
    }

    private inner class OldBlacklistClient(context: Context?) : RecommendationsClient(context) {
        override fun onConnected(service: IRecommendationsService) {
            synchronized(LauncherApp::class.java) {
                if (!sBlacklistMigrated) {
                    try {
                        val blacklist = service.blacklistedPackages
                        service.blacklistedPackages = arrayOfNulls(0)
                        sBlacklistMigrated = true
                        getSharedPreferences(javaClass.name, 0).edit()
                            .putInt("blacklist_migrate", 1).apply()
                        if (blacklist == null || blacklist.isEmpty()) {
                            Log.d(TAG, "No blacklist to migrate")
                        } else {
                            mNewBlacklistClient!!.saveBlackList(blacklist)
                        }
                    } catch (e: RemoteException) {
                        Log.e(TAG, "Could not migrate blacklist", e)
                    }
                }
            }
            disconnect()
            mOldBlacklistClient = null
        }

        override fun onDisconnected() {}
    }

    override fun onCreate() {
        appContext = applicationContext
        super.onCreate()
        ProcessLifecycleOwner
            .get().lifecycle
            .addObserver(lifecycleEventObserver)

        initDeviceCapabilities()
        demigrate()

        val autoupdate = PreferenceManager.getDefaultSharedPreferences(appContext)
            .getBoolean("update_check", true)
        if (autoupdate) // self update check
            CoroutineScope(Dispatchers.IO).launch {
                var count = 60
                try {
                    while (!isConnected(appContext) && count > 0) {
                        delay(1000) // wait for network
                        count--
                    }
                    if (Updater.check()) {
                        val intent = Intent(appContext, UpdateActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } catch (_: Exception) {
                }
            }
    }

    @Suppress("DEPRECATION")
    private fun isConnectedOld(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        return networkInfo?.isConnected == true

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnectedNewApi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun isConnected(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isConnectedNewApi(context)
        } else{
            isConnectedOld(context)
        }
    }

    private fun initDeviceCapabilities() {
        LauncherConfiguration.setInstance(HighEndLauncherConfiguration())
    }

    private fun demigrate() {
        var z = false
        if (sBlacklistMigrated || getSharedPreferences(
                javaClass.name,
                0
            ).getInt("blacklist_migrate", 0) >= 1
        ) {
            z = true
        }
        sBlacklistMigrated = z
        if (!sBlacklistMigrated) {
            mOldBlacklistClient = OldBlacklistClient(this)
            mNewBlacklistClient = NewBlacklistClient(this)
            try {
                mOldBlacklistClient!!.connect()
            } catch (e: RuntimeException) {
                Log.v(TAG, "Couldn't connect to service to read blacklist", e)
            }
        }
    }
}