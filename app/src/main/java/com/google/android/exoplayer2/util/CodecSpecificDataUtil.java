package com.google.android.exoplayer2.util;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;

public final class CodecSpecificDataUtil
{
  private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
  private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
  private static final int AUDIO_OBJECT_TYPE_PS = 29;
  private static final int AUDIO_OBJECT_TYPE_SBR = 5;
  private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
  private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = { 0, 1, 2, 3, 4, 5, 6, 8, -1, -1, -1, 7, 8, -1, 8, -1 };
  private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
  private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
  private static final byte[] NAL_START_CODE = { 0, 0, 0, 1 };
  
  static
  {
    AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[] { 96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350 };
  }
  
  public static byte[] buildAacAudioSpecificConfig(int paramInt1, int paramInt2, int paramInt3)
  {
    return new byte[] { (byte)(paramInt1 << 3 & 0xF8 | paramInt2 >> 1 & 0x7), (byte)(paramInt2 << 7 & 0x80 | paramInt3 << 3 & 0x78) };
  }
  
  public static byte[] buildAacLcAudioSpecificConfig(int paramInt1, int paramInt2)
  {
    int j = -1;
    int i = 0;
    while (i < AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE.length)
    {
      if (paramInt1 == AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i]) {
        j = i;
      }
      i += 1;
    }
    int k = -1;
    i = 0;
    while (i < AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE.length)
    {
      if (paramInt2 == AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[i]) {
        k = i;
      }
      i += 1;
    }
    if ((paramInt1 == -1) || (k == -1)) {
      throw new IllegalArgumentException("Invalid sample rate or number of channels: " + paramInt1 + ", " + paramInt2);
    }
    return buildAacAudioSpecificConfig(2, j, k);
  }
  
  public static byte[] buildNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[NAL_START_CODE.length + paramInt2];
    System.arraycopy(NAL_START_CODE, 0, arrayOfByte, 0, NAL_START_CODE.length);
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, NAL_START_CODE.length, paramInt2);
    return arrayOfByte;
  }
  
  private static int findNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte.length;
    int j = NAL_START_CODE.length;
    while (paramInt <= i - j)
    {
      if (isNalStartCode(paramArrayOfByte, paramInt)) {
        return paramInt;
      }
      paramInt += 1;
    }
    return -1;
  }
  
  private static boolean isNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte.length - paramInt <= NAL_START_CODE.length) {
      return false;
    }
    int i = 0;
    for (;;)
    {
      if (i >= NAL_START_CODE.length) {
        break label43;
      }
      if (paramArrayOfByte[(paramInt + i)] != NAL_START_CODE[i]) {
        break;
      }
      i += 1;
    }
    label43:
    return true;
  }
  
  public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] paramArrayOfByte)
  {
    boolean bool2 = true;
    paramArrayOfByte = new ParsableBitArray(paramArrayOfByte);
    int n = paramArrayOfByte.readBits(5);
    int i = paramArrayOfByte.readBits(4);
    int j;
    if (i == 15)
    {
      i = paramArrayOfByte.readBits(24);
      int m = paramArrayOfByte.readBits(4);
      int k;
      if (n != 5)
      {
        k = m;
        j = i;
        if (n != 29) {}
      }
      else
      {
        i = paramArrayOfByte.readBits(4);
        if (i != 15) {
          break label166;
        }
        i = paramArrayOfByte.readBits(24);
        k = m;
        j = i;
        if (paramArrayOfByte.readBits(5) == 22)
        {
          k = paramArrayOfByte.readBits(4);
          j = i;
        }
      }
      i = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[k];
      if (i == -1) {
        break label195;
      }
    }
    label166:
    label195:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Assertions.checkArgument(bool1);
      return Pair.create(Integer.valueOf(j), Integer.valueOf(i));
      if (i < 13) {}
      for (bool1 = true;; bool1 = false)
      {
        Assertions.checkArgument(bool1);
        i = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i];
        break;
      }
      if (i < 13) {}
      for (bool1 = true;; bool1 = false)
      {
        Assertions.checkArgument(bool1);
        i = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i];
        break;
      }
    }
  }
  
  public static byte[][] splitNalUnits(byte[] paramArrayOfByte)
  {
    if (!isNalStartCode(paramArrayOfByte, 0)) {
      return (byte[][])null;
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j;
    do
    {
      localArrayList.add(Integer.valueOf(i));
      j = findNalStartCode(paramArrayOfByte, NAL_START_CODE.length + i);
      i = j;
    } while (j != -1);
    byte[][] arrayOfByte = new byte[localArrayList.size()][];
    i = 0;
    if (i < localArrayList.size())
    {
      int k = ((Integer)localArrayList.get(i)).intValue();
      if (i < localArrayList.size() - 1) {}
      for (j = ((Integer)localArrayList.get(i + 1)).intValue();; j = paramArrayOfByte.length)
      {
        byte[] arrayOfByte1 = new byte[j - k];
        System.arraycopy(paramArrayOfByte, k, arrayOfByte1, 0, arrayOfByte1.length);
        arrayOfByte[i] = arrayOfByte1;
        i += 1;
        break;
      }
    }
    return arrayOfByte;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/CodecSpecificDataUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */