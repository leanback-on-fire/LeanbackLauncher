package com.google.android.exoplayer2.text.tx3g;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class Tx3gDecoder
  extends SimpleSubtitleDecoder
{
  private final ParsableByteArray parsableByteArray = new ParsableByteArray();
  
  public Tx3gDecoder()
  {
    super("Tx3gDecoder");
  }
  
  protected Subtitle decode(byte[] paramArrayOfByte, int paramInt)
  {
    this.parsableByteArray.reset(paramArrayOfByte, paramInt);
    paramInt = this.parsableByteArray.readUnsignedShort();
    if (paramInt == 0) {
      return Tx3gSubtitle.EMPTY;
    }
    return new Tx3gSubtitle(new Cue(this.parsableByteArray.readString(paramInt)));
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/tx3g/Tx3gDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */