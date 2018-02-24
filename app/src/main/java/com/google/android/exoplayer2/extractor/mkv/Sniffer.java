package com.google.android.exoplayer2.extractor.mkv;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

final class Sniffer
{
  private static final int ID_EBML = 440786851;
  private static final int SEARCH_LENGTH = 1024;
  private int peekLength;
  private final ParsableByteArray scratch = new ParsableByteArray(8);
  
  private long readUint(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.peekFully(this.scratch.data, 0, 1);
    int k = this.scratch.data[0] & 0xFF;
    if (k == 0) {
      return Long.MIN_VALUE;
    }
    int j = 128;
    int i = 0;
    while ((k & j) == 0)
    {
      j >>= 1;
      i += 1;
    }
    k &= (j ^ 0xFFFFFFFF);
    paramExtractorInput.peekFully(this.scratch.data, 1, i);
    j = 0;
    while (j < i)
    {
      k = (k << 8) + (this.scratch.data[(j + 1)] & 0xFF);
      j += 1;
    }
    this.peekLength += i + 1;
    return k;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    long l2 = paramExtractorInput.getLength();
    int i;
    if ((l2 == -1L) || (l2 > 1024L))
    {
      l1 = 1024L;
      i = (int)l1;
      paramExtractorInput.peekFully(this.scratch.data, 0, 4);
      l1 = this.scratch.readUnsignedInt();
      this.peekLength = 4;
    }
    for (;;)
    {
      if (l1 == 440786851L) {
        break label143;
      }
      int j = this.peekLength + 1;
      this.peekLength = j;
      if (j == i)
      {
        return false;
        l1 = l2;
        break;
      }
      paramExtractorInput.peekFully(this.scratch.data, 0, 1);
      l1 = l1 << 8 & 0xFFFFFFFFFFFFFF00 | this.scratch.data[0] & 0xFF;
    }
    label143:
    long l1 = readUint(paramExtractorInput);
    long l3 = this.peekLength;
    if ((l1 == Long.MIN_VALUE) || ((l2 != -1L) && (l3 + l1 >= l2))) {
      return false;
    }
    do
    {
      if (l2 != 0L)
      {
        paramExtractorInput.advancePeekPosition((int)l2);
        this.peekLength = ((int)(this.peekLength + l2));
      }
      if (this.peekLength >= l3 + l1) {
        break;
      }
      if (readUint(paramExtractorInput) == Long.MIN_VALUE) {
        return false;
      }
      l2 = readUint(paramExtractorInput);
    } while ((l2 >= 0L) && (l2 <= 2147483647L));
    return false;
    return this.peekLength == l3 + l1;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mkv/Sniffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */