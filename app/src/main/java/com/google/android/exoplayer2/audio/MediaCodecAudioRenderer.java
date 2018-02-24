package com.google.android.exoplayer2.audio;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.PlaybackParams;
import android.os.Handler;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;

@TargetApi(16)
public class MediaCodecAudioRenderer
  extends MediaCodecRenderer
  implements MediaClock
{
  private boolean allowPositionDiscontinuity;
  private final AudioTrack audioTrack = new AudioTrack(paramAudioCapabilities, new AudioTrackListener(null));
  private long currentPositionUs;
  private final AudioRendererEventListener.EventDispatcher eventDispatcher;
  private boolean passthroughEnabled;
  private MediaFormat passthroughMediaFormat;
  private int pcmEncoding;
  
  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector)
  {
    this(paramMediaCodecSelector, null, true);
  }
  
  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramMediaCodecSelector, null, true, paramHandler, paramAudioRendererEventListener);
  }
  
  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean)
  {
    this(paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, null, null);
  }
  
  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramAudioRendererEventListener, null);
  }
  
  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities)
  {
    super(1, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean);
    this.eventDispatcher = new AudioRendererEventListener.EventDispatcher(paramHandler, paramAudioRendererEventListener);
  }
  
  protected boolean allowPassthrough(String paramString)
  {
    return this.audioTrack.isPassthroughSupported(paramString);
  }
  
  protected void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto)
  {
    if (this.passthroughEnabled)
    {
      this.passthroughMediaFormat = paramFormat.getFrameworkMediaFormatV16();
      this.passthroughMediaFormat.setString("mime", "audio/raw");
      paramMediaCodec.configure(this.passthroughMediaFormat, null, paramMediaCrypto, 0);
      this.passthroughMediaFormat.setString("mime", paramFormat.sampleMimeType);
      return;
    }
    paramMediaCodec.configure(paramFormat.getFrameworkMediaFormatV16(), null, paramMediaCrypto, 0);
    this.passthroughMediaFormat = null;
  }
  
  protected MediaCodecInfo getDecoderInfo(MediaCodecSelector paramMediaCodecSelector, Format paramFormat, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    if (allowPassthrough(paramFormat.sampleMimeType))
    {
      MediaCodecInfo localMediaCodecInfo = paramMediaCodecSelector.getPassthroughDecoderInfo();
      if (localMediaCodecInfo != null)
      {
        this.passthroughEnabled = true;
        return localMediaCodecInfo;
      }
    }
    this.passthroughEnabled = false;
    return super.getDecoderInfo(paramMediaCodecSelector, paramFormat, paramBoolean);
  }
  
  public MediaClock getMediaClock()
  {
    return this;
  }
  
  public long getPositionUs()
  {
    long l = this.audioTrack.getCurrentPositionUs(isEnded());
    if (l != Long.MIN_VALUE) {
      if (!this.allowPositionDiscontinuity) {
        break label42;
      }
    }
    for (;;)
    {
      this.currentPositionUs = l;
      this.allowPositionDiscontinuity = false;
      return this.currentPositionUs;
      label42:
      l = Math.max(this.currentPositionUs, l);
    }
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    switch (paramInt)
    {
    default: 
      super.handleMessage(paramInt, paramObject);
      return;
    case 2: 
      this.audioTrack.setVolume(((Float)paramObject).floatValue());
      return;
    case 3: 
      this.audioTrack.setPlaybackParams((PlaybackParams)paramObject);
      return;
    }
    paramInt = ((Integer)paramObject).intValue();
    this.audioTrack.setStreamType(paramInt);
  }
  
  public boolean isEnded()
  {
    return (super.isEnded()) && (!this.audioTrack.hasPendingData());
  }
  
  public boolean isReady()
  {
    return (this.audioTrack.hasPendingData()) || (super.isReady());
  }
  
  protected void onAudioSessionId(int paramInt) {}
  
  protected void onAudioTrackPositionDiscontinuity() {}
  
  protected void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2) {}
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.decoderInitialized(paramString, paramLong1, paramLong2);
  }
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 56	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:audioTrack	Lcom/google/android/exoplayer2/audio/AudioTrack;
    //   4: invokevirtual 197	com/google/android/exoplayer2/audio/AudioTrack:release	()V
    //   7: aload_0
    //   8: invokespecial 199	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:onDisabled	()V
    //   11: aload_0
    //   12: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   15: invokevirtual 208	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   18: aload_0
    //   19: getfield 63	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   22: aload_0
    //   23: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   26: invokevirtual 212	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   29: return
    //   30: astore_1
    //   31: aload_0
    //   32: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   35: invokevirtual 208	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   38: aload_0
    //   39: getfield 63	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   42: aload_0
    //   43: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   46: invokevirtual 212	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   49: aload_1
    //   50: athrow
    //   51: astore_1
    //   52: aload_0
    //   53: invokespecial 199	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:onDisabled	()V
    //   56: aload_0
    //   57: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   60: invokevirtual 208	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   63: aload_0
    //   64: getfield 63	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   67: aload_0
    //   68: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   71: invokevirtual 212	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   74: aload_1
    //   75: athrow
    //   76: astore_1
    //   77: aload_0
    //   78: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   81: invokevirtual 208	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   84: aload_0
    //   85: getfield 63	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   88: aload_0
    //   89: getfield 203	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   92: invokevirtual 212	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   95: aload_1
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	MediaCodecAudioRenderer
    //   30	20	1	localObject1	Object
    //   51	24	1	localObject2	Object
    //   76	20	1	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   7	11	30	finally
    //   0	7	51	finally
    //   52	56	76	finally
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramBoolean);
    this.eventDispatcher.enabled(this.decoderCounters);
    int i = getConfiguration().tunnelingAudioSessionId;
    if (i != 0)
    {
      this.audioTrack.enableTunnelingV21(i);
      return;
    }
    this.audioTrack.disableTunneling();
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    super.onInputFormatChanged(paramFormat);
    this.eventDispatcher.inputFormatChanged(paramFormat);
    if ("audio/raw".equals(paramFormat.sampleMimeType)) {}
    for (int i = paramFormat.pcmEncoding;; i = 2)
    {
      this.pcmEncoding = i;
      return;
    }
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
  {
    int i;
    if (this.passthroughMediaFormat != null)
    {
      i = 1;
      if (i == 0) {
        break label71;
      }
      paramMediaCodec = this.passthroughMediaFormat.getString("mime");
      label23:
      if (i == 0) {
        break label77;
      }
      paramMediaFormat = this.passthroughMediaFormat;
    }
    label71:
    label77:
    for (;;)
    {
      i = paramMediaFormat.getInteger("channel-count");
      int j = paramMediaFormat.getInteger("sample-rate");
      this.audioTrack.configure(paramMediaCodec, i, j, this.pcmEncoding, 0);
      return;
      i = 0;
      break;
      paramMediaCodec = "audio/raw";
      break label23;
    }
  }
  
  protected void onOutputStreamEnded()
  {
    this.audioTrack.handleEndOfStream();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onPositionReset(paramLong, paramBoolean);
    this.audioTrack.reset();
    this.currentPositionUs = paramLong;
    this.allowPositionDiscontinuity = true;
  }
  
  protected void onStarted()
  {
    super.onStarted();
    this.audioTrack.play();
  }
  
  protected void onStopped()
  {
    this.audioTrack.pause();
    super.onStopped();
  }
  
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean)
    throws ExoPlaybackException
  {
    if ((this.passthroughEnabled) && ((paramInt2 & 0x2) != 0))
    {
      paramMediaCodec.releaseOutputBuffer(paramInt1, false);
      return true;
    }
    if (paramBoolean)
    {
      paramMediaCodec.releaseOutputBuffer(paramInt1, false);
      paramMediaCodec = this.decoderCounters;
      paramMediaCodec.skippedOutputBufferCount += 1;
      this.audioTrack.handleDiscontinuity();
      return true;
    }
    try
    {
      if (this.audioTrack.handleBuffer(paramByteBuffer, paramLong3))
      {
        paramMediaCodec.releaseOutputBuffer(paramInt1, false);
        paramMediaCodec = this.decoderCounters;
        paramMediaCodec.renderedOutputBufferCount += 1;
        return true;
      }
    }
    catch (AudioTrack.InitializationException paramMediaCodec)
    {
      throw ExoPlaybackException.createForRenderer(paramMediaCodec, getIndex());
      return false;
    }
    catch (AudioTrack.WriteException paramMediaCodec)
    {
      for (;;) {}
    }
  }
  
  protected int supportsFormat(MediaCodecSelector paramMediaCodecSelector, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    String str = paramFormat.sampleMimeType;
    if (!MimeTypes.isAudio(str)) {
      return 0;
    }
    if (Util.SDK_INT >= 21) {}
    for (int i = 16; (allowPassthrough(str)) && (paramMediaCodecSelector.getPassthroughDecoderInfo() != null); i = 0) {
      return i | 0x4 | 0x3;
    }
    paramMediaCodecSelector = paramMediaCodecSelector.getDecoderInfo(str, false);
    if (paramMediaCodecSelector == null) {
      return 1;
    }
    if ((Util.SDK_INT < 21) || (((paramFormat.sampleRate == -1) || (paramMediaCodecSelector.isAudioSampleRateSupportedV21(paramFormat.sampleRate))) && ((paramFormat.channelCount == -1) || (paramMediaCodecSelector.isAudioChannelCountSupportedV21(paramFormat.channelCount)))))
    {
      j = 1;
      if (j == 0) {
        break label142;
      }
    }
    label142:
    for (int j = 3;; j = 2)
    {
      return i | 0x4 | j;
      j = 0;
      break;
    }
  }
  
  private final class AudioTrackListener
    implements AudioTrack.Listener
  {
    private AudioTrackListener() {}
    
    public void onAudioSessionId(int paramInt)
    {
      MediaCodecAudioRenderer.this.eventDispatcher.audioSessionId(paramInt);
      MediaCodecAudioRenderer.this.onAudioSessionId(paramInt);
    }
    
    public void onPositionDiscontinuity()
    {
      MediaCodecAudioRenderer.this.onAudioTrackPositionDiscontinuity();
      MediaCodecAudioRenderer.access$202(MediaCodecAudioRenderer.this, true);
    }
    
    public void onUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      MediaCodecAudioRenderer.this.eventDispatcher.audioTrackUnderrun(paramInt, paramLong1, paramLong2);
      MediaCodecAudioRenderer.this.onAudioTrackUnderrun(paramInt, paramLong1, paramLong2);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/audio/MediaCodecAudioRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */