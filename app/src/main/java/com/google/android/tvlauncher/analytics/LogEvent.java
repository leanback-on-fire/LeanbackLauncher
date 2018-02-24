package com.google.android.tvlauncher.analytics;

import android.os.Bundle;

public class LogEvent
{
  private String[] mExpectedParameters;
  private final String mName;
  private Bundle mParameters;
  private boolean mRestricted;
  private Bundle mRestrictedParameters;
  private long mTimeoutMillis;
  
  public LogEvent(String paramString)
  {
    this.mName = paramString;
  }
  
  private void ensureBundle()
  {
    if (this.mParameters == null) {
      this.mParameters = new Bundle();
    }
  }
  
  private void ensureRestrictedBundle()
  {
    if (this.mRestrictedParameters == null) {
      this.mRestrictedParameters = new Bundle();
    }
  }
  
  public LogEvent expectParameters(String... paramVarArgs)
  {
    this.mExpectedParameters = paramVarArgs;
    return this;
  }
  
  String[] getExpectedParameters()
  {
    return this.mExpectedParameters;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  long getParameterTimeout()
  {
    return this.mTimeoutMillis;
  }
  
  Bundle getParameters()
  {
    return this.mParameters;
  }
  
  Bundle getRestrictedParameters()
  {
    return this.mRestrictedParameters;
  }
  
  public boolean isRestricted()
  {
    return this.mRestricted;
  }
  
  public LogEvent putParameter(String paramString, int paramInt)
  {
    ensureBundle();
    this.mParameters.putInt(paramString, paramInt);
    return this;
  }
  
  public LogEvent putParameter(String paramString1, String paramString2)
  {
    ensureBundle();
    this.mParameters.putString(paramString1, paramString2);
    return this;
  }
  
  public LogEvent putParameter(String paramString, boolean paramBoolean)
  {
    ensureBundle();
    this.mParameters.putBoolean(paramString, paramBoolean);
    return this;
  }
  
  public LogEvent putRestrictedParameter(String paramString, int paramInt)
  {
    ensureRestrictedBundle();
    this.mRestrictedParameters.putInt(paramString, paramInt);
    return this;
  }
  
  public LogEvent putRestrictedParameter(String paramString1, String paramString2)
  {
    ensureRestrictedBundle();
    this.mRestrictedParameters.putString(paramString1, paramString2);
    return this;
  }
  
  public LogEvent setParameterTimeout(long paramLong)
  {
    this.mTimeoutMillis = paramLong;
    return this;
  }
  
  public LogEvent setRestricted(boolean paramBoolean)
  {
    this.mRestricted = paramBoolean;
    return this;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/LogEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */