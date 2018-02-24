package com.google.android.tvrecommendations.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.clearcut.ClearcutLogger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.tvrecommendations.IRecommendationsClient;
import com.google.android.tvrecommendations.IRecommendationsService.Stub;
import java.util.List;

public abstract class BaseRecommendationsService extends Service {
    private LeanbackLauncherEventLogger mEventLogger;
    private GoogleApiClient mGoogleApiClient;
    private RecommendationsManager mManager;
    private final Class mNotificationsServiceClass;
    private final RankerParametersFactory mRankerParametersFactory;
    private Stub mServiceStub = new Stub() {
        public int getApiVersion() {
            return 1;
        }

        public void registerRecommendationsClient(IRecommendationsClient client, int version) throws RemoteException {
            if (version < 1) {
                throw new RemoteException("Unsupported client version: " + version);
            }
            registerRecommendationsClient(client, false);
        }

        public void registerPartnerRowClient(IRecommendationsClient client, int version) throws RemoteException {
            if (version < 1) {
                throw new RemoteException("Unsupported client version: " + version);
            }
            registerRecommendationsClient(client, true);
        }

        private synchronized void registerRecommendationsClient(IRecommendationsClient client, boolean isPartnerClient) throws RemoteException {
            if (client != null) {
                if (BaseRecommendationsService.this.isSystemUid(Binder.getCallingUid())) {
                    BaseRecommendationsService.this.mManager.registerNotificationsClient(client, isPartnerClient);
                    if (BaseRecommendationsService.this.mManager.isConnectedToNotificationService()) {
                        client.onServiceStatusChanged(true);
                    }
                } else {
                    Log.e(BaseRecommendationsService.this.mTag, "registerNotificationsClient - not a system process");
                }
            }
        }

        public void unregisterRecommendationsClient(IRecommendationsClient client) throws RemoteException {
            unregisterRecommendationsClient(client, false);
        }

        public void unregisterPartnerRowClient(IRecommendationsClient client) throws RemoteException {
            unregisterRecommendationsClient(client, true);
        }

        private synchronized void unregisterRecommendationsClient(IRecommendationsClient client, boolean isPartnerClient) throws RemoteException {
            BaseRecommendationsService.this.mManager.unregisterNotificationsClient(client, isPartnerClient);
        }

        public void dismissRecommendation(String key) throws RemoteException {
            BaseRecommendationsService.this.mManager.cancelRecommendation(key);
        }

        public Bitmap getImageForRecommendation(String key) {
            return BaseRecommendationsService.this.mManager.getRecomendationImage(key);
        }

        public void onActionOpenLaunchPoint(String component, String group) {
            BaseRecommendationsService.this.mManager.onActionOpenLaunchPoint(component, group);
        }

        public void onActionOpenRecommendation(String component, String group) {
            BaseRecommendationsService.this.mManager.onActionOpenRecommendation(component, group);
        }

        public void onActionRecommendationImpression(String component, String group) {
            BaseRecommendationsService.this.mManager.onActionRecommendationImpression(component, group);
        }

        public String[] getRecommendationsPackages() {
            List<String> list = BaseRecommendationsService.this.mManager.getRecommendationsPackages();
            return (String[]) list.toArray(new String[list.size()]);
        }

        public String[] getBlacklistedPackages() {
            List<String> list = BaseRecommendationsService.this.mManager.getBlacklistedPackages();
            return (String[]) list.toArray(new String[list.size()]);
        }

        public void setBlacklistedPackages(String[] blacklist) {
            BaseRecommendationsService.this.mManager.setBlacklistedPackages(blacklist);
        }
    };
    private final String mTag;
    private final boolean mUnbundled;

    public BaseRecommendationsService(boolean unbundled, Class notificationsServiceClass, RankerParametersFactory rankerParametersFactory) {
        this.mUnbundled = unbundled;
        this.mNotificationsServiceClass = notificationsServiceClass;
        this.mRankerParametersFactory = rankerParametersFactory;
        this.mTag = unbundled ? "RecommendationsService" : "RecommendationsServiceB";
    }

    public void onCreate() {
        super.onCreate();
        this.mManager = RecommendationsManager.getInstance(this, this.mUnbundled, this.mRankerParametersFactory);
        Context appContext = getApplicationContext();
        this.mGoogleApiClient = new Builder(appContext).addApi(ClearcutLogger.API).build();
        this.mEventLogger = LeanbackLauncherEventLogger.getInstance(appContext);
        this.mEventLogger.setGoogleApiClient(this.mGoogleApiClient);
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mGoogleApiClient != null) {
            this.mEventLogger.flush();
            this.mGoogleApiClient.disconnect();
            this.mGoogleApiClient = null;
        }
    }

    private boolean isSystemUid(int uid) {
        PackageManager pm = getPackageManager();
        String[] packages = pm.getPackagesForUid(uid);
        if (packages.length == 0) {
            return false;
        }
        int i = 0;
        while (i < packages.length) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packages[0], 128);
                if (appInfo == null || (appInfo.flags & 1) == 0) {
                    return false;
                }
                i++;
            } catch (Exception e) {
                Log.e(this.mTag, "Exception", e);
                return false;
            }
        }
        return true;
    }

    public IBinder onBind(Intent intent) {
        writeNotificationListenerSetting(this);
        return this.mServiceStub;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void writeNotificationListenerSetting(Context context) {
        String listeners = Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        String component = new ComponentName(context, this.mNotificationsServiceClass).flattenToShortString();
        String[] list = listeners == null ? new String[0] : listeners.split("\\s*:\\s*");
        boolean enabled = false;
        for (CharSequence equals : list) {
            if (TextUtils.equals(equals, component)) {
                enabled = true;
                break;
            }
        }
        if (!enabled && context.getPackageManager().checkPermission("android.permission.WRITE_SECURE_SETTINGS", context.getPackageName()) != -1) {
            if (listeners == null || listeners.length() == 0) {
                listeners = component;
            } else {
                listeners = listeners + ":" + component;
            }
            Secure.putString(context.getContentResolver(), "enabled_notification_listeners", listeners);
        }
    }
}
