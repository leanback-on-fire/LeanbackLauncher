package com.google.android.exoplayer2.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaCodec.OnFrameRenderedListener;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;

@TargetApi(16)
public class MediaCodecVideoRenderer
  extends MediaCodecRenderer
{
  private static final String KEY_CROP_BOTTOM = "crop-bottom";
  private static final String KEY_CROP_LEFT = "crop-left";
  private static final String KEY_CROP_RIGHT = "crop-right";
  private static final String KEY_CROP_TOP = "crop-top";
  private static final int[] STANDARD_LONG_EDGE_VIDEO_PX = { 1920, 1600, 1440, 1280, 960, 854, 640, 540, 480 };
  private static final String TAG = "MediaCodecVideoRenderer";
  private final long allowedJoiningTimeMs;
  private CodecMaxValues codecMaxValues;
  private int consecutiveDroppedFrameCount;
  private int currentHeight;
  private float currentPixelWidthHeightRatio;
  private int currentUnappliedRotationDegrees;
  private int currentWidth;
  private final boolean deviceNeedsAutoFrcWorkaround;
  private long droppedFrameAccumulationStartTimeMs;
  private int droppedFrames;
  private final VideoRendererEventListener.EventDispatcher eventDispatcher;
  private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
  private long joiningDeadlineMs;
  private int lastReportedHeight;
  private float lastReportedPixelWidthHeightRatio;
  private int lastReportedUnappliedRotationDegrees;
  private int lastReportedWidth;
  private final int maxDroppedFramesToNotify;
  private float pendingPixelWidthHeightRatio;
  private int pendingRotationDegrees;
  private boolean renderedFirstFrame;
  private int scalingMode;
  private Format[] streamFormats;
  private Surface surface;
  private boolean tunneling;
  private int tunnelingAudioSessionId;
  OnFrameRenderedListenerV23 tunnelingOnFrameRenderedListener;
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector)
  {
    this(paramContext, paramMediaCodecSelector, 0L);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong)
  {
    this(paramContext, paramMediaCodecSelector, paramLong, null, null, -1);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong, Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, int paramInt)
  {
    this(paramContext, paramMediaCodecSelector, paramLong, null, false, paramHandler, paramVideoRendererEventListener, paramInt);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, int paramInt)
  {
    super(2, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean);
    this.allowedJoiningTimeMs = paramLong;
    this.maxDroppedFramesToNotify = paramInt;
    this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(paramContext);
    this.eventDispatcher = new VideoRendererEventListener.EventDispatcher(paramHandler, paramVideoRendererEventListener);
    this.deviceNeedsAutoFrcWorkaround = deviceNeedsAutoFrcWorkaround();
    this.joiningDeadlineMs = -9223372036854775807L;
    this.currentWidth = -1;
    this.currentHeight = -1;
    this.currentPixelWidthHeightRatio = -1.0F;
    this.pendingPixelWidthHeightRatio = -1.0F;
    this.scalingMode = 1;
    clearLastReportedVideoSize();
  }
  
  private static boolean areAdaptationCompatible(Format paramFormat1, Format paramFormat2)
  {
    return (paramFormat1.sampleMimeType.equals(paramFormat2.sampleMimeType)) && (getRotationDegrees(paramFormat1) == getRotationDegrees(paramFormat2));
  }
  
  private void clearLastReportedVideoSize()
  {
    this.lastReportedWidth = -1;
    this.lastReportedHeight = -1;
    this.lastReportedPixelWidthHeightRatio = -1.0F;
    this.lastReportedUnappliedRotationDegrees = -1;
  }
  
  private void clearRenderedFirstFrame()
  {
    this.renderedFirstFrame = false;
    if ((Util.SDK_INT >= 23) && (this.tunneling))
    {
      MediaCodec localMediaCodec = getCodec();
      if (localMediaCodec != null) {
        this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(localMediaCodec, null);
      }
    }
  }
  
  @TargetApi(21)
  private static void configureTunnelingV21(MediaFormat paramMediaFormat, int paramInt)
  {
    paramMediaFormat.setFeatureEnabled("tunneled-playback", true);
    paramMediaFormat.setInteger("audio-session-id", paramInt);
  }
  
  private static boolean deviceNeedsAutoFrcWorkaround()
  {
    return (Util.SDK_INT <= 22) && ("foster".equals(Util.DEVICE)) && ("NVIDIA".equals(Util.MANUFACTURER));
  }
  
  private void dropOutputBuffer(MediaCodec paramMediaCodec, int paramInt)
  {
    TraceUtil.beginSection("dropVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    paramMediaCodec = this.decoderCounters;
    paramMediaCodec.droppedOutputBufferCount += 1;
    this.droppedFrames += 1;
    this.consecutiveDroppedFrameCount += 1;
    this.decoderCounters.maxConsecutiveDroppedOutputBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedOutputBufferCount);
    if (this.droppedFrames == this.maxDroppedFramesToNotify) {
      maybeNotifyDroppedFrames();
    }
  }
  
  private static Point getCodecMaxSize(MediaCodecInfo paramMediaCodecInfo, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    int i;
    int j;
    label25:
    int k;
    label36:
    int m;
    int i2;
    int n;
    Object localObject;
    if (paramFormat.height > paramFormat.width)
    {
      i = 1;
      if (i == 0) {
        break label105;
      }
      j = paramFormat.height;
      if (i == 0) {
        break label114;
      }
      k = paramFormat.width;
      float f1 = k / j;
      int[] arrayOfInt = STANDARD_LONG_EDGE_VIDEO_PX;
      int i3 = arrayOfInt.length;
      m = 0;
      if (m >= i3) {
        break label281;
      }
      i2 = arrayOfInt[m];
      n = (int)(i2 * f1);
      if ((i2 > j) && (n > k)) {
        break label123;
      }
      localObject = null;
    }
    label105:
    label114:
    label123:
    int i1;
    label140:
    label145:
    Point localPoint;
    float f2;
    do
    {
      return (Point)localObject;
      i = 0;
      break;
      j = paramFormat.width;
      break label25;
      k = paramFormat.height;
      break label36;
      if (Util.SDK_INT < 21) {
        break label206;
      }
      if (i == 0) {
        break label192;
      }
      i1 = n;
      if (i == 0) {
        break label199;
      }
      localPoint = paramMediaCodecInfo.alignVideoSizeV21(i1, i2);
      f2 = paramFormat.frameRate;
      localObject = localPoint;
    } while (paramMediaCodecInfo.isVideoSizeAndRateSupportedV21(localPoint.x, localPoint.y, f2));
    label192:
    label199:
    label206:
    do
    {
      m += 1;
      break;
      i1 = i2;
      break label140;
      i2 = n;
      break label145;
      i1 = Util.ceilDivide(i2, 16) * 16;
      n = Util.ceilDivide(n, 16) * 16;
    } while (i1 * n > MediaCodecUtil.maxH264DecodableFrameSize());
    if (i != 0)
    {
      j = n;
      if (i == 0) {
        break label274;
      }
    }
    for (;;)
    {
      return new Point(j, i1);
      j = i1;
      break;
      label274:
      i1 = n;
    }
    label281:
    return null;
  }
  
  private static CodecMaxValues getCodecMaxValues(MediaCodecInfo paramMediaCodecInfo, Format paramFormat, Format[] paramArrayOfFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    int k = paramFormat.width;
    int j = paramFormat.height;
    int i = getMaxInputSize(paramFormat);
    if (paramArrayOfFormat.length == 1) {
      return new CodecMaxValues(k, j, i);
    }
    int n = 0;
    int i5 = paramArrayOfFormat.length;
    int m = 0;
    if (m < i5)
    {
      Format localFormat = paramArrayOfFormat[m];
      int i4 = n;
      int i3 = j;
      i2 = i;
      i1 = k;
      if (areAdaptationCompatible(paramFormat, localFormat)) {
        if ((localFormat.width != -1) && (localFormat.height != -1)) {
          break label170;
        }
      }
      label170:
      for (i1 = 1;; i1 = 0)
      {
        i4 = n | i1;
        i1 = Math.max(k, localFormat.width);
        i3 = Math.max(j, localFormat.height);
        i2 = Math.max(i, getMaxInputSize(localFormat));
        m += 1;
        n = i4;
        j = i3;
        i = i2;
        k = i1;
        break;
      }
    }
    int i2 = j;
    int i1 = i;
    m = k;
    if (n != 0)
    {
      Log.w("MediaCodecVideoRenderer", "Resolutions unknown. Codec max resolution: " + k + "x" + j);
      paramMediaCodecInfo = getCodecMaxSize(paramMediaCodecInfo, paramFormat);
      i2 = j;
      i1 = i;
      m = k;
      if (paramMediaCodecInfo != null)
      {
        m = Math.max(k, paramMediaCodecInfo.x);
        i2 = Math.max(j, paramMediaCodecInfo.y);
        i1 = Math.max(i, getMaxInputSize(paramFormat.sampleMimeType, m, i2));
        Log.w("MediaCodecVideoRenderer", "Codec max resolution adjusted to: " + m + "x" + i2);
      }
    }
    return new CodecMaxValues(m, i2, i1);
  }
  
  private static int getMaxInputSize(Format paramFormat)
  {
    if (paramFormat.maxInputSize != -1) {
      return paramFormat.maxInputSize;
    }
    return getMaxInputSize(paramFormat.sampleMimeType, paramFormat.width, paramFormat.height);
  }
  
  private static int getMaxInputSize(String paramString, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == -1) || (paramInt2 == -1)) {
      return -1;
    }
    label76:
    int i;
    switch (paramString.hashCode())
    {
    default: 
      i = -1;
      switch (i)
      {
      default: 
        return -1;
      case 0: 
      case 1: 
        label78:
        paramInt1 *= paramInt2;
        paramInt2 = 2;
      }
      break;
    }
    for (;;)
    {
      return paramInt1 * 3 / (paramInt2 * 2);
      if (!paramString.equals("video/3gpp")) {
        break label76;
      }
      i = 0;
      break label78;
      if (!paramString.equals("video/mp4v-es")) {
        break label76;
      }
      i = 1;
      break label78;
      if (!paramString.equals("video/avc")) {
        break label76;
      }
      i = 2;
      break label78;
      if (!paramString.equals("video/x-vnd.on2.vp8")) {
        break label76;
      }
      i = 3;
      break label78;
      if (!paramString.equals("video/hevc")) {
        break label76;
      }
      i = 4;
      break label78;
      if (!paramString.equals("video/x-vnd.on2.vp9")) {
        break label76;
      }
      i = 5;
      break label78;
      if ("BRAVIA 4K 2015".equals(Util.MODEL)) {
        break;
      }
      paramInt1 = Util.ceilDivide(paramInt1, 16) * Util.ceilDivide(paramInt2, 16) * 16 * 16;
      paramInt2 = 2;
      continue;
      paramInt1 *= paramInt2;
      paramInt2 = 2;
      continue;
      paramInt1 *= paramInt2;
      paramInt2 = 4;
    }
  }
  
  @SuppressLint({"InlinedApi"})
  private static MediaFormat getMediaFormat(Format paramFormat, CodecMaxValues paramCodecMaxValues, boolean paramBoolean, int paramInt)
  {
    paramFormat = paramFormat.getFrameworkMediaFormatV16();
    paramFormat.setInteger("max-width", paramCodecMaxValues.width);
    paramFormat.setInteger("max-height", paramCodecMaxValues.height);
    if (paramCodecMaxValues.inputSize != -1) {
      paramFormat.setInteger("max-input-size", paramCodecMaxValues.inputSize);
    }
    if (paramBoolean) {
      paramFormat.setInteger("auto-frc", 0);
    }
    if (paramInt != 0) {
      configureTunnelingV21(paramFormat, paramInt);
    }
    return paramFormat;
  }
  
  private static float getPixelWidthHeightRatio(Format paramFormat)
  {
    if (paramFormat.pixelWidthHeightRatio == -1.0F) {
      return 1.0F;
    }
    return paramFormat.pixelWidthHeightRatio;
  }
  
  private static int getRotationDegrees(Format paramFormat)
  {
    if (paramFormat.rotationDegrees == -1) {
      return 0;
    }
    return paramFormat.rotationDegrees;
  }
  
  private void maybeNotifyDroppedFrames()
  {
    if (this.droppedFrames > 0)
    {
      long l1 = SystemClock.elapsedRealtime();
      long l2 = this.droppedFrameAccumulationStartTimeMs;
      this.eventDispatcher.droppedFrames(this.droppedFrames, l1 - l2);
      this.droppedFrames = 0;
      this.droppedFrameAccumulationStartTimeMs = l1;
    }
  }
  
  private void maybeNotifyVideoSizeChanged()
  {
    if ((this.lastReportedWidth != this.currentWidth) || (this.lastReportedHeight != this.currentHeight) || (this.lastReportedUnappliedRotationDegrees != this.currentUnappliedRotationDegrees) || (this.lastReportedPixelWidthHeightRatio != this.currentPixelWidthHeightRatio))
    {
      this.eventDispatcher.videoSizeChanged(this.currentWidth, this.currentHeight, this.currentUnappliedRotationDegrees, this.currentPixelWidthHeightRatio);
      this.lastReportedWidth = this.currentWidth;
      this.lastReportedHeight = this.currentHeight;
      this.lastReportedUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
      this.lastReportedPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
    }
  }
  
  private void renderOutputBuffer(MediaCodec paramMediaCodec, int paramInt)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, true);
    TraceUtil.endSection();
    paramMediaCodec = this.decoderCounters;
    paramMediaCodec.renderedOutputBufferCount += 1;
    this.consecutiveDroppedFrameCount = 0;
    maybeNotifyRenderedFirstFrame();
  }
  
  @TargetApi(21)
  private void renderOutputBufferV21(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, paramLong);
    TraceUtil.endSection();
    paramMediaCodec = this.decoderCounters;
    paramMediaCodec.renderedOutputBufferCount += 1;
    this.consecutiveDroppedFrameCount = 0;
    maybeNotifyRenderedFirstFrame();
  }
  
  private void setSurface(Surface paramSurface)
    throws ExoPlaybackException
  {
    if (this.surface != paramSurface)
    {
      this.surface = paramSurface;
      int i = getState();
      if ((i == 1) || (i == 2))
      {
        releaseCodec();
        maybeInitCodec();
      }
    }
    clearRenderedFirstFrame();
    clearLastReportedVideoSize();
  }
  
  private static void setVideoScalingMode(MediaCodec paramMediaCodec, int paramInt)
  {
    paramMediaCodec.setVideoScalingMode(paramInt);
  }
  
  private void skipOutputBuffer(MediaCodec paramMediaCodec, int paramInt)
  {
    TraceUtil.beginSection("skipVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    paramMediaCodec = this.decoderCounters;
    paramMediaCodec.skippedOutputBufferCount += 1;
  }
  
  protected boolean canReconfigureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, Format paramFormat1, Format paramFormat2)
  {
    return (areAdaptationCompatible(paramFormat1, paramFormat2)) && (paramFormat2.width <= this.codecMaxValues.width) && (paramFormat2.height <= this.codecMaxValues.height) && (paramFormat2.maxInputSize <= this.codecMaxValues.inputSize) && ((paramBoolean) || ((paramFormat1.width == paramFormat2.width) && (paramFormat1.height == paramFormat2.height)));
  }
  
  protected void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto)
    throws MediaCodecUtil.DecoderQueryException
  {
    this.codecMaxValues = getCodecMaxValues(paramMediaCodecInfo, paramFormat, this.streamFormats);
    paramMediaCodec.configure(getMediaFormat(paramFormat, this.codecMaxValues, this.deviceNeedsAutoFrcWorkaround, this.tunnelingAudioSessionId), this.surface, paramMediaCrypto, 0);
    if ((Util.SDK_INT >= 23) && (this.tunneling)) {
      this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(paramMediaCodec, null);
    }
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (paramInt == 1) {
      setSurface((Surface)paramObject);
    }
    do
    {
      return;
      if (paramInt != 5) {
        break;
      }
      this.scalingMode = ((Integer)paramObject).intValue();
      paramObject = getCodec();
    } while (paramObject == null);
    setVideoScalingMode((MediaCodec)paramObject, this.scalingMode);
    return;
    super.handleMessage(paramInt, paramObject);
  }
  
  public boolean isReady()
  {
    if (((this.renderedFirstFrame) || (super.shouldInitCodec())) && (super.isReady())) {
      this.joiningDeadlineMs = -9223372036854775807L;
    }
    do
    {
      return true;
      if (this.joiningDeadlineMs == -9223372036854775807L) {
        return false;
      }
    } while (SystemClock.elapsedRealtime() < this.joiningDeadlineMs);
    this.joiningDeadlineMs = -9223372036854775807L;
    return false;
  }
  
  void maybeNotifyRenderedFirstFrame()
  {
    if (!this.renderedFirstFrame)
    {
      this.renderedFirstFrame = true;
      this.eventDispatcher.renderedFirstFrame(this.surface);
    }
  }
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.decoderInitialized(paramString, paramLong1, paramLong2);
  }
  
  protected void onDisabled()
  {
    this.currentWidth = -1;
    this.currentHeight = -1;
    this.currentPixelWidthHeightRatio = -1.0F;
    this.pendingPixelWidthHeightRatio = -1.0F;
    clearLastReportedVideoSize();
    this.frameReleaseTimeHelper.disable();
    this.tunnelingOnFrameRenderedListener = null;
    try
    {
      super.onDisabled();
      return;
    }
    finally
    {
      this.decoderCounters.ensureUpdated();
      this.eventDispatcher.disabled(this.decoderCounters);
    }
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramBoolean);
    this.tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
    if (this.tunnelingAudioSessionId != 0) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.tunneling = paramBoolean;
      this.eventDispatcher.enabled(this.decoderCounters);
      this.frameReleaseTimeHelper.enable();
      return;
    }
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    super.onInputFormatChanged(paramFormat);
    this.eventDispatcher.inputFormatChanged(paramFormat);
    this.pendingPixelWidthHeightRatio = getPixelWidthHeightRatio(paramFormat);
    this.pendingRotationDegrees = getRotationDegrees(paramFormat);
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
  {
    int i;
    int j;
    if ((paramMediaFormat.containsKey("crop-right")) && (paramMediaFormat.containsKey("crop-left")) && (paramMediaFormat.containsKey("crop-bottom")) && (paramMediaFormat.containsKey("crop-top")))
    {
      i = 1;
      if (i == 0) {
        break label167;
      }
      j = paramMediaFormat.getInteger("crop-right") - paramMediaFormat.getInteger("crop-left") + 1;
      label59:
      this.currentWidth = j;
      if (i == 0) {
        break label179;
      }
      i = paramMediaFormat.getInteger("crop-bottom") - paramMediaFormat.getInteger("crop-top") + 1;
      label85:
      this.currentHeight = i;
      this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
      if (Util.SDK_INT < 21) {
        break label190;
      }
      if ((this.pendingRotationDegrees == 90) || (this.pendingRotationDegrees == 270))
      {
        i = this.currentWidth;
        this.currentWidth = this.currentHeight;
        this.currentHeight = i;
        this.currentPixelWidthHeightRatio = (1.0F / this.currentPixelWidthHeightRatio);
      }
    }
    for (;;)
    {
      setVideoScalingMode(paramMediaCodec, this.scalingMode);
      return;
      i = 0;
      break;
      label167:
      j = paramMediaFormat.getInteger("width");
      break label59;
      label179:
      i = paramMediaFormat.getInteger("height");
      break label85;
      label190:
      this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
    }
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onPositionReset(paramLong, paramBoolean);
    clearRenderedFirstFrame();
    this.consecutiveDroppedFrameCount = 0;
    if ((paramBoolean) && (this.allowedJoiningTimeMs > 0L)) {}
    for (paramLong = SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs;; paramLong = -9223372036854775807L)
    {
      this.joiningDeadlineMs = paramLong;
      return;
    }
  }
  
  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer)
  {
    if ((Util.SDK_INT < 23) && (this.tunneling)) {
      maybeNotifyRenderedFirstFrame();
    }
  }
  
  protected void onStarted()
  {
    super.onStarted();
    this.droppedFrames = 0;
    this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
  }
  
  protected void onStopped()
  {
    this.joiningDeadlineMs = -9223372036854775807L;
    maybeNotifyDroppedFrames();
    super.onStopped();
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat)
    throws ExoPlaybackException
  {
    this.streamFormats = paramArrayOfFormat;
    super.onStreamChanged(paramArrayOfFormat);
  }
  
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      skipOutputBuffer(paramMediaCodec, paramInt1);
      return true;
    }
    if (!this.renderedFirstFrame)
    {
      if (Util.SDK_INT >= 21) {
        renderOutputBufferV21(paramMediaCodec, paramInt1, System.nanoTime());
      }
      for (;;)
      {
        return true;
        renderOutputBuffer(paramMediaCodec, paramInt1);
      }
    }
    if (getState() != 2) {
      return false;
    }
    long l2 = SystemClock.elapsedRealtime();
    long l1 = System.nanoTime();
    paramLong1 = this.frameReleaseTimeHelper.adjustReleaseTime(paramLong3, l1 + 1000L * (paramLong3 - paramLong1 - (l2 * 1000L - paramLong2)));
    paramLong3 = (paramLong1 - l1) / 1000L;
    if (shouldDropOutputBuffer(paramLong3, paramLong2))
    {
      dropOutputBuffer(paramMediaCodec, paramInt1);
      return true;
    }
    if (Util.SDK_INT >= 21)
    {
      if (paramLong3 < 50000L)
      {
        renderOutputBufferV21(paramMediaCodec, paramInt1, paramLong1);
        return true;
      }
    }
    else if (paramLong3 < 30000L)
    {
      if (paramLong3 > 11000L) {}
      try
      {
        Thread.sleep((paramLong3 - 10000L) / 1000L);
        renderOutputBuffer(paramMediaCodec, paramInt1);
        return true;
      }
      catch (InterruptedException paramByteBuffer)
      {
        for (;;)
        {
          Thread.currentThread().interrupt();
        }
      }
    }
    return false;
  }
  
  protected boolean shouldDropOutputBuffer(long paramLong1, long paramLong2)
  {
    return paramLong1 < -30000L;
  }
  
  protected boolean shouldInitCodec()
  {
    return (super.shouldInitCodec()) && (this.surface != null) && (this.surface.isValid());
  }
  
  protected int supportsFormat(MediaCodecSelector paramMediaCodecSelector, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    String str = paramFormat.sampleMimeType;
    if (!MimeTypes.isVideo(str)) {
      return 0;
    }
    boolean bool2 = false;
    boolean bool1 = false;
    DrmInitData localDrmInitData = paramFormat.drmInitData;
    int i;
    if (localDrmInitData != null)
    {
      i = 0;
      for (;;)
      {
        bool2 = bool1;
        if (i >= localDrmInitData.schemeDataCount) {
          break;
        }
        bool1 |= localDrmInitData.get(i).requiresSecureDecryption;
        i += 1;
      }
    }
    paramMediaCodecSelector = paramMediaCodecSelector.getDecoderInfo(str, bool2);
    if (paramMediaCodecSelector == null) {
      return 1;
    }
    bool2 = paramMediaCodecSelector.isCodecSupported(paramFormat.codecs);
    bool1 = bool2;
    if (bool2)
    {
      bool1 = bool2;
      if (paramFormat.width > 0)
      {
        bool1 = bool2;
        if (paramFormat.height > 0)
        {
          if (Util.SDK_INT < 21) {
            break label191;
          }
          bool1 = paramMediaCodecSelector.isVideoSizeAndRateSupportedV21(paramFormat.width, paramFormat.height, paramFormat.frameRate);
        }
      }
    }
    label164:
    int j;
    if (paramMediaCodecSelector.adaptive)
    {
      i = 8;
      if (!paramMediaCodecSelector.tunneling) {
        break label296;
      }
      j = 16;
      label175:
      if (!bool1) {
        break label302;
      }
    }
    label191:
    label296:
    label302:
    for (int k = 3;; k = 2)
    {
      return i | j | k;
      if (paramFormat.width * paramFormat.height <= MediaCodecUtil.maxH264DecodableFrameSize()) {}
      for (bool2 = true;; bool2 = false)
      {
        bool1 = bool2;
        if (bool2) {
          break;
        }
        Log.d("MediaCodecVideoRenderer", "FalseCheck [legacyFrameSize, " + paramFormat.width + "x" + paramFormat.height + "] [" + Util.DEVICE_DEBUG_INFO + "]");
        bool1 = bool2;
        break;
      }
      i = 4;
      break label164;
      j = 0;
      break label175;
    }
  }
  
  private static final class CodecMaxValues
  {
    public final int height;
    public final int inputSize;
    public final int width;
    
    public CodecMaxValues(int paramInt1, int paramInt2, int paramInt3)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.inputSize = paramInt3;
    }
  }
  
  @TargetApi(23)
  private final class OnFrameRenderedListenerV23
    implements MediaCodec.OnFrameRenderedListener
  {
    private OnFrameRenderedListenerV23(MediaCodec paramMediaCodec)
    {
      paramMediaCodec.setOnFrameRenderedListener(this, new Handler());
    }
    
    public void onFrameRendered(MediaCodec paramMediaCodec, long paramLong1, long paramLong2)
    {
      if (this != MediaCodecVideoRenderer.this.tunnelingOnFrameRenderedListener) {
        return;
      }
      MediaCodecVideoRenderer.this.maybeNotifyRenderedFirstFrame();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/video/MediaCodecVideoRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */