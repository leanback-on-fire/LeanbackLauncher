package com.google.android.exoplayer2.upstream.cache;

public final class NoOpCacheEvictor
  implements CacheEvictor
{
  public void onCacheInitialized() {}
  
  public void onSpanAdded(Cache paramCache, CacheSpan paramCacheSpan) {}
  
  public void onSpanRemoved(Cache paramCache, CacheSpan paramCacheSpan) {}
  
  public void onSpanTouched(Cache paramCache, CacheSpan paramCacheSpan1, CacheSpan paramCacheSpan2) {}
  
  public void onStartFile(Cache paramCache, String paramString, long paramLong1, long paramLong2) {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/NoOpCacheEvictor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */