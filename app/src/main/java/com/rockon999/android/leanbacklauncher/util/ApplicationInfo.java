package com.rockon999.android.leanbacklauncher.util;

import android.text.TextUtils;

/**
 * Created by rockon999 on 2/15/18.
 */

public abstract class ApplicationInfo {

    public abstract String getComponentName();

    public abstract String getPackageName();

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj instanceof ApplicationInfo) {
            ApplicationInfo other = (ApplicationInfo) obj;
            return TextUtils.equals(getComponentName(), other.getComponentName()) && TextUtils.equals(getPackageName(), other.getPackageName());
        }
        return false;
    }
}
