package com.amazon.tv.tvrecommendations;

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

    protected abstract void onConnected(IRecommendationsService iRecommendationsService);

    protected abstract void onDisconnected();

    public static String clearReasonToString(int clearReason) {
        switch (clearReason) {
            case 2:
                return "CLEAR_RECOMMENDATIONS_DISABLED (" + clearReason + ")";
            case 3:
                return "CLEAR_RECOMMENDATIONS_PENDING (" + clearReason + ")";
            case 4:
                return "CLEAR_RECOMMENDATIONS_PENDING_DISABLED (" + clearReason + ")";
            default:
                return "UNKNOWN (" + clearReason + ")";
        }
    }

    public RecommendationsClient(Context context) {
        this.mContext = context;
    }

    public void connect() {
        this.mConnectedOrConnecting = true;
        Intent serviceIntent = getServiceIntent();
        if (serviceIntent == null) {
            return;
            //  throw new RuntimeException("Recommendation service is unavailable");
        }
        this.mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                RecommendationsClient.this.mBoundService = IRecommendationsService.Stub.asInterface(service);
                if (RecommendationsClient.this.mConnectedOrConnecting) {
                    RecommendationsClient.this.onConnected(RecommendationsClient.this.mBoundService);
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                RecommendationsClient.this.mBoundService = null;
                if (RecommendationsClient.this.mConnectedOrConnecting) {
                    RecommendationsClient.this.onDisconnected();
                }
            }
        };
        this.mContext.bindService(serviceIntent, this.mConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnect() {
        this.mConnectedOrConnecting = false;
        if (this.mConnection != null) {
            this.mContext.unbindService(this.mConnection);
            this.mConnection = null;
        }
    }

    protected Intent getServiceIntent() {
        ComponentName componentName = getServiceComponent();
        if (componentName == null) {
            return null;
        }
        Intent serviceIntent = new Intent("RecommendationsService");
        serviceIntent.setComponent(componentName);
        return serviceIntent;
    }

    public ComponentName getServiceComponent() {
        List<ResolveInfo> resolveInfoList = this.mContext.getPackageManager().queryIntentServices(new Intent("RecommendationsService"), 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        return new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
    }
}
