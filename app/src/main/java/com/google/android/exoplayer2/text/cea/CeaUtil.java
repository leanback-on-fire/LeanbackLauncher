package com.google.android.exoplayer2.text.cea;

import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class CeaUtil
{
  private static final int COUNTRY_CODE = 181;
  private static final int PAYLOAD_TYPE_CC = 4;
  private static final int PROVIDER_CODE = 49;
  private static final int USER_DATA_TYPE_CODE = 3;
  private static final int USER_ID = 1195456820;
  
  public static void consume(long paramLong, ParsableByteArray paramParsableByteArray, TrackOutput paramTrackOutput)
  {
    while (paramParsableByteArray.bytesLeft() > 1)
    {
      int i = 0;
      int k;
      int j;
      do
      {
        k = paramParsableByteArray.readUnsignedByte();
        j = i + k;
        i = j;
      } while (k == 255);
      i = 0;
      int m;
      do
      {
        m = paramParsableByteArray.readUnsignedByte();
        k = i + m;
        i = k;
      } while (m == 255);
      if (isSeiMessageCea608(j, k, paramParsableByteArray))
      {
        paramParsableByteArray.skipBytes(8);
        i = paramParsableByteArray.readUnsignedByte() & 0x1F;
        paramParsableByteArray.skipBytes(1);
        j = i * 3;
        paramTrackOutput.sampleData(paramParsableByteArray, j);
        paramTrackOutput.sampleMetadata(paramLong, 1, j, 0, null);
        paramParsableByteArray.skipBytes(k - (i * 3 + 10));
      }
      else
      {
        paramParsableByteArray.skipBytes(k);
      }
    }
  }
  
  private static boolean isSeiMessageCea608(int paramInt1, int paramInt2, ParsableByteArray paramParsableByteArray)
  {
    if ((paramInt1 != 4) || (paramInt2 < 8)) {}
    int i;
    int j;
    int k;
    do
    {
      return false;
      paramInt1 = paramParsableByteArray.getPosition();
      paramInt2 = paramParsableByteArray.readUnsignedByte();
      i = paramParsableByteArray.readUnsignedShort();
      j = paramParsableByteArray.readInt();
      k = paramParsableByteArray.readUnsignedByte();
      paramParsableByteArray.setPosition(paramInt1);
    } while ((paramInt2 != 181) || (i != 49) || (j != 1195456820) || (k != 3));
    return true;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/cea/CeaUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */