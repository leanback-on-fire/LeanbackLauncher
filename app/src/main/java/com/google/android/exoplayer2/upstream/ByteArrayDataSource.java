package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class ByteArrayDataSource
  implements DataSource
{
  private int bytesRemaining;
  private final byte[] data;
  private int readPosition;
  private Uri uri;
  
  public ByteArrayDataSource(byte[] paramArrayOfByte)
  {
    Assertions.checkNotNull(paramArrayOfByte);
    if (paramArrayOfByte.length > 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.data = paramArrayOfByte;
      return;
    }
  }
  
  public void close()
    throws IOException
  {
    this.uri = null;
  }
  
  public Uri getUri()
  {
    return this.uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    this.uri = paramDataSpec.uri;
    this.readPosition = ((int)paramDataSpec.position);
    if (paramDataSpec.length == -1L) {}
    for (long l = this.data.length - paramDataSpec.position;; l = paramDataSpec.length)
    {
      this.bytesRemaining = ((int)l);
      if ((this.bytesRemaining > 0) && (this.readPosition + this.bytesRemaining <= this.data.length)) {
        break;
      }
      throw new IOException("Unsatisfiable range: [" + this.readPosition + ", " + paramDataSpec.length + "], length: " + this.data.length);
    }
    return this.bytesRemaining;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (this.bytesRemaining == 0) {
      return -1;
    }
    paramInt2 = Math.min(paramInt2, this.bytesRemaining);
    System.arraycopy(this.data, this.readPosition, paramArrayOfByte, paramInt1, paramInt2);
    this.readPosition += paramInt2;
    this.bytesRemaining -= paramInt2;
    return paramInt2;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/ByteArrayDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */