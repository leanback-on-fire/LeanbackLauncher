package com.amazon.tv.leanbacklauncher

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import com.amazon.tv.leanbacklauncher.capabilities.HighEndLauncherConfiguration
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration
import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient
import com.amazon.tv.leanbacklauncher.util.Updater
import com.amazon.tv.tvrecommendations.IRecommendationsService
import com.amazon.tv.tvrecommendations.RecommendationsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class App : Application(), LifecycleObserver {
    private var mNewBlacklistClient: NewBlacklistClient? = null
    private var mOldBlacklistClient: OldBlacklistClient? = null
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName

    companion object {
        private lateinit var contextApp: Context
        var inForeground: Boolean = false
        private var sBlacklistMigrated = false
        fun getContext(): Context {
            return contextApp
        }

        fun toast(txt: String?, long: Boolean = false) {
            Handler(Looper.getMainLooper()).post {
                val show = if (long)
                    Toast.LENGTH_LONG
                else
                    Toast.LENGTH_SHORT
                if (!txt.isNullOrEmpty())
                    Toast.makeText(contextApp, txt, show).show()
            }
        }

        fun toast(resId: Int?, long: Boolean = false) {
            Handler(Looper.getMainLooper()).post {
                val show = if (long)
                    Toast.LENGTH_LONG
                else
                    Toast.LENGTH_SHORT
                if (resId != null)
                    Toast.makeText(contextApp, resId, show).show()
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
                    ArrayList<String?>(listOf(*service.blacklistedPackages))
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
            synchronized(App::class.java) {
                if (!sBlacklistMigrated) {
                    try {
                        val blacklist = service.blacklistedPackages
                        service.blacklistedPackages = arrayOfNulls(0)
                        sBlacklistMigrated = true
                        getSharedPreferences(javaClass.name, 0).edit()
                            .putInt("blacklist_migrate", 1).apply()
                        if (blacklist == null || blacklist.size <= 0) {
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
        contextApp = applicationContext
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        initDeviceCapabilities()
        initPrimes()
        demigrate()
        val autoupdate = PreferenceManager.getDefaultSharedPreferences(contextApp)
            .getBoolean("update_check", true)
        if (autoupdate)
        // self update check
            GlobalScope.launch(Dispatchers.IO) {
                var count = 60
                try {
                    while (!isConnected(contextApp) && count > 0) {
                        delay(1000) // wait for network
                        count--
                    }
                    if (Updater.check()) {
                        val intent = Intent(contextApp, UpdateActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                }
            }
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            networkInfo = connectivityManager.activeNetworkInfo
        }
        return networkInfo != null && networkInfo.state == NetworkInfo.State.CONNECTED
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        if (BuildConfig.DEBUG) Log.d(TAG, "in Foreground")
        inForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (BuildConfig.DEBUG) Log.d(TAG, "in Background")
        inForeground = false
    }

    private fun initDeviceCapabilities() {
        LauncherConfiguration.setInstance(HighEndLauncherConfiguration())
    }

    private fun initPrimes() {
        /*final PrimesSettings primesSettings = new PrimesSettings(this);
        if (primesSettings.isPrimesEnabled()) {
            Primes primes = Primes.initialize(PrimesApiProvider.newInstance(this, new PrimesConfigurationsProvider() {
                public PrimesConfigurations get() {
                    return PrimesConfigurations.newBuilder().setMetricTransmitter(LauncherApplication.this.getPrimesMetricTransmitter()).setPackageConfigurations(new PrimesPackageConfigurations(primesSettings.isPackageStatsMetricEnabled())).setMemoryConfigurations(new PrimesMemoryConfigurations(primesSettings.isMemoryMetricEnabled())).setCrashConfigurations(new PrimesCrashConfigurations(primesSettings.isCrashMetricEnabled())).build();
                }
            }));
            primes.startMemoryMonitor();
            primes.startCrashMonitor();
            return;
        }*/
        Log.d(TAG, "PRIMES not enabled")
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