package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class CacheDataSink
  implements DataSink
{
  private final int bufferSize;
  private ReusableBufferedOutputStream bufferedOutputStream;
  private final Cache cache;
  private DataSpec dataSpec;
  private long dataSpecBytesWritten;
  private File file;
  private final long maxCacheFileSize;
  private OutputStream outputStream;
  private long outputStreamBytesWritten;
  private FileOutputStream underlyingFileOutputStream;
  
  public CacheDataSink(Cache paramCache, long paramLong)
  {
    this(paramCache, paramLong, 0);
  }
  
  public CacheDataSink(Cache paramCache, long paramLong, int paramInt)
  {
    this.cache = ((Cache)Assertions.checkNotNull(paramCache));
    this.maxCacheFileSize = paramLong;
    this.bufferSize = paramInt;
  }
  
  private void closeCurrentOutputStream()
    throws IOException
  {
    if (this.outputStream == null) {
      return;
    }
    File localFile2;
    try
    {
      this.outputStream.flush();
      this.underlyingFileOutputStream.getFD().sync();
      Util.closeQuietly(this.outputStream);
      this.outputStream = null;
      File localFile1 = this.file;
      this.file = null;
      if (1 != 0)
      {
        this.cache.commitFile(localFile1);
        return;
      }
      localFile1.delete();
      return;
    }
    finally
    {
      Util.closeQuietly(this.outputStream);
      this.outputStream = null;
      localFile2 = this.file;
      this.file = null;
      if (0 == 0) {
        break label107;
      }
    }
    this.cache.commitFile(localFile2);
    for (;;)
    {
      throw ((Throwable)localObject);
      label107:
      localFile2.delete();
    }
  }
  
  private void openNextOutputStream()
    throws IOException
  {
    long l;
    if (this.dataSpec.length == -1L)
    {
      l = this.maxCacheFileSize;
      this.file = this.cache.startFile(this.dataSpec.key, this.dataSpec.absoluteStreamPosition + this.dataSpecBytesWritten, l);
      this.underlyingFileOutputStream = new FileOutputStream(this.file);
      if (this.bufferSize <= 0) {
        break label151;
      }
      if (this.bufferedOutputStream != null) {
        break label137;
      }
      this.bufferedOutputStream = new ReusableBufferedOutputStream(this.underlyingFileOutputStream, this.bufferSize);
    }
    label100:
    label137:
    label151:
    for (this.outputStream = this.bufferedOutputStream;; this.outputStream = this.underlyingFileOutputStream)
    {
      this.outputStreamBytesWritten = 0L;
      return;
      l = Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.maxCacheFileSize);
      break;
      this.bufferedOutputStream.reset(this.underlyingFileOutputStream);
      break label100;
    }
  }
  
  public void close()
    throws CacheDataSink.CacheDataSinkException
  {
    if (this.dataSpec == null) {
      return;
    }
    try
    {
      closeCurrentOutputStream();
      return;
    }
    catch (IOException localIOException)
    {
      throw new CacheDataSinkException(localIOException);
    }
  }
  
  public void open(DataSpec paramDataSpec)
    throws CacheDataSink.CacheDataSinkException
  {
    if ((paramDataSpec.length == -1L) && (!paramDataSpec.isFlagSet(2)))
    {
      this.dataSpec = null;
      return;
    }
    this.dataSpec = paramDataSpec;
    this.dataSpecBytesWritten = 0L;
    try
    {
      openNextOutputStream();
      return;
    }
    catch (IOException paramDataSpec)
    {
      throw new CacheDataSinkException(paramDataSpec);
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws CacheDataSink.CacheDataSinkException
  {
    if (this.dataSpec == null) {
      return;
    }
    int i = 0;
    while (i < paramInt2) {
      try
      {
        if (this.outputStreamBytesWritten == this.maxCacheFileSize)
        {
          closeCurrentOutputStream();
          openNextOutputStream();
        }
        int j = (int)Math.min(paramInt2 - i, this.maxCacheFileSize - this.outputStreamBytesWritten);
        this.outputStream.write(paramArrayOfByte, paramInt1 + i, j);
        i += j;
        this.outputStreamBytesWritten += j;
        this.dataSpecBytesWritten += j;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new CacheDataSinkException(paramArrayOfByte);
      }
    }
  }
  
  public static class CacheDataSinkException
    extends Cache.CacheException
  {
    public CacheDataSinkException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/cache/CacheDataSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */