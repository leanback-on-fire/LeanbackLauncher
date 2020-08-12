package com.amazon.tv.leanbacklauncher.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist.Guidance;
import androidx.leanback.widget.GuidedAction;
import androidx.leanback.widget.GuidedAction.Builder;

import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.R;

import java.io.File;
import java.util.List;

public class LegacyFileListFragment extends GuidedStepSupportFragment {

    private static String TAG = "LegacyFileListFragment";
    /* Action ID definition */
    private static final int ACTION_SELECT = 1;
    private static final int ACTION_BACK = 2;


    @NonNull
    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        Activity activity = getActivity();
        String desc = getWallpaper(activity);
        return new Guidance(
                getString(R.string.select_wallpaper_action_title), // title
                desc, // description
                getString(R.string.settings_dialog_title), // breadcrumb (parent)
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null) // icon
        );
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "READ_EXTERNAL_STORAGE permission is granted");
        } else {
            Log.v(TAG, "READ_EXTERNAL_STORAGE permission not granted");
            makeRequest();
        }
        actions.add(new Builder(
                getActivity())
                .id(ACTION_SELECT)
                .title(String.format(getString(R.string.select), Environment.getExternalStorageDirectory()))
                .description(null)
                .build()
        );
        actions.add(new Builder(
                getActivity())
                .id(ACTION_BACK)
                .title(R.string.goback)
                .description(null)
                .build()
        );
    }

    protected void makeRequest() {
        Activity activity = getActivity();
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                500);
    }

    public void onGuidedActionClicked(GuidedAction action) {
        Activity activity = getActivity();
        switch ((int) action.getId()) {
            case ACTION_SELECT:
                File file = new File(Environment.getExternalStorageDirectory(), "background.jpg");
                if (file.canRead())
                    setWallpaper(activity, Environment.getExternalStorageDirectory() + "/background.jpg");
                else
                    Toast.makeText(activity, activity.getString(R.string.file_no_access), Toast.LENGTH_LONG).show();
                getFragmentManager().popBackStack();
                break;
            case ACTION_BACK:
                getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    public boolean setWallpaper(Context context, String image) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (!image.isEmpty())
            pref.edit().putString("wallpaper_image", image).apply();
        // refresh home
        Activity activity = getActivity();
        Intent Broadcast = new Intent(MainActivity.class.getName()); // ACTION
        Broadcast.putExtra("RefreshHome", true);
        activity.sendBroadcast(Broadcast);
        return true;
    }

    private String getWallpaper(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String image = pref.getString("wallpaper_image", "");
        if (!image.isEmpty()) {
            return image;
        } else {
            File file = new File(context.getFilesDir(), "background.jpg");
            if (file.canRead()) {
                return file.toString();
            }
            return null;
        }
    }
}
