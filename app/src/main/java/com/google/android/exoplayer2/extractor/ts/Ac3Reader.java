package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;

final class Ac3Reader
  implements ElementaryStreamReader
{
  private static final int HEADER_SIZE = 8;
  private static final int STATE_FINDING_SYNC = 0;
  private static final int STATE_READING_HEADER = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private int bytesRead;
  private Format format;
  private final ParsableBitArray headerScratchBits = new ParsableBitArray(new byte[8]);
  private final ParsableByteArray headerScratchBytes = new ParsableByteArray(this.headerScratchBits.data);
  private boolean isEac3;
  private final String language;
  private boolean lastByteWas0B;
  private TrackOutput output;
  private long sampleDurationUs;
  private int sampleSize;
  private int state = 0;
  private long timeUs;
  
  public Ac3Reader()
  {
    this(null);
  }
  
  public Ac3Reader(String paramString)
  {
    this.language = paramString;
  }
  
  private boolean continueRead(ParsableByteArray paramParsableByteArray, byte[] paramArrayOfByte, int paramInt)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), paramInt - this.bytesRead);
    paramParsableByteArray.readBytes(paramArrayOfByte, this.bytesRead, i);
    this.bytesRead += i;
    return this.bytesRead == paramInt;
  }
  
  private void parseHeader()
  {
    boolean bool;
    Format localFormat;
    if (this.format == null)
    {
      this.headerScratchBits.skipBits(40);
      if (this.headerScratchBits.readBits(5) == 16)
      {
        bool = true;
        this.isEac3 = bool;
        this.headerScratchBits.setPosition(this.headerScratchBits.getPosition() - 45);
        if (!this.isEac3) {
          break label160;
        }
        localFormat = Ac3Util.parseEac3SyncframeFormat(this.headerScratchBits, null, this.language, null);
        label74:
        this.format = localFormat;
        this.output.format(this.format);
      }
    }
    else
    {
      if (!this.isEac3) {
        break label177;
      }
      i = Ac3Util.parseEAc3SyncframeSize(this.headerScratchBits.data);
      label110:
      this.sampleSize = i;
      if (!this.isEac3) {
        break label191;
      }
    }
    label160:
    label177:
    label191:
    for (int i = Ac3Util.parseEAc3SyncframeAudioSampleCount(this.headerScratchBits.data);; i = Ac3Util.getAc3SyncframeAudioSampleCount())
    {
      this.sampleDurationUs = ((int)(1000000L * i / this.format.sampleRate));
      return;
      bool = false;
      break;
      localFormat = Ac3Util.parseAc3SyncframeFormat(this.headerScratchBits, null, this.language, null);
      break label74;
      i = Ac3Util.parseAc3SyncframeSize(this.headerScratchBits.data);
      break label110;
    }
  }
  
  private boolean skipToNextSync(ParsableByteArray paramParsableByteArray)
  {
    if (paramParsableByteArray.bytesLeft() > 0)
    {
      if (!this.lastByteWas0B)
      {
        if (paramParsableByteArray.readUnsignedByte() == 11) {}
        for (bool = true;; bool = false)
        {
          this.lastByteWas0B = bool;
          break;
        }
      }
      int i = paramParsableByteArray.readUnsignedByte();
      if (i == 119)
      {
        this.lastByteWas0B = false;
        return true;
      }
      if (i == 11) {}
      for (boolean bool = true;; bool = false)
      {
        this.lastByteWas0B = bool;
        break;
      }
    }
    return false;
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0) {
      switch (this.state)
      {
      default: 
        break;
      case 0: 
        if (skipToNextSync(paramParsableByteArray))
        {
          this.state = 1;
          this.headerScratchBytes.data[0] = 11;
          this.headerScratchBytes.data[1] = 119;
          this.bytesRead = 2;
        }
        break;
      case 1: 
        if (continueRead(paramParsableByteArray, this.headerScratchBytes.data, 8))
        {
          parseHeader();
          this.headerScratchBytes.setPosition(0);
          this.output.sampleData(this.headerScratchBytes, 8);
          this.state = 2;
        }
        break;
      case 2: 
        int i = Math.min(paramParsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
        this.output.sampleData(paramParsableByteArray, i);
        this.bytesRead += i;
        if (this.bytesRead == this.sampleSize)
        {
          this.output.sampleMetadata(this.timeUs, 1, this.sampleSize, 0, null);
          this.timeUs += this.sampleDurationUs;
          this.state = 0;
        }
        break;
      }
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getNextId());
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.timeUs = paramLong;
  }
  
  public void seek()
  {
    this.state = 0;
    this.bytesRead = 0;
    this.lastByteWas0B = false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/ts/Ac3Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */