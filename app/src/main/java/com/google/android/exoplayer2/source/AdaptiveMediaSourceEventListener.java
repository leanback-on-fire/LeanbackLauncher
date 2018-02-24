package com.google.android.exoplayer2.source;

import android.os.Handler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public abstract interface AdaptiveMediaSourceEventListener
{
  public abstract void onDownstreamFormatChanged(int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong);
  
  public abstract void onLoadCanceled(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
  
  public abstract void onLoadCompleted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
  
  public abstract void onLoadError(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, IOException paramIOException, boolean paramBoolean);
  
  public abstract void onLoadStarted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3);
  
  public abstract void onUpstreamDiscarded(int paramInt, long paramLong1, long paramLong2);
  
  public static final class EventDispatcher
  {
    private final Handler handler;
    private final AdaptiveMediaSourceEventListener listener;
    private final long mediaTimeOffsetMs;
    
    public EventDispatcher(Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
    {
      this(paramHandler, paramAdaptiveMediaSourceEventListener, 0L);
    }
    
    public EventDispatcher(Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener, long paramLong)
    {
      if (paramAdaptiveMediaSourceEventListener != null) {}
      for (paramHandler = (Handler)Assertions.checkNotNull(paramHandler);; paramHandler = null)
      {
        this.handler = paramHandler;
        this.listener = paramAdaptiveMediaSourceEventListener;
        this.mediaTimeOffsetMs = paramLong;
        return;
      }
    }
    
    private long adjustMediaTime(long paramLong)
    {
      paramLong = C.usToMs(paramLong);
      if (paramLong == -9223372036854775807L) {
        return -9223372036854775807L;
      }
      return this.mediaTimeOffsetMs + paramLong;
    }
    
    public EventDispatcher copyWithMediaTimeOffsetMs(long paramLong)
    {
      return new EventDispatcher(this.handler, this.listener, paramLong);
    }
    
    public void downstreamFormatChanged(final int paramInt1, final Format paramFormat, final int paramInt2, final Object paramObject, final long paramLong)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onDownstreamFormatChanged(paramInt1, paramFormat, paramInt2, paramObject, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong));
          }
        });
      }
    }
    
    public void loadCanceled(final DataSpec paramDataSpec, final int paramInt1, final int paramInt2, final Format paramFormat, final int paramInt3, final Object paramObject, final long paramLong1, long paramLong2, final long paramLong3, long paramLong4, final long paramLong5)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadCanceled(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong1), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong3), paramLong5, this.val$loadDurationMs, this.val$bytesLoaded);
          }
        });
      }
    }
    
    public void loadCanceled(DataSpec paramDataSpec, int paramInt, long paramLong1, long paramLong2, long paramLong3)
    {
      loadCanceled(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3);
    }
    
    public void loadCompleted(final DataSpec paramDataSpec, final int paramInt1, final int paramInt2, final Format paramFormat, final int paramInt3, final Object paramObject, final long paramLong1, long paramLong2, final long paramLong3, long paramLong4, final long paramLong5)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadCompleted(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong1), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong3), paramLong5, this.val$loadDurationMs, this.val$bytesLoaded);
          }
        });
      }
    }
    
    public void loadCompleted(DataSpec paramDataSpec, int paramInt, long paramLong1, long paramLong2, long paramLong3)
    {
      loadCompleted(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3);
    }
    
    public void loadError(final DataSpec paramDataSpec, final int paramInt1, final int paramInt2, final Format paramFormat, final int paramInt3, final Object paramObject, final long paramLong1, long paramLong2, final long paramLong3, long paramLong4, final long paramLong5, IOException paramIOException, final boolean paramBoolean)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadError(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong1), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong3), paramLong5, paramBoolean, this.val$bytesLoaded, this.val$error, this.val$wasCanceled);
          }
        });
      }
    }
    
    public void loadError(DataSpec paramDataSpec, int paramInt, long paramLong1, long paramLong2, long paramLong3, IOException paramIOException, boolean paramBoolean)
    {
      loadError(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3, paramIOException, paramBoolean);
    }
    
    public void loadStarted(final DataSpec paramDataSpec, final int paramInt1, final int paramInt2, final Format paramFormat, final int paramInt3, final Object paramObject, final long paramLong1, long paramLong2, final long paramLong3)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadStarted(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong1), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong3), this.val$elapsedRealtimeMs);
          }
        });
      }
    }
    
    public void loadStarted(DataSpec paramDataSpec, int paramInt, long paramLong)
    {
      loadStarted(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong);
    }
    
    public void upstreamDiscarded(final int paramInt, final long paramLong1, long paramLong2)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onUpstreamDiscarded(paramInt, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(paramLong1), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaEndTimeUs));
          }
        });
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/AdaptiveMediaSourceEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */