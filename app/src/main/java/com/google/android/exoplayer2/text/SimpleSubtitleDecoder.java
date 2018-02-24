package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.decoder.SimpleDecoder;
import java.nio.ByteBuffer;

public abstract class SimpleSubtitleDecoder
  extends SimpleDecoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException>
  implements SubtitleDecoder
{
  private final String name;
  
  protected SimpleSubtitleDecoder(String paramString)
  {
    super(new SubtitleInputBuffer[2], new SubtitleOutputBuffer[2]);
    this.name = paramString;
    setInitialInputBufferSize(1024);
  }
  
  protected final SubtitleInputBuffer createInputBuffer()
  {
    return new SubtitleInputBuffer();
  }
  
  protected final SubtitleOutputBuffer createOutputBuffer()
  {
    return new SimpleSubtitleOutputBuffer(this);
  }
  
  protected abstract Subtitle decode(byte[] paramArrayOfByte, int paramInt)
    throws SubtitleDecoderException;
  
  protected final SubtitleDecoderException decode(SubtitleInputBuffer paramSubtitleInputBuffer, SubtitleOutputBuffer paramSubtitleOutputBuffer, boolean paramBoolean)
  {
    try
    {
      Object localObject = paramSubtitleInputBuffer.data;
      localObject = decode(((ByteBuffer)localObject).array(), ((ByteBuffer)localObject).limit());
      paramSubtitleOutputBuffer.setContent(paramSubtitleInputBuffer.timeUs, (Subtitle)localObject, paramSubtitleInputBuffer.subsampleOffsetUs);
      return null;
    }
    catch (SubtitleDecoderException paramSubtitleInputBuffer) {}
    return paramSubtitleInputBuffer;
  }
  
  public final String getName()
  {
    return this.name;
  }
  
  protected final void releaseOutputBuffer(SubtitleOutputBuffer paramSubtitleOutputBuffer)
  {
    super.releaseOutputBuffer(paramSubtitleOutputBuffer);
  }
  
  public void setPositionUs(long paramLong) {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/SimpleSubtitleDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */