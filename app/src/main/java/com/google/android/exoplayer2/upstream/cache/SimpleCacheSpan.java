package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SimpleCacheSpan
  extends CacheSpan
{
  private static final Pattern CACHE_FILE_PATTERN_V1 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v1\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V2 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v2\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V3 = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.v3\\.exo$", 32);
  private static final String SUFFIX = ".v3.exo";
  
  private SimpleCacheSpan(String paramString, long paramLong1, long paramLong2, long paramLong3, File paramFile)
  {
    super(paramString, paramLong1, paramLong2, paramLong3, paramFile);
  }
  
  public static SimpleCacheSpan createCacheEntry(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    String str2 = paramFile.getName();
    String str1 = str2;
    File localFile = paramFile;
    if (!str2.endsWith(".v3.exo"))
    {
      localFile = upgradeFile(paramFile, paramCachedContentIndex);
      if (localFile != null) {}
    }
    long l;
    do
    {
      do
      {
        return null;
        str1 = localFile.getName();
        paramFile = CACHE_FILE_PATTERN_V3.matcher(str1);
      } while (!paramFile.matches());
      l = localFile.length();
      paramCachedContentIndex = paramCachedContentIndex.getKeyForId(Integer.parseInt(paramFile.group(1)));
    } while (paramCachedContentIndex == null);
    return new SimpleCacheSpan(paramCachedContentIndex, Long.parseLong(paramFile.group(2)), l, Long.parseLong(paramFile.group(3)), localFile);
  }
  
  public static SimpleCacheSpan createClosedHole(String paramString, long paramLong1, long paramLong2)
  {
    return new SimpleCacheSpan(paramString, paramLong1, paramLong2, -9223372036854775807L, null);
  }
  
  public static SimpleCacheSpan createLookup(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }
  
  public static SimpleCacheSpan createOpenHole(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }
  
  public static File getCacheFile(File paramFile, int paramInt, long paramLong1, long paramLong2)
  {
    return new File(paramFile, paramInt + "." + paramLong1 + "." + paramLong2 + ".v3.exo");
  }
  
  private static File upgradeFile(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    Object localObject = paramFile.getName();
    Matcher localMatcher = CACHE_FILE_PATTERN_V2.matcher((CharSequence)localObject);
    if (localMatcher.matches())
    {
      String str = Util.unescapeFileName(localMatcher.group(1));
      localObject = str;
      if (str != null) {
        break label65;
      }
      paramCachedContentIndex = null;
    }
    label65:
    do
    {
      return paramCachedContentIndex;
      localMatcher = CACHE_FILE_PATTERN_V1.matcher((CharSequence)localObject);
      if (!localMatcher.matches()) {
        return null;
      }
      localObject = localMatcher.group(1);
      localObject = getCacheFile(paramFile.getParentFile(), paramCachedContentIndex.assignIdForKey((String)localObject), Long.parseLong(localMatcher.group(2)), Long.parseLong(localMatcher.group(3)));
      paramCachedContentIndex = (CachedContentIndex)localObject;
    } while (paramFile.renameTo((File)localObject));
    return null;
  }
  
  public SimpleCacheSpan copyWithUpdatedLastAccessTime(int paramInt)
  {
    Assertions.checkState(this.isCached);
    long l = System.currentTimeMillis();
    File localFile = getCacheFile(this.file.getParentFile(), paramInt, this.position, l);
    return new SimpleCacheSpan(this.key, this.position, this.length, l, localFile);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/SimpleCacheSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */