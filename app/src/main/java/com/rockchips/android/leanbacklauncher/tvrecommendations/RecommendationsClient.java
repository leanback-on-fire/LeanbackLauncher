package com.rockchips.android.leanbacklauncher.tvrecommendations;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import java.util.List;

public abstract class RecommendationsClient {
    private IRecommendationsService mBoundService;
    private boolean mConnectedOrConnecting;
    private ServiceConnection mConnection;
    private final Context mContext;

    /* renamed from: com.rockchips.android.tvrecommendations.RecommendationsClient.1 */
    class C02261 implements ServiceConnection {
        C02261() {
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            if (RecommendationsClient.this.mConnectedOrConnecting) {
                RecommendationsClient.this.mBoundService = IRecommendationsService.Stub.asInterface(service);
                RecommendationsClient.this.onConnected(RecommendationsClient.this.mBoundService);
                return;
            }
            RecommendationsClient.this.mContext.unbindService(this);
        }

        public void onServiceDisconnected(ComponentName className) {
            RecommendationsClient.this.mBoundService = null;
            if (RecommendationsClient.this.mConnectedOrConnecting) {
                RecommendationsClient.this.onDisconnected();
            }
        }
    }

    protected abstract void onConnected(IRecommendationsService iRecommendationsService);

    protected abstract void onDisconnected();

    public RecommendationsClient(Context context) {
        this.mConnection = new C02261();
        this.mContext = context;
    }

    public void connect() {
        this.mConnectedOrConnecting = true;
        Intent serviceIntent = getServiceIntent();
        if (serviceIntent == null) {
            throw new RuntimeException("Recommendation service is unavailable");
        }
        this.mContext.bindService(serviceIntent, this.mConnection, 1);
    }

    public void disconnect() {
        this.mConnectedOrConnecting = false;
        if (this.mBoundService != null) {
            this.mContext.unbindService(this.mConnection);
            this.mBoundService = null;
        }
    }

    protected Intent getServiceIntent() {
        Intent serviceIntent = new Intent("RecommendationsService");
        List<ResolveInfo> resolveInfoList = this.mContext.getPackageManager().queryIntentServices(serviceIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = (ResolveInfo) resolveInfoList.get(0);
        serviceIntent.setComponent(new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name));
        return serviceIntent;
    }
}
