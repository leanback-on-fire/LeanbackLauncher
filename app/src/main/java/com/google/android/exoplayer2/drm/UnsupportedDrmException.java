package com.google.android.exoplayer2.drm;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class UnsupportedDrmException
  extends Exception
{
  public static final int REASON_INSTANTIATION_ERROR = 2;
  public static final int REASON_UNSUPPORTED_SCHEME = 1;
  public final int reason;
  
  public UnsupportedDrmException(int paramInt)
  {
    this.reason = paramInt;
  }
  
  public UnsupportedDrmException(int paramInt, Exception paramException)
  {
    super(paramException);
    this.reason = paramInt;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Reason {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/UnsupportedDrmException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */