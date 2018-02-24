package com.google.android.tvlauncher.instantvideo.preload;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;

public abstract class PreloaderManager {
    public abstract void bringPreloadedVideoToTopPriority(Uri uri);

    public abstract int canPlayVideo(Uri uri);

    public abstract void clearPreloadedData(Uri uri);

    @Nullable
    public abstract Preloader createPreloader(Uri uri);

    public abstract MediaPlayer getOrCreatePlayer(Uri uri);

    public abstract boolean isPreloaded(Uri uri);

    public abstract void recycleMediaPlayer(MediaPlayer mediaPlayer);
}
