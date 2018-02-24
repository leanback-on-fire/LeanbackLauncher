package com.google.android.exoplayer2;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

public final class C
{
  public static final int AUDIO_SESSION_ID_UNSET = 0;
  public static final int BUFFER_FLAG_DECODE_ONLY = Integer.MIN_VALUE;
  public static final int BUFFER_FLAG_ENCRYPTED = 1073741824;
  public static final int BUFFER_FLAG_END_OF_STREAM = 4;
  public static final int BUFFER_FLAG_KEY_FRAME = 1;
  public static final int CHANNEL_OUT_7POINT1_SURROUND;
  public static final int CRYPTO_MODE_AES_CBC = 2;
  public static final int CRYPTO_MODE_AES_CTR = 1;
  public static final int CRYPTO_MODE_UNENCRYPTED = 0;
  public static final int DATA_TYPE_CUSTOM_BASE = 10000;
  public static final int DATA_TYPE_DRM = 3;
  public static final int DATA_TYPE_MANIFEST = 4;
  public static final int DATA_TYPE_MEDIA = 1;
  public static final int DATA_TYPE_MEDIA_INITIALIZATION = 2;
  public static final int DATA_TYPE_TIME_SYNCHRONIZATION = 5;
  public static final int DATA_TYPE_UNKNOWN = 0;
  public static final int DEFAULT_AUDIO_BUFFER_SIZE = 3538944;
  public static final int DEFAULT_BUFFER_SEGMENT_SIZE = 65536;
  public static final int DEFAULT_METADATA_BUFFER_SIZE = 131072;
  public static final int DEFAULT_MUXED_BUFFER_SIZE = 16777216;
  public static final int DEFAULT_TEXT_BUFFER_SIZE = 131072;
  public static final int DEFAULT_VIDEO_BUFFER_SIZE = 13107200;
  public static final int ENCODING_AC3 = 5;
  public static final int ENCODING_DTS = 7;
  public static final int ENCODING_DTS_HD = 8;
  public static final int ENCODING_E_AC3 = 6;
  public static final int ENCODING_INVALID = 0;
  public static final int ENCODING_PCM_16BIT = 2;
  public static final int ENCODING_PCM_24BIT = Integer.MIN_VALUE;
  public static final int ENCODING_PCM_32BIT = 1073741824;
  public static final int ENCODING_PCM_8BIT = 3;
  public static final int INDEX_UNSET = -1;
  public static final int LENGTH_UNSET = -1;
  public static final long MICROS_PER_SECOND = 1000000L;
  public static final int MSG_CUSTOM_BASE = 10000;
  public static final int MSG_SET_PLAYBACK_PARAMS = 3;
  public static final int MSG_SET_SCALING_MODE = 5;
  public static final int MSG_SET_STREAM_TYPE = 4;
  public static final int MSG_SET_SURFACE = 1;
  public static final int MSG_SET_VOLUME = 2;
  public static final long NANOS_PER_SECOND = 1000000000L;
  public static final UUID PLAYREADY_UUID;
  public static final int POSITION_UNSET = -1;
  public static final int RESULT_BUFFER_READ = -4;
  public static final int RESULT_END_OF_INPUT = -1;
  public static final int RESULT_FORMAT_READ = -5;
  public static final int RESULT_MAX_LENGTH_EXCEEDED = -2;
  public static final int RESULT_NOTHING_READ = -3;
  public static final int SELECTION_FLAG_AUTOSELECT = 4;
  public static final int SELECTION_FLAG_DEFAULT = 1;
  public static final int SELECTION_FLAG_FORCED = 2;
  public static final int SELECTION_REASON_ADAPTIVE = 3;
  public static final int SELECTION_REASON_CUSTOM_BASE = 10000;
  public static final int SELECTION_REASON_INITIAL = 1;
  public static final int SELECTION_REASON_MANUAL = 2;
  public static final int SELECTION_REASON_TRICK_PLAY = 4;
  public static final int SELECTION_REASON_UNKNOWN = 0;
  public static final int STEREO_MODE_LEFT_RIGHT = 2;
  public static final int STEREO_MODE_MONO = 0;
  public static final int STEREO_MODE_TOP_BOTTOM = 1;
  public static final int STREAM_TYPE_ALARM = 4;
  public static final int STREAM_TYPE_DEFAULT = 3;
  public static final int STREAM_TYPE_MUSIC = 3;
  public static final int STREAM_TYPE_NOTIFICATION = 5;
  public static final int STREAM_TYPE_RING = 2;
  public static final int STREAM_TYPE_SYSTEM = 1;
  public static final int STREAM_TYPE_VOICE_CALL = 0;
  public static final long TIME_END_OF_SOURCE = Long.MIN_VALUE;
  public static final long TIME_UNSET = -9223372036854775807L;
  public static final int TRACK_TYPE_AUDIO = 1;
  public static final int TRACK_TYPE_CUSTOM_BASE = 10000;
  public static final int TRACK_TYPE_DEFAULT = 0;
  public static final int TRACK_TYPE_METADATA = 4;
  public static final int TRACK_TYPE_TEXT = 3;
  public static final int TRACK_TYPE_UNKNOWN = -1;
  public static final int TRACK_TYPE_VIDEO = 2;
  public static final int TYPE_DASH = 0;
  public static final int TYPE_HLS = 2;
  public static final int TYPE_OTHER = 3;
  public static final int TYPE_SS = 1;
  public static final String UTF8_NAME = "UTF-8";
  public static final UUID UUID_NIL;
  public static final int VIDEO_SCALING_MODE_DEFAULT = 1;
  public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
  public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;
  public static final UUID WIDEVINE_UUID;
  
  static
  {
    if (Util.SDK_INT < 23) {}
    for (int i = 1020;; i = 6396)
    {
      CHANNEL_OUT_7POINT1_SURROUND = i;
      UUID_NIL = new UUID(0L, 0L);
      WIDEVINE_UUID = new UUID(-1301668207276963122L, -6645017420763422227L);
      PLAYREADY_UUID = new UUID(-7348484286925749626L, -6083546864340672619L);
      return;
    }
  }
  
  @TargetApi(21)
  public static int generateAudioSessionIdV21(Context paramContext)
  {
    return ((AudioManager)paramContext.getSystemService("audio")).generateAudioSessionId();
  }
  
  public static long msToUs(long paramLong)
  {
    if (paramLong == -9223372036854775807L) {
      return -9223372036854775807L;
    }
    return 1000L * paramLong;
  }
  
  public static long usToMs(long paramLong)
  {
    if (paramLong == -9223372036854775807L) {
      return -9223372036854775807L;
    }
    return paramLong / 1000L;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface BufferFlags {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ContentType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface CryptoMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Encoding {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface PcmEncoding {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface SelectionFlags {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface StereoMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface StreamType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface VideoScalingMode {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/C.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */