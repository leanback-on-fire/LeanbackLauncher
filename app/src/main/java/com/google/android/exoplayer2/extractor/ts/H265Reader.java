package com.google.android.exoplayer2.extractor.ts;

import android.util.Log;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.ParsableNalUnitBitArray;
import java.util.Collections;

final class H265Reader
  implements ElementaryStreamReader
{
  private static final int BLA_W_LP = 16;
  private static final int CRA_NUT = 21;
  private static final int PPS_NUT = 34;
  private static final int PREFIX_SEI_NUT = 39;
  private static final int RASL_R = 9;
  private static final int SPS_NUT = 33;
  private static final int SUFFIX_SEI_NUT = 40;
  private static final String TAG = "H265Reader";
  private static final int VPS_NUT = 32;
  private boolean hasOutputFormat;
  private TrackOutput output;
  private long pesTimeUs;
  private final NalUnitTargetBuffer pps = new NalUnitTargetBuffer(34, 128);
  private final boolean[] prefixFlags = new boolean[3];
  private final NalUnitTargetBuffer prefixSei = new NalUnitTargetBuffer(39, 128);
  private SampleReader sampleReader;
  private SeiReader seiReader;
  private final ParsableByteArray seiWrapper = new ParsableByteArray();
  private final NalUnitTargetBuffer sps = new NalUnitTargetBuffer(33, 128);
  private final NalUnitTargetBuffer suffixSei = new NalUnitTargetBuffer(40, 128);
  private long totalBytesWritten;
  private final NalUnitTargetBuffer vps = new NalUnitTargetBuffer(32, 128);
  
  private void endNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    if (this.hasOutputFormat) {
      this.sampleReader.endNalUnit(paramLong1, paramInt1);
    }
    for (;;)
    {
      if (this.prefixSei.endNalUnit(paramInt2))
      {
        paramInt1 = NalUnitUtil.unescapeStream(this.prefixSei.nalData, this.prefixSei.nalLength);
        this.seiWrapper.reset(this.prefixSei.nalData, paramInt1);
        this.seiWrapper.skipBytes(5);
        this.seiReader.consume(paramLong2, this.seiWrapper);
      }
      if (this.suffixSei.endNalUnit(paramInt2))
      {
        paramInt1 = NalUnitUtil.unescapeStream(this.suffixSei.nalData, this.suffixSei.nalLength);
        this.seiWrapper.reset(this.suffixSei.nalData, paramInt1);
        this.seiWrapper.skipBytes(5);
        this.seiReader.consume(paramLong2, this.seiWrapper);
      }
      return;
      this.vps.endNalUnit(paramInt2);
      this.sps.endNalUnit(paramInt2);
      this.pps.endNalUnit(paramInt2);
      if ((this.vps.isCompleted()) && (this.sps.isCompleted()) && (this.pps.isCompleted()))
      {
        this.output.format(parseMediaFormat(this.vps, this.sps, this.pps));
        this.hasOutputFormat = true;
      }
    }
  }
  
  private void nalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.hasOutputFormat) {
      this.sampleReader.readNalUnitData(paramArrayOfByte, paramInt1, paramInt2);
    }
    for (;;)
    {
      this.prefixSei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.suffixSei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      return;
      this.vps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.sps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.pps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  private static Format parseMediaFormat(NalUnitTargetBuffer paramNalUnitTargetBuffer1, NalUnitTargetBuffer paramNalUnitTargetBuffer2, NalUnitTargetBuffer paramNalUnitTargetBuffer3)
  {
    byte[] arrayOfByte = new byte[paramNalUnitTargetBuffer1.nalLength + paramNalUnitTargetBuffer2.nalLength + paramNalUnitTargetBuffer3.nalLength];
    System.arraycopy(paramNalUnitTargetBuffer1.nalData, 0, arrayOfByte, 0, paramNalUnitTargetBuffer1.nalLength);
    System.arraycopy(paramNalUnitTargetBuffer2.nalData, 0, arrayOfByte, paramNalUnitTargetBuffer1.nalLength, paramNalUnitTargetBuffer2.nalLength);
    System.arraycopy(paramNalUnitTargetBuffer3.nalData, 0, arrayOfByte, paramNalUnitTargetBuffer1.nalLength + paramNalUnitTargetBuffer2.nalLength, paramNalUnitTargetBuffer3.nalLength);
    paramNalUnitTargetBuffer1 = new ParsableNalUnitBitArray(paramNalUnitTargetBuffer2.nalData, 0, paramNalUnitTargetBuffer2.nalLength);
    paramNalUnitTargetBuffer1.skipBits(44);
    int m = paramNalUnitTargetBuffer1.readBits(3);
    paramNalUnitTargetBuffer1.skipBits(1);
    paramNalUnitTargetBuffer1.skipBits(88);
    paramNalUnitTargetBuffer1.skipBits(8);
    int i = 0;
    int k = 0;
    while (k < m)
    {
      j = i;
      if (paramNalUnitTargetBuffer1.readBit()) {
        j = i + 89;
      }
      i = j;
      if (paramNalUnitTargetBuffer1.readBit()) {
        i = j + 8;
      }
      k += 1;
    }
    paramNalUnitTargetBuffer1.skipBits(i);
    if (m > 0) {
      paramNalUnitTargetBuffer1.skipBits((8 - m) * 2);
    }
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    int i5 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    if (i5 == 3) {
      paramNalUnitTargetBuffer1.skipBits(1);
    }
    int i1 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    int n = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    k = i1;
    int j = n;
    if (paramNalUnitTargetBuffer1.readBit())
    {
      k = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i4 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i2 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i3 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      if ((i5 == 1) || (i5 == 2))
      {
        i = 2;
        if (i5 != 1) {
          break label387;
        }
        j = 2;
        label298:
        k = i1 - (k + i4) * i;
        j = n - (i2 + i3) * j;
      }
    }
    else
    {
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      n = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      if (!paramNalUnitTargetBuffer1.readBit()) {
        break label393;
      }
      i = 0;
    }
    for (;;)
    {
      if (i > m) {
        break label400;
      }
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      i += 1;
      continue;
      i = 1;
      break;
      label387:
      j = 1;
      break label298;
      label393:
      i = m;
    }
    label400:
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    if ((paramNalUnitTargetBuffer1.readBit()) && (paramNalUnitTargetBuffer1.readBit())) {
      skipScalingList(paramNalUnitTargetBuffer1);
    }
    paramNalUnitTargetBuffer1.skipBits(2);
    if (paramNalUnitTargetBuffer1.readBit())
    {
      paramNalUnitTargetBuffer1.skipBits(8);
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.skipBits(1);
    }
    skipShortTermRefPicSets(paramNalUnitTargetBuffer1);
    if (paramNalUnitTargetBuffer1.readBit())
    {
      i = 0;
      while (i < paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt())
      {
        paramNalUnitTargetBuffer1.skipBits(n + 4 + 1);
        i += 1;
      }
    }
    paramNalUnitTargetBuffer1.skipBits(2);
    float f2 = 1.0F;
    float f1 = f2;
    if (paramNalUnitTargetBuffer1.readBit())
    {
      f1 = f2;
      if (paramNalUnitTargetBuffer1.readBit())
      {
        i = paramNalUnitTargetBuffer1.readBits(8);
        if (i != 255) {
          break label631;
        }
        i = paramNalUnitTargetBuffer1.readBits(16);
        m = paramNalUnitTargetBuffer1.readBits(16);
        f1 = f2;
        if (i != 0)
        {
          f1 = f2;
          if (m != 0) {
            f1 = i / m;
          }
        }
      }
    }
    for (;;)
    {
      return Format.createVideoSampleFormat(null, "video/hevc", null, -1, -1, k, j, -1.0F, Collections.singletonList(arrayOfByte), -1, f1, null);
      label631:
      if (i < NalUnitUtil.ASPECT_RATIO_IDC_VALUES.length)
      {
        f1 = NalUnitUtil.ASPECT_RATIO_IDC_VALUES[i];
      }
      else
      {
        Log.w("H265Reader", "Unexpected aspect_ratio_idc value: " + i);
        f1 = f2;
      }
    }
  }
  
  private static void skipScalingList(ParsableNalUnitBitArray paramParsableNalUnitBitArray)
  {
    int i = 0;
    while (i < 4)
    {
      int j = 0;
      if (j < 6)
      {
        if (!paramParsableNalUnitBitArray.readBit())
        {
          paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
          label27:
          if (i != 3) {
            break label85;
          }
        }
        label85:
        for (int k = 3;; k = 1)
        {
          j += k;
          break;
          int m = Math.min(64, 1 << (i << 1) + 4);
          if (i > 1) {
            paramParsableNalUnitBitArray.readSignedExpGolombCodedInt();
          }
          k = 0;
          while (k < m)
          {
            paramParsableNalUnitBitArray.readSignedExpGolombCodedInt();
            k += 1;
          }
          break label27;
        }
      }
      i += 1;
    }
  }
  
  private static void skipShortTermRefPicSets(ParsableNalUnitBitArray paramParsableNalUnitBitArray)
  {
    int n = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
    boolean bool = false;
    int k = 0;
    int i = 0;
    while (i < n)
    {
      if (i != 0) {
        bool = paramParsableNalUnitBitArray.readBit();
      }
      if (bool)
      {
        paramParsableNalUnitBitArray.skipBits(1);
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        m = 0;
        for (;;)
        {
          j = k;
          if (m > k) {
            break;
          }
          if (paramParsableNalUnitBitArray.readBit()) {
            paramParsableNalUnitBitArray.skipBits(1);
          }
          m += 1;
        }
      }
      k = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
      int i1 = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
      int m = k + i1;
      int j = 0;
      while (j < k)
      {
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        paramParsableNalUnitBitArray.skipBits(1);
        j += 1;
      }
      k = 0;
      for (;;)
      {
        j = m;
        if (k >= i1) {
          break;
        }
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        paramParsableNalUnitBitArray.skipBits(1);
        k += 1;
      }
      i += 1;
      k = j;
    }
  }
  
  private void startNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    if (this.hasOutputFormat) {
      this.sampleReader.startNalUnit(paramLong1, paramInt1, paramInt2, paramLong2);
    }
    for (;;)
    {
      this.prefixSei.startNalUnit(paramInt2);
      this.suffixSei.startNalUnit(paramInt2);
      return;
      this.vps.startNalUnit(paramInt2);
      this.sps.startNalUnit(paramInt2);
      this.pps.startNalUnit(paramInt2);
    }
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int j;
    byte[] arrayOfByte;
    int k;
    while (paramParsableByteArray.bytesLeft() > 0)
    {
      i = paramParsableByteArray.getPosition();
      j = paramParsableByteArray.limit();
      arrayOfByte = paramParsableByteArray.data;
      this.totalBytesWritten += paramParsableByteArray.bytesLeft();
      this.output.sampleData(paramParsableByteArray, paramParsableByteArray.bytesLeft());
      if (i < j)
      {
        k = NalUnitUtil.findNalUnit(arrayOfByte, i, j, this.prefixFlags);
        if (k != j) {
          break label84;
        }
        nalUnitData(arrayOfByte, i, j);
      }
    }
    return;
    label84:
    int m = NalUnitUtil.getH265NalUnitType(arrayOfByte, k);
    int i1 = k - i;
    if (i1 > 0) {
      nalUnitData(arrayOfByte, i, k);
    }
    int n = j - k;
    long l = this.totalBytesWritten - n;
    if (i1 < 0) {}
    for (int i = -i1;; i = 0)
    {
      endNalUnit(l, n, i, this.pesTimeUs);
      startNalUnit(l, n, m, this.pesTimeUs);
      i = k + 3;
      break;
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getNextId());
    this.sampleReader = new SampleReader(this.output);
    this.seiReader = new SeiReader(paramExtractorOutput.track(paramTrackIdGenerator.getNextId()));
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.pesTimeUs = paramLong;
  }
  
  public void seek()
  {
    NalUnitUtil.clearPrefixFlags(this.prefixFlags);
    this.vps.reset();
    this.sps.reset();
    this.pps.reset();
    this.prefixSei.reset();
    this.suffixSei.reset();
    this.sampleReader.reset();
    this.totalBytesWritten = 0L;
  }
  
  private static final class SampleReader
  {
    private static final int FIRST_SLICE_FLAG_OFFSET = 2;
    private boolean isFirstParameterSet;
    private boolean isFirstSlice;
    private boolean lookingForFirstSliceFlag;
    private int nalUnitBytesRead;
    private boolean nalUnitHasKeyframeData;
    private long nalUnitStartPosition;
    private long nalUnitTimeUs;
    private final TrackOutput output;
    private boolean readingSample;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean writingParameterSets;
    
    public SampleReader(TrackOutput paramTrackOutput)
    {
      this.output = paramTrackOutput;
    }
    
    private void outputSample(int paramInt)
    {
      if (this.sampleIsKeyframe) {}
      for (int i = 1;; i = 0)
      {
        int j = (int)(this.nalUnitStartPosition - this.samplePosition);
        this.output.sampleMetadata(this.sampleTimeUs, i, j, paramInt, null);
        return;
      }
    }
    
    public void endNalUnit(long paramLong, int paramInt)
    {
      if ((this.writingParameterSets) && (this.isFirstSlice))
      {
        this.sampleIsKeyframe = this.nalUnitHasKeyframeData;
        this.writingParameterSets = false;
      }
      while ((!this.isFirstParameterSet) && (!this.isFirstSlice)) {
        return;
      }
      if (this.readingSample) {
        outputSample(paramInt + (int)(paramLong - this.nalUnitStartPosition));
      }
      this.samplePosition = this.nalUnitStartPosition;
      this.sampleTimeUs = this.nalUnitTimeUs;
      this.readingSample = true;
      this.sampleIsKeyframe = this.nalUnitHasKeyframeData;
    }
    
    public void readNalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (this.lookingForFirstSliceFlag)
      {
        int i = paramInt1 + 2 - this.nalUnitBytesRead;
        if (i >= paramInt2) {
          break label55;
        }
        if ((paramArrayOfByte[i] & 0x80) == 0) {
          break label49;
        }
      }
      label49:
      for (boolean bool = true;; bool = false)
      {
        this.isFirstSlice = bool;
        this.lookingForFirstSliceFlag = false;
        return;
      }
      label55:
      this.nalUnitBytesRead += paramInt2 - paramInt1;
    }
    
    public void reset()
    {
      this.lookingForFirstSliceFlag = false;
      this.isFirstSlice = false;
      this.isFirstParameterSet = false;
      this.readingSample = false;
      this.writingParameterSets = false;
    }
    
    public void startNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
    {
      boolean bool2 = false;
      this.isFirstSlice = false;
      this.isFirstParameterSet = false;
      this.nalUnitTimeUs = paramLong2;
      this.nalUnitBytesRead = 0;
      this.nalUnitStartPosition = paramLong1;
      if (paramInt2 >= 32)
      {
        if ((!this.writingParameterSets) && (this.readingSample))
        {
          outputSample(paramInt1);
          this.readingSample = false;
        }
        if (paramInt2 <= 34)
        {
          if (this.writingParameterSets) {
            break label139;
          }
          bool1 = true;
          this.isFirstParameterSet = bool1;
          this.writingParameterSets = true;
        }
      }
      if ((paramInt2 >= 16) && (paramInt2 <= 21)) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        this.nalUnitHasKeyframeData = bool1;
        if (!this.nalUnitHasKeyframeData)
        {
          bool1 = bool2;
          if (paramInt2 > 9) {}
        }
        else
        {
          bool1 = true;
        }
        this.lookingForFirstSliceFlag = bool1;
        return;
        label139:
        bool1 = false;
        break;
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/ts/H265Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */