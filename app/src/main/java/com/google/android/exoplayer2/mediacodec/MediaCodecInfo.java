package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

@TargetApi(16)
public final class MediaCodecInfo
{
  public static final String TAG = "MediaCodecInfo";
  public final boolean adaptive;
  private final MediaCodecInfo.CodecCapabilities capabilities;
  private final String mimeType;
  public final String name;
  public final boolean tunneling;
  
  private MediaCodecInfo(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    this.name = ((String)Assertions.checkNotNull(paramString1));
    this.mimeType = paramString2;
    this.capabilities = paramCodecCapabilities;
    if ((paramCodecCapabilities != null) && (isAdaptive(paramCodecCapabilities)))
    {
      bool1 = true;
      this.adaptive = bool1;
      if ((paramCodecCapabilities == null) || (!isTunneling(paramCodecCapabilities))) {
        break label76;
      }
    }
    label76:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.tunneling = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  @TargetApi(21)
  private static boolean areSizeAndRateSupported(MediaCodecInfo.VideoCapabilities paramVideoCapabilities, int paramInt1, int paramInt2, double paramDouble)
  {
    if ((paramDouble == -1.0D) || (paramDouble <= 0.0D)) {
      return paramVideoCapabilities.isSizeSupported(paramInt1, paramInt2);
    }
    return paramVideoCapabilities.areSizeAndRateSupported(paramInt1, paramInt2, paramDouble);
  }
  
  private static boolean isAdaptive(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return (Util.SDK_INT >= 19) && (isAdaptiveV19(paramCodecCapabilities));
  }
  
  @TargetApi(19)
  private static boolean isAdaptiveV19(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("adaptive-playback");
  }
  
  private static boolean isTunneling(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return (Util.SDK_INT >= 21) && (isTunnelingV21(paramCodecCapabilities));
  }
  
  @TargetApi(21)
  private static boolean isTunnelingV21(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("tunneled-playback");
  }
  
  private void logAssumedSupport(String paramString)
  {
    Log.d("MediaCodecInfo", "AssumedSupport [" + paramString + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
  }
  
  private void logNoSupport(String paramString)
  {
    Log.d("MediaCodecInfo", "NoSupport [" + paramString + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
  }
  
  public static MediaCodecInfo newInstance(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return new MediaCodecInfo(paramString1, paramString2, paramCodecCapabilities);
  }
  
  public static MediaCodecInfo newPassthroughInstance(String paramString)
  {
    return new MediaCodecInfo(paramString, null, null);
  }
  
  @TargetApi(21)
  public Point alignVideoSizeV21(int paramInt1, int paramInt2)
  {
    if (this.capabilities == null)
    {
      logNoSupport("align.caps");
      return null;
    }
    MediaCodecInfo.VideoCapabilities localVideoCapabilities = this.capabilities.getVideoCapabilities();
    if (localVideoCapabilities == null)
    {
      logNoSupport("align.vCaps");
      return null;
    }
    int i = localVideoCapabilities.getWidthAlignment();
    int j = localVideoCapabilities.getHeightAlignment();
    return new Point(Util.ceilDivide(paramInt1, i) * i, Util.ceilDivide(paramInt2, j) * j);
  }
  
  public MediaCodecInfo.CodecProfileLevel[] getProfileLevels()
  {
    if ((this.capabilities == null) || (this.capabilities.profileLevels == null)) {
      return new MediaCodecInfo.CodecProfileLevel[0];
    }
    return this.capabilities.profileLevels;
  }
  
  @TargetApi(21)
  public boolean isAudioChannelCountSupportedV21(int paramInt)
  {
    if (this.capabilities == null)
    {
      logNoSupport("channelCount.caps");
      return false;
    }
    MediaCodecInfo.AudioCapabilities localAudioCapabilities = this.capabilities.getAudioCapabilities();
    if (localAudioCapabilities == null)
    {
      logNoSupport("channelCount.aCaps");
      return false;
    }
    if (localAudioCapabilities.getMaxInputChannelCount() < paramInt)
    {
      logNoSupport("channelCount.support, " + paramInt);
      return false;
    }
    return true;
  }
  
  @TargetApi(21)
  public boolean isAudioSampleRateSupportedV21(int paramInt)
  {
    if (this.capabilities == null)
    {
      logNoSupport("sampleRate.caps");
      return false;
    }
    MediaCodecInfo.AudioCapabilities localAudioCapabilities = this.capabilities.getAudioCapabilities();
    if (localAudioCapabilities == null)
    {
      logNoSupport("sampleRate.aCaps");
      return false;
    }
    if (!localAudioCapabilities.isSampleRateSupported(paramInt))
    {
      logNoSupport("sampleRate.support, " + paramInt);
      return false;
    }
    return true;
  }
  
  public boolean isCodecSupported(String paramString)
  {
    if ((paramString == null) || (this.mimeType == null)) {
      return true;
    }
    String str = MimeTypes.getMediaMimeType(paramString);
    if (str == null) {
      return true;
    }
    if (!this.mimeType.equals(str))
    {
      logNoSupport("codec.mime " + paramString + ", " + str);
      return false;
    }
    Pair localPair = MediaCodecUtil.getCodecProfileAndLevel(paramString);
    if (localPair == null) {
      return true;
    }
    MediaCodecInfo.CodecProfileLevel[] arrayOfCodecProfileLevel = getProfileLevels();
    int j = arrayOfCodecProfileLevel.length;
    int i = 0;
    while (i < j)
    {
      MediaCodecInfo.CodecProfileLevel localCodecProfileLevel = arrayOfCodecProfileLevel[i];
      if ((localCodecProfileLevel.profile == ((Integer)localPair.first).intValue()) && (localCodecProfileLevel.level >= ((Integer)localPair.second).intValue())) {
        return true;
      }
      i += 1;
    }
    logNoSupport("codec.profileLevel, " + paramString + ", " + str);
    return false;
  }
  
  @TargetApi(21)
  public boolean isVideoSizeAndRateSupportedV21(int paramInt1, int paramInt2, double paramDouble)
  {
    if (this.capabilities == null)
    {
      logNoSupport("sizeAndRate.caps");
      return false;
    }
    MediaCodecInfo.VideoCapabilities localVideoCapabilities = this.capabilities.getVideoCapabilities();
    if (localVideoCapabilities == null)
    {
      logNoSupport("sizeAndRate.vCaps");
      return false;
    }
    if (!areSizeAndRateSupported(localVideoCapabilities, paramInt1, paramInt2, paramDouble))
    {
      if ((paramInt1 >= paramInt2) || (!areSizeAndRateSupported(localVideoCapabilities, paramInt2, paramInt1, paramDouble)))
      {
        logNoSupport("sizeAndRate.support, " + paramInt1 + "x" + paramInt2 + "x" + paramDouble);
        return false;
      }
      logAssumedSupport("sizeAndRate.rotated, " + paramInt1 + "x" + paramInt2 + "x" + paramDouble);
    }
    return true;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/mediacodec/MediaCodecInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */