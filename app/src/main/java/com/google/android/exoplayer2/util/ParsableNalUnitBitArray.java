package com.google.android.exoplayer2.util;

public final class ParsableNalUnitBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  private byte[] data;
  
  public ParsableNalUnitBitArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    reset(paramArrayOfByte, paramInt1, paramInt2);
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
  
  private int readExpGolombCodeNum()
  {
    int i = 0;
    while (!readBit()) {
      i += 1;
    }
    if (i > 0) {}
    for (int j = readBits(i);; j = 0) {
      return j + ((1 << i) - 1);
    }
  }
  
  private boolean shouldSkipByte(int paramInt)
  {
    return (2 <= paramInt) && (paramInt < this.byteLimit) && (this.data[paramInt] == 3) && (this.data[(paramInt - 2)] == 0) && (this.data[(paramInt - 1)] == 0);
  }
  
  public boolean canReadBits(int paramInt)
  {
    int m = this.byteOffset;
    int j = this.byteOffset + paramInt / 8;
    int k = this.bitOffset + paramInt % 8;
    int i = k;
    paramInt = j;
    if (k > 7)
    {
      paramInt = j + 1;
      i = k - 8;
    }
    j = m + 1;
    k = paramInt;
    paramInt = j;
    while ((paramInt <= k) && (k < this.byteLimit))
    {
      m = paramInt;
      j = k;
      if (shouldSkipByte(paramInt))
      {
        j = k + 1;
        m = paramInt + 2;
      }
      paramInt = m + 1;
      k = j;
    }
    return (k < this.byteLimit) || ((k == this.byteLimit) && (i == 0));
  }
  
  public boolean canReadExpGolombCodedNum()
  {
    int k = this.byteOffset;
    int m = this.bitOffset;
    int i = 0;
    while ((this.byteOffset < this.byteLimit) && (!readBit())) {
      i += 1;
    }
    if (this.byteOffset == this.byteLimit) {}
    for (int j = 1;; j = 0)
    {
      this.byteOffset = k;
      this.bitOffset = m;
      if ((j != 0) || (!canReadBits(i * 2 + 1))) {
        break;
      }
      return true;
    }
    return false;
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
    int n = paramInt / 8;
    int j = 0;
    int i = paramInt;
    paramInt = k;
    if (j < n)
    {
      if (shouldSkipByte(this.byteOffset + 1))
      {
        k = this.byteOffset + 2;
        label49:
        if (this.bitOffset == 0) {
          break label136;
        }
      }
      label136:
      for (int m = (this.data[this.byteOffset] & 0xFF) << this.bitOffset | (this.data[k] & 0xFF) >>> 8 - this.bitOffset;; m = this.data[this.byteOffset])
      {
        i -= 8;
        paramInt |= (m & 0xFF) << i;
        this.byteOffset = k;
        j += 1;
        break;
        k = this.byteOffset + 1;
        break label49;
      }
    }
    j = paramInt;
    if (i > 0)
    {
      k = this.bitOffset + i;
      j = (byte)(255 >> 8 - i);
      if (!shouldSkipByte(this.byteOffset + 1)) {
        break label264;
      }
      i = this.byteOffset + 2;
      if (k <= 8) {
        break label274;
      }
      paramInt |= ((this.data[this.byteOffset] & 0xFF) << k - 8 | (this.data[i] & 0xFF) >> 16 - k) & j;
      this.byteOffset = i;
    }
    for (;;)
    {
      this.bitOffset = (k % 8);
      j = paramInt;
      assertValidOffset();
      return j;
      label264:
      i = this.byteOffset + 1;
      break;
      label274:
      j = paramInt | (this.data[this.byteOffset] & 0xFF) >> 8 - k & j;
      paramInt = j;
      if (k == 8)
      {
        this.byteOffset = i;
        paramInt = j;
      }
    }
  }
  
  public int readSignedExpGolombCodedInt()
  {
    int j = readExpGolombCodeNum();
    if (j % 2 == 0) {}
    for (int i = -1;; i = 1) {
      return i * ((j + 1) / 2);
    }
  }
  
  public int readUnsignedExpGolombCodedInt()
  {
    return readExpGolombCodeNum();
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.data = paramArrayOfByte;
    this.byteOffset = paramInt1;
    this.byteLimit = paramInt2;
    this.bitOffset = 0;
    assertValidOffset();
  }
  
  public void skipBits(int paramInt)
  {
    int i = this.byteOffset;
    this.byteOffset += paramInt / 8;
    this.bitOffset += paramInt % 8;
    if (this.bitOffset > 7)
    {
      this.byteOffset += 1;
      this.bitOffset -= 8;
    }
    for (paramInt = i + 1; paramInt <= this.byteOffset; paramInt = i + 1)
    {
      i = paramInt;
      if (shouldSkipByte(paramInt))
      {
        this.byteOffset += 1;
        i = paramInt + 2;
      }
    }
    assertValidOffset();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/ParsableNalUnitBitArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */