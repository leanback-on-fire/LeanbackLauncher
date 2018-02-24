package com.google.android.exoplayer2.source.hls.playlist;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class HlsPlaylist
{
  public static final int TYPE_MASTER = 0;
  public static final int TYPE_MEDIA = 1;
  public final String baseUri;
  public final int type;
  
  protected HlsPlaylist(String paramString, int paramInt)
  {
    this.baseUri = paramString;
    this.type = paramInt;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Type {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/playlist/HlsPlaylist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */