package com.bumptech.glide.load.model;

import android.util.Log;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferEncoder
  implements Encoder<ByteBuffer>
{
  private static final String TAG = "ByteBufferEncoder";
  
  public boolean encode(ByteBuffer paramByteBuffer, File paramFile, Options paramOptions)
  {
    boolean bool = false;
    try
    {
      ByteBufferUtil.toFile(paramByteBuffer, paramFile);
      bool = true;
    }
    catch (IOException paramByteBuffer)
    {
      while (!Log.isLoggable("ByteBufferEncoder", 3)) {}
      Log.d("ByteBufferEncoder", "Failed to write data", paramByteBuffer);
    }
    return bool;
    return false;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/ByteBufferEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */