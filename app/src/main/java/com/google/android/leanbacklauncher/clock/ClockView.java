package com.google.android.leanbacklauncher.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextClock;
import java.text.SimpleDateFormat;

public class ClockView extends TextClock {
    private BroadcastReceiver mIntentReceiver;

    /* renamed from: com.google.android.leanbacklauncher.clock.ClockView.1 */
    class C01881 extends BroadcastReceiver {
        C01881() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.LOCALE_CHANGED".equals(action)) {
                ClockView.this.updatePatterns();
            }
        }
    }

    public ClockView(Context context) {
        this(context, null, 0);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIntentReceiver = new C01881();
        updatePatterns();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        filter.addAction("android.intent.action.LOCALE_CHANGED");
        getContext().registerReceiver(this.mIntentReceiver, filter, null, null);
        updatePatterns();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(this.mIntentReceiver);
    }

    private void updatePatterns() {
        String formatString = ((SimpleDateFormat) DateFormat.getTimeFormat(getContext())).toPattern().replace("a", "").trim();
        setFormat12Hour(formatString);
        setFormat24Hour(formatString);
    }
}
