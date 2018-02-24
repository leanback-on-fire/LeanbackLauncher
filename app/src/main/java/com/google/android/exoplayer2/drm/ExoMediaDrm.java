package com.google.android.exoplayer2.drm;

import android.media.DeniedByServerException;
import android.media.MediaCryptoException;
import android.media.NotProvisionedException;
import android.media.ResourceBusyException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract interface ExoMediaDrm<T extends ExoMediaCrypto>
{
  public abstract void closeSession(byte[] paramArrayOfByte);
  
  public abstract T createMediaCrypto(UUID paramUUID, byte[] paramArrayOfByte)
    throws MediaCryptoException;
  
  public abstract KeyRequest getKeyRequest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt, HashMap<String, String> paramHashMap)
    throws NotProvisionedException;
  
  public abstract byte[] getPropertyByteArray(String paramString);
  
  public abstract String getPropertyString(String paramString);
  
  public abstract ProvisionRequest getProvisionRequest();
  
  public abstract byte[] openSession()
    throws NotProvisionedException, ResourceBusyException;
  
  public abstract byte[] provideKeyResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NotProvisionedException, DeniedByServerException;
  
  public abstract void provideProvisionResponse(byte[] paramArrayOfByte)
    throws DeniedByServerException;
  
  public abstract Map<String, String> queryKeyStatus(byte[] paramArrayOfByte);
  
  public abstract void release();
  
  public abstract void restoreKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract void setOnEventListener(OnEventListener<? super T> paramOnEventListener);
  
  public abstract void setPropertyByteArray(String paramString, byte[] paramArrayOfByte);
  
  public abstract void setPropertyString(String paramString1, String paramString2);
  
  public static abstract interface KeyRequest
  {
    public abstract byte[] getData();
    
    public abstract String getDefaultUrl();
  }
  
  public static abstract interface OnEventListener<T extends ExoMediaCrypto>
  {
    public abstract void onEvent(ExoMediaDrm<? extends T> paramExoMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
  }
  
  public static abstract interface ProvisionRequest
  {
    public abstract byte[] getData();
    
    public abstract String getDefaultUrl();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/ExoMediaDrm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */