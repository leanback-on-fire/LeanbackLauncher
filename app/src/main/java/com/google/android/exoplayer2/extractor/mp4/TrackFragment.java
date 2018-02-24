package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

final class TrackFragment
{
  public long atomPosition;
  public long auxiliaryDataPosition;
  public long dataPosition;
  public boolean definesEncryptionData;
  public DefaultSampleValues header;
  public long nextFragmentDecodeTime;
  public int[] sampleCompositionTimeOffsetTable;
  public int sampleCount;
  public long[] sampleDecodingTimeTable;
  public ParsableByteArray sampleEncryptionData;
  public int sampleEncryptionDataLength;
  public boolean sampleEncryptionDataNeedsFill;
  public boolean[] sampleHasSubsampleEncryptionTable;
  public boolean[] sampleIsSyncFrameTable;
  public int[] sampleSizeTable;
  public TrackEncryptionBox trackEncryptionBox;
  public int trunCount;
  public long[] trunDataPosition;
  public int[] trunLength;
  
  public void fillEncryptionData(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.readFully(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
    this.sampleEncryptionData.setPosition(0);
    this.sampleEncryptionDataNeedsFill = false;
  }
  
  public void fillEncryptionData(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.readBytes(this.sampleEncryptionData.data, 0, this.sampleEncryptionDataLength);
    this.sampleEncryptionData.setPosition(0);
    this.sampleEncryptionDataNeedsFill = false;
  }
  
  public long getSamplePresentationTime(int paramInt)
  {
    return this.sampleDecodingTimeTable[paramInt] + this.sampleCompositionTimeOffsetTable[paramInt];
  }
  
  public void initEncryptionData(int paramInt)
  {
    if ((this.sampleEncryptionData == null) || (this.sampleEncryptionData.limit() < paramInt)) {
      this.sampleEncryptionData = new ParsableByteArray(paramInt);
    }
    this.sampleEncryptionDataLength = paramInt;
    this.definesEncryptionData = true;
    this.sampleEncryptionDataNeedsFill = true;
  }
  
  public void initTables(int paramInt1, int paramInt2)
  {
    this.trunCount = paramInt1;
    this.sampleCount = paramInt2;
    if ((this.trunLength == null) || (this.trunLength.length < paramInt1))
    {
      this.trunDataPosition = new long[paramInt1];
      this.trunLength = new int[paramInt1];
    }
    if ((this.sampleSizeTable == null) || (this.sampleSizeTable.length < paramInt2))
    {
      paramInt1 = paramInt2 * 125 / 100;
      this.sampleSizeTable = new int[paramInt1];
      this.sampleCompositionTimeOffsetTable = new int[paramInt1];
      this.sampleDecodingTimeTable = new long[paramInt1];
      this.sampleIsSyncFrameTable = new boolean[paramInt1];
      this.sampleHasSubsampleEncryptionTable = new boolean[paramInt1];
    }
  }
  
  public void reset()
  {
    this.trunCount = 0;
    this.nextFragmentDecodeTime = 0L;
    this.definesEncryptionData = false;
    this.sampleEncryptionDataNeedsFill = false;
    this.trackEncryptionBox = null;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mp4/TrackFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */