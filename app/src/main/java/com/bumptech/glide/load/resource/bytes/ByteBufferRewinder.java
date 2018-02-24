package com.bumptech.glide.load.resource.bytes;

import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.data.DataRewinder.Factory;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferRewinder
  implements DataRewinder<ByteBuffer>
{
  private final ByteBuffer buffer;
  
  public ByteBufferRewinder(ByteBuffer paramByteBuffer)
  {
    this.buffer = paramByteBuffer;
  }
  
  public void cleanup() {}
  
  public ByteBuffer rewindAndGet()
    throws IOException
  {
    this.buffer.position(0);
    return this.buffer;
  }
  
  public static class Factory
    implements DataRewinder.Factory<ByteBuffer>
  {
    public DataRewinder<ByteBuffer> build(ByteBuffer paramByteBuffer)
    {
      return new ByteBufferRewinder(paramByteBuffer);
    }
    
    public Class<ByteBuffer> getDataClass()
    {
      return ByteBuffer.class;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bytes/ByteBufferRewinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */