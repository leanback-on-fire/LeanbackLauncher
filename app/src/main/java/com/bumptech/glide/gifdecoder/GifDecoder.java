package com.bumptech.glide.gifdecoder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.NonNull;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract interface GifDecoder
{
  public static final int STATUS_FORMAT_ERROR = 1;
  public static final int STATUS_OK = 0;
  public static final int STATUS_OPEN_ERROR = 2;
  public static final int STATUS_PARTIAL_DECODE = 3;
  
  public abstract void advance();
  
  public abstract void clear();
  
  public abstract int getByteSize();
  
  public abstract int getCurrentFrameIndex();
  
  public abstract ByteBuffer getData();
  
  public abstract int getDelay(int paramInt);
  
  public abstract int getFrameCount();
  
  public abstract int getHeight();
  
  public abstract int getLoopCount();
  
  public abstract int getNextDelay();
  
  public abstract Bitmap getNextFrame();
  
  public abstract int getStatus();
  
  public abstract int getWidth();
  
  public abstract int read(InputStream paramInputStream, int paramInt);
  
  public abstract int read(byte[] paramArrayOfByte);
  
  public abstract void resetFrameIndex();
  
  public abstract void setData(GifHeader paramGifHeader, ByteBuffer paramByteBuffer);
  
  public abstract void setData(GifHeader paramGifHeader, ByteBuffer paramByteBuffer, int paramInt);
  
  public abstract void setData(GifHeader paramGifHeader, byte[] paramArrayOfByte);
  
  public static abstract interface BitmapProvider
  {
    @NonNull
    public abstract Bitmap obtain(int paramInt1, int paramInt2, Bitmap.Config paramConfig);
    
    public abstract byte[] obtainByteArray(int paramInt);
    
    public abstract int[] obtainIntArray(int paramInt);
    
    public abstract void release(Bitmap paramBitmap);
    
    public abstract void release(byte[] paramArrayOfByte);
    
    public abstract void release(int[] paramArrayOfInt);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/gifdecoder/GifDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */