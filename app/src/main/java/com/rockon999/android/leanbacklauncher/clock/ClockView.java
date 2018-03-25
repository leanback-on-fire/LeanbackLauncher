package com.rockon999.android.leanbacklauncher.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextClock;
import java.text.SimpleDateFormat;

public class ClockView extends TextClock {
    private final ContentObserver mFormatChangeObserver;
    private BroadcastReceiver mIntentReceiver;

    public ClockView(Context context) {
        this(context, null, 0);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.LOCALE_CHANGED".equals(action)) {
                    ClockView.this.updatePatterns();
                }
            }
        };
        this.mFormatChangeObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                ClockView.this.updatePatterns();
            }

            public void onChange(boolean selfChange, Uri uri) {
                ClockView.this.updatePatterns();
            }
        };
        updatePatterns();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerObserver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        filter.addAction("android.intent.action.LOCALE_CHANGED");
        getContext().registerReceiver(this.mIntentReceiver, filter, null, null);
        updatePatterns();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterObserver();
        getContext().unregisterReceiver(this.mIntentReceiver);
    }

    private void registerObserver() {
        getContext().getContentResolver().registerContentObserver(System.CONTENT_URI, true, this.mFormatChangeObserver);
    }

    private void unregisterObserver() {
        getContext().getContentResolver().unregisterContentObserver(this.mFormatChangeObserver);
    }

    private void updatePatterns() {
        String formatString = ((SimpleDateFormat) DateFormat.getTimeFormat(getContext())).toPattern().replace("a", "").trim();
        setFormat12Hour(formatString);
        setFormat24Hour(formatString);
    }
}
