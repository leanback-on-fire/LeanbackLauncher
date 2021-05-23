package com.amazon.tv.leanbacklauncher.clock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.text.format.DateFormat
import android.util.AttributeSet
import android.widget.TextClock
import java.text.SimpleDateFormat

class ClockView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextClock(context, attrs, defStyleAttr) {
    private val mFormatChangeObserver: ContentObserver
    private val mIntentReceiver: BroadcastReceiver
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerObserver()
        val filter = IntentFilter()
        filter.addAction("android.intent.action.TIME_SET")
        filter.addAction("android.intent.action.TIMEZONE_CHANGED")
        filter.addAction("android.intent.action.LOCALE_CHANGED")
        context.registerReceiver(mIntentReceiver, filter, null, null)
        updatePatterns()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregisterObserver()
        context.unregisterReceiver(mIntentReceiver)
    }

    private fun registerObserver() {
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            mFormatChangeObserver
        )
    }

    private fun unregisterObserver() {
        context.contentResolver.unregisterContentObserver(mFormatChangeObserver)
    }

    private fun updatePatterns() {
        val formatString =
            (DateFormat.getTimeFormat(context) as SimpleDateFormat).toPattern().replace("a", "")
                .trim { it <= ' ' }
        format12Hour = formatString
        format24Hour = formatString
    }

    init {
        mIntentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if ("android.intent.action.TIME_SET" == action || "android.intent.action.TIMEZONE_CHANGED" == action || "android.intent.action.LOCALE_CHANGED" == action) {
                    updatePatterns()
                }
            }
        }
        mFormatChangeObserver = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                updatePatterns()
            }

            override fun onChange(selfChange: Boolean, uri: Uri?) {
                updatePatterns()
            }
        }
        updatePatterns()
    }
}