package com.google.android.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.util.Util;
import java.lang.ref.WeakReference;
import java.util.List;

class NowPlayCardListener implements OnActiveSessionsChangedListener {
    private int mBannerMaxHeight;
    private int mBannerMaxWidth;
    private int mCardMaxHeight;
    private int mCardMaxWidth;
    private final Context mContext;
    private final boolean mIsTestRunning;
    private MediaController mLastMediaController;
    private final Callback mMediaSessionCallback;
    private Listener mNowPlayCardListener;
    private float mNowPlayingDefaultDarkening;

    public interface Listener {
        void onClientChanged(boolean z);

        void onClientPlaybackStateUpdate(int i, long j, long j2);

        void onMediaDataUpdated(NowPlayingCardData nowPlayingCardData);
    }

    private static class MediaControllerCallback extends Callback {
        private final WeakReference<NowPlayCardListener> mListener;

        MediaControllerCallback(NowPlayCardListener listener) {
            this.mListener = new WeakReference(listener);
        }

        public void onPlaybackStateChanged(PlaybackState state) {
            NowPlayCardListener listener = (NowPlayCardListener) this.mListener.get();
            if (listener != null) {
                if (Log.isLoggable("NowPlayCardListener", 3)) {
                    Log.d("NowPlayCardListener", "onPlaybackStateChanged: " + state);
                }
                listener.updatePlayback(state);
            }
        }

        public void onMetadataChanged(MediaMetadata metadata) {
            NowPlayCardListener listener = (NowPlayCardListener) this.mListener.get();
            if (listener != null) {
                listener.updateMetadata(metadata);
            }
        }
    }

    NowPlayCardListener(Context context, boolean isTestRunning) {
        this.mMediaSessionCallback = new MediaControllerCallback(this);
        this.mContext = context;
        this.mIsTestRunning = isTestRunning;
        Resources res = this.mContext.getResources();
        this.mCardMaxWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_max_width);
        this.mCardMaxHeight = res.getDimensionPixelOffset(R.dimen.notif_card_img_height);
        this.mBannerMaxWidth = res.getDimensionPixelOffset(R.dimen.banner_width);
        this.mBannerMaxHeight = res.getDimensionPixelOffset(R.dimen.banner_height);
        this.mNowPlayingDefaultDarkening = res.getFraction(R.fraction.now_playing_icon_color_darkening, 1, 1);
    }

    NowPlayCardListener(Context context) {
        this(context, false);
    }

    public void onActiveSessionsChanged(List<MediaController> controllers) {
        updateMediaSessionCallback(controllers.size() == 0 ? null : (MediaController) controllers.get(0));
    }

    private void updateMediaSessionCallback(MediaController activeController) {
        boolean clearing = false;
        if (activeController != this.mLastMediaController) {
            if (this.mLastMediaController != null) {
                this.mLastMediaController.unregisterCallback(this.mMediaSessionCallback);
            }
            if (activeController != null) {
                activeController.registerCallback(this.mMediaSessionCallback);
            }
            this.mLastMediaController = activeController;
        }
        if (this.mLastMediaController == null) {
            clearing = true;
        }
        if (this.mNowPlayCardListener != null) {
            this.mNowPlayCardListener.onClientChanged(clearing);
        }
        if (!clearing) {
            updateMetadata(this.mLastMediaController.getMetadata());
            updatePlayback(this.mLastMediaController.getPlaybackState());
        }
    }

    public synchronized void setRemoteControlListener(Listener listener) throws RemoteException {
        if (Log.isLoggable("NowPlayCardListener", 3)) {
            Log.d("NowPlayCardListener", "setRemoteControlListener: " + listener);
        }
        MediaSessionManager manager = (MediaSessionManager) this.mContext.getApplicationContext().getSystemService("media_session");
        if (listener != null) {
            manager.addOnActiveSessionsChangedListener(this, null);
            this.mNowPlayCardListener = listener;
            checkForMediaSession();
        } else {
            manager.removeOnActiveSessionsChangedListener(this);
            this.mNowPlayCardListener = null;
            updateMediaSessionCallback(null);
        }
    }

    private void updateMetadata(MediaMetadata metadata) {
        if (this.mNowPlayCardListener != null && metadata != null) {
            NowPlayingCardData data = new NowPlayingCardData();
            setPendingIntentAndPackage(data, (MediaSessionManager) this.mContext.getApplicationContext().getSystemService("media_session"));
            data.title = getMetadataString(metadata, "android.media.metadata.TITLE", this.mContext.getString(R.string.unknown_title));
            String fallbackArtist = getApplicationLabel(data.playerPackage);
            if (TextUtils.isEmpty(fallbackArtist)) {
                fallbackArtist = this.mContext.getString(R.string.unknown_artist);
            }
            data.artist = getMetadataString(metadata, "android.media.metadata.ARTIST", fallbackArtist);
            data.albumArtist = getMetadataString(metadata, "android.media.metadata.ALBUM_ARTIST", this.mContext.getString(R.string.unknown_album_artist));
            data.albumTitle = getMetadataString(metadata, "android.media.metadata.ALBUM", this.mContext.getString(R.string.unknown_album));
            data.year = getMetadataLong(metadata, "android.media.metadata.YEAR", Long.valueOf(-1)).longValue();
            data.trackNumber = getMetadataLong(metadata, "android.media.metadata.TRACK_NUMBER", Long.valueOf(-1)).longValue();
            data.duration = getMetadataLong(metadata, "android.media.metadata.DURATION", Long.valueOf(-1)).longValue();
            data.artwork = getResizedRecommendationBitmap(getArt(metadata), false, false);
            data.badgeIcon = getBadgeIcon(metadata);
            data.launchColor = getColor(data.playerPackage);
            if (data.artwork == null) {
                data.artwork = generateArtwork(data.playerPackage);
            }
            this.mNowPlayCardListener.onMediaDataUpdated(data);
        }
    }

    private void updatePlayback(PlaybackState state) {
        if (this.mNowPlayCardListener != null && state != null) {
            this.mNowPlayCardListener.onClientPlaybackStateUpdate(state.getState(), state.getLastPositionUpdateTime(), state.getPosition());
        }
    }

    public Bitmap getArt(MediaMetadata mediaMetadata) {
        if (mediaMetadata == null) {
            return null;
        }
        Bitmap art = mediaMetadata.getBitmap("android.media.metadata.ALBUM_ART");
        if (art == null) {
            return mediaMetadata.getBitmap("android.media.metadata.ART");
        }
        return art;
    }

    public Bitmap getBadgeIcon(MediaMetadata mediaMetadata) {
        if (mediaMetadata != null) {
            return mediaMetadata.getBitmap("android.media.metadata.DISPLAY_ICON");
        }
        return null;
    }

    public String getMetadataString(MediaMetadata mediaMetadata, String key, String defaultVal) {
        if (mediaMetadata != null) {
            String value = mediaMetadata.getString(key);
            if (value != null) {
                return value;
            }
        }
        return defaultVal;
    }

    public Long getMetadataLong(MediaMetadata mediaMetadata, String key, Long defaultVal) {
        if (mediaMetadata != null) {
            Long value = Long.valueOf(mediaMetadata.getLong(key));
            if (value.longValue() != 0) {
                return value;
            }
        }
        return defaultVal;
    }

    private String getApplicationLabel(String packageName) {
        try {
            PackageManager pkgMan = this.mContext.getPackageManager();
            return pkgMan.getApplicationLabel(pkgMan.getApplicationInfo(packageName, 0)).toString();
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    private Bitmap getResizedRecommendationBitmap(Bitmap image, boolean isBanner, boolean lowRes) {
        int maxWidth;
        int maxHeight;
        float f = 1.0f;
        if (isBanner) {
            maxWidth = this.mBannerMaxWidth;
        } else {
            maxWidth = (int) ((!lowRes ? 1.0f : 0.1f) * ((float) this.mCardMaxWidth));
        }
        if (isBanner) {
            maxHeight = this.mBannerMaxHeight;
        } else {
            float f2 = (float) this.mCardMaxHeight;
            if (lowRes) {
                f = 0.1f;
            }
            maxHeight = (int) (f2 * f);
        }
        return Util.getSizeCappedBitmap(image, maxWidth, maxHeight);
    }

    private int getColor(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return this.mContext.getResources().getColor(R.color.notif_background_color);
        }
        PackageManager pkgMan = this.mContext.getPackageManager();
        Intent intent = pkgMan.getLeanbackLaunchIntentForPackage(packageName);
        if (intent == null) {
            return this.mContext.getResources().getColor(R.color.notif_background_color);
        }
        ResolveInfo info = pkgMan.resolveActivity(intent, 0);
        if (info == null) {
            return this.mContext.getResources().getColor(R.color.notif_background_color);
        }
        int defaultColor = this.mContext.getResources().getColor(R.color.notif_background_color);
        try {
            Theme theme = this.mContext.createPackageContext(packageName, 0).getTheme();
            theme.applyStyle(info.activityInfo.getThemeResource(), true);
            TypedArray a = theme.obtainStyledAttributes(new int[]{16843827});
            int color = a.getColor(0, defaultColor);
            a.recycle();
            return color;
        } catch (NameNotFoundException e) {
            Log.e("NowPlayCardListener", "Exception", e);
            return defaultColor;
        }
    }

    private void setPendingIntentAndPackage(NowPlayingCardData data, MediaSessionManager sessionManager) {
        data.clickIntent = null;
        data.playerPackage = null;
        List<MediaController> controllers = sessionManager.getActiveSessions(null);
        MediaController controller = null;
        for (int i = 0; i < controllers.size(); i++) {
            MediaController aController = (MediaController) controllers.get(i);
            if ((aController.getFlags() & 2) != 0) {
                controller = aController;
                break;
            }
        }
        if (controller != null) {
            data.playerPackage = controller.getPackageName();
            data.clickIntent = controller.getSessionActivity();
            if (data.clickIntent == null) {
                data.clickIntent = getPendingIntentFallback(data.playerPackage);
            }
        }
    }

    private PendingIntent getPendingIntentFallback(String packageName) {
        Intent lbIntent = this.mContext.getPackageManager().getLeanbackLaunchIntentForPackage(packageName);
        if (lbIntent == null) {
            return null;
        }
        lbIntent.addCategory("android.intent.category.LAUNCHER");
        lbIntent.addFlags(270532608);
        return PendingIntent.getActivity(this.mContext, 0, lbIntent, 134217728);
    }

    private Bitmap generateArtwork(String playerPackage) {
        int appColor = getColor(playerPackage);
        int color = Color.rgb((int) (((float) Color.red(appColor)) * this.mNowPlayingDefaultDarkening), (int) (((float) Color.green(appColor)) * this.mNowPlayingDefaultDarkening), (int) (((float) Color.blue(appColor)) * this.mNowPlayingDefaultDarkening));
        Drawable playIcon = this.mContext.getResources().getDrawable(R.drawable.ic_now_playing_default);
        int height = playIcon.getIntrinsicHeight();
        int width = playIcon.getIntrinsicWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bmp.eraseColor(color);
        Canvas canvas = new Canvas(bmp);
        playIcon.setBounds(new Rect(0, 0, width, height));
        playIcon.draw(canvas);
        return bmp;
    }

    public void forceUpdate() {
        if (this.mLastMediaController != null) {
            updateMetadata(this.mLastMediaController.getMetadata());
            updatePlayback(this.mLastMediaController.getPlaybackState());
        }
    }

    public void checkForMediaSession() {
        MediaSessionManager manager = (MediaSessionManager) this.mContext.getApplicationContext().getSystemService("media_session");
        if (manager != null) {
            if (!this.mIsTestRunning) {
                new MediaSession(this.mContext.getApplicationContext(), "NowPlayCardListener").release();
            }
            onActiveSessionsChanged(manager.getActiveSessions(null));
        }
    }
}
