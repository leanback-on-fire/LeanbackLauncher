package com.google.android.exoplayer2.upstream.cache;

import java.io.File;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.Set;

public abstract interface Cache
{
  public abstract NavigableSet<CacheSpan> addListener(String paramString, Listener paramListener);
  
  public abstract void commitFile(File paramFile)
    throws Cache.CacheException;
  
  public abstract long getCacheSpace();
  
  public abstract NavigableSet<CacheSpan> getCachedSpans(String paramString);
  
  public abstract long getContentLength(String paramString);
  
  public abstract Set<String> getKeys();
  
  public abstract boolean isCached(String paramString, long paramLong1, long paramLong2);
  
  public abstract void releaseHoleSpan(CacheSpan paramCacheSpan);
  
  public abstract void removeListener(String paramString, Listener paramListener);
  
  public abstract void removeSpan(CacheSpan paramCacheSpan)
    throws Cache.CacheException;
  
  public abstract void setContentLength(String paramString, long paramLong)
    throws Cache.CacheException;
  
  public abstract File startFile(String paramString, long paramLong1, long paramLong2)
    throws Cache.CacheException;
  
  public abstract CacheSpan startReadWrite(String paramString, long paramLong)
    throws InterruptedException, Cache.CacheException;
  
  public abstract CacheSpan startReadWriteNonBlocking(String paramString, long paramLong)
    throws Cache.CacheException;
  
  public static class CacheException
    extends IOException
  {
    public CacheException(IOException paramIOException)
    {
      super();
    }
    
    public CacheException(String paramString)
    {
      super();
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onSpanAdded(Cache paramCache, CacheSpan paramCacheSpan);
    
    public abstract void onSpanRemoved(Cache paramCache, CacheSpan paramCacheSpan);
    
    public abstract void onSpanTouched(Cache paramCache, CacheSpan paramCacheSpan1, CacheSpan paramCacheSpan2);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/Cache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */