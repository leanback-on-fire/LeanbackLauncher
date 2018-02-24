package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Util;
import java.util.List;

public abstract class SegmentBase
{
  final RangedUri initialization;
  final long presentationTimeOffset;
  final long timescale;
  
  public SegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2)
  {
    this.initialization = paramRangedUri;
    this.timescale = paramLong1;
    this.presentationTimeOffset = paramLong2;
  }
  
  public RangedUri getInitialization(Representation paramRepresentation)
  {
    return this.initialization;
  }
  
  public long getPresentationTimeOffsetUs()
  {
    return Util.scaleLargeTimestamp(this.presentationTimeOffset, 1000000L, this.timescale);
  }
  
  public static abstract class MultiSegmentBase
    extends SegmentBase
  {
    final long duration;
    final List<SegmentBase.SegmentTimelineElement> segmentTimeline;
    final int startNumber;
    
    public MultiSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList)
    {
      super(paramLong1, paramLong2);
      this.startNumber = paramInt;
      this.duration = paramLong3;
      this.segmentTimeline = paramList;
    }
    
    public int getFirstSegmentNum()
    {
      return this.startNumber;
    }
    
    public abstract int getLastSegmentNum(long paramLong);
    
    public final long getSegmentDurationUs(int paramInt, long paramLong)
    {
      if (this.segmentTimeline != null) {
        return ((SegmentBase.SegmentTimelineElement)this.segmentTimeline.get(paramInt - this.startNumber)).duration * 1000000L / this.timescale;
      }
      if (paramInt == getLastSegmentNum(paramLong)) {
        return paramLong - getSegmentTimeUs(paramInt);
      }
      return this.duration * 1000000L / this.timescale;
    }
    
    public int getSegmentNum(long paramLong1, long paramLong2)
    {
      int m = getFirstSegmentNum();
      int k = m;
      int n = getLastSegmentNum(paramLong2);
      int i = n;
      int j = k;
      if (this.segmentTimeline == null)
      {
        paramLong2 = this.duration * 1000000L / this.timescale;
        j = this.startNumber + (int)(paramLong1 / paramLong2);
        if (j < k) {
          i = k;
        }
        do
        {
          do
          {
            return i;
            i = j;
          } while (n == -1);
          i = j;
        } while (j <= n);
        return n;
      }
      while (j <= i)
      {
        k = j + (i - j) / 2;
        paramLong2 = getSegmentTimeUs(k);
        if (paramLong2 < paramLong1) {
          j = k + 1;
        } else if (paramLong2 > paramLong1) {
          i = k - 1;
        } else {
          return k;
        }
      }
      if (j == m) {}
      for (;;)
      {
        return j;
        j = i;
      }
    }
    
    public final long getSegmentTimeUs(int paramInt)
    {
      if (this.segmentTimeline != null) {}
      for (long l = ((SegmentBase.SegmentTimelineElement)this.segmentTimeline.get(paramInt - this.startNumber)).startTime - this.presentationTimeOffset;; l = (paramInt - this.startNumber) * this.duration) {
        return Util.scaleLargeTimestamp(l, 1000000L, this.timescale);
      }
    }
    
    public abstract RangedUri getSegmentUrl(Representation paramRepresentation, int paramInt);
    
    public boolean isExplicit()
    {
      return this.segmentTimeline != null;
    }
  }
  
  public static class SegmentList
    extends SegmentBase.MultiSegmentBase
  {
    final List<RangedUri> mediaSegments;
    
    public SegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, List<RangedUri> paramList1)
    {
      super(paramLong1, paramLong2, paramInt, paramLong3, paramList);
      this.mediaSegments = paramList1;
    }
    
    public int getLastSegmentNum(long paramLong)
    {
      return this.startNumber + this.mediaSegments.size() - 1;
    }
    
    public RangedUri getSegmentUrl(Representation paramRepresentation, int paramInt)
    {
      return (RangedUri)this.mediaSegments.get(paramInt - this.startNumber);
    }
    
    public boolean isExplicit()
    {
      return true;
    }
  }
  
  public static class SegmentTemplate
    extends SegmentBase.MultiSegmentBase
  {
    final UrlTemplate initializationTemplate;
    final UrlTemplate mediaTemplate;
    
    public SegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2)
    {
      super(paramLong1, paramLong2, paramInt, paramLong3, paramList);
      this.initializationTemplate = paramUrlTemplate1;
      this.mediaTemplate = paramUrlTemplate2;
    }
    
    public RangedUri getInitialization(Representation paramRepresentation)
    {
      if (this.initializationTemplate != null) {
        return new RangedUri(this.initializationTemplate.buildUri(paramRepresentation.format.id, 0, paramRepresentation.format.bitrate, 0L), 0L, -1L);
      }
      return super.getInitialization(paramRepresentation);
    }
    
    public int getLastSegmentNum(long paramLong)
    {
      if (this.segmentTimeline != null) {
        return this.segmentTimeline.size() + this.startNumber - 1;
      }
      if (paramLong == -9223372036854775807L) {
        return -1;
      }
      long l = this.duration * 1000000L / this.timescale;
      return this.startNumber + (int)Util.ceilDivide(paramLong, l) - 1;
    }
    
    public RangedUri getSegmentUrl(Representation paramRepresentation, int paramInt)
    {
      if (this.segmentTimeline != null) {}
      for (long l = ((SegmentBase.SegmentTimelineElement)this.segmentTimeline.get(paramInt - this.startNumber)).startTime;; l = (paramInt - this.startNumber) * this.duration) {
        return new RangedUri(this.mediaTemplate.buildUri(paramRepresentation.format.id, paramInt, paramRepresentation.format.bitrate, l), 0L, -1L);
      }
    }
  }
  
  public static class SegmentTimelineElement
  {
    final long duration;
    final long startTime;
    
    public SegmentTimelineElement(long paramLong1, long paramLong2)
    {
      this.startTime = paramLong1;
      this.duration = paramLong2;
    }
  }
  
  public static class SingleSegmentBase
    extends SegmentBase
  {
    final long indexLength;
    final long indexStart;
    
    public SingleSegmentBase()
    {
      this(null, 1L, 0L, 0L, 0L);
    }
    
    public SingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    {
      super(paramLong1, paramLong2);
      this.indexStart = paramLong3;
      this.indexLength = paramLong4;
    }
    
    public RangedUri getIndex()
    {
      if (this.indexLength <= 0L) {
        return null;
      }
      return new RangedUri(null, this.indexStart, this.indexLength);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/SegmentBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */