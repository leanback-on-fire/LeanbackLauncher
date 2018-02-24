package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.util.List;
import java.util.UUID;

public class SsManifest
{
  public static final int UNSET_LOOKAHEAD = -1;
  public final long durationUs;
  public final long dvrWindowLengthUs;
  public final boolean isLive;
  public final int lookAheadCount;
  public final int majorVersion;
  public final int minorVersion;
  public final ProtectionElement protectionElement;
  public final StreamElement[] streamElements;
  
  public SsManifest(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, int paramInt3, boolean paramBoolean, ProtectionElement paramProtectionElement, StreamElement[] paramArrayOfStreamElement)
  {
    this.majorVersion = paramInt1;
    this.minorVersion = paramInt2;
    this.lookAheadCount = paramInt3;
    this.isLive = paramBoolean;
    this.protectionElement = paramProtectionElement;
    this.streamElements = paramArrayOfStreamElement;
    if (paramLong3 == 0L)
    {
      paramLong3 = -9223372036854775807L;
      this.dvrWindowLengthUs = paramLong3;
      if (paramLong2 != 0L) {
        break label87;
      }
    }
    label87:
    for (paramLong1 = -9223372036854775807L;; paramLong1 = Util.scaleLargeTimestamp(paramLong2, 1000000L, paramLong1))
    {
      this.durationUs = paramLong1;
      return;
      paramLong3 = Util.scaleLargeTimestamp(paramLong3, 1000000L, paramLong1);
      break;
    }
  }
  
  public static class ProtectionElement
  {
    public final byte[] data;
    public final UUID uuid;
    
    public ProtectionElement(UUID paramUUID, byte[] paramArrayOfByte)
    {
      this.uuid = paramUUID;
      this.data = paramArrayOfByte;
    }
  }
  
  public static class StreamElement
  {
    private static final String URL_PLACEHOLDER_BITRATE = "{bitrate}";
    private static final String URL_PLACEHOLDER_START_TIME = "{start time}";
    private final String baseUri;
    public final int chunkCount;
    private final List<Long> chunkStartTimes;
    private final long[] chunkStartTimesUs;
    private final String chunkTemplate;
    public final int displayHeight;
    public final int displayWidth;
    public final Format[] formats;
    public final String language;
    private final long lastChunkDurationUs;
    public final int maxHeight;
    public final int maxWidth;
    public final String name;
    public final String subType;
    public final long timescale;
    public final int type;
    
    public StreamElement(String paramString1, String paramString2, int paramInt1, String paramString3, long paramLong1, String paramString4, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString5, Format[] paramArrayOfFormat, List<Long> paramList, long paramLong2)
    {
      this.baseUri = paramString1;
      this.chunkTemplate = paramString2;
      this.type = paramInt1;
      this.subType = paramString3;
      this.timescale = paramLong1;
      this.name = paramString4;
      this.maxWidth = paramInt2;
      this.maxHeight = paramInt3;
      this.displayWidth = paramInt4;
      this.displayHeight = paramInt5;
      this.language = paramString5;
      this.formats = paramArrayOfFormat;
      this.chunkCount = paramList.size();
      this.chunkStartTimes = paramList;
      this.lastChunkDurationUs = Util.scaleLargeTimestamp(paramLong2, 1000000L, paramLong1);
      this.chunkStartTimesUs = Util.scaleLargeTimestamps(paramList, 1000000L, paramLong1);
    }
    
    public Uri buildRequestUri(int paramInt1, int paramInt2)
    {
      boolean bool2 = true;
      if (this.formats != null)
      {
        bool1 = true;
        Assertions.checkState(bool1);
        if (this.chunkStartTimes == null) {
          break label108;
        }
        bool1 = true;
        label25:
        Assertions.checkState(bool1);
        if (paramInt2 >= this.chunkStartTimes.size()) {
          break label113;
        }
      }
      label108:
      label113:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        Assertions.checkState(bool1);
        String str = this.chunkTemplate.replace("{bitrate}", Integer.toString(this.formats[paramInt1].bitrate)).replace("{start time}", ((Long)this.chunkStartTimes.get(paramInt2)).toString());
        return UriUtil.resolveToUri(this.baseUri, str);
        bool1 = false;
        break;
        bool1 = false;
        break label25;
      }
    }
    
    public long getChunkDurationUs(int paramInt)
    {
      if (paramInt == this.chunkCount - 1) {
        return this.lastChunkDurationUs;
      }
      return this.chunkStartTimesUs[(paramInt + 1)] - this.chunkStartTimesUs[paramInt];
    }
    
    public int getChunkIndex(long paramLong)
    {
      return Util.binarySearchFloor(this.chunkStartTimesUs, paramLong, true, true);
    }
    
    public long getStartTimeUs(int paramInt)
    {
      return this.chunkStartTimesUs[paramInt];
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/smoothstreaming/manifest/SsManifest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */