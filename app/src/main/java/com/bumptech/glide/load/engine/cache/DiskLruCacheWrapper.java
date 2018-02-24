package com.bumptech.glide.load.engine.cache;

import android.util.Log;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.disklrucache.DiskLruCache.Editor;
import com.bumptech.glide.disklrucache.DiskLruCache.Value;
import com.bumptech.glide.load.Key;
import java.io.File;
import java.io.IOException;

public class DiskLruCacheWrapper
  implements DiskCache
{
  private static final int APP_VERSION = 1;
  private static final String TAG = "DiskLruCacheWrapper";
  private static final int VALUE_COUNT = 1;
  private static DiskLruCacheWrapper wrapper = null;
  private final File directory;
  private DiskLruCache diskLruCache;
  private final int maxSize;
  private final SafeKeyGenerator safeKeyGenerator;
  private final DiskCacheWriteLocker writeLocker = new DiskCacheWriteLocker();
  
  protected DiskLruCacheWrapper(File paramFile, int paramInt)
  {
    this.directory = paramFile;
    this.maxSize = paramInt;
    this.safeKeyGenerator = new SafeKeyGenerator();
  }
  
  public static DiskCache get(File paramFile, int paramInt)
  {
    try
    {
      if (wrapper == null) {
        wrapper = new DiskLruCacheWrapper(paramFile, paramInt);
      }
      paramFile = wrapper;
      return paramFile;
    }
    finally {}
  }
  
  private DiskLruCache getDiskCache()
    throws IOException
  {
    try
    {
      if (this.diskLruCache == null) {
        this.diskLruCache = DiskLruCache.open(this.directory, 1, 1, this.maxSize);
      }
      DiskLruCache localDiskLruCache = this.diskLruCache;
      return localDiskLruCache;
    }
    finally {}
  }
  
  private void resetDiskCache()
  {
    try
    {
      this.diskLruCache = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void clear()
  {
    try
    {
      getDiskCache().delete();
      resetDiskCache();
      return;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
          Log.w("DiskLruCacheWrapper", "Unable to clear disk cache", localIOException);
        }
      }
    }
    finally {}
  }
  
  public void delete(Key paramKey)
  {
    paramKey = this.safeKeyGenerator.getSafeKey(paramKey);
    try
    {
      getDiskCache().remove(paramKey);
      return;
    }
    catch (IOException paramKey)
    {
      while (!Log.isLoggable("DiskLruCacheWrapper", 5)) {}
      Log.w("DiskLruCacheWrapper", "Unable to delete from disk cache", paramKey);
    }
  }
  
  public File get(Key paramKey)
  {
    Object localObject2 = this.safeKeyGenerator.getSafeKey(paramKey);
    if (Log.isLoggable("DiskLruCacheWrapper", 2))
    {
      paramKey = String.valueOf(paramKey);
      Log.v("DiskLruCacheWrapper", String.valueOf(localObject2).length() + 29 + String.valueOf(paramKey).length() + "Get: Obtained: " + (String)localObject2 + " for for Key: " + paramKey);
    }
    Object localObject1 = null;
    try
    {
      localObject2 = getDiskCache().get((String)localObject2);
      paramKey = (Key)localObject1;
      if (localObject2 != null) {
        paramKey = ((DiskLruCache.Value)localObject2).getFile(0);
      }
    }
    catch (IOException localIOException)
    {
      do
      {
        paramKey = (Key)localObject1;
      } while (!Log.isLoggable("DiskLruCacheWrapper", 5));
      Log.w("DiskLruCacheWrapper", "Unable to get from disk cache", localIOException);
    }
    return paramKey;
    return null;
  }
  
  public void put(Key paramKey, DiskCache.Writer paramWriter)
  {
    this.writeLocker.acquire(paramKey);
    try
    {
      String str = this.safeKeyGenerator.getSafeKey(paramKey);
      Object localObject;
      if (Log.isLoggable("DiskLruCacheWrapper", 2))
      {
        localObject = String.valueOf(paramKey);
        Log.v("DiskLruCacheWrapper", String.valueOf(str).length() + 29 + String.valueOf(localObject).length() + "Put: Obtained: " + str + " for for Key: " + (String)localObject);
      }
      for (;;)
      {
        try
        {
          localObject = getDiskCache();
          DiskLruCache.Value localValue = ((DiskLruCache)localObject).get(str);
          if (localValue != null) {
            return;
          }
          localObject = ((DiskLruCache)localObject).edit(str);
          if (localObject != null) {
            break label207;
          }
          paramWriter = String.valueOf(str);
          if (paramWriter.length() != 0)
          {
            paramWriter = "Had two simultaneous puts for: ".concat(paramWriter);
            throw new IllegalStateException(paramWriter);
          }
        }
        catch (IOException paramWriter)
        {
          if (Log.isLoggable("DiskLruCacheWrapper", 5)) {
            Log.w("DiskLruCacheWrapper", "Unable to put to disk cache", paramWriter);
          }
          return;
        }
        paramWriter = new String("Had two simultaneous puts for: ");
        continue;
        try
        {
          if (paramWriter.write(((DiskLruCache.Editor)localObject).getFile(0))) {
            ((DiskLruCache.Editor)localObject).commit();
          }
          ((DiskLruCache.Editor)localObject).abortUnlessCommitted();
        }
        finally
        {
          ((DiskLruCache.Editor)localObject).abortUnlessCommitted();
        }
      }
    }
    finally
    {
      this.writeLocker.release(paramKey);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */