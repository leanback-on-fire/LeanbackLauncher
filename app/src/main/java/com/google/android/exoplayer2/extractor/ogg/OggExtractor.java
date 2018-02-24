package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

public class OggExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new OggExtractor() };
    }
  };
  private static final int MAX_VERIFICATION_BYTES = 8;
  private StreamReader streamReader;
  
  private static ParsableByteArray resetPosition(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(0);
    return paramParsableByteArray;
  }
  
  StreamReader getStreamReader()
  {
    return this.streamReader;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    TrackOutput localTrackOutput = paramExtractorOutput.track(0);
    paramExtractorOutput.endTracks();
    this.streamReader.init(paramExtractorOutput, localTrackOutput);
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    return this.streamReader.read(paramExtractorInput, paramPositionHolder);
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.streamReader.seek(paramLong1, paramLong2);
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    try
    {
      Object localObject = new OggPageHeader();
      if (((OggPageHeader)localObject).populate(paramExtractorInput, true))
      {
        if ((((OggPageHeader)localObject).type & 0x2) != 2) {
          return false;
        }
        int i = Math.min(((OggPageHeader)localObject).bodySize, 8);
        localObject = new ParsableByteArray(i);
        paramExtractorInput.peekFully(((ParsableByteArray)localObject).data, 0, i);
        if (FlacReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
        {
          this.streamReader = new FlacReader();
          break label134;
        }
        if (VorbisReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
        {
          this.streamReader = new VorbisReader();
          break label134;
        }
        if (OpusReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
        {
          this.streamReader = new OpusReader();
          break label134;
        }
      }
      return false;
      label134:
      return true;
    }
    catch (ParserException paramExtractorInput) {}
    return false;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/ogg/OggExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */