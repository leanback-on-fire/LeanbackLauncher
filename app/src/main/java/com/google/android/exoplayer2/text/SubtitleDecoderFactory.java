package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.Format;
import java.lang.reflect.Constructor;

public abstract interface SubtitleDecoderFactory
{
  public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory()
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
        case -1004728940: 
          return Class.forName("com.google.android.exoplayer2.text.webvtt.WebvttDecoder");
        }
      }
      catch (ClassNotFoundException paramAnonymousString)
      {
        return null;
      }
      if (paramAnonymousString.equals("text/vtt"))
      {
        i = 0;
        break label249;
        if (paramAnonymousString.equals("application/ttml+xml"))
        {
          i = 1;
          break label249;
          if (paramAnonymousString.equals("application/x-mp4-vtt"))
          {
            i = 2;
            break label249;
            if (paramAnonymousString.equals("application/x-subrip"))
            {
              i = 3;
              break label249;
              if (paramAnonymousString.equals("application/x-quicktime-tx3g"))
              {
                i = 4;
                break label249;
                if (paramAnonymousString.equals("application/cea-608"))
                {
                  i = 5;
                  break label249;
                  if (paramAnonymousString.equals("application/x-mp4-cea-608"))
                  {
                    i = 6;
                    break label249;
                    if (paramAnonymousString.equals("application/cea-708"))
                    {
                      i = 7;
                      break label249;
                      return Class.forName("com.google.android.exoplayer2.text.ttml.TtmlDecoder");
                      return Class.forName("com.google.android.exoplayer2.text.webvtt.Mp4WebvttDecoder");
                      return Class.forName("com.google.android.exoplayer2.text.subrip.SubripDecoder");
                      return Class.forName("com.google.android.exoplayer2.text.tx3g.Tx3gDecoder");
                      return Class.forName("com.google.android.exoplayer2.text.cea.Cea608Decoder");
                      paramAnonymousString = Class.forName("com.google.android.exoplayer2.text.cea.Cea708Decoder");
                      return paramAnonymousString;
                    }
                  }
                }
              }
            }
          }
        }
      }
      label249:
      switch (i)
      {
      }
      return null;
    }
    
    public SubtitleDecoder createDecoder(Format paramAnonymousFormat)
    {
      Class localClass;
      try
      {
        localClass = getDecoderClass(paramAnonymousFormat.sampleMimeType);
        if (localClass == null) {
          throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
        }
      }
      catch (Exception paramAnonymousFormat)
      {
        throw new IllegalStateException("Unexpected error instantiating decoder", paramAnonymousFormat);
      }
      if ((paramAnonymousFormat.sampleMimeType.equals("application/cea-608")) || (paramAnonymousFormat.sampleMimeType.equals("application/x-mp4-cea-608"))) {
        return (SubtitleDecoder)localClass.asSubclass(SubtitleDecoder.class).getConstructor(new Class[] { String.class, Integer.TYPE }).newInstance(new Object[] { paramAnonymousFormat.sampleMimeType, Integer.valueOf(paramAnonymousFormat.accessibilityChannel) });
      }
      if (paramAnonymousFormat.sampleMimeType.equals("application/cea-708")) {
        return (SubtitleDecoder)localClass.asSubclass(SubtitleDecoder.class).getConstructor(new Class[] { Integer.TYPE }).newInstance(new Object[] { Integer.valueOf(paramAnonymousFormat.accessibilityChannel) });
      }
      paramAnonymousFormat = (SubtitleDecoder)localClass.asSubclass(SubtitleDecoder.class).getConstructor(new Class[0]).newInstance(new Object[0]);
      return paramAnonymousFormat;
    }
    
    public boolean supportsFormat(Format paramAnonymousFormat)
    {
      return getDecoderClass(paramAnonymousFormat.sampleMimeType) != null;
    }
  };
  
  public abstract SubtitleDecoder createDecoder(Format paramFormat);
  
  public abstract boolean supportsFormat(Format paramFormat);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/SubtitleDecoderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */