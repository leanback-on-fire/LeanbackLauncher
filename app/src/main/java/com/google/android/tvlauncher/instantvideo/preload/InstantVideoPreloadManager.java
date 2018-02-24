package com.google.android.tvlauncher.instantvideo.preload;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.LruCache;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.preload.Preloader.OnPreloadFinishedListener;

public class InstantVideoPreloadManager {
    private static final boolean DEBUG = false;
    private static final int MAX_CONCURRENT_VIDEO_PRELOAD_COUNT = 10;
    private static final String TAG = "InstantVideoPreloadMgr";
    private static InstantVideoPreloadManager sInstance;
    private final Context mAppContext;
    private PreloaderManagerImpl mPreloaderManager;
    private final LruCache<Uri, Preloader> mVideoPreloaderCache = new LruCache<Uri, Preloader>(10) {
        protected void entryRemoved(boolean evicted, Uri key, Preloader oldValue, Preloader newValue) {
            if (newValue != null) {
                InstantVideoPreloadManager.this.onEntryRemovedFromCache(key, oldValue);
            }
        }
    };

    public static synchronized InstantVideoPreloadManager getInstance(Context context) {
        InstantVideoPreloadManager instantVideoPreloadManager;
        synchronized (InstantVideoPreloadManager.class) {
            if (sInstance == null) {
                sInstance = new InstantVideoPreloadManager(context);
            }
            instantVideoPreloadManager = sInstance;
        }
        return instantVideoPreloadManager;
    }

    @VisibleForTesting
    InstantVideoPreloadManager(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mPreloaderManager = new PreloaderManagerImpl();
    }

    public void preload(@NonNull Uri videoUri) {
        if (videoUri == null) {
            throw new IllegalArgumentException("The video URI shouldn't be null.");
        } else if (this.mPreloaderManager.isPreloaded(videoUri)) {
            this.mPreloaderManager.bringPreloadedVideoToTopPriority(videoUri);
        } else {
            Preloader preloader = (Preloader) this.mVideoPreloaderCache.get(videoUri);
            if (preloader == null) {
                preloader = startPreloading(videoUri);
                if (preloader != null) {
                    this.mVideoPreloaderCache.put(videoUri, preloader);
                    return;
                }
                return;
            }
            this.mVideoPreloaderCache.put(videoUri, preloader);
            this.mPreloaderManager.bringPreloadedVideoToTopPriority(videoUri);
        }
    }

    private void onEntryRemovedFromCache(Uri videoUri, Preloader preloader) {
        preloader.stopPreload();
    }

    public void clearCache() {
    }

    public void registerPreloaderManager(PreloaderManager preloaderManager) {
        this.mPreloaderManager.registerPreloaderManager(preloaderManager);
    }

    public void unregisterPreloaderManager(PreloaderManager preloaderManager) {
        this.mPreloaderManager.unregisterPreloaderManager(preloaderManager);
    }

    public MediaPlayer getOrCreatePlayer(Uri videoUri) {
        return this.mPreloaderManager.getOrCreatePlayer(videoUri);
    }

    public void recyclePlayer(MediaPlayer player, Uri videoUri) {
        this.mPreloaderManager.recycleMediaPlayer(player);
    }

    private Preloader startPreloading(final Uri videoUri) {
        Preloader preloader = this.mPreloaderManager.createPreloader(videoUri);
        if (preloader == null) {
            return null;
        }
        preloader.startPreload(new OnPreloadFinishedListener() {
            public void onPreloadFinishedListener() {
                InstantVideoPreloadManager.this.mVideoPreloaderCache.remove(videoUri);
            }
        });
        return preloader;
    }
}
