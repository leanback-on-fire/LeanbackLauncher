package com.google.android.exoplayer2.extractor.mp3;

final class ConstantBitrateSeeker
  implements Mp3Extractor.Seeker
{
  private static final int BITS_PER_BYTE = 8;
  private final int bitrate;
  private final long durationUs;
  private final long firstFramePosition;
  
  public ConstantBitrateSeeker(long paramLong1, int paramInt, long paramLong2)
  {
    this.firstFramePosition = paramLong1;
    this.bitrate = paramInt;
    if (paramLong2 == -1L) {}
    for (paramLong1 = -9223372036854775807L;; paramLong1 = getTimeUs(paramLong2))
    {
      this.durationUs = paramLong1;
      return;
    }
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public long getPosition(long paramLong)
  {
    if (this.durationUs == -9223372036854775807L) {
      return 0L;
    }
    return this.firstFramePosition + this.bitrate * paramLong / 8000000L;
  }
  
  public long getTimeUs(long paramLong)
  {
    return Math.max(0L, paramLong - this.firstFramePosition) * 1000000L * 8L / this.bitrate;
  }
  
  public boolean isSeekable()
  {
    return this.durationUs != -9223372036854775807L;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mp3/ConstantBitrateSeeker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */