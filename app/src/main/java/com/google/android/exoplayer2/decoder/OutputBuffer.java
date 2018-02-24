package com.google.android.exoplayer2.decoder;

public abstract class OutputBuffer
  extends Buffer
{
  public int skippedOutputBufferCount;
  public long timeUs;
  
  public abstract void release();
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/decoder/OutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */