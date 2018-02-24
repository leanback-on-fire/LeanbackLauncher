package com.google.android.exoplayer2;

import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ExoPlaybackException
  extends Exception
{
  public static final int TYPE_RENDERER = 1;
  public static final int TYPE_SOURCE = 0;
  public static final int TYPE_UNEXPECTED = 2;
  public final int rendererIndex;
  public final int type;
  
  private ExoPlaybackException(int paramInt1, String paramString, Throwable paramThrowable, int paramInt2)
  {
    super(paramString, paramThrowable);
    this.type = paramInt1;
    this.rendererIndex = paramInt2;
  }
  
  public static ExoPlaybackException createForRenderer(Exception paramException, int paramInt)
  {
    return new ExoPlaybackException(1, null, paramException, paramInt);
  }
  
  public static ExoPlaybackException createForSource(IOException paramIOException)
  {
    return new ExoPlaybackException(0, null, paramIOException, -1);
  }
  
  static ExoPlaybackException createForUnexpected(RuntimeException paramRuntimeException)
  {
    return new ExoPlaybackException(2, null, paramRuntimeException, -1);
  }
  
  public Exception getRendererException()
  {
    boolean bool = true;
    if (this.type == 1) {}
    for (;;)
    {
      Assertions.checkState(bool);
      return (Exception)getCause();
      bool = false;
    }
  }
  
  public IOException getSourceException()
  {
    if (this.type == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return (IOException)getCause();
    }
  }
  
  public RuntimeException getUnexpectedException()
  {
    if (this.type == 2) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return (RuntimeException)getCause();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Type {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ExoPlaybackException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */