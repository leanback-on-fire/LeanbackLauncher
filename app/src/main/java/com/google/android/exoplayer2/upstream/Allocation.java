package com.google.android.exoplayer2.upstream;

public final class Allocation
{
  public final byte[] data;
  private final int offset;
  
  public Allocation(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.offset = paramInt;
  }
  
  public int translateOffset(int paramInt)
  {
    return this.offset + paramInt;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/Allocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */