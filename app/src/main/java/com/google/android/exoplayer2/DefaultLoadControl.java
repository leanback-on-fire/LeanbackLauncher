package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;

public final class DefaultLoadControl
  implements LoadControl
{
  private static final int ABOVE_HIGH_WATERMARK = 0;
  private static final int BELOW_LOW_WATERMARK = 2;
  private static final int BETWEEN_WATERMARKS = 1;
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
  public static final int DEFAULT_MAX_BUFFER_MS = 30000;
  public static final int DEFAULT_MIN_BUFFER_MS = 15000;
  public static final int LOADING_PRIORITY = 0;
  private final DefaultAllocator allocator;
  private final long bufferForPlaybackAfterRebufferUs;
  private final long bufferForPlaybackUs;
  private boolean isBuffering;
  private final long maxBufferUs;
  private final long minBufferUs;
  private final PriorityTaskManager priorityTaskManager;
  private int targetBufferSize;
  
  public DefaultLoadControl()
  {
    this(new DefaultAllocator(true, 65536));
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator)
  {
    this(paramDefaultAllocator, 15000, 30000, 2500L, 5000L);
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    this(paramDefaultAllocator, paramInt1, paramInt2, paramLong1, paramLong2, null);
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, long paramLong1, long paramLong2, PriorityTaskManager paramPriorityTaskManager)
  {
    this.allocator = paramDefaultAllocator;
    this.minBufferUs = (paramInt1 * 1000L);
    this.maxBufferUs = (paramInt2 * 1000L);
    this.bufferForPlaybackUs = (paramLong1 * 1000L);
    this.bufferForPlaybackAfterRebufferUs = (paramLong2 * 1000L);
    this.priorityTaskManager = paramPriorityTaskManager;
  }
  
  private int getBufferTimeState(long paramLong)
  {
    if (paramLong > this.maxBufferUs) {
      return 0;
    }
    if (paramLong < this.minBufferUs) {
      return 2;
    }
    return 1;
  }
  
  private void reset(boolean paramBoolean)
  {
    this.targetBufferSize = 0;
    if ((this.priorityTaskManager != null) && (this.isBuffering)) {
      this.priorityTaskManager.remove(0);
    }
    this.isBuffering = false;
    if (paramBoolean) {
      this.allocator.reset();
    }
  }
  
  public Allocator getAllocator()
  {
    return this.allocator;
  }
  
  public void onPrepared()
  {
    reset(false);
  }
  
  public void onReleased()
  {
    reset(true);
  }
  
  public void onStopped()
  {
    reset(true);
  }
  
  public void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
  {
    this.targetBufferSize = 0;
    int i = 0;
    while (i < paramArrayOfRenderer.length)
    {
      if (paramTrackSelectionArray.get(i) != null) {
        this.targetBufferSize += Util.getDefaultBufferSize(paramArrayOfRenderer[i].getTrackType());
      }
      i += 1;
    }
    this.allocator.setTargetBufferSize(this.targetBufferSize);
  }
  
  public boolean shouldContinueLoading(long paramLong)
  {
    boolean bool2 = true;
    int j = getBufferTimeState(paramLong);
    int i;
    boolean bool1;
    if (this.allocator.getTotalBytesAllocated() >= this.targetBufferSize)
    {
      i = 1;
      boolean bool3 = this.isBuffering;
      bool1 = bool2;
      if (j != 2)
      {
        if ((j != 1) || (!this.isBuffering) || (i != 0)) {
          break label110;
        }
        bool1 = bool2;
      }
      label63:
      this.isBuffering = bool1;
      if ((this.priorityTaskManager != null) && (this.isBuffering != bool3))
      {
        if (!this.isBuffering) {
          break label116;
        }
        this.priorityTaskManager.add(0);
      }
    }
    for (;;)
    {
      return this.isBuffering;
      i = 0;
      break;
      label110:
      bool1 = false;
      break label63;
      label116:
      this.priorityTaskManager.remove(0);
    }
  }
  
  public boolean shouldStartPlayback(long paramLong, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (long l = this.bufferForPlaybackAfterRebufferUs; (l <= 0L) || (paramLong >= l); l = this.bufferForPlaybackUs) {
      return true;
    }
    return false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/DefaultLoadControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */