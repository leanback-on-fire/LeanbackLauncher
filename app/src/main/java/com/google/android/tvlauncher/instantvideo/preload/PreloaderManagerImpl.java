package com.google.android.tvlauncher.instantvideo.preload;

import android.net.Uri;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import java.util.ArrayList;
import java.util.List;

class PreloaderManagerImpl extends PreloaderManager {
    private List<PreloaderManager> mPreloaderManagers = new ArrayList();

    PreloaderManagerImpl() {
    }

    public boolean isPreloaded(Uri videoUri) {
        PreloaderManager preloaderManager = choosePreloaderManager(videoUri);
        return preloaderManager != null && preloaderManager.isPreloaded(videoUri);
    }

    public Preloader createPreloader(Uri videoUri) {
        PreloaderManager preloaderManager = choosePreloaderManager(videoUri);
        if (preloaderManager != null) {
            return preloaderManager.createPreloader(videoUri);
        }
        return null;
    }

    public void clearPreloadedData(Uri videoUri) {
        PreloaderManager preloaderManager = choosePreloaderManager(videoUri);
        if (preloaderManager != null) {
            preloaderManager.clearPreloadedData(videoUri);
        }
    }

    public void bringPreloadedVideoToTopPriority(Uri videoUri) {
        PreloaderManager preloaderManager = choosePreloaderManager(videoUri);
        if (preloaderManager != null) {
            preloaderManager.bringPreloadedVideoToTopPriority(videoUri);
        }
    }

    public MediaPlayer getOrCreatePlayer(Uri videoUri) {
        PreloaderManager preloaderManager = choosePreloaderManager(videoUri);
        if (preloaderManager != null) {
            return preloaderManager.getOrCreatePlayer(videoUri);
        }
        return null;
    }

    public void recycleMediaPlayer(MediaPlayer mediaPlayer) {
        PreloaderManager preloaderManager = choosePreloaderManager(mediaPlayer.getVideoUri());
        if (preloaderManager != null) {
            preloaderManager.recycleMediaPlayer(mediaPlayer);
        }
    }

    public int canPlayVideo(Uri videoUri) {
        int maxScore = 0;
        for (PreloaderManager preloaderManager : this.mPreloaderManagers) {
            int score = preloaderManager.canPlayVideo(videoUri);
            if (maxScore < score) {
                maxScore = score;
            }
        }
        return maxScore;
    }

    void registerPreloaderManager(PreloaderManager preloaderManager) {
        this.mPreloaderManagers.add(preloaderManager);
    }

    void unregisterPreloaderManager(PreloaderManager preloaderManager) {
        if (preloaderManager == null) {
            this.mPreloaderManagers.clear();
        } else {
            this.mPreloaderManagers.remove(preloaderManager);
        }
    }

    private PreloaderManager choosePreloaderManager(Uri videoUri) {
        int maxScore = 0;
        PreloaderManager retPreloaderManager = null;
        for (PreloaderManager preloaderManager : this.mPreloaderManagers) {
            int score = preloaderManager.canPlayVideo(videoUri);
            if (maxScore < score) {
                maxScore = score;
                retPreloaderManager = preloaderManager;
            }
        }
        return retPreloaderManager;
    }
}
