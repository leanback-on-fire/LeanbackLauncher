package com.google.android.exoplayer2.metadata;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.util.Assertions;

public final class MetadataRenderer
  extends BaseRenderer
  implements Handler.Callback
{
  private static final int MSG_INVOKE_RENDERER = 0;
  private final MetadataInputBuffer buffer;
  private MetadataDecoder decoder;
  private final MetadataDecoderFactory decoderFactory;
  private final FormatHolder formatHolder;
  private boolean inputStreamEnded;
  private final Output output;
  private final Handler outputHandler;
  private Metadata pendingMetadata;
  private long pendingMetadataTimestamp;
  
  public MetadataRenderer(Output paramOutput, Looper paramLooper)
  {
    this(paramOutput, paramLooper, MetadataDecoderFactory.DEFAULT);
  }
  
  public MetadataRenderer(Output paramOutput, Looper paramLooper, MetadataDecoderFactory paramMetadataDecoderFactory)
  {
    super(4);
    this.output = ((Output)Assertions.checkNotNull(paramOutput));
    if (paramLooper == null) {}
    for (paramOutput = null;; paramOutput = new Handler(paramLooper, this))
    {
      this.outputHandler = paramOutput;
      this.decoderFactory = ((MetadataDecoderFactory)Assertions.checkNotNull(paramMetadataDecoderFactory));
      this.formatHolder = new FormatHolder();
      this.buffer = new MetadataInputBuffer();
      return;
    }
  }
  
  private void invokeRenderer(Metadata paramMetadata)
  {
    if (this.outputHandler != null)
    {
      this.outputHandler.obtainMessage(0, paramMetadata).sendToTarget();
      return;
    }
    invokeRendererInternal(paramMetadata);
  }
  
  private void invokeRendererInternal(Metadata paramMetadata)
  {
    this.output.onMetadata(paramMetadata);
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return false;
    }
    invokeRendererInternal((Metadata)paramMessage.obj);
    return true;
  }
  
  public boolean isEnded()
  {
    return this.inputStreamEnded;
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  protected void onDisabled()
  {
    this.pendingMetadata = null;
    this.decoder = null;
    super.onDisabled();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    this.pendingMetadata = null;
    this.inputStreamEnded = false;
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat)
    throws ExoPlaybackException
  {
    this.decoder = this.decoderFactory.createDecoder(paramArrayOfFormat[0]);
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if ((!this.inputStreamEnded) && (this.pendingMetadata == null))
    {
      this.buffer.clear();
      if (readSource(this.formatHolder, this.buffer) == -4)
      {
        if (!this.buffer.isEndOfStream()) {
          break label83;
        }
        this.inputStreamEnded = true;
      }
    }
    for (;;)
    {
      if ((this.pendingMetadata != null) && (this.pendingMetadataTimestamp <= paramLong1))
      {
        invokeRenderer(this.pendingMetadata);
        this.pendingMetadata = null;
      }
      return;
      label83:
      if (this.buffer.isDecodeOnly()) {
        continue;
      }
      this.pendingMetadataTimestamp = this.buffer.timeUs;
      this.buffer.subsampleOffsetUs = this.formatHolder.format.subsampleOffsetUs;
      this.buffer.flip();
      try
      {
        this.pendingMetadata = this.decoder.decode(this.buffer);
      }
      catch (MetadataDecoderException localMetadataDecoderException)
      {
        throw ExoPlaybackException.createForRenderer(localMetadataDecoderException, getIndex());
      }
    }
  }
  
  public int supportsFormat(Format paramFormat)
  {
    if (this.decoderFactory.supportsFormat(paramFormat)) {
      return 3;
    }
    return 0;
  }
  
  public static abstract interface Output
  {
    public abstract void onMetadata(Metadata paramMetadata);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/MetadataRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */