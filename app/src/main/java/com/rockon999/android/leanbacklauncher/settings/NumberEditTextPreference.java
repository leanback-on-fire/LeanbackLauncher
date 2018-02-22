package com.rockon999.android.leanbacklauncher.settings;

import android.content.Context;
import android.support.v7.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by rockon999 on 2/22/18.
 */

public class NumberEditTextPreference extends EditTextPreference {

    public NumberEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NumberEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NumberEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(1));
    }

    @Override
    protected boolean persistString(String value) {
        return persistInt(Integer.valueOf(value));
    }
}
