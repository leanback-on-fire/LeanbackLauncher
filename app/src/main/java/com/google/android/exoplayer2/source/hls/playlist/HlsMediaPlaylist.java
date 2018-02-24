package com.google.android.exoplayer2.source.hls.playlist;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

public final class HlsMediaPlaylist
  extends HlsPlaylist
{
  public static final int PLAYLIST_TYPE_EVENT = 2;
  public static final int PLAYLIST_TYPE_UNKNOWN = 0;
  public static final int PLAYLIST_TYPE_VOD = 1;
  public final int discontinuitySequence;
  public final long durationUs;
  public final boolean hasDiscontinuitySequence;
  public final boolean hasEndTag;
  public final boolean hasProgramDateTime;
  public final Segment initializationSegment;
  public final int mediaSequence;
  public final int playlistType;
  public final List<Segment> segments;
  public final long startOffsetUs;
  public final long startTimeUs;
  public final long targetDurationUs;
  public final int version;
  
  public HlsMediaPlaylist(int paramInt1, String paramString, long paramLong1, long paramLong2, boolean paramBoolean1, int paramInt2, int paramInt3, int paramInt4, long paramLong3, boolean paramBoolean2, boolean paramBoolean3, Segment paramSegment, List<Segment> paramList)
  {
    super(paramString, 1);
    this.playlistType = paramInt1;
    this.startTimeUs = paramLong2;
    this.hasDiscontinuitySequence = paramBoolean1;
    this.discontinuitySequence = paramInt2;
    this.mediaSequence = paramInt3;
    this.version = paramInt4;
    this.targetDurationUs = paramLong3;
    this.hasEndTag = paramBoolean2;
    this.hasProgramDateTime = paramBoolean3;
    this.initializationSegment = paramSegment;
    this.segments = Collections.unmodifiableList(paramList);
    if (!paramList.isEmpty())
    {
      paramString = (Segment)paramList.get(paramList.size() - 1);
      this.durationUs = (paramString.relativeStartTimeUs + paramString.durationUs);
      if (paramLong1 != -9223372036854775807L) {
        break label145;
      }
      paramLong2 = -9223372036854775807L;
    }
    for (;;)
    {
      this.startOffsetUs = paramLong2;
      return;
      this.durationUs = 0L;
      break;
      label145:
      paramLong2 = paramLong1;
      if (paramLong1 < 0L) {
        paramLong2 = paramLong1 + this.durationUs;
      }
    }
  }
  
  public HlsMediaPlaylist copyWith(long paramLong, int paramInt)
  {
    return new HlsMediaPlaylist(this.playlistType, this.baseUri, this.startOffsetUs, paramLong, true, paramInt, this.mediaSequence, this.version, this.targetDurationUs, this.hasEndTag, this.hasProgramDateTime, this.initializationSegment, this.segments);
  }
  
  public HlsMediaPlaylist copyWithEndTag()
  {
    if (this.hasEndTag) {
      return this;
    }
    return new HlsMediaPlaylist(this.playlistType, this.baseUri, this.startOffsetUs, this.startTimeUs, this.hasDiscontinuitySequence, this.discontinuitySequence, this.mediaSequence, this.version, this.targetDurationUs, true, this.hasProgramDateTime, this.initializationSegment, this.segments);
  }
  
  public long getEndTimeUs()
  {
    return this.startTimeUs + this.durationUs;
  }
  
  public boolean isNewerThan(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    boolean bool2 = false;
    boolean bool1;
    if ((paramHlsMediaPlaylist == null) || (this.mediaSequence > paramHlsMediaPlaylist.mediaSequence)) {
      bool1 = true;
    }
    do
    {
      do
      {
        int i;
        int j;
        do
        {
          do
          {
            return bool1;
            bool1 = bool2;
          } while (this.mediaSequence < paramHlsMediaPlaylist.mediaSequence);
          i = this.segments.size();
          j = paramHlsMediaPlaylist.segments.size();
          if (i > j) {
            break;
          }
          bool1 = bool2;
        } while (i != j);
        bool1 = bool2;
      } while (!this.hasEndTag);
      bool1 = bool2;
    } while (paramHlsMediaPlaylist.hasEndTag);
    return true;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface PlaylistType {}
  
  public static final class Segment
    implements Comparable<Long>
  {
    public final long byterangeLength;
    public final long byterangeOffset;
    public final long durationUs;
    public final String encryptionIV;
    public final String encryptionKeyUri;
    public final boolean isEncrypted;
    public final int relativeDiscontinuitySequence;
    public final long relativeStartTimeUs;
    public final String url;
    
    public Segment(String paramString1, long paramLong1, int paramInt, long paramLong2, boolean paramBoolean, String paramString2, String paramString3, long paramLong3, long paramLong4)
    {
      this.url = paramString1;
      this.durationUs = paramLong1;
      this.relativeDiscontinuitySequence = paramInt;
      this.relativeStartTimeUs = paramLong2;
      this.isEncrypted = paramBoolean;
      this.encryptionKeyUri = paramString2;
      this.encryptionIV = paramString3;
      this.byterangeOffset = paramLong3;
      this.byterangeLength = paramLong4;
    }
    
    public Segment(String paramString, long paramLong1, long paramLong2)
    {
      this(paramString, 0L, -1, -9223372036854775807L, false, null, null, paramLong1, paramLong2);
    }
    
    public int compareTo(Long paramLong)
    {
      if (this.relativeStartTimeUs > paramLong.longValue()) {
        return 1;
      }
      if (this.relativeStartTimeUs < paramLong.longValue()) {
        return -1;
      }
      return 0;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/playlist/HlsMediaPlaylist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */