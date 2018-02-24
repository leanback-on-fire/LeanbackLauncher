package com.bumptech.glide.load.data;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ExifOrientationStream
  extends FilterInputStream
{
  private static final byte[] EXIF_SEGMENT = { -1, -31, 0, 28, 69, 120, 105, 102, 0, 0, 77, 77, 0, 0, 0, 0, 0, 8, 0, 1, 1, 18, 0, 2, 0, 0, 0, 1, 0 };
  private static final int ORIENTATION_POSITION = SEGMENT_LENGTH + 2;
  private static final int SEGMENT_LENGTH = EXIF_SEGMENT.length;
  private static final int SEGMENT_START_POSITION = 2;
  private final byte orientation;
  private int position;
  
  public ExifOrientationStream(InputStream paramInputStream, int paramInt)
  {
    super(paramInputStream);
    if ((paramInt < -1) || (paramInt > 8)) {
      throw new IllegalArgumentException(43 + "Cannot add invalid orientation: " + paramInt);
    }
    this.orientation = ((byte)paramInt);
  }
  
  public void mark(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public int read()
    throws IOException
  {
    int i;
    if ((this.position < 2) || (this.position > ORIENTATION_POSITION)) {
      i = super.read();
    }
    for (;;)
    {
      if (i != -1) {
        this.position += 1;
      }
      return i;
      if (this.position == ORIENTATION_POSITION) {
        i = this.orientation;
      } else {
        i = EXIF_SEGMENT[(this.position - 2)] & 0xFF;
      }
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.position > ORIENTATION_POSITION) {
      paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
    }
    for (;;)
    {
      if (paramInt1 > 0) {
        this.position += paramInt1;
      }
      return paramInt1;
      if (this.position == ORIENTATION_POSITION)
      {
        paramArrayOfByte[paramInt1] = this.orientation;
        paramInt1 = 1;
      }
      else if (this.position < 2)
      {
        paramInt1 = super.read(paramArrayOfByte, paramInt1, 2 - this.position);
      }
      else
      {
        paramInt2 = Math.min(ORIENTATION_POSITION - this.position, paramInt2);
        System.arraycopy(EXIF_SEGMENT, this.position - 2, paramArrayOfByte, paramInt1, paramInt2);
        paramInt1 = paramInt2;
      }
    }
  }
  
  public void reset()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    paramLong = super.skip(paramLong);
    if (paramLong > 0L) {
      this.position = ((int)(this.position + paramLong));
    }
    return paramLong;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/ExifOrientationStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */