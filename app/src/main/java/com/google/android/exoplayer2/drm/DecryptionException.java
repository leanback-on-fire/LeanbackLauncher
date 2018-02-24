package com.google.android.exoplayer2.drm;

public class DecryptionException
  extends Exception
{
  private final int errorCode;
  
  public DecryptionException(int paramInt, String paramString)
  {
    super(paramString);
    this.errorCode = paramInt;
  }
  
  public int getErrorCode()
  {
    return this.errorCode;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/DecryptionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */