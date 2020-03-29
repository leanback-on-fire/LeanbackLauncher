package com.amazon.tv.leanbacklauncher.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedAction.Builder;

import android.support.v4.content.res.ResourcesCompat;

import com.amazon.tv.leanbacklauncher.R;

import java.util.List;

public class LegacyWallpaperFragment extends GuidedStepSupportFragment {

	/* Action ID definition */
	private static final int ACTION_CONTINUE = 0;
	private static final int ACTION_RESET = 1;
	private static final int ACTION_BACK = 2;

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
//		actions.add(new Builder(
//			getActivity())
//			.id(ACTION_BACK)
//			.title("Cancel")
//			.description("Go back")
//			.build()
//		);
	}

	public void onGuidedActionClicked(GuidedAction action) {

	    Activity activity = getActivity();

		switch ((int) action.getId()) {
			case ACTION_CONTINUE:
				break;
			case ACTION_RESET:
				setWallpaper(activity, "");
				break;
			case ACTION_BACK:
				getFragmentManager().popBackStack();
				// getActivity().finish();
				break;
			default:
				return;
		}
	}

    public boolean setWallpaper(Context context, String image) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (image != "")
        	pref.edit().putString("wallpaper_image", image).apply();
        else
        	pref.edit().remove("wallpaper_image").apply();
        return true;
    }

    private String getWallpaper(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String ret = pref.getString("wallpaper_image", "");
        return ret;
    }
}
