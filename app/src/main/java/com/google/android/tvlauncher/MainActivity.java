package com.google.android.tvlauncher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.tvlauncher.analytics.LoggingActivity;
import com.google.android.tvlauncher.home.HomeFragment;
import com.google.android.tvlauncher.notifications.NotificationsUtils;
import com.google.android.tvlauncher.trace.AppTrace;

import java.util.Set;

public class MainActivity
        extends LoggingActivity {
    private static final long CONTENT_RATINGS_MANAGER_STARTUP_DELAY_MILLIS = 2000L;
    private static final boolean DEBUG = false;
    private static final String TAG = "TvLauncherMainActivity";

    public MainActivity() {
        super("Home");
    }

    public void onBackPressed() {
        MainBackHomeController.getInstance().onBackPressed(this);
    }

    protected void onCreate(Bundle paramBundle) {
        AppTrace.beginSection("onCreate");
        try {
            super.onCreate(paramBundle);
            setContentView(R.layout.activity_main); //2130968605
            if (paramBundle == null) {
                // 2131951783
                getFragmentManager().beginTransaction().add(R.id.home_view_container, new HomeFragment()).commit();
            }
            AppTrace.endSection();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //ContentRatingsManager.getInstance(MainActivity.this).preload();
                }
            }, 2000L);
            return;
        } finally {
            AppTrace.endSection();
        }
    }

    public void onNewIntent(Intent paramIntent) {
        super.onNewIntent(paramIntent);
        Set<String> categories = paramIntent.getCategories();
        if ((categories != null) && (categories.contains("android.intent.category.HOME"))) {
            MainBackHomeController.getInstance().onHomePressed(this);
        }
    }

    protected void onResume() {
        super.onResume();
        NotificationsUtils.showUnshownNotifications(this);
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/MainActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */