package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class XingSeeker
  implements Mp3Extractor.Seeker
{
  private final long durationUs;
  private final long firstFramePosition;
  private final int headerSize;
  private final long inputLength;
  private final long sizeBytes;
  private final long[] tableOfContents;
  
  private XingSeeker(long paramLong1, long paramLong2, long paramLong3)
  {
    this(paramLong1, paramLong2, paramLong3, null, 0L, 0);
  }
  
  private XingSeeker(long paramLong1, long paramLong2, long paramLong3, long[] paramArrayOfLong, long paramLong4, int paramInt)
  {
    this.firstFramePosition = paramLong1;
    this.durationUs = paramLong2;
    this.inputLength = paramLong3;
    this.tableOfContents = paramArrayOfLong;
    this.sizeBytes = paramLong4;
    this.headerSize = paramInt;
  }
  
  public static XingSeeker create(MpegAudioHeader paramMpegAudioHeader, ParsableByteArray paramParsableByteArray, long paramLong1, long paramLong2)
  {
    int i = paramMpegAudioHeader.samplesPerFrame;
    int j = paramMpegAudioHeader.sampleRate;
    paramLong1 += paramMpegAudioHeader.frameSize;
    int k = paramParsableByteArray.readInt();
    int m;
    if ((k & 0x1) == 1)
    {
      m = paramParsableByteArray.readUnsignedIntToInt();
      if (m != 0) {}
    }
    else
    {
      return null;
    }
    long l1 = Util.scaleLargeTimestamp(m, i * 1000000L, j);
    if ((k & 0x6) != 6) {
      return new XingSeeker(paramLong1, l1, paramLong2);
    }
    long l2 = paramParsableByteArray.readUnsignedIntToInt();
    paramParsableByteArray.skipBytes(1);
    long[] arrayOfLong = new long[99];
    i = 0;
    while (i < 99)
    {
      arrayOfLong[i] = paramParsableByteArray.readUnsignedByte();
      i += 1;
    }
    return new XingSeeker(paramLong1, l1, paramLong2, arrayOfLong, l2, paramMpegAudioHeader.frameSize);
  }
  
  private long getTimeUsForTocPosition(int paramInt)
  {
    return this.durationUs * paramInt / 100L;
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public long getPosition(long paramLong)
  {
    if (!isSeekable()) {
      return this.firstFramePosition;
    }
    float f3 = (float)paramLong * 100.0F / (float)this.durationUs;
    float f1;
    long l1;
    long l2;
    if (f3 <= 0.0F)
    {
      f1 = 0.0F;
      l1 = Math.round(0.00390625D * f1 * this.sizeBytes);
      l2 = this.firstFramePosition;
      if (this.inputLength == -1L) {
        break label166;
      }
    }
    label111:
    label159:
    label166:
    for (paramLong = this.inputLength - 1L;; paramLong = this.firstFramePosition - this.headerSize + this.sizeBytes - 1L)
    {
      return Math.min(l1 + l2, paramLong);
      if (f3 >= 100.0F)
      {
        f1 = 256.0F;
        break;
      }
      int i = (int)f3;
      if (i == 0)
      {
        f1 = 0.0F;
        if (i >= 99) {
          break label159;
        }
      }
      for (float f2 = (float)this.tableOfContents[i];; f2 = 256.0F)
      {
        f1 += (f2 - f1) * (f3 - i);
        break;
        f1 = (float)this.tableOfContents[(i - 1)];
        break label111;
      }
    }
  }
  
  public long getTimeUs(long paramLong)
  {
    if ((!isSeekable()) || (paramLong < this.firstFramePosition)) {
      return 0L;
    }
    double d = 256.0D * (paramLong - this.firstFramePosition) / this.sizeBytes;
    int i = Util.binarySearchFloor(this.tableOfContents, d, true, false) + 1;
    long l2 = getTimeUsForTocPosition(i);
    long l1;
    label78:
    long l3;
    if (i == 0)
    {
      paramLong = 0L;
      if (i != 99) {
        break label115;
      }
      l1 = 256L;
      l3 = getTimeUsForTocPosition(i + 1);
      if (l1 != paramLong) {
        break label127;
      }
    }
    label115:
    label127:
    for (paramLong = 0L;; paramLong = ((l3 - l2) * (d - paramLong) / (l1 - paramLong)))
    {
      return l2 + paramLong;
      paramLong = this.tableOfContents[(i - 1)];
      break;
      l1 = this.tableOfContents[i];
      break label78;
    }
  }
  
  public boolean isSeekable()
  {
    return this.tableOfContents != null;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mp3/XingSeeker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */