package com.google.android.exoplayer2.drm;

import android.annotation.TargetApi;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

@TargetApi(18)
public final class HttpMediaDrmCallback
  implements MediaDrmCallback
{
  private static final Map<String, String> PLAYREADY_KEY_REQUEST_PROPERTIES = new HashMap();
  private final HttpDataSource.Factory dataSourceFactory;
  private final String defaultUrl;
  private final Map<String, String> keyRequestProperties;
  
  static
  {
    PLAYREADY_KEY_REQUEST_PROPERTIES.put("Content-Type", "text/xml");
    PLAYREADY_KEY_REQUEST_PROPERTIES.put("SOAPAction", "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
  }
  
  public HttpMediaDrmCallback(String paramString, HttpDataSource.Factory paramFactory)
  {
    this(paramString, paramFactory, null);
  }
  
  public HttpMediaDrmCallback(String paramString, HttpDataSource.Factory paramFactory, Map<String, String> paramMap)
  {
    this.dataSourceFactory = paramFactory;
    this.defaultUrl = paramString;
    this.keyRequestProperties = paramMap;
  }
  
  private byte[] executePost(String paramString, byte[] paramArrayOfByte, Map<String, String> paramMap)
    throws IOException
  {
    HttpDataSource localHttpDataSource = this.dataSourceFactory.createDataSource();
    if (paramMap != null)
    {
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        localHttpDataSource.setRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
      }
    }
    paramString = new DataSourceInputStream(localHttpDataSource, new DataSpec(Uri.parse(paramString), paramArrayOfByte, 0L, 0L, -1L, null, 1));
    try
    {
      paramArrayOfByte = Util.toByteArray(paramString);
      return paramArrayOfByte;
    }
    finally
    {
      Util.closeQuietly(paramString);
    }
  }
  
  public byte[] executeKeyRequest(UUID paramUUID, ExoMediaDrm.KeyRequest paramKeyRequest)
    throws Exception
  {
    Object localObject2 = paramKeyRequest.getDefaultUrl();
    Object localObject1 = localObject2;
    if (TextUtils.isEmpty((CharSequence)localObject2)) {
      localObject1 = this.defaultUrl;
    }
    localObject2 = new HashMap();
    ((Map)localObject2).put("Content-Type", "application/octet-stream");
    if (C.PLAYREADY_UUID.equals(paramUUID)) {
      ((Map)localObject2).putAll(PLAYREADY_KEY_REQUEST_PROPERTIES);
    }
    if (this.keyRequestProperties != null) {
      ((Map)localObject2).putAll(this.keyRequestProperties);
    }
    return executePost((String)localObject1, paramKeyRequest.getData(), (Map)localObject2);
  }
  
  public byte[] executeProvisionRequest(UUID paramUUID, ExoMediaDrm.ProvisionRequest paramProvisionRequest)
    throws IOException
  {
    return executePost(paramProvisionRequest.getDefaultUrl() + "&signedRequest=" + new String(paramProvisionRequest.getData()), new byte[0], null);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/HttpMediaDrmCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */