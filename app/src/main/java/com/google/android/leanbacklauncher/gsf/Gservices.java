package com.google.android.leanbacklauncher.gsf;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Gservices {
    public static final Uri CONTENT_PREFIX_URI;
    public static final Uri CONTENT_URI;
    public static final Pattern FALSE_PATTERN;
    public static final Pattern TRUE_PATTERN;
    private static final HashMap<String, Boolean> sBooleanCache;
    private static HashMap<String, String> sCache;
    private static final HashMap<String, Float> sFloatCache;
    private static final HashMap<String, Integer> sIntCache;
    private static final HashMap<String, Long> sLongCache;
    private static Context sPreCachePermissionCheckContext;
    private static String[] sPreloadedPrefixes;
    private static Object sVersionToken;

    /* renamed from: com.google.android.gsf.Gservices.1 */
    static class C01461 extends Thread {
        final /* synthetic */ ContentResolver val$cr;

        /* renamed from: com.google.android.gsf.Gservices.1.1 */
        class C01451 extends ContentObserver {
            final /* synthetic */ ContentResolver val$cr;

            C01451(Handler $anonymous0, ContentResolver val$cr) {
                super($anonymous0);
                this.val$cr = val$cr;
            }

            public void onChange(boolean selfChange) {
                synchronized (Gservices.class) {
                    Gservices.sCache.clear();
                    Gservices.sBooleanCache.clear();
                    Gservices.sIntCache.clear();
                    Gservices.sLongCache.clear();
                    Gservices.sFloatCache.clear();
                    Gservices.sVersionToken = new Object();
                    if (Gservices.sPreloadedPrefixes.length > 0) {
                        Gservices.bulkCacheByPrefix(this.val$cr, Gservices.sPreloadedPrefixes);
                    }
                }
            }
        }

        C01461(String $anonymous0, ContentResolver val$cr) {
            super($anonymous0);
            this.val$cr = val$cr;
        }

        public void run() {
            Looper.prepare();
            this.val$cr.registerContentObserver(Gservices.CONTENT_URI, true, new C01451(new Handler(Looper.myLooper()), this.val$cr));
            Looper.loop();
        }
    }

    static {
        CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
        CONTENT_PREFIX_URI = Uri.parse("content://com.google.android.gsf.gservices/prefix");
        TRUE_PATTERN = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
        FALSE_PATTERN = Pattern.compile("^(0|false|f|off|no|n)$", 2);
        sBooleanCache = new HashMap();
        sIntCache = new HashMap();
        sLongCache = new HashMap();
        sFloatCache = new HashMap();
        sPreloadedPrefixes = new String[0];
        sPreCachePermissionCheckContext = null;
    }

    private static void ensureCacheInitializedLocked(ContentResolver cr) {
        if (sCache == null) {
            sCache = new HashMap();
            sVersionToken = new Object();
            new C01461("Gservices", cr).start();
        }
    }

    private static void maybeEnforceCalingOrSelfPermission() {
        if (sPreCachePermissionCheckContext != null) {
            sPreCachePermissionCheckContext.enforceCallingOrSelfPermission("com.google.android.providers.gsf.permission.READ_GSERVICES", "attempting to read gservices without permission");
        }
    }

    public static String getString(ContentResolver cr, String key, String defValue) {
        maybeEnforceCalingOrSelfPermission();
        synchronized (Gservices.class) {
            ensureCacheInitializedLocked(cr);
            Object version = sVersionToken;
            String value;
            if (sCache.containsKey(key)) {
                value = (String) sCache.get(key);
                if (value == null) {
                    value = defValue;
                }
                return value;
            }
            for (String prefix : sPreloadedPrefixes) {
                if (key.startsWith(prefix)) {
                    return defValue;
                }
            }
            Cursor cursor = cr.query(CONTENT_URI, null, null, new String[]{key}, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        value = cursor.getString(1);
                        synchronized (Gservices.class) {
                            if (version == sVersionToken) {
                                sCache.put(key, value);
                            }
                        }
                        if (value == null) {
                            value = defValue;
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                        return value;
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            synchronized (Gservices.class) {
                if (version == sVersionToken) {
                    sCache.put(key, null);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return defValue;
        }
    }

    public static String getString(ContentResolver cr, String key) {
        return getString(cr, key, null);
    }

    public static float getFloat(ContentResolver cr, String key, float defValue) {
        Object version = getVersionToken(cr);
        Float cacheValue = (Float) getValue(sFloatCache, key, Float.valueOf(defValue));
        if (cacheValue != null) {
            return cacheValue.floatValue();
        }
        float value;
        String valString = getString(cr, key);
        if (valString == null) {
            value = defValue;
        } else {
            try {
                value = Float.parseFloat(valString);
                cacheValue = Float.valueOf(value);
            } catch (NumberFormatException e) {
                value = defValue;
            }
        }
        putValue(version, sFloatCache, key, cacheValue);
        return value;
    }

    public static Map<String, String> getStringsByPrefix(ContentResolver cr, String... prefixes) {
        maybeEnforceCalingOrSelfPermission();
        Cursor c = cr.query(CONTENT_PREFIX_URI, null, null, prefixes, null);
        TreeMap<String, String> out = new TreeMap();
        if (c == null) {
            return out;
        }
        while (c.moveToNext()) {
            try {
                out.put(c.getString(0), c.getString(1));
            } finally {
                c.close();
            }
        }
        return out;
    }

    public static void bulkCacheByPrefix(ContentResolver cr, String... prefixes) {
        maybeEnforceCalingOrSelfPermission();
        Map<String, String> entries = getStringsByPrefix(cr, prefixes);
        synchronized (Gservices.class) {
            ensureCacheInitializedLocked(cr);
            sPreloadedPrefixes = prefixes;
            for (Entry<String, String> entry : entries.entrySet()) {
                sCache.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
    }

    public static Object getVersionToken(ContentResolver cr) {
        Object obj;
        maybeEnforceCalingOrSelfPermission();
        synchronized (Gservices.class) {
            ensureCacheInitializedLocked(cr);
            obj = sVersionToken;
        }
        return obj;
    }

    private static <T> T getValue(HashMap<String, T> cache, String key, T defValue) {
        synchronized (Gservices.class) {
            if (cache.containsKey(key)) {
                T value = cache.get(key);
                if (value == null) {
                    value = defValue;
                }
                return value;
            }
            return null;
        }
    }

    private static <T> void putValue(Object version, HashMap<String, T> cache, String key, T value) {
        synchronized (Gservices.class) {
            if (version == sVersionToken) {
                cache.put(key, value);
            }
        }
    }
}
