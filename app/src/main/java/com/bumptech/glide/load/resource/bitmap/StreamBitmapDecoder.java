package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamBitmapDecoder
  implements ResourceDecoder<InputStream, Bitmap>
{
  private final ArrayPool byteArrayPool;
  private final Downsampler downsampler;
  
  public StreamBitmapDecoder(Downsampler paramDownsampler, ArrayPool paramArrayPool)
  {
    this.downsampler = paramDownsampler;
    this.byteArrayPool = paramArrayPool;
  }
  
  public Resource<Bitmap> decode(InputStream paramInputStream, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    if ((paramInputStream instanceof RecyclableBufferedInputStream)) {
      paramInputStream = (RecyclableBufferedInputStream)paramInputStream;
    }
    for (i = 0;; i = 1)
    {
      localExceptionCatchingInputStream = ExceptionCatchingInputStream.obtain(paramInputStream);
      MarkEnforcingInputStream localMarkEnforcingInputStream = new MarkEnforcingInputStream(localExceptionCatchingInputStream);
      UntrustedCallbacks localUntrustedCallbacks = new UntrustedCallbacks(paramInputStream, localExceptionCatchingInputStream);
      try
      {
        paramOptions = this.downsampler.decode(localMarkEnforcingInputStream, paramInt1, paramInt2, paramOptions, localUntrustedCallbacks);
        return paramOptions;
      }
      finally
      {
        localExceptionCatchingInputStream.release();
        if (i == 0) {
          break;
        }
        paramInputStream.release();
      }
      paramInputStream = new RecyclableBufferedInputStream(paramInputStream, this.byteArrayPool);
    }
  }
  
  public boolean handles(InputStream paramInputStream, Options paramOptions)
    throws IOException
  {
    return this.downsampler.handles(paramInputStream);
  }
  
  static class UntrustedCallbacks
    implements Downsampler.DecodeCallbacks
  {
    private final RecyclableBufferedInputStream bufferedStream;
    private final ExceptionCatchingInputStream exceptionStream;
    
    public UntrustedCallbacks(RecyclableBufferedInputStream paramRecyclableBufferedInputStream, ExceptionCatchingInputStream paramExceptionCatchingInputStream)
    {
      this.bufferedStream = paramRecyclableBufferedInputStream;
      this.exceptionStream = paramExceptionCatchingInputStream;
    }
    
    public void onDecodeComplete(BitmapPool paramBitmapPool, Bitmap paramBitmap)
      throws IOException
    {
      IOException localIOException = this.exceptionStream.getException();
      if (localIOException != null)
      {
        if (paramBitmap != null) {
          paramBitmapPool.put(paramBitmap);
        }
        throw localIOException;
      }
    }
    
    public void onObtainBounds()
    {
      this.bufferedStream.fixMarkLimit();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/StreamBitmapDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */