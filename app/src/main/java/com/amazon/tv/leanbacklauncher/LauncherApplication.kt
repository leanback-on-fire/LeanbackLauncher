package com.amazon.tv.leanbacklauncher

import android.app.Application
import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.amazon.tv.leanbacklauncher.capabilities.HighEndLauncherConfiguration
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration
import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient
import com.amazon.tv.tvrecommendations.IRecommendationsService
import com.amazon.tv.tvrecommendations.RecommendationsClient
import java.util.*

class LauncherApplication : Application(), LifecycleObserver {
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
    }

    private inner class NewBlacklistClient(context: Context?) : SwitchingRecommendationsClient(context) {
        private var mBlacklist: Array<String> = emptyArray()
        fun saveBlackList(blacklist: Array<String>) {
            mBlacklist = blacklist
            connect()
        }

        override fun onConnected(service: IRecommendationsService) {
            try {
                val newBlacklist: MutableList<String?> = ArrayList<String?>(Arrays.asList(*service.blacklistedPackages))
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
            synchronized(LauncherApplication::class.java) {
                if (!sBlacklistMigrated) {
                    try {
                        val blacklist = service.blacklistedPackages
                        service.blacklistedPackages = arrayOfNulls(0)
                        sBlacklistMigrated = true
                        getSharedPreferences(javaClass.name, 0).edit().putInt("blacklist_migrate", 1).apply()
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
        //final PrimesSettings primesSettings = new PrimesSettings(this);
        /*if (primesSettings.isPrimesEnabled()) {
            Primes primes = Primes.initialize(PrimesApiProvider.newInstance(this, new PrimesConfigurationsProvider() {
                public PrimesConfigurations get() {
                    return PrimesConfigurations.newBuilder().setMetricTransmitter(LauncherApplication.this.getPrimesMetricTransmitter()).setPackageConfigurations(new PrimesPackageConfigurations(primesSettings.isPackageStatsMetricEnabled())).setMemoryConfigurations(new PrimesMemoryConfigurations(primesSettings.isMemoryMetricEnabled())).setCrashConfigurations(new PrimesCrashConfigurations(primesSettings.isCrashMetricEnabled())).build();
                }
            }));
            primes.startMemoryMonitor();
            primes.startCrashMonitor();
            return;
        }*/
        Log.e(TAG, "PRIMES not enabled")
    }

    private fun demigrate() {
        var z = false
        if (sBlacklistMigrated || getSharedPreferences(javaClass.name, 0).getInt("blacklist_migrate", 0) >= 1) {
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