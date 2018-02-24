package com.google.android.exoplayer2.metadata;

import com.google.android.exoplayer2.Format;
import java.lang.reflect.Constructor;

public abstract interface MetadataDecoderFactory
{
  public static final MetadataDecoderFactory DEFAULT = new MetadataDecoderFactory()
  {
    private Class<?> getDecoderClass(String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      int i = -1;
      try
      {
        switch (paramAnonymousString.hashCode())
        {
        case -1248341703: 
          return Class.forName("com.google.android.exoplayer2.metadata.id3.Id3Decoder");
        }
      }
      catch (ClassNotFoundException paramAnonymousString)
      {
        return null;
      }
      if (paramAnonymousString.equals("application/id3"))
      {
        i = 0;
        break label113;
        if (paramAnonymousString.equals("application/x-emsg"))
        {
          i = 1;
          break label113;
          if (paramAnonymousString.equals("application/x-scte35"))
          {
            i = 2;
            break label113;
            return Class.forName("com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder");
            paramAnonymousString = Class.forName("com.google.android.exoplayer2.metadata.scte35.SpliceInfoDecoder");
            return paramAnonymousString;
          }
        }
      }
      label113:
      switch (i)
      {
      }
      return null;
    }
    
    public MetadataDecoder createDecoder(Format paramAnonymousFormat)
    {
      try
      {
        paramAnonymousFormat = getDecoderClass(paramAnonymousFormat.sampleMimeType);
        if (paramAnonymousFormat == null) {
          throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
        }
      }
      catch (Exception paramAnonymousFormat)
      {
        throw new IllegalStateException("Unexpected error instantiating decoder", paramAnonymousFormat);
      }
      paramAnonymousFormat = (MetadataDecoder)paramAnonymousFormat.asSubclass(MetadataDecoder.class).getConstructor(new Class[0]).newInstance(new Object[0]);
      return paramAnonymousFormat;
    }
    
    public boolean supportsFormat(Format paramAnonymousFormat)
    {
      return getDecoderClass(paramAnonymousFormat.sampleMimeType) != null;
    }
  };
  
  public abstract MetadataDecoder createDecoder(Format paramFormat);
  
  public abstract boolean supportsFormat(Format paramFormat);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/MetadataDecoderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */