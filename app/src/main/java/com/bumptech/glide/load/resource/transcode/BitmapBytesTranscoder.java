package com.bumptech.glide.load.resource.transcode;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bytes.BytesResource;
import java.io.ByteArrayOutputStream;

public class BitmapBytesTranscoder
  implements ResourceTranscoder<Bitmap, byte[]>
{
  private final Bitmap.CompressFormat compressFormat;
  private final int quality;
  
  public BitmapBytesTranscoder()
  {
    this(Bitmap.CompressFormat.JPEG, 100);
  }
  
  public BitmapBytesTranscoder(Bitmap.CompressFormat paramCompressFormat, int paramInt)
  {
    this.compressFormat = paramCompressFormat;
    this.quality = paramInt;
  }
  
  public Resource<byte[]> transcode(Resource<Bitmap> paramResource)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    ((Bitmap)paramResource.get()).compress(this.compressFormat, this.quality, localByteArrayOutputStream);
    paramResource.recycle();
    return new BytesResource(localByteArrayOutputStream.toByteArray());
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/transcode/BitmapBytesTranscoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */