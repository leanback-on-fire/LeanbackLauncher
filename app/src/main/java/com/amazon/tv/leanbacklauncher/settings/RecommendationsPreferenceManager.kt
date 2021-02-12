package com.amazon.tv.leanbacklauncher.settings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.DeadObjectException
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient
import com.amazon.tv.leanbacklauncher.settings.RecommendationsPreferenceManager.AsyncRecommendationsClient
import com.amazon.tv.tvrecommendations.IRecommendationsService
import java.lang.Boolean
import java.util.*

class RecommendationsPreferenceManager(context: Context) {
    private val mContext: Context

    interface LoadBlacklistCountCallback {
        fun onBlacklistCountLoaded(i: Int)
    }

    interface LoadRecommendationPackagesCallback {
        fun onRecommendationPackagesLoaded(list: List<PackageInfo>?)
    }

    private abstract class AsyncRecommendationsClient(context: Context?) : SwitchingRecommendationsClient(context) {
        private inner class Task() : AsyncTask<IRecommendationsService, Void?, kotlin.Boolean>() {

            override fun doInBackground(vararg params: IRecommendationsService): kotlin.Boolean {
                try {
                    callServiceInBackground(params[0])
                } catch (e: DeadObjectException) {
                    Log.e("RecPrefManager", "Rec service connection broken", e)
                    return Boolean.valueOf(true)
                } catch (e2: RemoteException) {
                    Log.e("RecPrefManager", "Call to recommendation service failed", e2)
                } finally {
                    disconnect()
                }
                return Boolean.valueOf(false)
            }

            override fun onPostExecute(retry: kotlin.Boolean) {
                if (retry) {
                    Log.d("RecPrefManager", "Task failed, retrying")
                    connect()
                    return
                }
                this@AsyncRecommendationsClient.onPostExecute()
            }
        }

        @Throws(RemoteException::class)
        protected abstract fun callServiceInBackground(iRecommendationsService: IRecommendationsService)
        override fun onConnected(service: IRecommendationsService) {
            Task().execute(service)
        }

        override fun onDisconnected() {}
        protected open fun onPostExecute() {}
    }

    private class LoadBlacklistCountTask(context: Context, private val mCallback: LoadBlacklistCountCallback) : AsyncRecommendationsClient(context) {
        private var mBlacklistedPackageCount = 0
        private val mPackageManager: PackageManager
        override fun callServiceInBackground(service: IRecommendationsService) {
            try {
                val blacklist = service?.blacklistedPackages
                blacklist?.let {
                    mBlacklistedPackageCount = blacklist.size
                    for (pkg in blacklist) {
                        try {
                            mPackageManager.getPackageInfo(pkg, 0)
                        } catch (e: PackageManager.NameNotFoundException) {
                            mBlacklistedPackageCount--
                        }
                    }
                }
            } catch (e2: RemoteException) {
                mBlacklistedPackageCount = 0
            }
        }

        override fun onPostExecute() {
            mCallback.onBlacklistCountLoaded(Math.max(0, mBlacklistedPackageCount))
        }

        init {
            mPackageManager = context.packageManager
        }
    }

    private class LoadRecommendationPackagesTask(private val mContext: Context, private val mCallback: LoadRecommendationPackagesCallback) : AsyncRecommendationsClient(mContext) {
        private var mPackages: MutableList<PackageInfo>? = null

        @Throws(RemoteException::class)
        override fun callServiceInBackground(service: IRecommendationsService) {
            val packages = service.recommendationsPackages
            val blacklistedPackages = Arrays.asList(*service.blacklistedPackages)
            mPackages = ArrayList<PackageInfo>(packages.size)
            val pm = this.mContext.packageManager
            for (packageName in packages) {
                val info = PackageInfo()
                info.packageName = packageName
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val res = pm.getResourcesForApplication(packageName)
                    info.appTitle = pm.getApplicationLabel(appInfo)
                    if (appInfo.banner != 0) {
                        info.banner = res.getDrawable(appInfo.banner, null)
                    } else {
                        val intent = Intent()
                        intent.addCategory("android.intent.category.LEANBACK_LAUNCHER")
                        intent.action = "android.intent.action.MAIN"
                        intent.setPackage(packageName)
                        val resolveInfo = pm.resolveActivity(intent, 0)
                        if (!(resolveInfo == null || resolveInfo.activityInfo == null)) {
                            if (resolveInfo.activityInfo.banner != 0) {
                                info.banner = res.getDrawable(resolveInfo.activityInfo.banner, null)
                            }
                            if (info.banner == null && resolveInfo.activityInfo.logo != 0) {
                                info.banner = res.getDrawable(resolveInfo.activityInfo.logo, null)
                            }
                        }
                    }
                    if (info.banner == null && appInfo.icon != 0) {
                        info.icon = res.getDrawable(appInfo.icon, null)
                    }
                    if (TextUtils.isEmpty(info.appTitle)) {
                        info.appTitle = packageName
                    }
                    if (info.banner == null && info.icon == null) {
                        info.icon = ContextCompat.getDrawable(this.mContext, 17301651)
                    }
                    info.blacklisted = blacklistedPackages.contains(packageName)
                    mPackages?.add(info)
                } catch (e: PackageManager.NameNotFoundException) {
                }
            }
        }

        override fun onPostExecute() {
            mCallback.onRecommendationPackagesLoaded(mPackages)
        }
    }

    class PackageInfo {
        var appTitle: CharSequence? = null
        var banner: Drawable? = null
        var blacklisted = false
        var icon: Drawable? = null
        var packageName: String? = null
    }

    private class SaveBlacklistTask(context: Context?, private val mPackageName: String?, private val mBlacklisted: kotlin.Boolean) : AsyncRecommendationsClient(context) {
        @Throws(RemoteException::class)
        override fun callServiceInBackground(service: IRecommendationsService) {
            val blacklist: MutableList<String?> = ArrayList<String?>(Arrays.asList(*service.blacklistedPackages))
            if (!mBlacklisted) {
                blacklist.remove(mPackageName)
            } else if (!blacklist.contains(mPackageName)) {
                blacklist.add(mPackageName)
            }
            service.blacklistedPackages = blacklist.toTypedArray()
        }
    }

    fun loadBlacklistCount(callback: LoadBlacklistCountCallback) {
        LoadBlacklistCountTask(mContext, callback).connect()
    }

    fun loadRecommendationsPackages(callback: LoadRecommendationPackagesCallback) {
        LoadRecommendationPackagesTask(mContext, callback).connect()
    }

    fun savePackageBlacklisted(packageName: String?, blacklisted: kotlin.Boolean) {
        SaveBlacklistTask(mContext, packageName, blacklisted).connect()
    }

    init {
        mContext = context.applicationContext
    }
}