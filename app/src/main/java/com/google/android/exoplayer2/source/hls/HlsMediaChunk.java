package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.text.TextUtils;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.ts.Ac3Extractor;
import com.google.android.exoplayer2.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

final class HlsMediaChunk
  extends MediaChunk
{
  private static final String AAC_FILE_EXTENSION = ".aac";
  private static final String AC3_FILE_EXTENSION = ".ac3";
  private static final String EC3_FILE_EXTENSION = ".ec3";
  private static final String MP3_FILE_EXTENSION = ".mp3";
  private static final String MP4_FILE_EXTENSION = ".mp4";
  private static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
  private static final AtomicInteger UID_SOURCE = new AtomicInteger();
  private static final String VTT_FILE_EXTENSION = ".vtt";
  private static final String WEBVTT_FILE_EXTENSION = ".webvtt";
  private int bytesLoaded;
  public final int discontinuitySequenceNumber;
  private Extractor extractor;
  private HlsSampleStreamWrapper extractorOutput;
  public final HlsMasterPlaylist.HlsUrl hlsUrl;
  private final ParsableByteArray id3Data;
  private final Id3Decoder id3Decoder;
  private final DataSource initDataSource;
  private final DataSpec initDataSpec;
  private boolean initLoadCompleted;
  private int initSegmentBytesLoaded;
  private final boolean isEncrypted;
  private final boolean isMasterTimestampSource;
  private final boolean isPackedAudio;
  private final String lastPathSegment;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final boolean needNewExtractor;
  private final Extractor previousExtractor;
  private final boolean shouldSpliceIn;
  private final TimestampAdjuster timestampAdjuster;
  public final int uid;
  
  public HlsMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec1, DataSpec paramDataSpec2, HlsMasterPlaylist.HlsUrl paramHlsUrl, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, int paramInt3, boolean paramBoolean, TimestampAdjuster paramTimestampAdjuster, HlsMediaChunk paramHlsMediaChunk, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(buildDataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2), paramDataSpec1, paramHlsUrl.format, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.initDataSpec = paramDataSpec2;
    this.hlsUrl = paramHlsUrl;
    this.isMasterTimestampSource = paramBoolean;
    this.timestampAdjuster = paramTimestampAdjuster;
    this.discontinuitySequenceNumber = paramInt3;
    this.isEncrypted = (this.dataSource instanceof Aes128DataSource);
    this.lastPathSegment = paramDataSpec1.uri.getLastPathSegment();
    if ((this.lastPathSegment.endsWith(".aac")) || (this.lastPathSegment.endsWith(".ac3")) || (this.lastPathSegment.endsWith(".ec3")) || (this.lastPathSegment.endsWith(".mp3")))
    {
      paramBoolean = true;
      this.isPackedAudio = paramBoolean;
      if (paramHlsMediaChunk == null) {
        break label247;
      }
      this.id3Decoder = paramHlsMediaChunk.id3Decoder;
      this.id3Data = paramHlsMediaChunk.id3Data;
      this.previousExtractor = paramHlsMediaChunk.extractor;
      if (paramHlsMediaChunk.hlsUrl == paramHlsUrl) {
        break label235;
      }
      paramBoolean = true;
      label181:
      this.shouldSpliceIn = paramBoolean;
      if ((paramHlsMediaChunk.discontinuitySequenceNumber == paramInt3) && (!this.shouldSpliceIn)) {
        break label241;
      }
    }
    label235:
    label241:
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.needNewExtractor = paramBoolean;
      this.initDataSource = paramDataSource;
      this.uid = UID_SOURCE.getAndIncrement();
      return;
      paramBoolean = false;
      break;
      paramBoolean = false;
      break label181;
    }
    label247:
    if (this.isPackedAudio)
    {
      paramDataSpec1 = new Id3Decoder();
      label262:
      this.id3Decoder = paramDataSpec1;
      if (!this.isPackedAudio) {
        break label312;
      }
    }
    label312:
    for (paramDataSpec1 = new ParsableByteArray(10);; paramDataSpec1 = null)
    {
      this.id3Data = paramDataSpec1;
      this.previousExtractor = null;
      this.shouldSpliceIn = false;
      this.needNewExtractor = true;
      break;
      paramDataSpec1 = null;
      break label262;
    }
  }
  
  private static DataSource buildDataSource(DataSource paramDataSource, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null)) {
      return paramDataSource;
    }
    return new Aes128DataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2);
  }
  
  private Extractor buildPackedAudioExtractor(long paramLong)
  {
    Object localObject;
    if (this.lastPathSegment.endsWith(".aac")) {
      localObject = new AdtsExtractor(paramLong);
    }
    for (;;)
    {
      ((Extractor)localObject).init(this.extractorOutput);
      return (Extractor)localObject;
      if ((this.lastPathSegment.endsWith(".ac3")) || (this.lastPathSegment.endsWith(".ec3")))
      {
        localObject = new Ac3Extractor(paramLong);
      }
      else
      {
        if (!this.lastPathSegment.endsWith(".mp3")) {
          break;
        }
        localObject = new Mp3Extractor(paramLong);
      }
    }
    throw new IllegalArgumentException("Unkown extension for audio file: " + this.lastPathSegment);
  }
  
  private Extractor createExtractor()
  {
    int k = 1;
    Object localObject;
    int i;
    if (("text/vtt".equals(this.hlsUrl.format.sampleMimeType)) || (this.lastPathSegment.endsWith(".webvtt")) || (this.lastPathSegment.endsWith(".vtt")))
    {
      localObject = new WebvttExtractor(this.trackFormat.language, this.timestampAdjuster);
      i = k;
    }
    for (;;)
    {
      if (i != 0) {
        ((Extractor)localObject).init(this.extractorOutput);
      }
      return (Extractor)localObject;
      if (!this.needNewExtractor)
      {
        i = 0;
        localObject = this.previousExtractor;
      }
      else if (this.lastPathSegment.endsWith(".mp4"))
      {
        localObject = new FragmentedMp4Extractor(0, this.timestampAdjuster);
        i = k;
      }
      else
      {
        int j = 16;
        localObject = this.trackFormat.codecs;
        i = j;
        if (!TextUtils.isEmpty((CharSequence)localObject))
        {
          if (!"audio/mp4a-latm".equals(MimeTypes.getAudioMediaMimeType((String)localObject))) {
            j = 0x10 | 0x2;
          }
          i = j;
          if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType((String)localObject))) {
            i = j | 0x4;
          }
        }
        localObject = new TsExtractor(this.timestampAdjuster, new DefaultTsPayloadReaderFactory(i), true);
        i = k;
      }
    }
  }
  
  private void loadMedia()
    throws IOException, InterruptedException
  {
    Object localObject1;
    if (this.isEncrypted)
    {
      localObject1 = this.dataSpec;
      if (this.bytesLoaded != 0) {
        i = 1;
      }
    }
    long l;
    DataSpec localDataSpec;
    for (;;)
    {
      if (!this.isMasterTimestampSource) {
        this.timestampAdjuster.waitUntilInitialized();
      }
      try
      {
        localObject1 = new DefaultExtractorInput(this.dataSource, ((DataSpec)localObject1).absoluteStreamPosition, this.dataSource.open((DataSpec)localObject1));
        if (this.extractor != null) {
          break label149;
        }
        l = peekId3PrivTimestamp((ExtractorInput)localObject1);
        if (l != -9223372036854775807L) {
          break;
        }
        throw new ParserException("ID3 PRIV timestamp missing.");
      }
      finally
      {
        Util.closeQuietly(this.dataSource);
      }
      i = 0;
      continue;
      localDataSpec = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
      i = 0;
    }
    this.extractor = buildPackedAudioExtractor(this.timestampAdjuster.adjustTsTimestamp(l));
    label149:
    if (i != 0) {
      localDataSpec.skipFully(this.bytesLoaded);
    }
    int i = 0;
    for (;;)
    {
      if (i == 0) {}
      try
      {
        if (!this.loadCanceled)
        {
          i = this.extractor.read(localDataSpec, null);
          continue;
        }
        this.bytesLoaded = ((int)(localDataSpec.getPosition() - this.dataSpec.absoluteStreamPosition));
        Util.closeQuietly(this.dataSource);
        this.loadCompleted = true;
        return;
      }
      finally
      {
        this.bytesLoaded = ((int)(localDataSpec.getPosition() - this.dataSpec.absoluteStreamPosition));
      }
    }
  }
  
  /* Error */
  private void maybeLoadInitData()
    throws IOException, InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 134	com/google/android/exoplayer2/source/hls/HlsMediaChunk:previousExtractor	Lcom/google/android/exoplayer2/extractor/Extractor;
    //   4: aload_0
    //   5: getfield 132	com/google/android/exoplayer2/source/hls/HlsMediaChunk:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
    //   8: if_acmpeq +17 -> 25
    //   11: aload_0
    //   12: getfield 332	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initLoadCompleted	Z
    //   15: ifne +10 -> 25
    //   18: aload_0
    //   19: getfield 89	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   22: ifnonnull +4 -> 26
    //   25: return
    //   26: aload_0
    //   27: getfield 89	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   30: aload_0
    //   31: getfield 334	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initSegmentBytesLoaded	I
    //   34: invokestatic 305	com/google/android/exoplayer2/util/Util:getRemainderDataSpec	(Lcom/google/android/exoplayer2/upstream/DataSpec;I)Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   37: astore_2
    //   38: new 271	com/google/android/exoplayer2/extractor/DefaultExtractorInput
    //   41: dup
    //   42: aload_0
    //   43: getfield 140	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initDataSource	Lcom/google/android/exoplayer2/upstream/DataSource;
    //   46: aload_2
    //   47: getfield 275	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   50: aload_0
    //   51: getfield 140	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initDataSource	Lcom/google/android/exoplayer2/upstream/DataSource;
    //   54: aload_2
    //   55: invokeinterface 281 2 0
    //   60: invokespecial 284	com/google/android/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lcom/google/android/exoplayer2/upstream/DataSource;JJ)V
    //   63: astore_2
    //   64: iconst_0
    //   65: istore_1
    //   66: iload_1
    //   67: ifne +25 -> 92
    //   70: aload_0
    //   71: getfield 318	com/google/android/exoplayer2/source/hls/HlsMediaChunk:loadCanceled	Z
    //   74: ifne +18 -> 92
    //   77: aload_0
    //   78: getfield 132	com/google/android/exoplayer2/source/hls/HlsMediaChunk:extractor	Lcom/google/android/exoplayer2/extractor/Extractor;
    //   81: aload_2
    //   82: aconst_null
    //   83: invokeinterface 322 3 0
    //   88: istore_1
    //   89: goto -23 -> 66
    //   92: aload_0
    //   93: aload_2
    //   94: invokeinterface 326 1 0
    //   99: aload_0
    //   100: getfield 89	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   103: getfield 275	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   106: lsub
    //   107: l2i
    //   108: putfield 334	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initSegmentBytesLoaded	I
    //   111: aload_0
    //   112: getfield 100	com/google/android/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lcom/google/android/exoplayer2/upstream/DataSource;
    //   115: invokestatic 301	com/google/android/exoplayer2/util/Util:closeQuietly	(Lcom/google/android/exoplayer2/upstream/DataSource;)V
    //   118: aload_0
    //   119: iconst_1
    //   120: putfield 332	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initLoadCompleted	Z
    //   123: return
    //   124: astore_3
    //   125: aload_0
    //   126: aload_2
    //   127: invokeinterface 326 1 0
    //   132: aload_0
    //   133: getfield 89	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initDataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   136: getfield 275	com/google/android/exoplayer2/upstream/DataSpec:absoluteStreamPosition	J
    //   139: lsub
    //   140: l2i
    //   141: putfield 334	com/google/android/exoplayer2/source/hls/HlsMediaChunk:initSegmentBytesLoaded	I
    //   144: aload_3
    //   145: athrow
    //   146: astore_2
    //   147: aload_0
    //   148: getfield 100	com/google/android/exoplayer2/source/hls/HlsMediaChunk:dataSource	Lcom/google/android/exoplayer2/upstream/DataSource;
    //   151: invokestatic 301	com/google/android/exoplayer2/util/Util:closeQuietly	(Lcom/google/android/exoplayer2/upstream/DataSource;)V
    //   154: aload_2
    //   155: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	156	0	this	HlsMediaChunk
    //   65	24	1	i	int
    //   37	90	2	localObject1	Object
    //   146	9	2	localObject2	Object
    //   124	21	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   70	89	124	finally
    //   38	64	146	finally
    //   92	111	146	finally
    //   125	146	146	finally
  }
  
  private long peekId3PrivTimestamp(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.resetPeekPosition();
    if (!paramExtractorInput.peekFully(this.id3Data.data, 0, 10, true)) {
      return -9223372036854775807L;
    }
    this.id3Data.reset(10);
    if (this.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
      return -9223372036854775807L;
    }
    this.id3Data.skipBytes(3);
    int i = this.id3Data.readSynchSafeInt();
    int j = i + 10;
    Object localObject;
    if (j > this.id3Data.capacity())
    {
      localObject = this.id3Data.data;
      this.id3Data.reset(j);
      System.arraycopy(localObject, 0, this.id3Data.data, 0, 10);
    }
    if (!paramExtractorInput.peekFully(this.id3Data.data, 10, i, true)) {
      return -9223372036854775807L;
    }
    paramExtractorInput = this.id3Decoder.decode(this.id3Data.data, i);
    if (paramExtractorInput == null) {
      return -9223372036854775807L;
    }
    j = paramExtractorInput.length();
    i = 0;
    while (i < j)
    {
      localObject = paramExtractorInput.get(i);
      if ((localObject instanceof PrivFrame))
      {
        localObject = (PrivFrame)localObject;
        if ("com.apple.streaming.transportStreamTimestamp".equals(((PrivFrame)localObject).owner))
        {
          System.arraycopy(((PrivFrame)localObject).privateData, 0, this.id3Data.data, 0, 8);
          this.id3Data.reset(8);
          return this.id3Data.readLong();
        }
      }
      i += 1;
    }
    return -9223372036854775807L;
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public void init(HlsSampleStreamWrapper paramHlsSampleStreamWrapper)
  {
    this.extractorOutput = paramHlsSampleStreamWrapper;
    paramHlsSampleStreamWrapper.init(this.uid, this.shouldSpliceIn);
  }
  
  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }
  
  public void load()
    throws IOException, InterruptedException
  {
    if ((this.extractor == null) && (!this.isPackedAudio)) {
      this.extractor = createExtractor();
    }
    maybeLoadInitData();
    if (!this.loadCanceled) {
      loadMedia();
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/HlsMediaChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */