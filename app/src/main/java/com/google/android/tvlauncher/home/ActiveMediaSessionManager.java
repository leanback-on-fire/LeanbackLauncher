package com.google.android.tvlauncher.home;

import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import java.util.List;

public class ActiveMediaSessionManager implements OnActiveSessionsChangedListener {
    private static ActiveMediaSessionManager sMediaSessionListener;
    private MediaController mActiveMediaController;
    private Context mContext;

    static ActiveMediaSessionManager getInstance(Context context) {
        if (sMediaSessionListener == null) {
            sMediaSessionListener = new ActiveMediaSessionManager(context);
        }
        return sMediaSessionListener;
    }

    private ActiveMediaSessionManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void onActiveSessionsChanged(List<MediaController> controllers) {
        this.mActiveMediaController = controllers.size() == 0 ? null : (MediaController) controllers.get(0);
    }

    public void start() {
        ((MediaSessionManager) this.mContext.getSystemService("media_session")).addOnActiveSessionsChangedListener(this, null);
    }

    public void stop() {
        ((MediaSessionManager) this.mContext.getSystemService("media_session")).removeOnActiveSessionsChangedListener(this);
    }

    public boolean hasActiveMediaSession() {
        if (this.mActiveMediaController == null) {
            return false;
        }
        PlaybackState state = this.mActiveMediaController.getPlaybackState();
        if (state == null || state.getState() != 3) {
            return false;
        }
        return true;
    }
}
