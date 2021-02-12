package com.amazon.tv.leanbacklauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MediaButtonBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("LeanbackLauncher", "Received media button event in LeanbackLauncher, ignoring.")
        abortBroadcast()
    }

}