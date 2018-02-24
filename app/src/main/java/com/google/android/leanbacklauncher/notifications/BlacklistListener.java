package com.google.android.leanbacklauncher.notifications;

public interface BlacklistListener {
    void onPackageBlacklisted(String str);

    void onPackageUnblacklisted(String str);
}
