package com.rockon999.android.leanbacklauncher.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.ConditionVariable;
import android.os.RemoteException;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.recommendations.SwitchingRecommendationsClient;
import com.rockon999.android.tvrecommendations.IRecommendationsService;

public class RecommendationImageLoader {
    private static RecommendationImageLoader sInstance;
    private IRecommendationsService mBoundService;
    private final SwitchingRecommendationsClient mClient;
    private ConditionVariable mServiceBound = new ConditionVariable();

    public static RecommendationImageLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RecommendationImageLoader(context);
        }
        return sInstance;
    }

    private RecommendationImageLoader(Context context) {
        this.mClient = new SwitchingRecommendationsClient(context) {
            protected void onConnected(IRecommendationsService service) {
                RecommendationImageLoader.this.onServiceConnected(service);
            }

            protected void onDisconnected() {
                RecommendationImageLoader.this.onServiceDisconnected();
            }
        };
        Log.i("RecImageLoader", "Connecting to recommendations service");
        this.mClient.connect();
    }

    public Bitmap getImageForRecommendation(String key) {
        if (getService() == null) {
            Log.i("RecImageLoader", "Waiting for service connection");
            this.mServiceBound.block(10000);
            if (getService() == null) {
                Log.e("RecImageLoader", "Service still null after waiting, attempting to reconnect");
                this.mClient.reconnect();
                this.mServiceBound.block(10000);
            }
        }
        IRecommendationsService service = getService();
        if (service != null) {
            try {
                Log.w("RecImageLoader", "Obtain bitmap for key: " + key);
                Bitmap bitmap = service.getImageForRecommendation(key);
                if (bitmap != null) {
                    return bitmap;
                }
                Log.e("RecImageLoader", "Recommendations service returned a null image");
                return bitmap; // todo: default image
            } catch (RemoteException e) {
                Log.e("RecImageLoader", "Cannot obtain recommendation image", e);
            }
        } else {
            Log.e("RecImageLoader", "Cannot obtain recommendation image - service not connected");
            return null;
        }
        return null;
    }

    private void onServiceConnected(IRecommendationsService service) {
        Log.i("RecImageLoader", "Service connected");
        setService(service);
        this.mServiceBound.open();
    }

    private void onServiceDisconnected() {
        Log.i("RecImageLoader", "Service disconnected");
        setService(null);
        this.mServiceBound.close();
    }

    private synchronized void setService(IRecommendationsService service) {
        this.mBoundService = service;
    }

    private synchronized IRecommendationsService getService() {
        return this.mBoundService;
    }
}
