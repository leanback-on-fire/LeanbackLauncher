package com.amazon.tv.firetv.leanbacklauncher.apps;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.leanback.app.GuidedStepFragment;

//import android.util.Log;

public class AppInfoActivity extends Activity {

    private Drawable icon;
    private String title, pkg, version, desc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            pkg = getIntent().getExtras().getString("pkg");

            try {
                ApplicationInfo info = getPackageManager().getApplicationInfo(pkg, 0);
                title = getPackageManager().getApplicationLabel(info).toString(); // todo flag/tostr
                version = getPackageManager().getPackageInfo(pkg, 0).versionName;
                desc = (version.isEmpty()) ? pkg : pkg + " (" + version + ")";
                icon = getPackageManager().getApplicationBanner(info);
                if (icon == null)
                    icon = getPackageManager().getApplicationLogo(info);
                if (icon == null)
                    icon = getPackageManager().getApplicationIcon(info);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#21272A")));

            AppInfoFragment fragment = AppInfoFragment.newInstance(title, pkg, icon);
            GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content);
        }
    }

    // close on Home press
    @Override
    protected void onUserLeaveHint() {
        //Log.d("onUserLeaveHint", "Home button pressed");
        super.onUserLeaveHint();
        this.finish();
    }

}
