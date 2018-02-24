package com.google.android.gsf;

import android.util.AndroidException;

public class GoogleLoginServiceNotFoundException
  extends AndroidException
{
  private int mErrorCode;
  
  public GoogleLoginServiceNotFoundException(int paramInt)
  {
    super(GoogleLoginServiceConstants.getErrorCodeMessage(paramInt));
    this.mErrorCode = paramInt;
  }
  
  int getErrorCode()
  {
    return this.mErrorCode;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/GoogleLoginServiceNotFoundException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */