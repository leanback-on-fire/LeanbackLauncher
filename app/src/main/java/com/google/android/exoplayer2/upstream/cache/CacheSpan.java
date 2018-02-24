package com.google.android.exoplayer2.upstream.cache;

import java.io.File;

public class CacheSpan
  implements Comparable<CacheSpan>
{
  public final File file;
  public final boolean isCached;
  public final String key;
  public final long lastAccessTimestamp;
  public final long length;
  public final long position;
  
  public CacheSpan(String paramString, long paramLong1, long paramLong2)
  {
    this(paramString, paramLong1, paramLong2, -9223372036854775807L, null);
  }
  
  public CacheSpan(String paramString, long paramLong1, long paramLong2, long paramLong3, File paramFile)
  {
    this.key = paramString;
    this.position = paramLong1;
    this.length = paramLong2;
    if (paramFile != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.isCached = bool;
      this.file = paramFile;
      this.lastAccessTimestamp = paramLong3;
      return;
    }
  }
  
  public int compareTo(CacheSpan paramCacheSpan)
  {
    if (!this.key.equals(paramCacheSpan.key)) {
      return this.key.compareTo(paramCacheSpan.key);
    }
    long l = this.position - paramCacheSpan.position;
    if (l == 0L) {
      return 0;
    }
    if (l < 0L) {
      return -1;
    }
    return 1;
  }
  
  public boolean isHoleSpan()
  {
    return !this.isCached;
  }
  
  public boolean isOpenEnded()
  {
    return this.length == -1L;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/CacheSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */