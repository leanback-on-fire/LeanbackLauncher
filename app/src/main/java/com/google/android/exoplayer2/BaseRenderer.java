package com.google.android.exoplayer2;

import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MediaClock;
import java.io.IOException;

public abstract class BaseRenderer
  implements Renderer, RendererCapabilities
{
  private RendererConfiguration configuration;
  private int index;
  private boolean readEndOfStream;
  private int state;
  private SampleStream stream;
  private boolean streamIsFinal;
  private long streamOffsetUs;
  private final int trackType;
  
  public BaseRenderer(int paramInt)
  {
    this.trackType = paramInt;
    this.readEndOfStream = true;
  }
  
  public final void disable()
  {
    boolean bool = true;
    if (this.state == 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      this.state = 0;
      onDisabled();
      this.stream = null;
      this.streamIsFinal = false;
      return;
      bool = false;
    }
  }
  
  public final void enable(RendererConfiguration paramRendererConfiguration, Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong1, boolean paramBoolean, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.state == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.configuration = paramRendererConfiguration;
      this.state = 1;
      onEnabled(paramBoolean);
      replaceStream(paramArrayOfFormat, paramSampleStream, paramLong2);
      onPositionReset(paramLong1, paramBoolean);
      return;
    }
  }
  
  public final RendererCapabilities getCapabilities()
  {
    return this;
  }
  
  protected final RendererConfiguration getConfiguration()
  {
    return this.configuration;
  }
  
  protected final int getIndex()
  {
    return this.index;
  }
  
  public MediaClock getMediaClock()
  {
    return null;
  }
  
  public final int getState()
  {
    return this.state;
  }
  
  public final SampleStream getStream()
  {
    return this.stream;
  }
  
  public final int getTrackType()
  {
    return this.trackType;
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {}
  
  public final boolean hasReadStreamToEnd()
  {
    return this.readEndOfStream;
  }
  
  public final boolean isCurrentStreamFinal()
  {
    return this.streamIsFinal;
  }
  
  protected final boolean isSourceReady()
  {
    if (this.readEndOfStream) {
      return this.streamIsFinal;
    }
    return this.stream.isReady();
  }
  
  public final void maybeThrowStreamError()
    throws IOException
  {
    this.stream.maybeThrowError();
  }
  
  protected void onDisabled() {}
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {}
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {}
  
  protected void onStarted()
    throws ExoPlaybackException
  {}
  
  protected void onStopped()
    throws ExoPlaybackException
  {}
  
  protected void onStreamChanged(Format[] paramArrayOfFormat)
    throws ExoPlaybackException
  {}
  
  protected final int readSource(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    int i = this.stream.readData(paramFormatHolder, paramDecoderInputBuffer);
    if (i == -4)
    {
      if (paramDecoderInputBuffer.isEndOfStream())
      {
        this.readEndOfStream = true;
        if (this.streamIsFinal) {
          return -4;
        }
        return -3;
      }
      paramDecoderInputBuffer.timeUs += this.streamOffsetUs;
    }
    for (;;)
    {
      return i;
      if (i == -5)
      {
        paramDecoderInputBuffer = paramFormatHolder.format;
        if (paramDecoderInputBuffer.subsampleOffsetUs != Long.MAX_VALUE) {
          paramFormatHolder.format = paramDecoderInputBuffer.copyWithSubsampleOffsetUs(paramDecoderInputBuffer.subsampleOffsetUs + this.streamOffsetUs);
        }
      }
    }
  }
  
  public final void replaceStream(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong)
    throws ExoPlaybackException
  {
    if (!this.streamIsFinal) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.stream = paramSampleStream;
      this.readEndOfStream = false;
      this.streamOffsetUs = paramLong;
      onStreamChanged(paramArrayOfFormat);
      return;
    }
  }
  
  public final void resetPosition(long paramLong)
    throws ExoPlaybackException
  {
    this.streamIsFinal = false;
    this.readEndOfStream = false;
    onPositionReset(paramLong, false);
  }
  
  public final void setCurrentStreamFinal()
  {
    this.streamIsFinal = true;
  }
  
  public final void setIndex(int paramInt)
  {
    this.index = paramInt;
  }
  
  protected void skipToKeyframeBefore(long paramLong)
  {
    this.stream.skipToKeyframeBefore(paramLong);
  }
  
  public final void start()
    throws ExoPlaybackException
  {
    boolean bool = true;
    if (this.state == 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      this.state = 2;
      onStarted();
      return;
      bool = false;
    }
  }
  
  public final void stop()
    throws ExoPlaybackException
  {
    if (this.state == 2) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 1;
      onStopped();
      return;
    }
  }
  
  public int supportsMixedMimeTypeAdaptation()
    throws ExoPlaybackException
  {
    return 0;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/BaseRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */