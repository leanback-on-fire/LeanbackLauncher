package com.google.android.exoplayer2.source.chunk;

import java.io.IOException;
import java.util.List;

public abstract interface ChunkSource
{
  public abstract void getNextChunk(MediaChunk paramMediaChunk, long paramLong, ChunkHolder paramChunkHolder);
  
  public abstract int getPreferredQueueSize(long paramLong, List<? extends MediaChunk> paramList);
  
  public abstract void maybeThrowError()
    throws IOException;
  
  public abstract void onChunkLoadCompleted(Chunk paramChunk);
  
  public abstract boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, Exception paramException);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/chunk/ChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */