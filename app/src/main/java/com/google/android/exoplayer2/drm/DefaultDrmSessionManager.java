package com.google.android.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@TargetApi(18)
public class DefaultDrmSessionManager<T extends ExoMediaCrypto>
  implements DrmSessionManager<T>, DrmSession<T>
{
  private static final int MAX_LICENSE_DURATION_TO_RENEW = 60;
  public static final int MODE_DOWNLOAD = 2;
  public static final int MODE_PLAYBACK = 0;
  public static final int MODE_QUERY = 1;
  public static final int MODE_RELEASE = 3;
  private static final int MSG_KEYS = 1;
  private static final int MSG_PROVISION = 0;
  public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
  private static final String TAG = "OfflineDrmSessionMngr";
  final MediaDrmCallback callback;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private DrmSession.DrmSessionException lastException;
  private T mediaCrypto;
  private final ExoMediaDrm<T> mediaDrm;
  DefaultDrmSessionManager<T>.MediaDrmHandler mediaDrmHandler;
  private int mode;
  private byte[] offlineLicenseKeySetId;
  private int openCount;
  private final HashMap<String, String> optionalKeyRequestParameters;
  private Looper playbackLooper;
  private Handler postRequestHandler;
  DefaultDrmSessionManager<T>.PostResponseHandler postResponseHandler;
  private boolean provisioningInProgress;
  private HandlerThread requestHandlerThread;
  private byte[] schemeInitData;
  private String schemeMimeType;
  private byte[] sessionId;
  private int state;
  final UUID uuid;
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm<T> paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
  {
    this.uuid = paramUUID;
    this.mediaDrm = paramExoMediaDrm;
    this.callback = paramMediaDrmCallback;
    this.optionalKeyRequestParameters = paramHashMap;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    paramExoMediaDrm.setOnEventListener(new MediaDrmEventListener(null));
    this.state = 1;
    this.mode = 0;
  }
  
  private void doLicense()
  {
    switch (this.mode)
    {
    }
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            if (this.offlineLicenseKeySetId == null)
            {
              postKeyRequest(this.sessionId, 1);
              return;
            }
          } while (!restoreKeys());
          long l = getLicenseDurationRemainingSec();
          if ((this.mode == 0) && (l <= 60L))
          {
            Log.d("OfflineDrmSessionMngr", "Offline license has expired or will expire soon. Remaining seconds: " + l);
            postKeyRequest(this.sessionId, 2);
            return;
          }
          if (l <= 0L)
          {
            onError(new KeysExpiredException());
            return;
          }
          this.state = 4;
        } while ((this.eventHandler == null) || (this.eventListener == null));
        this.eventHandler.post(new Runnable()
        {
          public void run()
          {
            DefaultDrmSessionManager.this.eventListener.onDrmKeysRestored();
          }
        });
        return;
        if (this.offlineLicenseKeySetId == null)
        {
          postKeyRequest(this.sessionId, 2);
          return;
        }
      } while (!restoreKeys());
      postKeyRequest(this.sessionId, 2);
      return;
    } while (!restoreKeys());
    postKeyRequest(this.offlineLicenseKeySetId, 3);
  }
  
  private long getLicenseDurationRemainingSec()
  {
    if (!C.WIDEVINE_UUID.equals(this.uuid)) {
      return Long.MAX_VALUE;
    }
    Pair localPair = WidevineUtil.getLicenseDurationRemainingSec(this);
    return Math.min(((Long)localPair.first).longValue(), ((Long)localPair.second).longValue());
  }
  
  public static DefaultDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID paramUUID, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return new DefaultDrmSessionManager(paramUUID, FrameworkMediaDrm.newInstance(paramUUID), paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener);
  }
  
  public static DefaultDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback paramMediaDrmCallback, String paramString, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    HashMap localHashMap;
    if (!TextUtils.isEmpty(paramString))
    {
      localHashMap = new HashMap();
      localHashMap.put("PRCustomData", paramString);
    }
    for (paramString = localHashMap;; paramString = null) {
      return newFrameworkInstance(C.PLAYREADY_UUID, paramMediaDrmCallback, paramString, paramHandler, paramEventListener);
    }
  }
  
  public static DefaultDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return newFrameworkInstance(C.WIDEVINE_UUID, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener);
  }
  
  private void onError(final Exception paramException)
  {
    this.lastException = new DrmSession.DrmSessionException(paramException);
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          DefaultDrmSessionManager.this.eventListener.onDrmSessionManagerError(paramException);
        }
      });
    }
    if (this.state != 4) {
      this.state = 0;
    }
  }
  
  private void onKeyResponse(Object paramObject)
  {
    if ((this.state != 3) && (this.state != 4)) {}
    do
    {
      for (;;)
      {
        return;
        if ((paramObject instanceof Exception))
        {
          onKeysError((Exception)paramObject);
          return;
        }
        try
        {
          if (this.mode == 3)
          {
            this.mediaDrm.provideKeyResponse(this.offlineLicenseKeySetId, (byte[])paramObject);
            if ((this.eventHandler == null) || (this.eventListener == null)) {
              continue;
            }
            this.eventHandler.post(new Runnable()
            {
              public void run()
              {
                DefaultDrmSessionManager.this.eventListener.onDrmKeysRemoved();
              }
            });
          }
        }
        catch (Exception paramObject)
        {
          onKeysError((Exception)paramObject);
          return;
        }
      }
      paramObject = this.mediaDrm.provideKeyResponse(this.sessionId, (byte[])paramObject);
      if (((this.mode == 2) || ((this.mode == 0) && (this.offlineLicenseKeySetId != null))) && (paramObject != null) && (paramObject.length != 0)) {
        this.offlineLicenseKeySetId = ((byte[])paramObject);
      }
      this.state = 4;
    } while ((this.eventHandler == null) || (this.eventListener == null));
    this.eventHandler.post(new Runnable()
    {
      public void run()
      {
        DefaultDrmSessionManager.this.eventListener.onDrmKeysLoaded();
      }
    });
  }
  
  private void onKeysError(Exception paramException)
  {
    if ((paramException instanceof NotProvisionedException))
    {
      postProvisionRequest();
      return;
    }
    onError(paramException);
  }
  
  private void onProvisionResponse(Object paramObject)
  {
    this.provisioningInProgress = false;
    if ((this.state != 2) && (this.state != 3) && (this.state != 4)) {
      return;
    }
    if ((paramObject instanceof Exception))
    {
      onError((Exception)paramObject);
      return;
    }
    try
    {
      this.mediaDrm.provideProvisionResponse((byte[])paramObject);
      if (this.state == 2)
      {
        openInternal(false);
        return;
      }
    }
    catch (DeniedByServerException paramObject)
    {
      onError((Exception)paramObject);
      return;
    }
    doLicense();
  }
  
  private void openInternal(boolean paramBoolean)
  {
    try
    {
      this.sessionId = this.mediaDrm.openSession();
      this.mediaCrypto = this.mediaDrm.createMediaCrypto(this.uuid, this.sessionId);
      this.state = 3;
      doLicense();
      return;
    }
    catch (NotProvisionedException localNotProvisionedException)
    {
      if (paramBoolean)
      {
        postProvisionRequest();
        return;
      }
      onError(localNotProvisionedException);
      return;
    }
    catch (Exception localException)
    {
      onError(localException);
    }
  }
  
  private void postKeyRequest(byte[] paramArrayOfByte, int paramInt)
  {
    try
    {
      paramArrayOfByte = this.mediaDrm.getKeyRequest(paramArrayOfByte, this.schemeInitData, this.schemeMimeType, paramInt, this.optionalKeyRequestParameters);
      this.postRequestHandler.obtainMessage(1, paramArrayOfByte).sendToTarget();
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      onKeysError(paramArrayOfByte);
    }
  }
  
  private void postProvisionRequest()
  {
    if (this.provisioningInProgress) {
      return;
    }
    this.provisioningInProgress = true;
    ExoMediaDrm.ProvisionRequest localProvisionRequest = this.mediaDrm.getProvisionRequest();
    this.postRequestHandler.obtainMessage(0, localProvisionRequest).sendToTarget();
  }
  
  private boolean restoreKeys()
  {
    try
    {
      this.mediaDrm.restoreKeys(this.sessionId, this.offlineLicenseKeySetId);
      return true;
    }
    catch (Exception localException)
    {
      Log.e("OfflineDrmSessionMngr", "Error trying to restore Widevine keys.", localException);
      onError(localException);
    }
    return false;
  }
  
  public DrmSession<T> acquireSession(Looper paramLooper, DrmInitData paramDrmInitData)
  {
    if ((this.playbackLooper == null) || (this.playbackLooper == paramLooper)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      int i = this.openCount + 1;
      this.openCount = i;
      if (i == 1) {
        break;
      }
      return this;
    }
    if (this.playbackLooper == null)
    {
      this.playbackLooper = paramLooper;
      this.mediaDrmHandler = new MediaDrmHandler(paramLooper);
      this.postResponseHandler = new PostResponseHandler(paramLooper);
    }
    this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
    this.requestHandlerThread.start();
    this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
    if (this.offlineLicenseKeySetId == null)
    {
      paramLooper = paramDrmInitData.get(this.uuid);
      if (paramLooper == null)
      {
        onError(new IllegalStateException("Media does not support uuid: " + this.uuid));
        return this;
      }
      this.schemeInitData = paramLooper.data;
      this.schemeMimeType = paramLooper.mimeType;
      if (Util.SDK_INT < 21)
      {
        paramLooper = PsshAtomUtil.parseSchemeSpecificData(this.schemeInitData, C.WIDEVINE_UUID);
        if (paramLooper != null) {
          break label233;
        }
      }
    }
    for (;;)
    {
      this.state = 2;
      openInternal(true);
      return this;
      label233:
      this.schemeInitData = paramLooper;
    }
  }
  
  public final DrmSession.DrmSessionException getError()
  {
    if (this.state == 0) {
      return this.lastException;
    }
    return null;
  }
  
  public final T getMediaCrypto()
  {
    if ((this.state != 3) && (this.state != 4)) {
      throw new IllegalStateException();
    }
    return this.mediaCrypto;
  }
  
  public byte[] getOfflineLicenseKeySetId()
  {
    return this.offlineLicenseKeySetId;
  }
  
  public final byte[] getPropertyByteArray(String paramString)
  {
    return this.mediaDrm.getPropertyByteArray(paramString);
  }
  
  public final String getPropertyString(String paramString)
  {
    return this.mediaDrm.getPropertyString(paramString);
  }
  
  public final int getState()
  {
    return this.state;
  }
  
  public Map<String, String> queryKeyStatus()
  {
    if (this.sessionId == null) {
      throw new IllegalStateException();
    }
    return this.mediaDrm.queryKeyStatus(this.sessionId);
  }
  
  public void releaseSession(DrmSession<T> paramDrmSession)
  {
    int i = this.openCount - 1;
    this.openCount = i;
    if (i != 0) {}
    do
    {
      return;
      this.state = 1;
      this.provisioningInProgress = false;
      this.mediaDrmHandler.removeCallbacksAndMessages(null);
      this.postResponseHandler.removeCallbacksAndMessages(null);
      this.postRequestHandler.removeCallbacksAndMessages(null);
      this.postRequestHandler = null;
      this.requestHandlerThread.quit();
      this.requestHandlerThread = null;
      this.schemeInitData = null;
      this.schemeMimeType = null;
      this.mediaCrypto = null;
      this.lastException = null;
    } while (this.sessionId == null);
    this.mediaDrm.closeSession(this.sessionId);
    this.sessionId = null;
  }
  
  public boolean requiresSecureDecoderComponent(String paramString)
  {
    if ((this.state != 3) && (this.state != 4)) {
      throw new IllegalStateException();
    }
    return this.mediaCrypto.requiresSecureDecoderComponent(paramString);
  }
  
  public void setMode(int paramInt, byte[] paramArrayOfByte)
  {
    if (this.openCount == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if ((paramInt == 1) || (paramInt == 3)) {
        Assertions.checkNotNull(paramArrayOfByte);
      }
      this.mode = paramInt;
      this.offlineLicenseKeySetId = paramArrayOfByte;
      return;
    }
  }
  
  public final void setPropertyByteArray(String paramString, byte[] paramArrayOfByte)
  {
    this.mediaDrm.setPropertyByteArray(paramString, paramArrayOfByte);
  }
  
  public final void setPropertyString(String paramString1, String paramString2)
  {
    this.mediaDrm.setPropertyString(paramString1, paramString2);
  }
  
  public static abstract interface EventListener
  {
    public abstract void onDrmKeysLoaded();
    
    public abstract void onDrmKeysRemoved();
    
    public abstract void onDrmKeysRestored();
    
    public abstract void onDrmSessionManagerError(Exception paramException);
  }
  
  private class MediaDrmEventListener
    implements ExoMediaDrm.OnEventListener<T>
  {
    private MediaDrmEventListener() {}
    
    public void onEvent(ExoMediaDrm<? extends T> paramExoMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
    {
      if (DefaultDrmSessionManager.this.mode == 0) {
        DefaultDrmSessionManager.this.mediaDrmHandler.sendEmptyMessage(paramInt1);
      }
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  private class MediaDrmHandler
    extends Handler
  {
    public MediaDrmHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if ((DefaultDrmSessionManager.this.openCount == 0) || ((DefaultDrmSessionManager.this.state != 3) && (DefaultDrmSessionManager.this.state != 4))) {}
      do
      {
        return;
        switch (paramMessage.what)
        {
        default: 
          return;
        case 1: 
          DefaultDrmSessionManager.access$302(DefaultDrmSessionManager.this, 3);
          DefaultDrmSessionManager.this.postProvisionRequest();
          return;
        case 2: 
          DefaultDrmSessionManager.this.doLicense();
          return;
        }
      } while (DefaultDrmSessionManager.this.state != 4);
      DefaultDrmSessionManager.access$302(DefaultDrmSessionManager.this, 3);
      DefaultDrmSessionManager.this.onError(new KeysExpiredException());
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Mode {}
  
  @SuppressLint({"HandlerLeak"})
  private class PostRequestHandler
    extends Handler
  {
    public PostRequestHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      for (;;)
      {
        try
        {
          switch (paramMessage.what)
          {
          case 0: 
            throw new RuntimeException();
          }
        }
        catch (Exception localException)
        {
          DefaultDrmSessionManager.this.postResponseHandler.obtainMessage(paramMessage.what, localException).sendToTarget();
          return;
        }
        byte[] arrayOfByte = DefaultDrmSessionManager.this.callback.executeProvisionRequest(DefaultDrmSessionManager.this.uuid, (ExoMediaDrm.ProvisionRequest)paramMessage.obj);
        continue;
        arrayOfByte = DefaultDrmSessionManager.this.callback.executeKeyRequest(DefaultDrmSessionManager.this.uuid, (ExoMediaDrm.KeyRequest)paramMessage.obj);
      }
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  private class PostResponseHandler
    extends Handler
  {
    public PostResponseHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 0: 
        DefaultDrmSessionManager.this.onProvisionResponse(paramMessage.obj);
        return;
      }
      DefaultDrmSessionManager.this.onKeyResponse(paramMessage.obj);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/DefaultDrmSessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */