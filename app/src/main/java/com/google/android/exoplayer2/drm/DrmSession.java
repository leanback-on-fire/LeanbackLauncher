package com.google.android.exoplayer2.drm;

import android.annotation.TargetApi;
import java.util.Map;

@TargetApi(16)
public abstract interface DrmSession<T extends ExoMediaCrypto>
{
  public static final int STATE_CLOSED = 1;
  public static final int STATE_ERROR = 0;
  public static final int STATE_OPENED = 3;
  public static final int STATE_OPENED_WITH_KEYS = 4;
  public static final int STATE_OPENING = 2;
  
  public abstract DrmSessionException getError();
  
  public abstract T getMediaCrypto();
  
  public abstract byte[] getOfflineLicenseKeySetId();
  
  public abstract int getState();
  
  public abstract Map<String, String> queryKeyStatus();
  
  public abstract boolean requiresSecureDecoderComponent(String paramString);
  
  public static class DrmSessionException
    extends Exception
  {
    DrmSessionException(Exception paramException)
    {
      super();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/DrmSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */