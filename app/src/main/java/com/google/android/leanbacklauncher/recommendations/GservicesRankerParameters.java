package com.google.android.leanbacklauncher.recommendations;

import android.content.ContentResolver;
import android.content.Context;
import com.google.android.gsf.Gservices;
import com.google.android.tvrecommendations.service.RankerParameters;
import com.google.android.tvrecommendations.service.RankerParametersFactory;

final class GservicesRankerParameters extends RankerParameters {
    private ContentResolver mContentResolver;

    public static class Factory implements RankerParametersFactory {
        public RankerParameters create(Context context) {
            return new GservicesRankerParameters(context);
        }
    }

    private GservicesRankerParameters(Context context) {
        this.mContentResolver = context.getApplicationContext().getContentResolver();
    }

    public Object getVersionToken() {
        return Gservices.getVersionToken(this.mContentResolver);
    }

    public float getFloat(String key, float defaultValue) {
        return Gservices.getFloat(this.mContentResolver, key, defaultValue);
    }
}
