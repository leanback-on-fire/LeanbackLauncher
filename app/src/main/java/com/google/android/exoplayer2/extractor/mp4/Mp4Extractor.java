package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class Mp4Extractor
  implements Extractor, SeekMap
{
  private static final int BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Mp4Extractor() };
    }
  };
  private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144L;
  private static final int STATE_READING_ATOM_HEADER = 0;
  private static final int STATE_READING_ATOM_PAYLOAD = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private ParsableByteArray atomData;
  private final ParsableByteArray atomHeader = new ParsableByteArray(16);
  private int atomHeaderBytesRead;
  private long atomSize;
  private int atomType;
  private final Stack<Atom.ContainerAtom> containerAtoms = new Stack();
  private long durationUs;
  private ExtractorOutput extractorOutput;
  private boolean isQuickTime;
  private final ParsableByteArray nalLength = new ParsableByteArray(4);
  private final ParsableByteArray nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
  private int parserState;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private Mp4Track[] tracks;
  
  private void enterReadingAtomHeaderState()
  {
    this.parserState = 0;
    this.atomHeaderBytesRead = 0;
  }
  
  private int getTrackIndexOfEarliestCurrentSample()
  {
    int j = -1;
    long l1 = Long.MAX_VALUE;
    int i = 0;
    if (i < this.tracks.length)
    {
      Mp4Track localMp4Track = this.tracks[i];
      int k = localMp4Track.sampleIndex;
      long l2;
      if (k == localMp4Track.sampleTable.sampleCount) {
        l2 = l1;
      }
      for (;;)
      {
        i += 1;
        l1 = l2;
        break;
        long l3 = localMp4Track.sampleTable.offsets[k];
        l2 = l1;
        if (l3 < l1)
        {
          l2 = l3;
          j = i;
        }
      }
    }
    return j;
  }
  
  private void processAtomEnded(long paramLong)
    throws ParserException
  {
    while ((!this.containerAtoms.isEmpty()) && (((Atom.ContainerAtom)this.containerAtoms.peek()).endPosition == paramLong))
    {
      Atom.ContainerAtom localContainerAtom = (Atom.ContainerAtom)this.containerAtoms.pop();
      if (localContainerAtom.type == Atom.TYPE_moov)
      {
        processMoovAtom(localContainerAtom);
        this.containerAtoms.clear();
        this.parserState = 2;
      }
      else if (!this.containerAtoms.isEmpty())
      {
        ((Atom.ContainerAtom)this.containerAtoms.peek()).add(localContainerAtom);
      }
    }
    if (this.parserState != 2) {
      enterReadingAtomHeaderState();
    }
  }
  
  private static boolean processFtypAtom(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (paramParsableByteArray.readInt() == BRAND_QUICKTIME) {
      return true;
    }
    paramParsableByteArray.skipBytes(4);
    while (paramParsableByteArray.bytesLeft() > 0) {
      if (paramParsableByteArray.readInt() == BRAND_QUICKTIME) {
        return true;
      }
    }
    return false;
  }
  
  private void processMoovAtom(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    long l4 = -9223372036854775807L;
    ArrayList localArrayList = new ArrayList();
    long l1 = Long.MAX_VALUE;
    Object localObject3 = null;
    GaplessInfoHolder localGaplessInfoHolder = new GaplessInfoHolder();
    Object localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_udta);
    if (localObject1 != null)
    {
      localObject1 = AtomParsers.parseUdta((Atom.LeafAtom)localObject1, this.isQuickTime);
      localObject3 = localObject1;
      if (localObject1 != null)
      {
        localGaplessInfoHolder.setFromMetadata((Metadata)localObject1);
        localObject3 = localObject1;
      }
    }
    int i = 0;
    if (i < paramContainerAtom.containerChildren.size())
    {
      localObject1 = (Atom.ContainerAtom)paramContainerAtom.containerChildren.get(i);
      long l3;
      long l2;
      if (((Atom.ContainerAtom)localObject1).type != Atom.TYPE_trak)
      {
        l3 = l1;
        l2 = l4;
      }
      for (;;)
      {
        i += 1;
        l4 = l2;
        l1 = l3;
        break;
        Track localTrack = AtomParsers.parseTrak((Atom.ContainerAtom)localObject1, paramContainerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), -9223372036854775807L, null, this.isQuickTime);
        l2 = l4;
        l3 = l1;
        if (localTrack != null)
        {
          TrackSampleTable localTrackSampleTable = AtomParsers.parseStbl(localTrack, ((Atom.ContainerAtom)localObject1).getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), localGaplessInfoHolder);
          l2 = l4;
          l3 = l1;
          if (localTrackSampleTable.sampleCount != 0)
          {
            Mp4Track localMp4Track = new Mp4Track(localTrack, localTrackSampleTable, this.extractorOutput.track(i));
            int j = localTrackSampleTable.maximumSize;
            Format localFormat = localTrack.format.copyWithMaxInputSize(j + 30);
            Object localObject2 = localFormat;
            if (localTrack.type == 1)
            {
              localObject1 = localFormat;
              if (localGaplessInfoHolder.hasGaplessInfo()) {
                localObject1 = localFormat.copyWithGaplessInfo(localGaplessInfoHolder.encoderDelay, localGaplessInfoHolder.encoderPadding);
              }
              localObject2 = localObject1;
              if (localObject3 != null) {
                localObject2 = ((Format)localObject1).copyWithMetadata((Metadata)localObject3);
              }
            }
            localMp4Track.trackOutput.format((Format)localObject2);
            l4 = Math.max(l4, localTrack.durationUs);
            localArrayList.add(localMp4Track);
            long l5 = localTrackSampleTable.offsets[0];
            l2 = l4;
            l3 = l1;
            if (l5 < l1)
            {
              l3 = l5;
              l2 = l4;
            }
          }
        }
      }
    }
    this.durationUs = l4;
    this.tracks = ((Mp4Track[])localArrayList.toArray(new Mp4Track[localArrayList.size()]));
    this.extractorOutput.endTracks();
    this.extractorOutput.seekMap(this);
  }
  
  private boolean readAtomHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (this.atomHeaderBytesRead == 0)
    {
      if (!paramExtractorInput.readFully(this.atomHeader.data, 0, 8, true)) {
        return false;
      }
      this.atomHeaderBytesRead = 8;
      this.atomHeader.setPosition(0);
      this.atomSize = this.atomHeader.readUnsignedInt();
      this.atomType = this.atomHeader.readInt();
    }
    if (this.atomSize == 1L)
    {
      paramExtractorInput.readFully(this.atomHeader.data, 8, 8);
      this.atomHeaderBytesRead += 8;
      this.atomSize = this.atomHeader.readUnsignedLongToLong();
    }
    if (shouldParseContainerAtom(this.atomType))
    {
      long l = paramExtractorInput.getPosition() + this.atomSize - this.atomHeaderBytesRead;
      this.containerAtoms.add(new Atom.ContainerAtom(this.atomType, l));
      if (this.atomSize == this.atomHeaderBytesRead) {
        processAtomEnded(l);
      }
    }
    for (;;)
    {
      return true;
      enterReadingAtomHeaderState();
      continue;
      if (shouldParseLeafAtom(this.atomType))
      {
        if (this.atomHeaderBytesRead == 8)
        {
          bool = true;
          label210:
          Assertions.checkState(bool);
          if (this.atomSize > 2147483647L) {
            break label285;
          }
        }
        label285:
        for (boolean bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          this.atomData = new ParsableByteArray((int)this.atomSize);
          System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
          this.parserState = 1;
          break;
          bool = false;
          break label210;
        }
      }
      this.atomData = null;
      this.parserState = 1;
    }
  }
  
  private boolean readAtomPayload(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l1 = this.atomSize - this.atomHeaderBytesRead;
    long l2 = paramExtractorInput.getPosition();
    int j = 0;
    int i;
    if (this.atomData != null)
    {
      paramExtractorInput.readFully(this.atomData.data, this.atomHeaderBytesRead, (int)l1);
      if (this.atomType == Atom.TYPE_ftyp)
      {
        this.isQuickTime = processFtypAtom(this.atomData);
        i = j;
      }
    }
    for (;;)
    {
      processAtomEnded(l2 + l1);
      if ((i == 0) || (this.parserState == 2)) {
        break;
      }
      return true;
      i = j;
      if (!this.containerAtoms.isEmpty())
      {
        ((Atom.ContainerAtom)this.containerAtoms.peek()).add(new Atom.LeafAtom(this.atomType, this.atomData));
        i = j;
        continue;
        if (l1 < 262144L)
        {
          paramExtractorInput.skipFully((int)l1);
          i = j;
        }
        else
        {
          paramPositionHolder.position = (paramExtractorInput.getPosition() + l1);
          i = 1;
        }
      }
    }
    return false;
  }
  
  private int readSample(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int i = getTrackIndexOfEarliestCurrentSample();
    if (i == -1) {
      return -1;
    }
    Mp4Track localMp4Track = this.tracks[i];
    TrackOutput localTrackOutput = localMp4Track.trackOutput;
    int k = localMp4Track.sampleIndex;
    long l2 = localMp4Track.sampleTable.offsets[k];
    int j = localMp4Track.sampleTable.sizes[k];
    i = j;
    long l1 = l2;
    if (localMp4Track.track.sampleTransformation == 1)
    {
      l1 = l2 + 8L;
      i = j - 8;
    }
    l2 = l1 - paramExtractorInput.getPosition() + this.sampleBytesWritten;
    if ((l2 < 0L) || (l2 >= 262144L))
    {
      paramPositionHolder.position = l1;
      return 1;
    }
    paramExtractorInput.skipFully((int)l2);
    if (localMp4Track.track.nalUnitLengthFieldLength != 0)
    {
      paramPositionHolder = this.nalLength.data;
      paramPositionHolder[0] = 0;
      paramPositionHolder[1] = 0;
      paramPositionHolder[2] = 0;
      int m = localMp4Track.track.nalUnitLengthFieldLength;
      int n = 4 - localMp4Track.track.nalUnitLengthFieldLength;
      for (;;)
      {
        j = i;
        if (this.sampleBytesWritten >= i) {
          break;
        }
        if (this.sampleCurrentNalBytesRemaining == 0)
        {
          paramExtractorInput.readFully(this.nalLength.data, n, m);
          this.nalLength.setPosition(0);
          this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
          this.nalStartCode.setPosition(0);
          localTrackOutput.sampleData(this.nalStartCode, 4);
          this.sampleBytesWritten += 4;
          i += n;
        }
        else
        {
          j = localTrackOutput.sampleData(paramExtractorInput, this.sampleCurrentNalBytesRemaining, false);
          this.sampleBytesWritten += j;
          this.sampleCurrentNalBytesRemaining -= j;
        }
      }
    }
    for (;;)
    {
      j = i;
      if (this.sampleBytesWritten >= i) {
        break;
      }
      j = localTrackOutput.sampleData(paramExtractorInput, i - this.sampleBytesWritten, false);
      this.sampleBytesWritten += j;
      this.sampleCurrentNalBytesRemaining -= j;
    }
    localTrackOutput.sampleMetadata(localMp4Track.sampleTable.timestampsUs[k], localMp4Track.sampleTable.flags[k], j, 0, null);
    localMp4Track.sampleIndex += 1;
    this.sampleBytesWritten = 0;
    this.sampleCurrentNalBytesRemaining = 0;
    return 0;
  }
  
  private static boolean shouldParseContainerAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_moov) || (paramInt == Atom.TYPE_trak) || (paramInt == Atom.TYPE_mdia) || (paramInt == Atom.TYPE_minf) || (paramInt == Atom.TYPE_stbl) || (paramInt == Atom.TYPE_edts);
  }
  
  private static boolean shouldParseLeafAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_mdhd) || (paramInt == Atom.TYPE_mvhd) || (paramInt == Atom.TYPE_hdlr) || (paramInt == Atom.TYPE_stsd) || (paramInt == Atom.TYPE_stts) || (paramInt == Atom.TYPE_stss) || (paramInt == Atom.TYPE_ctts) || (paramInt == Atom.TYPE_elst) || (paramInt == Atom.TYPE_stsc) || (paramInt == Atom.TYPE_stsz) || (paramInt == Atom.TYPE_stz2) || (paramInt == Atom.TYPE_stco) || (paramInt == Atom.TYPE_co64) || (paramInt == Atom.TYPE_tkhd) || (paramInt == Atom.TYPE_ftyp) || (paramInt == Atom.TYPE_udta);
  }
  
  private void updateSampleIndices(long paramLong)
  {
    Mp4Track[] arrayOfMp4Track = this.tracks;
    int m = arrayOfMp4Track.length;
    int i = 0;
    while (i < m)
    {
      Mp4Track localMp4Track = arrayOfMp4Track[i];
      TrackSampleTable localTrackSampleTable = localMp4Track.sampleTable;
      int k = localTrackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(paramLong);
      int j = k;
      if (k == -1) {
        j = localTrackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(paramLong);
      }
      localMp4Track.sampleIndex = j;
      i += 1;
    }
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public long getPosition(long paramLong)
  {
    long l1 = Long.MAX_VALUE;
    Mp4Track[] arrayOfMp4Track = this.tracks;
    int m = arrayOfMp4Track.length;
    int i = 0;
    while (i < m)
    {
      TrackSampleTable localTrackSampleTable = arrayOfMp4Track[i].sampleTable;
      int k = localTrackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(paramLong);
      int j = k;
      if (k == -1) {
        j = localTrackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(paramLong);
      }
      long l3 = localTrackSampleTable.offsets[j];
      long l2 = l1;
      if (l3 < l1) {
        l2 = l3;
      }
      i += 1;
      l1 = l2;
    }
    return l1;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
  }
  
  public boolean isSeekable()
  {
    return true;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    do
    {
      do
      {
        switch (this.parserState)
        {
        default: 
          throw new IllegalStateException();
        }
      } while (readAtomHeader(paramExtractorInput));
      return -1;
    } while (!readAtomPayload(paramExtractorInput, paramPositionHolder));
    return 1;
    return readSample(paramExtractorInput, paramPositionHolder);
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.containerAtoms.clear();
    this.atomHeaderBytesRead = 0;
    this.sampleBytesWritten = 0;
    this.sampleCurrentNalBytesRemaining = 0;
    if (paramLong1 == 0L) {
      enterReadingAtomHeaderState();
    }
    while (this.tracks == null) {
      return;
    }
    updateSampleIndices(paramLong2);
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return Sniffer.sniffUnfragmented(paramExtractorInput);
  }
  
  private static final class Mp4Track
  {
    public int sampleIndex;
    public final TrackSampleTable sampleTable;
    public final Track track;
    public final TrackOutput trackOutput;
    
    public Mp4Track(Track paramTrack, TrackSampleTable paramTrackSampleTable, TrackOutput paramTrackOutput)
    {
      this.track = paramTrack;
      this.sampleTable = paramTrackSampleTable;
      this.trackOutput = paramTrackOutput;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/mp4/Mp4Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */