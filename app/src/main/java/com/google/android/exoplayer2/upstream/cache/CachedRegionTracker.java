package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.extractor.ChunkIndex;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

public final class CachedRegionTracker
  implements Cache.Listener
{
  public static final int CACHED_TO_END = -2;
  public static final int NOT_CACHED = -1;
  private static final String TAG = "CachedRegionTracker";
  private final Cache cache;
  private final String cacheKey;
  private final ChunkIndex chunkIndex;
  private final Region lookupRegion;
  private final TreeSet<Region> regions;
  
  public CachedRegionTracker(Cache paramCache, String paramString, ChunkIndex paramChunkIndex)
  {
    this.cache = paramCache;
    this.cacheKey = paramString;
    this.chunkIndex = paramChunkIndex;
    this.regions = new TreeSet();
    this.lookupRegion = new Region(0L, 0L);
    try
    {
      paramCache = paramCache.addListener(paramString, this);
      if (paramCache != null)
      {
        paramCache = paramCache.descendingIterator();
        while (paramCache.hasNext()) {
          mergeSpan((CacheSpan)paramCache.next());
        }
      }
    }
    finally {}
  }
  
  private void mergeSpan(CacheSpan paramCacheSpan)
  {
    paramCacheSpan = new Region(paramCacheSpan.position, paramCacheSpan.position + paramCacheSpan.length);
    Region localRegion1 = (Region)this.regions.floor(paramCacheSpan);
    Region localRegion2 = (Region)this.regions.ceiling(paramCacheSpan);
    boolean bool = regionsConnect(localRegion1, paramCacheSpan);
    if (regionsConnect(paramCacheSpan, localRegion2))
    {
      if (bool)
      {
        localRegion1.endOffset = localRegion2.endOffset;
        localRegion1.endOffsetIndex = localRegion2.endOffsetIndex;
      }
      for (;;)
      {
        this.regions.remove(localRegion2);
        return;
        paramCacheSpan.endOffset = localRegion2.endOffset;
        paramCacheSpan.endOffsetIndex = localRegion2.endOffsetIndex;
        this.regions.add(paramCacheSpan);
      }
    }
    if (bool)
    {
      localRegion1.endOffset = paramCacheSpan.endOffset;
      i = localRegion1.endOffsetIndex;
      while ((i < this.chunkIndex.length - 1) && (this.chunkIndex.offsets[(i + 1)] <= localRegion1.endOffset)) {
        i += 1;
      }
      localRegion1.endOffsetIndex = i;
      return;
    }
    int j = Arrays.binarySearch(this.chunkIndex.offsets, paramCacheSpan.endOffset);
    int i = j;
    if (j < 0) {
      i = -j - 2;
    }
    paramCacheSpan.endOffsetIndex = i;
    this.regions.add(paramCacheSpan);
  }
  
  private boolean regionsConnect(Region paramRegion1, Region paramRegion2)
  {
    return (paramRegion1 != null) && (paramRegion2 != null) && (paramRegion1.endOffset == paramRegion2.startOffset);
  }
  
  /* Error */
  public int getRegionEndTimeMs(long paramLong)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore 4
    //   3: aload_0
    //   4: monitorenter
    //   5: aload_0
    //   6: getfield 49	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:lookupRegion	Lcom/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region;
    //   9: lload_1
    //   10: putfield 126	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   13: aload_0
    //   14: getfield 44	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   17: aload_0
    //   18: getfield 49	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:lookupRegion	Lcom/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region;
    //   21: invokevirtual 89	java/util/TreeSet:floor	(Ljava/lang/Object;)Ljava/lang/Object;
    //   24: checkcast 8	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   27: astore 5
    //   29: iload 4
    //   31: istore_3
    //   32: aload 5
    //   34: ifnull +30 -> 64
    //   37: iload 4
    //   39: istore_3
    //   40: lload_1
    //   41: aload 5
    //   43: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   46: lcmp
    //   47: ifgt +17 -> 64
    //   50: aload 5
    //   52: getfield 102	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   55: istore_3
    //   56: iload_3
    //   57: iconst_m1
    //   58: if_icmpne +10 -> 68
    //   61: iload 4
    //   63: istore_3
    //   64: aload_0
    //   65: monitorexit
    //   66: iload_3
    //   67: ireturn
    //   68: aload 5
    //   70: getfield 102	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   73: istore_3
    //   74: iload_3
    //   75: aload_0
    //   76: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   79: getfield 113	com/google/android/exoplayer2/extractor/ChunkIndex:length	I
    //   82: iconst_1
    //   83: isub
    //   84: if_icmpne +38 -> 122
    //   87: aload 5
    //   89: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   92: aload_0
    //   93: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   96: getfield 117	com/google/android/exoplayer2/extractor/ChunkIndex:offsets	[J
    //   99: iload_3
    //   100: laload
    //   101: aload_0
    //   102: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   105: getfield 132	com/google/android/exoplayer2/extractor/ChunkIndex:sizes	[I
    //   108: iload_3
    //   109: iaload
    //   110: i2l
    //   111: ladd
    //   112: lcmp
    //   113: ifne +9 -> 122
    //   116: bipush -2
    //   118: istore_3
    //   119: goto -55 -> 64
    //   122: aload_0
    //   123: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   126: getfield 135	com/google/android/exoplayer2/extractor/ChunkIndex:durationsUs	[J
    //   129: iload_3
    //   130: laload
    //   131: aload 5
    //   133: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   136: aload_0
    //   137: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   140: getfield 117	com/google/android/exoplayer2/extractor/ChunkIndex:offsets	[J
    //   143: iload_3
    //   144: laload
    //   145: lsub
    //   146: lmul
    //   147: aload_0
    //   148: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   151: getfield 132	com/google/android/exoplayer2/extractor/ChunkIndex:sizes	[I
    //   154: iload_3
    //   155: iaload
    //   156: i2l
    //   157: ldiv
    //   158: lstore_1
    //   159: aload_0
    //   160: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   163: getfield 138	com/google/android/exoplayer2/extractor/ChunkIndex:timesUs	[J
    //   166: iload_3
    //   167: laload
    //   168: lload_1
    //   169: ladd
    //   170: ldc2_w 139
    //   173: ldiv
    //   174: lstore_1
    //   175: lload_1
    //   176: l2i
    //   177: istore_3
    //   178: goto -114 -> 64
    //   181: astore 5
    //   183: aload_0
    //   184: monitorexit
    //   185: aload 5
    //   187: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	188	0	this	CachedRegionTracker
    //   0	188	1	paramLong	long
    //   31	147	3	i	int
    //   1	61	4	j	int
    //   27	105	5	localRegion	Region
    //   181	5	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	29	181	finally
    //   40	56	181	finally
    //   68	116	181	finally
    //   122	175	181	finally
  }
  
  public void onSpanAdded(Cache paramCache, CacheSpan paramCacheSpan)
  {
    try
    {
      mergeSpan(paramCacheSpan);
      return;
    }
    finally
    {
      paramCache = finally;
      throw paramCache;
    }
  }
  
  /* Error */
  public void onSpanRemoved(Cache paramCache, CacheSpan paramCacheSpan)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 8	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   5: dup
    //   6: aload_2
    //   7: getfield 82	com/google/android/exoplayer2/upstream/cache/CacheSpan:position	J
    //   10: aload_2
    //   11: getfield 82	com/google/android/exoplayer2/upstream/cache/CacheSpan:position	J
    //   14: aload_2
    //   15: getfield 85	com/google/android/exoplayer2/upstream/cache/CacheSpan:length	J
    //   18: ladd
    //   19: invokespecial 47	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:<init>	(JJ)V
    //   22: astore_2
    //   23: aload_0
    //   24: getfield 44	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   27: aload_2
    //   28: invokevirtual 89	java/util/TreeSet:floor	(Ljava/lang/Object;)Ljava/lang/Object;
    //   31: checkcast 8	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   34: astore_1
    //   35: aload_1
    //   36: ifnonnull +14 -> 50
    //   39: ldc 18
    //   41: ldc -111
    //   43: invokestatic 151	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   46: pop
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: aload_0
    //   51: getfield 44	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   54: aload_1
    //   55: invokevirtual 106	java/util/TreeSet:remove	(Ljava/lang/Object;)Z
    //   58: pop
    //   59: aload_1
    //   60: getfield 126	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   63: aload_2
    //   64: getfield 126	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   67: lcmp
    //   68: ifge +67 -> 135
    //   71: new 8	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   74: dup
    //   75: aload_1
    //   76: getfield 126	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   79: aload_2
    //   80: getfield 126	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   83: invokespecial 47	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:<init>	(JJ)V
    //   86: astore 5
    //   88: aload_0
    //   89: getfield 39	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lcom/google/android/exoplayer2/extractor/ChunkIndex;
    //   92: getfield 117	com/google/android/exoplayer2/extractor/ChunkIndex:offsets	[J
    //   95: aload 5
    //   97: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   100: invokestatic 123	java/util/Arrays:binarySearch	([JJ)I
    //   103: istore 4
    //   105: iload 4
    //   107: istore_3
    //   108: iload 4
    //   110: ifge +9 -> 119
    //   113: iload 4
    //   115: ineg
    //   116: iconst_2
    //   117: isub
    //   118: istore_3
    //   119: aload 5
    //   121: iload_3
    //   122: putfield 102	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   125: aload_0
    //   126: getfield 44	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   129: aload 5
    //   131: invokevirtual 109	java/util/TreeSet:add	(Ljava/lang/Object;)Z
    //   134: pop
    //   135: aload_1
    //   136: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   139: aload_2
    //   140: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   143: lcmp
    //   144: ifle -97 -> 47
    //   147: new 8	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   150: dup
    //   151: aload_2
    //   152: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   155: lconst_1
    //   156: ladd
    //   157: aload_1
    //   158: getfield 99	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   161: invokespecial 47	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:<init>	(JJ)V
    //   164: astore_2
    //   165: aload_2
    //   166: aload_1
    //   167: getfield 102	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   170: putfield 102	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   173: aload_0
    //   174: getfield 44	com/google/android/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   177: aload_2
    //   178: invokevirtual 109	java/util/TreeSet:add	(Ljava/lang/Object;)Z
    //   181: pop
    //   182: goto -135 -> 47
    //   185: astore_1
    //   186: aload_0
    //   187: monitorexit
    //   188: aload_1
    //   189: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	190	0	this	CachedRegionTracker
    //   0	190	1	paramCache	Cache
    //   0	190	2	paramCacheSpan	CacheSpan
    //   107	15	3	i	int
    //   103	11	4	j	int
    //   86	44	5	localRegion	Region
    // Exception table:
    //   from	to	target	type
    //   2	35	185	finally
    //   39	47	185	finally
    //   50	105	185	finally
    //   119	135	185	finally
    //   135	182	185	finally
  }
  
  public void onSpanTouched(Cache paramCache, CacheSpan paramCacheSpan1, CacheSpan paramCacheSpan2) {}
  
  public void release()
  {
    this.cache.removeListener(this.cacheKey, this);
  }
  
  private static class Region
    implements Comparable<Region>
  {
    public long endOffset;
    public int endOffsetIndex;
    public long startOffset;
    
    public Region(long paramLong1, long paramLong2)
    {
      this.startOffset = paramLong1;
      this.endOffset = paramLong2;
    }
    
    public int compareTo(Region paramRegion)
    {
      if (this.startOffset < paramRegion.startOffset) {
        return -1;
      }
      if (this.startOffset == paramRegion.startOffset) {
        return 0;
      }
      return 1;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/CachedRegionTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */