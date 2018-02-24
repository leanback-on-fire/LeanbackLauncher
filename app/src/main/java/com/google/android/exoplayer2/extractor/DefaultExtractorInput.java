package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Assertions;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

public final class DefaultExtractorInput
  implements ExtractorInput
{
  private static final byte[] SCRATCH_SPACE = new byte['က'];
  private final DataSource dataSource;
  private byte[] peekBuffer;
  private int peekBufferLength;
  private int peekBufferPosition;
  private long position;
  private final long streamLength;
  
  public DefaultExtractorInput(DataSource paramDataSource, long paramLong1, long paramLong2)
  {
    this.dataSource = paramDataSource;
    this.position = paramLong1;
    this.streamLength = paramLong2;
    this.peekBuffer = new byte[' '];
  }
  
  private void commitBytesRead(int paramInt)
  {
    if (paramInt != -1) {
      this.position += paramInt;
    }
  }
  
  private void ensureSpaceForPeek(int paramInt)
  {
    paramInt = this.peekBufferPosition + paramInt;
    if (paramInt > this.peekBuffer.length) {
      this.peekBuffer = Arrays.copyOf(this.peekBuffer, Math.max(this.peekBuffer.length * 2, paramInt));
    }
  }
  
  private int readFromDataSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    paramInt1 = this.dataSource.read(paramArrayOfByte, paramInt1 + paramInt3, paramInt2 - paramInt3);
    if (paramInt1 == -1)
    {
      if ((paramInt3 == 0) && (paramBoolean)) {
        return -1;
      }
      throw new EOFException();
    }
    return paramInt3 + paramInt1;
  }
  
  private int readFromPeekBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.peekBufferLength == 0) {
      return 0;
    }
    paramInt2 = Math.min(this.peekBufferLength, paramInt2);
    System.arraycopy(this.peekBuffer, 0, paramArrayOfByte, paramInt1, paramInt2);
    updatePeekBuffer(paramInt2);
    return paramInt2;
  }
  
  private int skipFromPeekBuffer(int paramInt)
  {
    paramInt = Math.min(this.peekBufferLength, paramInt);
    updatePeekBuffer(paramInt);
    return paramInt;
  }
  
  private void updatePeekBuffer(int paramInt)
  {
    this.peekBufferLength -= paramInt;
    this.peekBufferPosition = 0;
    System.arraycopy(this.peekBuffer, paramInt, this.peekBuffer, 0, this.peekBufferLength);
  }
  
  public void advancePeekPosition(int paramInt)
    throws IOException, InterruptedException
  {
    advancePeekPosition(paramInt, false);
  }
  
  public boolean advancePeekPosition(int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    ensureSpaceForPeek(paramInt);
    int i = Math.min(this.peekBufferLength - this.peekBufferPosition, paramInt);
    while (i < paramInt)
    {
      int j = readFromDataSource(this.peekBuffer, this.peekBufferPosition, paramInt, i, paramBoolean);
      i = j;
      if (j == -1) {
        return false;
      }
    }
    this.peekBufferPosition += paramInt;
    this.peekBufferLength = Math.max(this.peekBufferLength, this.peekBufferPosition);
    return true;
  }
  
  public long getLength()
  {
    return this.streamLength;
  }
  
  public long getPeekPosition()
  {
    return this.position + this.peekBufferPosition;
  }
  
  public long getPosition()
  {
    return this.position;
  }
  
  public void peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    peekFully(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public boolean peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    if (!advancePeekPosition(paramInt2, paramBoolean)) {
      return false;
    }
    System.arraycopy(this.peekBuffer, this.peekBufferPosition - paramInt2, paramArrayOfByte, paramInt1, paramInt2);
    return true;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    int j = readFromPeekBuffer(paramArrayOfByte, paramInt1, paramInt2);
    int i = j;
    if (j == 0) {
      i = readFromDataSource(paramArrayOfByte, paramInt1, paramInt2, 0, true);
    }
    commitBytesRead(i);
    return i;
  }
  
  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException
  {
    readFully(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public boolean readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    for (int i = readFromPeekBuffer(paramArrayOfByte, paramInt1, paramInt2); (i < paramInt2) && (i != -1); i = readFromDataSource(paramArrayOfByte, paramInt1, paramInt2, i, paramBoolean)) {}
    commitBytesRead(i);
    return i != -1;
  }
  
  public void resetPeekPosition()
  {
    this.peekBufferPosition = 0;
  }
  
  public <E extends Throwable> void setRetryPosition(long paramLong, E paramE)
    throws Throwable
  {
    if (paramLong >= 0L) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.position = paramLong;
      throw paramE;
    }
  }
  
  public int skip(int paramInt)
    throws IOException, InterruptedException
  {
    int j = skipFromPeekBuffer(paramInt);
    int i = j;
    if (j == 0) {
      i = readFromDataSource(SCRATCH_SPACE, 0, Math.min(paramInt, SCRATCH_SPACE.length), 0, true);
    }
    commitBytesRead(i);
    return i;
  }
  
  public void skipFully(int paramInt)
    throws IOException, InterruptedException
  {
    skipFully(paramInt, false);
  }
  
  public boolean skipFully(int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    for (int i = skipFromPeekBuffer(paramInt); (i < paramInt) && (i != -1); i = readFromDataSource(SCRATCH_SPACE, -i, Math.min(paramInt, SCRATCH_SPACE.length + i), i, paramBoolean)) {}
    commitBytesRead(i);
    return i != -1;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/DefaultExtractorInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */