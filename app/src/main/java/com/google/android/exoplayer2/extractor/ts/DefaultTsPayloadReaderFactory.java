package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DefaultTsPayloadReaderFactory
  implements TsPayloadReader.Factory
{
  public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
  public static final int FLAG_DETECT_ACCESS_UNITS = 8;
  public static final int FLAG_IGNORE_AAC_STREAM = 2;
  public static final int FLAG_IGNORE_H264_STREAM = 4;
  public static final int FLAG_IGNORE_SPLICE_INFO_STREAM = 16;
  private final int flags;
  
  public DefaultTsPayloadReaderFactory()
  {
    this(0);
  }
  
  public DefaultTsPayloadReaderFactory(int paramInt)
  {
    this.flags = paramInt;
  }
  
  private boolean isSet(int paramInt)
  {
    return (this.flags & paramInt) != 0;
  }
  
  public SparseArray<TsPayloadReader> createInitialPayloadReaders()
  {
    return new SparseArray();
  }
  
  public TsPayloadReader createPayloadReader(int paramInt, TsPayloadReader.EsInfo paramEsInfo)
  {
    switch (paramInt)
    {
    default: 
    case 3: 
    case 4: 
    case 15: 
    case 129: 
    case 135: 
    case 130: 
    case 138: 
    case 2: 
    case 27: 
    case 36: 
    case 134: 
      do
      {
        do
        {
          do
          {
            return null;
            return new PesReader(new MpegAudioReader(paramEsInfo.language));
          } while (isSet(2));
          return new PesReader(new AdtsReader(false, paramEsInfo.language));
          return new PesReader(new Ac3Reader(paramEsInfo.language));
          return new PesReader(new DtsReader(paramEsInfo.language));
          return new PesReader(new H262Reader());
        } while (isSet(4));
        return new PesReader(new H264Reader(isSet(1), isSet(8)));
        return new PesReader(new H265Reader());
      } while (isSet(16));
      return new SectionReader(new SpliceInfoSectionReader());
    }
    return new PesReader(new Id3Reader());
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/ts/DefaultTsPayloadReaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */