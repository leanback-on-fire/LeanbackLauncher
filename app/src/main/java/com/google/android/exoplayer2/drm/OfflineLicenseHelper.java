package com.google.android.exoplayer2.drm;

import android.net.Uri;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.InitializationChunk;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public final class OfflineLicenseHelper<T extends ExoMediaCrypto>
{
  private final ConditionVariable conditionVariable;
  private final DefaultDrmSessionManager<T> drmSessionManager;
  private final HandlerThread handlerThread = new HandlerThread("OfflineLicenseHelper");
  
  public OfflineLicenseHelper(ExoMediaDrm<T> paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap)
  {
    this.handlerThread.start();
    this.conditionVariable = new ConditionVariable();
    DefaultDrmSessionManager.EventListener local1 = new DefaultDrmSessionManager.EventListener()
    {
      public void onDrmKeysLoaded()
      {
        OfflineLicenseHelper.this.conditionVariable.open();
      }
      
      public void onDrmKeysRemoved()
      {
        OfflineLicenseHelper.this.conditionVariable.open();
      }
      
      public void onDrmKeysRestored()
      {
        OfflineLicenseHelper.this.conditionVariable.open();
      }
      
      public void onDrmSessionManagerError(Exception paramAnonymousException)
      {
        OfflineLicenseHelper.this.conditionVariable.open();
      }
    };
    this.drmSessionManager = new DefaultDrmSessionManager(C.WIDEVINE_UUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap, new Handler(this.handlerThread.getLooper()), local1);
  }
  
  private void blockingKeyRequest(int paramInt, byte[] paramArrayOfByte, DrmInitData paramDrmInitData)
    throws DrmSession.DrmSessionException
  {
    paramArrayOfByte = openBlockingKeyRequest(paramInt, paramArrayOfByte, paramDrmInitData);
    paramDrmInitData = paramArrayOfByte.getError();
    if (paramDrmInitData != null) {
      throw paramDrmInitData;
    }
    this.drmSessionManager.releaseSession(paramArrayOfByte);
  }
  
  public static DashManifest downloadManifest(HttpDataSource paramHttpDataSource, String paramString)
    throws IOException
  {
    paramString = new DataSourceInputStream(paramHttpDataSource, new DataSpec(Uri.parse(paramString)));
    try
    {
      paramString.open();
      paramHttpDataSource = new DashManifestParser().parse(paramHttpDataSource.getUri(), paramString);
      return paramHttpDataSource;
    }
    finally
    {
      paramString.close();
    }
  }
  
  private static InitializationChunk loadInitializationChunk(DataSource paramDataSource, Representation paramRepresentation)
    throws IOException, InterruptedException
  {
    RangedUri localRangedUri = paramRepresentation.getInitializationUri();
    if (localRangedUri == null) {
      return null;
    }
    paramDataSource = new InitializationChunk(paramDataSource, new DataSpec(localRangedUri.resolveUri(paramRepresentation.baseUrl), localRangedUri.start, localRangedUri.length, paramRepresentation.getCacheKey()), paramRepresentation.format, 0, null, newWrappedExtractor(paramRepresentation.format));
    paramDataSource.load();
    return paramDataSource;
  }
  
  public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap)
    throws UnsupportedDrmException
  {
    return new OfflineLicenseHelper(FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), paramMediaDrmCallback, paramHashMap);
  }
  
  public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String paramString, HttpDataSource.Factory paramFactory)
    throws UnsupportedDrmException
  {
    return newWidevineInstance(new HttpMediaDrmCallback(paramString, paramFactory, null), null);
  }
  
  private static ChunkExtractorWrapper newWrappedExtractor(Format paramFormat)
  {
    Object localObject = paramFormat.containerMimeType;
    int i;
    if ((((String)localObject).startsWith("video/webm")) || (((String)localObject).startsWith("audio/webm")))
    {
      i = 1;
      if (i == 0) {
        break label54;
      }
    }
    label54:
    for (localObject = new MatroskaExtractor();; localObject = new FragmentedMp4Extractor())
    {
      return new ChunkExtractorWrapper((Extractor)localObject, paramFormat, false, false);
      i = 0;
      break;
    }
  }
  
  private DrmSession<T> openBlockingKeyRequest(int paramInt, byte[] paramArrayOfByte, DrmInitData paramDrmInitData)
  {
    this.drmSessionManager.setMode(paramInt, paramArrayOfByte);
    this.conditionVariable.close();
    paramArrayOfByte = this.drmSessionManager.acquireSession(this.handlerThread.getLooper(), paramDrmInitData);
    this.conditionVariable.block();
    return paramArrayOfByte;
  }
  
  public byte[] download(HttpDataSource paramHttpDataSource, DashManifest paramDashManifest)
    throws IOException, InterruptedException, DrmSession.DrmSessionException
  {
    if (paramDashManifest.getPeriodCount() < 1) {}
    do
    {
      do
      {
        do
        {
          int i;
          do
          {
            return null;
            paramDashManifest = paramDashManifest.getPeriod(0);
            int j = paramDashManifest.getAdaptationSetIndex(2);
            i = j;
            if (j != -1) {
              break;
            }
            i = paramDashManifest.getAdaptationSetIndex(1);
          } while (i == -1);
          paramDashManifest = (AdaptationSet)paramDashManifest.adaptationSets.get(i);
        } while (paramDashManifest.representations.isEmpty());
        Representation localRepresentation = (Representation)paramDashManifest.representations.get(0);
        paramDashManifest = localRepresentation.format.drmInitData;
        localObject = paramDashManifest;
        if (paramDashManifest != null) {
          break;
        }
        paramHttpDataSource = loadInitializationChunk(paramHttpDataSource, localRepresentation);
      } while (paramHttpDataSource == null);
      localObject = paramHttpDataSource.getSampleFormat();
      paramHttpDataSource = paramDashManifest;
      if (localObject != null) {
        paramHttpDataSource = ((Format)localObject).drmInitData;
      }
    } while (paramHttpDataSource == null);
    Object localObject = paramHttpDataSource;
    blockingKeyRequest(2, null, (DrmInitData)localObject);
    return this.drmSessionManager.getOfflineLicenseKeySetId();
  }
  
  public byte[] download(HttpDataSource paramHttpDataSource, String paramString)
    throws IOException, InterruptedException, DrmSession.DrmSessionException
  {
    return download(paramHttpDataSource, downloadManifest(paramHttpDataSource, paramString));
  }
  
  public Pair<Long, Long> getLicenseDurationRemainingSec(byte[] paramArrayOfByte)
    throws DrmSession.DrmSessionException
  {
    Assertions.checkNotNull(paramArrayOfByte);
    paramArrayOfByte = openBlockingKeyRequest(1, paramArrayOfByte, null);
    Pair localPair = WidevineUtil.getLicenseDurationRemainingSec(this.drmSessionManager);
    this.drmSessionManager.releaseSession(paramArrayOfByte);
    return localPair;
  }
  
  public void release(byte[] paramArrayOfByte)
    throws DrmSession.DrmSessionException
  {
    Assertions.checkNotNull(paramArrayOfByte);
    blockingKeyRequest(3, paramArrayOfByte, null);
  }
  
  public void releaseResources()
  {
    this.handlerThread.quit();
  }
  
  public byte[] renew(byte[] paramArrayOfByte)
    throws DrmSession.DrmSessionException
  {
    Assertions.checkNotNull(paramArrayOfByte);
    blockingKeyRequest(2, paramArrayOfByte, null);
    return this.drmSessionManager.getOfflineLicenseKeySetId();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/OfflineLicenseHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */