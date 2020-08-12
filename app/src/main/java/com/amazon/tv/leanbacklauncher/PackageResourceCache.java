package com.amazon.tv.leanbacklauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import java.util.HashMap;

public class PackageResourceCache {
    private static PackageResourceCache sInstance;
    private HashMap<String, ResourceCacheEntry> mMap = new HashMap();
    private PackageManager mPackageManager;

    private static class ResourceCacheEntry {
        SparseArray<Drawable> drawableMap;
        Resources resources;

        private ResourceCacheEntry() {
            this.drawableMap = new SparseArray();
        }
    }

    public static PackageResourceCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PackageResourceCache(context);
        }
        return sInstance;
    }

    private PackageResourceCache(Context context) {
        Context appContext = context.getApplicationContext();
        this.mPackageManager = appContext.getPackageManager();
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                PackageResourceCache.this.mMap.remove(intent.getData().getSchemeSpecificPart());
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        appContext.registerReceiver(receiver, intentFilter);
    }

    public Drawable getDrawable(String packageName, int id) throws NameNotFoundException {
        ResourceCacheEntry entry = getCacheEntry(packageName);
        if (entry == null) {
            return null;
        }
        Drawable drawable = entry.drawableMap.get(id);
        if (drawable != null) {
            return drawable;
        }
        drawable = entry.resources.getDrawable(id, null);
        entry.drawableMap.put(id, drawable);
        return drawable;
    }

    private ResourceCacheEntry getCacheEntry(String packageName) throws NameNotFoundException {
        ResourceCacheEntry entry = this.mMap.get(packageName);
        if (entry != null) {
            return entry;
        }
        entry = new ResourceCacheEntry();
        entry.resources = this.mPackageManager.getResourcesForApplication(packageName);
        this.mMap.put(packageName, entry);
        return entry;
    }
}
