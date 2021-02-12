package com.amazon.tv.leanbacklauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.SparseArray
import java.util.*

class PackageResourceCache private constructor(context: Context?) {
    private val mMap: HashMap<String?, ResourceCacheEntry?> = hashMapOf()
    private val mPackageManager: PackageManager?

    private class ResourceCacheEntry {
        var drawableMap: SparseArray<Drawable?>
        var resources: Resources? = null

        init {
            drawableMap = SparseArray<Drawable?>()
        }
    }

    init {
        val appContext = context?.applicationContext
        mPackageManager = appContext?.packageManager
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mMap.remove(intent.data!!.schemeSpecificPart)
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED")
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED")
        intentFilter.addDataScheme("package")
        appContext?.registerReceiver(receiver, intentFilter)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getDrawable(packageName: String, id: Int): Drawable? {
        val entry = getCacheEntry(packageName) ?: return null
        var drawable = entry.drawableMap[id]
        if (drawable != null) {
            return drawable
        }
        drawable = entry.resources!!.getDrawable(id, null)
        entry.drawableMap.put(id, drawable)
        return drawable
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun getCacheEntry(packageName: String): ResourceCacheEntry {
        var entry = mMap[packageName]
        if (entry != null) {
            return entry
        }
        entry = ResourceCacheEntry()
        entry.resources = mPackageManager?.getResourcesForApplication(packageName)
        mMap[packageName] = entry
        return entry
    }

    companion object {
        private var sInstance: PackageResourceCache? = null
        fun getInstance(context: Context?): PackageResourceCache? {
            if (sInstance == null) {
                sInstance = PackageResourceCache(context)
            }
            return sInstance
        }
    }
}