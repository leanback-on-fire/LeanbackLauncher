package com.google.android.gsf;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gservices
{
  public static final String CHANGED_ACTION = "com.google.gservices.intent.action.GSERVICES_CHANGED";
  public static final Uri CONTENT_PREFIX_URI;
  public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
  private static final boolean DEBUG = false;
  public static final Pattern FALSE_PATTERN;
  public static final String OVERRIDE_ACTION = "com.google.gservices.intent.action.GSERVICES_OVERRIDE";
  public static final String PERMISSION_READ_GSERVICES = "com.google.android.providers.gsf.permission.READ_GSERVICES";
  public static final String TAG = "Gservices";
  public static final Pattern TRUE_PATTERN;
  static final HashMap<String, Boolean> sBooleanCache;
  static HashMap<String, String> sCache;
  static final HashMap<String, Float> sFloatCache = new HashMap();
  static final HashMap<String, Integer> sIntCache;
  private static final AtomicBoolean sInvalidateCache;
  static final HashMap<String, Long> sLongCache;
  private static boolean sPreloaded;
  static String[] sPreloadedPrefixes = new String[0];
  private static Object sVersionToken;
  
  static
  {
    CONTENT_PREFIX_URI = Uri.parse("content://com.google.android.gsf.gservices/prefix");
    TRUE_PATTERN = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
    FALSE_PATTERN = Pattern.compile("^(0|false|f|off|no|n)$", 2);
    sInvalidateCache = new AtomicBoolean();
    sBooleanCache = new HashMap();
    sIntCache = new HashMap();
    sLongCache = new HashMap();
  }
  
  private static String[] addNewPrefixesLocked(String[] paramArrayOfString)
  {
    HashSet localHashSet = new HashSet((sPreloadedPrefixes.length + paramArrayOfString.length) * 4 / 3 + 1);
    localHashSet.addAll(Arrays.asList(sPreloadedPrefixes));
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if (localHashSet.add(str)) {
        localArrayList.add(str);
      }
      i += 1;
    }
    if (localArrayList.isEmpty()) {
      return new String[0];
    }
    sPreloadedPrefixes = (String[])localHashSet.toArray(new String[localHashSet.size()]);
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public static void bulkCacheByPrefix(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return;
    }
    for (;;)
    {
      try
      {
        ensureCacheInitializedLocked(paramContentResolver);
        paramVarArgs = addNewPrefixesLocked(paramVarArgs);
        if ((!sPreloaded) || (sCache.isEmpty()))
        {
          bulkCacheByPrefixLocked(paramContentResolver, sPreloadedPrefixes);
          return;
        }
      }
      finally {}
      if (paramVarArgs.length != 0) {
        bulkCacheByPrefixLocked(paramContentResolver, paramVarArgs);
      }
    }
  }
  
  private static void bulkCacheByPrefixLocked(ContentResolver paramContentResolver, String[] paramArrayOfString)
  {
    sCache.putAll(getStringsByPrefix(paramContentResolver, paramArrayOfString));
    sPreloaded = true;
  }
  
  private static void ensureCacheInitializedLocked(ContentResolver paramContentResolver)
  {
    if (sCache == null)
    {
      sInvalidateCache.set(false);
      sCache = new HashMap();
      sVersionToken = new Object();
      sPreloaded = false;
      paramContentResolver.registerContentObserver(CONTENT_URI, true, new ContentObserver(null)
      {
        public void onChange(boolean paramAnonymousBoolean)
        {
          Gservices.sInvalidateCache.set(true);
        }
      });
    }
    while (!sInvalidateCache.getAndSet(false)) {
      return;
    }
    sCache.clear();
    sBooleanCache.clear();
    sIntCache.clear();
    sLongCache.clear();
    sFloatCache.clear();
    sVersionToken = new Object();
    sPreloaded = false;
  }
  
  public static boolean getBoolean(ContentResolver paramContentResolver, String paramString, boolean paramBoolean)
  {
    Object localObject = getVersionToken(paramContentResolver);
    Boolean localBoolean = (Boolean)getValue(sBooleanCache, paramString, Boolean.valueOf(paramBoolean));
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    paramContentResolver = getString(paramContentResolver, paramString);
    if ((paramContentResolver == null) || (paramContentResolver.equals(""))) {
      paramContentResolver = localBoolean;
    }
    for (;;)
    {
      putValueAndRemoveFromStringCache(localObject, sBooleanCache, paramString, paramContentResolver);
      return paramBoolean;
      if (TRUE_PATTERN.matcher(paramContentResolver).matches())
      {
        paramBoolean = true;
        paramContentResolver = Boolean.valueOf(true);
      }
      else if (FALSE_PATTERN.matcher(paramContentResolver).matches())
      {
        paramBoolean = false;
        paramContentResolver = Boolean.valueOf(false);
      }
      else
      {
        Log.w("Gservices", "attempt to read gservices key " + paramString + " (value \"" + paramContentResolver + "\") as boolean");
        paramContentResolver = localBoolean;
      }
    }
  }
  
  public static float getFloat(ContentResolver paramContentResolver, String paramString, float paramFloat)
  {
    Object localObject = getVersionToken(paramContentResolver);
    Float localFloat = (Float)getValue(sFloatCache, paramString, Float.valueOf(paramFloat));
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    paramContentResolver = getString(paramContentResolver, paramString);
    if (paramContentResolver == null) {
      paramContentResolver = localFloat;
    }
    for (;;)
    {
      putValueAndRemoveFromStringCache(localObject, sFloatCache, paramString, paramContentResolver);
      return paramFloat;
      try
      {
        float f = Float.parseFloat(paramContentResolver);
        paramContentResolver = Float.valueOf(f);
        paramFloat = f;
      }
      catch (NumberFormatException paramContentResolver)
      {
        paramContentResolver = localFloat;
      }
    }
  }
  
  public static int getInt(ContentResolver paramContentResolver, String paramString, int paramInt)
  {
    Object localObject = getVersionToken(paramContentResolver);
    Integer localInteger = (Integer)getValue(sIntCache, paramString, Integer.valueOf(paramInt));
    if (localInteger != null) {
      return localInteger.intValue();
    }
    paramContentResolver = getString(paramContentResolver, paramString);
    if (paramContentResolver == null) {
      paramContentResolver = localInteger;
    }
    for (;;)
    {
      putValueAndRemoveFromStringCache(localObject, sIntCache, paramString, paramContentResolver);
      return paramInt;
      try
      {
        int i = Integer.parseInt(paramContentResolver);
        paramContentResolver = Integer.valueOf(i);
        paramInt = i;
      }
      catch (NumberFormatException paramContentResolver)
      {
        paramContentResolver = localInteger;
      }
    }
  }
  
  public static long getLong(ContentResolver paramContentResolver, String paramString, long paramLong)
  {
    Object localObject = getVersionToken(paramContentResolver);
    Long localLong = (Long)getValue(sLongCache, paramString, Long.valueOf(paramLong));
    if (localLong != null) {
      return localLong.longValue();
    }
    paramContentResolver = getString(paramContentResolver, paramString);
    if (paramContentResolver == null) {
      paramContentResolver = localLong;
    }
    for (;;)
    {
      putValueAndRemoveFromStringCache(localObject, sLongCache, paramString, paramContentResolver);
      return paramLong;
      try
      {
        long l = Long.parseLong(paramContentResolver);
        paramContentResolver = Long.valueOf(l);
        paramLong = l;
      }
      catch (NumberFormatException paramContentResolver)
      {
        paramContentResolver = localLong;
      }
    }
  }
  
  @Deprecated
  public static String getString(ContentResolver paramContentResolver, String paramString)
  {
    return getString(paramContentResolver, paramString, null);
  }
  
  public static String getString(ContentResolver paramContentResolver, String paramString1, String paramString2)
  {
    for (;;)
    {
      Object localObject2;
      Object localObject1;
      int i;
      try
      {
        ensureCacheInitializedLocked(paramContentResolver);
        localObject2 = sVersionToken;
        if (sCache.containsKey(paramString1))
        {
          paramContentResolver = (String)sCache.get(paramString1);
          if (paramContentResolver != null) {
            paramString2 = paramContentResolver;
          }
          return paramString2;
        }
        localObject1 = sPreloadedPrefixes;
        int j = localObject1.length;
        i = 0;
        if (i >= j) {
          break label138;
        }
        if (!paramString1.startsWith(localObject1[i])) {
          break label276;
        }
        if ((!sPreloaded) || (sCache.isEmpty()))
        {
          bulkCacheByPrefixLocked(paramContentResolver, sPreloadedPrefixes);
          if (sCache.containsKey(paramString1))
          {
            paramContentResolver = (String)sCache.get(paramString1);
            if (paramContentResolver != null) {
              paramString2 = paramContentResolver;
            }
            return paramString2;
          }
        }
      }
      finally {}
      return paramString2;
      label138:
      Cursor localCursor = paramContentResolver.query(CONTENT_URI, null, null, new String[] { paramString1 }, null);
      if (localCursor != null) {}
      try
      {
        if (!localCursor.moveToFirst())
        {
          putStringCache(localObject2, paramString1, null);
          return paramString2;
        }
        localObject1 = localCursor.getString(1);
        paramContentResolver = (ContentResolver)localObject1;
        if (localObject1 != null)
        {
          paramContentResolver = (ContentResolver)localObject1;
          if (((String)localObject1).equals(paramString2)) {
            paramContentResolver = paramString2;
          }
        }
        putStringCache(localObject2, paramString1, paramContentResolver);
        if (paramContentResolver != null) {}
        for (;;)
        {
          return paramContentResolver;
          paramContentResolver = paramString2;
        }
        return paramString2;
      }
      finally
      {
        if (localCursor != null) {
          localCursor.close();
        }
      }
      label276:
      i += 1;
    }
  }
  
  public static Map<String, String> getStringsByPrefix(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    paramContentResolver = paramContentResolver.query(CONTENT_PREFIX_URI, null, null, paramVarArgs, null);
    paramVarArgs = new TreeMap();
    if (paramContentResolver == null) {
      return paramVarArgs;
    }
    try
    {
      if (paramContentResolver.moveToNext()) {
        paramVarArgs.put(paramContentResolver.getString(0), paramContentResolver.getString(1));
      }
      return paramVarArgs;
    }
    finally
    {
      paramContentResolver.close();
    }
  }
  
  private static <T> T getValue(HashMap<String, T> paramHashMap, String paramString, T paramT)
  {
    for (;;)
    {
      try
      {
        if (paramHashMap.containsKey(paramString))
        {
          paramHashMap = paramHashMap.get(paramString);
          if (paramHashMap != null) {
            return paramHashMap;
          }
        }
        else
        {
          return null;
        }
      }
      finally {}
      paramHashMap = paramT;
    }
  }
  
  public static Object getVersionToken(ContentResolver paramContentResolver)
  {
    try
    {
      ensureCacheInitializedLocked(paramContentResolver);
      paramContentResolver = sVersionToken;
      return paramContentResolver;
    }
    finally {}
  }
  
  private static void putStringCache(Object paramObject, String paramString1, String paramString2)
  {
    try
    {
      if (paramObject == sVersionToken) {
        sCache.put(paramString1, paramString2);
      }
      return;
    }
    finally {}
  }
  
  private static <T> void putValueAndRemoveFromStringCache(Object paramObject, HashMap<String, T> paramHashMap, String paramString, T paramT)
  {
    try
    {
      if (paramObject == sVersionToken)
      {
        paramHashMap.put(paramString, paramT);
        sCache.remove(paramString);
      }
      return;
    }
    finally {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/Gservices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */