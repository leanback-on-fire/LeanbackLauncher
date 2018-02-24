package com.google.android.gsf;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GoogleSettingsContract
{
  public static final String AUTHORITY = "com.google.settings";
  private static final boolean DEBUG = false;
  private static final String TAG = "GoogleSettings";
  
  public static class NameValueTable
    implements BaseColumns
  {
    public static final String NAME = "name";
    public static final String VALUE = "value";
    static HashMap<Uri, GoogleSettingsContract.UriCacheValue> sCache = new HashMap();
    
    private static GoogleSettingsContract.UriCacheValue ensureCacheInitializedLocked(ContentResolver paramContentResolver, Uri paramUri)
    {
      final GoogleSettingsContract.UriCacheValue localUriCacheValue = (GoogleSettingsContract.UriCacheValue)sCache.get(paramUri);
      if (localUriCacheValue == null)
      {
        localUriCacheValue = new GoogleSettingsContract.UriCacheValue();
        sCache.put(paramUri, localUriCacheValue);
        paramContentResolver.registerContentObserver(paramUri, true, new ContentObserver(null)
        {
          public void onChange(boolean paramAnonymousBoolean)
          {
            localUriCacheValue.invalidateCache.set(true);
          }
        });
        paramContentResolver = localUriCacheValue;
      }
      do
      {
        return paramContentResolver;
        paramContentResolver = localUriCacheValue;
      } while (!localUriCacheValue.invalidateCache.getAndSet(false));
      try
      {
        localUriCacheValue.valueCache.clear();
        localUriCacheValue.versionToken = new Object();
        return localUriCacheValue;
      }
      finally {}
    }
    
    /* Error */
    protected static String getString(ContentResolver paramContentResolver, Uri paramUri, String paramString)
    {
      // Byte code:
      //   0: ldc 2
      //   2: monitorenter
      //   3: aload_0
      //   4: aload_1
      //   5: invokestatic 80	com/google/android/gsf/GoogleSettingsContract$NameValueTable:ensureCacheInitializedLocked	(Landroid/content/ContentResolver;Landroid/net/Uri;)Lcom/google/android/gsf/GoogleSettingsContract$UriCacheValue;
      //   8: astore 7
      //   10: ldc 2
      //   12: monitorexit
      //   13: aload 7
      //   15: monitorenter
      //   16: aload 7
      //   18: getfield 74	com/google/android/gsf/GoogleSettingsContract$UriCacheValue:versionToken	Ljava/lang/Object;
      //   21: astore 8
      //   23: aload 7
      //   25: getfield 67	com/google/android/gsf/GoogleSettingsContract$UriCacheValue:valueCache	Ljava/util/HashMap;
      //   28: aload_2
      //   29: invokevirtual 84	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
      //   32: ifeq +27 -> 59
      //   35: aload 7
      //   37: getfield 67	com/google/android/gsf/GoogleSettingsContract$UriCacheValue:valueCache	Ljava/util/HashMap;
      //   40: aload_2
      //   41: invokevirtual 38	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   44: checkcast 86	java/lang/String
      //   47: astore_0
      //   48: aload 7
      //   50: monitorexit
      //   51: aload_0
      //   52: areturn
      //   53: astore_0
      //   54: ldc 2
      //   56: monitorexit
      //   57: aload_0
      //   58: athrow
      //   59: aload 7
      //   61: monitorexit
      //   62: aconst_null
      //   63: astore 6
      //   65: aconst_null
      //   66: astore 5
      //   68: aconst_null
      //   69: astore_3
      //   70: aload 6
      //   72: astore 4
      //   74: aload_0
      //   75: aload_1
      //   76: iconst_1
      //   77: anewarray 86	java/lang/String
      //   80: dup
      //   81: iconst_0
      //   82: ldc 18
      //   84: aastore
      //   85: ldc 88
      //   87: iconst_1
      //   88: anewarray 86	java/lang/String
      //   91: dup
      //   92: iconst_0
      //   93: aload_2
      //   94: aastore
      //   95: aconst_null
      //   96: invokevirtual 92	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   99: astore_0
      //   100: aload_0
      //   101: ifnull +21 -> 122
      //   104: aload_0
      //   105: astore_3
      //   106: aload 6
      //   108: astore 4
      //   110: aload_0
      //   111: astore 5
      //   113: aload_0
      //   114: invokeinterface 98 1 0
      //   119: ifne +39 -> 158
      //   122: aload_0
      //   123: astore_3
      //   124: aload 6
      //   126: astore 4
      //   128: aload_0
      //   129: astore 5
      //   131: aload 7
      //   133: aload 8
      //   135: aload_2
      //   136: aconst_null
      //   137: invokestatic 102	com/google/android/gsf/GoogleSettingsContract$NameValueTable:putCache	(Lcom/google/android/gsf/GoogleSettingsContract$UriCacheValue;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
      //   140: aload_0
      //   141: ifnull +9 -> 150
      //   144: aload_0
      //   145: invokeinterface 105 1 0
      //   150: aconst_null
      //   151: areturn
      //   152: astore_0
      //   153: aload 7
      //   155: monitorexit
      //   156: aload_0
      //   157: athrow
      //   158: aload_0
      //   159: astore_3
      //   160: aload 6
      //   162: astore 4
      //   164: aload_0
      //   165: astore 5
      //   167: aload_0
      //   168: iconst_0
      //   169: invokeinterface 108 2 0
      //   174: astore 6
      //   176: aload_0
      //   177: astore_3
      //   178: aload 6
      //   180: astore 4
      //   182: aload_0
      //   183: astore 5
      //   185: aload 7
      //   187: aload 8
      //   189: aload_2
      //   190: aload 6
      //   192: invokestatic 102	com/google/android/gsf/GoogleSettingsContract$NameValueTable:putCache	(Lcom/google/android/gsf/GoogleSettingsContract$UriCacheValue;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
      //   195: aload 6
      //   197: astore_1
      //   198: aload_0
      //   199: ifnull +12 -> 211
      //   202: aload_0
      //   203: invokeinterface 105 1 0
      //   208: aload 6
      //   210: astore_1
      //   211: aload_1
      //   212: areturn
      //   213: astore_0
      //   214: aload_3
      //   215: astore 5
      //   217: ldc 110
      //   219: new 112	java/lang/StringBuilder
      //   222: dup
      //   223: invokespecial 113	java/lang/StringBuilder:<init>	()V
      //   226: ldc 115
      //   228: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   231: aload_2
      //   232: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   235: ldc 121
      //   237: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   240: aload_1
      //   241: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   244: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   247: aload_0
      //   248: invokestatic 134	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   251: pop
      //   252: aload 4
      //   254: astore_1
      //   255: aload_3
      //   256: ifnull -45 -> 211
      //   259: aload_3
      //   260: invokeinterface 105 1 0
      //   265: aload 4
      //   267: astore_1
      //   268: goto -57 -> 211
      //   271: astore_0
      //   272: aload 5
      //   274: ifnull +10 -> 284
      //   277: aload 5
      //   279: invokeinterface 105 1 0
      //   284: aload_0
      //   285: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	286	0	paramContentResolver	ContentResolver
      //   0	286	1	paramUri	Uri
      //   0	286	2	paramString	String
      //   69	191	3	localContentResolver	ContentResolver
      //   72	194	4	str1	String
      //   66	212	5	localObject1	Object
      //   63	146	6	str2	String
      //   8	178	7	localUriCacheValue	GoogleSettingsContract.UriCacheValue
      //   21	167	8	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   3	13	53	finally
      //   54	57	53	finally
      //   16	51	152	finally
      //   59	62	152	finally
      //   153	156	152	finally
      //   74	100	213	android/database/SQLException
      //   113	122	213	android/database/SQLException
      //   131	140	213	android/database/SQLException
      //   167	176	213	android/database/SQLException
      //   185	195	213	android/database/SQLException
      //   74	100	271	finally
      //   113	122	271	finally
      //   131	140	271	finally
      //   167	176	271	finally
      //   185	195	271	finally
      //   217	252	271	finally
    }
    
    public static Uri getUriFor(Uri paramUri, String paramString)
    {
      return Uri.withAppendedPath(paramUri, paramString);
    }
    
    /* Error */
    protected static Object getVersionToken(ContentResolver paramContentResolver, Uri paramUri)
    {
      // Byte code:
      //   0: ldc 2
      //   2: monitorenter
      //   3: aload_0
      //   4: aload_1
      //   5: invokestatic 80	com/google/android/gsf/GoogleSettingsContract$NameValueTable:ensureCacheInitializedLocked	(Landroid/content/ContentResolver;Landroid/net/Uri;)Lcom/google/android/gsf/GoogleSettingsContract$UriCacheValue;
      //   8: astore_0
      //   9: ldc 2
      //   11: monitorexit
      //   12: aload_0
      //   13: monitorenter
      //   14: aload_0
      //   15: getfield 74	com/google/android/gsf/GoogleSettingsContract$UriCacheValue:versionToken	Ljava/lang/Object;
      //   18: astore_1
      //   19: aload_0
      //   20: monitorexit
      //   21: aload_1
      //   22: areturn
      //   23: astore_0
      //   24: ldc 2
      //   26: monitorexit
      //   27: aload_0
      //   28: athrow
      //   29: astore_1
      //   30: aload_0
      //   31: monitorexit
      //   32: aload_1
      //   33: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	34	0	paramContentResolver	ContentResolver
      //   0	34	1	paramUri	Uri
      // Exception table:
      //   from	to	target	type
      //   3	12	23	finally
      //   24	27	23	finally
      //   14	21	29	finally
      //   30	32	29	finally
    }
    
    private static void putCache(GoogleSettingsContract.UriCacheValue paramUriCacheValue, Object paramObject, String paramString1, String paramString2)
    {
      try
      {
        if (paramObject == paramUriCacheValue.versionToken) {
          paramUriCacheValue.valueCache.put(paramString1, paramString2);
        }
        return;
      }
      finally {}
    }
    
    /* Error */
    protected static boolean putString(ContentResolver paramContentResolver, Uri paramUri, String paramString1, String paramString2)
    {
      // Byte code:
      //   0: ldc 2
      //   2: monitorenter
      //   3: aload_0
      //   4: aload_1
      //   5: invokestatic 80	com/google/android/gsf/GoogleSettingsContract$NameValueTable:ensureCacheInitializedLocked	(Landroid/content/ContentResolver;Landroid/net/Uri;)Lcom/google/android/gsf/GoogleSettingsContract$UriCacheValue;
      //   8: astore 4
      //   10: ldc 2
      //   12: monitorexit
      //   13: aload 4
      //   15: aload_2
      //   16: invokestatic 151	com/google/android/gsf/GoogleSettingsContract$NameValueTable:removeCachedValue	(Lcom/google/android/gsf/GoogleSettingsContract$UriCacheValue;Ljava/lang/String;)V
      //   19: new 153	android/content/ContentValues
      //   22: dup
      //   23: invokespecial 154	android/content/ContentValues:<init>	()V
      //   26: astore 4
      //   28: aload 4
      //   30: ldc 15
      //   32: aload_2
      //   33: invokevirtual 157	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
      //   36: aload 4
      //   38: ldc 18
      //   40: aload_3
      //   41: invokevirtual 157	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
      //   44: aload_0
      //   45: aload_1
      //   46: aload 4
      //   48: invokevirtual 161	android/content/ContentResolver:insert	(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
      //   51: pop
      //   52: iconst_1
      //   53: ireturn
      //   54: astore_0
      //   55: ldc 2
      //   57: monitorexit
      //   58: aload_0
      //   59: athrow
      //   60: astore_0
      //   61: ldc 110
      //   63: new 112	java/lang/StringBuilder
      //   66: dup
      //   67: invokespecial 113	java/lang/StringBuilder:<init>	()V
      //   70: ldc -93
      //   72: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   75: aload_2
      //   76: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   79: ldc -91
      //   81: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   84: aload_1
      //   85: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   88: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   91: aload_0
      //   92: invokestatic 134	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   95: pop
      //   96: iconst_0
      //   97: ireturn
      //   98: astore_0
      //   99: ldc 110
      //   101: new 112	java/lang/StringBuilder
      //   104: dup
      //   105: invokespecial 113	java/lang/StringBuilder:<init>	()V
      //   108: ldc -93
      //   110: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   113: aload_2
      //   114: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   117: ldc -91
      //   119: invokevirtual 119	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   122: aload_1
      //   123: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   126: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   129: aload_0
      //   130: invokestatic 134	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   133: pop
      //   134: iconst_0
      //   135: ireturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	136	0	paramContentResolver	ContentResolver
      //   0	136	1	paramUri	Uri
      //   0	136	2	paramString1	String
      //   0	136	3	paramString2	String
      //   8	39	4	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   3	13	54	finally
      //   55	58	54	finally
      //   19	52	60	android/database/SQLException
      //   19	52	98	java/lang/IllegalArgumentException
    }
    
    private static void removeCachedValue(GoogleSettingsContract.UriCacheValue paramUriCacheValue, String paramString)
    {
      try
      {
        paramUriCacheValue.valueCache.remove(paramString);
        return;
      }
      finally {}
    }
  }
  
  public static final class Partner
    extends GoogleSettingsContract.NameValueTable
  {
    public static final String CHROME_CLIENT_ID = "chrome_client_id";
    public static final String CLIENT_ID = "client_id";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.settings/partner");
    public static final String DATA_STORE_VERSION = "data_store_version";
    public static final String LOGGING_ID2 = "logging_id2";
    public static final String MAPS_CLIENT_ID = "maps_client_id";
    public static final String MARKET_CHECKIN = "market_checkin";
    public static final String MARKET_CLIENT_ID = "market_client_id";
    public static final String NETWORK_LOCATION_OPT_IN = "network_location_opt_in";
    public static final String RLZ = "rlz";
    public static final String SEARCH_CLIENT_ID = "search_client_id";
    public static final String SHOPPER_CLIENT_ID = "shopper_client_id";
    public static final String USE_LOCATION_FOR_ADS = "use_location_for_ads";
    public static final String USE_LOCATION_FOR_SERVICES = "use_location_for_services";
    public static final String VOICESEARCH_CLIENT_ID = "voicesearch_client_id";
    public static final String WALLET_CLIENT_ID = "wallet_client_id";
    public static final String YOUTUBE_CLIENT_ID = "youtube_client_id";
    
    public static int getInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      if (paramContentResolver != null) {}
      try
      {
        int i = Integer.parseInt(paramContentResolver);
        return i;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramInt;
      return paramInt;
    }
    
    public static long getLong(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      if (paramContentResolver != null) {}
      try
      {
        long l = Long.parseLong(paramContentResolver);
        return l;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramLong;
      return paramLong;
    }
    
    public static String getString(ContentResolver paramContentResolver, String paramString)
    {
      return getString(paramContentResolver, CONTENT_URI, paramString);
    }
    
    public static String getString(ContentResolver paramContentResolver, String paramString1, String paramString2)
    {
      paramString1 = getString(paramContentResolver, paramString1);
      paramContentResolver = paramString1;
      if (paramString1 == null) {
        paramContentResolver = paramString2;
      }
      return paramContentResolver;
    }
    
    public static Uri getUriFor(String paramString)
    {
      return getUriFor(CONTENT_URI, paramString);
    }
    
    public static Object getVersionToken(ContentResolver paramContentResolver)
    {
      return getVersionToken(paramContentResolver, CONTENT_URI);
    }
    
    public static boolean putInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      return putString(paramContentResolver, paramString, String.valueOf(paramInt));
    }
    
    public static boolean putString(ContentResolver paramContentResolver, String paramString1, String paramString2)
    {
      return putString(paramContentResolver, CONTENT_URI, paramString1, paramString2);
    }
  }
  
  static class UriCacheValue
  {
    AtomicBoolean invalidateCache = new AtomicBoolean(false);
    HashMap<String, String> valueCache = new HashMap();
    Object versionToken = new Object();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/GoogleSettingsContract.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */