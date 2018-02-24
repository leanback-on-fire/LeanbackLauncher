package android.support.v4.media;

import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.SparseIntArray;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public class AudioAttributesCompat
{
  public static final int CONTENT_TYPE_MOVIE = 3;
  public static final int CONTENT_TYPE_MUSIC = 2;
  public static final int CONTENT_TYPE_SONIFICATION = 4;
  public static final int CONTENT_TYPE_SPEECH = 1;
  public static final int CONTENT_TYPE_UNKNOWN = 0;
  private static final int FLAG_ALL = 1023;
  private static final int FLAG_ALL_PUBLIC = 273;
  public static final int FLAG_AUDIBILITY_ENFORCED = 1;
  private static final int FLAG_BEACON = 8;
  private static final int FLAG_BYPASS_INTERRUPTION_POLICY = 64;
  private static final int FLAG_BYPASS_MUTE = 128;
  private static final int FLAG_DEEP_BUFFER = 512;
  public static final int FLAG_HW_AV_SYNC = 16;
  private static final int FLAG_HW_HOTWORD = 32;
  private static final int FLAG_LOW_LATENCY = 256;
  private static final int FLAG_SCO = 4;
  private static final int FLAG_SECURE = 2;
  private static final int[] SDK_USAGES = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16 };
  private static final int SUPPRESSIBLE_CALL = 2;
  private static final int SUPPRESSIBLE_NOTIFICATION = 1;
  private static final SparseIntArray SUPPRESSIBLE_USAGES = new SparseIntArray();
  private static final String TAG = "AudioAttributesCompat";
  public static final int USAGE_ALARM = 4;
  public static final int USAGE_ASSISTANCE_ACCESSIBILITY = 11;
  public static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12;
  public static final int USAGE_ASSISTANCE_SONIFICATION = 13;
  public static final int USAGE_ASSISTANT = 16;
  public static final int USAGE_GAME = 14;
  public static final int USAGE_MEDIA = 1;
  public static final int USAGE_NOTIFICATION = 5;
  public static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
  public static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
  public static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
  public static final int USAGE_NOTIFICATION_EVENT = 10;
  public static final int USAGE_NOTIFICATION_RINGTONE = 6;
  public static final int USAGE_UNKNOWN = 0;
  private static final int USAGE_VIRTUAL_SOURCE = 15;
  public static final int USAGE_VOICE_COMMUNICATION = 2;
  public static final int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3;
  private static boolean sForceLegacyBehavior;
  private AudioAttributesCompatApi21.Wrapper mAudioAttributesWrapper;
  int mContentType = 0;
  int mFlags = 0;
  Integer mLegacyStream;
  int mUsage = 0;
  
  static
  {
    SUPPRESSIBLE_USAGES.put(5, 1);
    SUPPRESSIBLE_USAGES.put(6, 2);
    SUPPRESSIBLE_USAGES.put(7, 2);
    SUPPRESSIBLE_USAGES.put(8, 1);
    SUPPRESSIBLE_USAGES.put(9, 1);
    SUPPRESSIBLE_USAGES.put(10, 1);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static void setForceLegacyBehavior(boolean paramBoolean)
  {
    sForceLegacyBehavior = paramBoolean;
  }
  
  static int toVolumeStreamType(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int i = 0;
    if ((paramInt1 & 0x1) == 1)
    {
      if (paramBoolean) {
        return 1;
      }
      return 7;
    }
    if ((paramInt1 & 0x4) == 4)
    {
      if (paramBoolean) {
        return 0;
      }
      return 6;
    }
    switch (paramInt2)
    {
    case 13: 
    case 15: 
    default: 
      if (paramBoolean) {
        throw new IllegalArgumentException("Unknown usage value " + paramInt2 + " in audio attributes");
      }
      break;
    case 1: 
    case 12: 
    case 14: 
    case 16: 
      return 3;
    case 2: 
      return 0;
    case 3: 
      if (paramBoolean) {}
      for (paramInt1 = i;; paramInt1 = 8) {
        return paramInt1;
      }
    case 4: 
      return 4;
    case 6: 
      return 2;
    case 5: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
      return 5;
    case 11: 
      return 10;
    case 0: 
      if (paramBoolean) {
        return Integer.MIN_VALUE;
      }
      return 3;
    }
    return 3;
  }
  
  static int toVolumeStreamType(boolean paramBoolean, AudioAttributesCompat paramAudioAttributesCompat)
  {
    return toVolumeStreamType(paramBoolean, paramAudioAttributesCompat.getFlags(), paramAudioAttributesCompat.getUsage());
  }
  
  private static int usageForStreamType(int paramInt)
  {
    int i = 2;
    switch (paramInt)
    {
    case 9: 
    default: 
      i = 0;
    case 0: 
    case 6: 
      return i;
    case 1: 
    case 7: 
      return 13;
    case 2: 
      return 6;
    case 3: 
      return 1;
    case 4: 
      return 4;
    case 5: 
      return 5;
    case 8: 
      return 3;
    }
    return 11;
  }
  
  static String usageToString(int paramInt)
  {
    switch (paramInt)
    {
    case 15: 
    default: 
      return new String("unknown usage " + paramInt);
    case 0: 
      return new String("USAGE_UNKNOWN");
    case 1: 
      return new String("USAGE_MEDIA");
    case 2: 
      return new String("USAGE_VOICE_COMMUNICATION");
    case 3: 
      return new String("USAGE_VOICE_COMMUNICATION_SIGNALLING");
    case 4: 
      return new String("USAGE_ALARM");
    case 5: 
      return new String("USAGE_NOTIFICATION");
    case 6: 
      return new String("USAGE_NOTIFICATION_RINGTONE");
    case 7: 
      return new String("USAGE_NOTIFICATION_COMMUNICATION_REQUEST");
    case 8: 
      return new String("USAGE_NOTIFICATION_COMMUNICATION_INSTANT");
    case 9: 
      return new String("USAGE_NOTIFICATION_COMMUNICATION_DELAYED");
    case 10: 
      return new String("USAGE_NOTIFICATION_EVENT");
    case 11: 
      return new String("USAGE_ASSISTANCE_ACCESSIBILITY");
    case 12: 
      return new String("USAGE_ASSISTANCE_NAVIGATION_GUIDANCE");
    case 13: 
      return new String("USAGE_ASSISTANCE_SONIFICATION");
    case 14: 
      return new String("USAGE_GAME");
    }
    return new String("USAGE_ASSISTANT");
  }
  
  @Nullable
  public static AudioAttributesCompat wrap(@NonNull Object paramObject)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (!sForceLegacyBehavior))
    {
      AudioAttributesCompat localAudioAttributesCompat = new AudioAttributesCompat();
      localAudioAttributesCompat.mAudioAttributesWrapper = AudioAttributesCompatApi21.Wrapper.wrap((AudioAttributes)paramObject);
      return localAudioAttributesCompat;
    }
    return null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (AudioAttributesCompat)paramObject;
      if ((Build.VERSION.SDK_INT >= 21) && (!sForceLegacyBehavior) && (this.mAudioAttributesWrapper != null)) {
        return this.mAudioAttributesWrapper.unwrap().equals(((AudioAttributesCompat)paramObject).unwrap());
      }
      if ((this.mContentType != ((AudioAttributesCompat)paramObject).getContentType()) || (this.mFlags != ((AudioAttributesCompat)paramObject).getFlags()) || (this.mUsage != ((AudioAttributesCompat)paramObject).getUsage())) {
        break;
      }
      if (this.mLegacyStream == null) {
        break label121;
      }
    } while (this.mLegacyStream.equals(((AudioAttributesCompat)paramObject).mLegacyStream));
    label121:
    while (((AudioAttributesCompat)paramObject).mLegacyStream != null) {
      return false;
    }
    return true;
  }
  
  public int getContentType()
  {
    if ((Build.VERSION.SDK_INT >= 21) && (!sForceLegacyBehavior) && (this.mAudioAttributesWrapper != null)) {
      return this.mAudioAttributesWrapper.unwrap().getContentType();
    }
    return this.mContentType;
  }
  
  public int getFlags()
  {
    if ((Build.VERSION.SDK_INT >= 21) && (!sForceLegacyBehavior) && (this.mAudioAttributesWrapper != null)) {
      return this.mAudioAttributesWrapper.unwrap().getFlags();
    }
    int j = this.mFlags;
    int k = getLegacyStreamType();
    int i;
    if (k == 6) {
      i = j | 0x4;
    }
    for (;;)
    {
      return i & 0x111;
      i = j;
      if (k == 7) {
        i = j | 0x1;
      }
    }
  }
  
  public int getLegacyStreamType()
  {
    if (this.mLegacyStream != null) {
      return this.mLegacyStream.intValue();
    }
    if ((Build.VERSION.SDK_INT >= 21) && (!sForceLegacyBehavior)) {
      return AudioAttributesCompatApi21.toLegacyStreamType(this.mAudioAttributesWrapper);
    }
    return toVolumeStreamType(false, this.mFlags, this.mUsage);
  }
  
  public int getUsage()
  {
    if ((Build.VERSION.SDK_INT >= 21) && (!sForceLegacyBehavior) && (this.mAudioAttributesWrapper != null)) {
      return this.mAudioAttributesWrapper.unwrap().getUsage();
    }
    return this.mUsage;
  }
  
  public int getVolumeControlStream()
  {
    if (this == null) {
      throw new IllegalArgumentException("Invalid null audio attributes");
    }
    if ((Build.VERSION.SDK_INT >= 26) && (!sForceLegacyBehavior) && (unwrap() != null)) {
      return ((AudioAttributes)unwrap()).getVolumeControlStream();
    }
    return toVolumeStreamType(true, this);
  }
  
  public int hashCode()
  {
    if ((Build.VERSION.SDK_INT >= 21) && (!sForceLegacyBehavior) && (this.mAudioAttributesWrapper != null)) {
      return this.mAudioAttributesWrapper.unwrap().hashCode();
    }
    return Arrays.hashCode(new Object[] { Integer.valueOf(this.mContentType), Integer.valueOf(this.mFlags), Integer.valueOf(this.mUsage), this.mLegacyStream });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("AudioAttributesCompat:");
    if (unwrap() != null) {
      localStringBuilder.append(" audioattributes=").append(unwrap());
    }
    for (;;)
    {
      return localStringBuilder.toString();
      if (this.mLegacyStream != null)
      {
        localStringBuilder.append(" stream=").append(this.mLegacyStream);
        localStringBuilder.append(" derived");
      }
      localStringBuilder.append(" usage=").append(usageToString()).append(" content=").append(this.mContentType).append(" flags=0x").append(Integer.toHexString(this.mFlags).toUpperCase());
    }
  }
  
  @Nullable
  public Object unwrap()
  {
    if (this.mAudioAttributesWrapper != null) {
      return this.mAudioAttributesWrapper.unwrap();
    }
    return null;
  }
  
  String usageToString()
  {
    return usageToString(this.mUsage);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface AttributeContentType {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface AttributeUsage {}
  
  private static abstract class AudioManagerHidden
  {
    public static final int STREAM_ACCESSIBILITY = 10;
    public static final int STREAM_BLUETOOTH_SCO = 6;
    public static final int STREAM_SYSTEM_ENFORCED = 7;
    public static final int STREAM_TTS = 9;
  }
  
  public static class Builder
  {
    private Object mAAObject;
    private int mContentType = 0;
    private int mFlags = 0;
    private Integer mLegacyStream;
    private int mUsage = 0;
    
    public Builder() {}
    
    public Builder(AudioAttributesCompat paramAudioAttributesCompat)
    {
      this.mUsage = paramAudioAttributesCompat.mUsage;
      this.mContentType = paramAudioAttributesCompat.mContentType;
      this.mFlags = paramAudioAttributesCompat.mFlags;
      this.mLegacyStream = paramAudioAttributesCompat.mLegacyStream;
      this.mAAObject = paramAudioAttributesCompat.unwrap();
    }
    
    public AudioAttributesCompat build()
    {
      if ((!AudioAttributesCompat.sForceLegacyBehavior) && (Build.VERSION.SDK_INT >= 21))
      {
        if (this.mAAObject != null) {
          return AudioAttributesCompat.wrap(this.mAAObject);
        }
        localObject = new AudioAttributes.Builder().setContentType(this.mContentType).setFlags(this.mFlags).setUsage(this.mUsage);
        if (this.mLegacyStream != null) {
          ((AudioAttributes.Builder)localObject).setLegacyStreamType(this.mLegacyStream.intValue());
        }
        return AudioAttributesCompat.wrap(((AudioAttributes.Builder)localObject).build());
      }
      Object localObject = new AudioAttributesCompat(null);
      ((AudioAttributesCompat)localObject).mContentType = this.mContentType;
      ((AudioAttributesCompat)localObject).mFlags = this.mFlags;
      ((AudioAttributesCompat)localObject).mUsage = this.mUsage;
      ((AudioAttributesCompat)localObject).mLegacyStream = this.mLegacyStream;
      AudioAttributesCompat.access$202((AudioAttributesCompat)localObject, null);
      return (AudioAttributesCompat)localObject;
    }
    
    public Builder setContentType(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        this.mUsage = 0;
        return this;
      }
      this.mContentType = paramInt;
      return this;
    }
    
    public Builder setFlags(int paramInt)
    {
      this.mFlags |= paramInt & 0x3FF;
      return this;
    }
    
    public Builder setLegacyStreamType(int paramInt)
    {
      if (paramInt == 10) {
        throw new IllegalArgumentException("STREAM_ACCESSIBILITY is not a legacy stream type that was used for audio playback");
      }
      this.mLegacyStream = Integer.valueOf(paramInt);
      this.mUsage = AudioAttributesCompat.usageForStreamType(paramInt);
      return this;
    }
    
    public Builder setUsage(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        this.mUsage = 0;
        return this;
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
        this.mUsage = paramInt;
        return this;
      }
      if ((!AudioAttributesCompat.sForceLegacyBehavior) && (Build.VERSION.SDK_INT > 25))
      {
        this.mUsage = paramInt;
        return this;
      }
      this.mUsage = 12;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/media/AudioAttributesCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */