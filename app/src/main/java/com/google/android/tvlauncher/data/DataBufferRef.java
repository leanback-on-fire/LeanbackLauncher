package com.google.android.tvlauncher.data;

abstract class DataBufferRef
{
  private AbstractDataBuffer mDataBuffer;
  private int mPosition;
  
  DataBufferRef(AbstractDataBuffer paramAbstractDataBuffer, int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= paramAbstractDataBuffer.getCount())) {
      throw new IllegalArgumentException("Position [" + paramInt + "] is out of bounds [0, " + (paramAbstractDataBuffer.getCount() - 1) + "]");
    }
    this.mDataBuffer = paramAbstractDataBuffer;
    this.mPosition = paramInt;
  }
  
  protected int getInt(int paramInt)
  {
    return this.mDataBuffer.getInt(this.mPosition, paramInt);
  }
  
  protected long getLong(int paramInt)
  {
    return this.mDataBuffer.getLong(this.mPosition, paramInt);
  }
  
  protected String getString(int paramInt)
  {
    return this.mDataBuffer.getString(this.mPosition, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/DataBufferRef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */