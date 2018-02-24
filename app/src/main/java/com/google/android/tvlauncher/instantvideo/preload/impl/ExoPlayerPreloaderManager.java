package com.google.android.tvlauncher.instantvideo.preload.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.SurfaceView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.impl.ExoPlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.Preloader;
import com.google.android.tvlauncher.instantvideo.preload.Preloader.OnPreloadFinishedListener;
import com.google.android.tvlauncher.instantvideo.preload.PreloaderManager;

import java.io.IOException;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ExoPlayerPreloaderManager extends PreloaderManager {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final byte[] BUFFER = new byte[1000];
    private static final boolean DEBUG = false;
    private static final int MSG_START_PRELOAD = 100;
    private static final int PLAYER_VIEW_POOL_SIZE = 2;
    private static final long PRELOAD_TIMEOUT_MS = 10000;
    private static final String TAG = "ExoPlayerPreloader";
    private CacheDataSourceFactory mCacheDataSourceFactory;
    private final long mCacheSizePerVideo;
    private Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private HandlerThread mHandlerThread;
    private final Deque<SimpleExoPlayerView> mPlayerViewPool = new LinkedList();
    private Set<Uri> mPreloadedVideo = new HashSet();

    private class PreloadHandler extends Handler {
        private OnPreloadFinishedListener mOnPreloadFinishedListener;
        private volatile boolean mStopped;
        private Uri mVideoUri;

        PreloadHandler(Looper looper, Uri videoUri, OnPreloadFinishedListener onPreloadFinishedListener) {
            super(looper);
            this.mVideoUri = videoUri;
            this.mOnPreloadFinishedListener = onPreloadFinishedListener;
        }

        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                DataSource dataSource = ExoPlayerPreloaderManager.this.mCacheDataSourceFactory.createDataSource();
                try {
                    dataSource.open(new DataSpec(this.mVideoUri));
                    long readTimeoutStartTimeMs = 0;
                    int totalReadSize = 0;
                    int i = 0;
                    while (((long) totalReadSize) < ExoPlayerPreloaderManager.this.mCacheSizePerVideo && !this.mStopped) {
                        try {
                            int readSize = dataSource.read(ExoPlayerPreloaderManager.BUFFER, 0, ExoPlayerPreloaderManager.BUFFER.length);
                            if (readSize >= 0) {
                                if (readSize != 0) {
                                    readTimeoutStartTimeMs = 0;
                                } else if (readTimeoutStartTimeMs == 0) {
                                    readTimeoutStartTimeMs = System.currentTimeMillis();
                                } else if (System.currentTimeMillis() - readTimeoutStartTimeMs > ExoPlayerPreloaderManager.PRELOAD_TIMEOUT_MS) {
                                    Log.w(ExoPlayerPreloaderManager.TAG, "Timeout during preloading the video uri (" + this.mVideoUri + ")");
                                    break;
                                }
                                totalReadSize += readSize;
                                i++;
                            }
                        } catch (IOException e) {
                            Log.w(ExoPlayerPreloaderManager.TAG, "Failed to open the video uri (" + this.mVideoUri + ")");
                        }
                    }
                    try {
                        dataSource.close();
                    } catch (IOException e2) {
                    }
                    notifyPreloadFinished(totalReadSize);
                } catch (IOException e3) {
                    notifyPreloadFinished(0);
                }
            }
        }

        private void notifyPreloadFinished(int preloadedSize) {
            if (this.mOnPreloadFinishedListener != null) {
                ExoPlayerPreloaderManager.this.mHandler.post(new Runnable() {
                    public void run() {
                        PreloadHandler.this.mOnPreloadFinishedListener.onPreloadFinishedListener();
                    }
                });
            }
        }
    }

    public ExoPlayerPreloaderManager(Context context, long diskCacheSizeInMb, long cacheSizePerVideo) {
        this.mContext = context.getApplicationContext();
        this.mCacheDataSourceFactory = new CacheDataSourceFactory(new SimpleCache(this.mContext.getCacheDir(), new LeastRecentlyUsedCacheEvictor((diskCacheSizeInMb * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)), new DefaultDataSourceFactory(context, BANDWIDTH_METER, new DefaultHttpDataSourceFactory("ExoPlayerPreloaderManager", BANDWIDTH_METER)), 0);
        this.mHandlerThread = new HandlerThread("ExoPlayerPreloaderManager");
        this.mHandlerThread.start();
        this.mCacheSizePerVideo = cacheSizePerVideo;
    }

    public boolean isPreloaded(Uri videoUri) {
        return this.mPreloadedVideo.contains(videoUri);
    }

    public Preloader createPreloader(final Uri videoUri) {
        return new Preloader() {
            PreloadHandler mPreloadHandler;

            public void startPreload(OnPreloadFinishedListener listener) {
                if (this.mPreloadHandler == null) {
                    this.mPreloadHandler = new PreloadHandler(ExoPlayerPreloaderManager.this.mHandlerThread.getLooper(), videoUri, listener);
                    this.mPreloadHandler.sendEmptyMessage(100);
                }
            }

            public void stopPreload() {
                if (this.mPreloadHandler != null) {
                    this.mPreloadHandler.mStopped = true;
                    this.mPreloadHandler = null;
                }
            }
        };
    }

    public void clearPreloadedData(Uri videoUri) {
        this.mPreloadedVideo.remove(videoUri);
    }

    public void bringPreloadedVideoToTopPriority(Uri videoUri) {
    }

    public MediaPlayer getOrCreatePlayer(Uri videoUri) {
        SimpleExoPlayerView playerView = (SimpleExoPlayerView) this.mPlayerViewPool.pollFirst();
        if (playerView == null) {
            playerView = new SimpleExoPlayerView(this.mContext);
            ((SurfaceView) playerView.getVideoSurfaceView()).getHolder().setFormat(-2);
            playerView.setUseController(false);
            playerView.setResizeMode(3);
        }
        ExoPlayerImpl player = new ExoPlayerImpl(this.mContext, ExoPlayerFactory.newSimpleInstance(this.mContext, new DefaultTrackSelector(new TrackSelection.Factory() {
            @Override
            public TrackSelection createTrackSelection(TrackGroup group, int... tracks) {
                // use? BANDWIDTH_METER
                return null; // todo
            }
        }), new DefaultLoadControl()), playerView, this.mHandler, this.mCacheDataSourceFactory);
        player.setVideoUri(videoUri);
        return player;
    }

    public void recycleMediaPlayer(MediaPlayer mediaPlayer) {
        ExoPlayerImpl player = (ExoPlayerImpl) mediaPlayer;
        player.getExoPlayerIntance().release();
        if (this.mPlayerViewPool.size() < 2) {
            this.mPlayerViewPool.addFirst((SimpleExoPlayerView) player.getPlayerView());
        }
    }

    public int canPlayVideo(Uri videoUri) {
        if (videoUri == null) {
            return 0;
        }
        if (videoUri.toString().endsWith(".mp4")) {
            return 100;
        }
        return 10;
    }
}
