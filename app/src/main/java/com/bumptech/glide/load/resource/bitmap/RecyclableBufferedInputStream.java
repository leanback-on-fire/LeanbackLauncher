package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RecyclableBufferedInputStream
  extends FilterInputStream
{
  private volatile byte[] buf;
  private final ArrayPool byteArrayPool;
  private int count;
  private int marklimit;
  private int markpos = -1;
  private int pos;
  
  public RecyclableBufferedInputStream(InputStream paramInputStream, ArrayPool paramArrayPool)
  {
    this(paramInputStream, paramArrayPool, 65536);
  }
  
  RecyclableBufferedInputStream(InputStream paramInputStream, ArrayPool paramArrayPool, int paramInt)
  {
    super(paramInputStream);
    this.byteArrayPool = paramArrayPool;
    this.buf = ((byte[])paramArrayPool.get(paramInt, byte[].class));
  }
  
  private int fillbuf(InputStream paramInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    if ((this.markpos == -1) || (this.pos - this.markpos >= this.marklimit))
    {
      i = paramInputStream.read(paramArrayOfByte);
      if (i > 0)
      {
        this.markpos = -1;
        this.pos = 0;
        this.count = i;
      }
      return i;
    }
    int j;
    byte[] arrayOfByte;
    if ((this.markpos == 0) && (this.marklimit > paramArrayOfByte.length) && (this.count == paramArrayOfByte.length))
    {
      j = paramArrayOfByte.length * 2;
      i = j;
      if (j > this.marklimit) {
        i = this.marklimit;
      }
      arrayOfByte = (byte[])this.byteArrayPool.get(i, byte[].class);
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
      this.buf = arrayOfByte;
      this.byteArrayPool.put(paramArrayOfByte, byte[].class);
      this.pos -= this.markpos;
      this.markpos = 0;
      this.count = 0;
      j = paramInputStream.read(arrayOfByte, this.pos, arrayOfByte.length - this.pos);
      if (j > 0) {
        break label238;
      }
    }
    label238:
    for (int i = this.pos;; i = this.pos + j)
    {
      this.count = i;
      return j;
      arrayOfByte = paramArrayOfByte;
      if (this.markpos <= 0) {
        break;
      }
      System.arraycopy(paramArrayOfByte, this.markpos, paramArrayOfByte, 0, paramArrayOfByte.length - this.markpos);
      arrayOfByte = paramArrayOfByte;
      break;
    }
  }
  
  private static IOException streamClosed()
    throws IOException
  {
    throw new IOException("BufferedInputStream is closed");
  }
  
  public int available()
    throws IOException
  {
    try
    {
      InputStream localInputStream = this.in;
      if ((this.buf == null) || (localInputStream == null)) {
        throw streamClosed();
      }
    }
    finally {}
    int i = this.count;
    int j = this.pos;
    int k = ((InputStream)localObject).available();
    return i - j + k;
  }
  
  public void close()
    throws IOException
  {
    if (this.buf != null)
    {
      this.byteArrayPool.put(this.buf, byte[].class);
      this.buf = null;
    }
    InputStream localInputStream = this.in;
    this.in = null;
    if (localInputStream != null) {
      localInputStream.close();
    }
  }
  
  public void fixMarkLimit()
  {
    try
    {
      this.marklimit = this.buf.length;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void mark(int paramInt)
  {
    try
    {
      this.marklimit = Math.max(this.marklimit, paramInt);
      this.markpos = this.pos;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public int read()
    throws IOException
  {
    int i = -1;
    byte[] arrayOfByte2;
    try
    {
      arrayOfByte2 = this.buf;
      InputStream localInputStream1 = this.in;
      if ((arrayOfByte2 == null) || (localInputStream1 == null)) {
        throw streamClosed();
      }
    }
    finally {}
    if (this.pos >= this.count)
    {
      int j = fillbuf(localInputStream2, arrayOfByte2);
      if (j != -1) {}
    }
    for (;;)
    {
      return i;
      byte[] arrayOfByte1 = arrayOfByte2;
      if (arrayOfByte2 != this.buf)
      {
        arrayOfByte2 = this.buf;
        arrayOfByte1 = arrayOfByte2;
        if (arrayOfByte2 == null) {
          throw streamClosed();
        }
      }
      if (this.count - this.pos > 0)
      {
        i = this.pos;
        this.pos = (i + 1);
        i = arrayOfByte1[i];
        i &= 0xFF;
      }
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int k = -1;
    Object localObject2;
    try
    {
      localObject2 = this.buf;
      if (localObject2 == null) {
        throw streamClosed();
      }
    }
    finally {}
    if (paramInt2 == 0) {
      paramInt1 = 0;
    }
    for (;;)
    {
      return paramInt1;
      InputStream localInputStream = this.in;
      if (localInputStream == null) {
        throw streamClosed();
      }
      int i;
      label132:
      int m;
      Object localObject1;
      int j;
      if (this.pos < this.count)
      {
        if (this.count - this.pos >= paramInt2) {}
        for (i = paramInt2;; i = this.count - this.pos)
        {
          System.arraycopy(localObject2, this.pos, paramArrayOfByte, paramInt1, i);
          this.pos += i;
          if (i == paramInt2) {
            break;
          }
          if (localInputStream.available() != 0) {
            break label348;
          }
          break;
        }
        if ((this.markpos == -1) && (i >= localObject2.length))
        {
          m = localInputStream.read(paramArrayOfByte, paramInt1, i);
          localObject1 = localObject2;
          j = m;
          if (m != -1) {
            break label368;
          }
          paramInt1 = k;
          if (i == paramInt2) {
            continue;
          }
          paramInt1 = paramInt2 - i;
          continue;
        }
        if (fillbuf(localInputStream, (byte[])localObject2) == -1)
        {
          paramInt1 = k;
          if (i == paramInt2) {
            continue;
          }
          paramInt1 = paramInt2 - i;
          continue;
        }
        localObject1 = localObject2;
        if (localObject2 != this.buf)
        {
          localObject2 = this.buf;
          localObject1 = localObject2;
          if (localObject2 == null) {
            throw streamClosed();
          }
        }
        if (this.count - this.pos >= i) {}
        for (j = i;; j = this.count - this.pos)
        {
          System.arraycopy(localObject1, this.pos, paramArrayOfByte, paramInt1, j);
          this.pos += j;
          break;
        }
      }
      label348:
      label368:
      do
      {
        m = localInputStream.available();
        if (m == 0)
        {
          paramInt1 = paramInt2 - i;
          break;
        }
        paramInt1 += j;
        localObject2 = localObject1;
        break label132;
        paramInt1 = i;
        break;
        paramInt1 += i;
        i = paramInt2 - i;
        break label132;
        i = paramInt2;
        break label132;
        i -= j;
      } while (i != 0);
      paramInt1 = paramInt2;
    }
  }
  
  public void release()
  {
    try
    {
      if (this.buf != null)
      {
        this.byteArrayPool.put(this.buf, byte[].class);
        this.buf = null;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void reset()
    throws IOException
  {
    try
    {
      if (this.buf == null) {
        throw new IOException("Stream is closed");
      }
    }
    finally {}
    if (-1 == this.markpos)
    {
      int i = this.pos;
      int j = this.marklimit;
      throw new InvalidMarkException(66 + "Mark has been invalidated, pos: " + i + " markLimit: " + j);
    }
    this.pos = this.markpos;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    InputStream localInputStream;
    try
    {
      byte[] arrayOfByte1 = this.buf;
      localInputStream = this.in;
      if (arrayOfByte1 == null) {
        throw streamClosed();
      }
    }
    finally {}
    if (paramLong < 1L) {
      paramLong = 0L;
    }
    for (;;)
    {
      return paramLong;
      if (localInputStream == null) {
        throw streamClosed();
      }
      if (this.count - this.pos >= paramLong)
      {
        this.pos = ((int)(this.pos + paramLong));
      }
      else
      {
        long l1 = this.count - this.pos;
        this.pos = this.count;
        if ((this.markpos != -1) && (paramLong <= this.marklimit))
        {
          if (fillbuf(localInputStream, arrayOfByte2) == -1)
          {
            paramLong = l1;
          }
          else if (this.count - this.pos >= paramLong - l1)
          {
            this.pos = ((int)(this.pos + (paramLong - l1)));
          }
          else
          {
            paramLong = this.count;
            long l2 = this.pos;
            this.pos = this.count;
            paramLong = paramLong + l1 - l2;
          }
        }
        else
        {
          paramLong = localInputStream.skip(paramLong - l1);
          paramLong = l1 + paramLong;
        }
      }
    }
  }
  
  public static class InvalidMarkException
    extends IOException
  {
    private static final long serialVersionUID = -4338378848813561757L;
    
    public InvalidMarkException(String paramString)
    {
      super();
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/RecyclableBufferedInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */