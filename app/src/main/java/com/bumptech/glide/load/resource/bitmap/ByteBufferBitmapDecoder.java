package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferBitmapDecoder
  implements ResourceDecoder<ByteBuffer, Bitmap>
{
  private final Downsampler downsampler;
  
  public ByteBufferBitmapDecoder(Downsampler paramDownsampler)
  {
    this.downsampler = paramDownsampler;
  }
  
  public Resource<Bitmap> decode(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    paramByteBuffer = ByteBufferUtil.toStream(paramByteBuffer);
    return this.downsampler.decode(paramByteBuffer, paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(ByteBuffer paramByteBuffer, Options paramOptions)
    throws IOException
  {
    return this.downsampler.handles(paramByteBuffer);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/ByteBufferBitmapDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */