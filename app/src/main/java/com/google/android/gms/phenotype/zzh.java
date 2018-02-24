package com.google.android.gms.phenotype;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.internal.zzbtf;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class zzh
{
  public static final String[] zzbum = { "key", "value" };
  private static final ConcurrentHashMap<Uri, zzh> zzcfu = new ConcurrentHashMap();
  private final Uri mUri;
  private final ContentResolver zzcfv;
  private final ContentObserver zzcfw;
  private final Object zzcfx = new Object();
  private volatile Map<String, String> zzcfy;
  
  private zzh(ContentResolver paramContentResolver, Uri paramUri)
  {
    this.zzcfv = paramContentResolver;
    this.mUri = paramUri;
    this.zzcfw = new ContentObserver(null)
    {
      @BinderThread
      public void onChange(boolean paramAnonymousBoolean)
      {
        synchronized (zzh.zza(zzh.this))
        {
          zzh.zza(zzh.this, null);
          return;
        }
      }
    };
  }
  
  public static zzh zza(@NonNull ContentResolver paramContentResolver, @NonNull Uri paramUri)
  {
    zzh localzzh = (zzh)zzcfu.get(paramUri);
    Object localObject = localzzh;
    if (localzzh == null)
    {
      paramContentResolver = new zzh(paramContentResolver, paramUri);
      paramUri = (zzh)zzcfu.putIfAbsent(paramUri, paramContentResolver);
      localObject = paramUri;
      if (paramUri == null)
      {
        paramContentResolver.zzcfv.registerContentObserver(paramContentResolver.mUri, false, paramContentResolver.zzcfw);
        localObject = paramContentResolver;
      }
    }
    return (zzh)localObject;
  }
  
  public Integer getInteger(String paramString, Integer paramInteger)
  {
    String str = getString(paramString, null);
    paramString = paramInteger;
    if (str != null) {}
    try
    {
      int i = Integer.parseInt(str);
      paramString = Integer.valueOf(i);
      return paramString;
    }
    catch (NumberFormatException paramString)
    {
      paramString = String.valueOf(paramString);
      Log.e("PhenotypeCfgPkg", String.valueOf(paramString).length() + 17 + "Invalid integer: " + paramString);
    }
    return paramInteger;
  }
  
  public Long getLong(String paramString, Long paramLong)
  {
    String str = getString(paramString, null);
    paramString = paramLong;
    if (str != null) {}
    try
    {
      long l = Long.parseLong(str);
      paramString = Long.valueOf(l);
      return paramString;
    }
    catch (NumberFormatException paramString)
    {
      paramString = String.valueOf(paramString);
      Log.e("PhenotypeCfgPkg", String.valueOf(paramString).length() + 14 + "Invalid long: " + paramString);
    }
    return paramLong;
  }
  
  public String getString(String paramString1, String paramString2)
  {
    Object localObject2 = this.zzcfy;
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      synchronized (this.zzcfx)
      {
        localObject2 = this.zzcfy;
        localObject1 = localObject2;
        if (localObject2 != null) {
          break label133;
        }
        localObject1 = new HashMap();
        localObject2 = this.zzcfv.query(this.mUri, zzbum, null, null, null);
        if (localObject2 == null) {
          break label128;
        }
      }
      ((Cursor)localObject2).close();
      label128:
      this.zzcfy = ((Map)localObject1);
    }
    label133:
    paramString1 = (String)((Map)localObject1).get(paramString1);
    if (paramString1 == null) {
      return paramString2;
    }
    return paramString1;
  }
  
  public Boolean zza(String paramString, Boolean paramBoolean)
  {
    String str = getString(paramString, null);
    paramString = paramBoolean;
    if (str != null)
    {
      if (!zzbtf.fW.matcher(str).matches()) {
        break label33;
      }
      paramString = Boolean.valueOf(true);
    }
    label33:
    do
    {
      return paramString;
      paramString = paramBoolean;
    } while (!zzbtf.fX.matcher(str).matches());
    return Boolean.valueOf(false);
  }
  
  public Double zza(String paramString, Double paramDouble)
  {
    String str = getString(paramString, null);
    paramString = paramDouble;
    if (str != null) {}
    try
    {
      double d = Double.parseDouble(str);
      paramString = Double.valueOf(d);
      return paramString;
    }
    catch (NumberFormatException paramString)
    {
      paramString = String.valueOf(paramString);
      Log.e("PhenotypeCfgPkg", String.valueOf(paramString).length() + 16 + "Invalid double: " + paramString);
    }
    return paramDouble;
  }
  
  public byte[] zzf(String paramString, byte[] paramArrayOfByte)
  {
    String str = getString(paramString, null);
    paramString = paramArrayOfByte;
    if (str != null) {}
    try
    {
      paramString = Base64.decode(str, 3);
      return paramString;
    }
    catch (IllegalArgumentException paramString)
    {
      paramString = String.valueOf(paramString);
      Log.e("PhenotypeCfgPkg", String.valueOf(paramString).length() + 16 + "Invalid byte[]: " + paramString);
    }
    return paramArrayOfByte;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */