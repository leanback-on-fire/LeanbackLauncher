package com.google.android.exoplayer2.extractor;

public final class MpegAudioHeader
{
  private static final int[] BITRATE_V1_L1;
  private static final int[] BITRATE_V1_L2 = { 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384 };
  private static final int[] BITRATE_V1_L3 = { 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320 };
  private static final int[] BITRATE_V2 = { 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160 };
  private static final int[] BITRATE_V2_L1;
  public static final int MAX_FRAME_SIZE_BYTES = 4096;
  private static final String[] MIME_TYPE_BY_LAYER = { "audio/mpeg-L1", "audio/mpeg-L2", "audio/mpeg" };
  private static final int[] SAMPLING_RATE_V1 = { 44100, 48000, 32000 };
  public int bitrate;
  public int channels;
  public int frameSize;
  public String mimeType;
  public int sampleRate;
  public int samplesPerFrame;
  public int version;
  
  static
  {
    BITRATE_V1_L1 = new int[] { 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448 };
    BITRATE_V2_L1 = new int[] { 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256 };
  }
  
  public static int getFrameSize(int paramInt)
  {
    if ((paramInt & 0xFFE00000) != -2097152) {}
    int m;
    int n;
    int i1;
    int i;
    do
    {
      do
      {
        do
        {
          do
          {
            return -1;
            m = paramInt >>> 19 & 0x3;
          } while (m == 1);
          n = paramInt >>> 17 & 0x3;
        } while (n == 0);
        i1 = paramInt >>> 12 & 0xF;
      } while ((i1 == 0) || (i1 == 15));
      i = paramInt >>> 10 & 0x3;
    } while (i == 3);
    int j = SAMPLING_RATE_V1[i];
    int k;
    if (m == 2)
    {
      i = j / 2;
      k = paramInt >>> 9 & 0x1;
      if (n != 3) {
        break label153;
      }
      if (m != 3) {
        break label141;
      }
    }
    label141:
    for (paramInt = BITRATE_V1_L1[(i1 - 1)];; paramInt = BITRATE_V2_L1[(i1 - 1)])
    {
      return (paramInt * 12000 / i + k) * 4;
      i = j;
      if (m != 0) {
        break;
      }
      i = j / 4;
      break;
    }
    label153:
    if (m == 3) {
      if (n == 2) {
        paramInt = BITRATE_V1_L2[(i1 - 1)];
      }
    }
    while (m == 3)
    {
      return 144000 * paramInt / i + k;
      paramInt = BITRATE_V1_L3[(i1 - 1)];
      continue;
      paramInt = BITRATE_V2[(i1 - 1)];
    }
    if (n == 1) {}
    for (j = 72000;; j = 144000) {
      return j * paramInt / i + k;
    }
  }
  
  public static boolean populateHeader(int paramInt, MpegAudioHeader paramMpegAudioHeader)
  {
    if ((0xFFE00000 & paramInt) != -2097152) {
      return false;
    }
    int n = paramInt >>> 19 & 0x3;
    if (n == 1) {
      return false;
    }
    int i1 = paramInt >>> 17 & 0x3;
    if (i1 == 0) {
      return false;
    }
    int k = paramInt >>> 12 & 0xF;
    if ((k == 0) || (k == 15)) {
      return false;
    }
    int i = paramInt >>> 10 & 0x3;
    if (i == 3) {
      return false;
    }
    int j = SAMPLING_RATE_V1[i];
    int i2;
    int m;
    label125:
    String str;
    if (n == 2)
    {
      i = j / 2;
      i2 = paramInt >>> 9 & 0x1;
      if (i1 != 3) {
        break label214;
      }
      if (n != 3) {
        break label201;
      }
      m = BITRATE_V1_L1[(k - 1)];
      j = (m * 12000 / i + i2) * 4;
      k = 384;
      str = MIME_TYPE_BY_LAYER[(3 - i1)];
      if ((paramInt >> 6 & 0x3) != 3) {
        break label325;
      }
    }
    label201:
    label214:
    label289:
    label319:
    label325:
    for (paramInt = 1;; paramInt = 2)
    {
      paramMpegAudioHeader.setValues(n, str, j, i, paramInt, m * 1000, k);
      return true;
      i = j;
      if (n != 0) {
        break;
      }
      i = j / 4;
      break;
      m = BITRATE_V2_L1[(k - 1)];
      break label125;
      if (n == 3)
      {
        if (i1 == 2) {}
        for (m = BITRATE_V1_L2[(k - 1)];; m = BITRATE_V1_L3[(k - 1)])
        {
          k = 1152;
          j = 144000 * m / i + i2;
          break;
        }
      }
      m = BITRATE_V2[(k - 1)];
      if (i1 == 1)
      {
        k = 576;
        if (i1 != 1) {
          break label319;
        }
      }
      for (j = 72000;; j = 144000)
      {
        j = j * m / i + i2;
        break;
        k = 1152;
        break label289;
      }
    }
  }
  
  private void setValues(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.version = paramInt1;
    this.mimeType = paramString;
    this.frameSize = paramInt2;
    this.sampleRate = paramInt3;
    this.channels = paramInt4;
    this.bitrate = paramInt5;
    this.samplesPerFrame = paramInt6;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/MpegAudioHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */