package com.google.android.exoplayer2.util;

public final class ParsableBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  public byte[] data;
  
  public ParsableBitArray() {}
  
  public ParsableBitArray(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public ParsableBitArray(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.byteLimit = paramInt;
  }
  
  private void assertValidOffset()
  {
    if ((this.byteOffset >= 0) && (this.bitOffset >= 0) && (this.bitOffset < 8) && ((this.byteOffset < this.byteLimit) || ((this.byteOffset == this.byteLimit) && (this.bitOffset == 0)))) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return;
    }
  }
  
  public int bitsLeft()
  {
    return (this.byteLimit - this.byteOffset) * 8 - this.bitOffset;
  }
  
  public int getPosition()
  {
    return this.byteOffset * 8 + this.bitOffset;
  }
  
  public boolean readBit()
  {
    return readBits(1) == 1;
  }
  
  public int readBits(int paramInt)
  {
    if (paramInt == 0) {
      return 0;
    }
    int k = 0;
    int m = paramInt / 8;
    int j = 0;
    int i = paramInt;
    paramInt = k;
    if (j < m)
    {
      if (this.bitOffset != 0) {}
      for (k = (this.data[this.byteOffset] & 0xFF) << this.bitOffset | (this.data[(this.byteOffset + 1)] & 0xFF) >>> 8 - this.bitOffset;; k = this.data[this.byteOffset])
      {
        i -= 8;
        paramInt |= (k & 0xFF) << i;
        this.byteOffset += 1;
        j += 1;
        break;
      }
    }
    j = paramInt;
    if (i > 0)
    {
      j = this.bitOffset + i;
      i = (byte)(255 >> 8 - i);
      if (j <= 8) {
        break label225;
      }
      paramInt |= ((this.data[this.byteOffset] & 0xFF) << j - 8 | (this.data[(this.byteOffset + 1)] & 0xFF) >> 16 - j) & i;
      this.byteOffset += 1;
    }
    for (;;)
    {
      this.bitOffset = (j % 8);
      j = paramInt;
      assertValidOffset();
      return j;
      label225:
      i = paramInt | (this.data[this.byteOffset] & 0xFF) >> 8 - j & i;
      paramInt = i;
      if (j == 8)
      {
        this.byteOffset += 1;
        paramInt = i;
      }
    }
  }
  
  public void reset(byte[] paramArrayOfByte)
  {
    reset(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.byteOffset = 0;
    this.bitOffset = 0;
    this.byteLimit = paramInt;
  }
  
  public void setPosition(int paramInt)
  {
    this.byteOffset = (paramInt / 8);
    this.bitOffset = (paramInt - this.byteOffset * 8);
    assertValidOffset();
  }
  
  public void skipBits(int paramInt)
  {
    this.byteOffset += paramInt / 8;
    this.bitOffset += paramInt % 8;
    if (this.bitOffset > 7)
    {
      this.byteOffset += 1;
      this.bitOffset -= 8;
    }
    assertValidOffset();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/ParsableBitArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */