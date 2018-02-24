package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.util.Assertions;

final class VorbisBitArray
{
  private int bitOffset;
  private int byteOffset;
  public final byte[] data;
  private final int limit;
  
  public VorbisBitArray(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public VorbisBitArray(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.limit = (paramInt * 8);
  }
  
  public int bitsLeft()
  {
    return this.limit - getPosition();
  }
  
  public int getPosition()
  {
    return this.byteOffset * 8 + this.bitOffset;
  }
  
  public int limit()
  {
    return this.limit;
  }
  
  public boolean readBit()
  {
    return readBits(1) == 1;
  }
  
  public int readBits(int paramInt)
  {
    if (getPosition() + paramInt <= this.limit) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (paramInt != 0) {
        break;
      }
      return 0;
    }
    int j = 0;
    int i = 0;
    int m;
    if (this.bitOffset != 0)
    {
      k = Math.min(paramInt, 8 - this.bitOffset);
      m = this.data[this.byteOffset] >>> this.bitOffset & 255 >>> 8 - k;
      this.bitOffset += k;
      i = k;
      j = m;
      if (this.bitOffset == 8)
      {
        this.byteOffset += 1;
        this.bitOffset = 0;
        j = m;
        i = k;
      }
    }
    int n = i;
    int k = j;
    if (paramInt - i > 7)
    {
      int i1 = (paramInt - i) / 8;
      m = 0;
      for (;;)
      {
        n = i;
        k = j;
        if (m >= i1) {
          break;
        }
        long l = j;
        byte[] arrayOfByte = this.data;
        j = this.byteOffset;
        this.byteOffset = (j + 1);
        j = (int)(l | (arrayOfByte[j] & 0xFF) << i);
        i += 8;
        m += 1;
      }
    }
    i = k;
    if (paramInt > n)
    {
      paramInt -= n;
      i = k | (this.data[this.byteOffset] & 255 >>> 8 - paramInt) << n;
      this.bitOffset += paramInt;
    }
    return i;
  }
  
  public void reset()
  {
    this.byteOffset = 0;
    this.bitOffset = 0;
  }
  
  public void setPosition(int paramInt)
  {
    if ((paramInt < this.limit) && (paramInt >= 0)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.byteOffset = (paramInt / 8);
      this.bitOffset = (paramInt - this.byteOffset * 8);
      return;
    }
  }
  
  public void skipBits(int paramInt)
  {
    if (getPosition() + paramInt <= this.limit) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.byteOffset += paramInt / 8;
      this.bitOffset += paramInt % 8;
      if (this.bitOffset > 7)
      {
        this.byteOffset += 1;
        this.bitOffset -= 8;
      }
      return;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/ogg/VorbisBitArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */