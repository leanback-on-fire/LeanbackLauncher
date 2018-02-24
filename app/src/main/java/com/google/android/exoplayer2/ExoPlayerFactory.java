package com.google.android.exoplayer2;

import android.content.Context;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.trackselection.TrackSelector;

public final class ExoPlayerFactory
{
  public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000L;
  
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector)
  {
    return newInstance(paramArrayOfRenderer, paramTrackSelector, new DefaultLoadControl());
  }
  
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return new ExoPlayerImpl(paramArrayOfRenderer, paramTrackSelector, paramLoadControl);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return newSimpleInstance(paramContext, paramTrackSelector, paramLoadControl, null);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager)
  {
    return newSimpleInstance(paramContext, paramTrackSelector, paramLoadControl, paramDrmSessionManager, 0);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt)
  {
    return newSimpleInstance(paramContext, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramInt, 5000L);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, long paramLong)
  {
    return new SimpleExoPlayer(paramContext, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramInt, paramLong);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ExoPlayerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */