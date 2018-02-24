package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.NalUnitUtil.SpsData;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.List;

public final class AvcConfig
{
  public final int height;
  public final List<byte[]> initializationData;
  public final int nalUnitLengthFieldLength;
  public final float pixelWidthAspectRatio;
  public final int width;
  
  private AvcConfig(List<byte[]> paramList, int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    this.initializationData = paramList;
    this.nalUnitLengthFieldLength = paramInt1;
    this.width = paramInt2;
    this.height = paramInt3;
    this.pixelWidthAspectRatio = paramFloat;
  }
  
  private static byte[] buildNalUnitForChild(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readUnsignedShort();
    int j = paramParsableByteArray.getPosition();
    paramParsableByteArray.skipBytes(i);
    return CodecSpecificDataUtil.buildNalUnit(paramParsableByteArray.data, j, i);
  }
  
  public static AvcConfig parse(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    int k;
    try
    {
      paramParsableByteArray.skipBytes(4);
      k = (paramParsableByteArray.readUnsignedByte() & 0x3) + 1;
      if (k == 3) {
        throw new IllegalStateException();
      }
    }
    catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
    {
      throw new ParserException("Error parsing AVC config", paramParsableByteArray);
    }
    ArrayList localArrayList = new ArrayList();
    int m = paramParsableByteArray.readUnsignedByte() & 0x1F;
    int i = 0;
    while (i < m)
    {
      localArrayList.add(buildNalUnitForChild(paramParsableByteArray));
      i += 1;
    }
    int j = paramParsableByteArray.readUnsignedByte();
    i = 0;
    while (i < j)
    {
      localArrayList.add(buildNalUnitForChild(paramParsableByteArray));
      i += 1;
    }
    i = -1;
    j = -1;
    float f = 1.0F;
    if (m > 0)
    {
      paramParsableByteArray = (byte[])localArrayList.get(0);
      paramParsableByteArray = NalUnitUtil.parseSpsNalUnit((byte[])localArrayList.get(0), k, paramParsableByteArray.length);
      i = paramParsableByteArray.width;
      j = paramParsableByteArray.height;
      f = paramParsableByteArray.pixelWidthAspectRatio;
    }
    paramParsableByteArray = new AvcConfig(localArrayList, k, i, j, f);
    return paramParsableByteArray;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/video/AvcConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */