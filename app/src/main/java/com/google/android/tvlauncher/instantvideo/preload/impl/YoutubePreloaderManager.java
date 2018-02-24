package com.google.android.tvlauncher.instantvideo.preload.impl;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;

import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.impl.YoutubePlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.Preloader;
import com.google.android.tvlauncher.instantvideo.preload.PreloaderManager;

import java.util.ArrayList;
import java.util.Iterator;

public class YoutubePreloaderManager extends PreloaderManager {
    private static final boolean DEBUG = false;
    private static final String TAG = "YoutubePreloaderManager";
    private static final int YOUTUBE_PLAYER_CACHE_SIZE = 2;
    private Context mContext;
    private final YoutubePlayerLruCache mYoutubePlayerCache = new YoutubePlayerLruCache(2);

    private class YoutubePlayerLruCache {
        private final ArrayList<Pair<Uri, YoutubePlayerImpl>> mCache;
        private final int mMaxCapacity;
        private YoutubePlayerImpl mReleasedPlayer;

        private YoutubePlayerLruCache(int maxCapacity) {
            this.mCache = new ArrayList();
            this.mMaxCapacity = maxCapacity;
        }

        private YoutubePlayerImpl get(Uri key) {
            int index = 0;
            Iterator it = this.mCache.iterator();
            while (it.hasNext() && !((Uri) ((Pair) it.next()).first).equals(key)) {
                index++;
            }
            YoutubePlayerImpl ret;

            if (index < this.mCache.size()) {
                ret = (this.mCache.get(index)).second;
                this.mCache.remove(index);
                return ret;
            }
            ret = this.mReleasedPlayer;
            if (ret != null) {
                this.mReleasedPlayer = null;
            } else {
                ret = new YoutubePlayerImpl(YoutubePreloaderManager.this.mContext);
            }
            ret.setVideoUri(key);
            return ret;
        }

        private void put(Uri key, YoutubePlayerImpl value) {
            if (this.mMaxCapacity <= 0) {
                this.mReleasedPlayer = value;
                this.mReleasedPlayer.release();
                return;
            }
            if (this.mCache.size() >= this.mMaxCapacity) {
                this.mReleasedPlayer = (YoutubePlayerImpl) ((Pair) this.mCache.get(0)).second;
                this.mReleasedPlayer.release();
                this.mCache.remove(0);
            }
            if (this.mCache.size() < this.mMaxCapacity) {
                this.mCache.add(new Pair(key, value));
            }
        }
    }

    public YoutubePreloaderManager(Context context) {
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
        return this.mYoutubePlayerCache.get(videoUri);
    }

    public void recycleMediaPlayer(MediaPlayer mediaPlayer) {
        YoutubePlayerImpl youtubePlayer = (YoutubePlayerImpl) mediaPlayer;
        if (youtubePlayer.getPlaybackState() != 1) {
            youtubePlayer.stop();
        }
        this.mYoutubePlayerCache.put(mediaPlayer.getVideoUri(), youtubePlayer);
    }

    public int canPlayVideo(Uri videoUri) {
        return YoutubePlayerImpl.isYoutubeUri(videoUri) ? 100 : 0;
    }
}
