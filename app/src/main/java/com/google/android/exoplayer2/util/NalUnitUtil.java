package com.google.android.exoplayer2.util;

import android.util.Log;
import java.nio.ByteBuffer;

public final class NalUnitUtil
{
  public static final float[] ASPECT_RATIO_IDC_VALUES;
  public static final int EXTENDED_SAR = 255;
  public static final byte[] NAL_START_CODE = { 0, 0, 0, 1 };
  private static final int NAL_UNIT_TYPE_SPS = 7;
  private static final String TAG = "NalUnitUtil";
  private static int[] scratchEscapePositions = new int[10];
  private static final Object scratchEscapePositionsLock;
  
  static
  {
    ASPECT_RATIO_IDC_VALUES = new float[] { 1.0F, 1.0F, 1.0909091F, 0.90909094F, 1.4545455F, 1.2121212F, 2.1818182F, 1.8181819F, 2.909091F, 2.4242425F, 1.6363636F, 1.3636364F, 1.939394F, 1.6161616F, 1.3333334F, 1.5F, 2.0F };
    scratchEscapePositionsLock = new Object();
  }
  
  public static void clearPrefixFlags(boolean[] paramArrayOfBoolean)
  {
    paramArrayOfBoolean[0] = false;
    paramArrayOfBoolean[1] = false;
    paramArrayOfBoolean[2] = false;
  }
  
  public static void discardToSps(ByteBuffer paramByteBuffer)
  {
    int m = paramByteBuffer.position();
    int i = 0;
    int k = 0;
    while (k + 1 < m)
    {
      int n = paramByteBuffer.get(k) & 0xFF;
      int j;
      if (i == 3)
      {
        j = i;
        if (n == 1)
        {
          j = i;
          if ((paramByteBuffer.get(k + 1) & 0x1F) == 7)
          {
            ByteBuffer localByteBuffer = paramByteBuffer.duplicate();
            localByteBuffer.position(k - 3);
            localByteBuffer.limit(m);
            paramByteBuffer.position(0);
            paramByteBuffer.put(localByteBuffer);
          }
        }
      }
      else
      {
        j = i;
        if (n == 0) {
          j = i + 1;
        }
      }
      i = j;
      if (n != 0) {
        i = 0;
      }
      k += 1;
    }
    paramByteBuffer.clear();
  }
  
  public static int findNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean[] paramArrayOfBoolean)
  {
    boolean bool2 = true;
    int i = paramInt2 - paramInt1;
    if (i >= 0)
    {
      bool1 = true;
      Assertions.checkState(bool1);
      if (i != 0) {
        break label34;
      }
    }
    label34:
    do
    {
      return paramInt2;
      bool1 = false;
      break;
      if (paramArrayOfBoolean != null)
      {
        if (paramArrayOfBoolean[0] != 0)
        {
          clearPrefixFlags(paramArrayOfBoolean);
          return paramInt1 - 3;
        }
        if ((i > 1) && (paramArrayOfBoolean[1] != 0) && (paramArrayOfByte[paramInt1] == 1))
        {
          clearPrefixFlags(paramArrayOfBoolean);
          return paramInt1 - 2;
        }
        if ((i > 2) && (paramArrayOfBoolean[2] != 0) && (paramArrayOfByte[paramInt1] == 0) && (paramArrayOfByte[(paramInt1 + 1)] == 1))
        {
          clearPrefixFlags(paramArrayOfBoolean);
          return paramInt1 - 1;
        }
      }
      paramInt1 += 2;
      if (paramInt1 < paramInt2 - 1)
      {
        if ((paramArrayOfByte[paramInt1] & 0xFE) != 0) {}
        for (;;)
        {
          paramInt1 += 3;
          break;
          if ((paramArrayOfByte[(paramInt1 - 2)] == 0) && (paramArrayOfByte[(paramInt1 - 1)] == 0) && (paramArrayOfByte[paramInt1] == 1))
          {
            if (paramArrayOfBoolean != null) {
              clearPrefixFlags(paramArrayOfBoolean);
            }
            return paramInt1 - 2;
          }
          paramInt1 -= 2;
        }
      }
    } while (paramArrayOfBoolean == null);
    if (i > 2) {
      if ((paramArrayOfByte[(paramInt2 - 3)] == 0) && (paramArrayOfByte[(paramInt2 - 2)] == 0) && (paramArrayOfByte[(paramInt2 - 1)] == 1))
      {
        bool1 = true;
        paramArrayOfBoolean[0] = bool1;
        if (i <= 1) {
          break label356;
        }
        if ((paramArrayOfByte[(paramInt2 - 2)] != 0) || (paramArrayOfByte[(paramInt2 - 1)] != 0)) {
          break label350;
        }
        bool1 = true;
        label252:
        paramArrayOfBoolean[1] = bool1;
        if (paramArrayOfByte[(paramInt2 - 1)] != 0) {
          break label382;
        }
      }
    }
    label350:
    label356:
    label382:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramArrayOfBoolean[2] = bool1;
      return paramInt2;
      bool1 = false;
      break;
      if (i == 2)
      {
        if ((paramArrayOfBoolean[2] != 0) && (paramArrayOfByte[(paramInt2 - 2)] == 0) && (paramArrayOfByte[(paramInt2 - 1)] == 1))
        {
          bool1 = true;
          break;
        }
        bool1 = false;
        break;
      }
      if ((paramArrayOfBoolean[1] != 0) && (paramArrayOfByte[(paramInt2 - 1)] == 1))
      {
        bool1 = true;
        break;
      }
      bool1 = false;
      break;
      bool1 = false;
      break label252;
      if ((paramArrayOfBoolean[2] != 0) && (paramArrayOfByte[(paramInt2 - 1)] == 0))
      {
        bool1 = true;
        break label252;
      }
      bool1 = false;
      break label252;
    }
  }
  
  private static int findNextUnescapeIndex(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2 - 2)
    {
      if ((paramArrayOfByte[paramInt1] == 0) && (paramArrayOfByte[(paramInt1 + 1)] == 0) && (paramArrayOfByte[(paramInt1 + 2)] == 3)) {
        return paramInt1;
      }
      paramInt1 += 1;
    }
    return paramInt2;
  }
  
  public static int getH265NalUnitType(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[(paramInt + 3)] & 0x7E) >> 1;
  }
  
  public static int getNalUnitType(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[(paramInt + 3)] & 0x1F;
  }
  
  public static PpsData parsePpsNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte = new ParsableNalUnitBitArray(paramArrayOfByte, paramInt1, paramInt2);
    paramArrayOfByte.skipBits(8);
    paramInt1 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    paramInt2 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    paramArrayOfByte.skipBits(1);
    return new PpsData(paramInt1, paramInt2, paramArrayOfByte.readBit());
  }
  
  public static SpsData parseSpsNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte = new ParsableNalUnitBitArray(paramArrayOfByte, paramInt1, paramInt2);
    paramArrayOfByte.skipBits(8);
    paramInt1 = paramArrayOfByte.readBits(8);
    paramArrayOfByte.skipBits(16);
    int n = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    int i = 1;
    boolean bool2 = false;
    boolean bool1 = false;
    if ((paramInt1 == 100) || (paramInt1 == 110) || (paramInt1 == 122) || (paramInt1 == 244) || (paramInt1 == 44) || (paramInt1 == 83) || (paramInt1 == 86) || (paramInt1 == 118) || (paramInt1 == 128) || (paramInt1 == 138))
    {
      j = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      if (j == 3) {
        bool1 = paramArrayOfByte.readBit();
      }
      paramArrayOfByte.readUnsignedExpGolombCodedInt();
      paramArrayOfByte.readUnsignedExpGolombCodedInt();
      paramArrayOfByte.skipBits(1);
      bool2 = bool1;
      i = j;
      if (paramArrayOfByte.readBit())
      {
        if (j != 3)
        {
          paramInt1 = 8;
          paramInt2 = 0;
          label167:
          bool2 = bool1;
          i = j;
          if (paramInt2 >= paramInt1) {
            break label223;
          }
          if (paramArrayOfByte.readBit()) {
            if (paramInt2 >= 6) {
              break label216;
            }
          }
        }
        label216:
        for (i = 16;; i = 64)
        {
          skipScalingList(paramArrayOfByte, i);
          paramInt2 += 1;
          break label167;
          paramInt1 = 12;
          break;
        }
      }
    }
    label223:
    int i1 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    int i2 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    int j = 0;
    bool1 = false;
    boolean bool3;
    label288:
    label378:
    float f2;
    float f1;
    if (i2 == 0)
    {
      paramInt2 = paramArrayOfByte.readUnsignedExpGolombCodedInt() + 4;
      paramArrayOfByte.readUnsignedExpGolombCodedInt();
      paramArrayOfByte.skipBits(1);
      int k = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      j = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      bool3 = paramArrayOfByte.readBit();
      if (!bool3) {
        break label573;
      }
      paramInt1 = 1;
      if (!bool3) {
        paramArrayOfByte.skipBits(1);
      }
      paramArrayOfByte.skipBits(1);
      k = (k + 1) * 16;
      int m = (2 - paramInt1) * (j + 1) * 16;
      j = k;
      paramInt1 = m;
      if (paramArrayOfByte.readBit())
      {
        int i5 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        int i6 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        int i3 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        int i4 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        if (i != 0) {
          break label583;
        }
        i = 1;
        if (!bool3) {
          break label578;
        }
        paramInt1 = 1;
        paramInt1 = 2 - paramInt1;
        j = k - (i5 + i6) * i;
        paramInt1 = m - (i3 + i4) * paramInt1;
      }
      f2 = 1.0F;
      f1 = f2;
      if (paramArrayOfByte.readBit())
      {
        f1 = f2;
        if (paramArrayOfByte.readBit())
        {
          i = paramArrayOfByte.readBits(8);
          if (i != 255) {
            break label643;
          }
          i = paramArrayOfByte.readBits(16);
          k = paramArrayOfByte.readBits(16);
          f1 = f2;
          if (i != 0)
          {
            f1 = f2;
            if (k != 0) {
              f1 = i / k;
            }
          }
        }
      }
    }
    for (;;)
    {
      return new SpsData(n, j, paramInt1, f1, bool2, bool3, i1 + 4, i2, paramInt2, bool1);
      paramInt2 = j;
      if (i2 != 1) {
        break;
      }
      bool3 = paramArrayOfByte.readBit();
      paramArrayOfByte.readSignedExpGolombCodedInt();
      paramArrayOfByte.readSignedExpGolombCodedInt();
      long l = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      paramInt1 = 0;
      for (;;)
      {
        paramInt2 = j;
        bool1 = bool3;
        if (paramInt1 >= l) {
          break;
        }
        paramArrayOfByte.readUnsignedExpGolombCodedInt();
        paramInt1 += 1;
      }
      label573:
      paramInt1 = 0;
      break label288;
      label578:
      paramInt1 = 0;
      break label378;
      label583:
      if (i == 3)
      {
        paramInt1 = 1;
        label591:
        if (i != 1) {
          break label631;
        }
        i = 2;
        label600:
        if (!bool3) {
          break label637;
        }
      }
      label631:
      label637:
      for (j = 1;; j = 0)
      {
        j = i * (2 - j);
        i = paramInt1;
        paramInt1 = j;
        break;
        paramInt1 = 2;
        break label591;
        i = 1;
        break label600;
      }
      label643:
      if (i < ASPECT_RATIO_IDC_VALUES.length)
      {
        f1 = ASPECT_RATIO_IDC_VALUES[i];
      }
      else
      {
        Log.w("NalUnitUtil", "Unexpected aspect_ratio_idc value: " + i);
        f1 = f2;
      }
    }
  }
  
  private static void skipScalingList(ParsableNalUnitBitArray paramParsableNalUnitBitArray, int paramInt)
  {
    int k = 8;
    int m = 8;
    int j = 0;
    if (j < paramInt)
    {
      int i = m;
      if (m != 0) {
        i = (k + paramParsableNalUnitBitArray.readSignedExpGolombCodedInt() + 256) % 256;
      }
      if (i == 0) {}
      for (;;)
      {
        j += 1;
        m = i;
        break;
        k = i;
      }
    }
  }
  
  /* Error */
  public static int unescapeStream(byte[] paramArrayOfByte, int paramInt)
  {
    // Byte code:
    //   0: getstatic 54	com/google/android/exoplayer2/util/NalUnitUtil:scratchEscapePositionsLock	Ljava/lang/Object;
    //   3: astore 8
    //   5: aload 8
    //   7: monitorenter
    //   8: iconst_0
    //   9: istore_2
    //   10: iconst_0
    //   11: istore_3
    //   12: iload_2
    //   13: iload_1
    //   14: if_icmpge +66 -> 80
    //   17: aload_0
    //   18: iload_2
    //   19: iload_1
    //   20: invokestatic 166	com/google/android/exoplayer2/util/NalUnitUtil:findNextUnescapeIndex	([BII)I
    //   23: istore 4
    //   25: iload 4
    //   27: istore_2
    //   28: iload 4
    //   30: iload_1
    //   31: if_icmpge -19 -> 12
    //   34: getstatic 56	com/google/android/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   37: arraylength
    //   38: iload_3
    //   39: if_icmpgt +18 -> 57
    //   42: getstatic 56	com/google/android/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   45: getstatic 56	com/google/android/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   48: arraylength
    //   49: iconst_2
    //   50: imul
    //   51: invokestatic 172	java/util/Arrays:copyOf	([II)[I
    //   54: putstatic 56	com/google/android/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   57: getstatic 56	com/google/android/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   60: astore 9
    //   62: aload 9
    //   64: iload_3
    //   65: iload 4
    //   67: iastore
    //   68: iload 4
    //   70: iconst_3
    //   71: iadd
    //   72: istore_2
    //   73: iload_3
    //   74: iconst_1
    //   75: iadd
    //   76: istore_3
    //   77: goto -65 -> 12
    //   80: iload_1
    //   81: iload_3
    //   82: isub
    //   83: istore 5
    //   85: iconst_0
    //   86: istore_2
    //   87: iconst_0
    //   88: istore 4
    //   90: iconst_0
    //   91: istore_1
    //   92: iload_1
    //   93: iload_3
    //   94: if_icmpge +65 -> 159
    //   97: getstatic 56	com/google/android/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   100: iload_1
    //   101: iaload
    //   102: iload_2
    //   103: isub
    //   104: istore 6
    //   106: aload_0
    //   107: iload_2
    //   108: aload_0
    //   109: iload 4
    //   111: iload 6
    //   113: invokestatic 178	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   116: iload 4
    //   118: iload 6
    //   120: iadd
    //   121: istore 4
    //   123: iload 4
    //   125: iconst_1
    //   126: iadd
    //   127: istore 7
    //   129: aload_0
    //   130: iload 4
    //   132: iconst_0
    //   133: bastore
    //   134: iload 7
    //   136: iconst_1
    //   137: iadd
    //   138: istore 4
    //   140: aload_0
    //   141: iload 7
    //   143: iconst_0
    //   144: bastore
    //   145: iload_2
    //   146: iload 6
    //   148: iconst_3
    //   149: iadd
    //   150: iadd
    //   151: istore_2
    //   152: iload_1
    //   153: iconst_1
    //   154: iadd
    //   155: istore_1
    //   156: goto -64 -> 92
    //   159: aload_0
    //   160: iload_2
    //   161: aload_0
    //   162: iload 4
    //   164: iload 5
    //   166: iload 4
    //   168: isub
    //   169: invokestatic 178	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   172: aload 8
    //   174: monitorexit
    //   175: iload 5
    //   177: ireturn
    //   178: astore_0
    //   179: aload 8
    //   181: monitorexit
    //   182: aload_0
    //   183: athrow
    //   184: astore_0
    //   185: goto -6 -> 179
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	188	0	paramArrayOfByte	byte[]
    //   0	188	1	paramInt	int
    //   9	152	2	i	int
    //   11	84	3	j	int
    //   23	146	4	k	int
    //   83	93	5	m	int
    //   104	46	6	n	int
    //   127	15	7	i1	int
    //   3	177	8	localObject	Object
    //   60	3	9	arrayOfInt	int[]
    // Exception table:
    //   from	to	target	type
    //   17	25	178	finally
    //   34	57	178	finally
    //   57	62	178	finally
    //   97	116	178	finally
    //   159	175	178	finally
    //   179	182	184	finally
  }
  
  public static final class PpsData
  {
    public final boolean bottomFieldPicOrderInFramePresentFlag;
    public final int picParameterSetId;
    public final int seqParameterSetId;
    
    public PpsData(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.picParameterSetId = paramInt1;
      this.seqParameterSetId = paramInt2;
      this.bottomFieldPicOrderInFramePresentFlag = paramBoolean;
    }
  }
  
  public static final class SpsData
  {
    public final boolean deltaPicOrderAlwaysZeroFlag;
    public final boolean frameMbsOnlyFlag;
    public final int frameNumLength;
    public final int height;
    public final int picOrderCntLsbLength;
    public final int picOrderCountType;
    public final float pixelWidthAspectRatio;
    public final boolean separateColorPlaneFlag;
    public final int seqParameterSetId;
    public final int width;
    
    public SpsData(int paramInt1, int paramInt2, int paramInt3, float paramFloat, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean3)
    {
      this.seqParameterSetId = paramInt1;
      this.width = paramInt2;
      this.height = paramInt3;
      this.pixelWidthAspectRatio = paramFloat;
      this.separateColorPlaneFlag = paramBoolean1;
      this.frameMbsOnlyFlag = paramBoolean2;
      this.frameNumLength = paramInt4;
      this.picOrderCountType = paramInt5;
      this.picOrderCntLsbLength = paramInt6;
      this.deltaPicOrderAlwaysZeroFlag = paramBoolean3;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/NalUnitUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */