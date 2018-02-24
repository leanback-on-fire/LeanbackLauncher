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
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;

public class ExoPlayerImpl implements MediaPlayer {
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
    private VideoCallback mVideoCallback;
    private Uri mVideoUri;

    public ExoPlayerImpl(Context context, SimpleExoPlayer player, SimpleExoPlayerView playerView, Handler handler, DataSource.Factory cacheDataFactory) {
        this.mContext = context;
        this.mPlayer = player;
        this.mHandler = handler;
        this.mCacheDataFactory = cacheDataFactory;
        this.mPlayerView = playerView;
    }

    public int getPlaybackState() {
        return this.mPlayer.getPlaybackState();
    }

    public void setVideoUri(Uri uri) {
        this.mVideoUri = uri;
    }

    public Uri getVideoUri() {
        return this.mVideoUri;
    }

    public void prepare() {
        this.mPlayer.prepare(buildMediaSource(this.mVideoUri, null));
        this.mPlayerView.setPlayer(this.mPlayer);
        this.mPlayer.setVideoDebugListener(new VideoRendererEventListener() {
            public void onVideoEnabled(DecoderCounters counters) {
            }

            public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            }

            public void onVideoInputFormatChanged(Format format) {
            }

            public void onDroppedFrames(int count, long elapsedMs) {
            }

            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            }

            public void onRenderedFirstFrame(Surface surface) {
                if (ExoPlayerImpl.this.mVideoCallback != null) {
                    ExoPlayerImpl.this.mVideoCallback.onVideoAvailable();
                }
            }

            public void onVideoDisabled(DecoderCounters counters) {
            }
        });
        this.mPlayer.addListener(new EventListener() {
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            }

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            public void onLoadingChanged(boolean isLoading) {
            }

            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == 4 && ExoPlayerImpl.this.mVideoCallback != null) {
                    ExoPlayerImpl.this.mVideoCallback.onVideoEnded();
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            public void onPlayerError(ExoPlaybackException error) {
                if (ExoPlayerImpl.this.mVideoCallback != null) {
                    ExoPlayerImpl.this.mVideoCallback.onVideoError();
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }

            public void onPositionDiscontinuity() {
            }
        });
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.mPlayer.setPlayWhenReady(playWhenReady);
    }

    public void stop() {
        this.mPlayer.stop();
        this.mPlayer.setVideoDebugListener(null);
        this.mPlayerView.setPlayer(null);
    }

    public void seekTo(int positionMs) {
        this.mPlayer.seekTo((long) positionMs);
    }

    public void setDisplaySize(int width, int height) {
    }

    public int getCurrentPosition() {
        return (int) this.mPlayer.getCurrentPosition();
    }

    public void setVolume(float volume) {
        this.mPlayer.setVolume(volume);
    }

    public View getPlayerView() {
        return this.mPlayerView;
    }

    public void setVideoCallback(VideoCallback callback) {
        this.mVideoCallback = callback;
    }

    public SimpleExoPlayer getExoPlayerIntance() {
        return this.mPlayer;
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        String lastPathSegment;
        if (TextUtils.isEmpty(overrideExtension)) {
            lastPathSegment = uri.getLastPathSegment();
        } else {
            lastPathSegment = "." + overrideExtension;
        }
        int type = inferContentType(lastPathSegment);
        switch (type) {
            case 0:
                return new DashMediaSource(uri, buildDataSourceFactory(this.mContext, false), new DefaultDashChunkSource.Factory(this.mCacheDataFactory), this.mHandler, null);
            case 1:
                return new SsMediaSource(uri, buildDataSourceFactory(this.mContext, false), new DefaultSsChunkSource.Factory(this.mCacheDataFactory), this.mHandler, null);
            case 2:
                return new HlsMediaSource(uri, this.mCacheDataFactory, this.mHandler, null);
            case 3:
                return new ExtractorMediaSource(uri, this.mCacheDataFactory, new DefaultExtractorsFactory(), this.mHandler, null);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private static int inferContentType(String fileName) {
        if (fileName == null) {
            return 3;
        }
        if (fileName.endsWith(".mpd")) {
            return 0;
        }
        if (fileName.endsWith(".ism") || fileName.endsWith(".isml")) {
            return 1;
        }
        if (fileName.endsWith(".m3u8")) {
            return 2;
        }
        return 3;
    }

    private static DefaultDataSourceFactory buildDataSourceFactory(Context context, boolean useBandwidthMeter) {
        TransferListener bandwidthMeter = useBandwidthMeter ? BANDWIDTH_METER : null;
        return new DefaultDataSourceFactory(context, bandwidthMeter, new DefaultHttpDataSourceFactory("ExoPlayerImpl", bandwidthMeter));
    }
}
