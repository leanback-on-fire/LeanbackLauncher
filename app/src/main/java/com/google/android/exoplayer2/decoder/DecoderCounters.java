package com.google.android.exoplayer2.decoder;

public final class DecoderCounters
{
  public int decoderInitCount;
  public int decoderReleaseCount;
  public int droppedOutputBufferCount;
  public int inputBufferCount;
  public int maxConsecutiveDroppedOutputBufferCount;
  public int renderedOutputBufferCount;
  public int skippedOutputBufferCount;
  
  public void ensureUpdated() {}
  
  public void merge(DecoderCounters paramDecoderCounters)
  {
    this.decoderInitCount += paramDecoderCounters.decoderInitCount;
    this.decoderReleaseCount += paramDecoderCounters.decoderReleaseCount;
    this.inputBufferCount += paramDecoderCounters.inputBufferCount;
    this.renderedOutputBufferCount += paramDecoderCounters.renderedOutputBufferCount;
    this.skippedOutputBufferCount += paramDecoderCounters.skippedOutputBufferCount;
    this.droppedOutputBufferCount += paramDecoderCounters.droppedOutputBufferCount;
    this.maxConsecutiveDroppedOutputBufferCount = Math.max(this.maxConsecutiveDroppedOutputBufferCount, paramDecoderCounters.maxConsecutiveDroppedOutputBufferCount);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/decoder/DecoderCounters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */