package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.util.Assertions;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

final class CachedContent
{
  private final TreeSet<SimpleCacheSpan> cachedSpans;
  public final int id;
  public final String key;
  private long length;
  
  public CachedContent(int paramInt, String paramString, long paramLong)
  {
    this.id = paramInt;
    this.key = paramString;
    this.length = paramLong;
    this.cachedSpans = new TreeSet();
  }
  
  public CachedContent(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readInt(), paramDataInputStream.readUTF(), paramDataInputStream.readLong());
  }
  
  private SimpleCacheSpan getSpanInternal(long paramLong)
  {
    SimpleCacheSpan localSimpleCacheSpan2 = SimpleCacheSpan.createLookup(this.key, paramLong);
    SimpleCacheSpan localSimpleCacheSpan3 = (SimpleCacheSpan)this.cachedSpans.floor(localSimpleCacheSpan2);
    SimpleCacheSpan localSimpleCacheSpan1;
    if (localSimpleCacheSpan3 != null)
    {
      localSimpleCacheSpan1 = localSimpleCacheSpan3;
      if (localSimpleCacheSpan3.position + localSimpleCacheSpan3.length > paramLong) {}
    }
    else
    {
      localSimpleCacheSpan1 = localSimpleCacheSpan2;
    }
    return localSimpleCacheSpan1;
  }
  
  public void addSpan(SimpleCacheSpan paramSimpleCacheSpan)
  {
    this.cachedSpans.add(paramSimpleCacheSpan);
  }
  
  public long getLength()
  {
    return this.length;
  }
  
  public SimpleCacheSpan getSpan(long paramLong)
  {
    SimpleCacheSpan localSimpleCacheSpan = getSpanInternal(paramLong);
    if (!localSimpleCacheSpan.isCached)
    {
      localSimpleCacheSpan = (SimpleCacheSpan)this.cachedSpans.ceiling(localSimpleCacheSpan);
      if (localSimpleCacheSpan == null) {
        return SimpleCacheSpan.createOpenHole(this.key, paramLong);
      }
      return SimpleCacheSpan.createClosedHole(this.key, paramLong, localSimpleCacheSpan.position - paramLong);
    }
    return localSimpleCacheSpan;
  }
  
  public TreeSet<SimpleCacheSpan> getSpans()
  {
    return this.cachedSpans;
  }
  
  public int headerHashCode()
  {
    return (this.id * 31 + this.key.hashCode()) * 31 + (int)(this.length ^ this.length >>> 32);
  }
  
  public boolean isCached(long paramLong1, long paramLong2)
  {
    Object localObject = getSpanInternal(paramLong1);
    if (!((SimpleCacheSpan)localObject).isCached) {
      return false;
    }
    long l = paramLong1 + paramLong2;
    paramLong1 = ((SimpleCacheSpan)localObject).position + ((SimpleCacheSpan)localObject).length;
    if (paramLong1 >= l) {
      return true;
    }
    localObject = this.cachedSpans.tailSet(localObject, false).iterator();
    while (((Iterator)localObject).hasNext())
    {
      SimpleCacheSpan localSimpleCacheSpan = (SimpleCacheSpan)((Iterator)localObject).next();
      if (localSimpleCacheSpan.position > paramLong1) {
        return false;
      }
      paramLong2 = Math.max(paramLong1, localSimpleCacheSpan.position + localSimpleCacheSpan.length);
      paramLong1 = paramLong2;
      if (paramLong2 >= l) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    return this.cachedSpans.isEmpty();
  }
  
  public boolean removeSpan(CacheSpan paramCacheSpan)
  {
    if (this.cachedSpans.remove(paramCacheSpan))
    {
      paramCacheSpan.file.delete();
      return true;
    }
    return false;
  }
  
  public void setLength(long paramLong)
  {
    this.length = paramLong;
  }
  
  public SimpleCacheSpan touch(SimpleCacheSpan paramSimpleCacheSpan)
    throws Cache.CacheException
  {
    Assertions.checkState(this.cachedSpans.remove(paramSimpleCacheSpan));
    SimpleCacheSpan localSimpleCacheSpan = paramSimpleCacheSpan.copyWithUpdatedLastAccessTime(this.id);
    if (!paramSimpleCacheSpan.file.renameTo(localSimpleCacheSpan.file)) {
      throw new Cache.CacheException("Renaming of " + paramSimpleCacheSpan.file + " to " + localSimpleCacheSpan.file + " failed.");
    }
    this.cachedSpans.add(localSimpleCacheSpan);
    return localSimpleCacheSpan;
  }
  
  public void writeToStream(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeInt(this.id);
    paramDataOutputStream.writeUTF(this.key);
    paramDataOutputStream.writeLong(this.length);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/CachedContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */