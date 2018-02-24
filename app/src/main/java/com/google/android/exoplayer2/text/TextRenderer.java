package com.google.android.exoplayer2.text;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.Collections;
import java.util.List;

public final class TextRenderer
  extends BaseRenderer
  implements Handler.Callback
{
  private static final int MSG_UPDATE_OUTPUT = 0;
  private SubtitleDecoder decoder;
  private final SubtitleDecoderFactory decoderFactory;
  private final FormatHolder formatHolder;
  private boolean inputStreamEnded;
  private SubtitleInputBuffer nextInputBuffer;
  private SubtitleOutputBuffer nextSubtitle;
  private int nextSubtitleEventIndex;
  private final Output output;
  private final Handler outputHandler;
  private boolean outputStreamEnded;
  private SubtitleOutputBuffer subtitle;
  
  public TextRenderer(Output paramOutput, Looper paramLooper)
  {
    this(paramOutput, paramLooper, SubtitleDecoderFactory.DEFAULT);
  }
  
  public TextRenderer(Output paramOutput, Looper paramLooper, SubtitleDecoderFactory paramSubtitleDecoderFactory)
  {
    super(3);
    this.output = ((Output)Assertions.checkNotNull(paramOutput));
    if (paramLooper == null) {}
    for (paramOutput = null;; paramOutput = new Handler(paramLooper, this))
    {
      this.outputHandler = paramOutput;
      this.decoderFactory = paramSubtitleDecoderFactory;
      this.formatHolder = new FormatHolder();
      return;
    }
  }
  
  private void clearOutput()
  {
    updateOutput(Collections.emptyList());
  }
  
  private long getNextEventTime()
  {
    if ((this.nextSubtitleEventIndex == -1) || (this.nextSubtitleEventIndex >= this.subtitle.getEventTimeCount())) {
      return Long.MAX_VALUE;
    }
    return this.subtitle.getEventTime(this.nextSubtitleEventIndex);
  }
  
  private void invokeUpdateOutputInternal(List<Cue> paramList)
  {
    this.output.onCues(paramList);
  }
  
  private void resetBuffers()
  {
    this.nextInputBuffer = null;
    this.nextSubtitleEventIndex = -1;
    if (this.subtitle != null)
    {
      this.subtitle.release();
      this.subtitle = null;
    }
    if (this.nextSubtitle != null)
    {
      this.nextSubtitle.release();
      this.nextSubtitle = null;
    }
  }
  
  private void updateOutput(List<Cue> paramList)
  {
    if (this.outputHandler != null)
    {
      this.outputHandler.obtainMessage(0, paramList).sendToTarget();
      return;
    }
    invokeUpdateOutputInternal(paramList);
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return false;
    }
    invokeUpdateOutputInternal((List)paramMessage.obj);
    return true;
  }
  
  public boolean isEnded()
  {
    return this.outputStreamEnded;
  }
  
  public boolean isReady()
  {
    return true;
  }
  
  protected void onDisabled()
  {
    clearOutput();
    resetBuffers();
    this.decoder.release();
    this.decoder = null;
    super.onDisabled();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    clearOutput();
    resetBuffers();
    this.decoder.flush();
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat)
    throws ExoPlaybackException
  {
    if (this.decoder != null)
    {
      this.decoder.release();
      this.nextInputBuffer = null;
    }
    this.decoder = this.decoderFactory.createDecoder(paramArrayOfFormat[0]);
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputStreamEnded) {}
    int i;
    int j;
    for (;;)
    {
      return;
      if (this.nextSubtitle == null) {
        this.decoder.setPositionUs(paramLong1);
      }
      try
      {
        this.nextSubtitle = ((SubtitleOutputBuffer)this.decoder.dequeueOutputBuffer());
        if (getState() == 2)
        {
          i = 0;
          j = 0;
          if (this.subtitle != null)
          {
            paramLong2 = getNextEventTime();
            for (i = j; paramLong2 <= paramLong1; i = 1)
            {
              this.nextSubtitleEventIndex += 1;
              paramLong2 = getNextEventTime();
            }
          }
          j = i;
        }
      }
      catch (SubtitleDecoderException localSubtitleDecoderException1)
      {
        throw ExoPlaybackException.createForRenderer(localSubtitleDecoderException1, getIndex());
      }
    }
    if (this.nextSubtitle != null)
    {
      if (!this.nextSubtitle.isEndOfStream()) {
        break label327;
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if (getNextEventTime() == Long.MAX_VALUE)
        {
          if (this.subtitle != null)
          {
            this.subtitle.release();
            this.subtitle = null;
          }
          this.nextSubtitle.release();
          this.nextSubtitle = null;
          this.outputStreamEnded = true;
          j = i;
        }
      }
    }
    label195:
    if (j != 0) {
      updateOutput(this.subtitle.getCues(paramLong1));
    }
    label327:
    label388:
    label415:
    do
    {
      for (;;)
      {
        try
        {
          if (this.inputStreamEnded) {
            break;
          }
          if (this.nextInputBuffer == null)
          {
            this.nextInputBuffer = ((SubtitleInputBuffer)this.decoder.dequeueInputBuffer());
            if (this.nextInputBuffer == null) {
              break;
            }
          }
          i = readSource(this.formatHolder, this.nextInputBuffer);
          if (i != -4) {
            break label415;
          }
          this.nextInputBuffer.clearFlag(Integer.MIN_VALUE);
          if (!this.nextInputBuffer.isEndOfStream()) {
            break label388;
          }
          this.inputStreamEnded = true;
          this.decoder.queueInputBuffer(this.nextInputBuffer);
          this.nextInputBuffer = null;
          continue;
          j = i;
        }
        catch (SubtitleDecoderException localSubtitleDecoderException2)
        {
          throw ExoPlaybackException.createForRenderer(localSubtitleDecoderException2, getIndex());
        }
        if (this.nextSubtitle.timeUs > paramLong1) {
          break label195;
        }
        if (this.subtitle != null) {
          this.subtitle.release();
        }
        this.subtitle = this.nextSubtitle;
        this.nextSubtitle = null;
        this.nextSubtitleEventIndex = this.subtitle.getNextEventTimeIndex(paramLong1);
        j = 1;
        break label195;
        this.nextInputBuffer.subsampleOffsetUs = this.formatHolder.format.subsampleOffsetUs;
        this.nextInputBuffer.flip();
      }
    } while (i != -3);
  }
  
  public int supportsFormat(Format paramFormat)
  {
    if (this.decoderFactory.supportsFormat(paramFormat)) {
      return 3;
    }
    if (MimeTypes.isText(paramFormat.sampleMimeType)) {
      return 1;
    }
    return 0;
  }
  
  public static abstract interface Output
  {
    public abstract void onCues(List<Cue> paramList);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/TextRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */