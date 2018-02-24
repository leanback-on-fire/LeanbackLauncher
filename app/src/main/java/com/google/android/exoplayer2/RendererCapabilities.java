package com.google.android.exoplayer2;

public abstract interface RendererCapabilities
{
  public static final int ADAPTIVE_NOT_SEAMLESS = 4;
  public static final int ADAPTIVE_NOT_SUPPORTED = 0;
  public static final int ADAPTIVE_SEAMLESS = 8;
  public static final int ADAPTIVE_SUPPORT_MASK = 12;
  public static final int FORMAT_EXCEEDS_CAPABILITIES = 2;
  public static final int FORMAT_HANDLED = 3;
  public static final int FORMAT_SUPPORT_MASK = 3;
  public static final int FORMAT_UNSUPPORTED_SUBTYPE = 1;
  public static final int FORMAT_UNSUPPORTED_TYPE = 0;
  public static final int TUNNELING_NOT_SUPPORTED = 0;
  public static final int TUNNELING_SUPPORTED = 16;
  public static final int TUNNELING_SUPPORT_MASK = 16;
  
  public abstract int getTrackType();
  
  public abstract int supportsFormat(Format paramFormat)
    throws ExoPlaybackException;
  
  public abstract int supportsMixedMimeTypeAdaptation()
    throws ExoPlaybackException;
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/RendererCapabilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */