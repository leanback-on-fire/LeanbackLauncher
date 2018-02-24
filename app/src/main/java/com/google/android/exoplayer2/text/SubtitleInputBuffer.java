package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.decoder.DecoderInputBuffer;

public final class SubtitleInputBuffer
  extends DecoderInputBuffer
  implements Comparable<SubtitleInputBuffer>
{
  public long subsampleOffsetUs;
  
  public SubtitleInputBuffer()
  {
    super(1);
  }
  
  public int compareTo(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    long l = this.timeUs - paramSubtitleInputBuffer.timeUs;
    if (l == 0L) {
      return 0;
    }
    if (l > 0L) {
      return 1;
    }
    return -1;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/SubtitleInputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */