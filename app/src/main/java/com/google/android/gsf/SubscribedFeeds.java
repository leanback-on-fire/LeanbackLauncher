package com.google.android.gsf;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class SubscribedFeeds
{
  private static final String SELECT_FEEDS_BY_ID = "_id=?";
  private static final String SELECT_SUBSCRIBED_FEEDS_BY_ACCOUNT_AND_AUTHORITY = "_sync_account=? AND _sync_account_type=? AND authority=?";
  
  public static Uri addFeed(ContentResolver paramContentResolver, String paramString1, Account paramAccount, String paramString2, String paramString3)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("feed", paramString1);
    localContentValues.put("_sync_account", paramAccount.name);
    localContentValues.put("_sync_account_type", paramAccount.type);
    localContentValues.put("authority", paramString2);
    localContentValues.put("service", paramString3);
    return paramContentResolver.insert(Feeds.CONTENT_URI, localContentValues);
  }
  
  public static int deleteFeed(ContentResolver paramContentResolver, String paramString1, Account paramAccount, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("_sync_account=?");
    localStringBuilder.append(" AND _sync_account_type=?");
    localStringBuilder.append(" AND feed=?");
    localStringBuilder.append(" AND authority=?");
    return paramContentResolver.delete(Feeds.CONTENT_URI, localStringBuilder.toString(), new String[] { paramAccount.name, paramAccount.type, paramString1, paramString2 });
  }
  
  public static int deleteFeeds(ContentResolver paramContentResolver, Account paramAccount, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("_sync_account=?");
    localStringBuilder.append(" AND _sync_account_type=?");
    localStringBuilder.append(" AND authority=?");
    return paramContentResolver.delete(Feeds.CONTENT_URI, localStringBuilder.toString(), new String[] { paramAccount.name, paramAccount.type, paramString });
  }
  
  public static boolean manageSubscriptions(ContentResolver paramContentResolver, Account paramAccount, String paramString1, String paramString2, Collection<String> paramCollection)
  {
    HashMap localHashMap = new HashMap();
    Object localObject1 = Feeds.CONTENT_URI;
    Object localObject2 = paramAccount.name;
    String str = paramAccount.type;
    localObject1 = paramContentResolver.query((Uri)localObject1, new String[] { "_id", "feed" }, "_sync_account=? AND _sync_account_type=? AND authority=?", new String[] { localObject2, str, paramString1 }, null);
    if (localObject1 == null) {
      return false;
    }
    long l;
    for (;;)
    {
      try
      {
        if (!((Cursor)localObject1).moveToNext()) {
          break;
        }
        l = ((Cursor)localObject1).getLong(0);
        localObject2 = ((Cursor)localObject1).getString(1);
        if (localHashMap.containsKey(localObject2)) {
          paramContentResolver.delete(Feeds.CONTENT_URI, "_id=?", new String[] { Long.toString(l) });
        } else {
          localHashMap.put(localObject2, Long.valueOf(l));
        }
      }
      finally
      {
        ((Cursor)localObject1).close();
      }
    }
    ((Cursor)localObject1).close();
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      localObject1 = (String)paramCollection.next();
      if (!localHashMap.containsKey(localObject1))
      {
        localObject2 = new ContentValues();
        ((ContentValues)localObject2).put("_sync_account", paramAccount.name);
        ((ContentValues)localObject2).put("_sync_account_type", paramAccount.type);
        ((ContentValues)localObject2).put("feed", (String)localObject1);
        ((ContentValues)localObject2).put("service", paramString2);
        ((ContentValues)localObject2).put("authority", paramString1);
        try
        {
          paramContentResolver.insert(Feeds.CONTENT_URI, (ContentValues)localObject2);
        }
        catch (IllegalArgumentException paramContentResolver)
        {
          return false;
        }
      }
      else
      {
        localHashMap.remove(localObject1);
      }
    }
    paramAccount = localHashMap.entrySet().iterator();
    while (paramAccount.hasNext())
    {
      l = ((Long)((Map.Entry)paramAccount.next()).getValue()).longValue();
      try
      {
        paramContentResolver.delete(ContentUris.withAppendedId(Feeds.CONTENT_URI, l), null, null);
      }
      catch (IllegalArgumentException paramContentResolver)
      {
        return false;
      }
    }
    return true;
  }
  
  public static boolean manageSubscriptions(ContentResolver paramContentResolver, Account paramAccount, String paramString1, String paramString2, String... paramVarArgs)
  {
    ArrayList localArrayList = new ArrayList(paramVarArgs.length);
    int i = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      localArrayList.add(paramVarArgs[i]);
      i += 1;
    }
    return manageSubscriptions(paramContentResolver, paramAccount, paramString1, paramString2, localArrayList);
  }
  
  public static abstract interface AccountColumns
  {
    public static final String _SYNC_ACCOUNT = "_sync_account";
    public static final String _SYNC_ACCOUNT_TYPE = "_sync_account_type";
  }
  
  public static final class Accounts
    implements BaseColumns, SubscribedFeeds.AccountColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/subscribedfeedaccount";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/subscribedfeedaccounts";
    public static final Uri CONTENT_URI = Uri.parse("content://subscribedfeeds/accounts");
    public static final String DEFAULT_SORT_ORDER = "_SYNC_ACCOUNT_TYPE, _SYNC_ACCOUNT ASC";
    
    public static Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString)
    {
      return paramContentResolver.query(CONTENT_URI, paramArrayOfString, null, null, "_SYNC_ACCOUNT_TYPE, _SYNC_ACCOUNT ASC");
    }
    
    public static Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString, String paramString1, String paramString2)
    {
      Uri localUri = CONTENT_URI;
      if (paramString2 == null) {
        paramString2 = "_SYNC_ACCOUNT_TYPE, _SYNC_ACCOUNT ASC";
      }
      for (;;)
      {
        return paramContentResolver.query(localUri, paramArrayOfString, paramString1, null, paramString2);
      }
    }
  }
  
  public static abstract interface FeedColumns
  {
    public static final String AUTHORITY = "authority";
    public static final String FEED = "feed";
    public static final String SERVICE = "service";
    public static final String _SYNC_ACCOUNT = "_sync_account";
    public static final String _SYNC_ACCOUNT_TYPE = "_sync_account_type";
    public static final String _SYNC_DIRTY = "_sync_dirty";
    public static final String _SYNC_ID = "_sync_id";
    public static final String _SYNC_LOCAL_ID = "_sync_local_id";
    public static final String _SYNC_MARK = "_sync_mark";
    public static final String _SYNC_TIME = "_sync_time";
    public static final String _SYNC_VERSION = "_sync_version";
  }
  
  public static final class Feeds
    implements BaseColumns, SubscribedFeeds.FeedColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/subscribedfeed";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/subscribedfeeds";
    public static final Uri CONTENT_URI = Uri.parse("content://subscribedfeeds/feeds");
    public static final String DEFAULT_SORT_ORDER = "_SYNC_ACCOUNT_TYPE, _SYNC_ACCOUNT ASC";
    public static final Uri DELETED_CONTENT_URI = Uri.parse("content://subscribedfeeds/deleted_feeds");
    
    public static Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString)
    {
      return paramContentResolver.query(CONTENT_URI, paramArrayOfString, null, null, "_SYNC_ACCOUNT_TYPE, _SYNC_ACCOUNT ASC");
    }
    
    public static Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
    {
      Uri localUri = CONTENT_URI;
      if (paramString2 == null) {
        paramString2 = "_SYNC_ACCOUNT_TYPE, _SYNC_ACCOUNT ASC";
      }
      for (;;)
      {
        return paramContentResolver.query(localUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/SubscribedFeeds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */