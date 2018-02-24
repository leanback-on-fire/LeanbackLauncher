package com.google.android.exoplayer2.decoder;

import java.nio.ByteBuffer;

public class SimpleOutputBuffer
  extends OutputBuffer
{
  public ByteBuffer data;
  private final SimpleDecoder<?, SimpleOutputBuffer, ?> owner;
  
  public SimpleOutputBuffer(SimpleDecoder<?, SimpleOutputBuffer, ?> paramSimpleDecoder)
  {
    this.owner = paramSimpleDecoder;
  }
  
  public void clear()
  {
    super.clear();
    if (this.data != null) {
      this.data.clear();
    }
  }
  
  public ByteBuffer init(long paramLong, int paramInt)
  {
    this.timeUs = paramLong;
    if ((this.data == null) || (this.data.capacity() < paramInt)) {
      this.data = ByteBuffer.allocateDirect(paramInt);
    }
    this.data.position(0);
    this.data.limit(paramInt);
    return this.data;
  }
  
  public void release()
  {
    this.owner.releaseOutputBuffer(this);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/decoder/SimpleOutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */