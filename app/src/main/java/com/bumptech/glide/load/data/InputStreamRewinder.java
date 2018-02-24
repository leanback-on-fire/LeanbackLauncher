package com.bumptech.glide.load.data;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreamRewinder
  implements DataRewinder<InputStream>
{
  private static final int MARK_LIMIT = 5242880;
  private final RecyclableBufferedInputStream bufferedStream;
  
  InputStreamRewinder(InputStream paramInputStream, ArrayPool paramArrayPool)
  {
    this.bufferedStream = new RecyclableBufferedInputStream(paramInputStream, paramArrayPool);
    this.bufferedStream.mark(5242880);
  }
  
  public void cleanup()
  {
    this.bufferedStream.release();
  }
  
  public InputStream rewindAndGet()
    throws IOException
  {
    this.bufferedStream.reset();
    return this.bufferedStream;
  }
  
  public static final class Factory
    implements DataRewinder.Factory<InputStream>
  {
    private final ArrayPool byteArrayPool;
    
    public Factory(ArrayPool paramArrayPool)
    {
      this.byteArrayPool = paramArrayPool;
    }
    
    public DataRewinder<InputStream> build(InputStream paramInputStream)
    {
      return new InputStreamRewinder(paramInputStream, this.byteArrayPool);
    }
    
    public Class<InputStream> getDataClass()
    {
      return InputStream.class;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/InputStreamRewinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */