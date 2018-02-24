package com.google.android.tvlauncher.clock;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextClock;

import java.text.SimpleDateFormat;

public class ClockView
        extends TextClock {
    private final ContentObserver mFormatChangeObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean paramAnonymousBoolean) {
            ClockView.this.updatePatterns();
        }

        public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri) {
            ClockView.this.updatePatterns();
        }
    };
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
            String action = paramAnonymousIntent.getAction();
            if (("android.intent.action.TIME_SET".equals(action)) || ("android.intent.action.TIMEZONE_CHANGED".equals(action)) || ("android.intent.action.LOCALE_CHANGED".equals(action))) {
                ClockView.this.updatePatterns();
            }
        }
    };

    public ClockView(Context paramContext) {
        this(paramContext, null, 0);
    }

    public ClockView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public ClockView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        updatePatterns();
    }

    private void registerObserver() {
        getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver);
    }

    private void unregisterObserver() {
        getContext().getContentResolver().unregisterContentObserver(this.mFormatChangeObserver);
    }

    private void updatePatterns() {
        String str = ((SimpleDateFormat) DateFormat.getTimeFormat(getContext())).toPattern().replace("a", "").trim();
        setFormat12Hour(str);
        setFormat24Hour(str);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerObserver();
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.TIME_SET");
        localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        localIntentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        getContext().registerReceiver(this.mIntentReceiver, localIntentFilter, null, null);
        updatePatterns();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterObserver();
        getContext().unregisterReceiver(this.mIntentReceiver);
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/clock/ClockView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */