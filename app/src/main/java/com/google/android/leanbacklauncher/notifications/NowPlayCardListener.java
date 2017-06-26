package com.google.android.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.content.ComponentName;
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
import android.media.AudioManager;
import android.media.RemoteController;
import android.media.RemoteController.MetadataEditor;
import android.media.RemoteController.OnClientUpdateListener;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.util.Util;
import java.util.List;

class NowPlayCardListener implements OnClientUpdateListener {
    private int mBannerMaxHeight;
    private int mBannerMaxWidth;
    private int mCardMaxHeight;
    private int mCardMaxWidth;
    private final Context mContext;
    private Listener mNowPlayCardListener;
    private float mNowPlayingDefaultDarkening;
    private RemoteController mRemoteController;

    public interface Listener {
        void onClientChanged(boolean z);

        void onClientPlaybackStateUpdate(int i, long j, long j2);

        void onMediaDataUpdated(NowPlayingCardData nowPlayingCardData);
    }

    NowPlayCardListener(Context context) {
        this.mContext = context;
        Resources res = this.mContext.getResources();
        this.mCardMaxWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_max_width);
        this.mCardMaxHeight = res.getDimensionPixelOffset(R.dimen.notif_card_img_height);
        this.mBannerMaxWidth = res.getDimensionPixelOffset(R.dimen.banner_width);
        this.mBannerMaxHeight = res.getDimensionPixelOffset(R.dimen.banner_height);
        TypedValue out = new TypedValue();
        res.getValue(R.raw.now_playing_icon_color_darkening, out, true);
        this.mNowPlayingDefaultDarkening = out.getFloat();
    }

    public synchronized void setRemoteControlListener(Listener listener) throws RemoteException {
        if (Log.isLoggable("NowPlayCardListener", 3)) {
            Log.d("NowPlayCardListener", "setRemoteControlListener: " + listener);
        }
        if (listener != null) {
            this.mNowPlayCardListener = listener;
            registerRemoteController();
        } else {
            this.mNowPlayCardListener = null;
            unregisterRemoteController();
        }
    }

    public void onClientChange(boolean clearing) {
        if (Log.isLoggable("NowPlayCardListener", 3)) {
            Log.d("NowPlayCardListener", "onClientChange: " + clearing);
        }
        if (this.mNowPlayCardListener != null) {
            this.mNowPlayCardListener.onClientChanged(clearing);
        }
    }

    public void onClientPlaybackStateUpdate(int state) {
        if (Log.isLoggable("NowPlayCardListener", 3)) {
            Log.d("NowPlayCardListener", "onClientPlaybackStateUpdate = " + state);
        }
        if (this.mNowPlayCardListener != null) {
            this.mNowPlayCardListener.onClientPlaybackStateUpdate(state, 0, -1);
        }
    }

    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        if (Log.isLoggable("NowPlayCardListener", 3)) {
            Log.d("NowPlayCardListener", "onClientPlaybackStateUpdate = " + state + " , currentPosMs = " + currentPosMs);
        }
        if (this.mNowPlayCardListener != null) {
            this.mNowPlayCardListener.onClientPlaybackStateUpdate(state, stateChangeTimeMs, currentPosMs);
        }
    }

    public void onClientTransportControlUpdate(int transportControlFlags) {
    }

    public void onClientMetadataUpdate(MetadataEditor metadataEditor) {
        if (this.mNowPlayCardListener != null) {
            Parcel parcel = Parcel.obtain();
            NowPlayingCardData data = new NowPlayingCardData(parcel);
            data.title = metadataEditor.getString(7, this.mContext.getString(R.string.unknown_title));
            String fallbackArtist = getApplicationLabel(data.playerPackage);
            if (TextUtils.isEmpty(fallbackArtist)) {
                fallbackArtist = this.mContext.getString(R.string.unknown_artist);
            }
            data.artist = metadataEditor.getString(2, fallbackArtist);
            data.albumArtist = metadataEditor.getString(13, this.mContext.getString(R.string.unknown_album_artist));
            data.albumTitle = metadataEditor.getString(1, this.mContext.getString(R.string.unknown_album));
            data.year = metadataEditor.getLong(8, -1);
            data.trackNumber = metadataEditor.getLong(0, -1);
            data.duration = metadataEditor.getLong(9, -1);
            data.artwork = getResizedRecommendationBitmap(metadataEditor.getBitmap(100, null), false, false);
            data.launchColor = getColor(data.playerPackage);
            setPendingIntentAndPackage(data, (MediaSessionManager) this.mContext.getSystemService("media_session"));
            if (data.artwork == null) {
                data.artwork = generateArtwork(data.playerPackage);
            }
            this.mNowPlayCardListener.onMediaDataUpdated(data);
            parcel.recycle();
        }
    }

    private void registerRemoteController() {
        if (this.mRemoteController == null) {
            this.mRemoteController = new RemoteController(this.mContext, this);
            if (this.mRemoteController == null) {
                return;
            }
            if (((AudioManager) this.mContext.getSystemService("audio")).registerRemoteController(this.mRemoteController)) {
                Resources res = this.mContext.getResources();
                this.mRemoteController.setArtworkConfiguration(res.getDimensionPixelOffset(R.dimen.notif_card_img_max_width), res.getDimensionPixelOffset(R.dimen.notif_card_img_height));
                this.mRemoteController.setSynchronizationMode(1);
                return;
            }
            Log.e("NowPlayCardListener", "Failed to register RemoteController with Audio Manager.");
            this.mRemoteController = null;
        }
    }

    private void unregisterRemoteController() {
        if (this.mRemoteController != null) {
            ((AudioManager) this.mContext.getSystemService("audio")).unregisterRemoteController(this.mRemoteController);
            this.mRemoteController = null;
        }
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
        ComponentName componentName = lbIntent.getComponent();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentName);
        intent.addFlags(270532608);
        intent.addFlags(268435456);
        return PendingIntent.getActivity(this.mContext, 0, intent, 268435456);
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
}
