package com.amazon.tv.leanbacklauncher.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedAction.Builder;
import android.support.v4.content.res.ResourcesCompat;
import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.R;

import java.io.File;
import java.lang.Exception;
import java.util.List;

public class LegacyWallpaperFragment extends GuidedStepSupportFragment {

	/* Action ID definition */
	private static final int ACTION_CONTINUE = 0;
	private static final int ACTION_RESET = 1;

	@NonNull
	public Guidance onCreateGuidance(Bundle savedInstanceState) {
		Activity activity = getActivity();
		String desc = getWallpaper(activity);
		return new Guidance(
			getString(R.string.wallpaper_title), // title
			desc, // description
			getString(R.string.settings_dialog_title), // breadcrumb (parent)
			ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null) // icon
		);
	}

	public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
		actions.add(new Builder(
			getActivity())
			.id(ACTION_CONTINUE)
			.title((int) R.string.select_wallpaper_action_title)
			.description((int) R.string.select_wallpaper_action_desc)
			.build()
		);
		actions.add(new Builder(
			getActivity())
			.id(ACTION_RESET)
			.title((int) R.string.reset_wallpaper_action_title)
			.description((int) R.string.reset_wallpaper_action_desc)
			.build()
		);
	}

	public void onGuidedActionClicked(GuidedAction action) {

		Activity activity = getActivity();

		switch ((int) action.getId()) {
			case ACTION_CONTINUE:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyFileListFragment());
				break;
			case ACTION_RESET:
				resetWallpaper(activity);
				getFragmentManager().popBackStack();
				break;
			default:
				break;
		}
	}

	public boolean resetWallpaper(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		pref.edit().remove("wallpaper_image").apply();
		File file = new File(context.getFilesDir(),"background.jpg");
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
			File file = new File(context.getFilesDir(),"background.jpg");
			if (file.canRead()) {
				return file.toString();
			}
			return null;
		}
	}
}
