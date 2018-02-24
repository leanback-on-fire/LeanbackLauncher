package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;

public final class Mp3Extractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Mp3Extractor() };
    }
  };
  private static final int HEADER_MASK = -128000;
  private static final int INFO_HEADER = Util.getIntegerCodeForString("Info");
  private static final int MAX_SNIFF_BYTES = 4096;
  private static final int MAX_SYNC_BYTES = 131072;
  private static final int SCRATCH_LENGTH = 10;
  private static final int VBRI_HEADER = Util.getIntegerCodeForString("VBRI");
  private static final int XING_HEADER = Util.getIntegerCodeForString("Xing");
  private long basisTimeUs;
  private ExtractorOutput extractorOutput;
  private final long forcedFirstSampleTimestampUs;
  private final GaplessInfoHolder gaplessInfoHolder;
  private Metadata metadata;
  private int sampleBytesRemaining;
  private long samplesRead;
  private final ParsableByteArray scratch;
  private Seeker seeker;
  private final MpegAudioHeader synchronizedHeader;
  private int synchronizedHeaderData;
  private TrackOutput trackOutput;
  
  public Mp3Extractor()
  {
    this(-9223372036854775807L);
  }
  
  public Mp3Extractor(long paramLong)
  {
    this.forcedFirstSampleTimestampUs = paramLong;
    this.scratch = new ParsableByteArray(10);
    this.synchronizedHeader = new MpegAudioHeader();
    this.gaplessInfoHolder = new GaplessInfoHolder();
    this.basisTimeUs = -9223372036854775807L;
  }
  
  private void peekId3Data(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = 0;
    paramExtractorInput.peekFully(this.scratch.data, 0, 10);
    this.scratch.setPosition(0);
    if (this.scratch.readUnsignedInt24() != Id3Decoder.ID3_TAG)
    {
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      return;
    }
    this.scratch.skipBytes(3);
    int j = this.scratch.readSynchSafeInt();
    int k = j + 10;
    if (this.metadata == null)
    {
      byte[] arrayOfByte = new byte[k];
      System.arraycopy(this.scratch.data, 0, arrayOfByte, 0, 10);
      paramExtractorInput.peekFully(arrayOfByte, 10, j);
      this.metadata = new Id3Decoder().decode(arrayOfByte, k);
      if (this.metadata != null) {
        this.gaplessInfoHolder.setFromMetadata(this.metadata);
      }
    }
    for (;;)
    {
      i += k;
      break;
      paramExtractorInput.advancePeekPosition(j);
    }
  }
  
  private int readSample(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (this.sampleBytesRemaining == 0)
    {
      paramExtractorInput.resetPeekPosition();
      if (!paramExtractorInput.peekFully(this.scratch.data, 0, 4, true)) {
        return -1;
      }
      this.scratch.setPosition(0);
      i = this.scratch.readInt();
      if (((0xFFFE0C00 & i) != (this.synchronizedHeaderData & 0xFFFE0C00)) || (MpegAudioHeader.getFrameSize(i) == -1))
      {
        paramExtractorInput.skipFully(1);
        this.synchronizedHeaderData = 0;
        return 0;
      }
      MpegAudioHeader.populateHeader(i, this.synchronizedHeader);
      if (this.basisTimeUs == -9223372036854775807L)
      {
        this.basisTimeUs = this.seeker.getTimeUs(paramExtractorInput.getPosition());
        if (this.forcedFirstSampleTimestampUs != -9223372036854775807L)
        {
          l1 = this.seeker.getTimeUs(0L);
          this.basisTimeUs += this.forcedFirstSampleTimestampUs - l1;
        }
      }
      this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
    }
    int i = this.trackOutput.sampleData(paramExtractorInput, this.sampleBytesRemaining, true);
    if (i == -1) {
      return -1;
    }
    this.sampleBytesRemaining -= i;
    if (this.sampleBytesRemaining > 0) {
      return 0;
    }
    long l1 = this.basisTimeUs;
    long l2 = this.samplesRead * 1000000L / this.synchronizedHeader.sampleRate;
    this.trackOutput.sampleMetadata(l1 + l2, 1, this.synchronizedHeader.frameSize, 0, null);
    this.samplesRead += this.synchronizedHeader.samplesPerFrame;
    this.sampleBytesRemaining = 0;
    return 0;
  }
  
  private Seeker setupSeeker(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = 21;
    ParsableByteArray localParsableByteArray = new ParsableByteArray(this.synchronizedHeader.frameSize);
    paramExtractorInput.peekFully(localParsableByteArray.data, 0, this.synchronizedHeader.frameSize);
    long l1 = paramExtractorInput.getPosition();
    long l2 = paramExtractorInput.getLength();
    int j = 0;
    Object localObject2 = null;
    Object localObject1;
    if ((this.synchronizedHeader.version & 0x1) != 0)
    {
      if (this.synchronizedHeader.channels != 1) {
        i = 36;
      }
      if (localParsableByteArray.limit() >= i + 4)
      {
        localParsableByteArray.setPosition(i);
        j = localParsableByteArray.readInt();
      }
      if ((j != XING_HEADER) && (j != INFO_HEADER)) {
        break label317;
      }
      localObject1 = XingSeeker.create(this.synchronizedHeader, localParsableByteArray, l1, l2);
      if ((localObject1 != null) && (!this.gaplessInfoHolder.hasGaplessInfo()))
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.advancePeekPosition(i + 141);
        paramExtractorInput.peekFully(this.scratch.data, 0, 3);
        this.scratch.setPosition(0);
        this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
      }
      paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
    }
    for (;;)
    {
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        paramExtractorInput.resetPeekPosition();
        paramExtractorInput.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        localObject2 = new ConstantBitrateSeeker(paramExtractorInput.getPosition(), this.synchronizedHeader.bitrate, l2);
      }
      return (Seeker)localObject2;
      if (this.synchronizedHeader.channels != 1) {
        break;
      }
      i = 13;
      break;
      label317:
      localObject1 = localObject2;
      if (localParsableByteArray.limit() >= 40)
      {
        localParsableByteArray.setPosition(36);
        localObject1 = localObject2;
        if (localParsableByteArray.readInt() == VBRI_HEADER)
        {
          localObject1 = VbriSeeker.create(this.synchronizedHeader, localParsableByteArray, l1, l2);
          paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
        }
      }
    }
  }
  
  private boolean synchronize(ExtractorInput paramExtractorInput, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    int i1 = 0;
    int i2 = 0;
    int n = 0;
    int i3 = 0;
    int m;
    int i;
    int j;
    int k;
    label105:
    boolean bool;
    if (paramBoolean)
    {
      m = 4096;
      paramExtractorInput.resetPeekPosition();
      i = i2;
      j = i3;
      k = i1;
      if (paramExtractorInput.getPosition() == 0L)
      {
        peekId3Data(paramExtractorInput);
        int i4 = (int)paramExtractorInput.getPeekPosition();
        i = i2;
        n = i4;
        j = i3;
        k = i1;
        if (!paramBoolean)
        {
          paramExtractorInput.skipFully(i4);
          k = i1;
          j = i3;
          n = i4;
          i = i2;
        }
      }
      byte[] arrayOfByte = this.scratch.data;
      if (k <= 0) {
        break label166;
      }
      bool = true;
      label122:
      if (paramExtractorInput.peekFully(arrayOfByte, 0, 4, bool)) {
        break label172;
      }
      label137:
      if (!paramBoolean) {
        break label356;
      }
      paramExtractorInput.skipFully(n + j);
    }
    for (;;)
    {
      this.synchronizedHeaderData = i;
      return true;
      m = 131072;
      break;
      label166:
      bool = false;
      break label122;
      label172:
      this.scratch.setPosition(0);
      i2 = this.scratch.readInt();
      if ((i == 0) || ((0xFFFE0C00 & i2) == (0xFFFE0C00 & i)))
      {
        i3 = MpegAudioHeader.getFrameSize(i2);
        if (i3 != -1) {}
      }
      else
      {
        i = j + 1;
        if (j == m)
        {
          if (!paramBoolean) {
            throw new ParserException("Searched too many bytes.");
          }
          return false;
        }
        k = 0;
        i1 = 0;
        if (paramBoolean)
        {
          paramExtractorInput.resetPeekPosition();
          paramExtractorInput.advancePeekPosition(n + i);
          j = i;
          i = i1;
          break label105;
        }
        paramExtractorInput.skipFully(1);
        j = i;
        i = i1;
        break label105;
      }
      i1 = k + 1;
      if (i1 == 1)
      {
        MpegAudioHeader.populateHeader(i2, this.synchronizedHeader);
        k = i2;
      }
      do
      {
        paramExtractorInput.advancePeekPosition(i3 - 4);
        i = k;
        k = i1;
        break;
        k = i;
      } while (i1 != 4);
      break label137;
      label356:
      paramExtractorInput.resetPeekPosition();
    }
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = this.extractorOutput.track(0);
    this.extractorOutput.endTracks();
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (this.synchronizedHeaderData == 0) {}
    try
    {
      synchronize(paramExtractorInput, false);
      if (this.seeker == null)
      {
        this.seeker = setupSeeker(paramExtractorInput);
        this.extractorOutput.seekMap(this.seeker);
        this.trackOutput.format(Format.createAudioSampleFormat(null, this.synchronizedHeader.mimeType, null, -1, 4096, this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, -1, this.gaplessInfoHolder.encoderDelay, this.gaplessInfoHolder.encoderPadding, null, null, 0, null, this.metadata));
      }
      return readSample(paramExtractorInput);
    }
    catch (EOFException paramExtractorInput) {}
    return -1;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.synchronizedHeaderData = 0;
    this.basisTimeUs = -9223372036854775807L;
    this.samplesRead = 0L;
    this.sampleBytesRemaining = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return synchronize(paramExtractorInput, true);
  }
  
  static abstract interface Seeker
    extends SeekMap
  {
    public abstract long getTimeUs(long paramLong);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mp3/Mp3Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */