package com.google.android.tvlauncher.instantvideo.preload.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.tv.TvContract;
import android.media.tv.TvView;
import android.net.Uri;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.impl.TvPlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.Preloader;
import com.google.android.tvlauncher.instantvideo.preload.PreloaderManager;
import java.util.Deque;
import java.util.LinkedList;

@TargetApi(24)
public class TvPlayerPreloaderManager extends PreloaderManager {
    private static final int TV_VIEW_POOL_SIZE = 2;
    private final Context mContext;
    private final Deque<TvView> mTvViewPool = new LinkedList();

    public TvPlayerPreloaderManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public boolean isPreloaded(Uri videoUri) {
        return false;
    }

    public Preloader createPreloader(Uri videoUri) {
        return null;
    }

    public void clearPreloadedData(Uri videoUri) {
    }

    public void bringPreloadedVideoToTopPriority(Uri videoUri) {
    }

    public MediaPlayer getOrCreatePlayer(Uri videoUri) {
        TvView tvView = (TvView) this.mTvViewPool.pollFirst();
        if (tvView == null) {
            tvView = new TvView(this.mContext);
        }
        TvPlayerImpl tvPlayer = new TvPlayerImpl(this.mContext, tvView);
        tvPlayer.setVideoUri(videoUri);
        return tvPlayer;
    }

    public void recycleMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer.getPlaybackState() != 1) {
            mediaPlayer.stop();
        }
        if (this.mTvViewPool.size() < 2) {
            this.mTvViewPool.addFirst((TvView) mediaPlayer.getPlayerView());
        }
    }

    public int canPlayVideo(Uri videoUri) {
        return (TvContract.isChannelUri(videoUri) || TvPlayerImpl.isRecordedProgramUri(videoUri) || TvPlayerImpl.isPreviewProgramUri(videoUri)) ? 100 : 0;
    }
}
