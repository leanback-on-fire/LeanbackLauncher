package com.amazon.tv.leanbacklauncher.notifications

interface BlacklistListener {
    fun onPackageBlacklisted(str: String?)
    fun onPackageUnblacklisted(str: String?)
}