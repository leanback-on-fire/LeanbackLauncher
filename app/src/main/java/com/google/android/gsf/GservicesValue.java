package com.google.android.gsf;

import android.content.ContentResolver;
import android.content.Context;

public abstract class GservicesValue<T>
{
  private static ContentResolver sContentResolver = null;
  protected final T mDefaultValue;
  protected final String mKey;
  
  protected GservicesValue(String paramString, T paramT)
  {
    this.mKey = paramString;
    this.mDefaultValue = paramT;
  }
  
  public static void init(Context paramContext)
  {
    sContentResolver = paramContext.getContentResolver();
  }
  
  public static GservicesValue<String> partnerSetting(String paramString1, String paramString2)
  {
    new GservicesValue(paramString1, paramString2)
    {
      protected String retrieve(String paramAnonymousString)
      {
        return GoogleSettingsContract.Partner.getString(GservicesValue.sContentResolver, this.mKey, (String)this.mDefaultValue);
      }
    };
  }
  
  public static GservicesValue<Float> value(String paramString, Float paramFloat)
  {
    new GservicesValue(paramString, paramFloat)
    {
      protected Float retrieve(String paramAnonymousString)
      {
        return Float.valueOf(Gservices.getFloat(GservicesValue.sContentResolver, this.mKey, ((Float)this.mDefaultValue).floatValue()));
      }
    };
  }
  
  public static GservicesValue<Integer> value(String paramString, Integer paramInteger)
  {
    new GservicesValue(paramString, paramInteger)
    {
      protected Integer retrieve(String paramAnonymousString)
      {
        return Integer.valueOf(Gservices.getInt(GservicesValue.sContentResolver, this.mKey, ((Integer)this.mDefaultValue).intValue()));
      }
    };
  }
  
  public static GservicesValue<Long> value(String paramString, Long paramLong)
  {
    new GservicesValue(paramString, paramLong)
    {
      protected Long retrieve(String paramAnonymousString)
      {
        return Long.valueOf(Gservices.getLong(GservicesValue.sContentResolver, this.mKey, ((Long)this.mDefaultValue).longValue()));
      }
    };
  }
  
  public static GservicesValue<String> value(String paramString1, String paramString2)
  {
    new GservicesValue(paramString1, paramString2)
    {
      protected String retrieve(String paramAnonymousString)
      {
        return Gservices.getString(GservicesValue.sContentResolver, this.mKey, (String)this.mDefaultValue);
      }
    };
  }
  
  public static GservicesValue<Boolean> value(String paramString, boolean paramBoolean)
  {
    new GservicesValue(paramString, Boolean.valueOf(paramBoolean))
    {
      protected Boolean retrieve(String paramAnonymousString)
      {
        return Boolean.valueOf(Gservices.getBoolean(GservicesValue.sContentResolver, this.mKey, ((Boolean)this.mDefaultValue).booleanValue()));
      }
    };
  }
  
  public final T get()
  {
    return (T)retrieve(this.mKey);
  }
  
  protected abstract T retrieve(String paramString);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/GservicesValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */