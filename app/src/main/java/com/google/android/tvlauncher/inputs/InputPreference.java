package com.google.android.tvlauncher.inputs;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.TypedValue;
import android.view.View;
import com.google.android.tvlauncher.R;

class InputPreference extends Preference {
    private final float mConnectedStateIconAlpha;
    private final float mConnectedStateTextAlpha;
    private final float mDisconnectedStateIconAlpha;
    private final float mDisconnectedStateTextAlpha;
    private final int mInputState;
    private final float mStandbyStateIconAlpha;
    private final float mStandbyStateTextAlpha;

    InputPreference(Context context, int state) {
        super(context);
        this.mInputState = state;
        Resources res = context.getResources();
        this.mDisconnectedStateTextAlpha = getFloat(res, R.fraction.input_banner_disconnected_text_alpha);
        this.mConnectedStateTextAlpha = getFloat(res, R.fraction.input_banner_connected_text_alpha);
        this.mStandbyStateTextAlpha = getFloat(res, R.fraction.input_banner_standby_text_alpha);
        this.mDisconnectedStateIconAlpha = getFloat(res, R.fraction.input_banner_disconnected_image_alpha);
        this.mConnectedStateIconAlpha = getFloat(res, R.fraction.input_banner_connected_image_alpha);
        this.mStandbyStateIconAlpha = getFloat(res, R.fraction.input_banner_standby_image_alpha);
    }

    private float getFloat(Resources res, int resourceId) {
        TypedValue resValue = new TypedValue();
        res.getValue(resourceId, resValue, true);
        return resValue.getFloat();
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View icon = holder.findViewById(16908294);
        View title = holder.findViewById(16908310);
        if (this.mInputState == 2) {
            icon.setAlpha(this.mDisconnectedStateIconAlpha);
            title.setAlpha(this.mDisconnectedStateTextAlpha);
        } else if (this.mInputState == 1) {
            icon.setAlpha(this.mStandbyStateIconAlpha);
            title.setAlpha(this.mStandbyStateTextAlpha);
        } else {
            icon.setAlpha(this.mConnectedStateIconAlpha);
            title.setAlpha(this.mConnectedStateTextAlpha);
        }
    }
}
