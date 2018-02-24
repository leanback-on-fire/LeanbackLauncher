package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class VbriSeeker
  implements Mp3Extractor.Seeker
{
  private final long durationUs;
  private final long[] positions;
  private final long[] timesUs;
  
  private VbriSeeker(long[] paramArrayOfLong1, long[] paramArrayOfLong2, long paramLong)
  {
    this.timesUs = paramArrayOfLong1;
    this.positions = paramArrayOfLong2;
    this.durationUs = paramLong;
  }
  
  public static VbriSeeker create(MpegAudioHeader paramMpegAudioHeader, ParsableByteArray paramParsableByteArray, long paramLong1, long paramLong2)
  {
    paramParsableByteArray.skipBytes(10);
    int i = paramParsableByteArray.readInt();
    if (i <= 0) {
      return null;
    }
    int j = paramMpegAudioHeader.sampleRate;
    long l1 = i;
    if (j >= 32000) {}
    long l2;
    int k;
    int m;
    long[] arrayOfLong;
    for (i = 1152;; i = 576)
    {
      l2 = Util.scaleLargeTimestamp(l1, i * 1000000L, j);
      k = paramParsableByteArray.readUnsignedShort();
      m = paramParsableByteArray.readUnsignedShort();
      int n = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(2);
      paramLong1 += paramMpegAudioHeader.frameSize;
      paramMpegAudioHeader = new long[k + 1];
      arrayOfLong = new long[k + 1];
      paramMpegAudioHeader[0] = 0L;
      arrayOfLong[0] = paramLong1;
      j = 1;
      if (j >= paramMpegAudioHeader.length) {
        break;
      }
      switch (n)
      {
      default: 
        return null;
      }
    }
    i = paramParsableByteArray.readUnsignedByte();
    label172:
    paramLong1 += i * m;
    paramMpegAudioHeader[j] = (j * l2 / k);
    if (paramLong2 == -1L) {}
    for (l1 = paramLong1;; l1 = Math.min(paramLong2, paramLong1))
    {
      arrayOfLong[j] = l1;
      j += 1;
      break;
      i = paramParsableByteArray.readUnsignedShort();
      break label172;
      i = paramParsableByteArray.readUnsignedInt24();
      break label172;
      i = paramParsableByteArray.readUnsignedIntToInt();
      break label172;
    }
    return new VbriSeeker(paramMpegAudioHeader, arrayOfLong, l2);
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public long getPosition(long paramLong)
  {
    return this.positions[Util.binarySearchFloor(this.timesUs, paramLong, true, true)];
  }
  
  public long getTimeUs(long paramLong)
  {
    return this.timesUs[Util.binarySearchFloor(this.positions, paramLong, true, true)];
  }
  
  public boolean isSeekable()
  {
    return true;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mp3/VbriSeeker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */