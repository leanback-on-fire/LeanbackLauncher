package com.rockon999.android.leanbacklauncher.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.rockon999.android.leanbacklauncher.MainActivity;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.AppsPreferences;
import com.rockon999.android.leanbacklauncher.apps.AppsPreferences.SortingMode;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment.Action;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment.Action.Listener;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment.Builder;

import java.util.ArrayList;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));

        setContentView(R.layout.settings_fragment);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}


