package com.bumptech.glide.module;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class ManifestParser {
    private final Context context;

    public ManifestParser(Context context) {
        this.context = context;
    }

    public List<GlideModule> parse() {
        if (Log.isLoggable("ManifestParser", 3)) {
            Log.d("ManifestParser", "Loading Glide modules");
        }
        List<GlideModule> modules = new ArrayList();
        try {
            ApplicationInfo appInfo = this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(), 128);
            if (appInfo.metaData != null) {
                for (String key : appInfo.metaData.keySet()) {
                    if ("GlideModule".equals(appInfo.metaData.get(key))) {
                        modules.add(parseModule(key));
                        if (Log.isLoggable("ManifestParser", 3)) {
                            String str = "ManifestParser";
                            String str2 = "Loaded Glide module: ";
                            String valueOf = String.valueOf(key);
                            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                        } else {
                            continue;
                        }
                    }
                }
                if (Log.isLoggable("ManifestParser", 3)) {
                    Log.d("ManifestParser", "Finished loading Glide modules");
                }
            }
            return modules;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Unable to find metadata to parse GlideModules", e);
        }
    }

    private static GlideModule parseModule(String className) {
        String valueOf;
        ReflectiveOperationException e;
        try {
            Class<?> clazz = Class.forName(className);
            try {
                Object module = clazz.newInstance();
                if (module instanceof GlideModule) {
                    return (GlideModule) module;
                }
                valueOf = String.valueOf(module);
                throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 44).append("Expected instanceof GlideModule, but found: ").append(valueOf).toString());
            } catch (InstantiationException e2) {
                e = e2;
                valueOf = String.valueOf(clazz);
                throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 53).append("Unable to instantiate GlideModule implementation for ").append(valueOf).toString(), e);
            } catch (IllegalAccessException e3) {
                e = e3;
                valueOf = String.valueOf(clazz);
                throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 53).append("Unable to instantiate GlideModule implementation for ").append(valueOf).toString(), e);
            }
        } catch (ClassNotFoundException e4) {
            throw new IllegalArgumentException("Unable to find GlideModule implementation", e4);
        }
    }
}
