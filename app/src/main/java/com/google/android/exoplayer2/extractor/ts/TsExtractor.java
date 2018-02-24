package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;

public final class TsExtractor
  implements Extractor
{
  private static final long AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("AC-3");
  private static final int BUFFER_PACKET_COUNT = 5;
  private static final int BUFFER_SIZE = 940;
  private static final long E_AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("EAC3");
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new TsExtractor() };
    }
  };
  private static final long HEVC_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("HEVC");
  private static final int MAX_PID_PLUS_ONE = 8192;
  private static final int TS_PACKET_SIZE = 188;
  private static final int TS_PAT_PID = 0;
  public static final int TS_STREAM_TYPE_AAC = 15;
  public static final int TS_STREAM_TYPE_AC3 = 129;
  public static final int TS_STREAM_TYPE_DTS = 138;
  public static final int TS_STREAM_TYPE_E_AC3 = 135;
  public static final int TS_STREAM_TYPE_H262 = 2;
  public static final int TS_STREAM_TYPE_H264 = 27;
  public static final int TS_STREAM_TYPE_H265 = 36;
  public static final int TS_STREAM_TYPE_HDMV_DTS = 130;
  public static final int TS_STREAM_TYPE_ID3 = 21;
  public static final int TS_STREAM_TYPE_MPA = 3;
  public static final int TS_STREAM_TYPE_MPA_LSF = 4;
  public static final int TS_STREAM_TYPE_SPLICE_INFO = 134;
  private static final int TS_SYNC_BYTE = 71;
  private final SparseIntArray continuityCounters;
  private final boolean hlsMode;
  private TsPayloadReader id3Reader;
  private ExtractorOutput output;
  private final TsPayloadReader.Factory payloadReaderFactory;
  private final TimestampAdjuster timestampAdjuster;
  private final SparseBooleanArray trackIds;
  private boolean tracksEnded;
  private final ParsableByteArray tsPacketBuffer;
  private final SparseArray<TsPayloadReader> tsPayloadReaders;
  private final ParsableBitArray tsScratch;
  
  public TsExtractor()
  {
    this(new TimestampAdjuster(0L));
  }
  
  public TsExtractor(TimestampAdjuster paramTimestampAdjuster)
  {
    this(paramTimestampAdjuster, new DefaultTsPayloadReaderFactory(), false);
  }
  
  public TsExtractor(TimestampAdjuster paramTimestampAdjuster, TsPayloadReader.Factory paramFactory, boolean paramBoolean)
  {
    this.timestampAdjuster = paramTimestampAdjuster;
    this.payloadReaderFactory = ((TsPayloadReader.Factory)Assertions.checkNotNull(paramFactory));
    this.hlsMode = paramBoolean;
    this.tsPacketBuffer = new ParsableByteArray(940);
    this.tsScratch = new ParsableBitArray(new byte[3]);
    this.trackIds = new SparseBooleanArray();
    this.tsPayloadReaders = new SparseArray();
    this.continuityCounters = new SparseIntArray();
    resetPayloadReaders();
  }
  
  private void resetPayloadReaders()
  {
    this.trackIds.clear();
    this.tsPayloadReaders.clear();
    SparseArray localSparseArray = this.payloadReaderFactory.createInitialPayloadReaders();
    int j = localSparseArray.size();
    int i = 0;
    while (i < j)
    {
      this.tsPayloadReaders.put(localSparseArray.keyAt(i), localSparseArray.valueAt(i));
      i += 1;
    }
    this.tsPayloadReaders.put(0, new SectionReader(new PatReader()));
    this.id3Reader = null;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.output = paramExtractorOutput;
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    paramPositionHolder = this.tsPacketBuffer.data;
    if (940 - this.tsPacketBuffer.getPosition() < 188)
    {
      i = this.tsPacketBuffer.bytesLeft();
      if (i > 0) {
        System.arraycopy(paramPositionHolder, this.tsPacketBuffer.getPosition(), paramPositionHolder, 0, i);
      }
      this.tsPacketBuffer.reset(paramPositionHolder, i);
    }
    while (this.tsPacketBuffer.bytesLeft() < 188)
    {
      i = this.tsPacketBuffer.limit();
      j = paramExtractorInput.read(paramPositionHolder, i, 940 - i);
      if (j == -1) {
        return -1;
      }
      this.tsPacketBuffer.setLimit(i + j);
    }
    int k = this.tsPacketBuffer.limit();
    int i = this.tsPacketBuffer.getPosition();
    while ((i < k) && (paramPositionHolder[i] != 71)) {
      i += 1;
    }
    this.tsPacketBuffer.setPosition(i);
    int m = i + 188;
    if (m > k) {
      return 0;
    }
    this.tsPacketBuffer.skipBytes(1);
    this.tsPacketBuffer.readBytes(this.tsScratch, 3);
    if (this.tsScratch.readBit())
    {
      this.tsPacketBuffer.setPosition(m);
      return 0;
    }
    boolean bool1 = this.tsScratch.readBit();
    this.tsScratch.skipBits(1);
    int n = this.tsScratch.readBits(13);
    this.tsScratch.skipBits(2);
    boolean bool2 = this.tsScratch.readBit();
    boolean bool3 = this.tsScratch.readBit();
    int j = 0;
    int i1 = this.tsScratch.readBits(4);
    i = j;
    if (!this.hlsMode)
    {
      int i2 = this.continuityCounters.get(n, i1 - 1);
      this.continuityCounters.put(n, i1);
      if (i2 == i1)
      {
        i = j;
        if (bool3)
        {
          this.tsPacketBuffer.setPosition(m);
          return 0;
        }
      }
      else
      {
        i = j;
        if (i1 != (i2 + 1) % 16) {
          i = 1;
        }
      }
    }
    if (bool2)
    {
      j = this.tsPacketBuffer.readUnsignedByte();
      this.tsPacketBuffer.skipBytes(j);
    }
    if (bool3)
    {
      paramExtractorInput = (TsPayloadReader)this.tsPayloadReaders.get(n);
      if (paramExtractorInput != null)
      {
        if (i != 0) {
          paramExtractorInput.seek();
        }
        this.tsPacketBuffer.setLimit(m);
        paramExtractorInput.consume(this.tsPacketBuffer, bool1);
        if (this.tsPacketBuffer.getPosition() > m) {
          break label483;
        }
      }
    }
    label483:
    for (bool1 = true;; bool1 = false)
    {
      Assertions.checkState(bool1);
      this.tsPacketBuffer.setLimit(k);
      this.tsPacketBuffer.setPosition(m);
      return 0;
    }
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.timestampAdjuster.reset();
    this.tsPacketBuffer.reset();
    this.continuityCounters.clear();
    resetPayloadReaders();
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool2 = false;
    byte[] arrayOfByte = this.tsPacketBuffer.data;
    paramExtractorInput.peekFully(arrayOfByte, 0, 940);
    int i = 0;
    boolean bool1 = bool2;
    int j;
    if (i < 188) {
      j = 0;
    }
    for (;;)
    {
      if (j == 5)
      {
        paramExtractorInput.skipFully(i);
        bool1 = true;
        return bool1;
      }
      if (arrayOfByte[(j * 188 + i)] != 71)
      {
        i += 1;
        break;
      }
      j += 1;
    }
  }
  
  private class PatReader
    implements SectionPayloadReader
  {
    private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);
    
    public PatReader() {}
    
    public void consume(ParsableByteArray paramParsableByteArray)
    {
      if (paramParsableByteArray.readUnsignedByte() != 0) {
        return;
      }
      paramParsableByteArray.skipBytes(7);
      int j = paramParsableByteArray.bytesLeft() / 4;
      int i = 0;
      label23:
      int k;
      if (i < j)
      {
        paramParsableByteArray.readBytes(this.patScratch, 4);
        k = this.patScratch.readBits(16);
        this.patScratch.skipBits(3);
        if (k != 0) {
          break label77;
        }
        this.patScratch.skipBits(13);
      }
      for (;;)
      {
        i += 1;
        break label23;
        break;
        label77:
        k = this.patScratch.readBits(13);
        TsExtractor.this.tsPayloadReaders.put(k, new SectionReader(new TsExtractor.PmtReader(TsExtractor.this, k)));
      }
    }
    
    public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator) {}
  }
  
  private class PmtReader
    implements SectionPayloadReader
  {
    private static final int TS_PMT_DESC_AC3 = 106;
    private static final int TS_PMT_DESC_DTS = 123;
    private static final int TS_PMT_DESC_EAC3 = 122;
    private static final int TS_PMT_DESC_ISO639_LANG = 10;
    private static final int TS_PMT_DESC_REGISTRATION = 5;
    private final int pid;
    private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
    
    public PmtReader(int paramInt)
    {
      this.pid = paramInt;
    }
    
    private TsPayloadReader.EsInfo readEsInfo(ParsableByteArray paramParsableByteArray, int paramInt)
    {
      int j = paramParsableByteArray.getPosition();
      int k = j + paramInt;
      int i = -1;
      Object localObject1 = null;
      if (paramParsableByteArray.getPosition() < k)
      {
        int i1 = paramParsableByteArray.readUnsignedByte();
        int m = paramParsableByteArray.readUnsignedByte();
        int n = paramParsableByteArray.getPosition();
        long l;
        Object localObject2;
        if (i1 == 5)
        {
          l = paramParsableByteArray.readUnsignedInt();
          if (l == TsExtractor.AC3_FORMAT_IDENTIFIER)
          {
            paramInt = 129;
            localObject2 = localObject1;
          }
        }
        for (;;)
        {
          paramParsableByteArray.skipBytes(n + m - paramParsableByteArray.getPosition());
          localObject1 = localObject2;
          i = paramInt;
          break;
          if (l == TsExtractor.E_AC3_FORMAT_IDENTIFIER)
          {
            paramInt = 135;
            localObject2 = localObject1;
          }
          else
          {
            localObject2 = localObject1;
            paramInt = i;
            if (l == TsExtractor.HEVC_FORMAT_IDENTIFIER)
            {
              paramInt = 36;
              localObject2 = localObject1;
              continue;
              if (i1 == 106)
              {
                paramInt = 129;
                localObject2 = localObject1;
              }
              else if (i1 == 122)
              {
                paramInt = 135;
                localObject2 = localObject1;
              }
              else if (i1 == 123)
              {
                paramInt = 138;
                localObject2 = localObject1;
              }
              else
              {
                localObject2 = localObject1;
                paramInt = i;
                if (i1 == 10)
                {
                  localObject2 = new String(paramParsableByteArray.data, paramParsableByteArray.getPosition(), 3).trim();
                  paramInt = i;
                }
              }
            }
          }
        }
      }
      paramParsableByteArray.setPosition(k);
      return new TsPayloadReader.EsInfo(i, (String)localObject1, Arrays.copyOfRange(paramParsableByteArray.data, j, k));
    }
    
    public void consume(ParsableByteArray paramParsableByteArray)
    {
      if (paramParsableByteArray.readUnsignedByte() != 2) {
        return;
      }
      paramParsableByteArray.skipBytes(9);
      paramParsableByteArray.readBytes(this.pmtScratch, 2);
      this.pmtScratch.skipBits(4);
      paramParsableByteArray.skipBytes(this.pmtScratch.readBits(12));
      Object localObject;
      if ((TsExtractor.this.hlsMode) && (TsExtractor.this.id3Reader == null))
      {
        localObject = new TsPayloadReader.EsInfo(21, null, new byte[0]);
        TsExtractor.access$202(TsExtractor.this, TsExtractor.this.payloadReaderFactory.createPayloadReader(21, (TsPayloadReader.EsInfo)localObject));
        TsExtractor.this.id3Reader.init(TsExtractor.this.timestampAdjuster, TsExtractor.this.output, new TsPayloadReader.TrackIdGenerator(21, 8192));
      }
      int j = paramParsableByteArray.bytesLeft();
      while (j > 0)
      {
        paramParsableByteArray.readBytes(this.pmtScratch, 5);
        int k = this.pmtScratch.readBits(8);
        this.pmtScratch.skipBits(3);
        int m = this.pmtScratch.readBits(13);
        this.pmtScratch.skipBits(4);
        int n = this.pmtScratch.readBits(12);
        localObject = readEsInfo(paramParsableByteArray, n);
        int i = k;
        if (k == 6) {
          i = ((TsPayloadReader.EsInfo)localObject).streamType;
        }
        n = j - (n + 5);
        if (TsExtractor.this.hlsMode)
        {
          k = i;
          label255:
          j = n;
          if (TsExtractor.this.trackIds.get(k)) {
            continue;
          }
          TsExtractor.this.trackIds.put(k, true);
          if ((!TsExtractor.this.hlsMode) || (i != 21)) {
            break label346;
          }
          localObject = TsExtractor.this.id3Reader;
        }
        for (;;)
        {
          j = n;
          if (localObject == null) {
            break;
          }
          TsExtractor.this.tsPayloadReaders.put(m, localObject);
          j = n;
          break;
          k = m;
          break label255;
          label346:
          TsPayloadReader localTsPayloadReader = TsExtractor.this.payloadReaderFactory.createPayloadReader(i, (TsPayloadReader.EsInfo)localObject);
          localObject = localTsPayloadReader;
          if (localTsPayloadReader != null)
          {
            localTsPayloadReader.init(TsExtractor.this.timestampAdjuster, TsExtractor.this.output, new TsPayloadReader.TrackIdGenerator(k, 8192));
            localObject = localTsPayloadReader;
          }
        }
      }
      if (TsExtractor.this.hlsMode) {
        if (!TsExtractor.this.tracksEnded) {
          TsExtractor.this.output.endTracks();
        }
      }
      for (;;)
      {
        TsExtractor.access$702(TsExtractor.this, true);
        return;
        TsExtractor.this.tsPayloadReaders.remove(0);
        TsExtractor.this.tsPayloadReaders.remove(this.pid);
        TsExtractor.this.output.endTracks();
      }
    }
    
    public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/ts/TsExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */