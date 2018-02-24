package com.google.android.tvlauncher.data;

import android.database.Cursor;

abstract class AbstractDataBuffer<T>
{
  protected Cursor mCursor;
  
  AbstractDataBuffer(Cursor paramCursor)
  {
    this.mCursor = paramCursor;
  }
  
  private void checkNotReleased()
  {
    if (isReleased()) {
      throw new IllegalArgumentException("Buffer is released.");
    }
  }
  
  public abstract T get(int paramInt);
  
  public int getCount()
  {
    checkNotReleased();
    return this.mCursor.getCount();
  }
  
  public int getInt(int paramInt1, int paramInt2)
  {
    checkNotReleased();
    this.mCursor.moveToPosition(paramInt1);
    return this.mCursor.getInt(paramInt2);
  }
  
  public long getLong(int paramInt1, int paramInt2)
  {
    checkNotReleased();
    this.mCursor.moveToPosition(paramInt1);
    return this.mCursor.getLong(paramInt2);
  }
  
  public String getString(int paramInt1, int paramInt2)
  {
    checkNotReleased();
    this.mCursor.moveToPosition(paramInt1);
    return this.mCursor.getString(paramInt2);
  }
  
  public boolean isReleased()
  {
    return this.mCursor.isClosed();
  }
  
  public void release()
  {
    this.mCursor.close();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/AbstractDataBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */