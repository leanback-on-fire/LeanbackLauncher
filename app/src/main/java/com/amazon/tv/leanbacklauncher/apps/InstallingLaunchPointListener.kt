package com.amazon.tv.leanbacklauncher.apps

interface InstallingLaunchPointListener {
    fun onInstallingLaunchPointAdded(launchPoint: LaunchPoint?)
    fun onInstallingLaunchPointChanged(launchPoint: LaunchPoint?)
    fun onInstallingLaunchPointRemoved(launchPoint: LaunchPoint?, z: Boolean)
}