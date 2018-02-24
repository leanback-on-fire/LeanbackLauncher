package com.google.android.tvlauncher.instantvideo.media.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource.Factory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource.Factory;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer.VideoCallback;

public class ExoPlayerImpl
  implements MediaPlayer
{
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private static final int TYPE_DASH = 0;
  private static final int TYPE_HLS = 2;
  private static final int TYPE_OTHER = 3;
  private static final int TYPE_SS = 1;
  private DataSource.Factory mCacheDataFactory;
  private Context mContext;
  private Handler mHandler;
  private SimpleExoPlayer mPlayer;
  private SimpleExoPlayerView mPlayerView;
  private MediaPlayer.VideoCallback mVideoCallback;
  private Uri mVideoUri;
  
  public ExoPlayerImpl(Context paramContext, SimpleExoPlayer paramSimpleExoPlayer, SimpleExoPlayerView paramSimpleExoPlayerView, Handler paramHandler, DataSource.Factory paramFactory)
  {
    this.mContext = paramContext;
    this.mPlayer = paramSimpleExoPlayer;
    this.mHandler = paramHandler;
    this.mCacheDataFactory = paramFactory;
    this.mPlayerView = paramSimpleExoPlayerView;
  }
  
  private static DefaultDataSourceFactory buildDataSourceFactory(Context paramContext, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (DefaultBandwidthMeter localDefaultBandwidthMeter = BANDWIDTH_METER;; localDefaultBandwidthMeter = null) {
      return new DefaultDataSourceFactory(paramContext, localDefaultBandwidthMeter, new DefaultHttpDataSourceFactory("ExoPlayerImpl", localDefaultBandwidthMeter));
    }
  }
  
  private MediaSource buildMediaSource(Uri paramUri, String paramString)
  {
    if (!TextUtils.isEmpty(paramString)) {}
    for (paramString = "." + paramString;; paramString = paramUri.getLastPathSegment())
    {
      int i = inferContentType(paramString);
      switch (i)
      {
      default: 
        throw new IllegalStateException("Unsupported type: " + i);
      }
    }
    return new SsMediaSource(paramUri, buildDataSourceFactory(this.mContext, false), new DefaultSsChunkSource.Factory(this.mCacheDataFactory), this.mHandler, null);
    return new DashMediaSource(paramUri, buildDataSourceFactory(this.mContext, false), new DefaultDashChunkSource.Factory(this.mCacheDataFactory), this.mHandler, null);
    return new HlsMediaSource(paramUri, this.mCacheDataFactory, this.mHandler, null);
    return new ExtractorMediaSource(paramUri, this.mCacheDataFactory, new DefaultExtractorsFactory(), this.mHandler, null);
  }
  
  private static int inferContentType(String paramString)
  {
    if (paramString == null) {}
    do
    {
      return 3;
      if (paramString.endsWith(".mpd")) {
        return 0;
      }
      if ((paramString.endsWith(".ism")) || (paramString.endsWith(".isml"))) {
        return 1;
      }
    } while (!paramString.endsWith(".m3u8"));
    return 2;
  }
  
  public int getCurrentPosition()
  {
    return (int)this.mPlayer.getCurrentPosition();
  }
  
  public SimpleExoPlayer getExoPlayerIntance()
  {
    return this.mPlayer;
  }
  
  public int getPlaybackState()
  {
    return this.mPlayer.getPlaybackState();
  }
  
  public View getPlayerView()
  {
    return this.mPlayerView;
  }
  
  public Uri getVideoUri()
  {
    return this.mVideoUri;
  }
  
  public void prepare()
  {
    MediaSource localMediaSource = buildMediaSource(this.mVideoUri, null);
    this.mPlayer.prepare(localMediaSource);
    this.mPlayerView.setPlayer(this.mPlayer);
    this.mPlayer.setVideoDebugListener(new VideoRendererEventListener()
    {
      public void onDroppedFrames(int paramAnonymousInt, long paramAnonymousLong) {}
      
      public void onRenderedFirstFrame(Surface paramAnonymousSurface)
      {
        if (ExoPlayerImpl.this.mVideoCallback != null) {
          ExoPlayerImpl.this.mVideoCallback.onVideoAvailable();
        }
      }
      
      public void onVideoDecoderInitialized(String paramAnonymousString, long paramAnonymousLong1, long paramAnonymousLong2) {}
      
      public void onVideoDisabled(DecoderCounters paramAnonymousDecoderCounters) {}
      
      public void onVideoEnabled(DecoderCounters paramAnonymousDecoderCounters) {}
      
      public void onVideoInputFormatChanged(Format paramAnonymousFormat) {}
      
      public void onVideoSizeChanged(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, float paramAnonymousFloat) {}
    });
    this.mPlayer.addListener(new ExoPlayer.EventListener()
    {
      public void onLoadingChanged(boolean paramAnonymousBoolean) {}
      
      public void onPlayerError(ExoPlaybackException paramAnonymousExoPlaybackException)
      {
        if (ExoPlayerImpl.this.mVideoCallback != null) {
          ExoPlayerImpl.this.mVideoCallback.onVideoError();
        }
      }
      
      public void onPlayerStateChanged(boolean paramAnonymousBoolean, int paramAnonymousInt)
      {
        if ((paramAnonymousInt == 4) && (ExoPlayerImpl.this.mVideoCallback != null)) {
          ExoPlayerImpl.this.mVideoCallback.onVideoEnded();
        }
      }
      
      public void onPositionDiscontinuity() {}
      
      public void onTimelineChanged(Timeline paramAnonymousTimeline, Object paramAnonymousObject) {}
      
      public void onTracksChanged(TrackGroupArray paramAnonymousTrackGroupArray, TrackSelectionArray paramAnonymousTrackSelectionArray) {}
    });
  }
  
  public void seekTo(int paramInt)
  {
    this.mPlayer.seekTo(paramInt);
  }
  
  public void setDisplaySize(int paramInt1, int paramInt2) {}
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    this.mPlayer.setPlayWhenReady(paramBoolean);
  }
  
  public void setVideoCallback(MediaPlayer.VideoCallback paramVideoCallback)
  {
    this.mVideoCallback = paramVideoCallback;
  }
  
  public void setVideoUri(Uri paramUri)
  {
    this.mVideoUri = paramUri;
  }
  
  public void setVolume(float paramFloat)
  {
    this.mPlayer.setVolume(paramFloat);
  }
  
  public void stop()
  {
    this.mPlayer.stop();
    this.mPlayer.setVideoDebugListener(null);
    this.mPlayerView.setPlayer(null);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/media/impl/ExoPlayerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */