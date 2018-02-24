package com.google.android.exoplayer2.extractor.wav;

import android.util.Log;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class WavHeaderReader
{
  private static final String TAG = "WavHeaderReader";
  private static final int TYPE_PCM = 1;
  private static final int TYPE_WAVE_FORMAT_EXTENSIBLE = 65534;
  
  public static WavHeader peek(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    Assertions.checkNotNull(paramExtractorInput);
    ParsableByteArray localParsableByteArray = new ParsableByteArray(16);
    if (ChunkHeader.peek(paramExtractorInput, localParsableByteArray).id != Util.getIntegerCodeForString("RIFF")) {
      return null;
    }
    paramExtractorInput.peekFully(localParsableByteArray.data, 0, 4);
    localParsableByteArray.setPosition(0);
    int i = localParsableByteArray.readInt();
    if (i != Util.getIntegerCodeForString("WAVE"))
    {
      Log.e("WavHeaderReader", "Unsupported RIFF format: " + i);
      return null;
    }
    for (ChunkHeader localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray); localChunkHeader.id != Util.getIntegerCodeForString("fmt "); localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray)) {
      paramExtractorInput.advancePeekPosition((int)localChunkHeader.size);
    }
    if (localChunkHeader.size >= 16L) {}
    int j;
    int k;
    int m;
    int n;
    int i1;
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      paramExtractorInput.peekFully(localParsableByteArray.data, 0, 16);
      localParsableByteArray.setPosition(0);
      i = localParsableByteArray.readLittleEndianUnsignedShort();
      j = localParsableByteArray.readLittleEndianUnsignedShort();
      k = localParsableByteArray.readLittleEndianUnsignedIntToInt();
      m = localParsableByteArray.readLittleEndianUnsignedIntToInt();
      n = localParsableByteArray.readLittleEndianUnsignedShort();
      i1 = localParsableByteArray.readLittleEndianUnsignedShort();
      i2 = j * i1 / 8;
      if (n == i2) {
        break;
      }
      throw new ParserException("Expected block alignment: " + i2 + "; got: " + n);
    }
    int i2 = Util.getPcmEncoding(i1);
    if (i2 == 0)
    {
      Log.e("WavHeaderReader", "Unsupported WAV bit depth: " + i1);
      return null;
    }
    if ((i != 1) && (i != 65534))
    {
      Log.e("WavHeaderReader", "Unsupported WAV format type: " + i);
      return null;
    }
    paramExtractorInput.advancePeekPosition((int)localChunkHeader.size - 16);
    return new WavHeader(j, k, m, n, i1, i2);
  }
  
  public static void skipToData(ExtractorInput paramExtractorInput, WavHeader paramWavHeader)
    throws IOException, InterruptedException
  {
    Assertions.checkNotNull(paramExtractorInput);
    Assertions.checkNotNull(paramWavHeader);
    paramExtractorInput.resetPeekPosition();
    ParsableByteArray localParsableByteArray = new ParsableByteArray(8);
    for (ChunkHeader localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray); localChunkHeader.id != Util.getIntegerCodeForString("data"); localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray))
    {
      Log.w("WavHeaderReader", "Ignoring unknown WAV chunk: " + localChunkHeader.id);
      long l = 8L + localChunkHeader.size;
      if (localChunkHeader.id == Util.getIntegerCodeForString("RIFF")) {
        l = 12L;
      }
      if (l > 2147483647L) {
        throw new ParserException("Chunk is too large (~2GB+) to skip; id: " + localChunkHeader.id);
      }
      paramExtractorInput.skipFully((int)l);
    }
    paramExtractorInput.skipFully(8);
    paramWavHeader.setDataBounds(paramExtractorInput.getPosition(), localChunkHeader.size);
  }
  
  private static final class ChunkHeader
  {
    public static final int SIZE_IN_BYTES = 8;
    public final int id;
    public final long size;
    
    private ChunkHeader(int paramInt, long paramLong)
    {
      this.id = paramInt;
      this.size = paramLong;
    }
    
    public static ChunkHeader peek(ExtractorInput paramExtractorInput, ParsableByteArray paramParsableByteArray)
      throws IOException, InterruptedException
    {
      paramExtractorInput.peekFully(paramParsableByteArray.data, 0, 8);
      paramParsableByteArray.setPosition(0);
      return new ChunkHeader(paramParsableByteArray.readInt(), paramParsableByteArray.readLittleEndianUnsignedInt());
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/wav/WavHeaderReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */