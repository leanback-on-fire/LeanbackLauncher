package com.bumptech.glide.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

public class ExceptionCatchingInputStream
  extends InputStream
{
  private static final Queue<ExceptionCatchingInputStream> QUEUE = Util.createQueue(0);
  private IOException exception;
  private InputStream wrapped;
  
  static void clearQueue()
  {
    while (!QUEUE.isEmpty()) {
      QUEUE.remove();
    }
  }
  
  public static ExceptionCatchingInputStream obtain(InputStream paramInputStream)
  {
    synchronized (QUEUE)
    {
      ExceptionCatchingInputStream localExceptionCatchingInputStream = (ExceptionCatchingInputStream)QUEUE.poll();
      ??? = localExceptionCatchingInputStream;
      if (localExceptionCatchingInputStream == null) {
        ??? = new ExceptionCatchingInputStream();
      }
      ((ExceptionCatchingInputStream)???).setInputStream(paramInputStream);
      return (ExceptionCatchingInputStream)???;
    }
  }
  
  public int available()
    throws IOException
  {
    return this.wrapped.available();
  }
  
  public void close()
    throws IOException
  {
    this.wrapped.close();
  }
  
  public IOException getException()
  {
    return this.exception;
  }
  
  public void mark(int paramInt)
  {
    this.wrapped.mark(paramInt);
  }
  
  public boolean markSupported()
  {
    return this.wrapped.markSupported();
  }
  
  public int read()
    throws IOException
  {
    try
    {
      int i = this.wrapped.read();
      return i;
    }
    catch (IOException localIOException)
    {
      this.exception = localIOException;
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      int i = this.wrapped.read(paramArrayOfByte);
      return i;
    }
    catch (IOException paramArrayOfByte)
    {
      this.exception = paramArrayOfByte;
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      paramInt1 = this.wrapped.read(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      this.exception = paramArrayOfByte;
    }
    return -1;
  }
  
  public void release()
  {
    this.exception = null;
    this.wrapped = null;
    synchronized (QUEUE)
    {
      QUEUE.offer(this);
      return;
    }
  }
  
  public void reset()
    throws IOException
  {
    try
    {
      this.wrapped.reset();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  void setInputStream(InputStream paramInputStream)
  {
    this.wrapped = paramInputStream;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    try
    {
      paramLong = this.wrapped.skip(paramLong);
      return paramLong;
    }
    catch (IOException localIOException)
    {
      this.exception = localIOException;
    }
    return 0L;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/util/ExceptionCatchingInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */