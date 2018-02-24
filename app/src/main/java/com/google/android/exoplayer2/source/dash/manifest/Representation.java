package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.dash.DashSegmentIndex;
import java.util.Collections;
import java.util.List;

public abstract class Representation
{
  public static final long REVISION_ID_DEFAULT = -1L;
  public final String baseUrl;
  public final String contentId;
  public final Format format;
  public final List<SchemeValuePair> inbandEventStreams;
  private final RangedUri initializationUri;
  public final long presentationTimeOffsetUs;
  public final long revisionId;
  
  private Representation(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase paramSegmentBase, List<SchemeValuePair> paramList)
  {
    this.contentId = paramString1;
    this.revisionId = paramLong;
    this.format = paramFormat;
    this.baseUrl = paramString2;
    if (paramList == null) {}
    for (paramString1 = Collections.emptyList();; paramString1 = Collections.unmodifiableList(paramList))
    {
      this.inbandEventStreams = paramString1;
      this.initializationUri = paramSegmentBase.getInitialization(this);
      this.presentationTimeOffsetUs = paramSegmentBase.getPresentationTimeOffsetUs();
      return;
    }
  }
  
  public static Representation newInstance(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase paramSegmentBase)
  {
    return newInstance(paramString1, paramLong, paramFormat, paramString2, paramSegmentBase, null);
  }
  
  public static Representation newInstance(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase paramSegmentBase, List<SchemeValuePair> paramList)
  {
    return newInstance(paramString1, paramLong, paramFormat, paramString2, paramSegmentBase, paramList, null);
  }
  
  public static Representation newInstance(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase paramSegmentBase, List<SchemeValuePair> paramList, String paramString3)
  {
    if ((paramSegmentBase instanceof SegmentBase.SingleSegmentBase)) {
      return new SingleSegmentRepresentation(paramString1, paramLong, paramFormat, paramString2, (SegmentBase.SingleSegmentBase)paramSegmentBase, paramList, paramString3, -1L);
    }
    if ((paramSegmentBase instanceof SegmentBase.MultiSegmentBase)) {
      return new MultiSegmentRepresentation(paramString1, paramLong, paramFormat, paramString2, (SegmentBase.MultiSegmentBase)paramSegmentBase, paramList);
    }
    throw new IllegalArgumentException("segmentBase must be of type SingleSegmentBase or MultiSegmentBase");
  }
  
  public abstract String getCacheKey();
  
  public abstract DashSegmentIndex getIndex();
  
  public abstract RangedUri getIndexUri();
  
  public RangedUri getInitializationUri()
  {
    return this.initializationUri;
  }
  
  public static class MultiSegmentRepresentation
    extends Representation
    implements DashSegmentIndex
  {
    private final SegmentBase.MultiSegmentBase segmentBase;
    
    public MultiSegmentRepresentation(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase.MultiSegmentBase paramMultiSegmentBase, List<SchemeValuePair> paramList)
    {
      super(paramLong, paramFormat, paramString2, paramMultiSegmentBase, paramList, null);
      this.segmentBase = paramMultiSegmentBase;
    }
    
    public String getCacheKey()
    {
      return null;
    }
    
    public long getDurationUs(int paramInt, long paramLong)
    {
      return this.segmentBase.getSegmentDurationUs(paramInt, paramLong);
    }
    
    public int getFirstSegmentNum()
    {
      return this.segmentBase.getFirstSegmentNum();
    }
    
    public DashSegmentIndex getIndex()
    {
      return this;
    }
    
    public RangedUri getIndexUri()
    {
      return null;
    }
    
    public int getLastSegmentNum(long paramLong)
    {
      return this.segmentBase.getLastSegmentNum(paramLong);
    }
    
    public int getSegmentNum(long paramLong1, long paramLong2)
    {
      return this.segmentBase.getSegmentNum(paramLong1, paramLong2);
    }
    
    public RangedUri getSegmentUrl(int paramInt)
    {
      return this.segmentBase.getSegmentUrl(this, paramInt);
    }
    
    public long getTimeUs(int paramInt)
    {
      return this.segmentBase.getSegmentTimeUs(paramInt);
    }
    
    public boolean isExplicit()
    {
      return this.segmentBase.isExplicit();
    }
  }
  
  public static class SingleSegmentRepresentation
    extends Representation
  {
    private final String cacheKey;
    public final long contentLength;
    private final RangedUri indexUri;
    private final SingleSegmentIndex segmentIndex;
    public final Uri uri;
    
    public SingleSegmentRepresentation(String paramString1, long paramLong1, Format paramFormat, String paramString2, SegmentBase.SingleSegmentBase paramSingleSegmentBase, List<SchemeValuePair> paramList, String paramString3, long paramLong2)
    {
      super(paramLong1, paramFormat, paramString2, paramSingleSegmentBase, paramList, null);
      this.uri = Uri.parse(paramString2);
      this.indexUri = paramSingleSegmentBase.getIndex();
      if (paramString3 != null)
      {
        this.cacheKey = paramString3;
        this.contentLength = paramLong2;
        if (this.indexUri == null) {
          break label116;
        }
      }
      label116:
      for (paramString1 = null;; paramString1 = new SingleSegmentIndex(new RangedUri(null, 0L, paramLong2)))
      {
        this.segmentIndex = paramString1;
        return;
        if (paramString1 != null)
        {
          paramString3 = paramString1 + "." + paramFormat.id + "." + paramLong1;
          break;
        }
        paramString3 = null;
        break;
      }
    }
    
    public static SingleSegmentRepresentation newInstance(String paramString1, long paramLong1, Format paramFormat, String paramString2, long paramLong2, long paramLong3, long paramLong4, long paramLong5, List<SchemeValuePair> paramList, String paramString3, long paramLong6)
    {
      return new SingleSegmentRepresentation(paramString1, paramLong1, paramFormat, paramString2, new SegmentBase.SingleSegmentBase(new RangedUri(null, paramLong2, paramLong3 - paramLong2 + 1L), 1L, 0L, paramLong4, 1L + (paramLong5 - paramLong4)), paramList, paramString3, paramLong6);
    }
    
    public String getCacheKey()
    {
      return this.cacheKey;
    }
    
    public DashSegmentIndex getIndex()
    {
      return this.segmentIndex;
    }
    
    public RangedUri getIndexUri()
    {
      return this.indexUri;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/Representation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */