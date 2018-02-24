package com.google.android.exoplayer2.audio;

import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;

public abstract class SimpleDecoderAudioRenderer
  extends BaseRenderer
  implements MediaClock
{
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  private boolean allowPositionDiscontinuity;
  private final AudioTrack audioTrack;
  private boolean audioTrackNeedsConfigure;
  private long currentPositionUs;
  private SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> decoder;
  private DecoderCounters decoderCounters;
  private boolean decoderReceivedBuffers;
  private int decoderReinitializationState;
  private DrmSession<ExoMediaCrypto> drmSession;
  private final DrmSessionManager<ExoMediaCrypto> drmSessionManager;
  private final AudioRendererEventListener.EventDispatcher eventDispatcher;
  private final FormatHolder formatHolder;
  private DecoderInputBuffer inputBuffer;
  private Format inputFormat;
  private boolean inputStreamEnded;
  private SimpleOutputBuffer outputBuffer;
  private boolean outputStreamEnded;
  private DrmSession<ExoMediaCrypto> pendingDrmSession;
  private final boolean playClearSamplesWithoutKeys;
  private boolean waitingForKeys;
  
  public SimpleDecoderAudioRenderer()
  {
    this(null, null);
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramHandler, paramAudioRendererEventListener, null);
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities)
  {
    this(paramHandler, paramAudioRendererEventListener, paramAudioCapabilities, null, false);
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities, DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, boolean paramBoolean)
  {
    super(1);
    this.eventDispatcher = new AudioRendererEventListener.EventDispatcher(paramHandler, paramAudioRendererEventListener);
    this.audioTrack = new AudioTrack(paramAudioCapabilities, new AudioTrackListener(null));
    this.drmSessionManager = paramDrmSessionManager;
    this.formatHolder = new FormatHolder();
    this.playClearSamplesWithoutKeys = paramBoolean;
    this.decoderReinitializationState = 0;
    this.audioTrackNeedsConfigure = true;
  }
  
  private boolean drainOutputBuffer()
    throws ExoPlaybackException, AudioDecoderException, AudioTrack.InitializationException, AudioTrack.WriteException
  {
    if (this.outputBuffer == null)
    {
      this.outputBuffer = ((SimpleOutputBuffer)this.decoder.dequeueOutputBuffer());
      if (this.outputBuffer != null) {}
    }
    do
    {
      return false;
      localObject = this.decoderCounters;
      ((DecoderCounters)localObject).skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
      if (this.outputBuffer.isEndOfStream())
      {
        if (this.decoderReinitializationState == 2)
        {
          releaseDecoder();
          maybeInitDecoder();
          this.audioTrackNeedsConfigure = true;
          return false;
        }
        this.outputBuffer.release();
        this.outputBuffer = null;
        this.outputStreamEnded = true;
        this.audioTrack.handleEndOfStream();
        return false;
      }
      if (this.audioTrackNeedsConfigure)
      {
        localObject = getOutputFormat();
        this.audioTrack.configure(((Format)localObject).sampleMimeType, ((Format)localObject).channelCount, ((Format)localObject).sampleRate, ((Format)localObject).pcmEncoding, 0);
        this.audioTrackNeedsConfigure = false;
      }
    } while (!this.audioTrack.handleBuffer(this.outputBuffer.data, this.outputBuffer.timeUs));
    Object localObject = this.decoderCounters;
    ((DecoderCounters)localObject).renderedOutputBufferCount += 1;
    this.outputBuffer.release();
    this.outputBuffer = null;
    return true;
  }
  
  private boolean feedInputBuffer()
    throws AudioDecoderException, ExoPlaybackException
  {
    if ((this.decoder == null) || (this.decoderReinitializationState == 2) || (this.inputStreamEnded)) {}
    label139:
    do
    {
      for (;;)
      {
        return false;
        if (this.inputBuffer == null)
        {
          this.inputBuffer = this.decoder.dequeueInputBuffer();
          if (this.inputBuffer == null) {}
        }
        else
        {
          if (this.decoderReinitializationState == 1)
          {
            this.inputBuffer.setFlags(4);
            this.decoder.queueInputBuffer(this.inputBuffer);
            this.inputBuffer = null;
            this.decoderReinitializationState = 2;
            return false;
          }
          if (this.waitingForKeys) {}
          for (int i = -4; i != -3; i = readSource(this.formatHolder, this.inputBuffer))
          {
            if (i != -5) {
              break label139;
            }
            onInputFormatChanged(this.formatHolder.format);
            return true;
          }
        }
      }
      if (this.inputBuffer.isEndOfStream())
      {
        this.inputStreamEnded = true;
        this.decoder.queueInputBuffer(this.inputBuffer);
        this.inputBuffer = null;
        return false;
      }
      this.waitingForKeys = shouldWaitForKeys(this.inputBuffer.isEncrypted());
    } while (this.waitingForKeys);
    this.inputBuffer.flip();
    this.decoder.queueInputBuffer(this.inputBuffer);
    this.decoderReceivedBuffers = true;
    DecoderCounters localDecoderCounters = this.decoderCounters;
    localDecoderCounters.inputBufferCount += 1;
    this.inputBuffer = null;
    return true;
  }
  
  private void flushDecoder()
    throws ExoPlaybackException
  {
    this.waitingForKeys = false;
    if (this.decoderReinitializationState != 0)
    {
      releaseDecoder();
      maybeInitDecoder();
      return;
    }
    this.inputBuffer = null;
    if (this.outputBuffer != null)
    {
      this.outputBuffer.release();
      this.outputBuffer = null;
    }
    this.decoder.flush();
    this.decoderReceivedBuffers = false;
  }
  
  private void maybeInitDecoder()
    throws ExoPlaybackException
  {
    if (this.decoder != null) {}
    int i;
    do
    {
      return;
      this.drmSession = this.pendingDrmSession;
      localObject = null;
      if (this.drmSession == null) {
        break;
      }
      i = this.drmSession.getState();
      if (i == 0) {
        throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
      }
    } while ((i != 3) && (i != 4));
    Object localObject = this.drmSession.getMediaCrypto();
    try
    {
      long l1 = SystemClock.elapsedRealtime();
      TraceUtil.beginSection("createAudioDecoder");
      this.decoder = createDecoder(this.inputFormat, (ExoMediaCrypto)localObject);
      TraceUtil.endSection();
      long l2 = SystemClock.elapsedRealtime();
      this.eventDispatcher.decoderInitialized(this.decoder.getName(), l2, l2 - l1);
      localObject = this.decoderCounters;
      ((DecoderCounters)localObject).decoderInitCount += 1;
      return;
    }
    catch (AudioDecoderException localAudioDecoderException)
    {
      throw ExoPlaybackException.createForRenderer(localAudioDecoderException, getIndex());
    }
  }
  
  private void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    Object localObject = this.inputFormat;
    this.inputFormat = paramFormat;
    DrmInitData localDrmInitData = this.inputFormat.drmInitData;
    int i;
    if (localObject == null)
    {
      localObject = null;
      if (Util.areEqual(localDrmInitData, localObject)) {
        break label83;
      }
      i = 1;
    }
    for (;;)
    {
      if (i != 0)
      {
        if (this.inputFormat.drmInitData == null) {
          break label156;
        }
        if (this.drmSessionManager == null)
        {
          throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
          localObject = ((Format)localObject).drmInitData;
          break;
          label83:
          i = 0;
          continue;
        }
        this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.inputFormat.drmInitData);
        if (this.pendingDrmSession == this.drmSession) {
          this.drmSessionManager.releaseSession(this.pendingDrmSession);
        }
      }
    }
    if (this.decoderReceivedBuffers) {
      this.decoderReinitializationState = 1;
    }
    for (;;)
    {
      this.eventDispatcher.inputFormatChanged(paramFormat);
      return;
      label156:
      this.pendingDrmSession = null;
      break;
      releaseDecoder();
      maybeInitDecoder();
      this.audioTrackNeedsConfigure = true;
    }
  }
  
  private boolean readFormat()
    throws ExoPlaybackException
  {
    if (readSource(this.formatHolder, null) == -5)
    {
      onInputFormatChanged(this.formatHolder.format);
      return true;
    }
    return false;
  }
  
  private void releaseDecoder()
  {
    if (this.decoder == null) {
      return;
    }
    this.inputBuffer = null;
    this.outputBuffer = null;
    this.decoder.release();
    this.decoder = null;
    DecoderCounters localDecoderCounters = this.decoderCounters;
    localDecoderCounters.decoderReleaseCount += 1;
    this.decoderReinitializationState = 0;
    this.decoderReceivedBuffers = false;
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
  
  protected abstract SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> createDecoder(Format paramFormat, ExoMediaCrypto paramExoMediaCrypto)
    throws AudioDecoderException;
  
  public MediaClock getMediaClock()
  {
    return this;
  }
  
  protected Format getOutputFormat()
  {
    return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, this.inputFormat.channelCount, this.inputFormat.sampleRate, 2, null, null, 0, null);
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
    return (this.outputStreamEnded) && (!this.audioTrack.hasPendingData());
  }
  
  public boolean isReady()
  {
    return (this.audioTrack.hasPendingData()) || ((this.inputFormat != null) && (!this.waitingForKeys) && ((isSourceReady()) || (this.outputBuffer != null)));
  }
  
  protected void onAudioSessionId(int paramInt) {}
  
  protected void onAudioTrackPositionDiscontinuity() {}
  
  protected void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2) {}
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 282	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:inputFormat	Lcom/google/android/exoplayer2/Format;
    //   5: aload_0
    //   6: iconst_1
    //   7: putfield 97	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:audioTrackNeedsConfigure	Z
    //   10: aload_0
    //   11: iconst_0
    //   12: putfield 210	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:waitingForKeys	Z
    //   15: aload_0
    //   16: invokespecial 141	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:releaseDecoder	()V
    //   19: aload_0
    //   20: getfield 83	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:audioTrack	Lcom/google/android/exoplayer2/audio/AudioTrack;
    //   23: invokevirtual 407	com/google/android/exoplayer2/audio/AudioTrack:release	()V
    //   26: aload_0
    //   27: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   30: ifnull +16 -> 46
    //   33: aload_0
    //   34: getfield 85	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   37: aload_0
    //   38: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   41: invokeinterface 332 2 0
    //   46: aload_0
    //   47: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   50: ifnull +27 -> 77
    //   53: aload_0
    //   54: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   57: aload_0
    //   58: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   61: if_acmpeq +16 -> 77
    //   64: aload_0
    //   65: getfield 85	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   68: aload_0
    //   69: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   72: invokeinterface 332 2 0
    //   77: aload_0
    //   78: aconst_null
    //   79: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   82: aload_0
    //   83: aconst_null
    //   84: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   87: aload_0
    //   88: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   91: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   94: aload_0
    //   95: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   98: aload_0
    //   99: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   102: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   105: return
    //   106: astore_1
    //   107: aload_0
    //   108: aconst_null
    //   109: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   112: aload_0
    //   113: aconst_null
    //   114: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   117: aload_0
    //   118: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   121: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   124: aload_0
    //   125: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   128: aload_0
    //   129: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   132: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   135: aload_1
    //   136: athrow
    //   137: astore_1
    //   138: aload_0
    //   139: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   142: ifnull +27 -> 169
    //   145: aload_0
    //   146: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   149: aload_0
    //   150: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   153: if_acmpeq +16 -> 169
    //   156: aload_0
    //   157: getfield 85	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   160: aload_0
    //   161: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   164: invokeinterface 332 2 0
    //   169: aload_0
    //   170: aconst_null
    //   171: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   174: aload_0
    //   175: aconst_null
    //   176: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   179: aload_0
    //   180: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   183: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   186: aload_0
    //   187: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   190: aload_0
    //   191: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   194: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   197: aload_1
    //   198: athrow
    //   199: astore_1
    //   200: aload_0
    //   201: aconst_null
    //   202: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   205: aload_0
    //   206: aconst_null
    //   207: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   210: aload_0
    //   211: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   214: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   217: aload_0
    //   218: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   221: aload_0
    //   222: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   225: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   228: aload_1
    //   229: athrow
    //   230: astore_1
    //   231: aload_0
    //   232: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   235: ifnull +16 -> 251
    //   238: aload_0
    //   239: getfield 85	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   242: aload_0
    //   243: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   246: invokeinterface 332 2 0
    //   251: aload_0
    //   252: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   255: ifnull +27 -> 282
    //   258: aload_0
    //   259: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   262: aload_0
    //   263: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   266: if_acmpeq +16 -> 282
    //   269: aload_0
    //   270: getfield 85	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   273: aload_0
    //   274: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   277: invokeinterface 332 2 0
    //   282: aload_0
    //   283: aconst_null
    //   284: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   287: aload_0
    //   288: aconst_null
    //   289: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   292: aload_0
    //   293: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   296: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   299: aload_0
    //   300: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   303: aload_0
    //   304: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   307: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   310: aload_1
    //   311: athrow
    //   312: astore_1
    //   313: aload_0
    //   314: aconst_null
    //   315: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   318: aload_0
    //   319: aconst_null
    //   320: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   323: aload_0
    //   324: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   327: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   330: aload_0
    //   331: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   334: aload_0
    //   335: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   338: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   341: aload_1
    //   342: athrow
    //   343: astore_1
    //   344: aload_0
    //   345: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   348: ifnull +27 -> 375
    //   351: aload_0
    //   352: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   355: aload_0
    //   356: getfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   359: if_acmpeq +16 -> 375
    //   362: aload_0
    //   363: getfield 85	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lcom/google/android/exoplayer2/drm/DrmSessionManager;
    //   366: aload_0
    //   367: getfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   370: invokeinterface 332 2 0
    //   375: aload_0
    //   376: aconst_null
    //   377: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   380: aload_0
    //   381: aconst_null
    //   382: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   385: aload_0
    //   386: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   389: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   392: aload_0
    //   393: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   396: aload_0
    //   397: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   400: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   403: aload_1
    //   404: athrow
    //   405: astore_1
    //   406: aload_0
    //   407: aconst_null
    //   408: putfield 245	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   411: aload_0
    //   412: aconst_null
    //   413: putfield 243	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lcom/google/android/exoplayer2/drm/DrmSession;
    //   416: aload_0
    //   417: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   420: invokevirtual 410	com/google/android/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   423: aload_0
    //   424: getfield 73	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lcom/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   427: aload_0
    //   428: getfield 129	com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lcom/google/android/exoplayer2/decoder/DecoderCounters;
    //   431: invokevirtual 414	com/google/android/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lcom/google/android/exoplayer2/decoder/DecoderCounters;)V
    //   434: aload_1
    //   435: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	436	0	this	SimpleDecoderAudioRenderer
    //   106	30	1	localObject1	Object
    //   137	61	1	localObject2	Object
    //   199	30	1	localObject3	Object
    //   230	81	1	localObject4	Object
    //   312	30	1	localObject5	Object
    //   343	61	1	localObject6	Object
    //   405	30	1	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   46	77	106	finally
    //   26	46	137	finally
    //   138	169	199	finally
    //   15	26	230	finally
    //   251	282	312	finally
    //   231	251	343	finally
    //   344	375	405	finally
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.decoderCounters = new DecoderCounters();
    this.eventDispatcher.enabled(this.decoderCounters);
    int i = getConfiguration().tunnelingAudioSessionId;
    if (i != 0)
    {
      this.audioTrack.enableTunnelingV21(i);
      return;
    }
    this.audioTrack.disableTunneling();
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.audioTrack.reset();
    this.currentPositionUs = paramLong;
    this.allowPositionDiscontinuity = true;
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.decoder != null) {
      flushDecoder();
    }
  }
  
  protected void onStarted()
  {
    this.audioTrack.play();
  }
  
  protected void onStopped()
  {
    this.audioTrack.pause();
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputStreamEnded) {}
    do
    {
      do
      {
        return;
      } while ((this.inputFormat == null) && (!readFormat()));
      maybeInitDecoder();
    } while (this.decoder == null);
    try
    {
      TraceUtil.beginSection("drainAndFeed");
      while (drainOutputBuffer()) {}
      while (feedInputBuffer()) {}
      TraceUtil.endSection();
      this.decoderCounters.ensureUpdated();
      return;
    }
    catch (AudioTrack.WriteException localWriteException)
    {
      throw ExoPlaybackException.createForRenderer(localWriteException, getIndex());
    }
    catch (AudioTrack.InitializationException localInitializationException)
    {
      for (;;) {}
    }
    catch (AudioDecoderException localAudioDecoderException)
    {
      for (;;) {}
    }
  }
  
  public final int supportsFormat(Format paramFormat)
  {
    int j = supportsFormatInternal(paramFormat);
    if ((j == 0) || (j == 1)) {
      return j;
    }
    if (Util.SDK_INT >= 21) {}
    for (int i = 16;; i = 0) {
      return j | i | 0x4;
    }
  }
  
  protected abstract int supportsFormatInternal(Format paramFormat);
  
  private final class AudioTrackListener
    implements AudioTrack.Listener
  {
    private AudioTrackListener() {}
    
    public void onAudioSessionId(int paramInt)
    {
      SimpleDecoderAudioRenderer.this.eventDispatcher.audioSessionId(paramInt);
      SimpleDecoderAudioRenderer.this.onAudioSessionId(paramInt);
    }
    
    public void onPositionDiscontinuity()
    {
      SimpleDecoderAudioRenderer.this.onAudioTrackPositionDiscontinuity();
      SimpleDecoderAudioRenderer.access$202(SimpleDecoderAudioRenderer.this, true);
    }
    
    public void onUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      SimpleDecoderAudioRenderer.this.eventDispatcher.audioTrackUnderrun(paramInt, paramLong1, paramLong2);
      SimpleDecoderAudioRenderer.this.onAudioTrackUnderrun(paramInt, paramLong1, paramLong2);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/audio/SimpleDecoderAudioRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */