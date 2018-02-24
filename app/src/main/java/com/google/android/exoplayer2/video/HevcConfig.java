package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Collections;
import java.util.List;

public final class HevcConfig
{
  public final List<byte[]> initializationData;
  public final int nalUnitLengthFieldLength;
  
  private HevcConfig(List<byte[]> paramList, int paramInt)
  {
    this.initializationData = paramList;
    this.nalUnitLengthFieldLength = paramInt;
  }
  
  public static HevcConfig parse(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    for (;;)
    {
      int i;
      int j;
      try
      {
        paramParsableByteArray.skipBytes(21);
        int n = paramParsableByteArray.readUnsignedByte();
        int i1 = paramParsableByteArray.readUnsignedByte();
        i = 0;
        int m = paramParsableByteArray.getPosition();
        j = 0;
        if (j < i1)
        {
          paramParsableByteArray.skipBytes(1);
          i2 = paramParsableByteArray.readUnsignedShort();
          k = 0;
          if (k >= i2) {
            break label223;
          }
          i3 = paramParsableByteArray.readUnsignedShort();
          i += i3 + 4;
          paramParsableByteArray.skipBytes(i3);
          k += 1;
          continue;
        }
        paramParsableByteArray.setPosition(m);
        arrayOfByte = new byte[i];
        m = 0;
        j = 0;
        if (j >= i1) {
          break label237;
        }
        paramParsableByteArray.skipBytes(1);
        int i2 = paramParsableByteArray.readUnsignedShort();
        int k = 0;
        if (k >= i2) {
          break label230;
        }
        int i3 = paramParsableByteArray.readUnsignedShort();
        System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, arrayOfByte, m, NalUnitUtil.NAL_START_CODE.length);
        m += NalUnitUtil.NAL_START_CODE.length;
        System.arraycopy(paramParsableByteArray.data, paramParsableByteArray.getPosition(), arrayOfByte, m, i3);
        m += i3;
        paramParsableByteArray.skipBytes(i3);
        k += 1;
        continue;
        return new HevcConfig(paramParsableByteArray, (n & 0x3) + 1);
      }
      catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
      {
        byte[] arrayOfByte;
        throw new ParserException("Error parsing HEVC config", paramParsableByteArray);
      }
      paramParsableByteArray = Collections.singletonList(arrayOfByte);
      continue;
      label223:
      j += 1;
      continue;
      label230:
      j += 1;
      continue;
      label237:
      if (i == 0) {
        paramParsableByteArray = null;
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/video/HevcConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */