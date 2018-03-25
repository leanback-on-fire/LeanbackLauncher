package com.rockon999.android.leanbacklauncher.notifications;

public interface BlacklistListener {
    void onPackageBlacklisted(String str);

    void onPackageUnblacklisted(String str);
}
