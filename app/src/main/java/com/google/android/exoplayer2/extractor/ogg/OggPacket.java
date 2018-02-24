package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

final class OggPacket
{
  private int currentSegmentIndex = -1;
  private final ParsableByteArray packetArray = new ParsableByteArray(new byte[65025], 0);
  private final OggPageHeader pageHeader = new OggPageHeader();
  private boolean populated;
  private int segmentCount;
  
  private int calculatePacketSize(int paramInt)
  {
    this.segmentCount = 0;
    int i = 0;
    int j;
    int k;
    do
    {
      j = i;
      if (this.segmentCount + paramInt >= this.pageHeader.pageSegmentCount) {
        break;
      }
      int[] arrayOfInt = this.pageHeader.laces;
      j = this.segmentCount;
      this.segmentCount = (j + 1);
      k = arrayOfInt[(j + paramInt)];
      j = i + k;
      i = j;
    } while (k == 255);
    return j;
  }
  
  public OggPageHeader getPageHeader()
  {
    return this.pageHeader;
  }
  
  public ParsableByteArray getPayload()
  {
    return this.packetArray;
  }
  
  public boolean populate(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (paramExtractorInput != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (this.populated)
      {
        this.populated = false;
        this.packetArray.reset();
      }
      if (this.populated) {
        break label255;
      }
      if (this.currentSegmentIndex >= 0) {
        break label140;
      }
      if (this.pageHeader.populate(paramExtractorInput, true)) {
        break;
      }
      return false;
    }
    int k = 0;
    int m = this.pageHeader.headerSize;
    int j = m;
    int i = k;
    if ((this.pageHeader.type & 0x1) == 1)
    {
      j = m;
      i = k;
      if (this.packetArray.limit() == 0)
      {
        j = m + calculatePacketSize(0);
        i = 0 + this.segmentCount;
      }
    }
    paramExtractorInput.skipFully(j);
    this.currentSegmentIndex = i;
    label140:
    i = calculatePacketSize(this.currentSegmentIndex);
    j = this.currentSegmentIndex + this.segmentCount;
    if (i > 0)
    {
      paramExtractorInput.readFully(this.packetArray.data, this.packetArray.limit(), i);
      this.packetArray.setLimit(this.packetArray.limit() + i);
      if (this.pageHeader.laces[(j - 1)] == 255) {
        break label249;
      }
    }
    label249:
    for (bool = true;; bool = false)
    {
      this.populated = bool;
      i = j;
      if (j == this.pageHeader.pageSegmentCount) {
        i = -1;
      }
      this.currentSegmentIndex = i;
      break;
    }
    label255:
    return true;
  }
  
  public void reset()
  {
    this.pageHeader.reset();
    this.packetArray.reset();
    this.currentSegmentIndex = -1;
    this.populated = false;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/ogg/OggPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */