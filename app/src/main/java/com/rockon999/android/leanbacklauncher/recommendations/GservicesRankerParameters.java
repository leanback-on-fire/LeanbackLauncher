package com.rockon999.android.leanbacklauncher.recommendations;

import android.content.ContentResolver;
import android.content.Context;

import com.rockon999.android.tvrecommendations.service.RankerParameters;
import com.rockon999.android.tvrecommendations.service.RankerParametersFactory;

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
        return 0;
    }

    public float getFloat(String key, float defaultValue) {
        return defaultValue;
    }
}
