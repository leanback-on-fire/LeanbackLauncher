package android.support.v7.app;

import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.LongSparseArray;
import java.lang.reflect.Field;
import java.util.Map;

class ResourcesFlusher
{
  private static final String TAG = "ResourcesFlusher";
  private static Field sDrawableCacheField;
  private static boolean sDrawableCacheFieldFetched;
  private static Field sResourcesImplField;
  private static boolean sResourcesImplFieldFetched;
  private static Class sThemedResourceCacheClazz;
  private static boolean sThemedResourceCacheClazzFetched;
  private static Field sThemedResourceCache_mUnthemedEntriesField;
  private static boolean sThemedResourceCache_mUnthemedEntriesFieldFetched;
  
  static boolean flush(@NonNull Resources paramResources)
  {
    if (Build.VERSION.SDK_INT >= 24) {
      return flushNougats(paramResources);
    }
    if (Build.VERSION.SDK_INT >= 23) {
      return flushMarshmallows(paramResources);
    }
    if (Build.VERSION.SDK_INT >= 21) {
      return flushLollipops(paramResources);
    }
    return false;
  }
  
  @RequiresApi(21)
  private static boolean flushLollipops(@NonNull Resources paramResources)
  {
    if (!sDrawableCacheFieldFetched) {}
    try
    {
      sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
      sDrawableCacheField.setAccessible(true);
      sDrawableCacheFieldFetched = true;
      if (sDrawableCacheField != null) {
        Object localObject = null;
      }
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      try
      {
        paramResources = (Map)sDrawableCacheField.get(paramResources);
        if (paramResources != null)
        {
          paramResources.clear();
          return true;
          localNoSuchFieldException = localNoSuchFieldException;
          Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", localNoSuchFieldException);
        }
      }
      catch (IllegalAccessException paramResources)
      {
        for (;;)
        {
          Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", paramResources);
          paramResources = localNoSuchFieldException;
        }
      }
    }
    return false;
  }
  
  @RequiresApi(23)
  private static boolean flushMarshmallows(@NonNull Resources paramResources)
  {
    boolean bool = true;
    if (!sDrawableCacheFieldFetched) {}
    Object localObject2;
    try
    {
      sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
      sDrawableCacheField.setAccessible(true);
      sDrawableCacheFieldFetched = true;
      localObject3 = null;
      localObject1 = localObject3;
      if (sDrawableCacheField == null) {}
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      try
      {
        Object localObject1 = sDrawableCacheField.get(paramResources);
        if (localObject1 == null)
        {
          return false;
          localNoSuchFieldException = localNoSuchFieldException;
          Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", localNoSuchFieldException);
        }
      }
      catch (IllegalAccessException paramResources)
      {
        for (;;)
        {
          Object localObject3;
          Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", paramResources);
          localObject2 = localObject3;
        }
        if (localObject2 == null) {
          break label94;
        }
      }
    }
    if (flushThemedResourcesCache(localObject2)) {}
    for (;;)
    {
      return bool;
      label94:
      bool = false;
    }
  }
  
  @RequiresApi(24)
  private static boolean flushNougats(@NonNull Resources paramResources)
  {
    boolean bool = true;
    if (!sResourcesImplFieldFetched) {}
    for (;;)
    {
      Object localObject1;
      try
      {
        sResourcesImplField = Resources.class.getDeclaredField("mResourcesImpl");
        sResourcesImplField.setAccessible(true);
        sResourcesImplFieldFetched = true;
        if (sResourcesImplField == null) {
          return false;
        }
      }
      catch (NoSuchFieldException localNoSuchFieldException1)
      {
        Log.e("ResourcesFlusher", "Could not retrieve Resources#mResourcesImpl field", localNoSuchFieldException1);
        continue;
        localObject1 = null;
      }
      try
      {
        paramResources = sResourcesImplField.get(paramResources);
        if (paramResources == null) {
          continue;
        }
        if (sDrawableCacheFieldFetched) {}
      }
      catch (IllegalAccessException localNoSuchFieldException2)
      {
        try
        {
          sDrawableCacheField = paramResources.getClass().getDeclaredField("mDrawableCache");
          sDrawableCacheField.setAccessible(true);
          sDrawableCacheFieldFetched = true;
          localObject3 = null;
          localObject1 = localObject3;
          if (sDrawableCacheField == null) {}
        }
        catch (NoSuchFieldException localNoSuchFieldException2)
        {
          try
          {
            for (;;)
            {
              localObject1 = sDrawableCacheField.get(paramResources);
              if ((localObject1 == null) || (!flushThemedResourcesCache(localObject1))) {
                break;
              }
              return bool;
              paramResources = paramResources;
              Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mResourcesImpl", paramResources);
              paramResources = (Resources)localObject1;
            }
            localNoSuchFieldException2 = localNoSuchFieldException2;
            Log.e("ResourcesFlusher", "Could not retrieve ResourcesImpl#mDrawableCache field", localNoSuchFieldException2);
          }
          catch (IllegalAccessException paramResources)
          {
            for (;;)
            {
              Object localObject3;
              Log.e("ResourcesFlusher", "Could not retrieve value from ResourcesImpl#mDrawableCache", paramResources);
              Object localObject2 = localObject3;
              continue;
              bool = false;
            }
          }
        }
      }
    }
  }
  
  @RequiresApi(16)
  private static boolean flushThemedResourcesCache(@NonNull Object paramObject)
  {
    if (!sThemedResourceCacheClazzFetched) {}
    try
    {
      sThemedResourceCacheClazz = Class.forName("android.content.res.ThemedResourceCache");
      sThemedResourceCacheClazzFetched = true;
      if (sThemedResourceCacheClazz == null) {
        return false;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;)
      {
        Log.e("ResourcesFlusher", "Could not find ThemedResourceCache class", localClassNotFoundException);
      }
      if (!sThemedResourceCache_mUnthemedEntriesFieldFetched) {}
      try
      {
        sThemedResourceCache_mUnthemedEntriesField = sThemedResourceCacheClazz.getDeclaredField("mUnthemedEntries");
        sThemedResourceCache_mUnthemedEntriesField.setAccessible(true);
        sThemedResourceCache_mUnthemedEntriesFieldFetched = true;
        if (sThemedResourceCache_mUnthemedEntriesField == null) {
          return false;
        }
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;)
        {
          Log.e("ResourcesFlusher", "Could not retrieve ThemedResourceCache#mUnthemedEntries field", localNoSuchFieldException);
        }
        Object localObject = null;
        try
        {
          paramObject = (LongSparseArray)sThemedResourceCache_mUnthemedEntriesField.get(paramObject);
          if (paramObject != null)
          {
            ((LongSparseArray)paramObject).clear();
            return true;
          }
        }
        catch (IllegalAccessException paramObject)
        {
          for (;;)
          {
            Log.e("ResourcesFlusher", "Could not retrieve value from ThemedResourceCache#mUnthemedEntries", (Throwable)paramObject);
            paramObject = localObject;
          }
        }
      }
    }
    return false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/app/ResourcesFlusher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */