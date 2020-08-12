package com.amazon.tv.leanbacklauncher.recommendations;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Keep;

import com.amazon.tv.tvrecommendations.RecommendationsClient;

public abstract class SwitchingRecommendationsClient extends RecommendationsClient {
    private Context mContext;
    private Delegate mDelegate;

    public interface Delegate {
        Intent getServiceIntent();

        void release();
    }

    public SwitchingRecommendationsClient(Context context) {
        super(context);
        this.mContext = context;
    }

    protected Intent getServiceIntent() {
        Intent serviceIntent = null;
        if (this.mDelegate != null) {
            serviceIntent = this.mDelegate.getServiceIntent();
        }
        if (serviceIntent == null) {
            return new Intent(this.mContext, RecommendationsService.class);
        } else {
            return serviceIntent;
        }
    }

    @Keep
    public void reconnect() {
        disconnect();
        connect();
    }

    protected void finalize() throws Throwable {
        if (this.mDelegate != null) {
            this.mDelegate.release();
        }
        super.finalize();
    }
}
