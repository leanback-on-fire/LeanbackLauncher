package com.google.android.exoplayer2.video;

import android.os.Handler;
import android.view.Surface;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;

public abstract interface VideoRendererEventListener
{
  public abstract void onDroppedFrames(int paramInt, long paramLong);
  
  public abstract void onRenderedFirstFrame(Surface paramSurface);
  
  public abstract void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2);
  
  public abstract void onVideoDisabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onVideoEnabled(DecoderCounters paramDecoderCounters);
  
  public abstract void onVideoInputFormatChanged(Format paramFormat);
  
  public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  
  public static final class EventDispatcher
  {
    private final Handler handler;
    private final VideoRendererEventListener listener;
    
    public EventDispatcher(Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener)
    {
      if (paramVideoRendererEventListener != null) {}
      for (paramHandler = (Handler)Assertions.checkNotNull(paramHandler);; paramHandler = null)
      {
        this.handler = paramHandler;
        this.listener = paramVideoRendererEventListener;
        return;
      }
    }
    
    public void decoderInitialized(final String paramString, final long paramLong1, long paramLong2)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            VideoRendererEventListener.this.onVideoDecoderInitialized(paramString, paramLong1, this.val$initializationDurationMs);
          }
        });
      }
    }
    
    public void disabled(final DecoderCounters paramDecoderCounters)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            paramDecoderCounters.ensureUpdated();
            VideoRendererEventListener.this.onVideoDisabled(paramDecoderCounters);
          }
        });
      }
    }
    
    public void droppedFrames(final int paramInt, final long paramLong)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            VideoRendererEventListener.this.onDroppedFrames(paramInt, paramLong);
          }
        });
      }
    }
    
    public void enabled(final DecoderCounters paramDecoderCounters)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            VideoRendererEventListener.this.onVideoEnabled(paramDecoderCounters);
          }
        });
      }
    }
    
    public void inputFormatChanged(final Format paramFormat)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            VideoRendererEventListener.this.onVideoInputFormatChanged(paramFormat);
          }
        });
      }
    }
    
    public void renderedFirstFrame(final Surface paramSurface)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            VideoRendererEventListener.this.onRenderedFirstFrame(paramSurface);
          }
        });
      }
    }
    
    public void videoSizeChanged(final int paramInt1, final int paramInt2, final int paramInt3, final float paramFloat)
    {
      if (this.listener != null) {
        this.handler.post(new Runnable()
        {
          public void run()
          {
            VideoRendererEventListener.this.onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
          }
        });
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/video/VideoRendererEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */