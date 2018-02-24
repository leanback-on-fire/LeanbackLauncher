package com.google.android.tvlauncher.home;

import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;

import java.util.List;

public class ActiveMediaSessionManager
        implements MediaSessionManager.OnActiveSessionsChangedListener {
    private static ActiveMediaSessionManager sMediaSessionListener;
    private MediaController mActiveMediaController;
    private Context mContext;

    private ActiveMediaSessionManager(Context paramContext) {
        this.mContext = paramContext.getApplicationContext();
    }

    static ActiveMediaSessionManager getInstance(Context paramContext) {
        if (sMediaSessionListener == null) {
            sMediaSessionListener = new ActiveMediaSessionManager(paramContext);
        }
        return sMediaSessionListener;
    }

    public boolean hasActiveMediaSession() {
        if (this.mActiveMediaController == null) {
        }

        PlaybackState localPlaybackState = this.mActiveMediaController.getPlaybackState();


        if ((localPlaybackState == null) || (localPlaybackState.getState() != 3)) {
            return false;
        } else {
            return true;
        }
    }

    public void onActiveSessionsChanged(List<MediaController> paramList) {
        if (paramList.size() == 0) {
            this.mActiveMediaController = null;
        } else {
            this.mActiveMediaController = paramList.get(0);
        }
    }

    public void start() {
        ((MediaSessionManager) this.mContext.getSystemService("media_session")).addOnActiveSessionsChangedListener(this, null);
    }

    public void stop() {
        ((MediaSessionManager) this.mContext.getSystemService("media_session")).removeOnActiveSessionsChangedListener(this);
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ActiveMediaSessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */