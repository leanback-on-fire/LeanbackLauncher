package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@TargetApi(16)
public abstract class MediaCodecRenderer
  extends BaseRenderer
{
  private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
  private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
  private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000L;
  private static final int RECONFIGURATION_STATE_NONE = 0;
  private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
  private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  private static final String TAG = "MediaCodecRenderer";
  private final DecoderInputBuffer buffer;
  private MediaCodec codec;
  private long codecHotswapDeadlineMs;
  private boolean codecIsAdaptive;
  private boolean codecNeedsAdaptationWorkaround;
  private boolean codecNeedsAdaptationWorkaroundBuffer;
  private boolean codecNeedsDiscardToSpsWorkaround;
  private boolean codecNeedsEosFlushWorkaround;
  private boolean codecNeedsEosPropagationWorkaround;
  private boolean codecNeedsFlushWorkaround;
  private boolean codecNeedsMonoChannelCountWorkaround;
  private boolean codecReceivedBuffers;
  private boolean codecReceivedEos;
  private int codecReconfigurationState;
  private boolean codecReconfigured;
  private int codecReinitializationState;
  private final List<Long> decodeOnlyPresentationTimestamps;
  protected DecoderCounters decoderCounters;
  private DrmSession<FrameworkMediaCrypto> drmSession;
  private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
  private Format format;
  private final FormatHolder formatHolder;
  private ByteBuffer[] inputBuffers;
  private int inputIndex;
  private boolean inputStreamEnded;
  private final MediaCodecSelector mediaCodecSelector;
  private final MediaCodec.BufferInfo outputBufferInfo;
  private ByteBuffer[] outputBuffers;
  private int outputIndex;
  private boolean outputStreamEnded;
  private DrmSession<FrameworkMediaCrypto> pendingDrmSession;
  private final boolean playClearSamplesWithoutKeys;
  private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
  private boolean shouldSkipOutputBuffer;
  private boolean waitingForFirstSyncFrame;
  private boolean waitingForKeys;
  
  public MediaCodecRenderer(int paramInt, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean)
  {
    super(paramInt);
    if (Util.SDK_INT >= 16) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.mediaCodecSelector = ((MediaCodecSelector)Assertions.checkNotNull(paramMediaCodecSelector));
      this.drmSessionManager = paramDrmSessionManager;
      this.playClearSamplesWithoutKeys = paramBoolean;
      this.buffer = new DecoderInputBuffer(0);
      this.formatHolder = new FormatHolder();
      this.decodeOnlyPresentationTimestamps = new ArrayList();
      this.outputBufferInfo = new MediaCodec.BufferInfo();
      this.codecReconfigurationState = 0;
      this.codecReinitializationState = 0;
      return;
    }
  }
  
  private static boolean codecNeedsAdaptationWorkaround(String paramString)
  {
    return (Util.SDK_INT < 24) && (("OMX.Nvidia.h264.decode".equals(paramString)) || ("OMX.Nvidia.h264.decode.secure".equals(paramString))) && (("flounder".equals(Util.DEVICE)) || ("flounder_lte".equals(Util.DEVICE)) || ("grouper".equals(Util.DEVICE)) || ("tilapia".equals(Util.DEVICE)));
  }
  
  private static boolean codecNeedsDiscardToSpsWorkaround(String paramString, Format paramFormat)
  {
    return (Util.SDK_INT < 21) && (paramFormat.initializationData.isEmpty()) && ("OMX.MTK.VIDEO.DECODER.AVC".equals(paramString));
  }
  
  private static boolean codecNeedsEosFlushWorkaround(String paramString)
  {
    return ((Util.SDK_INT <= 23) && ("OMX.google.vorbis.decoder".equals(paramString))) || ((Util.SDK_INT <= 19) && ("hb2000".equals(Util.DEVICE)) && (("OMX.amlogic.avc.decoder.awesome".equals(paramString)) || ("OMX.amlogic.avc.decoder.awesome.secure".equals(paramString))));
  }
  
  private static boolean codecNeedsEosPropagationWorkaround(String paramString)
  {
    return (Util.SDK_INT <= 17) && (("OMX.rk.video_decoder.avc".equals(paramString)) || ("OMX.allwinner.video.decoder.avc".equals(paramString)));
  }
  
  private static boolean codecNeedsFlushWorkaround(String paramString)
  {
    return (Util.SDK_INT < 18) || ((Util.SDK_INT == 18) && (("OMX.SEC.avc.dec".equals(paramString)) || ("OMX.SEC.avc.dec.secure".equals(paramString)))) || ((Util.SDK_INT == 19) && (Util.MODEL.startsWith("SM-G800")) && (("OMX.Exynos.avc.dec".equals(paramString)) || ("OMX.Exynos.avc.dec.secure".equals(paramString))));
  }
  
  private static boolean codecNeedsMonoChannelCountWorkaround(String paramString, Format paramFormat)
  {
    return (Util.SDK_INT <= 18) && (paramFormat.channelCount == 1) && ("OMX.MTK.AUDIO.DECODER.MP3".equals(paramString));
  }
  
  private boolean drainOutputBuffer(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputIndex < 0)
    {
      this.outputIndex = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
      if (this.outputIndex >= 0)
      {
        if (this.shouldSkipAdaptationWorkaroundOutputBuffer)
        {
          this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
          this.codec.releaseOutputBuffer(this.outputIndex, false);
          this.outputIndex = -1;
          return true;
        }
        if ((this.outputBufferInfo.flags & 0x4) != 0)
        {
          processEndOfStream();
          this.outputIndex = -1;
          return false;
        }
        ByteBuffer localByteBuffer = this.outputBuffers[this.outputIndex];
        if (localByteBuffer != null)
        {
          localByteBuffer.position(this.outputBufferInfo.offset);
          localByteBuffer.limit(this.outputBufferInfo.offset + this.outputBufferInfo.size);
        }
        this.shouldSkipOutputBuffer = shouldSkipOutputBuffer(this.outputBufferInfo.presentationTimeUs);
      }
    }
    else
    {
      if (!processOutputBuffer(paramLong1, paramLong2, this.codec, this.outputBuffers[this.outputIndex], this.outputIndex, this.outputBufferInfo.flags, this.outputBufferInfo.presentationTimeUs, this.shouldSkipOutputBuffer)) {
        break label272;
      }
      onProcessedOutputBuffer(this.outputBufferInfo.presentationTimeUs);
      this.outputIndex = -1;
      return true;
    }
    if (this.outputIndex == -2)
    {
      processOutputFormat();
      return true;
    }
    if (this.outputIndex == -3)
    {
      processOutputBuffersChanged();
      return true;
    }
    if ((this.codecNeedsEosPropagationWorkaround) && ((this.inputStreamEnded) || (this.codecReinitializationState == 2))) {
      processEndOfStream();
    }
    return false;
    label272:
    return false;
  }
  
  private boolean feedInputBuffer()
    throws ExoPlaybackException
  {
    if ((this.codec == null) || (this.codecReinitializationState == 2) || (this.inputStreamEnded)) {
      return false;
    }
    if (this.inputIndex < 0)
    {
      this.inputIndex = this.codec.dequeueInputBuffer(0L);
      if (this.inputIndex < 0) {
        return false;
      }
      this.buffer.data = this.inputBuffers[this.inputIndex];
      this.buffer.clear();
    }
    if (this.codecReinitializationState == 1)
    {
      if (this.codecNeedsEosPropagationWorkaround) {}
      for (;;)
      {
        this.codecReinitializationState = 2;
        return false;
        this.codecReceivedEos = true;
        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
        this.inputIndex = -1;
      }
    }
    if (this.codecNeedsAdaptationWorkaroundBuffer)
    {
      this.codecNeedsAdaptationWorkaroundBuffer = false;
      this.buffer.data.put(ADAPTATION_WORKAROUND_BUFFER);
      this.codec.queueInputBuffer(this.inputIndex, 0, ADAPTATION_WORKAROUND_BUFFER.length, 0L, 0);
      this.inputIndex = -1;
      this.codecReceivedBuffers = true;
      return true;
    }
    int i = 0;
    if (this.waitingForKeys) {}
    for (int j = -4; j == -3; j = readSource(this.formatHolder, this.buffer))
    {
      return false;
      if (this.codecReconfigurationState == 1)
      {
        i = 0;
        while (i < this.format.initializationData.size())
        {
          byte[] arrayOfByte = (byte[])this.format.initializationData.get(i);
          this.buffer.data.put(arrayOfByte);
          i += 1;
        }
        this.codecReconfigurationState = 2;
      }
      i = this.buffer.data.position();
    }
    if (j == -5)
    {
      if (this.codecReconfigurationState == 2)
      {
        this.buffer.clear();
        this.codecReconfigurationState = 1;
      }
      onInputFormatChanged(this.formatHolder.format);
      return true;
    }
    if (this.buffer.isEndOfStream())
    {
      if (this.codecReconfigurationState == 2)
      {
        this.buffer.clear();
        this.codecReconfigurationState = 1;
      }
      this.inputStreamEnded = true;
      if (!this.codecReceivedBuffers)
      {
        processEndOfStream();
        return false;
      }
      try
      {
        if (this.codecNeedsEosPropagationWorkaround) {
          break label700;
        }
        this.codecReceivedEos = true;
        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
        this.inputIndex = -1;
      }
      catch (MediaCodec.CryptoException localCryptoException1)
      {
        throw ExoPlaybackException.createForRenderer(localCryptoException1, getIndex());
      }
    }
    if ((this.waitingForFirstSyncFrame) && (!this.buffer.isKeyFrame()))
    {
      this.buffer.clear();
      if (this.codecReconfigurationState == 2) {
        this.codecReconfigurationState = 1;
      }
      return true;
    }
    this.waitingForFirstSyncFrame = false;
    boolean bool = this.buffer.isEncrypted();
    this.waitingForKeys = shouldWaitForKeys(bool);
    if (this.waitingForKeys) {
      return false;
    }
    if ((this.codecNeedsDiscardToSpsWorkaround) && (!bool))
    {
      NalUnitUtil.discardToSps(this.buffer.data);
      if (this.buffer.data.position() == 0) {
        return true;
      }
      this.codecNeedsDiscardToSpsWorkaround = false;
    }
    try
    {
      long l = this.buffer.timeUs;
      if (this.buffer.isDecodeOnly()) {
        this.decodeOnlyPresentationTimestamps.add(Long.valueOf(l));
      }
      this.buffer.flip();
      onQueueInputBuffer(this.buffer);
      Object localObject;
      if (bool)
      {
        localObject = getFrameworkCryptoInfo(this.buffer, i);
        this.codec.queueSecureInputBuffer(this.inputIndex, 0, (MediaCodec.CryptoInfo)localObject, l, 0);
      }
      for (;;)
      {
        this.inputIndex = -1;
        this.codecReceivedBuffers = true;
        this.codecReconfigurationState = 0;
        localObject = this.decoderCounters;
        ((DecoderCounters)localObject).inputBufferCount += 1;
        return true;
        this.codec.queueInputBuffer(this.inputIndex, 0, this.buffer.data.limit(), l, 0);
      }
      return false;
    }
    catch (MediaCodec.CryptoException localCryptoException2)
    {
      throw ExoPlaybackException.createForRenderer(localCryptoException2, getIndex());
    }
  }
  
  private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer paramDecoderInputBuffer, int paramInt)
  {
    paramDecoderInputBuffer = paramDecoderInputBuffer.cryptoInfo.getFrameworkCryptoInfoV16();
    if (paramInt == 0) {
      return paramDecoderInputBuffer;
    }
    if (paramDecoderInputBuffer.numBytesOfClearData == null) {
      paramDecoderInputBuffer.numBytesOfClearData = new int[1];
    }
    int[] arrayOfInt = paramDecoderInputBuffer.numBytesOfClearData;
    arrayOfInt[0] += paramInt;
    return paramDecoderInputBuffer;
  }
  
  private void processEndOfStream()
    throws ExoPlaybackException
  {
    if (this.codecReinitializationState == 2)
    {
      releaseCodec();
      maybeInitCodec();
      return;
    }
    this.outputStreamEnded = true;
    onOutputStreamEnded();
  }
  
  private void processOutputBuffersChanged()
  {
    this.outputBuffers = this.codec.getOutputBuffers();
  }
  
  private void processOutputFormat()
  {
    MediaFormat localMediaFormat = this.codec.getOutputFormat();
    if ((this.codecNeedsAdaptationWorkaround) && (localMediaFormat.getInteger("width") == 32) && (localMediaFormat.getInteger("height") == 32))
    {
      this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
      return;
    }
    if (this.codecNeedsMonoChannelCountWorkaround) {
      localMediaFormat.setInteger("channel-count", 1);
    }
    onOutputFormatChanged(this.codec, localMediaFormat);
  }
  
  private void readFormat()
    throws ExoPlaybackException
  {
    if (readSource(this.formatHolder, null) == -5) {
      onInputFormatChanged(this.formatHolder.format);
    }
  }
  
  private boolean shouldSkipOutputBuffer(long paramLong)
  {
    int j = this.decodeOnlyPresentationTimestamps.size();
    int i = 0;
    while (i < j)
    {
      if (((Long)this.decodeOnlyPresentationTimestamps.get(i)).longValue() == paramLong)
      {
        this.decodeOnlyPresentationTimestamps.remove(i);
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean shouldWaitForKeys(boolean paramBoolean)
    throws ExoPlaybackException
  {
    if (this.drmSession == null) {}
    int i;
    do
    {
      return false;
      i = this.drmSession.getState();
      if (i == 0) {
        throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
      }
    } while ((i == 4) || ((!paramBoolean) && (this.playClearSamplesWithoutKeys)));
    return true;
  }
  
  private void throwDecoderInitError(DecoderInitializationException paramDecoderInitializationException)
    throws ExoPlaybackException
  {
    throw ExoPlaybackException.createForRenderer(paramDecoderInitializationException, getIndex());
  }
  
  protected boolean canReconfigureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, Format paramFormat1, Format paramFormat2)
  {
    return false;
  }
  
  protected abstract void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto)
    throws MediaCodecUtil.DecoderQueryException;
  
  protected void flushCodec()
    throws ExoPlaybackException
  {
    this.codecHotswapDeadlineMs = -9223372036854775807L;
    this.inputIndex = -1;
    this.outputIndex = -1;
    this.waitingForFirstSyncFrame = true;
    this.waitingForKeys = false;
    this.shouldSkipOutputBuffer = false;
    this.decodeOnlyPresentationTimestamps.clear();
    this.codecNeedsAdaptationWorkaroundBuffer = false;
    this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
    if ((this.codecNeedsFlushWorkaround) || ((this.codecNeedsEosFlushWorkaround) && (this.codecReceivedEos)))
    {
      releaseCodec();
      maybeInitCodec();
    }
    for (;;)
    {
      if ((this.codecReconfigured) && (this.format != null)) {
        this.codecReconfigurationState = 1;
      }
      return;
      if (this.codecReinitializationState != 0)
      {
        releaseCodec();
        maybeInitCodec();
      }
      else
      {
        this.codec.flush();
        this.codecReceivedBuffers = false;
      }
    }
  }
  
  protected final MediaCodec getCodec()
  {
    return this.codec;
  }
  
  protected MediaCodecInfo getDecoderInfo(MediaCodecSelector paramMediaCodecSelector, Format paramFormat, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    return paramMediaCodecSelector.getDecoderInfo(paramFormat.sampleMimeType, paramBoolean);
  }
  
  protected long getDequeueOutputBufferTimeoutUs()
  {
    return 0L;
  }
  
  public boolean isEnded()
  {
    return this.outputStreamEnded;
  }
  
  public boolean isReady()
  {
    return (this.format != null) && (!this.waitingForKeys) && ((isSourceReady()) || (this.outputIndex >= 0) || ((this.codecHotswapDeadlineMs != -9223372036854775807L) && (SystemClock.elapsedRealtime() < this.codecHotswapDeadlineMs)));
  }
  
  protected final void maybeInitCodec()
    throws ExoPlaybackException
  {
    if (!shouldInitCodec()) {}
    String str;
    int i;
    do
    {
      return;
      this.drmSession = this.pendingDrmSession;
      str = this.format.sampleMimeType;
      localMediaCrypto = null;
      bool = false;
      if (this.drmSession == null) {
        break;
      }
      i = this.drmSession.getState();
      if (i == 0) {
        throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
      }
    } while ((i != 3) && (i != 4));
    localMediaCrypto = ((FrameworkMediaCrypto)this.drmSession.getMediaCrypto()).getWrappedMediaCrypto();
    bool = this.drmSession.requiresSecureDecoderComponent(str);
    localObject1 = null;
    try
    {
      localObject2 = getDecoderInfo(this.mediaCodecSelector, this.format, bool);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (bool)
        {
          localObject1 = localObject2;
          localObject2 = getDecoderInfo(this.mediaCodecSelector, this.format, false);
          localObject1 = localObject2;
          if (localObject2 != null)
          {
            localObject1 = localObject2;
            Log.w("MediaCodecRenderer", "Drm session requires secure decoder for " + str + ", but " + "no secure decoder available. Trying to proceed with " + ((MediaCodecInfo)localObject2).name + ".");
            localObject1 = localObject2;
          }
        }
      }
    }
    catch (MediaCodecUtil.DecoderQueryException localDecoderQueryException)
    {
      try
      {
        Object localObject2;
        l1 = SystemClock.elapsedRealtime();
        TraceUtil.beginSection("createCodec:" + (String)localObject2);
        this.codec = MediaCodec.createByCodecName((String)localObject2);
        TraceUtil.endSection();
        TraceUtil.beginSection("configureCodec");
        configureCodec((MediaCodecInfo)localObject1, this.codec, this.format, localMediaCrypto);
        TraceUtil.endSection();
        TraceUtil.beginSection("startCodec");
        this.codec.start();
        TraceUtil.endSection();
        long l2 = SystemClock.elapsedRealtime();
        onCodecInitialized((String)localObject2, l2, l2 - l1);
        this.inputBuffers = this.codec.getInputBuffers();
        this.outputBuffers = this.codec.getOutputBuffers();
        if (getState() != 2) {
          break label560;
        }
        l1 = SystemClock.elapsedRealtime() + 1000L;
        this.codecHotswapDeadlineMs = l1;
        this.inputIndex = -1;
        this.outputIndex = -1;
        this.waitingForFirstSyncFrame = true;
        localObject1 = this.decoderCounters;
        ((DecoderCounters)localObject1).decoderInitCount += 1;
        return;
        localDecoderQueryException = localDecoderQueryException;
        throwDecoderInitError(new DecoderInitializationException(this.format, localDecoderQueryException, bool, -49998));
      }
      catch (Exception localException)
      {
        for (;;)
        {
          throwDecoderInitError(new DecoderInitializationException(this.format, localException, bool, localDecoderQueryException));
          continue;
          long l1 = -9223372036854775807L;
        }
      }
    }
    if (localObject1 == null) {
      throwDecoderInitError(new DecoderInitializationException(this.format, null, bool, -49999));
    }
    localObject2 = ((MediaCodecInfo)localObject1).name;
    this.codecIsAdaptive = ((MediaCodecInfo)localObject1).adaptive;
    this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround((String)localObject2, this.format);
    this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround((String)localObject2);
    this.codecNeedsAdaptationWorkaround = codecNeedsAdaptationWorkaround((String)localObject2);
    this.codecNeedsEosPropagationWorkaround = codecNeedsEosPropagationWorkaround((String)localObject2);
    this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround((String)localObject2);
    this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround((String)localObject2, this.format);
  }
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2) {}
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 328	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:format	Lcom/google/android/exoplayer2/Format;
    //   5: aload_0
    //   6: invokevirtual 435	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:releaseCodec	()V
    //   9: aload_0
    //   10: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   13: ifnull +16 -> 29
    //   16: aload_0
    //   17: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   20: aload_0
    //   21: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   24: invokeinterface 656 2 0
    //   29: aload_0
    //   30: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   33: ifnull +27 -> 60
    //   36: aload_0
    //   37: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   40: aload_0
    //   41: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   44: if_acmpeq +16 -> 60
    //   47: aload_0
    //   48: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   51: aload_0
    //   52: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   55: invokeinterface 656 2 0
    //   60: aload_0
    //   61: aconst_null
    //   62: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   65: aload_0
    //   66: aconst_null
    //   67: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   70: return
    //   71: astore_1
    //   72: aload_0
    //   73: aconst_null
    //   74: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   77: aload_0
    //   78: aconst_null
    //   79: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   82: aload_1
    //   83: athrow
    //   84: astore_1
    //   85: aload_0
    //   86: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   89: ifnull +27 -> 116
    //   92: aload_0
    //   93: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   96: aload_0
    //   97: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   100: if_acmpeq +16 -> 116
    //   103: aload_0
    //   104: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   107: aload_0
    //   108: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   111: invokeinterface 656 2 0
    //   116: aload_0
    //   117: aconst_null
    //   118: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   121: aload_0
    //   122: aconst_null
    //   123: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   126: aload_1
    //   127: athrow
    //   128: astore_1
    //   129: aload_0
    //   130: aconst_null
    //   131: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   134: aload_0
    //   135: aconst_null
    //   136: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   139: aload_1
    //   140: athrow
    //   141: astore_1
    //   142: aload_0
    //   143: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   146: ifnull +16 -> 162
    //   149: aload_0
    //   150: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   153: aload_0
    //   154: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   157: invokeinterface 656 2 0
    //   162: aload_0
    //   163: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   166: ifnull +27 -> 193
    //   169: aload_0
    //   170: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   173: aload_0
    //   174: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   177: if_acmpeq +16 -> 193
    //   180: aload_0
    //   181: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   184: aload_0
    //   185: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   188: invokeinterface 656 2 0
    //   193: aload_0
    //   194: aconst_null
    //   195: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   198: aload_0
    //   199: aconst_null
    //   200: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   203: aload_1
    //   204: athrow
    //   205: astore_1
    //   206: aload_0
    //   207: aconst_null
    //   208: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   211: aload_0
    //   212: aconst_null
    //   213: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   216: aload_1
    //   217: athrow
    //   218: astore_1
    //   219: aload_0
    //   220: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   223: ifnull +27 -> 250
    //   226: aload_0
    //   227: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   230: aload_0
    //   231: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   234: if_acmpeq +16 -> 250
    //   237: aload_0
    //   238: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   241: aload_0
    //   242: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   245: invokeinterface 656 2 0
    //   250: aload_0
    //   251: aconst_null
    //   252: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   255: aload_0
    //   256: aconst_null
    //   257: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   260: aload_1
    //   261: athrow
    //   262: astore_1
    //   263: aload_0
    //   264: aconst_null
    //   265: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   268: aload_0
    //   269: aconst_null
    //   270: putfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   273: aload_1
    //   274: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	275	0	this	MediaCodecRenderer
    //   71	12	1	localObject1	Object
    //   84	43	1	localObject2	Object
    //   128	12	1	localObject3	Object
    //   141	63	1	localObject4	Object
    //   205	12	1	localObject5	Object
    //   218	43	1	localObject6	Object
    //   262	12	1	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   29	60	71	finally
    //   9	29	84	finally
    //   85	116	128	finally
    //   5	9	141	finally
    //   162	193	205	finally
    //   142	162	218	finally
    //   219	250	262	finally
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.decoderCounters = new DecoderCounters();
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    Format localFormat = this.format;
    this.format = paramFormat;
    DrmInitData localDrmInitData = this.format.drmInitData;
    int i;
    if (localFormat == null)
    {
      paramFormat = null;
      if (Util.areEqual(localDrmInitData, paramFormat)) {
        break label86;
      }
      i = 1;
    }
    for (;;)
    {
      if (i != 0)
      {
        if (this.format.drmInitData == null) {
          break label232;
        }
        if (this.drmSessionManager == null)
        {
          throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
          paramFormat = localFormat.drmInitData;
          break;
          label86:
          i = 0;
          continue;
        }
        this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.format.drmInitData);
        if (this.pendingDrmSession == this.drmSession) {
          this.drmSessionManager.releaseSession(this.pendingDrmSession);
        }
      }
    }
    if ((this.pendingDrmSession == this.drmSession) && (this.codec != null) && (canReconfigureCodec(this.codec, this.codecIsAdaptive, localFormat, this.format)))
    {
      this.codecReconfigured = true;
      this.codecReconfigurationState = 1;
      if ((this.codecNeedsAdaptationWorkaround) && (this.format.width == localFormat.width) && (this.format.height == localFormat.height)) {}
      for (boolean bool = true;; bool = false)
      {
        this.codecNeedsAdaptationWorkaroundBuffer = bool;
        return;
        label232:
        this.pendingDrmSession = null;
        break;
      }
    }
    if (this.codecReceivedBuffers)
    {
      this.codecReinitializationState = 1;
      return;
    }
    releaseCodec();
    maybeInitCodec();
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat) {}
  
  protected void onOutputStreamEnded() {}
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.codec != null) {
      flushCodec();
    }
  }
  
  protected void onProcessedOutputBuffer(long paramLong) {}
  
  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer) {}
  
  protected void onStarted() {}
  
  protected void onStopped() {}
  
  protected abstract boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean)
    throws ExoPlaybackException;
  
  /* Error */
  protected void releaseCodec()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   4: ifnull +189 -> 193
    //   7: aload_0
    //   8: ldc2_w 503
    //   11: putfield 506	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecHotswapDeadlineMs	J
    //   14: aload_0
    //   15: iconst_m1
    //   16: putfield 297	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   19: aload_0
    //   20: iconst_m1
    //   21: putfield 224	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:outputIndex	I
    //   24: aload_0
    //   25: iconst_0
    //   26: putfield 326	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:waitingForKeys	Z
    //   29: aload_0
    //   30: iconst_0
    //   31: putfield 273	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:shouldSkipOutputBuffer	Z
    //   34: aload_0
    //   35: getfield 138	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decodeOnlyPresentationTimestamps	Ljava/util/List;
    //   38: invokeinterface 507 1 0
    //   43: aload_0
    //   44: aconst_null
    //   45: putfield 303	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:inputBuffers	[Ljava/nio/ByteBuffer;
    //   48: aload_0
    //   49: aconst_null
    //   50: putfield 250	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:outputBuffers	[Ljava/nio/ByteBuffer;
    //   53: aload_0
    //   54: iconst_0
    //   55: putfield 513	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigured	Z
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 324	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedBuffers	Z
    //   63: aload_0
    //   64: iconst_0
    //   65: putfield 599	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecIsAdaptive	Z
    //   68: aload_0
    //   69: iconst_0
    //   70: putfield 371	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsDiscardToSpsWorkaround	Z
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 509	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsFlushWorkaround	Z
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 453	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaround	Z
    //   83: aload_0
    //   84: iconst_0
    //   85: putfield 289	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosPropagationWorkaround	Z
    //   88: aload_0
    //   89: iconst_0
    //   90: putfield 511	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosFlushWorkaround	Z
    //   93: aload_0
    //   94: iconst_0
    //   95: putfield 465	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsMonoChannelCountWorkaround	Z
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 318	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaroundBuffer	Z
    //   103: aload_0
    //   104: iconst_0
    //   105: putfield 238	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:shouldSkipAdaptationWorkaroundOutputBuffer	Z
    //   108: aload_0
    //   109: iconst_0
    //   110: putfield 312	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedEos	Z
    //   113: aload_0
    //   114: iconst_0
    //   115: putfield 145	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   118: aload_0
    //   119: iconst_0
    //   120: putfield 147	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codecReinitializationState	I
    //   123: aload_0
    //   124: getfield 409	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   127: astore_1
    //   128: aload_1
    //   129: aload_1
    //   130: getfield 697	com/google/android/exoplayer2/decoder/DecoderCounters:decoderReleaseCount	I
    //   133: iconst_1
    //   134: iadd
    //   135: putfield 697	com/google/android/exoplayer2/decoder/DecoderCounters:decoderReleaseCount	I
    //   138: aload_0
    //   139: getfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   142: invokevirtual 700	android/media/MediaCodec:stop	()V
    //   145: aload_0
    //   146: getfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   149: invokevirtual 703	android/media/MediaCodec:release	()V
    //   152: aload_0
    //   153: aconst_null
    //   154: putfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   157: aload_0
    //   158: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   161: ifnull +32 -> 193
    //   164: aload_0
    //   165: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   168: aload_0
    //   169: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   172: if_acmpeq +21 -> 193
    //   175: aload_0
    //   176: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   179: aload_0
    //   180: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   183: invokeinterface 656 2 0
    //   188: aload_0
    //   189: aconst_null
    //   190: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   193: return
    //   194: astore_1
    //   195: aload_0
    //   196: aconst_null
    //   197: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   200: aload_1
    //   201: athrow
    //   202: astore_1
    //   203: aload_0
    //   204: aconst_null
    //   205: putfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   208: aload_0
    //   209: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   212: ifnull +32 -> 244
    //   215: aload_0
    //   216: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   219: aload_0
    //   220: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   223: if_acmpeq +21 -> 244
    //   226: aload_0
    //   227: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   230: aload_0
    //   231: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   234: invokeinterface 656 2 0
    //   239: aload_0
    //   240: aconst_null
    //   241: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   244: aload_1
    //   245: athrow
    //   246: astore_1
    //   247: aload_0
    //   248: aconst_null
    //   249: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   252: aload_1
    //   253: athrow
    //   254: astore_1
    //   255: aload_0
    //   256: getfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   259: invokevirtual 703	android/media/MediaCodec:release	()V
    //   262: aload_0
    //   263: aconst_null
    //   264: putfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   267: aload_0
    //   268: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   271: ifnull +32 -> 303
    //   274: aload_0
    //   275: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   278: aload_0
    //   279: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   282: if_acmpeq +21 -> 303
    //   285: aload_0
    //   286: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   289: aload_0
    //   290: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   293: invokeinterface 656 2 0
    //   298: aload_0
    //   299: aconst_null
    //   300: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   303: aload_1
    //   304: athrow
    //   305: astore_1
    //   306: aload_0
    //   307: aconst_null
    //   308: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   311: aload_1
    //   312: athrow
    //   313: astore_1
    //   314: aload_0
    //   315: aconst_null
    //   316: putfield 226	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   319: aload_0
    //   320: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   323: ifnull +32 -> 355
    //   326: aload_0
    //   327: getfield 543	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   330: aload_0
    //   331: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   334: if_acmpeq +21 -> 355
    //   337: aload_0
    //   338: getfield 120	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   341: aload_0
    //   342: getfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   345: invokeinterface 656 2 0
    //   350: aload_0
    //   351: aconst_null
    //   352: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   355: aload_1
    //   356: athrow
    //   357: astore_1
    //   358: aload_0
    //   359: aconst_null
    //   360: putfield 484	com/google/android/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   363: aload_1
    //   364: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	365	0	this	MediaCodecRenderer
    //   127	3	1	localDecoderCounters	DecoderCounters
    //   194	7	1	localObject1	Object
    //   202	43	1	localObject2	Object
    //   246	7	1	localObject3	Object
    //   254	50	1	localObject4	Object
    //   305	7	1	localObject5	Object
    //   313	43	1	localObject6	Object
    //   357	7	1	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   175	188	194	finally
    //   145	152	202	finally
    //   226	239	246	finally
    //   138	145	254	finally
    //   285	298	305	finally
    //   255	262	313	finally
    //   337	350	357	finally
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputStreamEnded) {
      return;
    }
    if (this.format == null) {
      readFormat();
    }
    maybeInitCodec();
    if (this.codec != null)
    {
      TraceUtil.beginSection("drainAndFeed");
      while (drainOutputBuffer(paramLong1, paramLong2)) {}
      while (feedInputBuffer()) {}
      TraceUtil.endSection();
    }
    for (;;)
    {
      this.decoderCounters.ensureUpdated();
      return;
      if (this.format != null) {
        skipToKeyframeBefore(paramLong1);
      }
    }
  }
  
  protected boolean shouldInitCodec()
  {
    return (this.codec == null) && (this.format != null);
  }
  
  public final int supportsFormat(Format paramFormat)
    throws ExoPlaybackException
  {
    try
    {
      int i = supportsFormat(this.mediaCodecSelector, paramFormat);
      return i;
    }
    catch (MediaCodecUtil.DecoderQueryException paramFormat)
    {
      throw ExoPlaybackException.createForRenderer(paramFormat, getIndex());
    }
  }
  
  protected abstract int supportsFormat(MediaCodecSelector paramMediaCodecSelector, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException;
  
  public final int supportsMixedMimeTypeAdaptation()
    throws ExoPlaybackException
  {
    return 4;
  }
  
  public static class DecoderInitializationException
    extends Exception
  {
    private static final int CUSTOM_ERROR_CODE_BASE = -50000;
    private static final int DECODER_QUERY_ERROR = -49998;
    private static final int NO_SUITABLE_DECODER_ERROR = -49999;
    public final String decoderName;
    public final String diagnosticInfo;
    public final String mimeType;
    public final boolean secureDecoderRequired;
    
    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, int paramInt)
    {
      super(paramThrowable);
      this.mimeType = paramFormat.sampleMimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = null;
      this.diagnosticInfo = buildCustomDiagnosticInfo(paramInt);
    }
    
    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, String paramString)
    {
      super(paramThrowable);
      this.mimeType = paramFormat.sampleMimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = paramString;
      if (Util.SDK_INT >= 21) {}
      for (paramFormat = getDiagnosticInfoV21(paramThrowable);; paramFormat = null)
      {
        this.diagnosticInfo = paramFormat;
        return;
      }
    }
    
    private static String buildCustomDiagnosticInfo(int paramInt)
    {
      if (paramInt < 0) {}
      for (String str = "neg_";; str = "") {
        return "com.google.android.exoplayer.MediaCodecTrackRenderer_" + str + Math.abs(paramInt);
      }
    }
    
    @TargetApi(21)
    private static String getDiagnosticInfoV21(Throwable paramThrowable)
    {
      if ((paramThrowable instanceof MediaCodec.CodecException)) {
        return ((MediaCodec.CodecException)paramThrowable).getDiagnosticInfo();
      }
      return null;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/mediacodec/MediaCodecRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */