package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

public final class ChunkExtractorWrapper
  implements ExtractorOutput, TrackOutput
{
  public final Extractor extractor;
  private boolean extractorInitialized;
  private final Format manifestFormat;
  private final boolean preferManifestDrmInitData;
  private final boolean resendFormatOnInit;
  private SeekMapOutput seekMapOutput;
  private boolean seenTrack;
  private int seenTrackId;
  private Format sentFormat;
  private TrackOutput trackOutput;
  
  public ChunkExtractorWrapper(Extractor paramExtractor, Format paramFormat, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.extractor = paramExtractor;
    this.manifestFormat = paramFormat;
    this.preferManifestDrmInitData = paramBoolean1;
    this.resendFormatOnInit = paramBoolean2;
  }
  
  public void endTracks()
  {
    Assertions.checkState(this.seenTrack);
  }
  
  public void format(Format paramFormat)
  {
    this.sentFormat = paramFormat.copyWithManifestFormatInfo(this.manifestFormat, this.preferManifestDrmInitData);
    this.trackOutput.format(this.sentFormat);
  }
  
  public void init(SeekMapOutput paramSeekMapOutput, TrackOutput paramTrackOutput)
  {
    this.seekMapOutput = paramSeekMapOutput;
    this.trackOutput = paramTrackOutput;
    if (!this.extractorInitialized)
    {
      this.extractor.init(this);
      this.extractorInitialized = true;
    }
    do
    {
      return;
      this.extractor.seek(0L, 0L);
    } while ((!this.resendFormatOnInit) || (this.sentFormat == null));
    paramTrackOutput.format(this.sentFormat);
  }
  
  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    return this.trackOutput.sampleData(paramExtractorInput, paramInt, paramBoolean);
  }
  
  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    this.trackOutput.sampleData(paramParsableByteArray, paramInt);
  }
  
  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    this.trackOutput.sampleMetadata(paramLong, paramInt1, paramInt2, paramInt3, paramArrayOfByte);
  }
  
  public void seekMap(SeekMap paramSeekMap)
  {
    this.seekMapOutput.seekMap(paramSeekMap);
  }
  
  public TrackOutput track(int paramInt)
  {
    if ((!this.seenTrack) || (this.seenTrackId == paramInt)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.seenTrack = true;
      this.seenTrackId = paramInt;
      return this;
    }
  }
  
  public static abstract interface SeekMapOutput
  {
    public abstract void seekMap(SeekMap paramSeekMap);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/chunk/ChunkExtractorWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */