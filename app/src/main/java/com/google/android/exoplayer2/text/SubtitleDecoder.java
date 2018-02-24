package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.decoder.Decoder;

public abstract interface SubtitleDecoder
  extends Decoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException>
{
  public abstract void setPositionUs(long paramLong);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/SubtitleDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */